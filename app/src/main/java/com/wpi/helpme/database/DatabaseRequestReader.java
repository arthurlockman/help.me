package com.wpi.helpme.database;

import com.google.firebase.database.DatabaseReference;

public class DatabaseRequestReader {
    private static final String TAG = "DatabaseRequestReader";
    private static final String REQUESTS_DIR = "requests/";

    public static void readRequestsFromDatabase(DatabaseReference db) {
        final DatabaseReference dbRef = db.child(REQUESTS_DIR);
        dbRef.addListenerForSingleValueEvent(new LoadRequestValueEventListener());
    }
}
