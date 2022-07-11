import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        UdpClient udp_client = new UdpClient();
        TcpClient tcp_client = new TcpClient();

        while(true){
            try {

                // get Data from Sensor A
                String rcv_A_data = udp_client.get_A_values();
                TimeUnit.SECONDS.sleep(1);

                // Send received data from Sensor A to the http server
                tcp_client.communicateWithCloud("POST" + rcv_A_data);
                TimeUnit.SECONDS.sleep(1);

                // get Sensor B Data from Adaptor
                String rcv_B_data = udp_client.get_B_values();
                TimeUnit.SECONDS.sleep(1);

                // Send received Sensor B Data to the http server
                tcp_client.communicateWithCloud("POST" + rcv_B_data);
                TimeUnit.SECONDS.sleep(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
