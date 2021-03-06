package com.lunchdash.lunchdash.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.RestaurantsArrayAdapter;
import com.lunchdash.lunchdash.models.Restaurant;

import java.util.LinkedList;
import java.util.List;

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

        adapterRestaurants = new RestaurantsArrayAdapter(getActivity(), ((RestaurantSearchFragment) getParentFragment()).restaurants);
        View v = inflater.inflate(R.layout.fragment_list, parent, false);
        lvRestaurants = (ListView) v.findViewById(R.id.lvRestaurants);
        lvRestaurants.setAdapter(adapterRestaurants);

        lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant restaurant = (Restaurant) lvRestaurants.getItemAtPosition(position);
                restaurant.toggleSelected();
                ImageView iv = (ImageView) view.findViewById(R.id.ivSave);
                iv.setImageResource(android.R.color.transparent);
                if (restaurant.isSelected()) {
                    view.setBackgroundColor(getResources().getColor(R.color.listItemSelected));
                    iv.setImageResource(R.mipmap.ic_saved);
                } else {
                    view.setBackgroundColor(Color.WHITE);
                    iv.setImageResource(R.mipmap.ic_save);
                }

                //If any restaurants are selected, show the start matching button.
                List<String> selectedRestaurants = new LinkedList<>();

                for (int i = 0; i < RestaurantSearchFragment.restaurants.size(); i++) {
                    Restaurant r = RestaurantSearchFragment.restaurants.get(i);
                    if (r.isSelected()) {
                        selectedRestaurants.add(r.getId()); //If the restaurant is selected, add the restaurant id to the list.
                    }
                }
                Button btnFinished = (Button) getParentFragment().getView().findViewById(R.id.btnFinished);
                if (selectedRestaurants.size() > 0) {
                    btnFinished.setVisibility(View.VISIBLE);
                } else {
                    btnFinished.setVisibility(View.GONE);
                }
            }
        });

        return v;
    }
}
