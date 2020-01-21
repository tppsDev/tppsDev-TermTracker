package com.example.termtracker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class TermTracker extends Application {
    public static final String ALERT_CHANNEL_1 = "alert_channel_1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel  alertChannel1 = new NotificationChannel(
                    ALERT_CHANNEL_1,
                    "Term Tracker Alert Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            alertChannel1.setDescription("Term Tracker Alert Channel");

            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(alertChannel1);
            }
        }
    }
}
