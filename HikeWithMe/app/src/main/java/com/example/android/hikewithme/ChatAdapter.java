package com.example.android.hikewithme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> mChatList;

    public ChatAdapter(List<Chat> chatList) {
        mChatList = chatList;
    }
    private List<Chat> chatList = new ArrayList<>();

    private Context context;



    public void addChat(Chat chat) {
        chatList.add(chat);
        notifyItemInserted(chatList.size() - 1);
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = mChatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public void clearChats() {
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mMessageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mMessageTextView = itemView.findViewById(R.id.message_text_view);
        }

        public void bind(Chat chat) {
            mMessageTextView.setText(chat.getMessage());
        }
    }
    public static class Chat {

        private String mMessage;

        public Chat() {
            // Default constructor required for Firebase Realtime Database
        }

        public Chat(String message) {
            mMessage = message;
        }

        public Chat(String chatId, String userId, String message) {
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }
    }

}