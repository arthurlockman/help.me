package com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.wpi.helpme.profile.UserProfile;

/**
 * This class represents a singleton instance that can be used to write user profiles to the
 * database.
 */
public class DatabaseProfileWriter {
    private static final String PROFILES_DIR = "profiles/";
    private static final String TAG = "DatabaseProfileWriter";

    /**
     * Retrieves the profile object from the database for the specified user profile. Loads the
     * profile into the main application as the main profile instance. If the profile is new, writes
     * it to the database.
     *
     * @param db
     *         The {@link DatabaseReference} for the Firebase database root.
     * @param profile
     *         The {@link UserProfile} of the user to check.
     */
    public static void loadDatabaseProfile(DatabaseReference db, final UserProfile profile) {
        // Go to profiles entry
        final DatabaseReference dbRef = db.child(PROFILES_DIR + profile.getUserId());

        // Add one time listener to "retrieve" value
        dbRef.addListenerForSingleValueEvent(new LoadUserProfileValueEventListener(profile));
    }

    /**
     * Writes the specified profile to the database.
     *
     * @param db
     *         The {@link DatabaseReference} for the Firebase database root.
     * @param profile
     *         The {@link UserProfile} to write.
     */
    public static void writeProfile(DatabaseReference db, final UserProfile profile) {
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
