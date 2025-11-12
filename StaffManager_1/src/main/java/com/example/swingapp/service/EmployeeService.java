package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.EmployeeDAO;
import com.example.swingapp.model.Employee;

public class EmployeeService {
	private final EmployeeDAO dao = new EmployeeDAO();

	public List<Employee> getAll() {
		return dao.getAll();
	}

	public boolean add(Employee e) {
		return dao.insert(e);
	}

	public boolean update(Employee e) {
		return dao.update(e);
	}

	public boolean delete(int id) {
		return dao.delete(id);
	}
	public List<Employee> getByRestaurantId(int id){
		return dao.getByRestaurantId(id);
	}
	public boolean checkExistPhone(String phone) {
		return dao.checkPhone(phone);
	}
	public boolean checkActiveContract(int employeeId) {
		return dao.hasActiveContract(employeeId);
	}
}
