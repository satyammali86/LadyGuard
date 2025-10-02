package com.example.women_safety;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signUpText;

    private DatabaseReference databaseReference;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);
        // Check if user is already logged in
         preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If logged in, skip login and go directly to HomeActivity
            Intent intent = new Intent(Activity_login.this, Activity_home.class);
            startActivity(intent);
            finish();
        }

        // Login Button Click
        loginButton.setOnClickListener(v -> loginUser());

        // Navigate to Register Activity
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_login.this, Activity_register.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        // Query Firebase Database to check if user exists
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User exists, validate the password
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        if (dbPassword != null && dbPassword.equals(password)) {
                            // Successful login
                            Toast.makeText(Activity_login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Save login state in SharedPreferences
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();
                            // Redirect to Home Activity
                            Intent intent = new Intent(Activity_login.this,Activity_home.class);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            // Incorrect password
                            Toast.makeText(Activity_login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    // User does not exist
                    Toast.makeText(Activity_login.this, "User not found, Please register first to login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
