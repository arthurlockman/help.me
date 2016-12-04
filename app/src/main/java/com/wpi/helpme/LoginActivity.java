package com.wpi.helpme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final int RC_LOGIN = 9001;

    private GoogleApiClient mApiClient;
    private FirebaseAuth mFAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setUpAuthentication();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mFAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),
                            "Already signed in as " + user.getDisplayName(), Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Signing in with Google",
                            Toast.LENGTH_LONG)
                            .show();

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
                    startActivityForResult(signInIntent, RC_LOGIN);
                }
            }
        });

        SignInButton signOutButton = (SignInButton) findViewById(R.id.sign_out_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG)
                            .show();
                    signOutGoogleAccount();
                } else {
                    Toast.makeText(getApplicationContext(), "Already signed out", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        for (int i = 0; i < signOutButton.getChildCount(); i++) {
            View v = signOutButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign out");
                return;
            }
        }
    }

    protected void setUpAuthentication() {
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Google API Client connection failed.");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gsio)
                .build();

        firebase = FirebaseDatabase.getInstance().getReference();
        mFAuth = FirebaseAuth.getInstance();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider
                        .getCredential(acct.getIdToken(), null);
                mFAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "signInWithCredential", task.getException());
                                } else {
                                    firebase = FirebaseDatabase.getInstance().getReference();
                                    Toast.makeText(getApplicationContext(),
                                            "Firebase database connected", Toast.LENGTH_LONG);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signOutGoogleAccount() {
        Auth.GoogleSignInApi.signOut(mApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d(TAG, "Signed out");
                firebase.onDisconnect();
                mFAuth.signOut();
            }
        });
    }
}
