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
	public OTJunction getById(int id) {
		return dao.getAll().stream()
				.filter(o -> o.getId() == id)
				.findFirst()
				.orElse(null);
	}

}
