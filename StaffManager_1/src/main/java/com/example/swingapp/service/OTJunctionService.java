package com.example.swingapp.service;

import com.example.swingapp.dao.OTJunctionDAO;
import com.example.swingapp.model.OTJunction;
import java.util.List;

public class OTJunctionService {
    private final OTJunctionDAO dao = new OTJunctionDAO();

    public List<OTJunction> getAll() { return dao.getAll(); }
    public boolean add(OTJunction o) { return dao.insert(o); }
    public boolean update(OTJunction o) { return dao.update(o); }
    public boolean delete(int id) { return dao.delete(id); }
}
