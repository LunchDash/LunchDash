package com.lunchdash.lunchdash.models;

import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;
import com.lunchdash.lunchdash.datastore.UserRestaurantsTable;

/**
 * Created by nandinimargada on 3/14/15.
 */
public class UserRestaurantMatches {

    public UserRestaurantMatches(){
        setMatchedStatus(false);
        setReqStatus(false);
    }

    public UserRestaurantMatches(UserRestaurantMatchesTable urmt){
        setReqUserId(urmt.getRequesterId());
        setMatchedUserID(urmt.getMatchedUserId());
        setRestaurantId(urmt.getRestaurantId());
        setMatchedStatus(urmt.isMatchedStatus());
        setReqStatus(urmt.isRequesterStatus());
    }


    public String getReqUserId() {
        return reqUserId;
    }

    public void setReqUserId(String reqUserId) {
        this.reqUserId = reqUserId;
    }

    public String getMatchedUserID() {
        return matchedUserID;
    }

    public void setMatchedUserID(String matchedUserID) {
        this.matchedUserID = matchedUserID;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(boolean reqStatus) {
        this.reqStatus = reqStatus;
    }

    public boolean isMatchedStatus() {
        return matchedStatus;
    }

    public void setMatchedStatus(boolean matchedStatus) {
        this.matchedStatus = matchedStatus;
    }

    private String reqUserId;
    private String matchedUserID;
    private String restaurantId;
    private boolean reqStatus;
    private boolean matchedStatus;

}
