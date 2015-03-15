package com.lunchdash.lunchdash.APIs;

import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static User getUser(String userId) {
        return new User(getUserTable(userId));
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
        ut.setPhoneNumber(user.getPhoneNumber());
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
        }

        return null;
    }

    public static void deleteUserRestaurantPairs(String userId) throws ParseException { //Delete all User-Restaurant pairs in Parse that match a specified userId
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo("userId", userId);
        List<ParseObject> results = query.find();
        for (ParseObject result : results) {
            result.delete();
        }
    }


    public static void populateUsersResutaurantMatches(UserRestaurants userRestaurant) throws ParseException {
        //query users who also are interested in this restaurant.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo("restaurantId", userRestaurant.getRestaurantId());
        query.whereNotEqualTo("userId", userRestaurant.getUserId());
        List<ParseObject> results = query.find();
        for (ParseObject restaurant : results) {

            UserRestaurantMatches match = new UserRestaurantMatches();
            match.setReqUserId(userRestaurant.getUserId());
            match.setMatchedUserID(((UserRestaurantsTable) restaurant).getUserId());
            match.setRestaurantId(userRestaurant.getRestaurantId());
            saveUserRestaurantMatch(match);
        }

    }

    private static UserRestaurantMatchesTable getUserRestaurantMatch(String reqId, String matchId, String resturantId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, reqId);
        query.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, matchId);
        query.whereEqualTo(UserRestaurantMatchesTable.RESTAURANT_ID, resturantId);
        try {
            UserRestaurantMatchesTable match = (UserRestaurantMatchesTable) query.getFirst();
            return match;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void saveUserRestaurantMatch(UserRestaurantMatches urm) {
        UserRestaurantMatchesTable urmt;
        urmt = getUserRestaurantMatch(urm.getReqUserId(), urm.getMatchedUserID(), urm.getRestaurantId());
        if (urmt == null) {
            urmt = new UserRestaurantMatchesTable();
        }
        urmt.setRequesterId(urm.getReqUserId());
        urmt.setMatchedUserId(urm.getMatchedUserID());
        urmt.setRestaurantId(urm.getRestaurantId());
        urmt.setRequesterStatus(urm.isReqStatus());
        urmt.setMatchedStatus(urm.isMatchedStatus());
        urmt.saveInBackground();

    }

    public static void deleteRestaurantMatches(String userId) throws ParseException {
        List<ParseObject> results = getUserRestaurantsMatches(userId);

        for (ParseObject result : results) {
            result.delete();
        }

    }

    private static List<ParseObject> getUserRestaurantsMatches(String userId) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo("reqUserId", userId);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query2.whereEqualTo("matchedUserID", userId);

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));

        try {
            List<ParseObject> results = query.find();
            return results;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<UserRestaurantMatches> getUserMatches(String userId){
        List<UserRestaurantMatches> matches = new ArrayList<UserRestaurantMatches>();
        List<ParseObject> results = getUserRestaurantsMatches(userId);
        for (ParseObject match : results) {
            UserRestaurantMatches matchObject = new UserRestaurantMatches(((UserRestaurantMatchesTable)match));
            matches.add(matchObject);
        }

        return matches;
    }


}
