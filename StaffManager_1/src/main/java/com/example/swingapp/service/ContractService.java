package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.ContractDAO;
import com.example.swingapp.model.Contract;

public class ContractService {
	private final ContractDAO dao = new ContractDAO();

	public List<Contract> getAll() { return dao.getAll(); }
	public boolean add(Contract c) { System.out.println("[DEBUG] Insert contract into DB: " + c); return dao.insert(c); }
	public boolean update(Contract c) { return dao.update(c); }
	public boolean delete(int id) { return dao.delete(id); }
	public boolean hasActiveContract(int id) {
		return dao.hasActiveContract(id);
	}
}
