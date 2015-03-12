package com.lunchdash.lunchdash.models;

import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by chandrav on 3/11/15.
 */
public class User {
    private String userId;
    private String userName;
    private String imageUrl;
    private String email;
    private String status;
    
    public User(String userId, String userName, String imageUrl, String email, String status){
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.email = email;
        this.userName = userName;
    }
    
    public void save(){
        UserTable ut = new UserTable();
        ut.setUserId(userId);
        ut.setUserName(userName);
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
}
