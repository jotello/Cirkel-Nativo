package com.cirkel.nativo.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;
import static com.cirkel.nativo.network.FirebaseNetwork.getDatabaseReference;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("REC_ALERT", String.valueOf(Calendar.getInstance().getTime()));
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("INFO", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token) {
        String userId = getAuthInstance().getCurrentUser().getUid();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userId + "/fcmToken", token);
        getDatabaseReference().updateChildren(childUpdates);
    }

}
