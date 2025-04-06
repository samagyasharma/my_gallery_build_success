package com.example.my_application;

import android.os.Parcel;
import android.os.Parcelable;

public class Painting implements Parcelable {
    private final String name;
    private final int imageResId; // For local images
    private final String imageUrl; // For online images
    private final String description;
    private final String artist; // Add artist field
    private int price;
    private boolean isSelected;

    public Painting(String name, int imageResId, String imageUrl, String description, String artist, int price) {
        this.name = name;
        this.imageResId = imageResId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.artist = artist;
        this.price = price;
    }

    protected Painting(Parcel in) {
        name = in.readString();
        imageResId = in.readInt();
        imageUrl = in.readString();
        description = in.readString();
        artist = in.readString();
        price = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Painting> CREATOR = new Parcelable.Creator<Painting>() {
        @Override
        public Painting createFromParcel(Parcel in) {
            return new Painting(in);
        }

        @Override
        public Painting[] newArray(int size) {
            return new Painting[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getArtist() {
        return artist;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(imageResId);
        parcel.writeString(imageUrl);
        parcel.writeString(description);
        parcel.writeString(artist);
        parcel.writeInt(price);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Painting painting = (Painting) obj;
        
        // Compare names first
        if (!name.equals(painting.name)) return false;
        
        // If both have URLs, compare them
        if (imageUrl != null && painting.imageUrl != null) {
            return imageUrl.equals(painting.imageUrl);
        }
        
        // If no URLs, compare resource IDs
        return imageResId == painting.imageResId;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + imageResId;
        return result;
    }
}
