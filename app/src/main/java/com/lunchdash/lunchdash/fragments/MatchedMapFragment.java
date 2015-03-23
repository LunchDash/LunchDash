package com.lunchdash.lunchdash.fragments;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.ContactActivity;
import com.lunchdash.lunchdash.adapters.UserWindowAdapter;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;

public class MatchedMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    Marker restaurantMarker;
    Marker otherUserMarker;
    public static String otherUserMarkerId;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        MapView mapView = (MapView) v.findViewById(R.id.restaurantMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity().getApplicationContext());
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new UserWindowAdapter(inflater));

        loadMap(map);

        return v;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Log.d("APPDEBUG", "Map Fragment was loaded properly!");
            map.setMyLocationEnabled(true);
            Restaurant restaurant = ContactActivity.restaurant;
            restaurantMarker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_shop))
            );
            builder.include(restaurantMarker.getPosition());

            UserRestaurantMatches match = ContactActivity.match;
            String otherUserId = match.getReqUserId().equals(LunchDashApplication.user.getUserId()) ? match.getMatchedUserID() : match.getReqUserId();
            User otherUser = ParseClient.getUser(otherUserId);

            otherUserMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(
                                    Double.parseDouble(otherUser.getCurrentLat()),
                                    Double.parseDouble(otherUser.getCurrentLon())
                            )
                    ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_walker)));
            otherUserMarkerId = otherUserMarker.getId();
            builder.include(otherUserMarker.getPosition());

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            Log.d("APPDEBUG", "Error - Map was null!!");
        }
    }

    protected void connectClient() {
        Log.d("APPDEBUG", "Calling Connect Client");
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            return false;
        }
    }

    /*
 * Called by Location Services when the request to connect the client
 * finishes successfully. At this point, you can request the current
 * location or start periodic updates
 */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Log.d("APPDEBUG", "Map Fragment was loaded properly!");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            builder.include(latLng);
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Log.d("Contact", "Current location was null, enable GPS on emulator!");
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        Log.d("APPDEBUG", "Calling on Location changed");
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


        //query other users location and udpate the marker too.
        UserRestaurantMatches match = ContactActivity.match;
        String otherUserId = match.getReqUserId().equals(LunchDashApplication.user.getUserId()) ? match.getMatchedUserID() : match.getReqUserId();
        User otherUser = ParseClient.getUser(otherUserId);
        otherUserMarker.setPosition(new LatLng(
                Double.parseDouble(otherUser.getCurrentLat()),
                Double.parseDouble(otherUser.getCurrentLon())
        ));

        //Also save the current location update to parse.
        LunchDashApplication.user.setCurrentLat(Double.toString(location.getLatitude()));
        LunchDashApplication.user.setCurrentLon(Double.toString(location.getLongitude()));
        ParseClient.saveUser(LunchDashApplication.user);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("APPDEBUG", "Calling on Connection Suspended");
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Log.d("APPDEBUG", "Disconnected. Please re-connect.");
        } else if (i == CAUSE_NETWORK_LOST) {
            Log.d("APPDEBUG", "Network lost. Please re-connect.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        Log.d("APPDEBUG", "Calling on Connection Failed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Log.d("APPDEBUG", "Sorry. Location services not available to you");
        }
    }
}
