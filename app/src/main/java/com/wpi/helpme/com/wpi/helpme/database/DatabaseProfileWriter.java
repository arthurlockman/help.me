package com.wpi.helpme.com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * This class represents a singleton instance that can be used to write user profiles to the
 * database.
 */
public class DatabaseProfileWriter {
    private static final String PROFILES_DIR = "profiles/";
    private static final String TAG = "DatabaseProfileWriter";
    private static DatabaseProfileWriter instance;

    /**
     * Creates a DatabaseProfileWriter instance.
     */
    private DatabaseProfileWriter() {
    }

    /**
     * Returns the global instance for this class.
     *
     * @return a DatabaseProfileWriter
     */
    public static DatabaseProfileWriter getInstance() {
        if (instance == null) {
            instance = new DatabaseProfileWriter();
        }
        return instance;
    }

    /**
     * Retrieves the profile object from the database for the specified user profile. Executes the
     * provided callback depending on the existence of the database entry.
     *
     * @param db
     *         The {@link DatabaseReference} for the Firebase database root.
     * @param userId
     *         The user ID of the user to check.
     * @param listener
     *         The one time listener to execute to get the profile.
     */
    public void retrieveProfile(DatabaseReference db, String userId,
                                ValueEventListener listener) {
        // Go to profiles entry
        final DatabaseReference dbRef = db.child(PROFILES_DIR + userId);

        // Add one time listener to "retrieve" value
        dbRef.addListenerForSingleValueEvent(listener);
    }

    /**
     * Writes the specified profile to the database.
     *
     * @param db
     *         The {@link DatabaseReference} for the Firebase database root.
     * @param profile
     *         The {@link UserProfile} to write.
     */
    public void writeProfile(DatabaseReference db, final UserProfile profile) {
        // Go to profiles entry
        DatabaseReference dbRef = db.child(PROFILES_DIR + profile.getUserId());

        // Run transaction to prevent incorrect values
        dbRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserProfile p = mutableData.getValue(UserProfile.class);
                mutableData.setValue(profile);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "profile-Transaction:onComplete:" + databaseError);
            }
        });
    }
}
