package com.lunchdash.lunchdash.models;

public class UserRestaurants {

    private String userId;
    private String restaurantId;
    private String resturantName;

    public UserRestaurants(String userId, String resturantId, String resturantName) {
        this.userId = userId;
        this.restaurantId = resturantId;
        this.resturantName = resturantName;
    }
    public String getResturantName() {
        return resturantName;
    }

    public void setResturantName(String resturantName) {
        this.resturantName = resturantName;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
