package com.example.my_application;

import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    private String text;
    private String userId;
    private String userName;
    private Object timestamp; // Use Object to handle both String and Timestamp
    private String paintingId;

    // Required empty constructor for Firebase
    public Comment() {}

    public Comment(String text, String userId, String userName, Timestamp timestamp, String paintingId) {
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.paintingId = paintingId;
    }

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPaintingId() { return paintingId; }
    public void setPaintingId(String paintingId) { this.paintingId = paintingId; }

    // Modified timestamp handling
    public Timestamp getTimestamp() {
        if (timestamp instanceof Timestamp) {
            return (Timestamp) timestamp;
        } else if (timestamp instanceof String) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse((String) timestamp);
                return new Timestamp(date);
            } catch (ParseException e) {
                return Timestamp.now(); // Fallback to current time if parsing fails
            }
        }
        return Timestamp.now(); // Default fallback
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
} 