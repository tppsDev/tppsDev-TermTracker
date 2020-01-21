package com.example.termtracker.util;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.example.termtracker.R;

import static com.example.termtracker.TermTracker.ALERT_CHANNEL_1;


public class AlertReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        int alertId = intent.getIntExtra(context.getString(R.string.alert_id_key), -1);
        Notification notification = new Notification.Builder(context, ALERT_CHANNEL_1)
                .setSmallIcon(R.drawable.ic_school_dark_24dp)
                .setContentTitle(intent.getStringExtra(context.getString(R.string.alert_title_key)))
                .setContentText(intent.getStringExtra(context.getString(R.string.alert_message_key)))
                .setCategory(Notification.CATEGORY_ALARM)
                .build();
        notificationManager.notify(alertId, notification);
    }
}
