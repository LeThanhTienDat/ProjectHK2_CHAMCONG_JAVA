package com.example.swingapp.model;

public class MonthlySummary {
    private int id;
    private int employeeId;
    private int totalShift;
    private double bonus;
    private double finalSalary;
    private String status;
    private int month;
    private int year;
    private int totalComeLate;
    private int totalEarlyLeave;
    private double totalOverTime;
    private double totalWorkTime;
    private double alUsed;
    private double totalWorkSalary;
    private double totalOtSalary;

    public MonthlySummary() {}

    public MonthlySummary(int id, int employeeId, int totalShift, double bonus, double finalSalary, String status,
                          int month, int year, int totalComeLate, int totalEarlyLeave, double totalOverTime,
                          double totalWorkTime, double alUsed, double totalWorkSalary, double totalOtSalary) {
        this.id = id;
        this.employeeId = employeeId;
        this.totalShift = totalShift;
        this.bonus = bonus;
        this.finalSalary = finalSalary;
        this.status = status;
        this.month = month;
        this.year = year;
        this.totalComeLate = totalComeLate;
        this.totalEarlyLeave = totalEarlyLeave;
        this.totalOverTime = totalOverTime;
        this.totalWorkTime = totalWorkTime;
        this.alUsed = alUsed;
        this.totalWorkSalary = totalWorkSalary;
        this.totalOtSalary = totalOtSalary;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public int getTotalShift() { return totalShift; }
    public void setTotalShift(int totalShift) { this.totalShift = totalShift; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getFinalSalary() { return finalSalary; }
    public void setFinalSalary(double finalSalary) { this.finalSalary = finalSalary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getTotalComeLate() { return totalComeLate; }
    public void setTotalComeLate(int totalComeLate) { this.totalComeLate = totalComeLate; }
    public int getTotalEarlyLeave() { return totalEarlyLeave; }
    public void setTotalEarlyLeave(int totalEarlyLeave) { this.totalEarlyLeave = totalEarlyLeave; }
    public double getTotalOverTime() { return totalOverTime; }
    public void setTotalOverTime(double totalOverTime) { this.totalOverTime = totalOverTime; }
    public double getTotalWorkTime() { return totalWorkTime; }
    public void setTotalWorkTime(double totalWorkTime) { this.totalWorkTime = totalWorkTime; }
    public double getAlUsed() { return alUsed; }
    public void setAlUsed(double alUsed) { this.alUsed = alUsed; }
    public double getTotalWorkSalary() { return totalWorkSalary; }
    public void setTotalWorkSalary(double totalWorkSalary) { this.totalWorkSalary = totalWorkSalary; }
    public double getTotalOtSalary() { return totalOtSalary; }
    public void setTotalOtSalary(double totalOtSalary) { this.totalOtSalary = totalOtSalary; }
}
