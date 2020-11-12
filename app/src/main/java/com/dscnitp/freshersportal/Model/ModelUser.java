package com.dscnitp.freshersportal.Model;

public class ModelUser {
    String name;
    boolean isBlocked;

    public ModelUser() {
    }

    String onlineStatus;
    String typingTo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setImage(String image) {
        this.imgUrl = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getAboutYou() {
        return aboutYou;
    }


    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String id;
    String aboutYou;
    public ModelUser(String name, String email, String image,String id, String uid) {
        this.name = name;
        this.isBlocked = isBlocked;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.email = email;
        this.imgUrl = image;
        this.id=id;
        this.cover = cover;
        this.phone = phone;
        this.uid = uid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    String email;

    String imgUrl;
    String cover;


    String phone;
    String uid;
}
