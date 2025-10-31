package com.example.swingapp.model;

public class Absent {
    private int id;
    private String absentType;
    private String reason;
    private String description;

    public Absent() {}

    public Absent(int id, String absentType, String reason, String description) {
        this.id = id;
        this.absentType = absentType;
        this.reason = reason;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbsentType() {
        return absentType;
    }

    public void setAbsentType(String absentType) {
        this.absentType = absentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
