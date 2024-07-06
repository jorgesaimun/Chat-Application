package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LogInOTPActivity extends AppCompatActivity {
    Button btn_nxt;
    EditText et_otp;
    String phoneNumber;

    ProgressBar login_otp_progressBar;
    TextView reSendOTP;
    String verificationCode;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    PhoneAuthProvider.ForceResendingToken resendingToken;
    private Long timeoutSeconds = 60L;

    private static final String TAG = "LogInOTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_otpactivity);

        btn_nxt = findViewById(R.id.btn_nextId);
        et_otp = findViewById(R.id.et_OTPId);
        reSendOTP = findViewById(R.id.resentOTPID);
        login_otp_progressBar = findViewById(R.id.login_otp_progressbarId);

        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            Log.d(TAG, "Received phone number: " + phoneNumber);
        } else {
            phoneNumber = null;
            Log.e(TAG, "Phone number not received in intent");
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sendOTP(phoneNumber, false);
        } else {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if phone number is invalid
        }

        btn_nxt.setOnClickListener(v -> {
            String enterOTP = et_otp.getText().toString().trim();
            if (enterOTP.isEmpty() || enterOTP.length() < 6) {
                et_otp.setError("Enter valid OTP");
                et_otp.requestFocus();
                return;
            }
            verifyOTP(enterOTP);
        });

        reSendOTP.setOnClickListener(v -> sendOTP(phoneNumber, true));
    }

    private void sendOTP(String phoneNumber, boolean isResend) {
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
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        Toast.makeText(LogInOTPActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LogInOTPActivity.this, "OTP verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                        Log.e(TAG, "OTP Verification failed", e);
                    }
                });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

        startResendTimer();
    }

    private void verifyOTP(String otp) {
        setInProgress(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LogInOTPActivity.this, LogInUserNameActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LogInOTPActivity.this, "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign in failed", task.getException());
                }
            }
        });
    }

    private void startResendTimer() {
        reSendOTP.setEnabled(false);
        reSendOTP.setText("Resend OTP in " + timeoutSeconds + " seconds");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    timeoutSeconds--;
                    reSendOTP.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    if (timeoutSeconds <= 0) {
                        timeoutSeconds = 60L;
                        timer.cancel();
                        reSendOTP.setEnabled(true);
                        reSendOTP.setText("Resend OTP");
                    }
                });
            }
        }, 0, 1000);
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            login_otp_progressBar.setVisibility(View.VISIBLE);
            btn_nxt.setVisibility(View.GONE);
        } else {
            login_otp_progressBar.setVisibility(View.GONE);
            btn_nxt.setVisibility(View.VISIBLE);
        }
    }
}
