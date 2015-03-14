package com.lunchdash.lunchdash.APIs;

import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class ParseClient {

    private static UserTable getUserTable(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
        query.whereEqualTo(UserTable.USER_ID, userId);

        try {
            UserTable user = (UserTable) query.getFirst();
            return user;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void saveUser(User user) { //Called after the info is set.
        UserTable ut;
        ut = getUserTable(user.getUserId());
        if (ut == null) {
            ut = new UserTable();
        }
        ut.setUserId(user.getUserId());
        ut.setName(user.getName());
        ut.setImageUrl(user.getImageUrl());
        ut.setEmail(user.getEmail());
        ut.setStatus(user.getStatus());
        ut.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("TAG", "user saved");
            }
        });
    }

    public static void saveUserRestaurantPair(UserRestaurants ur) {
        UserRestaurantsTable urt;
        urt = getUserRestaurantTable(ur);
        if (urt == null) {
            urt = new UserRestaurantsTable();
        }
        urt.setUserId(ur.getUserId());
        urt.setRestaurantId(ur.getRestaurantId());
        urt.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("TAG", "user restaurant saved");
            }
        });

    }

    private static UserRestaurantsTable getUserRestaurantTable(UserRestaurants ur) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo(UserRestaurantsTable.USER_ID, ur.getUserId());
        query.whereEqualTo(UserRestaurantsTable.RESTAURANT_ID, ur.getRestaurantId());

        try {
            UserRestaurantsTable userRestaurant = (UserRestaurantsTable) query.getFirst();
            return userRestaurant;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteURPairsWithId(String userId) throws ParseException { //Delete all User-Restaurant pairs in Parse that match a specified userId
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo("userId", userId);
        List<ParseObject> results = query.find();
        for (ParseObject result : results) {
            result.delete();
        }
    }


    public static void deleteUserRestaurantPairs(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserResturantsTable");
        query.whereEqualTo(UserRestaurantsTable.USER_ID, userId);
        try {
            List<ParseObject> urt = query.find();
            for(int i = 0; i < urt.size(); i++){
                ParseObject ur = urt.get(i);
                ur.delete();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
