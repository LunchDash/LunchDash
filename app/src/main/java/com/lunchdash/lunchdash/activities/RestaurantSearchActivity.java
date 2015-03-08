package com.lunchdash.lunchdash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunchdash.lunchdash.APIs.Keys;
import com.lunchdash.lunchdash.APIs.YelpAPI;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.RestaurantsArrayAdapter;
import com.lunchdash.lunchdash.models.Restaurant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class RestaurantSearchActivity extends Activity {
    public YelpAPI yapi;
    ArrayList<Restaurant> restaurants;
    ListView lvRestaurants;
    RestaurantsArrayAdapter adapterRestaurants;
    TextView tvSelectedRestaurantCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        yapi = new YelpAPI(Keys.yelpConsumerKey, Keys.yelpConsumerSecret, Keys.yelpToken, Keys.yelpTokenSecret);
        restaurants = new ArrayList<>();
        adapterRestaurants = new RestaurantsArrayAdapter(this, restaurants);
        lvRestaurants = (ListView) findViewById(R.id.lvRestaurants);
        lvRestaurants.setAdapter(adapterRestaurants);
        tvSelectedRestaurantCount = (TextView) findViewById(R.id.tvSelectedRestaurantCount);
        setupListViewListener();
    }

    public void onRestaurantSearch(View v) {
        EditText etRestaurantSearch = (EditText) findViewById(R.id.etRestaurantSearch);
        String searchTerm = etRestaurantSearch.getText().toString();
        if (searchTerm.equals("")) return;
        try {
            restaurants = new ConnectToYelp().execute(searchTerm, "San Francisco, CA").get();
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

        Toast.makeText(this,
                String.valueOf(lvRestaurants.getCheckedItemCount()),
                Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }

    private class ConnectToYelp extends AsyncTask<String, Void, ArrayList<Restaurant>> {
        @Override
        protected ArrayList<Restaurant> doInBackground(String... params) {
            String jsonResults = yapi.searchForRestaurants(params[0], params[1]);
            ArrayList<Restaurant> results = null;
            try {
                results = Restaurant.fromJSONArray((new JSONObject(jsonResults)).getJSONArray("businesses"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    private void setupListViewListener() {
        lvRestaurants.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                restaurants.get(position).toggleSelected();

                tvSelectedRestaurantCount.setText(lvRestaurants.getCheckedItemCount() + " Selected");

                //Toast.makeText(view.getContext(), restaurants.get(position).getName() + restaurants.get(position).isSelected()
                //        , Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onClickDoneButton (View v) {
        for (int i = 0; i < restaurants.size(); i++) {
            if (!restaurants.get(i).isSelected()) {
                adapterRestaurants.remove(restaurants.get(i));
            }
        }

        // TBD
        // Will call WaitActivity, & return to the shorter list here
        Intent i = new Intent(this, WaitActivity.class);
        startActivity(i);
    }

}
