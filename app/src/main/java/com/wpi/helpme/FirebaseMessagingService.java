package com.wpi.helpme;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService
        extends com.google.firebase.messaging.FirebaseMessagingService {
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
