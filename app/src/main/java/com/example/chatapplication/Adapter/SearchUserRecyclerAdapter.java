package com.example.chatapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FireBaseUtils;

import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {
    private List<UserModel> userList;
    private Context context;

    public SearchUserRecyclerAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel userModel = userList.get(position);
        holder.userName.setText(userModel.getUsername());
        holder.phoneNumber.setText(userModel.getPhone());

        // Show "Me" if the user is the current user
        if (userModel.getUserId().equals(FireBaseUtils.currentUserId())) {
            holder.userName.setText(userModel.getUsername() + " (Me)");
        }

        // Set click listener on itemView
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, userModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateUserList(List<UserModel> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView userName, phoneNumber;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_text);
            phoneNumber = itemView.findViewById(R.id.user_phoneNumber_text);
        }
    }
}
