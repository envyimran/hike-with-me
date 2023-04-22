package com.example.android.hikewithme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String API_KEY = "KEY HERE";
    private ScrollView scrollView;
    private LinearLayout container;

    private SearchView searchBar;

    private String searchContent = "";
    private String selectedAddress = "";
    private Button chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = findViewById(R.id.searchbar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("Search Detected!");
                searchContent = query;
                // Request location permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Get the device's current location
                    Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(MainActivity.this).getLastLocation();
                    // Use the Places API to find nearby hiking trails
                    locationTask.addOnSuccessListener(MainActivity.this::findHikingTrails);
                    searchBar.clearFocus();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
                return true;
            }
        });
        chatButton = findViewById(R.id.button3);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat(v);
            }
        });
        container = new LinearLayout(this);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);

        scrollView = findViewById(R.id.scroll_view);
        scrollView.addView(container);

        // Initialize the Places API client
        Places.initialize(getApplicationContext(), API_KEY);

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the device's current location
            Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            // Use the Places API to find nearby hiking trails
            locationTask.addOnSuccessListener(this::findHikingTrails);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void findHikingTrails(Location location) {
        container.removeAllViews();
        new PlacesAsyncTask().execute(location);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get the device's current location
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                }
                Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
                // Use the Places API to find nearby hiking trails
                locationTask.addOnSuccessListener(this::findHikingTrails);
            }
        }
    }

    public void goToChat(View view) {
        //Intent chat = new Intent(this, Verification.class);
        //startActivity(chat);
    }
    private class PlacesAsyncTask extends AsyncTask<Location, Void, PlacesSearchResponse> {

        private GeoApiContext context;

        @Override
        protected void onPreExecute() {
            // Set up the GeoApiContext with the API key
            context = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();
        }

        @Override
        protected PlacesSearchResponse doInBackground(Location... locations) {
            // Set the location bias to the user's current location
            LatLng loc = new LatLng(locations[0].getLatitude(), locations[0].getLongitude());

            // Search for hiking trails within a radius of 5000 meters
            NearbySearchRequest request;
            if(searchContent.equals("")) request = PlacesApi.nearbySearchQuery(context, loc)
                    .radius(30000)
                    .keyword("hiking trail")
                    .rankby(RankBy.PROMINENCE);
            else request = PlacesApi.nearbySearchQuery(context, loc)
                    .radius(30000)
                    .keyword("hiking trail")
                    .name(searchContent)
                    .rankby(RankBy.PROMINENCE);
            try {
                return request.await();
            } catch (ApiException | IOException | InterruptedException e) {
                Log.e("TAG", "Error finding hiking trails", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PlacesSearchResponse result) {
            // Add each hiking trail's name and address to the container
            if (result != null) {
                for (PlacesSearchResult place : result.results) {
                    LinearLayout child = new LinearLayout(MainActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
                    params.setMargins(100, 20, 0 ,20);

                    child.setLayoutParams(params);
                    child.setOrientation(LinearLayout.VERTICAL);
                    child.setPadding(100, 10, 100, 10);
                    child.setBackgroundResource(R.drawable.trailborder);

                    TextView nameTextView = new TextView(MainActivity.this);
                    nameTextView.setText(place.name);
                    nameTextView.setGravity(Gravity.CENTER);
                    nameTextView.setTextColor(getResources().getColor(R.color.black));
                    child.addView(nameTextView);

                    TextView addressTextView = new TextView(MainActivity.this);
                    addressTextView.setText(place.vicinity);
                    addressTextView.setGravity(Gravity.CENTER);
                    addressTextView.setTextColor(getResources().getColor(R.color.black));
                    child.addView(addressTextView);

                    RatingBar ratingBar = new RatingBar(MainActivity.this);
                    ratingBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                    ratingBar.setNumStars(5);
                    ratingBar.setRating(place.rating);
                    ratingBar.setScaleX(0.5f);
                    ratingBar.setScaleY(0.5f);
                    ratingBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(248, 180, 0)));
                    ratingBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    ratingBar.setIsIndicator(true);

                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedAddress = addressTextView.getText().toString();
                            //TODO Open fragment
                            Toast toast = Toast.makeText(MainActivity.this, selectedAddress, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                    child.addView(ratingBar);
                    container.addView(child);
                }
            }
        }
    }
}