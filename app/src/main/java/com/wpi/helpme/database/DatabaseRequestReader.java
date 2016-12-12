package com.wpi.helpme.database;

import com.google.firebase.database.DatabaseReference;

/**
 * This class represents a static class that can be used to read help requests from the database.
 */
public class DatabaseRequestReader {
    private static final String TAG = "DatabaseRequestReader";
    private static final String REQUESTS_DIR = "requests/";

    /**
     * Reads the requests from the database and updates the application instance with the request
     * list.
     *
     * @param db
     *         The {@link DatabaseReference} for the Firebase database root.
     * @param runnable
     *         The {@link Runnable} to execute once the application has an updated list of
     *         requests.
     */
    public static void readRequestsFromDatabase(DatabaseReference db, Runnable runnable) {
        final DatabaseReference dbRef = db.child(REQUESTS_DIR);
        dbRef.addListenerForSingleValueEvent(new LoadRequestValueEventListener(runnable));
    }
}
