package com.example.my_application;

public class Comment {
    private String text;
    private String userId;
    private String userName;
    private String timestamp;
    private String paintingId;

    public Comment() {
        // Required empty constructor for Firebase
    }

    public Comment(String text, String userId, String userName, String timestamp, String paintingId) {
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.paintingId = paintingId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPaintingId() {
        return paintingId;
    }

    public void setPaintingId(String paintingId) {
        this.paintingId = paintingId;
    }
} 