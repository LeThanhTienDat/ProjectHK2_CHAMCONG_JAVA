package com.example.swingapp.service;

import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.dao.OTJunctionDAO;
import com.example.swingapp.model.OTJunction;

public class OTJunctionService {
	private final OTJunctionDAO dao = new OTJunctionDAO();

	public List<OTJunction> getAll() { return dao.getAll(); }
	public boolean add(OTJunction o) { return dao.insert(o); }
	public boolean update(OTJunction o) { return dao.update(o); }
	public boolean delete(int id) { return dao.delete(id); }
	public List<OTJunction> getByEmployeeAndDate(int employeeId, String date) {
		List<OTJunction> list = new ArrayList<>();
		for (OTJunction ot : dao.getAll()) {
			if (ot.getWorkScheduleId() > 0) {
				var ws = new com.example.swingapp.service.WorkScheduleService().getById(ot.getWorkScheduleId());
				if (ws != null && ws.getEmployeeId() == employeeId && ws.getWorkDate().toString().equals(date)) {
					list.add(ot);
				}

			}
		}
		return list;
	}
	public List<OTJunction> getByWorkScheduleId(int workSchedule_id) {
		var list = dao.getByWorkScheduleId(workSchedule_id);

		return list;
	}

	public OTJunction getById(int id) {
		return dao.getAll().stream()
				.filter(o -> o.getId() == id)
				.findFirst()
				.orElse(null);
	}
	public boolean checkInOt(int workScheduleId, int otTypeId) {
		return dao.checkInOt(workScheduleId, otTypeId);
	}
	public boolean checkOutOt(int workScheduleId, int otTypeId) {
		return dao.checkOutOt(workScheduleId, otTypeId);
	}
	public List<Object[]> getFullOtByWorkScheduleId(int workScheduleId){
		return dao.getFullOtByWorkScheduleId(workScheduleId);
	}
	public List<Object[]> getAllOtRecordsForMonth(int year, int month){
		return dao.getAllOtRecordsForMonth(year, month);
	}
	public boolean confirmOt(int otJunctionId) {
		return dao.confirmOt(otJunctionId);
	}
	public boolean rejectOt(int otJunctionId) {
		return dao.rejectOt(otJunctionId);
	}
	public List<Object[]> getOtConfirmList(String keyword, int restaurantId, String date){
		return dao.getOtConfirmList(keyword, restaurantId, date);
	}
	public List<Object[]> getAllOtConfirmList(String from, String to){
		return dao.getAllOtConfirmList(from, to);
	}
}
