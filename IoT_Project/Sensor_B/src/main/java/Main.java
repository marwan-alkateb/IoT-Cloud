import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws MqttException, InterruptedException, UnknownHostException {

        //int SENSOR_ADAPTOR_PORT = Integer.parseInt(System.getenv("SENSOR_ADAPTOR_PORT"));
        //InetAddress ADAPTOR_IP = InetAddress.getByName(System.getenv("SENSOR_B_IP"));

        System.out.println("== START PUBLISHER ==");

        MqttClient client = new MqttClient("tcp://172.20.0.9:1884", MqttClient.generateClientId());

        //options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true); //Auto Reconnect
        options.setCleanSession(true);
        options.setConnectionTimeout(5);

        //connect
        client.connect(options);

        while(true){
            MqttMessage message = new MqttMessage();

            message.setQos(1);
            //message.setRetained(true);

            message.setPayload(Sensors.getCurrentValues().getBytes());
            client.publish("SensorB_Data", message);
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
