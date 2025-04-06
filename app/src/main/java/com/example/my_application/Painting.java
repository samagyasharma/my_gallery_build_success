package com.example.my_application;

import android.os.Parcel;
import android.os.Parcelable;

public class Painting implements Parcelable {
    private String title;
    private String artist;
    private String price;
    private String imageUrl;
    private String description;
    private int imageResId;
    private boolean selected;

    // Constructor for URL-based images
    public Painting(String title, String artist, String price, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageResId = 0;
        this.selected = false;
    }

    // Constructor for resource-based images
    public Painting(String title, String artist, String price, int imageResId) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.imageResId = imageResId;
        this.imageUrl = "";
        this.selected = false;
    }

    // Parcelable constructor
    protected Painting(Parcel in) {
        title = in.readString();
        artist = in.readString();
        price = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        imageResId = in.readInt();
        selected = in.readByte() != 0;
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
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeInt(imageResId);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
    
    public boolean isUrlBased() { return !imageUrl.isEmpty(); }
}
