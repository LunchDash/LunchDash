package com.lunchdash.lunchdash.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;


public class SplashActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static int SPLASH_TIME_OUT = 500;
    Location location;
    GoogleApiClient gApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        buildGoogleApiClient();

    }

    @Override
    protected void onStart() { //Connect to the Google Api Client (we use this to find the device's longitude/latitude)
        super.onStart();
        gApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        gApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(gApiClient);
        if (location != null) { //Grab their lat/long and continue.
            LunchDashApplication.latitude = location.getLatitude() + "";
            LunchDashApplication.longitude = location.getLongitude() + "";
            Log.d("APPDEBUG", "This device's Latitude/Longitude is: " + LunchDashApplication.latitude + "," + LunchDashApplication.longitude);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else { //We couldn't find their long/lat probably because their GPS is disabled.  Show an error message and exit out.
            Log.d("APPDEBUG", "Location is null");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Could not get your current location.  Make sure your GPS is enabled!").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
