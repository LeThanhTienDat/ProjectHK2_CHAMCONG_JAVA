package com.example.swingapp.service;

import com.example.swingapp.dao.WorkScheduleDAO;
import com.example.swingapp.model.WorkSchedule;
import java.util.List;

public class WorkScheduleService {
    private final WorkScheduleDAO dao = new WorkScheduleDAO();

    public List<WorkSchedule> getAll() {
        return dao.getAll();
    }

    public boolean add(WorkSchedule w) {
        return dao.insert(w);
    }

    public boolean update(WorkSchedule w) {
        return dao.update(w);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }
}
