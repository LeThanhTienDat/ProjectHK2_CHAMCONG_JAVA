package com.example.swingapp.service;

import com.example.swingapp.dao.AttendanceLockStatusDAO;
import com.example.swingapp.model.AttendanceLockStatus;

public class AttendanceLockStatusService {
	private final AttendanceLockStatusDAO dao = new AttendanceLockStatusDAO();

	public AttendanceLockStatus getLast() { return dao.getLast(); }
	public boolean lockMonthYear(int month, int year) {
		return dao.lockMonth(month, year);
	}

}