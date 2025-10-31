package com.example.swingapp.model;

public class EmployeeRestaurant {
    private int id;
    private int employeeId;
    private int restaurantId;

    public EmployeeRestaurant() {}

    public EmployeeRestaurant(int id, int employeeId, int restaurantId) {
        this.id = id;
        this.employeeId = employeeId;
        this.restaurantId = restaurantId;
    }

    // Getters and Setters
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

    public int getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public String toString() {
        return "EmployeeRestaurant{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", restaurantId=" + restaurantId +
                '}';
    }
}
