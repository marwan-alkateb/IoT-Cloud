import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class SimpleMqttCallBack implements MqttCallback {

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
