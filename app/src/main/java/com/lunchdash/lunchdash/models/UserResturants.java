package com.lunchdash.lunchdash.models;

import android.util.Log;

import com.lunchdash.lunchdash.datastore.UserResturantsTable;
import com.lunchdash.lunchdash.datastore.UserTable;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by nandinimargada on 3/14/15.
 */
public class UserResturants {

    private String userid;
    private String resturantId;
    public UserResturants(String userId, String resturantId){
        this.userid = userId;
        this.resturantId = resturantId;
    }

    public void save(){
        UserResturantsTable urt ;
        urt = getUserResturantTable();
        if(urt == null){
            urt = new UserResturantsTable();
        }
        urt.setUserid(userid);
        urt.setResturantid(resturantId);
        urt.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("TAG", "user resturant saved");
            }
        });

    }

    private UserResturantsTable getUserResturantTable() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserResturantsTable");
        query.whereEqualTo(UserResturantsTable.USER_ID, userid);
        query.whereEqualTo(UserResturantsTable.RESTURANT_ID, resturantId);

        try {
            UserResturantsTable userResturant = (UserResturantsTable) query.getFirst();
            return userResturant;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
