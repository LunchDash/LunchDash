package com.lunchdash.lunchdash.models;


import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class User {
    private String userId;
    private String imageUrl;
    private String status;
    private String email;
    private String name;

    private static User user = null;

    public User() {
    }

    public static User getUserInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void save() {
        UserTable ut = new UserTable();
        ut.setUserId(userId);
        ut.setUserName(name);
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
