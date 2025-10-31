package com.example.swingapp.service;

import com.example.swingapp.dao.RestaurantDAO;
import com.example.swingapp.model.Restaurant;
import java.util.List;

public class RestaurantService {
    private final RestaurantDAO dao = new RestaurantDAO();

    public List<Restaurant> getAll() { return dao.getAll(); }
    public boolean add(Restaurant r) { return dao.insert(r); }
    public boolean update(Restaurant r) { return dao.update(r); }
    public boolean delete(int id) { return dao.delete(id); }
}
