package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lunchdash.lunchdash.APIs.Keys;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.APIs.YelpAPI;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.RestaurantsArrayAdapter;
import com.lunchdash.lunchdash.fragments.FilterDialog;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RestaurantSearchActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public YelpAPI yapi;
    ArrayList<Restaurant> restaurants;
    ListView lvRestaurants;
    RestaurantsArrayAdapter adapterRestaurants;
    Location location;
    GoogleApiClient gApiClient;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);
        buildGoogleApiClient();

        yapi = new YelpAPI(Keys.yelpConsumerKey, Keys.yelpConsumerSecret, Keys.yelpToken, Keys.yelpTokenSecret);
        restaurants = new ArrayList<>();
        adapterRestaurants = new RestaurantsArrayAdapter(this, restaurants);
        lvRestaurants = (ListView) findViewById(R.id.lvRestaurants);
        lvRestaurants.setAdapter(adapterRestaurants);

        lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant restaurant = (Restaurant) lvRestaurants.getItemAtPosition(position);
                restaurant.toggleSelected();
                if (restaurant.isSelected()) {
                    view.setBackgroundColor(0xF1FFA05); //First byte is alpha
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        gApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onRestaurantSearch(View v) {
        EditText etRestaurantSearch = (EditText) findViewById(R.id.etRestaurantSearch);
        String searchTerm = etRestaurantSearch.getText().toString();

        //Hide the soft keyboard.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etRestaurantSearch.getWindowToken(), 0);

        //Get filter settings
        SharedPreferences filters = getSharedPreferences("settings", 0);
        String sortBy = filters.getString("sortBy", "Best Match");
        String maxDistance = filters.getString("maxDistance", "Best Match");

        if (searchTerm.equals("")) return;
        try {
            restaurants = new ConnectToYelp().execute(searchTerm, latitude, longitude, sortBy, maxDistance).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < restaurants.size(); i++) { //Unselect all the restaurants.
            restaurants.get(i).setSelected(false);
        }

        adapterRestaurants.notifyDataSetChanged();
        adapterRestaurants.clear();
        adapterRestaurants.addAll(restaurants);
        adapterRestaurants.notifyDataSetChanged();

        lvRestaurants.smoothScrollToPosition(0); //Scroll back to the top.
    }

    public void onFilterClick(View v) {
        FragmentManager fm = getFragmentManager();
        FilterDialog fd = FilterDialog.newInstance();
        fd.show(fm, "fragment_filter_options");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_search) {
            return true;
        }*/

        Toast.makeText(this,
                String.valueOf(lvRestaurants.getCheckedItemCount()),
                Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(gApiClient);
        if (location != null) {
            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
            Log.d("APPDEBUG", "This device's Latitude/Longitude is: " + latitude + "," + longitude);
        } else {
            Log.d("APPDEBUG", "Location is null");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class ConnectToYelp extends AsyncTask<String, Void, ArrayList<Restaurant>> {
        @Override
        protected ArrayList<Restaurant> doInBackground(String... params) {

            String term = params[0];
            String latitude = params[1];
            String longitude = params[2];
            String sortBy = params[3];
            String maxDistance = params[4];

            String jsonResults = yapi.searchForRestaurants(term, latitude, longitude, sortBy, maxDistance);
            ArrayList<Restaurant> results = null;
            try {
                results = Restaurant.fromJSONArray((new JSONObject(jsonResults)).getJSONArray("businesses"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    public void onFinishedClick(View v) throws ParseException {


        List<String> selectedRestaurants = new LinkedList();

        LunchDashApplication.restaurantList = new ArrayList<>();

        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            if (restaurant.isSelected()) {
                selectedRestaurants.add(restaurant.getId()); //If the restaurant is selected, add the restaurant id to the list.
                LunchDashApplication.restaurantList.add(restaurant);
            }
        }
        if (selectedRestaurants.size() < 1) { //We're going to take no action if they didn't select at least 1 restaurant.
            Toast.makeText(this, "Please select at least 1 restaurant!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = LunchDashApplication.user;
        //user.setPhoneNumber("1234567890"); //Uncomment on Emulator 

        ParseClient.saveUser(user); //Create or update user info.
        ParseClient.deleteUserRestaurantPairs(user.getUserId()); //Delete any existing user/restaurant pairs in the UserRestaurantsTable
        ParseClient.deleteRestaurantMatches(user.getUserId());

        for (String restaurantId : selectedRestaurants) { //Insert restaurants into the UserRestaurantsTable
            UserRestaurants userRestaurantPair = new UserRestaurants(user.getUserId(), restaurantId);
            ParseClient.saveUserRestaurantPair(userRestaurantPair);
            ParseClient.populateUsersResutaurantMatches(userRestaurantPair);
        }

        Intent i = new Intent(this, WaitActivity.class); //Start waiting for restaurants.
        startActivity(i);
    }
}
