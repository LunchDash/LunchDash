package com.lunchdash.lunchdash.APIs;

import android.util.Log;

import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.activities.ContactActivity;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.HashMap;
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
        ut.setUserCurrentLat(user.getCurrentLat());
        ut.setUserCurrentLon(user.getCurrentLon());
        try {
            ut.save();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("userid", ut.getUserid());
            installation.saveInBackground();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void saveLocation() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
        query.whereEqualTo("userId", LunchDashApplication.user.getUserId());
        ParseObject po = null;
        try {
            po = query.getFirst();
        } catch (ParseException e) {
        }
        po.put("currentLat", LunchDashApplication.user.getCurrentLat());
        po.put("currentLon", LunchDashApplication.user.getCurrentLon());
        po.saveInBackground();
    }

    public static void saveUserRestaurantPair(UserRestaurants ur) {
        UserRestaurantsTable urt;
        urt = getUserRestaurantTable(ur);
        if (urt == null) {
            urt = new UserRestaurantsTable();
        }
        urt.setUserId(ur.getUserId());
        urt.setRestaurantId(ur.getRestaurantId());
        urt.setRestaurantName(ur.getRestaurantName());
        try {
            urt.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, String> getUserProfile(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
        query.whereEqualTo("userId", userId);
        ParseObject po = null;
        try {
            po = query.getFirst();
        } catch (ParseException e) {
        }

        if (po != null) {
            String snippet = po.getString("snippet");
            String bio = po.getString("bio");
            HashMap<String, String> hm = new HashMap<>();
            hm.put("snippet", snippet);
            hm.put("bio", bio);
            return hm;
        } else {
            return null;
        }
    }

    public static void saveUserProfile(String userId, String snippet, String bio) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
        query.whereEqualTo("userId", userId);
        ParseObject po = null;
        try {
            po = query.getFirst();
        } catch (ParseException e) {
        }

        if (po == null) { //There's no previous entry, create a new one.
            ParseObject poProfile = new ParseObject("UserProfile");
            poProfile.put("userId", userId);
            poProfile.put("snippet", snippet);
            poProfile.put("bio", bio);
            poProfile.saveInBackground();
        } else { //Update previous entry.
            po.put("snippet", snippet);
            po.put("bio", bio);
            po.saveInBackground();
        }


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


    public static int getUserCountForRestaurant(String restaurantId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo(UserRestaurantsTable.RESTAURANT_ID, restaurantId);
        try {
            List<ParseObject> results = query.find();
            if (results != null && !results.isEmpty()) {
                return results.size();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void populateUsersResutaurantMatches(UserRestaurants userRestaurant) throws ParseException {
        //query users who also are interested in this restaurant.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo("restaurantId", userRestaurant.getRestaurantId());
        query.whereNotEqualTo("userId", userRestaurant.getUserId());
        List<ParseObject> results = query.find();
        for (ParseObject restaurant : results) {
            User matchedUser = ParseClient.getUser(((UserRestaurantsTable) restaurant).getUserId());
            UserRestaurantMatches match = new UserRestaurantMatches();
            match.setReqUserId(userRestaurant.getUserId());
            match.setMatchedUserID(matchedUser.getUserId());
            match.setRestaurantId(userRestaurant.getRestaurantId());
            match.setRestaurantName(userRestaurant.getRestaurantName());
            match.setMatchedName(matchedUser.getName());
            saveUserRestaurantMatch(match);
        }

    }

    private static UserRestaurantMatchesTable getUserRestaurantMatch(String reqId, String matchId, String restaurantId) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, reqId);
        query1.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, matchId);
        query1.whereEqualTo(UserRestaurantMatchesTable.RESTAURANT_ID, restaurantId);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query2.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, matchId);
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, reqId);
        query2.whereEqualTo(UserRestaurantMatchesTable.RESTAURANT_ID, restaurantId);

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));


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
        if (!urm.getReqStatus().equals(UserRestaurantMatches.STATUS_UNCHANGED)) {
            urmt.setRequesterStatus(urm.getReqStatus());
        }
        if (!urm.getMatchedStatus().equals(UserRestaurantMatches.STATUS_UNCHANGED)) {
            urmt.setMatchedStatus(urm.getMatchedStatus());
        }

        urmt.setMatchedUserName(urm.getMatchedName());
        urmt.setRestaurantName(urm.getRestaurantName());

        try {
            urmt.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRestaurantMatches(String userId) throws ParseException {

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, userId);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, userId);

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));
        List<ParseObject> results = null;
        try {
            results = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ParseObject result : results) {
            result.delete();
        }

    }

    public static String getUserStatus(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
        query.whereEqualTo("userId", userId);
        ParseObject po = null;
        try {
            po = query.getFirst();
        } catch (ParseException e) {
        }

        if (po != null) {
            return po.getString("status");
        } else {
            return "";
        }
    }

    public static void setUserStatus(String status) {
        if (status.equals("Waiting") || status.equals("Matching") || status.equals("Finished") || status.equals("None")) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTable");
            query.whereEqualTo("userId", LunchDashApplication.user.getUserId());
            ParseObject po = null;
            try {
                po = query.getFirst();
            } catch (ParseException e) {
                Log.d("APPDEBUG", "setting status failed due to " + e);
            }
            po.put("status", status);
            po.saveInBackground();
        } else {
            Log.e("APPDEBUG", "Trying to set an invalid user status!");
        }

    }


    public static void deleteUserSelections(String userId) {
        try {
            deleteRestaurantMatches(userId);
            deleteUserRestaurantPairs(userId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void clearUserRestaurantSelections(String userId) { //Removes all their entries from  UserRestaurantsTable
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo(UserRestaurantsTable.USER_ID, userId);
        try {
            List<ParseObject> matches = query.find();
            ParseObject.deleteAll(matches);
        } catch (ParseException e) {
        }


    }

    public static void saveChatMessage(String roomId, String userId, String message) {
        ChatMessageTable chatMessage = new ChatMessageTable();
        chatMessage.setChatRoomId(roomId);
        chatMessage.setUserId(userId);
        chatMessage.setMessageBody(message);
        try {
            chatMessage.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<ChatMessageTable> getChatMessages(String roomId) {
        ParseQuery<ChatMessageTable> query = ParseQuery.getQuery(ChatMessageTable.class);
        query.setLimit(ContactActivity.MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        query.whereEqualTo(ChatMessageTable.CHAT_ROOM_ID, roomId);

        try {
            List<ChatMessageTable> results = query.find();
            return results;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static UserRestaurantMatches getMatch(String matchid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query.whereEqualTo("objectId", matchid);

        try {
            UserRestaurantMatchesTable matchRow = (UserRestaurantMatchesTable) query.getFirst();
            UserRestaurantMatches match = new UserRestaurantMatches(matchRow);
            return match;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
