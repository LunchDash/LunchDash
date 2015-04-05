package com.lunchdash.lunchdash.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.MainActivity;
import com.lunchdash.lunchdash.models.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;


public class ProfileFragment extends Fragment {
    private EditText etSnippet;
    private EditText etBio;
    Drawable snippetBackground;
    Drawable bioBackground;
    ImageButton ibEdit;
    ImageButton ibOk;
    ImageButton ibCancel;
    String snippet = "";
    String bio = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, parent, false);
        User user = LunchDashApplication.user;

        HashMap<String, String> profileHm = ParseClient.getUserProfile(LunchDashApplication.user.getUserId());
        if (profileHm != null) {
            snippet = profileHm.get("snippet");
            bio = profileHm.get("bio");
        }

        setupViews(v, user);

        return v;
    }

    public void setupViews(View v, User user) {
        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        etSnippet = (EditText) v.findViewById(R.id.etSnippet);
        etBio = (EditText) v.findViewById(R.id.etBio);

        snippetBackground = etSnippet.getBackground();
        bioBackground = etBio.getBackground();

        etSnippet.setText(snippet);
        etBio.setText(bio);

        etSnippet.setBackgroundColor(Color.parseColor("#00000000")); //Remove the edittext underline for now.
        etBio.setBackgroundColor(Color.parseColor("#00000000")); //Remove the edittext underline for now.

        Transformation transformation = new RoundedTransformationBuilder().borderColor(Color.BLACK).borderWidthDp(1).oval(true).scaleType(ImageView.ScaleType.CENTER_CROP).build();

        Picasso.with(getActivity()).load(user.getImageUrl()).transform(transformation).into((ivProfileImage));
        tvName.setText(user.getName());

        ibEdit = (ImageButton) v.findViewById(R.id.ibEdit);
        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(true);
            }
        });

        ibOk = (ImageButton) v.findViewById(R.id.ibOk);
        ibOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToParse();
                setEditMode(false);
            }
        });

        ibCancel = (ImageButton) v.findViewById(R.id.ibCancel);
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(false);

                //restore previous text
                etSnippet.setText(snippet);
                etBio.setText(bio);

            }
        });

        ImageButton ibDrawer = (ImageButton) v.findViewById(R.id.ibDrawer); //This button opens/closes the nav drawer
        ibDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).toggleDrawer();
            }
        });

        setEditMode(false);
    }

    public void setEditMode(boolean enabled) {
        if (enabled) {
            ibOk.setVisibility(View.VISIBLE);
            ibCancel.setVisibility(View.VISIBLE);
            ibEdit.setVisibility(View.GONE);
            etSnippet.setBackground(snippetBackground);
            etSnippet.setFocusableInTouchMode(true);
            etBio.setBackground(bioBackground);
            etBio.setFocusableInTouchMode(true);
        } else {
            ibOk.setVisibility(View.GONE);
            ibCancel.setVisibility(View.GONE);
            ibEdit.setVisibility(View.VISIBLE);
            etSnippet.setBackgroundColor(Color.parseColor("#00000000")); //Remove the edittext underline for now.
            etBio.setBackgroundColor(Color.parseColor("#00000000")); //Remove the edittext underline for now.
            etSnippet.setFocusable(false);
            etBio.setFocusable(false);

        }

    }

    public void saveToParse() {
        String userId = LunchDashApplication.user.getUserId();
        snippet = etSnippet.getText().toString();
        bio = etBio.getText().toString();

        ParseClient.saveUserProfile(userId, snippet, bio);
    }

}
