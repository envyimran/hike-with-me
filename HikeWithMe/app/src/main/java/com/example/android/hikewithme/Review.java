package com.example.android.hikewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Review extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mReviewEditText;
    private TextView mLocationTextView;
    private Button mPostButton;

    private DatabaseReference mDatabaseReference;
    private String mLocationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mNameEditText = findViewById(R.id.nameEditText);
        mReviewEditText = findViewById(R.id.reviewEditText);
        mLocationTextView = findViewById(R.id.locationTextView);
        mPostButton = findViewById(R.id.postButton);
        Intent intent = getIntent();
        mLocationAddress = intent.getStringExtra("locationAddress");
        mLocationTextView.setText(mLocationAddress);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(mLocationAddress);

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                String review = mReviewEditText.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(review)) {
                    Toast.makeText(Review.this, "Please enter name and review", Toast.LENGTH_SHORT).show();
                } else {
                    ReviewModel reviewModel = new ReviewModel(name, review);
                    mDatabaseReference.push().setValue(reviewModel);

                    mNameEditText.setText("");
                    mReviewEditText.setText("");

                    Toast.makeText(Review.this, "Review posted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}