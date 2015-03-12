package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

      //  Parse.enableLocalDatastore(this);
        Parse.initialize(this, "scdBFiBhXpbSgYm6ii3GyOTZhzW1z3OkplDeqhLD", "POAzmk8AO0H695i4QYHHjSKDSg8VkD4tdEodghYE");

        if (ParseUser.getCurrentUser() == null) {
            setContentView(R.layout.activity_login);
        } else {
            onLoginSuccess();
        }
    }

    public void login(View v) {
        ParseFacebookUtils.logIn(Arrays.asList("email"), this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("AppDebug", "The user cancelled the Facebook login.");
                } else {
                    Log.d("AppDebug", "Authentication successful.");
                    onLoginSuccess();
                }
            }
        });
    }

    public void onLoginSuccess() {
        populateUserModel();
        Intent i = new Intent(LoginActivity.this, RestaurantSearchActivity.class);
        startActivity(i);

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
                    User user = User.getUserInstance();
                    userJSON = new JSONObject(response.getRawResponse());
                    user.setUserId(userJSON.getString("id"));
                    user.setEmail(userJSON.getString("email"));
                    user.setName(userJSON.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();

        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "large");

        new Request(fbSession, "/me/picture", params, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                User user = User.getUserInstance();
                try {
                    JSONObject profilePictureJSON = new JSONObject(response.getRawResponse());
                    user.setImageUrl(profilePictureJSON.getJSONObject("data").getString("url"));
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
