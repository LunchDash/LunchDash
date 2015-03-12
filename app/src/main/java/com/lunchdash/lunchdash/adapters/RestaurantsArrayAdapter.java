package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantsArrayAdapter extends ArrayAdapter<Restaurant> {


    public RestaurantsArrayAdapter(Context context, List<Restaurant> restaurants) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restaurant restaurant = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurant, parent, false);
        }

        TextView tvRestName = (TextView) convertView.findViewById(R.id.tvRestName);
        TextView tvReviews = (TextView) convertView.findViewById(R.id.tvReviews);
        TextView tvCategories = (TextView) convertView.findViewById(R.id.tvCategories);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);


        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        ImageView ivRating = (ImageView) convertView.findViewById(R.id.ivRating);

        //Fill info
        tvRestName.setText(restaurant.getName());
        tvReviews.setText(restaurant.getReviewCount() + " reviews");

        //Concat all the Categories
        String[][] categoryArray = restaurant.getCategories();
        String categories = "";
        for (int i = 0; i < categoryArray.length; i++) {
            categories += categoryArray[i][0];
            if (i != categoryArray.length - 1) { //If it's not the last item, add a comma.
                categories += ", ";
            }
        }
        tvCategories.setText(categories);
        //Concat all the lines of the Display Address
        String[] addressArray = restaurant.getDisplayAddress();
        String address = "";
        for (int i = 0; i < addressArray.length; i++) {
            address += addressArray[i];
            if (i != addressArray.length - 1) { //If it's not the last item, add a space.
                address += " ";
            }
        }
        tvAddress.setText(address);

        ivImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(getContext()).load(restaurant.getImageURL()).into(ivImage);
        ivRating.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(getContext()).load(restaurant.getRatingImgUrl()).into(ivRating);

        return convertView;
    }
}
