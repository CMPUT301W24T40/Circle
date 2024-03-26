package com.example.circleapp.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * This class is used for Firebase Messaging ordeals.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("tag", "Refreshed token: " + token);
    }

    /**
     * This method is used for dealing with sending notifications
     * to apps on the foreground
     * @param remoteMessage Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    }

}
