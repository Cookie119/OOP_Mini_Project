package com.example.eventvista;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventNotificationHelper {
    private Context context;

    public EventNotificationHelper(Context context) {
        this.context = context;
    }

    public void sendEventNotification(String eventTitle, String eventDetails) {
        String channelId = "event_notifications";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification) // Use your own icon here
                .setContentTitle("Upcoming Event: " + eventTitle)
                .setContentText(eventDetails)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }
}
