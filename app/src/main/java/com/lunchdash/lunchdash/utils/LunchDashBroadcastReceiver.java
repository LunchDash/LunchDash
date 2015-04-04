package com.lunchdash.lunchdash.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.activities.AcceptDeclineActivity;
import com.lunchdash.lunchdash.activities.WaitActivity;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.parse.ParsePushBroadcastReceiver;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandra.vijayarenu on 3/28/15.
 */
public class LunchDashBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_EXTRA_DATA_KEY         =   "com.parse.Data";
    public static final int ACTION_ACCEPT_DECLINE = 1;
    public static final int ACTION_MATCH = 2;

    @Override
    protected Class<? extends android.app.Activity> getActivity(Context context, Intent intent) {

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            int action = json.getInt("action");
            switch (action){
                case ACTION_ACCEPT_DECLINE:
                    String userid = json.getString("userid");
                    String matchid = json.getString("matchid");
                    UserRestaurantMatches match = ParseClient.getMatch(matchid);

                    Intent acceptDeclineActivityIntent = new Intent(context, AcceptDeclineActivity.class);
                    if (match.getReqUserId().equals(userid)) {
                        acceptDeclineActivityIntent.putExtra("userId", match.getMatchedUserID());
                    } else {
                        acceptDeclineActivityIntent.putExtra("userId", match.getReqUserId());
                    }
                    acceptDeclineActivityIntent.putExtra("restaurantId", match.getRestaurantId());
                    acceptDeclineActivityIntent.putExtra("match", match);
                    acceptDeclineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    acceptDeclineActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(acceptDeclineActivityIntent);
                    break;
                case ACTION_MATCH:


                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return WaitActivity.class;
    }

}
