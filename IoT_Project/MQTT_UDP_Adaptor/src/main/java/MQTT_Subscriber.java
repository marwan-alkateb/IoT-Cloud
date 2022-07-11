import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTT_Subscriber {
    public static MqttClient client;
    public static String msg;

    public MQTT_Subscriber() throws MqttException {
        System.out.println("== START SUBSCRIBER ==");
        client = new MqttClient("tcp://172.20.0.9:1884", MqttClient.generateClientId());
    }

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);

        client.setCallback(new Mqtt_Adaptor_CallBack());
        client.connect(options);
        client.subscribe("SensorB_Data");
    }


    public class Mqtt_Adaptor_CallBack implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            MQTT_Subscriber.msg = new String(mqttMessage.getPayload());
            System.out.println("Received from Broker: " + MQTT_Subscriber.msg + "  [topic : " + topic + "]");
            String msg = new String(mqttMessage.getPayload());
            saveComponentInfo(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            try {
                System.out.println("Auslieferung komplett: " + iMqttDeliveryToken.getMessage());
            } catch (MqttException e) {
                System.err.println("Fehler beim senden vom ausliefern des tokens: " + e.getMessage());
            }
        }

        private void saveComponentInfo(String msg) {
            UdpServer.data.add(msg);
        }
    }

}
