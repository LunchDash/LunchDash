package com.lunchdash.lunchdash.utils;

import android.content.Context;
import android.content.Intent;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.activities.AcceptDeclineActivity;
import com.lunchdash.lunchdash.activities.ContactActivity;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class LunchDashBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final int ACTION_ACCEPT_DECLINE = 1;
    public static final int ACTION_MATCH = 2;

    @Override
    protected void onPushOpen(Context context, Intent intent) {

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            int action = json.getInt("action");
            String userid = json.getString("userid");
            String matchid = json.getString("matchid");
            UserRestaurantMatches match = ParseClient.getMatch(matchid);
            String restaurantid = match.getRestaurantId();

            String matchedUserid = userid;
            if (match.getReqUserId().equals(userid)) {
                matchedUserid = match.getMatchedUserID();
            } else {
                matchedUserid = match.getReqUserId();
            }


            switch (action) {
                case ACTION_ACCEPT_DECLINE:
                    Intent acceptDeclineActivityIntent = new Intent(context, AcceptDeclineActivity.class);
                    acceptDeclineActivityIntent.putExtra("restaurantId", restaurantid);
                    acceptDeclineActivityIntent.putExtra("match", match);
                    acceptDeclineActivityIntent.putExtra("userId", matchedUserid);
                    acceptDeclineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    acceptDeclineActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(acceptDeclineActivityIntent);
                    break;
                case ACTION_MATCH:
                    Intent contact = new Intent(context, ContactActivity.class);
                    contact.putExtra("userId",matchedUserid);
                    contact.putExtra("restaurantId", restaurantid);
                    contact.putExtra("match", match);
                    contact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    contact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(contact);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
