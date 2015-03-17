package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.squareup.picasso.Picasso;

public class ContactActivity extends ActionBarActivity {
    ImageView ivUserImage;
    TextView tvContactText;


    User user;
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        User user;

        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");

        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        tvContactText = (TextView) findViewById(R.id.tvContactText);

        user = ParseClient.getUser(userId);
        Restaurant restaurant =  LunchDashApplication.getRestaurantById(restaurantId);

        tvContactText.setText(user.getName() + " is ready for an awesome lunch!\\nGet in touch with him/her @ restaurant " + restaurant.getName());
        ivUserImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(this).load(user.getImageUrl()).into((ivUserImage));

    }

    public void onCallClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0123456789"));
        startActivity(intent);
    }

    public void onEmailClick(View v) {
        Intent mailClient = new Intent(Intent.ACTION_VIEW);
        mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        mailClient.setType("plain/text");
        mailClient.putExtra(Intent.EXTRA_EMAIL, user.getEmail());
        mailClient.putExtra(Intent.EXTRA_SUBJECT, "LunchDash Meetup at"+ restaurant.getName());

        startActivity(mailClient);
    }

}
