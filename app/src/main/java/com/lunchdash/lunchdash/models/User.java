package com.lunchdash.lunchdash.models;


import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class User {
    private String userId;
    private String imageUrl;
    private String status;
    private String email;
    private String name;

    private User(UserTable ut) {
        this.userId = ut.getString(UserTable.USER_ID);
        this.imageUrl = ut.getString(UserTable.USER_IMAGE_URL);
        this.status = ut.getString(UserTable.USER_STATUS);
        this.email = ut.getString(UserTable.USER_EMAIL);
        this.name = ut.getString(UserTable.USER_NAME);
    }

    public User() {
        setStatus("Waiting");
    }

    public static User getUser(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
        query.setLimit(1);
        query.whereEqualTo(UserTable.USER_ID, userId);

        try {
            List<ParseObject> users = query.find();
            if (users.size() < 1) { //Return null if we don't find any matches.
                return null;
            }
            UserTable user = (UserTable) users.get(0);
            User u = new User(user);
            return u;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save() { //Called after the info is set.
        UserTable ut = new UserTable();
        ut.setUserId(userId);
        ut.setName(name);
        ut.setImageUrl(imageUrl);
        ut.setEmail(email);
        ut.setStatus(status);
        ut.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("TAG", "user saved");
            }
        });
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

}
