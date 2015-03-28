package com.lunchdash.lunchdash.APIs;

import com.lunchdash.lunchdash.activities.ContactActivity;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.lunchdash.lunchdash.models.UserRestaurants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
        ut.setUserCurrentLat(user.getCurrentLat());
        ut.setUserCurrentLon(user.getCurrentLon());
        try {
            ut.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserRestaurantPair(UserRestaurants ur) {
        UserRestaurantsTable urt;
        urt = getUserRestaurantTable(ur);
        if (urt == null) {
            urt = new UserRestaurantsTable();
        }
        urt.setUserId(ur.getUserId());
        urt.setRestaurantId(ur.getRestaurantId());
        try {
            urt.save();
        } catch (ParseException e) {
            e.printStackTrace();
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


    public static int getUserCountForResturant(String resturantId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserRestaurantsTable");
        query.whereEqualTo(UserRestaurantsTable.RESTAURANT_ID, resturantId);
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

            UserRestaurantMatches match = new UserRestaurantMatches();
            match.setReqUserId(userRestaurant.getUserId());
            match.setMatchedUserID(((UserRestaurantsTable) restaurant).getUserId());
            match.setRestaurantId(userRestaurant.getRestaurantId());
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

    private static void deleteInactiveRestaurantMatches(String userId) throws ParseException {
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
            String status = (String) result.get(UserRestaurantMatchesTable.REQUESTER_STATUS);
            String status2 = (String) result.get(UserRestaurantMatchesTable.MATCHED_STATUS);
            if (status == null || status2 == null || !status.equals(UserRestaurantMatches.STATUS_ACCEPTED) || !status2.equals(UserRestaurantMatches.STATUS_ACCEPTED)) { //If either is not ACCEPTED, delete it.
                result.delete();
            }
        }

    }

    private static List<ParseObject> getUserRestaurantsMatches(String userId) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, userId);
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_STATUS, UserRestaurantMatches.STATUS_WAITING);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, userId);
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_STATUS, UserRestaurantMatches.STATUS_WAITING);

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));

        try {
            List<ParseObject> results = query.find();
            return results;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isActive(String userId, String matchedId, String restaurantId) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.RESTAURANT_ID, restaurantId);
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, userId);
        query1.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, matchedId);
        query1.whereNotEqualTo(UserRestaurantMatchesTable.REQUESTER_STATUS, UserRestaurantMatches.STATUS_DENIED);
        query1.whereNotEqualTo(UserRestaurantMatchesTable.MATCHED_STATUS, UserRestaurantMatches.STATUS_DENIED);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.RESTAURANT_ID, restaurantId);
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, matchedId);
        query1.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, userId);
        query1.whereNotEqualTo(UserRestaurantMatchesTable.REQUESTER_STATUS, UserRestaurantMatches.STATUS_DENIED);
        query1.whereNotEqualTo(UserRestaurantMatchesTable.MATCHED_STATUS, UserRestaurantMatches.STATUS_DENIED);

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));

        try {
            List<ParseObject> results = query.find();
            return results.size() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<UserRestaurantMatches> getUserMatches(String userId) {
        List<UserRestaurantMatches> matches = new ArrayList<UserRestaurantMatches>();
        List<ParseObject> results = getUserRestaurantsMatches(userId);
        for (ParseObject match : results) {
            UserRestaurantMatches matchObject = new UserRestaurantMatches(((UserRestaurantMatchesTable) match));
            matches.add(matchObject);
        }

        return matches;
    }

    public static UserRestaurantMatches getUserRestaurantMatchAccepted(String userId) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_USER_ID, userId);
        query1.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_STATUS, UserRestaurantMatches.STATUS_ACCEPTED);
        query1.whereEqualTo(UserRestaurantMatchesTable.MATCHED_STATUS, UserRestaurantMatches.STATUS_ACCEPTED);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserRestaurantMatchesTable");
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_USER_ID, userId);
        query2.whereEqualTo(UserRestaurantMatchesTable.REQUESTER_STATUS, UserRestaurantMatches.STATUS_ACCEPTED);
        query2.whereEqualTo(UserRestaurantMatchesTable.MATCHED_STATUS, UserRestaurantMatches.STATUS_ACCEPTED);


        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(query1, query2));

        try {
            UserRestaurantMatchesTable match = (UserRestaurantMatchesTable) query.getFirst();
            return new UserRestaurantMatches(match);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to be called when the app exits.
     *
     * @param userId
     */
    public static void deleteUserSelections(String userId) {
        try {
            deleteRestaurantMatches(userId);
            deleteUserRestaurantPairs(userId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void deleteInactiveUserSelections(String userId) { //Won't delete RestaurantPairs where both are Active because the second device may still need to look at it.
        try {
            deleteInactiveRestaurantMatches(userId);
            deleteUserRestaurantPairs(userId);
        } catch (ParseException e) {
            e.printStackTrace();
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

    public static boolean userHasProfile(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
        query.whereEqualTo("userId", userId);
        try {
            return query.find().size() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
