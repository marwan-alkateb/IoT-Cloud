
public class Main {
    public static void main(String[] args) throws Exception {

        MQTT_Subscriber mqttSubscriber = new MQTT_Subscriber();
        mqttSubscriber.connect();

        UdpServer udpserver = new UdpServer();
        udpserver.start();
    }
}
