package com.lunchdash.lunchdash.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.RestaurantWindowAdapter;
import com.lunchdash.lunchdash.models.Restaurant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class GMapFragment extends Fragment {
    private MapView mapView;
    private static GoogleMap map;
    private static Double latitude, longitude;
    public static HashMap<String, Integer> markerRestaurantPair; //Key is the marker's id.  The value is the position of the restaurant in the restaurants List.

    public GMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) v.findViewById(R.id.restaurantMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        map = mapView.getMap();
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new RestaurantWindowAdapter(inflater));
        latitude = Double.parseDouble(LunchDashApplication.latitude);
        longitude = Double.parseDouble(LunchDashApplication.longitude);
        centerCamera();
        updateMap();
        return v;

    }

    public void updateMap() {
        map.clear();
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                List<Restaurant> rList = RestaurantSearchFragment.restaurants;
                markerRestaurantPair = new HashMap<>();
                for (int i = 0; i < rList.size(); i++) {
                    LatLng rLatLng = new LatLng(rList.get(i).getLatitude(), rList.get(i).getLongitude());
                    Marker marker = map.addMarker(new MarkerOptions().position(rLatLng));
                    markerRestaurantPair.put(marker.getId(), i);

                    boolean selected = RestaurantSearchFragment.restaurants.get(i).isSelected();
                    if (selected) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected));
                        dropPin(marker);
                    } else {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_unselected));
                    }

                }
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int position = markerRestaurantPair.get(marker.getId()); //The position of the restaurant in the restaurants array list.
                Restaurant restaurant = RestaurantSearchFragment.restaurants.get(position);
                restaurant.toggleSelected();
                boolean selected = restaurant.isSelected();
                if (selected) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected));
                    dropPin(marker);
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_unselected));
                }

                //If any restaurants are selected, show the start matching button.
                List<String> selectedRestaurants = new LinkedList<>();

                for (int i = 0; i < RestaurantSearchFragment.restaurants.size(); i++) {
                    Restaurant r = RestaurantSearchFragment.restaurants.get(i);
                    if (r.isSelected()) {
                        selectedRestaurants.add(r.getId()); //If the restaurant is selected, add the restaurant id to the list.
                    }
                }
                Button btnFinished = (Button) getParentFragment().getView().findViewById(R.id.btnFinished);
                if (selectedRestaurants.size() > 0) {
                    btnFinished.setVisibility(View.VISIBLE);
                } else {
                    btnFinished.setVisibility(View.GONE);
                }

            }
        });

    }

    public void centerCamera() { //Center the camera on your current location.
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        map.moveCamera(center);
        map.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    public void dropPin(final Marker marker) {
        final android.os.Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);
                if (t > 0.0) {
                    handler.postDelayed(this, 15);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
