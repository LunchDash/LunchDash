package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserRestaurantsTable")
public class UserRestaurantsTable extends ParseObject {
    public final static String USER_ID = "userId";
    public final static String RESTAURANT_ID = "restaurantId";
    public final static String RESTURANT_NAME = "resturantName";

    public String getResturantName() {
        return getString(RESTURANT_NAME);
    }

    public void setResturantName(String resturantName){
        put (RESTURANT_NAME, resturantName);
    }

    public String getUserId() {
        return getString(USER_ID);
    }

    public void setUserId(String userId) {
        put(USER_ID, userId);
    }

    public String getRestaurantId() {
        return getString(RESTAURANT_ID);
    }

    public void setRestaurantId(String restaurantId) {
        put(RESTAURANT_ID, restaurantId);
    }

}
