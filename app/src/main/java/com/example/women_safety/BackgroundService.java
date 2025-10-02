package com.example.women_safety;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {
    private static final String CHANNEL_ID = "BackgroundServiceChannel";
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        runnable = new Runnable() {
            @Override
            public void run() {
                myFunction(); // Call the function every 3 seconds
                handler.postDelayed(this, 3000); // Repeat every 3 seconds
            }
        };
        handler.post(runnable);
    }

    private void myFunction() {
        Log.d("BackgroundService", "Function called every 3 seconds!");
        // Add your logic here (e.g., send location to Firebase)
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Background Service Running")
                .setContentText("This service is running in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(1, notification);
        return START_STICKY; // Restart service if it's killed
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop repeating task
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
