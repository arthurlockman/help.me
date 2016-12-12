package com.wpi.helpme;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * This class represents the retrieval service that gets the refreshed device token.
 */
public class FirebaseTokenRegistrationService extends FirebaseInstanceIdService {
    private static final String TAG = "TokenRegisterService";

    /**
     * Creates a {@link FirebaseTokenRegistrationService} instance.
     */
    public FirebaseTokenRegistrationService() {
    }

    /**
     * @see {@link FirebaseInstanceIdService#onTokenRefresh()}
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        this.syncNewToken(refreshedToken);
        HelpMeApplication.getInstance().getPreferencesManager().updateDeviceToken(refreshedToken);
    }

    /**
     * Syncs the profile with the new token to the database.
     *
     * @param token
     *         The refreshed device token.
     */
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
