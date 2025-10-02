package com.example.women_safety;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;


public class Activity_home extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean isEnabled = false;


    private FusedLocationProviderClient fusedLocationProviderClient;

    // Shake detection parameters
    private static final float SHAKE_THRESHOLD = 12.0f; // Adjust as needed
    private static final int SHAKE_DURATION_THRESHOLD = 300; // Minimum shake duration in milliseconds
    private static final int LONG_PRESS_THRESHOLD = 1000; // 1 second
    private long volumeDownPressTime = 0;

    private long shakeStartTime = 0;
    private boolean isEmergencyTriggered = false;

    // Fixed parent's phone number
    private static final String PARENT_PHONE_NUMBER = "8766534031"; // Replace with the parent's phone number
    private LinearLayout card_Logout,card_Helpline,cardRelatives,cardSiren,cardSelf,howTo;
    SharedPreferences preferences;
    public SwitchMaterial toggleShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //helpline button
        card_Helpline = findViewById(R.id.card_helpline);
        card_Helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_home.this, Activity_emergency.class);
                startActivity(intent);
            }
        });

        //Relatives card
        cardRelatives = findViewById(R.id.card_parent);
        cardRelatives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_home.this, Activity_relatives.class);
                startActivity(intent);
            }
        });

        //logout button
        card_Logout = findViewById(R.id.card_logout);
        card_Logout.setOnClickListener(v -> logoutUser());
        // Find the toggle switch by ID
         toggleShake = findViewById(R.id.toggle_shake);
         toggleShake.setClickable(false);
        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this, R.raw.siren);


        //card siren
        cardSiren = findViewById(R.id.card_siren);
        cardSiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    toggleShake.setChecked(true);
                    toggleShake.setClickable(true);
                }
            }
        });

        //self defence
        cardSelf = findViewById(R.id.card_self);
        cardSelf.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_home.this, Activity_selfdefence.class);
            startActivity(intent);
        });

        //how to use
         howTo = findViewById(R.id.card_howto);
         howTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_home.this, Activity_howto.class);
                startActivity(intent);
            }
        });



        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                // Set up the accelerometer event listener
                sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            detectShake(event);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        // No implementation needed
                    }
                };
                // Register the listener
                sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(this, "Accelerometer not available!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SensorManager initialization failed!", Toast.LENGTH_SHORT).show();
        }

        // Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for required permissions
        checkPermissions();


        // Set the listener for toggle changes
        toggleShake.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                // Show toast when disabled
                Toast.makeText(this, "Emergency service Stopped", Toast.LENGTH_SHORT).show();

                // Stop emergency actions
                stopEmergencyActions();
            }
        });
    }


    private void detectShake(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calculate the acceleration magnitude
        float accelerationMagnitude = (float) Math.sqrt(x * x + y * y + z * z);

        if (accelerationMagnitude > SHAKE_THRESHOLD) {
            long currentTime = System.currentTimeMillis();

            if (shakeStartTime == 0) {
                // Start counting shake duration
                shakeStartTime = currentTime;
            } else if (currentTime - shakeStartTime >= SHAKE_DURATION_THRESHOLD && !isEmergencyTriggered) {
                // If shaking persists for the required duration, trigger emergency actions
                isEmergencyTriggered = true;
                triggerEmergencyActions();
            }
        } else {
            // Reset shake detection if the acceleration drops below the threshold
            shakeStartTime = 0;
            isEmergencyTriggered = false;
        }
    }
    private void triggerEmergencyActions() {
        // Vibrate the phone
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE)); // Vibrate for 2 seconds
            } else {
                vibrator.vibrate(2000);
            }
        }

        // Play the sound
        if (mediaPlayer != null) {
            mediaPlayer.start();
            toggleShake.setChecked(true);
        }

        // Send location and message
        sendLocation();
    }

    private void sendLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String locationMessage = "Hello, I am in danger, Please urgently reach me out. " +
                                    "Here is my live location:\n" +
                                    "\nGoogle Maps Link: https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();

                            sendMessage(locationMessage);
                        } else {
                            Toast.makeText(Activity_home.this, "Unable to get location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendMessage(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(PARENT_PHONE_NUMBER, null, message, null, null);
            Toast.makeText(this, "Emergency message sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0) {
                if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permission granted!", Toast.LENGTH_SHORT).show();
                } else if (permissions[0].equals(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS permission granted!", Toast.LENGTH_SHORT).show();
                } else if (permissions[0].equals(Manifest.permission.CALL_PHONE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Call permission granted!", Toast.LENGTH_SHORT).show();
                    emergencyCall(); // Call immediately after permission granted
                } else {
                    Toast.makeText(this, "Some permissions denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release resources
        if (sensorManager != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void stopEmergencyActions() {
        toggleShake.setClickable(false);
        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Stop sound
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(this, R.raw.siren); // Reset the media player
        }

    }
    private void logoutUser() {
        // Clear the login state in SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Remove all stored preferences
        editor.apply();

        // Show a Toast message
        Toast.makeText(Activity_home.this, "You have been logged out", Toast.LENGTH_SHORT).show();

        // Redirect to Login Activity
        Intent intent = new Intent(Activity_home.this, Activity_login.class);
        startActivity(intent);

        // Close the current activity
        finish();
    }
    private void emergencyCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(android.net.Uri.parse("tel:" + PARENT_PHONE_NUMBER));
        startActivity(callIntent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (volumeDownPressTime == 0) {
                volumeDownPressTime = System.currentTimeMillis(); // Start timing
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            long pressDuration = System.currentTimeMillis() - volumeDownPressTime;
            volumeDownPressTime = 0; // Reset timer

            if (pressDuration >= LONG_PRESS_THRESHOLD) {
                // Long press detected
                emergencyCall();
            } else {
                Toast.makeText(this, "Long press Volume Down to trigger emergency call", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}

