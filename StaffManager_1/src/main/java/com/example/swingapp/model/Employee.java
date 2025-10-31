package com.example.swingapp.model;

import java.util.Date;

public class Employee {
	private int id;
	private String name;
	private String role;
	private String phone;
	private String email;
	private java.util.Date  dob;
	private String gender;
	private int active;

	public Employee() {}

	public Employee(int id, String name, String role, String phone, String email, java.util.Date  dob, String gender, int active) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.phone = phone;
		this.email = email;
		this.dob = dob;
		this.gender = gender;
		this.active = active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return name;
	}
}
