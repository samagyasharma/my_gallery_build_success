package com.example.my_application;

import android.os.Parcel;
import android.os.Parcelable;

public class Painting implements Parcelable {
    private String title;
    private String artist;
    private String price;
    private String medium;
    private String imageUrl;
    private int imageResId;
    private boolean isUrlBased;

    // Constructor for URL-based paintings
    public Painting(String title, String artist, String price, String medium, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.medium = medium;
        this.imageUrl = imageUrl;
        this.imageResId = 0;
        this.isUrlBased = true;
    }

    // Constructor for resource-based paintings
    public Painting(String title, String artist, String price, String medium, int imageResId) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.medium = medium;
        this.imageUrl = "";
        this.imageResId = imageResId;
        this.isUrlBased = false;
    }

    protected Painting(Parcel in) {
        title = in.readString();
        artist = in.readString();
        price = in.readString();
        medium = in.readString();
        imageUrl = in.readString();
        imageResId = in.readInt();
        isUrlBased = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(price);
        dest.writeString(medium);
        dest.writeString(imageUrl);
        dest.writeInt(imageResId);
        dest.writeByte((byte) (isUrlBased ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPrice() {
        return price;
    }

    public String getMedium() {
        return medium;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isUrlBased() {
        return isUrlBased;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Painting painting = (Painting) o;
        if (isUrlBased) {
            return title.equals(painting.title) && imageUrl.equals(painting.imageUrl);
        } else {
            return title.equals(painting.title) && imageResId == painting.imageResId;
        }
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (isUrlBased ? (imageUrl != null ? imageUrl.hashCode() : 0) : imageResId);
        return result;
    }
}
