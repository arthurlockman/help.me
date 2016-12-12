package com.wpi.helpme;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wpi.helpme.database.DatabaseProfileWriter;
import com.wpi.helpme.database.HelpRequest;
import com.wpi.helpme.database.PreferencesManager;
import com.wpi.helpme.profile.UserProfile;

import java.util.List;

/**
 * This class represents the high level application that persists over all activities.
 */
public class HelpMeApplication extends Application {
    private static String TAG = "HelpMeApplication";
    private static HelpMeApplication instance = null;
    private static UserProfile profile = null;
    private static DatabaseReference databaseReference;
    private static PreferencesManager preferencesManager;
    private static List<HelpRequest> requests;

    /**
     * Returns the single instance of the application.
     *
     * @return a {@link HelpMeApplication}
     */
    public synchronized static HelpMeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        preferencesManager = new PreferencesManager(this.getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Stores the specified profile as the currently logged in user.
     *
     * @param profile
     *         The {@link UserProfile} instance of the current user.
     */
    public synchronized void storeProfile(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Syncs the current user profile to the database.
     */
    public synchronized void syncProfileToDatabase() {
        Log.d(TAG, "Syncing profile to database...");
        DatabaseProfileWriter.writeProfile(getDatabaseReference(), getUserProfile());
    }

    /**
     * Returns the Firebase database reference.
     *
     * @return a {@link DatabaseReference}
     */
    public synchronized DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * Returns the user profile of the user logged in.
     *
     * @return a {@link UserProfile}
     */
    public synchronized UserProfile getUserProfile() {
        return profile;
    }

    /**
     * Returns the single instance of the preferences manager.
     *
     * @return a {@link PreferencesManager}
     */
    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }

    /**
     * Updates the list of requests with the new list.
     *
     * @param newRequests
     *         The {@link List<HelpRequest>} of new requests.
     */
    public void updateRequests(List<HelpRequest> newRequests) {
        requests = newRequests;
    }

    /**
     * Returns the list of requests from the database.
     *
     * @return a {@link List<HelpRequest>}
     */
    public List<HelpRequest> getRequests() {
        return requests;
    }
}
