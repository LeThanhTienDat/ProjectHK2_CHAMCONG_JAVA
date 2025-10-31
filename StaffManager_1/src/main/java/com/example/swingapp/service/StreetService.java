package com.example.swingapp.service;

import com.example.swingapp.dao.StreetDAO;
import com.example.swingapp.model.Street;
import java.util.List;

public class StreetService {
    private final StreetDAO dao = new StreetDAO();

    public List<Street> getAll() { return dao.getAll(); }
    public boolean add(Street s) { return dao.insert(s); }
    public boolean update(Street s) { return dao.update(s); }
    public boolean delete(int id) { return dao.delete(id); }
}
