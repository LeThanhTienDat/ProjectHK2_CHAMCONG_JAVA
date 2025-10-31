package com.example.swingapp.model;

import java.sql.Date;
import java.sql.Time;

public class WorkSchedule {
    private int id;
    private int employeeId;
    private int shiftId;
    private Date workDate;
    private boolean comeLate;
    private boolean earlyLeave;
    private int absentId;
    private double timeWork;
    private double totalOt;
    private Time checkInTime;
    private Time checkOutTime;

    public WorkSchedule() {}

    public WorkSchedule(int id, int employeeId, int shiftId, Date workDate,
                        boolean comeLate, boolean earlyLeave, int absentId,
                        double timeWork, double totalOt, Time checkInTime, Time checkOutTime) {
        this.id = id;
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.workDate = workDate;
        this.comeLate = comeLate;
        this.earlyLeave = earlyLeave;
        this.absentId = absentId;
        this.timeWork = timeWork;
        this.totalOt = totalOt;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public boolean isComeLate() {
        return comeLate;
    }

    public void setComeLate(boolean comeLate) {
        this.comeLate = comeLate;
    }

    public boolean isEarlyLeave() {
        return earlyLeave;
    }

    public void setEarlyLeave(boolean earlyLeave) {
        this.earlyLeave = earlyLeave;
    }

    public int getAbsentId() {
        return absentId;
    }

    public void setAbsentId(int absentId) {
        this.absentId = absentId;
    }

    public double getTimeWork() {
        return timeWork;
    }

    public void setTimeWork(double timeWork) {
        this.timeWork = timeWork;
    }

    public double getTotalOt() {
        return totalOt;
    }

    public void setTotalOt(double totalOt) {
        this.totalOt = totalOt;
    }

    public Time getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Time checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Time getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Time checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
