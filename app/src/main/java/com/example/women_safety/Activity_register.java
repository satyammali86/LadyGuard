package com.example.women_safety;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.women_safety.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Activity_register extends AppCompatActivity {

    // UI elements
    private TextInputEditText fullNameInput, phoneInput, emergencyContactInput, emailInput, passwordInput, otpInput;
    private Button registerButton, verifyOtpButton;
    private TextInputLayout otpInputLayout;

    // Firebase database reference
    private DatabaseReference databaseReference;

    // Variables to temporarily store registration details after OTP is generated
    private String registeredFullName, registeredPhone, registeredEmergencyContact, registeredEmail, registeredPassword;
    // Generated OTP to compare with user input
    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI Elements
        fullNameInput = findViewById(R.id.fullNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emergencyContactInput = findViewById(R.id.emergencyContactInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);

        otpInputLayout = findViewById(R.id.otpInputLayout);
        otpInput = findViewById(R.id.otpInput);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        // Set onClick for Login Text to navigate to login screen
        findViewById(R.id.loginText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_register.this, Activity_login.class);
                startActivity(intent);
            }
        });

        // When the user taps the Register button:
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Read input field values
                final String fullName = fullNameInput.getText().toString().trim();
                final String phone = phoneInput.getText().toString().trim();
                final String emergencyContact = emergencyContactInput.getText().toString().trim();
                final String email = emailInput.getText().toString().trim();
                final String password = passwordInput.getText().toString().trim();

                // Validate that no field is empty
                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) ||
                        TextUtils.isEmpty(emergencyContact) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password)) {
                    Toast.makeText(Activity_register.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if a user with this email already exists in the database
                databaseReference.orderByChild("email").equalTo(email)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Email already exists
                                    Toast.makeText(Activity_register.this, "User with this email already exists!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Save registration details for later use
                                    registeredFullName = fullName;
                                    registeredPhone = phone;
                                    registeredEmergencyContact = emergencyContact;
                                    registeredEmail = email;
                                    registeredPassword = password;

                                    // Generate a 6-digit OTP
                                    generatedOtp = String.valueOf(100000 + new Random().nextInt(900000));

                                    // Simulate sending the OTP via email (replace with your own email-sending code)
                                    sendOtpEmail(registeredEmail, generatedOtp);

                                    // Inform the user to check their email for the OTP
                                    Toast.makeText(Activity_register.this, "OTP sent to your email. Please check your inbox.", Toast.LENGTH_LONG).show();

                                    // Show the OTP input field and the verify button
                                    otpInputLayout.setVisibility(View.VISIBLE);
                                    verifyOtpButton.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Activity_register.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // When the user taps the Verify OTP button:
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredOtp = otpInput.getText().toString().trim();
                if (TextUtils.isEmpty(enteredOtp)) {
                    Toast.makeText(Activity_register.this, "Please enter the OTP!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enteredOtp.equals(generatedOtp)) {
                    // OTP is correct—proceed to complete the registration
                    completeRegistration();
                } else {
                    Toast.makeText(Activity_register.this, "Incorrect OTP! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Simulate sending an OTP to the provided email.
     * In a production app, call your backend API or use Firebase Cloud Functions
     * integrated with an email API (e.g., SendGrid, Mailgun) to send the OTP.
     */
    private void sendOtpEmail(String email, String otp) {
        String subject = "Secure Your Account - OTP for Women Safety App Registration";
        String message = "Dear User,\n\n"
                + "Thank you for registering with our Women Safety Application. "
                + "To complete your registration, please use the One-Time Password (OTP) below:\n\n"
                + "🔒 OTP: " + otp + "\n\n"
                + "For security reasons, do not share this OTP with anyone. It will expire in 10 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Stay safe,\n"
                + "Women Safety App Team";

        // Execute the JavaMailAPI AsyncTask to send the email
        new JavaMailAPI(Activity_register.this, email, subject, message).execute();
    }

    /**
     * Complete the registration by writing the user data to Firebase Database.
     */
    private void completeRegistration() {
        // Create a new unique key for the user
        String userId = databaseReference.push().getKey();
        User user = new User(registeredFullName, registeredPhone, registeredEmergencyContact, registeredEmail, registeredPassword);
        if (userId != null) {
            databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Activity_register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                            // Navigate to the login screen
                            Intent intent = new Intent(Activity_register.this, Activity_login.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Activity_register.this, "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
