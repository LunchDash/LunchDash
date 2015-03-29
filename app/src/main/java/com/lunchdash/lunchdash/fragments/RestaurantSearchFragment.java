package com.lunchdash.lunchdash.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lunchdash.lunchdash.APIs.Keys;
import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.APIs.YelpAPI;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.WaitActivity;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RestaurantSearchFragment extends Fragment {
    public YelpAPI yapi;
    public static List<Restaurant> restaurants;
    List<String> selectedRestaurants;
    public static FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant_search, container, false);
        yapi = new YelpAPI(Keys.yelpConsumerKey, Keys.yelpConsumerSecret, Keys.yelpToken, Keys.yelpTokenSecret);
        restaurants = new ArrayList<>();

        fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayoutRestaurant, new RestaurantListFragment(), "RESTAURANT_LIST");
        ft.commit();

        setupViews(v);


        //We don't want an empty list when we start the activity, so we'll search for restaurants nearby with Sort  By and Max Distance both set to "Best Match"
        new ConnectToYelp().execute("Restaurants", LunchDashApplication.latitude, LunchDashApplication.longitude, "Best Match", "Best Match");

        return v;
    }

    private void setupViews(View v) {

        final EditText etRestaurantSearch = (EditText) v.findViewById(R.id.etRestaurantSearch);

        etRestaurantSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) { //Submits if you press the enter key on the soft keyboard.
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onRestaurantSearch(etRestaurantSearch);
                    return true;
                }
                return false;
            }
        });

        ImageButton ibFilter = (ImageButton) v.findViewById(R.id.ibFilter);
        ibFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                FilterDialog fd = FilterDialog.newInstance();
                fd.show(fm, "fragment_filter_options");
            }
        });

        ImageButton ibSwitcher = (ImageButton) v.findViewById(R.id.ibSwitcher);
        ibSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();

                RestaurantListFragment rListFragment = (RestaurantListFragment) getChildFragmentManager().findFragmentByTag("RESTAURANT_LIST");

                ImageButton ibSwitcher = (ImageButton) getView().findViewById(R.id.ibSwitcher);

                if (rListFragment != null) { //We see the list.  Switch to the map.
                    ft.replace(R.id.frameLayoutRestaurant, new GMapFragment(), "RESTAURANT_MAP");
                    ibSwitcher.setImageResource(R.drawable.ic_list); //Show the list button.

                } else { //We're seeing the map.  Switch to the list.
                    ft.replace(R.id.frameLayoutRestaurant, new RestaurantListFragment(), "RESTAURANT_LIST");
                    ibSwitcher.setImageResource(android.R.drawable.ic_dialog_map); //Show the map button.
                }

                ft.commit();
            }
        });

        ImageButton ibSearch = (ImageButton) v.findViewById(R.id.ib_search);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestaurantSearch(v);
            }
        });

        Button ibFinished = (Button) v.findViewById(R.id.btnFinished);
        ibFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRestaurants = new LinkedList<>();

                LunchDashApplication.restaurantList = new ArrayList<>();

                for (int i = 0; i < restaurants.size(); i++) {
                    Restaurant restaurant = restaurants.get(i);
                    if (restaurant.isSelected()) {
                        selectedRestaurants.add(restaurant.getId()); //If the restaurant is selected, add the restaurant id to the list.
                        LunchDashApplication.restaurantList.add(restaurant);
                    }
                }

                if (selectedRestaurants.size() > 0) {
                    new FinishTask().execute(null, null, null); //Run the parse tasks in the background.
                } else { //We're going to take no action if they didn't select at least 1 restaurant.
                    Toast.makeText(getActivity(), "Please select at least 1 restaurant!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onRestaurantSearch(View v) {
        EditText etRestaurantSearch = (EditText) getView().findViewById(R.id.etRestaurantSearch);
        String searchTerm = etRestaurantSearch.getText().toString();

        //Get filter settings
        SharedPreferences filters = getActivity().getSharedPreferences("settings", 0);
        String sortBy = filters.getString("sortBy", "Best Match");
        String maxDistance = filters.getString("maxDistance", "Best Match");

        if (searchTerm.equals("")) return;

        new ConnectToYelp().execute(searchTerm, LunchDashApplication.latitude, LunchDashApplication.longitude, sortBy, maxDistance);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etRestaurantSearch.getWindowToken(), 0);
    }

    public void onFinished() throws ParseException {
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
    }

    private class ConnectToYelp extends AsyncTask<String, Void, List<Restaurant>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Searching Restaurants", "Please wait...", true);
        }

        @Override
        protected List<Restaurant> doInBackground(String... params) {

            String term = params[0];
            String latitude = params[1];
            String longitude = params[2];
            String sortBy = params[3];
            String maxDistance = params[4];

            String jsonResults = yapi.searchForRestaurants(term, latitude, longitude, sortBy, maxDistance);
            try {
                restaurants = Restaurant.fromJSONArray((new JSONObject(jsonResults)).getJSONArray("businesses"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return restaurants;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            for (int i = 0; i < restaurants.size(); i++) { //Unselect all the restaurants
                Restaurant restaurant = restaurants.get(i);
                restaurant.setSelected(false);
                restaurant.setUserCount(ParseClient.getUserCountForResturant(restaurant.getId()));
            }

            //Get the current fragment
            RestaurantListFragment rListFragment = (RestaurantListFragment) getChildFragmentManager().findFragmentByTag("RESTAURANT_LIST");
            GMapFragment gMapFragment = (GMapFragment) getChildFragmentManager().findFragmentByTag("RESTAURANT_MAP");

            //RestaurantListFragment rListFragment = (RestaurantListFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayoutRestaurant);
            if (rListFragment != null) {
                rListFragment.adapterRestaurants.clear();
                rListFragment.adapterRestaurants.addAll(restaurants);
                rListFragment.adapterRestaurants.notifyDataSetChanged();
                rListFragment.lvRestaurants.smoothScrollToPosition(0); //Scroll back to the top.
            }
            if (gMapFragment != null) {
                gMapFragment.updateMap();
            }

            Button btnFinished = (Button) getView().findViewById(R.id.btnFinished);
            btnFinished.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    }

    private class FinishTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Getting Ready", "Please wait...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                onFinished();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            dialog.dismiss();
            Intent i = new Intent(getActivity(), WaitActivity.class); //Start waiting for restaurants.
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

}
