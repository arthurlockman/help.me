package com.wpi.helpme.com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class DatabaseProfileWriter {
    private static final String PROFILES_DIR = "profiles/";
    private static final String TAG = "DatabaseProfileWriter";
    private static DatabaseProfileWriter instance;

    private DatabaseProfileWriter() {
    }

    public static DatabaseProfileWriter getInstance() {
        if (instance == null) {
            instance = new DatabaseProfileWriter();
        }
        return instance;
    }

    public void onProfileExists(DatabaseReference db, final UserProfile profile, final boolean runIfExists, final Runnable function) {
        final DatabaseReference dbRef = db.child(PROFILES_DIR + profile.getUserId());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    if (!runIfExists) {
                        Log.d(TAG, "Profile " + profile.getUserId() + " does not exist. Running callback...");
                        function.run();
                    } else {
                        Log.d(TAG, "Profile " + profile.getUserId() + " does not exist. Skipping callback...");
                    }
                } else {
                    if (runIfExists) {
                        Log.d(TAG, "Profile " + profile.getUserId() + " exists. Running callback...");
                        function.run();
                    } else {
                        Log.d(TAG, "Profile " + profile.getUserId() + " exists. Skipping callback...");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "profile-ValueEventListener:onCancelled:" + databaseError);
            }
        });
    }

    public void writeProfile(DatabaseReference db, final UserProfile profile) {
        DatabaseReference dbRef = db.child(PROFILES_DIR + profile.getUserId());
        dbRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserProfile p = mutableData.getValue(UserProfile.class);
                mutableData.setValue(profile);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "profile-Transaction:onComplete:" + databaseError);
            }
        });
    }
}
