package com.example.women_safety.Model;

public class User {
    public String fullName;
    public String phone;
    public String emergencyContact;
    public String email;
    public String password;

    // Default constructor (required for Firebase)
    public User() {}

    public User(String fullName, String phone, String emergencyContact, String email, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.emergencyContact = emergencyContact;
        this.email = email;
        this.password = password;
    }
}
