package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class LogInPhoneNumberActivity extends AppCompatActivity {
    Button btn_sendOTP;
    EditText et_phoneNumber;
    ProgressBar login_phoneNumber_progressbar;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_phone_number);

        btn_sendOTP = findViewById(R.id.btn_sentOTPId);
        et_phoneNumber = findViewById(R.id.et_PhoneNumberId);
        login_phoneNumber_progressbar = findViewById(R.id.login_phoneNumber_progressbarId);
        countryCodePicker = findViewById(R.id.login_countrycodeId);
        countryCodePicker.registerCarrierNumberEditText(et_phoneNumber);

        login_phoneNumber_progressbar.setVisibility(View.GONE);

        btn_sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_phoneNumber.getText().toString().isEmpty()) {
                    et_phoneNumber.setError("Phone number is required");
                    return;
                }

                if (!countryCodePicker.isValidFullNumber()) {
                    et_phoneNumber.setError("Phone number not valid");
                    return;
                }

                login_phoneNumber_progressbar.setVisibility(View.VISIBLE);

                String fullPhoneNumber = countryCodePicker.getFullNumberWithPlus();
                Intent intent = new Intent(LogInPhoneNumberActivity.this, LogInOTPActivity.class);
                intent.putExtra("phoneNumber", fullPhoneNumber);
                startActivity(intent);

                // Reset progress bar visibility once the intent is started
                login_phoneNumber_progressbar.setVisibility(View.GONE);
            }
        });
    }
}
