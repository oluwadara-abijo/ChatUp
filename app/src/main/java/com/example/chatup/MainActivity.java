package com.example.chatup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    TextView sentMessageTextView;
    TextView receivedMessageTextView;
    EditText userIdInput;
    EditText messageInput;
    Button sendButton;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MqttAndroidClient mqttClient;
    private String messageToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sentMessageTextView = findViewById(R.id.tv_messageSent);
        receivedMessageTextView = findViewById(R.id.tv_messageReceived);
        userIdInput = findViewById(R.id.editText_userId);
        messageInput = findViewById(R.id.editText_messageInput);
        sendButton = findViewById(R.id.button_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage () {
        messageToSend = messageInput.getText().toString();
        String topic = userIdInput.getText().toString();
        //Initialize Mqtt client
        initializeClient(topic, messageToSend);
        messageInput.setText("");
    }

    private void initializeClient(final String topic, final String message) {
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttAndroidClient(getApplicationContext(),
                "tcp://broker.hivemq.com:1883", clientId);

        //Connect to client
        try {
            IMqttToken token = mqttClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOG_TAG, "onSuccess");
                    publishMessage(topic, message, mqttClient);
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
     * @param message Message to publish
     * @param mqttClient MQQT client
     */
    private void publishMessage(String topic, String message, MqttAndroidClient mqttClient) {
        //Publish message
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        try {
            mqttClient.publish(topic, mqttMessage);
            sentMessageTextView.setText(mqttMessage.toString());
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
                    Log.d(LOG_TAG, "Subscription successful");
                    receivedMessageTextView.setText(messageToSend);
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
