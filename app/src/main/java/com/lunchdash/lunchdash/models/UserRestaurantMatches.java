package com.lunchdash.lunchdash.models;

import com.lunchdash.lunchdash.datastore.UserRestaurantMatchesTable;

import java.io.Serializable;

public class UserRestaurantMatches implements Serializable {

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DENIED = "denied";
    public static final String STATUS_UNCHANGED = "noaction";

    private String id;
    private String reqUserId;
    private String matchedUserID;
    private String restaurantId;
    private String reqStatus;
    private String matchedStatus;
    private String matchedName;
    private String restaurantName;
    private String reqUserName;


    public UserRestaurantMatches() {
        setMatchedStatus(STATUS_WAITING);
        setReqStatus(STATUS_WAITING);
    }

    public UserRestaurantMatches(UserRestaurantMatchesTable urmt) {
        setId(urmt.getObjectId());
        setReqUserId(urmt.getRequesterId());
        setMatchedUserID(urmt.getMatchedUserId());
        setRestaurantId(urmt.getRestaurantId());
        setMatchedStatus(urmt.getMatchedStatus());
        setReqStatus(urmt.getRequesterStatus());
        setMatchedName(urmt.getMatchedUserName());
        setRestaurantName(urmt.getRestaurantName());
        setReqUserName(urmt.getRequesterUserName());

    }

    public String getReqUserName() {
        return reqUserName;
    }

    public void setReqUserName(String reqUserName) {
        this.reqUserName = reqUserName;
    }



    public String getMatchedName() {
        return matchedName;
    }

    public void setMatchedName(String matchedName) {
        this.matchedName = matchedName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
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

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getMatchedStatus() {
        return matchedStatus;
    }

    public void setMatchedStatus(String matchedStatus) {
        this.matchedStatus = matchedStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
