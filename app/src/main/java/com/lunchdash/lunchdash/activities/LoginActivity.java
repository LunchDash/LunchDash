package com.lunchdash.lunchdash.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.lunchdash.lunchdash.R;

import com.lunchdash.lunchdash.fragments.MainFragment;
import com.lunchdash.lunchdash.models.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends FragmentActivity {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
       
        ParseUser u = ParseUser.getCurrentUser();

        if(u != null) {
            Intent i = new Intent(LoginActivity.this, RestaurantSearchActivity.class);
            startActivity(i);
            finish();
        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);


        ParseFacebookUtils.logIn(Arrays.asList("email"), this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else {
                    if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");
                    }

                    Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback(){
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            String fullName = user.getName();
                            String email = user.getProperty("email").toString();
                            String facebookId = user.getId();

                            //Do whatever you want
                            User u = new User("sd3", "sdfsdffullName", "asdfadsf", "asdfasdf", "asdfd");
                            u.save();

                        }
                        
                    });
                    
                    Intent i = new Intent(LoginActivity.this, RestaurantSearchActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    
}
