import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ThriftServer {

    public final static int COORDINATOR_PORT = Integer.parseInt(System.getenv("COORDINATOR_PORT"));

    //DB1
    public static final String DATABASE_IP = System.getenv("DATABASE_IP");
    public static final int DATABASE_PORT = Integer.parseInt(System.getenv("DATABASE_PORT"));
    static Socket clientSocket = null;
    static PrintStream os = null;
    static DataInputStream is = null;
    static BufferedReader inputLine = null;

    // DB2
    public static String DATABASE2_IP = System.getenv("DATABASE2_IP");
    public static int DATABASE2_PORT = Integer.parseInt(System.getenv("DATABASE2_PORT"));
    static Socket clientSocket2 = null;
    static PrintStream os2 = null;
    static DataInputStream is2 = null;
    static BufferedReader inputLine2 = null;


    public static class MessageHandler implements ThriftService.Iface {

        private static int entry_id = 0;

        private void establishConnectionWithDB1() throws IOException {
            clientSocket = new Socket(DATABASE_IP, DATABASE_PORT);
            inputLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        }

        private void establishConnectionWithDB2() throws IOException {
            clientSocket2 = new Socket(DATABASE2_IP, DATABASE2_PORT);
            inputLine2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
            os2 = new PrintStream(clientSocket2.getOutputStream());
            is2 = new DataInputStream(clientSocket2.getInputStream());
        }

        private void closeStreamsAndSockets () throws IOException {
            os.close();
            os2.close();
            is.close();
            is2.close();
            clientSocket.close();
            clientSocket2.close();
        }

        @Override
        public boolean createEntry(SensorData sensorData) {

            boolean queryCreated = false;
            long time_at_receiving_data = System.nanoTime();
            System.out.println("[Server] received: " + sensorData);

            String queryString = "createEntry" + "_" +
                    entry_id + "_" +
                    sensorData.getSensorType() + "_" +
                    sensorData.getTemperatureValue() + "_" +
                    sensorData.getHumidityValue() + "_" +
                    sensorData.getFluidValue();

            entry_id++;

            try {
                establishConnectionWithDB1();
                establishConnectionWithDB2();
            } catch (Exception e) {
                System.out.println("Exception occurred : " + e.getMessage());
            }

            try {
                os.println(queryString);
                os2.println(queryString);

                String vote = inputLine.readLine();
                String vote2 = inputLine2.readLine();

                if (vote.equals("ABORT") || vote2.equals("ABORT")) {
                    System.out.println("Redundant PK, Aborted.");
                    os.println("ABORTED");
                    os2.println("ABORTED");
                    closeStreamsAndSockets();

                } else if (vote.equals("COMMIT") && vote2.equals("COMMIT")) {
                    System.out.println("No Redundant PK, Committed.");
                    os.println("COMMITTED");
                    os2.println("COMMITTED");
                    queryCreated = true;
                }
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }

            long time_at_sending_data = System.nanoTime();
            System.out.println("RTT of 2CP = " + (time_at_sending_data - time_at_receiving_data) + " ns");

            return queryCreated;
        }

        @Override
        public Map<String,String> readEntry(String received_ID) {
            String queryString = "readEntry" + "_" + received_ID;
            try {
                establishConnectionWithDB1();
                establishConnectionWithDB2();
            } catch (Exception e) {
                System.out.println("Exception occurred : " + e.getMessage());
            }
            try {
                // send query to DB1 & DB2
                os.println(queryString);
                os2.println(queryString);
                // receive query from DB1 & DB2
                String entry = inputLine.readLine();
                String entry2 = inputLine2.readLine();
                // print received data
                System.out.println("Read Entry from DB1: " + entry);
                System.out.println("Read Entry from DB2: " + entry2);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
            return null;
        }

        @Override
        public String updateEntry(String received_ID, Map<String, String> newEntry) {

            String queryString = "updateEntry" + "_" +
                    received_ID + "_" +
                    newEntry.get("Sensor Type") + "_" +
                    newEntry.get("Temperature") + "_" +
                    newEntry.get("Humidity") + "_" +
                    newEntry.get("Fluid");

            try {
                establishConnectionWithDB1();
                establishConnectionWithDB2();
            } catch (Exception e) {
                System.out.println("Exception occurred : " + e.getMessage());
            }

            try {
                os.println(queryString);
                os2.println(queryString);

                String vote = inputLine.readLine();
                String vote2 = inputLine2.readLine();

                if (vote.equals("ABORT") || vote2.equals("ABORT")) {
                    System.out.println("Can not make update. Aborted.");
                    os.println("ABORTED");
                    os2.println("ABORTED");
                    closeStreamsAndSockets();
                } else if (vote.equals("COMMIT") && vote2.equals("COMMIT")) {
                    System.out.println("Committed updated Entry.");
                    os.println("COMMITTED");
                    os2.println("COMMITTED");
                }
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
            return null;
        }

        @Override
        public String deleteEntry(String received_ID) {

            String queryString = "deleteEntry" + "_" + received_ID;

            try {
                establishConnectionWithDB1();
                establishConnectionWithDB2();
            } catch (Exception e) {
                System.out.println("Exception occurred : " + e.getMessage());
            }

            try {
                os.println(queryString);
                os2.println(queryString);

                String vote = inputLine.readLine();
                String vote2 = inputLine2.readLine();

                if (vote.equals("ABORT") || vote2.equals("ABORT")) {
                    System.out.println("Can not delete Entry. Aborted.");
                    os.println("ABORTED");
                    os2.println("ABORTED");
                    closeStreamsAndSockets();
                } else if (vote.equals("COMMIT") && vote2.equals("COMMIT")) {
                    System.out.println("Committed to delete Entry.");
                    os.println("COMMITTED");
                    os2.println("COMMITTED");
                }
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
            return null;
        }
    }

    ThriftServer() {
        try {
            TServerSocket thriftSocket = new TServerSocket(COORDINATOR_PORT);
            TProcessor proc = new ThriftService.Processor<>(new MessageHandler());
            TServer server = new TSimpleServer(new TServer.Args(thriftSocket).processor(proc));

            System.out.println("[Server] waiting for connections");
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
