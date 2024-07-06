package com.example.chatapplication.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FireBaseUtils {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static boolean isLoggedIn() {
        if (currentUserId() != null) return true;
        return false;
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static StorageReference getOtherProfilePicStorageRef(String userId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(userId);
    }

    public static DocumentReference getChatRoomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
        // return userId2 + "_" + userId1;
    }

    public static CollectionReference getChatRoomMessageReference(String chatroomId) {
        return getChatRoomReference(chatroomId).collection("chats");
    }
}
