import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws MqttException, InterruptedException {

        String BROKER_IP = System.getenv("BROKER_IP");
        String BROKER_PORT = System.getenv("BROKER_PORT");

        MqttClient mqttClient = new MqttClient("tcp://" + BROKER_IP + ":" + BROKER_PORT, MqttClient.generateClientId());

        //options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);    //Auto Reconnect
        options.setCleanSession(true);
        options.setConnectionTimeout(5);
        // set up connection
        mqttClient.connect(options);
        // Sensor B as Publisher sends every 1 second the current data to broker
        while (true) {
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            message.setPayload(Sensors.getCurrentValues().getBytes());
            mqttClient.publish("SensorB_Data", message);
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
