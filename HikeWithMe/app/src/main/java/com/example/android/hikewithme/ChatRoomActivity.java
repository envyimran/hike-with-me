package com.example.android.hikewithme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseSecurityReference;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        checkSecurity();

        mRecyclerView = findViewById(R.id.recycler_view);
        mMessageEditText = findViewById(R.id.message_edit_text);
        mSendButton = findViewById(R.id.send_button);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // user is not authenticated, redirect to login activity
            Intent intent = new Intent(ChatRoomActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // finish current activity
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatAdapter = new ChatAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mChatAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");
        mValueEventListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatAdapter.Chat> chats = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatAdapter.Chat chat = snapshot.getValue(ChatAdapter.Chat.class);
                    chats.add(chat);
                }
                mChatAdapter = new ChatAdapter(chats);
                mRecyclerView.setAdapter(mChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatRoomActivity", "onCancelled", databaseError.toException());
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    ChatAdapter.Chat chat = new ChatAdapter.Chat(message);
                    mDatabaseReference.push().setValue(chat);
                    mMessageEditText.setText("");
                }
            }
        });
    }

    private void checkSecurity() {
        mDatabaseSecurityReference = FirebaseDatabase.getInstance().getReference("security");
        Query query = mDatabaseSecurityReference.getRef();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isApproved = false;
                System.out.println("Ran");
                List<SecureModel> secures = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SecureModel secure = snapshot.getValue(SecureModel.class);
                    secures.add(secure);
                }
                for(SecureModel secure: secures) {
                    if(secure.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        isApproved = true;
                        break;
                    }
                }
                if(!isApproved) {
                    Intent intent = new Intent(ChatRoomActivity.this, Verification.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Review", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mValueEventListener);
    }
}