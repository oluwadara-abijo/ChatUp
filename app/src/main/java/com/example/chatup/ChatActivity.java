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

public class ChatActivity extends AppCompatActivity {

    private static final String LOG_TAG = ChatActivity.class.getSimpleName();

    //The string used as MQQT topic and user Id
    public static final String MQTT_TOPIC = "chat/up";

    //UI elements
    TextView sentMessageTextView;
    TextView receivedMessageTextView;
    EditText messageInput;
    Button sendButton;

    private MqttAndroidClient mqttClient;

    private String messageToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sentMessageTextView = findViewById(R.id.tv_messageSent);
        receivedMessageTextView = findViewById(R.id.tv_messageReceived);
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
        //Initialize Mqtt client
        initializeClient(messageToSend);
        messageInput.setText("");
    }

    private void initializeClient(final String message) {
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
                    publishMessage(message, mqttClient);
                    subscribeToTopic(mqttClient);
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
     * @param message Message to publish
     * @param mqttClient MQQT client
     */
    private void publishMessage(String message, MqttAndroidClient mqttClient) {
        //Publish message
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        try {
            mqttClient.publish(ChatActivity.MQTT_TOPIC, mqttMessage);
            sentMessageTextView.setText(mqttMessage.toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to a topic
     * @param mqttClient The MQTT client
     *
     */
    private void subscribeToTopic(MqttAndroidClient mqttClient) {
        try {
            IMqttToken subscriptionToken = mqttClient.subscribe(ChatActivity.MQTT_TOPIC, 1);
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
