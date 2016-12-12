package com.wpi.helpme.com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.com.wpi.helpme.profile.UserProfile;

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

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "Writing new profile.");
            DatabaseProfileWriter
                    .writeProfile(HelpMeApplication.getInstance().getDatabaseReference(), profile);
        } else {
            Log.d(TAG, "Getting profile from database...");
            profile = dataSnapshot.getValue(UserProfile.class);
        }

        HelpMeApplication.getInstance().storeProfile(profile);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "profile-ValueEventListener:onCancelled:" + databaseError);
    }
}
