package com.lunchdash.lunchdash.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.activities.MainActivity;
import com.lunchdash.lunchdash.helpers.PreferenceFragment;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flSettings, new PrefFragment(), "Preference Fragment");
        ft.commit();

        ImageButton ibDrawer = (ImageButton) v.findViewById(R.id.ibDrawer); //This button opens/closes the nav drawer
        ibDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).toggleDrawer();
            }
        });

        return v;
    }

    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        /*@Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            View view = super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
            view.setBackgroundColor(Color.WHITE);
            return view;
        }*/
    }

}
