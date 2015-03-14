package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserTable")
public class UserTable extends ParseObject {

    /**
     * Coloums
     */
    public final static String USER_ID = "userId";
    public final static String USER_NAME = "userName";
    public final static String USER_EMAIL = "email";
    public final static String USER_IMAGE_URL = "imageUrl";
    public final static String USER_STATUS = "status";

    public String getUserid() {
        return getString(USER_ID);
    }

    public void setUserId(String userid) {
        put(USER_ID, userid);
    }

    public String getUserName() {
        return getString(USER_NAME);
    }

    public void setName(String name) {
        put(USER_NAME, name);

    }

    public String getEmail() {
        return getString(USER_EMAIL);

    }

    public void setEmail(String email) {
        put(USER_EMAIL, email);

    }

    public String getImageUrl() {
        return getString(USER_IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(USER_IMAGE_URL, imageUrl);
    }

    public String getStatus() {
        return getString(USER_STATUS);
    }

    public void setStatus(String status) {
        put(USER_STATUS, status);
    }
}
