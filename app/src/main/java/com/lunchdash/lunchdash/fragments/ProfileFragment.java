package com.lunchdash.lunchdash.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.User;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, parent, false);
        User user = LunchDashApplication.user;
        setupViews(v, user);
        return v;
    }

    public void setupViews(View v, User user) {
        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvSnippet = (TextView) v.findViewById(R.id.tvName);
        TextView tvBio = (TextView) v.findViewById(R.id.tvName);

        Picasso.with(getActivity()).load(user.getImageUrl()).into((ivProfileImage));
        tvName.setText(user.getName());


    }

}
