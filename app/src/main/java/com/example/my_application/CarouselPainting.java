package com.example.my_application;

public class CarouselPainting {
    private int imageResourceId;
    private String title;
    private String artist;
    private String description;
    private double price;

    public CarouselPainting(int imageResourceId, String title, String artist, String description, double price) {
        this.imageResourceId = imageResourceId;
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.price = price;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
} 