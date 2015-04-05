package com.lunchdash.lunchdash.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.OnSwipeTouchListener;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AcceptDeclineActivity extends ActionBarActivity {
    ImageView ivProfileImg;
    TextView tvMessage;
    User matchedUser;
    Restaurant restaurant;
    UserRestaurantMatches match;
    int screenWidth;
    ObjectAnimator moveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_decline);

        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");
        match = (UserRestaurantMatches) i.getSerializableExtra("match");

        matchedUser = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(restaurantId);

        //Get the screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImage);
        View swipeHitbox = findViewById(R.id.swipeHitbox);
        swipeHitbox.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                hideButtons();
                animateProfile("left");
            }

            @Override
            public void onSwipeRight() {
                hideButtons();
                animateProfile("right");
            }
        });

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        tvMessage.setText(matchedUser.getName() + " would like to go to lunch with you at " + restaurant.getName());
        ivProfileImg.setImageResource(android.R.color.transparent);
        Transformation transformation = new RoundedTransformationBuilder().borderColor(Color.BLACK).borderWidthDp(1).oval(true).scaleType(ImageView.ScaleType.CENTER_CROP).build();
        Picasso.with(this).load(matchedUser.getImageUrl()).transform(transformation).into((ivProfileImg));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void animateProfile(String direction) {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        if (direction.equals("left")) {
            moveProfile = ObjectAnimator.ofFloat(ivProfileImage, "translationX", -screenWidth);
            moveProfile.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) { //Wait for animation to finish before continuing.
                    super.onAnimationEnd(animation);
                    decline();
                }
            });
        } else {
            moveProfile = ObjectAnimator.ofFloat(ivProfileImage, "translationX", screenWidth);
            Log.d("APPDEBUG", "screen width is: " + screenWidth);
            moveProfile.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    accept();
                }
            });
        }
        moveProfile.setInterpolator(new AccelerateInterpolator());
        moveProfile.setDuration(500);
        moveProfile.start();
    }

    public void hideButtons() {
        LinearLayout llButtons = (LinearLayout) findViewById(R.id.llButtons);
        llButtons.setVisibility(View.INVISIBLE);
    }


    private void accept() {
        String userMatchResponse = UserRestaurantMatches.STATUS_ACCEPTED;

        if (LunchDashApplication.user.getUserId().equals(match.getReqUserId())) {
            match.setReqStatus(UserRestaurantMatches.STATUS_ACCEPTED);
            match.setMatchedStatus(UserRestaurantMatches.STATUS_UNCHANGED);
        } else {
            match.setMatchedStatus(UserRestaurantMatches.STATUS_ACCEPTED);
            match.setReqStatus(UserRestaurantMatches.STATUS_UNCHANGED);
        }

        ParseClient.saveUserRestaurantMatch(match);

        Intent i = new Intent();
        i.putExtra("userMatchResponse", userMatchResponse);
        setResult(Activity.RESULT_OK, i);
        finish();

    }

    public void onAccept(View v) {
        accept();
    }

    public void decline() {
        String userMatchResponse = UserRestaurantMatches.STATUS_DENIED;

        if (LunchDashApplication.user.getUserId().equals(match.getReqUserId())) {
            match.setReqStatus(UserRestaurantMatches.STATUS_DENIED);
        } else {
            match.setMatchedStatus(UserRestaurantMatches.STATUS_DENIED);
        }

        ParseClient.saveUserRestaurantMatch(match);

        Intent i = new Intent();
        i.putExtra("userMatchResponse", userMatchResponse);
        setResult(Activity.RESULT_OK, i);
        finish();

    }

    public void onDecline(View v) {
        decline();
    }
}
