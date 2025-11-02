package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.OTTypeDAO;
import com.example.swingapp.model.OTType;

public class OTTypeService {
	private final OTTypeDAO dao = new OTTypeDAO();

	public List<OTType> getAll() { return dao.getAll(); }
	public boolean add(OTType o) { return dao.insert(o); }
	public boolean update(OTType o) { return dao.update(o); }
	public boolean delete(int id) { return dao.delete(id); }
	public OTType getById(int id) {
		return dao.getById(id);
	}
}
