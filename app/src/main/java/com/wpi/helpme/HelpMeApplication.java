package com.wpi.helpme;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wpi.helpme.com.wpi.helpme.profile.UserProfile;

public class HelpMeApplication extends Application {

    private static HelpMeApplication instance = null;
    private static UserProfile profile = null;
    private static DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static HelpMeApplication getInstance() {
        return instance;
    }

    public UserProfile getUserProfile() {
        return profile;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void storeProfile(UserProfile profile) {
        this.profile = profile;
    }
}
