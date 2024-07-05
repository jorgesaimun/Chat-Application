package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogInPhoneNumberActivity extends AppCompatActivity {
    Button btn_sendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_phone_number);

        btn_sendOTP = findViewById(R.id.btn_sentOTPId);


        btn_sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInPhoneNumberActivity.this, LogInOTPActivity.class);
                startActivity(intent);
            }
        });

    }
}