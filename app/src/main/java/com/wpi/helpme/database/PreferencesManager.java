package com.wpi.helpme.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class represents the single instance that manages the shared preferences for the
 * application.
 */
public class PreferencesManager {
    private static final String DEVICE_TOKEN = "registration_id";
    private SharedPreferences preferences;

    /**
     * Creates a {@link PreferencesManager} instance.
     *
     * @param context
     *         The application context to instantiate with.
     */
    public PreferencesManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Retrieves the device token from the shared preferences.
     *
     * @return a String
     */
    public String getDeviceToken() {
        return preferences.getString(DEVICE_TOKEN, null);
    }

    /**
     * Updates the shared preferences with a new device token.
     *
     * @param token
     *         The refreshed device token.
     */
    public void updateDeviceToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICE_TOKEN, token);
        editor.apply();
        editor.commit();
    }
}
