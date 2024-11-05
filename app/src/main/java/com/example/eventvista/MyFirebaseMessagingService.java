package com.example.eventvista;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Create an Intent to send to NotificationReceiver
            Intent intent = new Intent("com.example.eventvista.NOTIFICATION");
            intent.putExtra("title", title);
            intent.putExtra("message", body);
            sendBroadcast(intent); // Send the broadcast
        }
    }


}
