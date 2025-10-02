package com.example.women_safety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Your code here to perform the task
        Log.d("AlarmReceiver", "Alarm received!");
        // Example: Call your function here
        performTask();
    }

    private void performTask() {
        // Your task logic here
        Log.d("AlarmReceiver", "Task performed!");
    }
}