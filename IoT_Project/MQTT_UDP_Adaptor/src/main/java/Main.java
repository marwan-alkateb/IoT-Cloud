import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        MQTT_Subscriber mqttSubscriber = new MQTT_Subscriber();
        mqttSubscriber.connect();

        UdpServer udpserver = new UdpServer();
        udpserver.start();
    }
}
