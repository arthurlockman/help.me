package com.wpi.helpme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.wpi.helpme.database.DatabaseProfileWriter;
import com.wpi.helpme.database.DatabaseRequestReader;
import com.wpi.helpme.database.HelpRequest;
import com.wpi.helpme.profile.UserProfile;

import java.util.ArrayList;
import java.util.List;

//Class take and modified from online google tutorial / demo: https://github.com/googlemaps/android-samples/tree/master/tutorials/CurrentPlaceDetailsOnMap

public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        LocationListener {

    private static final String TAG = LocationActivity.class.getSimpleName();
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int RC_LOGIN = 9001;
    private static final List<HelpRequest> helpRequests = new ArrayList<>();

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mApiClient;
    private FirebaseAuth mFAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_location);
        // Build the Play services client for use by the Fused Location Provider and the Places API.

        this.setUpAuthentication();
        mApiClient.connect();

        // Do sign in process in case user is not logged in
        this.doSignInProcess();
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
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
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
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getDeviceLocation();
                        // Build the map.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(LocationActivity.this);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Play services connection suspended");
                    }
                })
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gsio)
                .build();

        createLocationRequest();

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

        // Loads profile to application memory if logged in
        if (this.checkAlreadyLoggedIn()) {
            FirebaseUser user = mFAuth.getCurrentUser();
            loadProfile(user.getUid(), user.getEmail(), user.getDisplayName());
        }
    }

    /**
     * Completes the sign in process for Google authentication.
     */
    private void doSignInProcess() {
        // If user exists, do nothing
        // If no user, start sign in process
        FirebaseUser user = mFAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.already_signed_in_toast) + " " +
                            user.getDisplayName(),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
            startActivityForResult(signInIntent, RC_LOGIN);
        }
    }

    /**
     * Gets the current location of the device and starts the location update notifications.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient,
                    mLocationRequest, this);
        }
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
         * Sets the fastest rate for active location updates. This interval is exact, and your
         * application will never receive updates faster than this value.
         */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Checks if the user is already logged in, and retrieves their user profile.
     */
    private boolean checkAlreadyLoggedIn() {
        return mFAuth.getCurrentUser() != null;
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
            // Edit profile filters
            case R.id.settings_menu_edit_filter:
                try {
                    // Try to get filters from profile, if null it will throw exception
                    HelpMeApplication.getInstance().getUserProfile()
                            .getFilters();

                    // Start editor activity
                    Intent openFilterEditor = new Intent(this, EditFiltersActivity.class);
                    startActivity(openFilterEditor);
                    return true;
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.user_not_logged_in),
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User profile not loaded.");
                    return false;
                }
                // Sign in
            case R.id.settings_menu_sign_in:
                this.doSignInProcess();
                return true;
            // Sign out
            case R.id.settings_menu_sign_out:
                this.doSignOutProcess();
                return true;
            // Refresh map markers
            case R.id.settings_menu_refresh_map:
                Toast.makeText(getApplicationContext(), getString(R.string.refreshing_data_now),
                        Toast.LENGTH_SHORT).show();
                this.refreshMarkerData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Completes the sign out process for Google authentication.
     */
    private void doSignOutProcess() {
        // If no user, do nothing
        // If user exists, start sign out process
        if (mFAuth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), getString(R.string.signed_out_toast),
                    Toast.LENGTH_SHORT)
                    .show();
            signOutGoogleAccount();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.already_signed_out_toast),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Refreshes the markers by reloading new help requests from the database.
     */
    private void refreshMarkerData() {
        DatabaseRequestReader
                .readRequestsFromDatabase(HelpMeApplication.getInstance().getDatabaseReference(),
                        new Runnable() {
                            @Override
                            public void run() {
                                helpRequests.clear();
                                helpRequests.addAll(HelpMeApplication.getInstance().getRequests());
                                updateMarkers();
                                Log.d(TAG, "Retrieved new requests and updated markers.");
                            }
                        });
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
                Log.d(TAG, getString(R.string.signed_out_toast));
                mDatabaseRef.onDisconnect();
            }
        });

        this.clearCurrentUserLogin();
    }

    /**
     * Adds markers for places nearby the device and turns the My Location feature on or off,
     * provided location permission has been granted.
     */
    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        // Clear map of markers
        mMap.clear();

        if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
//            @SuppressWarnings("MissingPermission")
//            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
//                    .getCurrentPlace(mApiClient, null);
//            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                @Override
//                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
//                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        // Add a marker for each place near the device's current location, with an
//                        // info window showing place information.
//                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
//                        String snippet = (String) placeLikelihood.getPlace().getAddress();
//                        if (attributions != null) {
//                            snippet = snippet + "\n" + attributions;
//                        }
//
//                        mMap.addMarker(new MarkerOptions()
//                                .position(placeLikelihood.getPlace().getLatLng())
//                                .title((String) placeLikelihood.getPlace().getName())
//                                .snippet(snippet));
//                    }
//                    // Release the place likelihood buffer.
//                    likelyPlaces.release();
//                }
//            });

            // Add each request and its location to the map
            for (HelpRequest req : helpRequests) {
                LatLng loc = new LatLng(req.getLatitude(), req.getLongitude());
                mMap.addMarker(new MarkerOptions().position(loc).title(req.getTitleText())
                        .snippet(req.getBodyText()));
            }
        } else {
            // Add default location
            mMap.addMarker(new MarkerOptions()
                    .position(mDefaultLocation)
                    .title("title")
                    .snippet("info"));
        }
    }

    /**
     * Clears the current user session. Clears the user profile and the map markers.
     */
    private void clearCurrentUserLogin() {
        this.helpRequests.clear();
        HelpMeApplication.getInstance().clearUserProfile();

        this.updateMarkers();
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

                                    // Load profile
                                    loadProfile(user.getUid(),
                                            user.getEmail(), user.getDisplayName());

                                    // Update map markers
                                    refreshMarkerData();
                                }
                            }
                        });
            }
        }
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery
     * consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mApiClient, this);
        }
    }

    /**
     * Get the device location and nearby places when the activity is restored after a pause.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mApiClient.isConnected()) {
            getDeviceLocation();
        }
        updateMarkers();
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    /**
     * Launches the help request activity.
     *
     * @param v
     *         The {@link View} associated with this callback.
     */
    public void openRequestActivity(View v) {
        // If there is no user, do not launch request activity
        try {
            HelpMeApplication.getInstance().getUserProfile()
                    .getFilters();
            Intent getLocation = new Intent(this, RequestMain.class);
            startActivity(getLocation);
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_not_logged_in),
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User profile not loaded.");
        }
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMarkers();
    }

    /**
     * Manipulates the map when it's available. This callback is triggered when the map is ready to
     * be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Add markers for nearby places.
        updateMarkers();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            /**
             * @see {@link com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoWindow(Marker)}
             */
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            /**
             * @see {@link com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoContents(Marker)}
             */
            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}
