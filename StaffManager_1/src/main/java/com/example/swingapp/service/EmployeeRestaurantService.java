package com.example.swingapp.service;

import com.example.swingapp.dao.EmployeeRestaurantDAO;
import com.example.swingapp.model.EmployeeRestaurant;
import java.util.List;

public class EmployeeRestaurantService {
    private final EmployeeRestaurantDAO dao = new EmployeeRestaurantDAO();

    public List<EmployeeRestaurant> getAll() { return dao.getAll(); }
    public boolean add(EmployeeRestaurant e) { return dao.insert(e); }
    public boolean update(EmployeeRestaurant e) { return dao.update(e); }
    public boolean delete(int id) { return dao.delete(id); }
}
