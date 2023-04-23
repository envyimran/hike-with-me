package com.example.android.hikewithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrailData extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private ScrollView scrollView;
    private LinearLayout container;
    private String locationAddress;
    private String trailName;
    private Button addReviewButton;
    private Button weather;
    private TextView trail;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_info);
        container = new LinearLayout(this);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);
        weather = findViewById(R.id.weather);
        scrollView = findViewById(R.id.review_scrollview);
        scrollView.addView(container);
        addReviewButton = findViewById(R.id.addReviewButton);
        trail = findViewById(R.id.pageHeader);
        Intent intent = getIntent();
        locationAddress = intent.getStringExtra("locationAddress");
        trailName = intent.getStringExtra("trailName");
        trail.setText(trailName);
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        getReviewsByLocationAddress(locationAddress);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newReview = new Intent(TrailData.this, Review.class);
                newReview.putExtra("locationAddress", locationAddress);
                startActivity(newReview);
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newWeather = new Intent(TrailData.this, Weather.class);
                newWeather.putExtra("lat", lat);
                newWeather.putExtra("lng", lng);
                startActivity(newWeather);
            }
        });
    }

    private void getReviewsByLocationAddress(String locationAddress) {
        scrollView.clearFocus();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = mDatabaseReference.child(locationAddress);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Ran");
                List<ReviewModel> reviews = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewModel review = snapshot.getValue(ReviewModel.class);
                    reviews.add(review);
                }
                for(ReviewModel review: reviews) {
                    LinearLayout child = new LinearLayout(TrailData.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
                    params.setMargins(100, 20, 0 ,20);
                    child.setLayoutParams(params);
                    child.setOrientation(LinearLayout.VERTICAL);
                    child.setPadding(100, 10, 100, 10);
                    child.setBackgroundResource(R.drawable.trailborder);

                    TextView name = new TextView(TrailData.this);
                    TextView content = new TextView(TrailData.this);
                    name.setText(review.getName());
                    content.setText(review.getReview());
                    child.addView(name);
                    child.addView(content);
                    container.addView(child);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Review", "onCancelled", databaseError.toException());
            }
        });
    }
}