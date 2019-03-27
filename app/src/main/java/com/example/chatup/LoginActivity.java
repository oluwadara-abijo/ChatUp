package com.example.chatup;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.chatup.ChatActivity.MQTT_TOPIC;

public class LoginActivity extends AppCompatActivity {

    EditText userIdEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdEditText = findViewById(R.id.editText_userId);
        loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdEditText.getText().toString();
                if (userId.equals(MQTT_TOPIC)) {
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(loginButton, "Incorrect user ID", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }
}
