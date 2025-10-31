package com.example.swingapp.service;

import com.example.swingapp.dao.OTTypeDAO;
import com.example.swingapp.model.OTType;
import java.util.List;

public class OTTypeService {
    private final OTTypeDAO dao = new OTTypeDAO();

    public List<OTType> getAll() { return dao.getAll(); }
    public boolean add(OTType o) { return dao.insert(o); }
    public boolean update(OTType o) { return dao.update(o); }
    public boolean delete(int id) { return dao.delete(id); }
}
