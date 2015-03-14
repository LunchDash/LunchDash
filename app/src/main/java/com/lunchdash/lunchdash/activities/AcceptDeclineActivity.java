package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.squareup.picasso.Picasso;

public class AcceptDeclineActivity extends Activity {
    ImageView ivProfileImg;
    TextView  tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_decline);

        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");

        User user = ParseClient.getUser(userId);
        Restaurant restaurant = LunchDashApplication.getRestaurantById(restaurantId);

        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImage);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        //tvMessage.setText(user.getName() + " would like to go to lunch with you at " + restaurant.getName());
        tvMessage.setText(user.getName() + " would like to go to lunch with you at " + restaurant.getName());

        ivProfileImg.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(this).load(user.getImageUrl()).into((ivProfileImg));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_decline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAccept(View v) {
        //Communicate with parse.
        //Put up some kind of "Waiting for person to respond..." popup until the other person responds.
        //If fail, throw them back into WaitActivity
        //If success, load ContactAtivity
    }

    public void onDecline(View v) {
        //Communicate with parse.
        //Go back into WaitActivity.
    }
}
