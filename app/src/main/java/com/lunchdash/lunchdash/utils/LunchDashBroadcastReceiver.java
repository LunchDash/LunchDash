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
    protected Class<? extends android.app.Activity> getActivity(Context context, Intent intent) {
       return WaitActivity.class;
    }

}
