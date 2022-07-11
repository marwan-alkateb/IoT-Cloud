
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpServer {
    private static final String newHtmlLine = "<br>";
    private static String htmlResponseCodeOk = "HTTP/1.1 200 Ok" + "\r\n";
    private static String htmlContentType = "Content-Type: text/html" + "\r\n";
    private static final String HTML_START =
            "<!DOCTYPE html>\n<html>\n<body>\n" +
                    "<header>\n<h2>Distributed Systems - Sensor Data</h2>\n</header>\n" +
                    "<h3>Sensor Values :</h3>\n";
    private static final String HTML_END = "</body>\n</html>";
    public final static int GATE_CLOUD_PORT = Integer.parseInt(System.getenv("CLOUD_PORT"));

    private static int createdEntries = 0;

    public TcpServer() {

    }

    static class MultiThreaded_TCP implements Callable<Void> {
        private Socket tcp_socket;
        private static ArrayList<String> getRequestArray = new ArrayList<>();
        private static ArrayList<String> AllSensorData = new ArrayList<>();
        private static PrintWriter out_socket = null;
        ThriftService.Client thrift_client = null;


        MultiThreaded_TCP(Socket tcpSocket, ThriftService.Client thrift_client) throws TTransportException {

            this.tcp_socket = tcpSocket;
            this.thrift_client = thrift_client;
        }

        @Override
        public Void call() {
            try {
                //// TCP SERVER ////
                BufferedReader in_socket = new BufferedReader(new InputStreamReader(tcp_socket.getInputStream()));
                out_socket = new PrintWriter(new OutputStreamWriter(tcp_socket.getOutputStream()), true);

                String received_msg = in_socket.readLine();

                if (received_msg.contains("GET")) {
                    // GET REQUEST
                    System.out.println("GET Request Received: " + received_msg);
                    getRequestArray.add(received_msg);
                    System.out.println(getRequestArray);
                    printData(thrift_client);
                    getRequestArray.clear();

                } else if (received_msg.contains("POST")) {
                    received_msg = received_msg.replaceAll("POST","");
                    //POST REQUEST
                    System.out.println("POST Request from IoT-Gateway : " + received_msg);
                    out_socket.println("Confirmation - Cloud got the message : " + received_msg);
                    AllSensorData.add(received_msg);

                    //// Thrift Client ////
                    System.out.println("[Thrift Client] sending value " + received_msg);
                    String[] parts = received_msg.split("_");
                    SensorData sd = new SensorData();
                    sd.setSensorType(parts[0]);
                    sd.setTemperatureValue(parts[1]);
                    sd.setHumidityValue(parts[2]);
                    sd.setFluidValue(parts[3]);

                    // RTT
                    long time_at_sending_data = System.nanoTime();
                    boolean confirmation = thrift_client.createEntry(sd); // received_msg
                    long time_at_receiving_data = System.nanoTime();
                    System.out.println("RTT of RPC = " + (time_at_receiving_data - time_at_sending_data) + " ns");

                    createdEntries++;

                    System.out.println("[Confirmation from Cloud] : " + confirmation);
                    Thread.sleep(1000);
                }

                else {
                    throw new IllegalArgumentException("Unknown TCP Request!");
                }

            } catch (IOException | TException ex) {
                ex.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    tcp_socket.close();
                } catch (IOException e) {
                    // ignore;
                }
            }
            return null;
        }

        private static void printData(ThriftService.Client thrift_client) throws IOException, TException {

            if (!getRequestArray.isEmpty()) {
                out_socket.print(htmlResponseCodeOk);
                out_socket.print(htmlContentType);
                out_socket.print("\r\n");

                out_socket.print(HTML_START);

                out_socket.print("Sensor Data" + "\r\n");

                for (String t : AllSensorData) {
                    out_socket.println(newHtmlLine + t);
                }
                out_socket.println(HTML_END);
            }
        }
    }
}
