import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.UnknownHostException;

public class MQTT_Subscriber {
    public static MqttClient client;
    public static String msg;
    public MQTT_Subscriber() throws MqttException {
        System.out.println("== START SUBSCRIBER ==");
        client = new MqttClient("tcp://172.20.0.9:1884", MqttClient.generateClientId());
    }
    public void connect() throws MqttException{
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);

        client.setCallback( new SimpleMqttCallBack() );
        client.connect(options);
        client.subscribe("SensorB_Data");
    }
}
