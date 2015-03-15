package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;


public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
        if (location != null) {
            LunchDashApplication.latitude = location.getLatitude() + "";
            LunchDashApplication.longitude = location.getLongitude() + "";
            Log.d("APPDEBUG", "This device's Latitude/Longitude is: " + LunchDashApplication.latitude + "," + LunchDashApplication.longitude);
        } else {
            Log.d("APPDEBUG", "Location is null");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
