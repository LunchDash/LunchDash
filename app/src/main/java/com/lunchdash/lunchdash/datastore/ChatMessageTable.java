package com.lunchdash.lunchdash.datastore;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ChatMessageTable")
public class ChatMessageTable extends ParseObject{
    public final static String CHAT_ROOM_ID = "chatRoomId";
    public final static String USER_ID = "userId";
    public final static String MESSAGE_BODY = "message";

    public String getChatRoomId() {
        return getString(CHAT_ROOM_ID);
    }

    public void setChatRoomId(String chatRoomId) {
        put(CHAT_ROOM_ID, chatRoomId);
    }

    public String getUserId() {
        return  getString(USER_ID);
    }

    public void setUserId(String userId) {
        put(USER_ID, userId);
    }

    public String getMessageBody() {
        return getString(MESSAGE_BODY);
    }

    public void setMessageBody(String messageBody) {
        put(MESSAGE_BODY, messageBody);
    }


}
