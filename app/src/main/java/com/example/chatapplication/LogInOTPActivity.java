package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LogInOTPActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button btn_next;
    ProgressBar login_OTP_ProgressBar;
    EditText et_otp;
    String phoneNumber;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    TextView tv_resentOTP;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_otpactivity);
        et_otp = findViewById(R.id.et_OTPId);
        btn_next = findViewById(R.id.btn_nextId);
        login_OTP_ProgressBar = findViewById(R.id.login_otp_progressbarId);
        tv_resentOTP = findViewById(R.id.resentOTPID);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getExtras().getString("phoneNumber");
        } else {
            phoneNumber = null;
        }

        sendOTP(phoneNumber, false);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = et_otp.getText().toString().trim();
                if (otp.isEmpty() || otp.length() < 6) {
                    et_otp.setError("Enter valid OTP");
                    et_otp.requestFocus();
                    return;
                }
                verifyOTP(otp);
            }
        });

        tv_resentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP(phoneNumber, true);
            }
        });
    }

    void sendOTP(String phoneNumber, boolean isResend) {
        setInProgress(true);

        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LogInOTPActivity.this, "OTP verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        Toast.makeText(LogInOTPActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }
                });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void verifyOTP(String otp) {
        setInProgress(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LogInOTPActivity.this, LogInUserNameActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LogInOTPActivity.this, "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            login_OTP_ProgressBar.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        } else {
            login_OTP_ProgressBar.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }
}
