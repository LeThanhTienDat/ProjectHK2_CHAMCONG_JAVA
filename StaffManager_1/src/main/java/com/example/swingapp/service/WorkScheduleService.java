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
	public boolean addShift(int workScheduleId, int shiftId) {
		return dao.updateShift(workScheduleId, shiftId);
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
	public boolean checkInShift(int workScheduleId) {
		return dao.checkInShift(workScheduleId);
	}
	public boolean checkOutShift(int workScheduleId) {
		return dao.checkOutShift(workScheduleId);
	}
	public Object[] getWorkDateAndTime(int workScheduleId) {
		return dao.getWorkDateAndTime(workScheduleId);
	}
}
