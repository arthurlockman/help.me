package com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wpi.helpme.HelpMeApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the callback listener that executes when the requests are loaded from the
 * database. The list of requests is saved into the high level application.
 */
public class LoadRequestValueEventListener implements ValueEventListener {
    private static final String TAG = "LoadRequestValueEventListener";
    private Runnable run;

    /**
     * Creates a LoadRequestValueEventListener instance.
     *
     * @param runnable
     *         The {@link Runnable} to execute once the requests are loaded.
     */
    public LoadRequestValueEventListener(Runnable runnable) {
        run = runnable;
    }

    /**
     * @see {@link ValueEventListener#onDataChange(DataSnapshot)}
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "Null value");
        } else {
            List<HelpRequest> requests = new ArrayList<>();

            // Get list of objects in requests
            for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                HelpRequest req = requestSnapshot.getValue(HelpRequest.class);
                try {
                    Log.d(TAG, req.toString());
                    requests.add(req);
                } catch (NullPointerException e) {
                    Log.d(TAG, "Failed to get request attributes of null request.", e);
                }
            }


            // Update application with request list.
            HelpMeApplication.getInstance().updateRequests(requests);
        }

        // Run custom runnable
        run.run();
    }

    /**
     * @see {@link ValueEventListener#onCancelled(DatabaseError)}
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "profile-ValueEventListener:onCancelled:" + databaseError);
    }
}
