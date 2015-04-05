package com.lunchdash.lunchdash.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;

import java.util.List;

public class WaitActivity extends ActionBarActivity {
    // Create a handler which can run code periodically
    private Handler handler = new Handler();
    private static final String TAG = "WaitActivity";
    public final int REQUEST_CODE = 100;
    FindMatchTask matchTask;
    User user;
    int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        user = LunchDashApplication.user;
        if (user == null) {
            user = LunchDashApplication.getUserFromSharedPref(this);
        }

        //Get the screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        doAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        matchTask.cancel(true); //Stop the task.
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (!keepAlive) {
            Log.d("APPDEBUG", "Calling this when it's not supposed to...");
            ParseClient.deleteInactiveUserSelections(user.getUserId());
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        matchTask.cancel(true); //Stop the task.
        ParseClient.deleteInactiveUserSelections(user.getUserId());

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
        List<UserRestaurantMatches> matches = ParseClient.getUserMatches(user.getUserId());

        onGetConfirmationRequest();

        for (UserRestaurantMatches match : matches) {
            Log.d("APPDEBUG", "Found a match on" + user.getUserId());
            boolean isActive = ParseClient.isActive(match.getReqUserId(), match.getMatchedUserID(), match.getRestaurantId());
            if (!isActive) { //Make sure the other side hasn't already declined while we were waiting.
                continue;
            }

            Intent acceptDeclineActivityIntent = new Intent(WaitActivity.this, AcceptDeclineActivity.class);
            if (match.getReqUserId().equals(user.getUserId())) {
                acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
            } else {
                acceptDeclineActivityIntent.putExtra("userId", match.getReqUserId());
            }
            acceptDeclineActivityIntent.putExtra("restaurantId", match.getRestaurantId());
            acceptDeclineActivityIntent.putExtra("match", match);

            matchTask.cancel(true);
            // keepAlive = true; //We will prevent Parse cleaning up when WaitActivity goes into onStop
            startActivityForResult(acceptDeclineActivityIntent, REQUEST_CODE);
            // keepAlive = false;
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
        ObjectAnimator moveFork = ObjectAnimator.ofFloat(fork, "translationX", screenWidth / 2 + screenWidth / 20);
        moveFork.setInterpolator(new BounceInterpolator());
        moveFork.setDuration(2000);
        moveFork.setRepeatCount(ValueAnimator.INFINITE); //Loop forever


        ImageView spoon = (ImageView) findViewById(R.id.ivSpoon);
        ObjectAnimator moveSpoon = ObjectAnimator.ofFloat(spoon, "translationX", -screenWidth / 2 + screenWidth / 20);
        moveSpoon.setInterpolator(new BounceInterpolator());
        moveSpoon.setDuration(2000);
        moveSpoon.setRepeatCount(ValueAnimator.INFINITE);

        moveFork.start();
        moveSpoon.start();

    }


    void onGetConfirmationRequest() {

        UserRestaurantMatches usersMatchConfirmation =
                ParseClient.getUserRestaurantMatchAccepted(user.getUserId());

        if (usersMatchConfirmation != null) {
            matchTask.cancel(true);

            Intent contactActivityIntent = new Intent(WaitActivity.this, ContactActivity.class);
            if (usersMatchConfirmation.getReqUserId().equals(user.getUserId())) {
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
