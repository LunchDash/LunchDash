package com.lunchdash.lunchdash.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.ContactActivity;
import com.lunchdash.lunchdash.fragments.MatchedMapFragment;
import com.squareup.picasso.Picasso;

public class UserWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public UserWindowAdapter(LayoutInflater i) {
        mInflater = i;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.d("APPDEBUG", "Marker id is: " + marker.getId());
        if (marker.getId().equals(MatchedMapFragment.otherUserMarkerId)) {
            Log.d("APPDEBUG", "Marker matches");
            View v = mInflater.inflate(R.layout.infowindow_user, null);

            TextView tvName = (TextView) v.findViewById(R.id.tvName);
            ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);

            //Fill info
            tvName.setText(ContactActivity.matchedUser.getName());
            ivProfileImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
            Picasso.with(v.getContext()).load(ContactActivity.matchedUser.getImageUrl()).into(ivProfileImage);
            return v;
        } else {
            Log.d("APPDEBUG", "Marker doesn't match");
            return null;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
