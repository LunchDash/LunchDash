package com.lunchdash.lunchdash.models;

import android.util.Log;

import com.lunchdash.lunchdash.APIs.ParseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Restaurant {
    //https://www.yelp.com/developers/documentation/v2/search_api for sample JSON response
    private String phoneNumber;
    private String id;
    private String imageURL;
    private String address[];
    private String city;
    private String country;
    private String displayAddress[]; //This is an array where each item represents one line of the address
    private String zipcode;
    private String state;
    private String mobileUrl;
    private String name;
    private String ratingImgUrl;
    private String ratingImgUrl_Large;
    private String ratingImgUrl_Small;
    private double rating;
    private int reviewCount;
    private String snippetImageUrl;
    private String snippetText;
    private double distance;
    private double latitude;
    private double longitude;

    public int getNumOfUsersInterested() {
        return numOfUsersInterested;
    }

    public void setNumOfUsersInterested(int numOfUsersInterested) {
        this.numOfUsersInterested = numOfUsersInterested;
    }

    private int  numOfUsersInterested;

    private String[][] categories; //Array that contains 2 item arrays that have name/alias eg: [["Local Flavor", "localflavor"], ["Active Life", "active"], ["Mass Media", "massmedia"]]

    private boolean selected = false;

    public static Restaurant fromJSON(JSONObject jo) {
        Restaurant restaurant = new Restaurant();
        try {
            restaurant.phoneNumber = jo.optString("display_phone");
            restaurant.id = jo.getString("id");
            restaurant.imageURL = jo.getString("image_url");

            JSONArray tempArray = jo.getJSONObject("location").getJSONArray("address");
            int arrlength = tempArray.length();
            restaurant.address = new String[arrlength];
            for (int i = 0; i < arrlength; i++) {
                restaurant.address[i] = tempArray.getString(i);
            }

            JSONObject location = jo.getJSONObject("location");

            restaurant.city = location.getString("city");
            restaurant.country = location.getString("country_code");

            tempArray = location.getJSONArray("display_address");
            arrlength = tempArray.length();
            restaurant.displayAddress = new String[arrlength];
            for (int i = 0; i < arrlength; i++) {
                restaurant.displayAddress[i] = tempArray.getString(i);
            }

            restaurant.zipcode = location.getString("postal_code");
            restaurant.state = location.getString("state_code");
            restaurant.latitude = location.getJSONObject("coordinate").getDouble("latitude");
            restaurant.longitude = location.getJSONObject("coordinate").getDouble("longitude");
            restaurant.mobileUrl = jo.getString("mobile_url");
            restaurant.name = jo.getString("name");
            restaurant.ratingImgUrl = jo.getString("rating_img_url");
            restaurant.ratingImgUrl_Large = jo.getString("rating_img_url_large");
            restaurant.ratingImgUrl_Small = jo.getString("rating_img_url_small");
            restaurant.rating = jo.getDouble("rating");
            restaurant.reviewCount = jo.getInt("review_count");
            restaurant.snippetImageUrl = jo.getString("snippet_image_url");
            restaurant.snippetText = jo.getString("snippet_text");
            restaurant.distance = jo.getDouble("distance"); //Won't be there unless we specified longitude/latitude
            restaurant.snippetText = jo.getString("snippet_text");

            tempArray = jo.getJSONArray("categories");
            arrlength = tempArray.length();
            restaurant.categories = new String[arrlength][2];
            for (int i = 0; i < arrlength; i++) {
                restaurant.categories[i][0] = tempArray.getJSONArray(i).getString(0);
                restaurant.categories[i][1] = tempArray.getJSONArray(i).getString(1);
            }

            // Try to get the number of users interested in this restaurant.
            restaurant.numOfUsersInterested = ParseClient.getUserCountForResturant(restaurant.id);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return restaurant;
    }

    public static ArrayList<Restaurant> fromJSONArray(JSONArray jsonArray) {
        Log.d("DEBUG", jsonArray.toString());
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject restaurantJSON = jsonArray.getJSONObject(i);
                Restaurant restaurant = fromJSON(restaurantJSON);
                restaurants.add(restaurant);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return restaurants;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String[] getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String[] getDisplayAddress() {
        return displayAddress;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getState() {
        return state;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public String getName() {
        return name;
    }

    public String getRatingImgUrl() {
        return ratingImgUrl;
    }

    public String getRatingImgUrl_Large() {
        return ratingImgUrl_Large;
    }

    public String getRatingImgUrl_Small() {
        return ratingImgUrl_Small;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getSnippetImageUrl() {
        return snippetImageUrl;
    }

    public String getSnippetText() {
        return snippetText;
    }

    public String[][] getCategories() {
        return categories;
    }

    public double getDistance() {
        return distance;
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public void toggleSelected() {
        selected = !selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}