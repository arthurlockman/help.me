package com.wpi.helpme.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadRequestValueEventListener implements ValueEventListener {
    private static final String TAG = "LoadRequestValueEventListener";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private static final String LAT = "lat";
    private static final String LONG = "long";

    public LoadRequestValueEventListener() {
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
            List<Object> objectMap = (ArrayList<Object>) dataSnapshot.getValue();

            for (Object obj : objectMap) {
                if (obj instanceof Map) {
                    Map<String, Object> mapObj = (Map<String, Object>) obj;

                    try {
                        String body = (String) mapObj.get(BODY);
                        String title = (String) mapObj.get(TITLE);
                        double lat = (Double) mapObj.get(LAT);
                        double lon = (Double) mapObj.get(LONG);
                        requests.add(new HelpRequest(title, body, lat, lon));
                    } catch (NullPointerException e) {
                        Log.d(TAG, "Failed to get request attributes of null request.", e);
                    }
                }
            }

            Log.d(TAG, requests.toString());
        }
    }

    /**
     * @see {@link ValueEventListener#onCancelled(DatabaseError)}
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "profile-ValueEventListener:onCancelled:" + databaseError);
    }
}
