package com.example.women_safety;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class Activity_parent extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView locationText;
    private String userEmail = "user@example.com"; // Set email to fetch location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        locationText = findViewById(R.id.locationText);
        databaseReference = FirebaseDatabase.getInstance().getReference("UserLocations");

        fetchLiveLocation();
    }

    private void fetchLiveLocation() {
        databaseReference.child(userEmail.replace(".", "_")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    locationText.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                } else {
                    locationText.setText("Location not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                locationText.setText("Failed to load location");
            }
        });
    }
}
