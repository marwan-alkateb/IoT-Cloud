import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private static int CLOUD_PORT;
    private static InetAddress CLOUD_SERVER_IP;
    public TcpClient() throws Exception {
        CLOUD_PORT = Integer.parseInt(System.getenv("CLOUD_PORT"));
        CLOUD_SERVER_IP = InetAddress.getByName(System.getenv("CLOUD_SERVER_IP"));
    }

    public void communicateWithCloud (String msg) throws IOException {
        Socket socket = new Socket(CLOUD_SERVER_IP,CLOUD_PORT);

        // I/O streams
        BufferedReader in_socket = new BufferedReader (new InputStreamReader (socket.getInputStream()));
        PrintWriter out_socket = new PrintWriter (new OutputStreamWriter (socket.getOutputStream()), true);
        long time_at_sending_data = System.nanoTime();
        out_socket.println(msg);
        String confirmation_msg = in_socket.readLine();
        long time_at_receiving_data = System.nanoTime();
        System.out.println("RTT of TCP = " + (time_at_receiving_data - time_at_sending_data) + " ns");
        System.out.println("[Received from Cloud] : " + confirmation_msg);
    }

}
