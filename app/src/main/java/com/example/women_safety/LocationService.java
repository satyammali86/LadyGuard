package com.example.women_safety;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import android.util.Log;

public class LocationService extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String userEmail;
    private DatabaseReference databaseReference;
    private LocationCallback locationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            userEmail = intent.getStringExtra("userEmail");
        }
        if (userEmail == null || userEmail.isEmpty()) {
            // Handle the case where userEmail is not provided
            stopSelf();
            return START_NOT_STICKY;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(300000); // 5 minutes in milliseconds
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        storeLocation(location);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void storeLocation(Location location) {
        // Generate a unique ID for the location entry
        String locationId = databaseReference.push().getKey();
        if (locationId == null) {
            Log.e("LocationService", "Failed to generate a unique ID for the location entry.");
            return;
        }

        // Create a Google Maps link
        String googleMapsLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();

        // Create a LocationData object with the unique ID and Google Maps link
        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), googleMapsLink);

        // Store the location data under the user's email and the unique location ID
        DatabaseReference locationRef = databaseReference.child(userEmail).child("location").child(locationId);
        locationRef.setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("LocationService", "Location stored successfully: " + locationId);
                })
                .addOnFailureListener(e -> {
                    Log.e("LocationService", "Failed to store location: " + e.getMessage());
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}