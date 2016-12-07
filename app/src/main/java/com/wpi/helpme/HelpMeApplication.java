package com.wpi.helpme;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wpi.helpme.com.wpi.helpme.profile.UserProfile;

/**
 * This class represents the high level application that persists over all activities.
 */
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

    /**
     * Returns the single instance of the application.
     * @return a {@link HelpMeApplication}
     */
    public static HelpMeApplication getInstance() {
        return instance;
    }

    /**
     * Returns the user profile of the user logged in.
     * @return a {@link UserProfile}
     */
    public UserProfile getUserProfile() {
        return profile;
    }

    /**
     * Returns the Firebase database reference.
     * @return a {@link DatabaseReference}
     */
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * Stores the specified profile as the currently logged in user.
     * @param profile The {@link UserProfile} instance of the current user.
     */
    public void storeProfile(UserProfile profile) {
        this.profile = profile;
    }
}
