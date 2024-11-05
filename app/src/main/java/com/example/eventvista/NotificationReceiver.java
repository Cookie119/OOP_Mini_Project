// NotificationReceiver.java
package com.example.eventvista;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "event_notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the intent's action is the one we are listening for
        if (intent.getAction().equals("com.example.eventvista.NOTIFICATION")) {
            // Extract data from the intent
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            showNotification(context, title, message);
        }
    }

    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Set your own notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Notify the user
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
