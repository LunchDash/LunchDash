package com.lunchdash.lunchdash.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() == null) {
            setContentView(R.layout.activity_login);
        } else {
            onLoginSuccess();
        }
    }

    public void onLoginClick(View v) {

        final ImageButton ibLogin = (ImageButton) findViewById(R.id.ibLogin);
        ibLogin.setVisibility(View.INVISIBLE); //Hide the login button.

        ParseFacebookUtils.logIn(Arrays.asList("email"), this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("AppDebug", "The user cancelled the Facebook login.");
                    ibLogin.setVisibility(View.VISIBLE); //Show the login button.
                } else {
                    Log.d("AppDebug", "Authentication successful.");
                    onLoginSuccess();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void onLoginSuccess() {
        LunchDashApplication.user = new User();

        //Store their phone number.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        LunchDashApplication.user.setPhoneNumber(tMgr.getLine1Number());
        LunchDashApplication.user.setCurrentLat(LunchDashApplication.latitude);
        LunchDashApplication.user.setCurrentLon(LunchDashApplication.longitude);
        LunchDashApplication.user.setPhoneNumber("1234567890"); //Uncomment on Emulator

        populateUserModel();
        Intent i;

        User savedUser = LunchDashApplication.getUserFromSharedPref(LoginActivity.this); //We're using this so we don't have to wait for the facebook call to finish.
        String userStatus;
        if (savedUser == null) { //This is their first time using the app or they've cleared their data.
            userStatus = "None";
        } else {
            userStatus = ParseClient.getUserStatus(savedUser.getUserId());
        }

        if (userStatus.equals("None") || userStatus.equals("")) { //It's an empty string if the user still doesn't have an entry in the UserTable
            i = new Intent(LoginActivity.this, MainActivity.class);
        } else {
            i = new Intent(LoginActivity.this, WaitActivity.class);
        }
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();

    }

    public void populateUserModel() {
        ParseFacebookUtils.initialize("scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD");
        final Session fbSession = ParseFacebookUtils.getSession();

        new Request(fbSession, "/me", null, HttpMethod.GET, new Request.Callback() { //Send a request to get the user's id, email, and name
            @Override
            public void onCompleted(Response response) {
                JSONObject userJSON = null;
                try {
                    userJSON = new JSONObject(response.getRawResponse());
                    LunchDashApplication.user.setUserId(userJSON.getString("id"));
                    String firstName = userJSON.getString("first_name");
                    String lastName = userJSON.getString("last_name");
                    if (lastName != null && !lastName.equals("")) {
                        lastName = lastName.substring(0, 1) + "."; //Get the first initial of their last name.
                    } else {
                        lastName = "";
                    }

                    LunchDashApplication.user.setName(firstName + " " + lastName);
                    LunchDashApplication.user.setEmail(userJSON.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();

        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        //params.putString("type", "large");
        params.putInt("height", 400);

        new Request(fbSession, "/me/picture", params, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                try {
                    JSONObject profilePictureJSON = new JSONObject(response.getRawResponse());
                    LunchDashApplication.user.setImageUrl(profilePictureJSON.getJSONObject("data").getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
