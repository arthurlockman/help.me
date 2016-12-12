package com.wpi.helpme;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseTokenRegistrationService extends FirebaseInstanceIdService
{
    private static final String TAG = "TokenRegisterService";
    public FirebaseTokenRegistrationService()
    {
    }

    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        this.syncNewToken(refreshedToken);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("registration_id", refreshedToken);
        editor.apply();
    }

    private String getTokenFromPrefs()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("registration_id", null);
    }

    private void syncNewToken(String token) {
        try {
            HelpMeApplication.getInstance().getUserProfile().setDeviceToken(token);
            HelpMeApplication.getInstance().syncProfileToDatabase();
        } catch (NullPointerException e) {
            // Only exception thrown because profile may not exist on first install
            Log.d(TAG, "User profile does not exist.");
        } catch (Exception e) {
            // All else fails catch
            e.printStackTrace();
        }
    }
}