package com.example.women_safety;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                Intent serviceIntent = new Intent(context, LocationService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
