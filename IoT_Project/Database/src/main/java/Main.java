import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    List<ClientThread> clientThreadsList;
    static DatabaseManager dbm = new DatabaseManager();
    static int DATABASE_PORT = Integer.parseInt(System.getenv("DATABASE_PORT"));

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(5000);

        //// Data Base ////
        dbm.createDatabase("Sensors Database");
        Database db = dbm.getDatabaseHashMap().get("Sensors Database");
        db.createTable("Sensors Table");

        Main server = new Main();
        try (ServerSocket serverSocket = new ServerSocket(DATABASE_PORT)) {
            while (true) {
                Callable<Void> task = new ClientThread(server, serverSocket.accept());
                pool.submit(task);
            }
        } catch (IOException e) {
            System.err.println("Couldn't start server " + e);
        }
    }
}

