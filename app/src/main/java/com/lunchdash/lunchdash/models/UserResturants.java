package com.lunchdash.lunchdash.models;

public class UserResturants {

    private String userId;
    private String restaurantId;

    public UserResturants(String userId, String resturantId) {
        this.userId = userId;
        this.restaurantId = resturantId;
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
