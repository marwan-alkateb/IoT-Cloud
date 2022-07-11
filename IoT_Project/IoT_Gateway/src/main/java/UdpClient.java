
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpClient {
    private static final int BUFFER_LENGTH = 1500;
    private static byte[] buffer = new byte[BUFFER_LENGTH];
    private static DatagramPacket packet = new DatagramPacket(buffer, BUFFER_LENGTH);
    private static int SENSOR_A_PORT;
    private static InetAddress SENSOR_A_IP;
    private static int ADAPTOR_PORT;
    private static InetAddress ADAPTOR_IP;
    private static DatagramSocket sensorA_Socket;
    private static DatagramSocket adaptorSocket;

    public UdpClient() throws Exception{
        SENSOR_A_PORT = Integer.parseInt(System.getenv("SENSOR_A_PORT"));
        SENSOR_A_IP = InetAddress.getByName(System.getenv("SENSOR_A_IP"));
        ADAPTOR_PORT = Integer.parseInt(System.getenv("ADAPTOR_PORT"));
        ADAPTOR_IP = InetAddress.getByName(System.getenv("ADAPTOR_IP"));

        sensorA_Socket = new DatagramSocket();
        adaptorSocket = new DatagramSocket();
    }

    private static String sendAndReceive(String message, DatagramSocket socket, InetAddress IP, int port) throws IOException{
        packet = new DatagramPacket(message.getBytes(), message.getBytes().length, IP, port);
        socket.send(packet);
        byte[] buffer = new byte[BUFFER_LENGTH];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String received_sensorA_Values = new String(buffer, StandardCharsets.UTF_8);
        System.out.println("[Temperature Sensor]: " + received_sensorA_Values);
        return received_sensorA_Values;
    }

    public static String get_A_values() throws IOException {
        System.out.println("Gate is communicating with the Temperature Sensor.");
        long time_at_sending_data = System.nanoTime();
        String received_msg = sendAndReceive("Send me Your Data!", sensorA_Socket, SENSOR_A_IP, SENSOR_A_PORT);
        System.out.println("RECEIVED VALUE : " + received_msg);
        long time_at_receiving_data = System.nanoTime();
        System.out.println("RTT of UDP = " + (time_at_receiving_data - time_at_sending_data) + " ns");
        return received_msg;
    }

    public static String get_B_values() throws IOException, InterruptedException {
        System.out.println("Gate is communicating with Adaptor");
        String received_msg = sendAndReceive("Send me Your Data!", adaptorSocket, ADAPTOR_IP, ADAPTOR_PORT);
        System.out.println("received value from Adaptor : " + received_msg);
        return received_msg;
    }
}
