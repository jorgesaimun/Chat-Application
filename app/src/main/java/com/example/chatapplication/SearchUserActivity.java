package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatapplication.Adapter.SearchUserRecyclerAdapter;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.FireBaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    EditText et_searchUser;
    ImageButton btn_searchUser;
    RecyclerView recyclerView;
    ImageButton btn_back;

    SearchUserRecyclerAdapter adapter;
    List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        btn_back = findViewById(R.id.back_btn);
        btn_searchUser = findViewById(R.id.search_user_btn);

        recyclerView = findViewById(R.id.search_user_recycler_view);
        et_searchUser = findViewById(R.id.seach_username_input);

        et_searchUser.requestFocus();

        btn_back.setOnClickListener(v -> onBackPressed());

        btn_searchUser.setOnClickListener(v -> {
            String searchTerm = et_searchUser.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                et_searchUser.setError("Invalid user name");
                return;
            }


            searchUsers(searchTerm);
        });

        // Initialize RecyclerView
        userList = new ArrayList<>();
        adapter = new SearchUserRecyclerAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void searchUsers(String searchTerm) {



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users")
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + "\uf8ff");

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            List<UserModel> users = queryDocumentSnapshots.toObjects(UserModel.class);
            updateUserList(users);

        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserList(List<UserModel> users) {
        userList.clear();
        userList.addAll(users);
        adapter.updateUserList(userList);
    }
}
