package com.example.swingapp.model;

import java.sql.Time;

public class Shift {
    private int id;
    private String shiftName;
    private Time startTime;
    private Time endTime;

    public Shift() {}

    public Shift(int id, String shiftName, Time startTime, Time endTime) {
        this.id = id;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
