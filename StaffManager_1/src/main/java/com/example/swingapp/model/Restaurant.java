package com.example.swingapp.model;

public class Restaurant {
    private int id;
    private String name;
    private int streetId;

    public Restaurant() {}

    public Restaurant(int id, String name, int streetId) {
        this.id = id;
        this.name = name;
        this.streetId = streetId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStreetId() {
        return streetId;
    }

    public void setStreetId(int streetId) {
        this.streetId = streetId;
    }

    @Override
    public String toString() {
        return name;
    }
}
