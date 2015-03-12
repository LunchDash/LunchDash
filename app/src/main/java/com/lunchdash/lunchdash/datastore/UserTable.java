package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by chandrav on 3/11/15.
 */
@ParseClassName("UserTable")
public class UserTable extends ParseObject {
    public String getUserid(){
        return getString("userId");
    }
    
    public void setUserId(String userid){
        put ("userId", userid);
    }
    
    public String getUserName() {
        return getString("userName");        
    }
    public void setUserName(String userName){
        put ("userName",userName );
        
    }
    
    public String getEmail(){
        return getString("email");
        
    }
    public void setEmail(String email){
        put ("email", email);
        
    }
    
    public String getImageUrl(){
        return getString("imageUrl");
    }
    
    public void setImageUrl(String imageUrl){
        put("imageUrl", imageUrl);
    }
    
    public String getStatus(){
        return getString("status");
    }
    
    public void setStatus(String status){
        put ("status", status);
    }
}
