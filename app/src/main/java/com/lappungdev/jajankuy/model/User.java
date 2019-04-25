package com.lappungdev.jajankuy.model;

public class User {

    private String userEmail;
    private String userID;
    private String userName;
    private String userPhone;
    private String userZID;
    private int userMeter;

    public User(String userEmail, String userID, String userName, String userPhone, String userZID, int userMeter) {
        this.setUserEmail(userEmail);
        this.setUserID(userID);
        this.setUserName(userName);
        this.setUserPhone(userPhone);
        this.setUserZID(userZID);
        this.setUserMeter(userMeter);
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserZID() {
        return userZID;
    }

    public void setUserZID(String userZID) {
        this.userZID = userZID;
    }

    public int getUserMeter() {
        return userMeter;
    }

    public void setUserMeter(int userMeter) {
        this.userMeter = userMeter;
    }
}
