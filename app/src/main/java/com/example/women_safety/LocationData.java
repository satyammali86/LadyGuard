package com.example.women_safety;
public class LocationData {
    private double latitude;
    private double longitude;
    private String googleMapsLink;

    public LocationData() {}

    public LocationData(double latitude, double longitude, String googleMapsLink) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.googleMapsLink = googleMapsLink;
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGoogleMapsLink() {
        return googleMapsLink;
    }

    public void setGoogleMapsLink(String googleMapsLink) {
        this.googleMapsLink = googleMapsLink;
    }
}