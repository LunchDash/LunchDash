package com.lunchdash.lunchdash.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lunchdash.lunchdash.activities.WaitActivity;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by chandra.vijayarenu on 3/28/15.
 */
public class LunchDashBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Activity getActivity(Context context, Intent intent) {
        Activity wait = new WaitActivity();
        return wait; // the activity that shows up
    }

}
