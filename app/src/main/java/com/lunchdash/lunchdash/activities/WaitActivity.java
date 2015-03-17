package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;

import java.util.List;

public class WaitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        handler.postDelayed(runnable, INITIAL_POLLING_INTERVAL);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //RestartTheThreadAgain
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        if (resultCode == Activity.RESULT_OK) {
            //onGetConfirmationRequest();
        }

    }

    void handleRequests() {

        List<UserRestaurantMatches> matches = ParseClient.getUserMatches(LunchDashApplication.user.getUserId());

        //Toast.makeText(this, "Found #" + matches.size(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "handleRequests:: Found #" + matches.size());

        onGetConfirmationRequest();

        for (UserRestaurantMatches match : matches) {

            //PauseTheThreadHere();

            Intent acceptDeclineActivityIntent = new Intent(WaitActivity.this, AcceptDeclineActivity.class);
            if (match.getReqUserId() == LunchDashApplication.user.getUserId()) {
                acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
            } else {
                acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
            }
            acceptDeclineActivityIntent.putExtra("restaurantId", match.getRestaurantId());
            acceptDeclineActivityIntent.putExtra("match", match);
            startActivityForResult(acceptDeclineActivityIntent, REQUEST_CODE);

            onGetConfirmationRequest();

        }

    }


    void onGetConfirmationRequest() {

        UserRestaurantMatches usersMatchConfirmation =
            ParseClient.getUserRestaurantMatchAccepted(LunchDashApplication.user.getUserId());

        if (usersMatchConfirmation != null) {
            Log.i(TAG, "onGetConfirmationRequest");

            //StopTheThreadHere();

            Intent contactActivityIntent = new Intent(WaitActivity.this, ContactActivity.class);
            if (usersMatchConfirmation.getReqUserId() == LunchDashApplication.user.getUserId()) {
                contactActivityIntent.putExtra("userId", usersMatchConfirmation.getMatchedUserID());
            } else {
                contactActivityIntent.putExtra("userId", usersMatchConfirmation.getReqUserId());
            }

            contactActivityIntent.putExtra("restaurantId", usersMatchConfirmation.getRestaurantId());
            startActivity(contactActivityIntent);

            finish();
        }
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handleRequests();
            handler.postDelayed(this, POLLING_INTERVAL);
        }


    };

    // Create a handler which can run code periodically
    private Handler handler = new Handler();
    private static final String TAG = "WaitActivity";
    public final int REQUEST_CODE = 100;
    private static int POLLING_INTERVAL = 20000;
    private static int INITIAL_POLLING_INTERVAL = 200;
}
