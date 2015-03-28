package com.lunchdash.lunchdash;

import android.app.Application;

import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import java.util.List;


public class LunchDashApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD";
    public static final String YOUR_CLIENT_KEY = "POAzmk8AO0H695i4QYHHjSKDSg8VkD4tdEodghYE";
    public static User user;
    public static List<Restaurant> restaurantList;
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
        ParseFacebookUtils.initialize("scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD");

    }

    public static Restaurant getRestaurantById(String restaurantId) {
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getId().equals(restaurantId)) {
                return restaurant;
            }
        }
        return null;
    }
}