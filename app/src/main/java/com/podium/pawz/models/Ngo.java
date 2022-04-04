package com.podium.pawz.models;

public class Ngo {

    public String name;
    public String phone1;
    public String phone2;
    public String phone3;
    public String address;
    public String website;

    public Ngo(String name, String phone1, String phone2, String phone3, String address, String website) {
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
        this.address = address;
        this.website = website;
    }

    public Ngo() {
    }
}