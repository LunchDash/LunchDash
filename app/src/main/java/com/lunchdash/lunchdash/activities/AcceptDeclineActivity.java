package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunchdash.lunchdash.APIs.OnSwipeTouchListener;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.squareup.picasso.Picasso;

public class AcceptDeclineActivity extends ActionBarActivity {
    ImageView ivProfileImg;
    TextView tvMessage;
    User matchedUser;
    Restaurant restaurant;
    UserRestaurantMatches match;

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

        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImg.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
//                Toast.makeText(MainActivity.this, "Down", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
//                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
                decline();
            }

            @Override
            public void onSwipeUp() {
//                Toast.makeText(MainActivity.this, "Up", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeRight() {
                accept();
                //Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        tvMessage.setText(matchedUser.getName() + " would like to go to lunch with you at " + restaurant.getName());
        ivProfileImg.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(matchedUser.getImageUrl()).into((ivProfileImg));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    private void accept(){
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

    public void decline(){
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
