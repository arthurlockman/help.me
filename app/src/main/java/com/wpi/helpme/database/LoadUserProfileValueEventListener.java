package com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.profile.UserProfile;

/**
 * This class represents the callback listener that executes when a profile is loaded at user login.
 * If the profile does not exist in the database, it is written back. The profile is saved into the
 * high level application.
 */
public class LoadUserProfileValueEventListener implements ValueEventListener {
    private static final String TAG = "LoadUserProfileValueEventListener";
    private UserProfile profile;

    /**
     * Creates a LoadUserProfileValueEventListener instance.
     *
     * @param profile
     *         The {@link UserProfile} to load from the database or write to the database.
     */
    public LoadUserProfileValueEventListener(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * @see {@link ValueEventListener#onDataChange(DataSnapshot)}
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String token = HelpMeApplication.getInstance().getPreferencesManager().getDeviceToken();
        Log.d(TAG, "Device token is: " + token);

        if (dataSnapshot.getValue() == null) {
            if (token.length() > 0) {
                profile.setDeviceToken(token);
            }

            Log.d(TAG, "Writing new profile.");
        } else {
            Log.d(TAG, "Getting profile from database...");
            profile = dataSnapshot.getValue(UserProfile.class);
            profile.setDeviceToken(token);
        }

        HelpMeApplication.getInstance().storeProfile(profile);
        HelpMeApplication.getInstance().syncProfileToDatabase();
    }

    /**
     * @see {@link ValueEventListener#onCancelled(DatabaseError)}
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "profile-ValueEventListener:onCancelled:" + databaseError);
    }
}
