package net.itempire.brokerapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MYFireBaseMessageingService extends FirebaseMessagingService {
    public MYFireBaseMessageingService() {
    }
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
/*
    @Override
    public void onNewToken(String token) {
        *//*super.onNewToken(token);*//*
        Log.d(TAG, "Refreshed token: " + token);
    }*/
    /*
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }*/

}
