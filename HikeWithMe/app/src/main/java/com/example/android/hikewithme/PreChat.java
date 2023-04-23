package com.example.android.hikewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;


public class PreChat extends AppCompatActivity implements OnMapReadyCallback {
    private Button startChatButton;
    private Button buttonLogin;
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_pre_chat);

        // Initialize the activity_map.xml fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Get a reference to the start chat button
        startChatButton = findViewById(R.id.start_chat_button);

        // Set an OnClickListener to launch the ChatActivity when the button is clicked
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreChat.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        // Launch LoginActivity and finish the current activity after the startChatButton is set up
        Intent intent = new Intent(PreChat.this, LoginActivity.class);
        startActivity(intent);
        finish(); // finish the MainActivity so that the user cannot go back to it without logging in
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add pins to the activity_map.xml
        LatLng location1 = new LatLng(40.6892, -74.0445);
        map.addMarker(new MarkerOptions().position(location1).title("Marker 1"));

        LatLng location2 = new LatLng(51.5074, -0.1278);
        map.addMarker(new MarkerOptions().position(location2).title("Marker 2"));

        // Set the camera position to the first location
        map.moveCamera(CameraUpdateFactory.newLatLng(location1));
    }
}