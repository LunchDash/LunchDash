package com.lunchdash.lunchdash.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.ChatListAdapter;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    ImageView ivUserImage;
    TextView tvContactText;
    EditText etMessage;
    Button btSend;
    UserRestaurantMatches match;
    Restaurant restaurant;

    Marker restaurantMarker;
    Marker otherUserMarker;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();


    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    // Create a handler which can run code periodically
    private Handler handler = new Handler();


    public static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private ListView lvChat;
    private ArrayList<ChatMessageTable> mMessages;
    private ChatListAdapter mAdapter;





    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");
        match = (UserRestaurantMatches) i.getSerializableExtra("match");

        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        tvContactText = (TextView) findViewById(R.id.tvContactText);
        User user = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(restaurantId);

        tvContactText.setText(user.getName() + " is ready for an awesome lunch at " + restaurant.getName() + "!\nGet in touch with them!");
        ivUserImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view

        Picasso.with(this)
                .load(user.getImageUrl())
                .into((ivUserImage));
        setupMessagePosting();
        handler.postDelayed(runnable, 100);


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mvContactMap));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


    }


    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);

            restaurantMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
                    .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_shop))
            );
            builder.include(restaurantMarker.getPosition());

            String otherUserId = match.getReqUserId().equals(LunchDashApplication.user.getUserId())?match.getMatchedUserID():match.getReqUserId();
            User otherUser = ParseClient.getUser(otherUserId);

            otherUserMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            Double.parseDouble(otherUser.getCurrentLat()),
                            Double.parseDouble(otherUser.getCurrentLon())
                            )
                    )
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_walker))
            );

            builder.include(otherUserMarker.getPosition());


            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }


    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

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
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            builder.include(latLng);
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            //Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
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
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


        //query other users location and udpate the marker too.
        String otherUserId = match.getReqUserId().equals(LunchDashApplication.user.getUserId())?match.getMatchedUserID():match.getReqUserId();
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
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void onCallClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0123456789"));
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void onEmailClick(View v) {
        Intent mailClient = new Intent(Intent.ACTION_VIEW);
        mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        mailClient.setType("plain/text");
        mailClient.putExtra(Intent.EXTRA_EMAIL, LunchDashApplication.user.getEmail());
        mailClient.putExtra(Intent.EXTRA_SUBJECT, "LunchDash Meetup at" + restaurant.getName());

        startActivity(mailClient);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);

        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<ChatMessageTable>();
        mAdapter = new ChatListAdapter(this, LunchDashApplication.user.getUserId(), mMessages);
        lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                if (data.equals("")) {
                    return;
                }
                ParseClient.saveChatMessage(match.getId(), LunchDashApplication.user.getUserId(), data);
                etMessage.setText("");
            }
        });
    }


    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        List<ChatMessageTable> messages = ParseClient.getChatMessages(match.getId());
        if (messages != null){
            mMessages.clear();
            mMessages.addAll(messages);
            mAdapter.notifyDataSetChanged();
            lvChat.invalidate();
        } else {
            Log.d("message", "Error: Could not get messages ");
        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
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
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}
