package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Adapter.ChatRecyclerAdapter;
import com.example.chatapplication.Model.ChatMessageModel;
import com.example.chatapplication.Model.ChatRoomModel;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FireBaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText messageInput;
    ImageButton messageSend;
    ImageButton btn_back;
    TextView other_userName;
    RecyclerView recyclerView;
    ImageView profileImg;

    String chatroomId;
    ChatRoomModel chatRoomModel;

    ChatRecyclerAdapter adapter;
    List<ChatMessageModel> chatMessageList = new ArrayList<>();  // Initialize the chatMessageList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toast.makeText(this, "Hello from chat activity", Toast.LENGTH_SHORT).show();

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        chatroomId = FireBaseUtils.getChatroomId(FireBaseUtils.currentUserId(), otherUser.getUserId());

        btn_back = findViewById(R.id.back_btn);
        other_userName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recyclerView);
        // profileImg = findViewById(R.id.profile_pic_imageView);
        messageInput = findViewById(R.id.chat_message_input);
        messageSend = findViewById(R.id.btn_chat_message_send);
        other_userName.setText(otherUser.getUsername());

        btn_back.setOnClickListener(v -> onBackPressed());

        messageSend.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) return;
            sendMessageToUser(message);
        });

        getOrCreateChatRoomModel();
        setUpChatRecyclerView();
    }

    private void setUpChatRecyclerView() {
        adapter = new ChatRecyclerAdapter(this, chatMessageList);  // Initialize the adapter
        recyclerView.setAdapter(adapter);  // Set the adapter to RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = FireBaseUtils.getChatRoomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            chatMessageList.clear();  // Clear the existing list
            chatMessageList.addAll(queryDocumentSnapshots.toObjects(ChatMessageModel.class));  // Add the fetched messages
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load messages", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendMessageToUser(String message) {
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FireBaseUtils.currentUserId());
        FireBaseUtils.getChatRoomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FireBaseUtils.currentUserId(), Timestamp.now());

        FireBaseUtils.getChatRoomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    messageInput.setText("");
                    chatMessageList.add(0, chatMessageModel);  // Add new message to the top of the list
                    adapter.notifyItemInserted(0);  // Notify adapter of the new item
                    recyclerView.scrollToPosition(0);  // Scroll to the new message
                }
            }
        });
    }

    private void getOrCreateChatRoomModel() {
        FireBaseUtils.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);

                if (chatRoomModel == null) {
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FireBaseUtils.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );

                    FireBaseUtils.getChatRoomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }
}
