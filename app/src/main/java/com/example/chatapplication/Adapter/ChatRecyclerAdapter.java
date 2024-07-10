package com.example.chatapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Model.ChatMessageModel;
import com.example.chatapplication.R;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FireBaseUtils;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder> {
    private List<ChatMessageModel> chatMessageList;
    private Context context;
    ChatMessageModel message;

    public ChatRecyclerAdapter(Context context, List<ChatMessageModel> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {
       message= chatMessageList.get(position);

        if (message.getSenderId().equals(FireBaseUtils.currentUserId())) {
            holder.left_chat_layout.setVisibility(View.GONE);
            holder.right_chat_layout.setVisibility(View.VISIBLE);
            holder.right_chat_textView.setText(message.getMessage());
        }else{
            holder.left_chat_layout.setVisibility(View.VISIBLE);
            holder.right_chat_layout.setVisibility(View.GONE);
            holder.left_chat_textView.setText(message.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void updateChatMessageList(List<ChatMessageModel> chatMessageList) {
        this.chatMessageList = chatMessageList;
        notifyDataSetChanged();
    }

    public static class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout left_chat_layout, right_chat_layout;
        TextView left_chat_textView, right_chat_textView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            left_chat_layout = itemView.findViewById(R.id.left_chat_layout);
            left_chat_textView = itemView.findViewById(R.id.left_chat_textView);

            right_chat_layout = itemView.findViewById(R.id.right_chat_layout);
            right_chat_textView = itemView.findViewById(R.id.right_chat_textView);

        }
    }
}
