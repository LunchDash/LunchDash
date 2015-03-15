package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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


public class RestaurantSearchActivity extends Activity {
    public YelpAPI yapi;
    ArrayList<Restaurant> restaurants;
    ListView lvRestaurants;
    RestaurantsArrayAdapter adapterRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);


        yapi = new YelpAPI(Keys.yelpConsumerKey, Keys.yelpConsumerSecret, Keys.yelpToken, Keys.yelpTokenSecret);
        restaurants = new ArrayList<>();
        adapterRestaurants = new RestaurantsArrayAdapter(this, restaurants);
        lvRestaurants = (ListView) findViewById(R.id.lvRestaurants);
        lvRestaurants.setAdapter(adapterRestaurants);

        //We don't want an empty list when we start the activity, so we'll search for restaurants nearby with Sort  By and Max Distance both set to "Best Match"
        new ConnectToYelp().execute("Restaurants", LunchDashApplication.latitude, LunchDashApplication.longitude, "Best Match", "Best Match");

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

    public void onRestaurantSearch(View v) {
        EditText etRestaurantSearch = (EditText) findViewById(R.id.etRestaurantSearch);
        String searchTerm = etRestaurantSearch.getText().toString();

        //Get filter settings
        SharedPreferences filters = getSharedPreferences("settings", 0);
        String sortBy = filters.getString("sortBy", "Best Match");
        String maxDistance = filters.getString("maxDistance", "Best Match");


        if (searchTerm.equals("")) return;

        new ConnectToYelp().execute(searchTerm, LunchDashApplication.latitude, LunchDashApplication.longitude, sortBy, maxDistance);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etRestaurantSearch.getWindowToken(), 0);
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

    private class ConnectToYelp extends AsyncTask<String, Void, ArrayList<Restaurant>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(RestaurantSearchActivity.this, "Searching Restaurants", "Please wait...", true);
        }

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
                restaurants = Restaurant.fromJSONArray((new JSONObject(jsonResults)).getJSONArray("businesses"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return restaurants;
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurants) {
            for (int i = 0; i < restaurants.size(); i++) { //Unselect all the restaurants.
                restaurants.get(i).setSelected(false);
            }

            adapterRestaurants.notifyDataSetChanged();
            adapterRestaurants.clear();
            adapterRestaurants.addAll(restaurants);
            adapterRestaurants.notifyDataSetChanged();

            lvRestaurants.smoothScrollToPosition(0); //Scroll back to the top.
            Button btnFinished = (Button) findViewById(R.id.btnFinished);
            btnFinished.setVisibility(View.VISIBLE);
            dialog.dismiss();
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
        user.setPhoneNumber("1234567890"); //Uncomment on Emulator

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
