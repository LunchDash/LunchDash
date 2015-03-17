package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
    int position = 0;

    public RestaurantsArrayAdapter(Context context, List<Restaurant> restaurants) {
        super(context, android.R.layout.simple_list_item_1, restaurants);
    }

    private static class ViewHolder {
        TextView tvDistance;
        TextView tvRestName;
        TextView tvReviews;
        TextView tvCategories;
        TextView tvAddress;

        ImageView ivImage;
        ImageView ivRating;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.position = position;
        Restaurant restaurant = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_restaurant, parent, false);

            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvRestName = (TextView) convertView.findViewById(R.id.tvRestName);
            viewHolder.tvReviews = (TextView) convertView.findViewById(R.id.tvReviews);
            viewHolder.tvCategories = (TextView) convertView.findViewById(R.id.tvCategories);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.ivRating = (ImageView) convertView.findViewById(R.id.ivRating);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (restaurant.isSelected()) {
            convertView.setBackgroundColor(Color.parseColor("#E8F3FF"));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        //Fill info
        String distanceString = metersToMiles(restaurant.getDistance()) + " mi";
        viewHolder.tvDistance.setText(distanceString);
        viewHolder.tvRestName.setText(restaurant.getName());
        viewHolder.tvReviews.setText(restaurant.getReviewCount() + " reviews");

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
            viewHolder.tvCategories.setText(categories);
        } catch (NullPointerException e) {
            Log.d("APPDEBUG", "Restaurant found without a category!");
            viewHolder.tvCategories.setText("");
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
        viewHolder.tvAddress.setText(address);

        viewHolder.ivImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(getContext()).load(restaurant.getImageURL()).into(viewHolder.ivImage);
        viewHolder.ivRating.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        Picasso.with(getContext()).load(restaurant.getRatingImgUrl()).into(viewHolder.ivRating);

        return convertView;
    }

    public String metersToMiles(double meters) {
        double miles = meters * 0.000621371;
        miles = (double) Math.round(miles * 10) / 10;
        return miles + "";
    }

}
