package com.lunchdash.lunchdash.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.RestaurantSearchActivity;
import com.lunchdash.lunchdash.adapters.RestaurantsArrayAdapter;
import com.lunchdash.lunchdash.models.Restaurant;

public class RestaurantListFragment extends Fragment {
    public RestaurantsArrayAdapter adapterRestaurants;
    public ListView lvRestaurants;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        adapterRestaurants = new RestaurantsArrayAdapter(getActivity(), ((RestaurantSearchActivity) getActivity()).restaurants);
        View v = inflater.inflate(R.layout.fragment_list, parent, false);
        lvRestaurants = (ListView) v.findViewById(R.id.lvRestaurants);
        lvRestaurants.setAdapter(adapterRestaurants);

        lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant restaurant = (Restaurant) lvRestaurants.getItemAtPosition(position);
                restaurant.toggleSelected();
                if (restaurant.isSelected()) {
                    view.setBackgroundColor(Color.parseColor("#E8F3FF"));
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        });

        return v;
    }
}