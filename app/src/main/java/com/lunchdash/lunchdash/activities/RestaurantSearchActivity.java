package com.lunchdash.lunchdash.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.lunchdash.lunchdash.APIs.Keys;
import com.lunchdash.lunchdash.APIs.YelpAPI;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.RestaurantsArrayAdapter;
import com.lunchdash.lunchdash.fragments.FilterDialog;
import com.lunchdash.lunchdash.models.Restaurant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class RestaurantSearchActivity extends ActionBarActivity {
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

    }

    public void onRestaurantSearch(View v) {
        EditText etRestaurantSearch = (EditText) findViewById(R.id.etRestaurantSearch);
        String searchTerm = etRestaurantSearch.getText().toString();

        //METHOD TO GRAB CURRENT LAT/LONG
        String latitude = "37.525773";
        String longitude = "-122.286674";
        SharedPreferences filters = getSharedPreferences("settings", 0);
        String sortBy = filters.getString("sortBy", "Best Match");
        String maxDistance = filters.getString("maxDistance", "Best Match");

        if (searchTerm.equals("")) return;
        try {
            //term, latitude, longitude, sortBy, maxDistance
            restaurants = new ConnectToYelp().execute(searchTerm, latitude, longitude, sortBy, maxDistance).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        adapterRestaurants.notifyDataSetChanged();
        adapterRestaurants.clear();
        adapterRestaurants.addAll(restaurants);
        adapterRestaurants.notifyDataSetChanged();
        lvRestaurants.smoothScrollToPosition(0);
    }

    public void onFilterClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_search) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
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
}
