package com.dscnitp.freshersportal.Model;

import java.util.List;

public class ModelGroupChats {
String sender;
    String message;

    public ModelGroupChats() {
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public List<Boolean> getSeeen() {
        return seeen;
    }

    public void setSeeen(List<Boolean> seeen) {
        this.seeen = seeen;
    }

    List<Boolean> seeen;


    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ModelGroupChats(String sender, String message, String timestamp, String type,List<Boolean> seeen) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public ModelGroupChats(String sender, String message, List<Boolean> seeen, String timestamp, String type) {
        this.sender = sender;
        this.message = message;
        this.seeen = seeen;
        this.timestamp = timestamp;
        this.type = type;
    }

    String timestamp;
    String type;
}
