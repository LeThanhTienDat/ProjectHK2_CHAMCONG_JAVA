package com.example.swingapp.model;

import java.sql.Date;

public class Contract {
    private int contractId;
    private int employeeId;
    private Date startDate;
    private Date endDate;
    private double salary;
    private String position;
    private String status;

    public Contract() {}

    public Contract(int contractId, int employeeId, Date startDate, Date endDate, double salary, String position, String status) {
        this.contractId = contractId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.position = position;
        this.status = status;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
