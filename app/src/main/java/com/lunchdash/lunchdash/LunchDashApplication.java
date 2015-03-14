package com.lunchdash.lunchdash;

import android.app.Application;

import com.lunchdash.lunchdash.datastore.UserResturanMatchesTable;
import com.lunchdash.lunchdash.datastore.UserResturantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.User;
import com.parse.Parse;
import com.parse.ParseObject;


public class LunchDashApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD";
    public static final String YOUR_CLIENT_KEY = "POAzmk8AO0H695i4QYHHjSKDSg8VkD4tdEodghYE";
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(UserTable.class);
        ParseObject.registerSubclass(UserResturantsTable.class);
        ParseObject.registerSubclass(UserResturanMatchesTable.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
    }
}