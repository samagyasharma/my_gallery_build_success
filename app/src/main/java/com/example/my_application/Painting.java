package com.example.my_application;

import android.os.Parcel;
import android.os.Parcelable;

public class Painting implements Parcelable {
    private String title;
    private String artist;
    private String price;
    private String description;
    private String imageUrl;
    private int imageResId;
    private boolean isSelected;

    // Constructor for URL-based paintings
    public Painting(String title, String artist, String price, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageResId = 0;
        this.description = "";
    }

    // Constructor for resource-based paintings
    public Painting(String title, String artist, String price, int imageResId) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.imageUrl = "";
        this.imageResId = imageResId;
        this.description = "";
    }

    // Parcelable constructor
    protected Painting(Parcel in) {
        title = in.readString();
        artist = in.readString();
        price = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        imageResId = in.readInt();
        isSelected = in.readByte() != 0;
    }

    // Parcelable CREATOR
    public static final Creator<Painting> CREATOR = new Creator<Painting>() {
        @Override
        public Painting createFromParcel(Parcel in) {
            return new Painting(in);
        }

        @Override
        public Painting[] newArray(int size) {
            return new Painting[size];
        }
    };

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeInt(imageResId);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public boolean isUrlBased() { return imageUrl != null && !imageUrl.isEmpty(); }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
