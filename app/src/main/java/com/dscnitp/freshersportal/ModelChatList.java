package com.dscnitp.freshersportal;

public class ModelChatList {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModelChatList() {
    }


    public ModelChatList(String datetime, String id) {
        this.datetime = datetime;
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    String datetime;
    String id;

}
