package com.lunchdash.lunchdash.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class WaitActivity extends ActionBarActivity {
    User user;
    int screenWidth;
    boolean animationStarted = false;
    TriggerTask triggerTask;

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

        triggerTask = new TriggerTask();
        triggerTask.execute(null, null, null); //Run code to trigger the push notif search on parse cloud.

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!animationStarted) {
            doAnimation();
            animationStarted = true;
        }
    }

    @Override
    public void onBackPressed() {
        ParseClient.clearUserRestaurantSelections(user.getUserId()); //Get rid of all their restaurant selections.
        ParseClient.setUserStatus("None"); //Set their status to "None".  This indicates that when the app is started, it shouldn't go straight to WaitActivity.
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }

    @Override
    protected void onStop() {
        super.onStop();
        triggerTask.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (triggerTask.isCancelled()) { //When we resume, run the async task again.
            triggerTask = new TriggerTask();
            triggerTask.execute(null, null, null); //Run code to trigger the push notif search on parse cloud.
        }
    }

    void doAnimation() {

        ImageView fork = (ImageView) findViewById(R.id.ivFork);

        int forkWidth = fork.getWidth();
        float forkTranslationDistance = screenWidth / 2 + 2 * forkWidth;
        ObjectAnimator moveForkIn = ObjectAnimator.ofFloat(fork, "translationX", 0, forkTranslationDistance);
        moveForkIn.setInterpolator(new BounceInterpolator());
        moveForkIn.setDuration(2000);

        ObjectAnimator moveForkBack = ObjectAnimator.ofFloat(fork, "translationX", forkTranslationDistance, 0);
        moveForkBack.setInterpolator(new AccelerateInterpolator());
        moveForkBack.setDuration(1000);

        ImageView spoon = (ImageView) findViewById(R.id.ivSpoon);
        float spoonTranslationDistance = -screenWidth / 2 - forkWidth;
        ObjectAnimator moveSpoonIn = ObjectAnimator.ofFloat(spoon, "translationX", 0, spoonTranslationDistance);
        moveSpoonIn.setInterpolator(new BounceInterpolator());
        moveSpoonIn.setDuration(2000);

        ObjectAnimator moveSpoonBack = ObjectAnimator.ofFloat(spoon, "translationX", spoonTranslationDistance, 0);
        moveSpoonBack.setInterpolator(new AccelerateInterpolator());
        moveSpoonBack.setDuration(1000);

        final AnimatorSet asFork = new AnimatorSet();
        asFork.playSequentially(moveForkIn, moveForkBack);
        asFork.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                asFork.start();
            }
        });

        final AnimatorSet asSpoon = new AnimatorSet();
        asSpoon.playSequentially(moveSpoonIn, moveSpoonBack);
        asSpoon.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                asSpoon.start();
            }
        });

        asFork.start();
        asSpoon.start();

    }

    private class TriggerTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            while (true) {
                ParseCloud.callFunctionInBackground("triggerMatchPushNotify", new HashMap<String, Object>(), new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        Log.d("APPDEBUG", "match job triggered " + e);
                    }
                });
                ParseCloud.callFunctionInBackground("triggerChatPushNotify", new HashMap<String, Object>(), new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        Log.d("APPDEBUG", "chat job triggered " + e);
                    }
                });
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
