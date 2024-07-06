package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.FireBaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LogInUserNameActivity extends AppCompatActivity {
    Button btn_createAccount;
    EditText et_userName;
    String phoneNumber;
    UserModel userModel;
    ProgressBar login_userName_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_user_name);

        btn_createAccount = findViewById(R.id.btn_createAccount);
        et_userName = findViewById(R.id.et_userNameId);
        login_userName_progressBar = findViewById(R.id.login_userName_progressbarId);

        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        getUserName();

        btn_createAccount.setOnClickListener(v -> {
            setUserName();
        });
    }

    private void setUserName() {
        setInProgress(true);
        String userName = et_userName.getText().toString();
        if (userName.isEmpty() || userName.length() < 3) {
            et_userName.setError("User name length should be at least 3 chars");
            return;
        }

        if (userModel != null) {
            userModel.setUsername(userName);
        } else {
            userModel = new UserModel(phoneNumber, userName, Timestamp.now(),FireBaseUtils.currentUserId());
        }

        FireBaseUtils.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);

                if (task.isSuccessful()) {
                    Intent intent = new Intent(LogInUserNameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });
    }

    private void getUserName() {
        setInProgress(true);
        FireBaseUtils.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        et_userName.setText(userModel.getUsername());
                        btn_createAccount.setText("Enter");
                    }
                } else {

                }
            }
        });
    }


    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            login_userName_progressBar.setVisibility(View.VISIBLE);
            btn_createAccount.setVisibility(View.GONE);
        } else {
            login_userName_progressBar.setVisibility(View.GONE);
            btn_createAccount.setVisibility(View.VISIBLE);
        }
    }
}