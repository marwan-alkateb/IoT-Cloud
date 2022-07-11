
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {
    private static final int BUFFER_LENGTH = 1500;

    public UdpServer() throws Exception {

        int SENSOR_A_PORT = Integer.parseInt(System.getenv("SENSOR_A_PORT"));
        DatagramSocket socket = new DatagramSocket(SENSOR_A_PORT);
        System.out.println("Sensor Receiver is running...");

        while (true) {
            // Connection Setup
            byte[] buffer = new byte[BUFFER_LENGTH];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            // Receiving Requests ...
            socket.receive(packet);
            String msg = new String(buffer).trim();
            System.out.println("Received: " + msg + " – at: " + java.time.LocalTime.now());
            String currentValues = Sensors.getCurrentValues();
            System.out.println("Sent to IoT-Gateway: " + currentValues);
            // turn current sensor date to bytes and send them to IoT-Gateway
            buffer = currentValues.getBytes();
            packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
            socket.send(packet);
        }
    }
}
