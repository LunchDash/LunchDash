package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantsArrayAdapter extends ArrayAdapter<Restaurant> {
    int position = 0;
    Context context;

    public RestaurantsArrayAdapter(Context context, List<Restaurant> restaurants) {
        super(context, android.R.layout.simple_list_item_1, restaurants);
        this.context = context;
    }

    private static class ViewHolder {
        TextView tvPeopleWaiting;
        TextView tvDistance;
        TextView tvRestName;
        TextView tvCategories;
        TextView tvAddress;
        TextView tvRate;
        RelativeLayout rvPeopleWaiting;
        ImageView ivImage;
        ImageView ivSave;

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

            viewHolder.tvPeopleWaiting = (TextView) convertView.findViewById(R.id.tvPeopleWaiting);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvRestName = (TextView) convertView.findViewById(R.id.tvRestName);
            viewHolder.tvCategories = (TextView) convertView.findViewById(R.id.tvCategories);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvRate = (TextView) convertView.findViewById(R.id.tvrate);
            viewHolder.rvPeopleWaiting = (RelativeLayout) convertView.findViewById(R.id.rvPeopleWaiting);

            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.ivSave = (ImageView) convertView.findViewById(R.id.ivSave);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivSave.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        if (restaurant.isSelected()) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listItemSelected));
            viewHolder.ivSave.setImageResource(R.mipmap.ic_saved);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
            viewHolder.ivSave.setImageResource(R.mipmap.ic_save);
        }

        //Fill info
        viewHolder.tvPeopleWaiting.setText(""); //Clear text field
        int numWaiting = restaurant.getUserCount();
        if (numWaiting > 0) { //Set text if there's anyone waiting for that restaurant.
            viewHolder.rvPeopleWaiting.setVisibility(View.VISIBLE);
            viewHolder.tvPeopleWaiting.setText(numWaiting + " waiting!");
        } else {
            viewHolder.rvPeopleWaiting.setVisibility(View.GONE);
        }

        String distanceString = metersToMiles(restaurant.getDistance()) + " mi";
        viewHolder.tvDistance.setText(distanceString);
        viewHolder.tvRestName.setText(restaurant.getName());

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

        viewHolder.tvRate.setText(Double.toString(restaurant.getRating()));

        return convertView;
    }

    public String metersToMiles(double meters) {
        double miles = meters * 0.000621371;
        miles = (double) Math.round(miles * 10) / 10;
        return miles + "";
    }

}
