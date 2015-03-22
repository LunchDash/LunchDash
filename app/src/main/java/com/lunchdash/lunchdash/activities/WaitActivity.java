package com.lunchdash.lunchdash.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;

import java.util.List;

public class WaitActivity extends ActionBarActivity {
    // Create a handler which can run code periodically
    private Handler handler = new Handler();
    private static final String TAG = "WaitActivity";
    public final int REQUEST_CODE = 100;
    FindMatchTask matchTask;
    int loopCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        doAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("APPDEBUG", "pause called");
        matchTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("APPDEBUG", "Destroy called");
        matchTask.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("APPDEBUG", "resume called");

        matchTask = new FindMatchTask();
        matchTask.execute(null, null, null); //Restart the async task when we return from the AcceptDeclineActivity
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        if (resultCode == Activity.RESULT_OK) {
            Log.d("APPDEBUG", "successfully did acceptdeclineactivity");
            //onGetConfirmationRequest();
        }

    }

    void handleRequests() {
        loopCount++;
        Log.d("APPDEBUG", "loop count is: " + loopCount);
        List<UserRestaurantMatches> matches = ParseClient.getUserMatches(LunchDashApplication.user.getUserId());

        //Toast.makeText(this, "Found #" + matches.size(), Toast.LENGTH_SHORT).show();
        // Log.i(TAG, "handleRequests:: Found #" + matches.size());

        onGetConfirmationRequest();

        for (UserRestaurantMatches match : matches) {
            Log.d("APPDEBUG", "Found a match on" + LunchDashApplication.user.getUserId());

            Intent acceptDeclineActivityIntent = new Intent(WaitActivity.this, AcceptDeclineActivity.class);
            if (match.getReqUserId().equals(LunchDashApplication.user.getUserId())) {
                acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
            } else {
                acceptDeclineActivityIntent.putExtra("userId", match.getReqUserId());
            }
            acceptDeclineActivityIntent.putExtra("restaurantId", match.getRestaurantId());
            acceptDeclineActivityIntent.putExtra("match", match);

            matchTask.cancel(true);
            startActivityForResult(acceptDeclineActivityIntent, REQUEST_CODE);

            onGetConfirmationRequest();

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    void doAnimation() {

        ImageView fork = (ImageView) findViewById(R.id.ivFork);
        ObjectAnimator moveFork = ObjectAnimator.ofFloat(fork, "translationX", 315);
        moveFork.setInterpolator(new BounceInterpolator());
        moveFork.setDuration(2000);
        moveFork.setRepeatCount(ValueAnimator.INFINITE); //Loop forever
        moveFork.start();

        ImageView spoon = (ImageView) findViewById(R.id.ivSpoon);
        ObjectAnimator moveSpoon = ObjectAnimator.ofFloat(spoon, "translationX", -215);
        moveSpoon.setInterpolator(new BounceInterpolator());
        moveSpoon.setDuration(2000);
        moveSpoon.setRepeatCount(ValueAnimator.INFINITE);
        moveSpoon.start();

    }


    void onGetConfirmationRequest() {

        UserRestaurantMatches usersMatchConfirmation =
                ParseClient.getUserRestaurantMatchAccepted(LunchDashApplication.user.getUserId());

        if (usersMatchConfirmation != null) {
            matchTask.cancel(true);

            Intent contactActivityIntent = new Intent(WaitActivity.this, ContactActivity.class);
            if (usersMatchConfirmation.getReqUserId().equals(LunchDashApplication.user.getUserId())) {
                contactActivityIntent.putExtra("userId", usersMatchConfirmation.getMatchedUserID());
            } else {
                contactActivityIntent.putExtra("userId", usersMatchConfirmation.getReqUserId());
            }

            contactActivityIntent.putExtra("restaurantId", usersMatchConfirmation.getRestaurantId());
            contactActivityIntent.putExtra("match", usersMatchConfirmation);
            startActivity(contactActivityIntent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
    }

    private class FindMatchTask extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... params) {
            while (!matchTask.isCancelled()) {
                handleRequests();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
