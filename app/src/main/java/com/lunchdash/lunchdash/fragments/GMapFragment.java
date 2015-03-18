package com.lunchdash.lunchdash.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;


public class GMapFragment extends Fragment {
    private MapView mapView;
    private static GoogleMap map;
    private static Double latitude, longitude;

    public GMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) v.findViewById(R.id.restaurantMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        map = mapView.getMap();
        loadMap(map);
        return v;

    }

    public void loadMap(GoogleMap map) {
        latitude = Double.parseDouble(LunchDashApplication.latitude);
        longitude = Double.parseDouble(LunchDashApplication.longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        map.moveCamera(center);
        map.animateCamera(CameraUpdateFactory.zoomTo(12));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
