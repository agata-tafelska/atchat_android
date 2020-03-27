package com.example.atchat;

public class MessageText {

    private String date;
    private String userName;
    private String message;
    private String userId;

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public MessageText(String date, String userName, String message, String userId) {
        this.date = date;
        this.userName = userName;
        this.message = message;
        this.userId = userId;
    }
}
