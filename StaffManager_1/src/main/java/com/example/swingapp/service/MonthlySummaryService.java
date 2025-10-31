package com.example.swingapp.service;

import com.example.swingapp.dao.MonthlySummaryDAO;
import com.example.swingapp.model.MonthlySummary;
import java.util.List;

public class MonthlySummaryService {
    private final MonthlySummaryDAO dao = new MonthlySummaryDAO();

    public List<MonthlySummary> getAll() { return dao.getAll(); }
    public boolean add(MonthlySummary m) { return dao.insert(m); }
    public boolean update(MonthlySummary m) { return dao.update(m); }
    public boolean delete(int id) { return dao.delete(id); }
}
