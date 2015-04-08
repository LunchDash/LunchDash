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
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.HashMap;

public class AcceptDeclineActivity extends ActionBarActivity {
    ImageView ivProfileImg;
    TextView tvMessage;
    User matchedUser;
    Restaurant restaurant;
    UserRestaurantMatches match;
    int screenWidth;
    ObjectAnimator moveProfile;
    User user;
    String snippet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_decline);

        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");
        match = (UserRestaurantMatches) i.getSerializableExtra("match");

        matchedUser = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(this, restaurantId);

        HashMap<String, String> profile = ParseClient.getUserProfile(LunchDashApplication.user.getUserId());
        if (profile != null) {
            snippet = profile.get("snippet");
        }

        user = LunchDashApplication.user;
        if (user == null) {
            user = LunchDashApplication.getUserFromSharedPref(this);
        }


        //Get the screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        setupViews();

    }

    private void setupViews() {
        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImage);
        View swipeHitbox = findViewById(R.id.swipeHitbox);
        swipeHitbox.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                animateProfile("left");
            }

            @Override
            public void onSwipeRight() {
                animateProfile("right");
            }
        });

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        tvMessage.setText(matchedUser.getName() + " would like to go to lunch with you at " + restaurant.getName());
        ivProfileImg.setImageResource(android.R.color.transparent);
        Transformation transformation = new RoundedTransformationBuilder().borderColor(Color.BLACK).borderWidthDp(1).oval(true).scaleType(ImageView.ScaleType.CENTER_CROP).build();
        Picasso.with(this).load(matchedUser.getImageUrl()).transform(transformation).into((ivProfileImg));

        if (!snippet.equals("")) {
            TextView tvSnippet = (TextView) findViewById(R.id.tvSnippet);
            tvSnippet.setText('"' + snippet + '"');
            tvSnippet.setVisibility(View.VISIBLE);
        }

        ImageButton ibAccept = (ImageButton) findViewById(R.id.ibAccept);
        ImageButton ibDecline = (ImageButton) findViewById(R.id.ibDecline);

        ibAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateProfile("right");
            }
        });

        ibDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateProfile("left");
            }
        });


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

    private void accept() {
        ParseClient.setUserStatus("Waiting"); //Set their status back to Waiting
        String userMatchResponse = UserRestaurantMatches.STATUS_ACCEPTED;

        if (user.getUserId().equals(match.getReqUserId())) {
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

    public void decline() {
        ParseClient.setUserStatus("Waiting"); //Set their status back to Waiting
        String userMatchResponse = UserRestaurantMatches.STATUS_DENIED;

        if (user.getUserId().equals(match.getReqUserId())) {
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

}
