package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by chandrav on 3/12/15.
 */
@ParseClassName("UserResturanMatchesTable")
public class UserResturanMatchesTable  extends ParseObject{
    
    public final static String REQUESTER_USER_ID = "reqUserId";
    public final static String MATCHED_USER_ID = "matchedUserID";
    public final static String RESTURANT_ID = "resturanId";
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

    public String getResturantId() {
        return getString(RESTURANT_ID);
    }

    public void setResturantId(String resturantId) {
        put(RESTURANT_ID, resturantId);
    }

    public boolean isRequesterStatus() {
        return getBoolean(REQUESTER_STATUS);
    }

    public void setRequesterStatus(boolean requesterStatus) {
        put(REQUESTER_STATUS, requesterStatus);
    }

    public boolean isMatchedStatus() {
        return getBoolean(MATCHED_STATUS);
    }

    public void setMatchedStatus(boolean matchedStatus) {
        put(MATCHED_STATUS, matchedStatus);
    }

}
