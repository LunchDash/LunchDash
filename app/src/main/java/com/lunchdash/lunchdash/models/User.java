package com.lunchdash.lunchdash.models;


import com.lunchdash.lunchdash.datastore.UserTable;


public class User {
    private String userId;
    private String imageUrl;
    private String status;
    private String email = "email";
    private String name = "name";
    private String phoneNumber = "1234567890";

    public User(UserTable ut) {
        this.userId = ut.getString(UserTable.USER_ID);
        this.imageUrl = ut.getString(UserTable.USER_IMAGE_URL);
        this.status = ut.getString(UserTable.USER_STATUS);
        this.email = ut.getString(UserTable.USER_EMAIL);
        this.name = ut.getString(UserTable.USER_NAME);
    }

    public User() {
        setStatus("Waiting");
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

