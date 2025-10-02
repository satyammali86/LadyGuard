package com.example.women_safety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class Activity_splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        // Check if user is already logged in
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);


        if (isLoggedIn) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Activity_splash.this, Activity_home.class);
                startActivity(intent);
                finish();
            }, 1300); // Adjust the time delay if needed
        }else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Activity_splash.this, Activity_login.class);
                startActivity(intent);
                finish();
            }, 1300); // Adjust the time delay if needed
        }
    }
}