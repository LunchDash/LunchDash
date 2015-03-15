package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserRestaurantMatchesTable")
public class UserRestaurantMatchesTable extends ParseObject {

    public final static String REQUESTER_USER_ID = "reqUserId";
    public final static String MATCHED_USER_ID = "matchedUserID";
    public final static String RESTAURANT_ID = "restaurantId";
    public final static String REQUESTER_STATUS = "reqStatus";
    public final static String MATCHED_STATUS = "matchedStatus";


    public String getRequesterId() {
        return getString(REQUESTER_USER_ID);
    }

    public void setRequesterId(String requesterId) {
        put(REQUESTER_USER_ID, requesterId);
    }

    public String getMatchedUserId() {
        return getString(MATCHED_USER_ID);
    }

    public void setMatchedUserId(String matchedUserId) {
        put(MATCHED_USER_ID, matchedUserId);
    }

    public String getRestaurantId() {
        return getString(RESTAURANT_ID);
    }

    public void setRestaurantId(String restaurantId) {
        put(RESTAURANT_ID, restaurantId);
    }

    public String getRequesterStatus() {
        return getString(REQUESTER_STATUS);
    }

    public void setRequesterStatus(String requesterStatus) {
        put(REQUESTER_STATUS, requesterStatus);
    }

    public String getMatchedStatus() {
        return getString(MATCHED_STATUS);
    }

    public void setMatchedStatus(String matchedStatus) {
        put(MATCHED_STATUS, matchedStatus);
    }

}
