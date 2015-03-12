package com.lunchdash.lunchdash;

import android.app.Application;
import android.os.Message;

import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by chandrav on 3/11/15.
 */
public class LunchDashApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD";
    public static final String YOUR_CLIENT_KEY = "POAzmk8AO0H695i4QYHHjSKDSg8VkD4tdEodghYE";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(UserTable.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
    }
}