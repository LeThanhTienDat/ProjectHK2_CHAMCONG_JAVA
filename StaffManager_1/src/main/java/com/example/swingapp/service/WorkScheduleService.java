package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.WorkScheduleDAO;
import com.example.swingapp.model.WorkSchedule;

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
	public int addAndReturnId(WorkSchedule w) {
		return dao.addAndReturnId(w);
	}
	public WorkSchedule getById (int id) {
		return dao.getById(id);
	}
}
