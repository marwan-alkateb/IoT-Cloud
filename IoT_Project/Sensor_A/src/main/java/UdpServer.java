
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UdpServer {
    private static final int BUFFER_LENGTH = 1500;
    public UdpServer() throws Exception{

        int SENSOR_A_PORT = Integer.parseInt(System.getenv("SENSOR_A_PORT"));
        DatagramSocket socket = new DatagramSocket(SENSOR_A_PORT);
        System.out.println("Sensor Receiver is running...");

        while(true) {

            // receive Requests
            byte[] buffer = new byte[BUFFER_LENGTH];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Receiving Requests ...");
            socket.receive(packet);
            String message = new String(buffer).trim();
            System.out.println("Received: " + message + " – at: " + java.time.LocalTime.now());

            String currentValues = Sensors.getCurrentValues();
            System.out.println("Sent Values: " + currentValues);

            // send Sensor Data
            buffer = currentValues.getBytes();
            packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort() );
            socket.send(packet);

        }
    }
}
