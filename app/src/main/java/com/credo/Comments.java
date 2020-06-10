package com.credo;

import java.util.Date;

public class Comments {

    private String message, UID;
    private Date timestamp;

    public Comments() {}

    public Comments(String message, String UID, Date timestamp) {
        this.message = message;
        this.UID = UID;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
