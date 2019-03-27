package com.example.chatup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    TextView messageTextView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = findViewById(R.id.tv_message);

        final String topic = "chat_up";
        final String payload = "chat_payload";

        //Initialize Mqtt client
        initializeClient(topic, payload);

    }

    private void initializeClient(final String topic, final String payload) {
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient mqttClient = new MqttAndroidClient(getApplicationContext(),
                "tcp://broker.hivemq.com:1883", clientId);

        //Connect to client
        try {
            IMqttToken token = mqttClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOG_TAG, "onSuccess");
                    publishMessage(topic, payload, mqttClient);
                    subscribeToTopic(mqttClient, topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(LOG_TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publish a message
     * @param topic The MQTT topic
     * @param payload Payload used to publish
     * @param mqttClient MQQT client
     */
    private void publishMessage(String topic, String payload, MqttAndroidClient mqttClient) {
        //Publish message
        MqttMessage message = new MqttMessage(payload.getBytes());
        try {
            mqttClient.publish(topic, message);
            messageTextView.setText(message.toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to a topic
     * @param mqttClient The MQTT client
     * @param topic The MQTT topic
     */
    private void subscribeToTopic(MqttAndroidClient mqttClient, String topic) {
        try {
            IMqttToken subscriptionToken = mqttClient.subscribe(topic, 1);
            subscriptionToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOG_TAG, "Message was published");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(LOG_TAG, "Subscription failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
