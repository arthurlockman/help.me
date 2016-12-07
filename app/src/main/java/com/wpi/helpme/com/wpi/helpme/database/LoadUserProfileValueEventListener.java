package com.wpi.helpme.com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.com.wpi.helpme.profile.UserProfile;

public class LoadUserProfileValueEventListener implements ValueEventListener {
    private static final String TAG = "LoadUserProfileValueEventListener";
    private DatabaseReference db;
    private UserProfile profile;

    public LoadUserProfileValueEventListener(DatabaseReference db, UserProfile profile) {
        this.profile = profile;
        this.db = db;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "Writing new profile.");
            DatabaseProfileWriter
                    .writeProfile(db, profile);
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
