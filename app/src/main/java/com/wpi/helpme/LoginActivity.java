package com.wpi.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.wpi.helpme.database.DatabaseProfileWriter;
import com.wpi.helpme.profile.UserProfile;

import java.util.List;

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

        // Create Firebase objects
        this.setUpAuthentication();

        // Sign in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If user exists, do nothing
                // If no user, start sign in process
                FirebaseUser user = mFAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_already_signed_in) + user.getDisplayName(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
                    startActivityForResult(signInIntent, RC_LOGIN);
                }
            }
        });

        // Sign out button
        SignInButton signOutButton = (SignInButton) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If no user, do nothing
                // If user exists, start sign out process
                if (mFAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_sign_out),
                            Toast.LENGTH_SHORT)
                            .show();
                    signOutGoogleAccount();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_already_signed_out),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Modify com.google.android.gms.common.SignInButton to say "Sign out"
        // Referenced from http://stackoverflow.com/questions/18040815/can-i-edit-the-text-of-sign-in-button-on-google
        for (int i = 0; i < signOutButton.getChildCount(); i++) {
            View v = signOutButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(getString(R.string.login_sign_out));
                return;
            }
        }
    }

    /**
     * Sets up the Google sign in authentication objects.
     *
     * @see <a href="https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java">Quickstart
     * Example</a>
     */
    protected void setUpAuthentication() {
        // Google sign in builder
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Google client
        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Google API Client connection failed.");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gsio)
                .build();

        // Get Firebase reference
        mDatabaseRef = HelpMeApplication.getInstance().getDatabaseReference();
        mFAuth = FirebaseAuth.getInstance();

        // Listener for Firebase state changes
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        this.checkAlreadyLoggedIn();
    }

    /**
     * Signs the current user out of the application with their Google account.
     */
    private void signOutGoogleAccount() {
        // Firebase sign out
        mFAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d(TAG, getString(R.string.login_signed_out));
                mDatabaseRef.onDisconnect();
            }
        });
    }

    /**
     * Checks if the user is already logged in, and retrieves their user profile.
     */
    private void checkAlreadyLoggedIn() {
        FirebaseUser user = mFAuth.getCurrentUser();
        if (user != null) {
            // Loads profile to application memory
            loadProfile(user.getUid(), user.getEmail(), user.getDisplayName());
        }
    }

    /**
     * Writes the user profile on log in if it does not already exist in the database.
     *
     * @param userId
     *         The unique user ID.
     * @param email
     *         The email address.
     * @param userName
     *         The display user name.
     */
    private void loadProfile(String userId, String email, String userName) {
        DatabaseProfileWriter
                .loadDatabaseProfile(HelpMeApplication.getInstance().getDatabaseReference(),
                        new UserProfile(userId, email, userName));
    }

    /**
     * @see {@link AppCompatActivity#onStart()}
     */
    @Override
    public void onStart() {
        super.onStart();
        mFAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * @see {@link AppCompatActivity#onStop()}
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * @see {@link AppCompatActivity#onCreateOptionsMenu(Menu)}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    /**
     * @see {@link AppCompatActivity#onOptionsItemSelected(MenuItem)}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
                try {
                    // Try to get filters from profile, if null it will throw exception
                    List<String> filters = HelpMeApplication.getInstance().getUserProfile()
                            .getFilters();

                    // Start editor activity
                    Intent openFilterEditor = new Intent(this, EditFiltersActivity.class);
                    startActivity(openFilterEditor);
                    return true;
                } catch (NullPointerException e) {
                    Log.d(TAG, "User profile not loaded.");
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @see {@link AppCompatActivity#onActivityResult(int, int, Intent)}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check login result
        if (requestCode == RC_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            // Successful result
            if (result.isSuccess()) {
                // Get credentials
                GoogleSignInAccount acct = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider
                        .getCredential(acct.getIdToken(), null);
                mFAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Log if successful or not
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "signInWithCredential:", task.getException());
                                } else {
                                    Log.d(TAG, "signInWithCredential:successful");
                                    FirebaseUser user = mFAuth.getCurrentUser();
                                    loadProfile(user.getUid(),
                                            user.getEmail(), user.getDisplayName());
                                }
                            }
                        });
            }
        }
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

    /**
     * Launches the help request activity.
     *
     * @param v
     *         The {@link View} associated with this callback.
     */
    public void openRequestActivity(View v) {
        Intent getLocation = new Intent(this, RequestMain.class);
        startActivity(getLocation);
    }
}
