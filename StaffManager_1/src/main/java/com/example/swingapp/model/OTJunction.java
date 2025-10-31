package com.example.swingapp.model;

import java.sql.Time;

public class OTJunction {
    private int id;
    private int workScheduleId;
    private int otTypeId;
    private Time otCheckInTime;
    private Time otCheckOutTime;
    private boolean otConfirm;

    public OTJunction() {}

    public OTJunction(int id, int workScheduleId, int otTypeId, Time otCheckInTime, Time otCheckOutTime, boolean otConfirm) {
        this.id = id;
        this.workScheduleId = workScheduleId;
        this.otTypeId = otTypeId;
        this.otCheckInTime = otCheckInTime;
        this.otCheckOutTime = otCheckOutTime;
        this.otConfirm = otConfirm;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getWorkScheduleId() {
        return workScheduleId;
    }
    public void setWorkScheduleId(int workScheduleId) {
        this.workScheduleId = workScheduleId;
    }

    public int getOtTypeId() {
        return otTypeId;
    }
    public void setOtTypeId(int otTypeId) {
        this.otTypeId = otTypeId;
    }

    public Time getOtCheckInTime() {
        return otCheckInTime;
    }
    public void setOtCheckInTime(Time otCheckInTime) {
        this.otCheckInTime = otCheckInTime;
    }

    public Time getOtCheckOutTime() {
        return otCheckOutTime;
    }
    public void setOtCheckOutTime(Time otCheckOutTime) {
        this.otCheckOutTime = otCheckOutTime;
    }

    public boolean isOtConfirm() {
        return otConfirm;
    }
    public void setOtConfirm(boolean otConfirm) {
        this.otConfirm = otConfirm;
    }

    @Override
    public String toString() {
        return "OTJunction{" +
                "id=" + id +
                ", workScheduleId=" + workScheduleId +
                ", otTypeId=" + otTypeId +
                ", otCheckInTime=" + otCheckInTime +
                ", otCheckOutTime=" + otCheckOutTime +
                ", otConfirm=" + otConfirm +
                '}';
    }
}
