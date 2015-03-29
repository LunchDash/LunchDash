package com.lunchdash.lunchdash.fragments;

import android.os.Bundle;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.helpers.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
