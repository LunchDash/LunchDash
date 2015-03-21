package com.lunchdash.lunchdash.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.RestaurantSearchActivity;
import com.lunchdash.lunchdash.fragments.GMapFragment;
import com.lunchdash.lunchdash.models.Restaurant;
import com.squareup.picasso.Picasso;

public class RestaurantWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public RestaurantWindowAdapter(LayoutInflater i) {
        mInflater = i;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        View v = mInflater.inflate(R.layout.infowindow_restaurant, null);

        TextView tvDistance = (TextView) v.findViewById(R.id.tvDistance);
        TextView tvRestName = (TextView) v.findViewById(R.id.tvRestName);
        TextView tvCategories = (TextView) v.findViewById(R.id.tvCategories);


        int listPos = GMapFragment.markerRestaurantPair.get(marker.getId());
        Restaurant restaurant = RestaurantSearchActivity.restaurants.get(listPos);

        //Fill info
        String distanceString = metersToMiles(restaurant.getDistance()) + " mi";
        tvDistance.setText(distanceString);
        tvRestName.setText(restaurant.getName());


        //Concat all the Categories
        try {
            String[][] categoryArray = restaurant.getCategories();
            String categories = "";
            for (int i = 0; i < categoryArray.length; i++) {
                categories += categoryArray[i][0];
                if (i != categoryArray.length - 1) { //If it's not the last item, add a comma.
                    categories += ", ";
                }
            }
            tvCategories.setText(categories);
        } catch (NullPointerException e) {
            Log.d("APPDEBUG", "Restaurant found without a category!");
            tvCategories.setText("");
        }

        //Concat all the lines of the Display Address
        String[] addressArray = restaurant.getDisplayAddress();
        String address = "";
        try {
            for (int i = 0; i < addressArray.length; i++) {
                address += addressArray[i];
                if (i != addressArray.length - 1) { //If it's not the last item, add a space.
                    address += " ";
                }
            }
        } catch (NullPointerException e) {
        }

        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public String metersToMiles(double meters) {
        double miles = meters * 0.000621371;
        miles = (double) Math.round(miles * 10) / 10;
        return miles + "";
    }
}
