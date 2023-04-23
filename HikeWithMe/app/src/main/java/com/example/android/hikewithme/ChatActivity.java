package com.example.android.hikewithme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;



import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatAdapter.Chat> chatList = new ArrayList<>();

    private EditText messageEditText;
    private ImageButton sendButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_chat);



        // Get a reference to the chat RecyclerView and set its layout manager
        chatRecyclerView = findViewById(R.id.recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the chat adapter and attach it to the RecyclerView
        chatAdapter = new ChatAdapter(chatList);
        chatRecyclerView.setAdapter(chatAdapter);

        // Get a reference to the message EditText and send button
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);


        // Get a reference to the Firebase Realtime Database and listen for new chat messages
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatAdapter.Chat chat = snapshot.getValue(ChatAdapter.Chat.class);
                    chatList.add(chat);
                }

                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ChatActivity", "onCancelled: " + databaseError.getMessage());
            }
        });

        // Set an OnClickListener on the send button to send a new chat message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString().trim();

                if (!message.isEmpty()) {
                    String chatId = databaseReference.push().getKey();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ChatAdapter.Chat chat = new ChatAdapter.Chat(chatId, userId, message);
                    databaseReference.child(chatId).setValue(chat);

                    messageEditText.setText("");
                }
            }
        });
    }
}