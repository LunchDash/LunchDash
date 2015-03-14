package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserRestaurantsTable")
public class UserRestaurantsTable extends ParseObject {
    public final static String USER_ID = "userId";
    public final static String RESTAURANT_ID = "restaurantId";

    public String getUserid() {
        return getString(USER_ID);
    }

    public void setUserid(String userid) {
        put(USER_ID, userid);
    }

    public String getResturantid() {
        return getString(RESTAURANT_ID);
    }

    public void setResturantid(String restaurantid) {
        put(RESTAURANT_ID, restaurantid);
    }

}
