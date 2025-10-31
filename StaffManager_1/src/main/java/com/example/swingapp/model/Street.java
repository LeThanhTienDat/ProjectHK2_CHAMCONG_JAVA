package com.example.swingapp.model;

public class Street {
    private int id;
    private String streetName;

    public Street() {}

    public Street(int id, String streetName) {
        this.id = id;
        this.streetName = streetName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String toString() {
        return streetName;
    }
}
