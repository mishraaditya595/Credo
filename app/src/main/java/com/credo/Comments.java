package com.credo;

import java.util.Date;

public class Comments {

    private String message, userID;
    private Date timestamp;

    public Comments() {}

    public Comments(String message, String UID, Date timestamp) {
        this.message = message;
        this.userID = UID;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
