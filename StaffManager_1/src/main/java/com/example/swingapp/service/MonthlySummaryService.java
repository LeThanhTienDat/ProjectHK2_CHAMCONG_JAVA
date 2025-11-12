package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.MonthlySummaryDAO;
import com.example.swingapp.model.MonthlySummary;

public class MonthlySummaryService {
	private final MonthlySummaryDAO dao = new MonthlySummaryDAO();

	public List<MonthlySummary> getAll() { return dao.getAll(); }
	public boolean add(MonthlySummary m) { return dao.insert(m); }
	public boolean update(MonthlySummary m) { return dao.update(m); }
	public boolean delete(int id) { return dao.delete(id); }
	public boolean reFresh(int month, int year) {
		return dao.reFreshMonthlySummary(month, year);
	}
}
