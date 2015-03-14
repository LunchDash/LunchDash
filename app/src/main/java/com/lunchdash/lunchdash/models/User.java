package com.lunchdash.lunchdash.models;

import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

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
    
    private User(UserTable ut){
        this.userId = ut.getString(UserTable.USER_ID);
        this.imageUrl = ut.getString(UserTable.USER_IMAGE_URL);
        this.status = ut.getString(UserTable.USER_STATUS);
        this.email = ut.getString(UserTable.USER_EMAIL);
        this.userName = ut.getString(UserTable.USER_NAME);
    }
    
    public static User GetUser(String userId){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
        query.setLimit(1);
        query.whereEqualTo(UserTable.USER_ID, userId);

        try {
            List<ParseObject> users = query.find();
            UserTable user = (UserTable) users.get(0);
            User u = new User(user);
            return u;
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> users, ParseException e) {
//                if (e == null) {
//                    Log.d("score", "Retrieved " + users.size() + " users");
//                } else {
//                    Log.d("score", "Error: " + e.getMessage());
//                }
//            }
//        });
        return null;
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
