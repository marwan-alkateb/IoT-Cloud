import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Callable;

class ClientThread implements Callable<Void> {
    Main server;
    Socket clientSocket;
    BufferedReader inputLine = null;
    PrintWriter out_socket = null;

    public ClientThread(Main server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    private HashMap<String, String> createTestEntry() {
        HashMap <String,String> testEntry = new HashMap<>();
        testEntry.put("Sensor Type", "test");
        testEntry.put("Temperature", "test");
        testEntry.put("Humidity", "test");
        testEntry.put("Fluid", "test");
        return testEntry;
    }

    @Override
    public Void call() throws Exception {

        boolean updateEntry = false;
        HashMap<String, String> entry = new HashMap<>();

        inputLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out_socket = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

        while (true) {

            Database db = Main.dbm.getDatabaseHashMap().get("Sensors Database");
            Table table = db.getTableHashMap().get("Sensors Table");

            // One time test for Consistency
            // a received entry from coordinator should not be created because this test entry has the same ID
            table.createEntry("3", createTestEntry());

            System.out.println("Reading Entry from Coordinator ...");
            String received_msg = inputLine.readLine();
            String[] parts = received_msg.split("_");
            String received_CRUD = parts[0];
            String received_ID = parts[1];
            entry.put("Sensor Type", parts[2]);
            entry.put("Temperature", parts[3]);
            entry.put("Humidity", parts[4]);
            entry.put("Fluid", parts[5]);

            if (table.entryExists(received_ID)) {

                if (received_CRUD.equals("createEntry")) {
                    out_socket.println("ABORT");
                    System.out.println("Entry aborted");
                } else {                                                    // read // update // delete
                    updateEntry = true;
                    out_socket.println("COMMIT");
                    System.out.println("Entry committed");
                }

            } else {

                if (received_CRUD.equals("createEntry")) {
                    out_socket.println("COMMIT");
                    System.out.println("Entry committed");
                } else {
                    out_socket.println("ABORT");
                    System.out.println("Entry aborted");
                }
            }

            String coordinatorCmd = inputLine.readLine();

            if (coordinatorCmd.equalsIgnoreCase("ABORTED")) {
                System.out.println("Aborted....");
                for (int i = 0; i < (server.clientThreadsList).size(); i++) {
                    ((server.clientThreadsList).get(i)).out_socket.println("GLOBAL_ABORT");
                    ((server.clientThreadsList).get(i)).inputLine.close();
                    ((server.clientThreadsList).get(i)).inputLine.close();
                }
                break;
            }

            if (coordinatorCmd.equalsIgnoreCase("COMMITTED")) {
                System.out.println("COMMITTING...");

                if (received_CRUD.equals("createEntry")) {
                    table.createEntry(received_ID, entry);
                    table.show();
                } else if (received_CRUD.equals("readEntry")) {
                    HashMap<String,String> readEntry = table.readEntry(received_ID);
                    System.out.println(readEntry);
                    out_socket.println(readEntry.get(received_ID));
                } else if (updateEntry) {
                    table.updateEntry(received_ID, entry);
                    table.show();
                } else if (received_CRUD.equals("deleteEntry")) {
                    table.deleteEntry(received_ID);
                    table.show();
                }
            }

        }

        return null;
    }
}
