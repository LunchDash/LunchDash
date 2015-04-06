package com.lunchdash.lunchdash;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;


public class LunchDashApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD";
    public static final String YOUR_CLIENT_KEY = "POAzmk8AO0H695i4QYHHjSKDSg8VkD4tdEodghYE";

    public static User user;
    public static String longitude;
    public static String latitude;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(UserTable.class);
        ParseObject.registerSubclass(UserRestaurantsTable.class);
        ParseObject.registerSubclass(UserRestaurantMatchesTable.class);
        ParseObject.registerSubclass(ChatMessageTable.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public static void saveUserToSharedPref(Context context) { //Saves the user to the shared preference.  Run this when something has changed.

        SharedPreferences spData = context.getSharedPreferences("data", 0);
        SharedPreferences.Editor editor = spData.edit();
        Gson gson = new Gson();
        editor.putString("user", gson.toJson(user));
        editor.commit();
    }

    public static User getUserFromSharedPref(Context context) { //Saves the user to the shared preference.  Run this when something has changed.
        SharedPreferences spData = context.getSharedPreferences("data", 0);
        Gson gson = new Gson();
        String json = spData.getString("user", null);
        if (json == null) {
            return null;
        }
        LunchDashApplication.user = gson.fromJson(json, User.class);
        return LunchDashApplication.user;
    }

    public static Restaurant getRestaurantById(Context context, String restaurantId) {
        Gson gson = new Gson();
        SharedPreferences data = context.getSharedPreferences("data", 0);
        String json = data.getString("restaurants", null);
        Restaurant[] restaurants = gson.fromJson(json, Restaurant[].class);

        for (Restaurant restaurant : restaurants) {
            if (restaurant.getId().equals(restaurantId)) {
                return restaurant;
            }
        }
        return null;
    }


}