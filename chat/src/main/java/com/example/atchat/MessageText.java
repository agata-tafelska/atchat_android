package com.example.atchat;

public class MessageText {

    private String date;
    private String user;
    private String message;

    public void setDate(String date) {
        this.date = date;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public MessageText(String date, String user, String message) {
        this.date = date;
        this.user = user;
        this.message = message;
    }
}
