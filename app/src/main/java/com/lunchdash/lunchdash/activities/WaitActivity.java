package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.lunchdash.lunchdash.R;

import java.util.Random;

public class WaitActivity extends Activity {

    private static int WAIT_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                handleRequests();
            }
        }, WAIT_TIME_OUT);
    }

    void handleRequests()
    {
        if (onGetMatchRequest()) {
            String userId = "10155239203120307"; //TODO
            String restaurantId = "a-slice-of-new-york-san-jose"; //TODO

            Intent i = new Intent(WaitActivity.this, AcceptDeclineActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("restaurantId",restaurantId);
            startActivity(i);
            finish();
        }

        if (onGetConfirmationRequest()) {
            String userId = "10155239203120307"; //TODO
            String restaurantId = "a-slice-of-new-york-san-jose"; //TODO
            Intent i = new Intent(WaitActivity.this, ContactActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("restaurantId",restaurantId);
            startActivity(i);
            finish();
        }
    }

    boolean onGetMatchRequest() {
        Random r = new Random();
        boolean haveMatchRequest = r.nextBoolean();
        return haveMatchRequest;
    }

    boolean onGetConfirmationRequest() {
        Random r = new Random();
        boolean haveConfimation = r.nextBoolean();
        return haveConfimation;

    }

    public boolean onGet(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wait, menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wait, menu);
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

}
