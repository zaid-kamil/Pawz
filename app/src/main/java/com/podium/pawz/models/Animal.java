package com.podium.pawz.models;

public class Animal {
    public String uid;
    public String imageStr;
    public String userPhone;
    public String petType;
    public String title;
    public Double latitude;
    public Double longitude;
    public String message;
    public String username;

    public Animal(String imageStr, String userPhone, String petType, String title, String message, String username, String uid, Double latitude, Double longitude) {
        this.imageStr = imageStr;
        this.userPhone = userPhone;
        this.petType = petType;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.username = username;
        this.uid = uid;
    }

    public Animal() {
    }
}
