package com.lunchdash.lunchdash.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.helpers.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }
}
