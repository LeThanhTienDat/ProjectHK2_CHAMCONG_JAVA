package com.example.swingapp.service;

import com.example.swingapp.dao.AbsentDAO;
import com.example.swingapp.model.Absent;
import java.util.List;

public class AbsentService {
    private final AbsentDAO dao = new AbsentDAO();

    public List<Absent> getAll() { return dao.getAll(); }
    public boolean add(Absent a) { return dao.insert(a); }
    public boolean update(Absent a) { return dao.update(a); }
    public boolean delete(int id) { return dao.delete(id); }
}
