package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.squareup.picasso.Picasso;

public class AcceptDeclineActivity extends Activity {
    ImageView ivProfileImg;
    TextView  tvMessage;
    User matchedUser;
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_decline);

        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");

        matchedUser = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(restaurantId);

        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImage);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        tvMessage.setText(matchedUser.getName() + " would like to go to lunch with you at " + restaurant.getName());
        ivProfileImg.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(matchedUser.getImageUrl()).into((ivProfileImg));
    }

    public void onAccept(View v) {
        ParseClient.saveUserRestaurantMatch(
                LunchDashApplication.user.getUserId(),
                matchedUser.getUserId(),
                restaurant.getId(),
                UserRestaurantMatches.STATUS_ACCEPTED);
        finish();
    }

    public void onDecline(View v) {
        ParseClient.saveUserRestaurantMatch(LunchDashApplication.user.getUserId(),
                matchedUser.getUserId(),
                restaurant.getId(),
                UserRestaurantMatches.STATUS_DENIED);
        finish();
    }
}
