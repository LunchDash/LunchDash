package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by chandrav on 3/12/15.
 */
@ParseClassName("UserResturantsTable")
public class UserResturantsTable extends ParseObject{
    public final static String USER_ID = "userId";
    public final static String RESTURANT_ID = "resturantId";
    
    public String getUserid() {
        return getString(USER_ID);
    }

    public void setUserid(String userid) {
        put(USER_ID, userid);
    }

    public String getResturantid() {
        return getString(RESTURANT_ID);
    }

    public void setResturantid(String resturantid) {
        put( RESTURANT_ID, resturantid);
    }

}
