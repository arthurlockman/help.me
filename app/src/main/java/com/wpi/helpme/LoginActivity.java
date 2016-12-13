package com.wpi.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final int RC_LOGIN = 9001;

    private GoogleApiClient mApiClient;
    private FirebaseAuth mFAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef;

    /**
     * @see {@link AppCompatActivity#onCreate(Bundle)}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        // Sign in button
//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // If user exists, do nothing
//                // If no user, start sign in process
//                FirebaseUser user = mFAuth.getCurrentUser();
//                if (user != null) {
//                    Toast.makeText(getApplicationContext(),
//                            getString(R.string.already_signed_in_toast) + " " +
//                                    user.getDisplayName(),
//                            Toast.LENGTH_SHORT)
//                            .show();
//                } else {
//                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
//                    startActivityForResult(signInIntent, RC_LOGIN);
//                }
//            }
//        });
//
//        // Sign out button
//        SignInButton signOutButton = (SignInButton) findViewById(R.id.sign_out_button);
//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // If no user, do nothing
//                // If user exists, start sign out process
//                if (mFAuth.getCurrentUser() != null) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.signed_out_toast),
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    //signOutGoogleAccount();
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            getString(R.string.already_signed_out_toast),
//                            Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        // Modify com.google.android.gms.common.SignInButton to say "Sign out"
//        // Referenced from http://stackoverflow.com/questions/18040815/can-i-edit-the-text-of-sign-in-button-on-google
//        for (int i = 0; i < signOutButton.getChildCount(); i++) {
//            View v = signOutButton.getChildAt(i);
//
//            if (v instanceof TextView) {
//                TextView tv = (TextView) v;
//                tv.setText(getString(R.string.signed_out_toast));
//                return;
//            }
//        }
    }


    /**
     * Launches the location activity.
     *
     * @param v
     *         The {@link View} associated with this callback.
     */
    public void openLocationActivity(View v) {
        Intent getLocation = new Intent(this, LocationActivity.class);
        startActivity(getLocation);
    }


}
