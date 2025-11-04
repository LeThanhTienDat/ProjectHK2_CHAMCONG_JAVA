package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.ShiftDAO;
import com.example.swingapp.model.Shift;

public class ShiftService {
	private final ShiftDAO dao = new ShiftDAO();

	public List<Shift> getAll() { return dao.getAll(); }
	public boolean add(Shift s) { return dao.insert(s); }
	public boolean update(Shift s) { return dao.update(s); }
	public boolean delete(int id) { return dao.delete(id); }
	public Shift getById(int id) {
		return dao.getById(id);
	}
}
