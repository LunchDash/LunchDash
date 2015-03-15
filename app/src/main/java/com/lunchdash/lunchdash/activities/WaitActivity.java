package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;

import java.util.List;

public class WaitActivity extends Activity {

    private static final String TAG = "WaitActivity";

    private static int WAIT_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              finish();
            }
        }, WAIT_TIME_OUT);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        handleRequests();
    }

    void handleRequests() {

        List<UserRestaurantMatches> matches = ParseClient.getUserMatches(LunchDashApplication.user.getUserId());

        Toast.makeText(this, "Found #" + matches.size(), Toast.LENGTH_SHORT).show();

        Log.i(TAG, "handleRequests:: Found #" + matches.size());

        for (UserRestaurantMatches match : matches) {

            Intent acceptDeclineActivityIntent = new Intent(WaitActivity.this, AcceptDeclineActivity.class);
            acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
            acceptDeclineActivityIntent.putExtra("restaurantId",match.getRestaurantId());
            startActivity(acceptDeclineActivityIntent);

            //Always check if we have a match already.. before showing the next one
            onGetConfirmationRequest();

        }
        onGetConfirmationRequest();
    }

    void onGetConfirmationRequest() {


        UserRestaurantMatches usersMatchConfirmation =
                ParseClient.getUserRestaurantMatchAccepted(LunchDashApplication.user.getUserId());

        if (usersMatchConfirmation != null) {
            Log.i(TAG, "onGetConfirmationRequest");
            Intent contactActivityIntent = new Intent(WaitActivity.this, ContactActivity.class);
            contactActivityIntent.putExtra("userId", usersMatchConfirmation.getMatchedUserID());
            contactActivityIntent.putExtra("restaurantId", usersMatchConfirmation.getRestaurantId());
            startActivity(contactActivityIntent);
            finish();
        }
    }
}
