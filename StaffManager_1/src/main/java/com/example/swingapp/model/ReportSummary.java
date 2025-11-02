package com.example.swingapp.model;

import java.util.Map;

public class ReportSummary {
	private double totalSalary;
	private double totalHours;
	private double totalOverTime;
	private double totalOverTimeSalary;
	private Map<String, Number> salaryDistribution;
	private Map<String, Number> OnTimeDistribution;
	private Map<String, Number> EmployeeDistribution;
	private int totalEmployee;
	private double OntimePercentage;

	public double getOntimePercentage() {
		return OntimePercentage;
	}

	public void setOntimePercentage(double ontimePercentage) {
		OntimePercentage = ontimePercentage;
	}

	public Map<String, Number> getOnTimeDistribution() {
		return OnTimeDistribution;
	}

	public void setOnTimeDistribution(Map<String, Number> onTimeDistribution) {
		OnTimeDistribution = onTimeDistribution;
	}

	public Map<String, Number> getEmployeeDistribution() {
		return EmployeeDistribution;
	}

	public void setEmployeeDistribution(Map<String, Number> employeeDistribution) {
		EmployeeDistribution = employeeDistribution;
	}

	public double getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(double totalSalary) {
		this.totalSalary = totalSalary;
	}

	public double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}

	public double getTotalOverTime() {
		return totalOverTime;
	}

	public void setTotalOverTime(double totalOverTime) {
		this.totalOverTime = totalOverTime;
	}

	public int getTotalEmployee() {
		return totalEmployee;
	}

	public void setTotalEmployee(int totalEmployee) {
		this.totalEmployee = totalEmployee;
	}

	public double getTotalOverTimeSalary() {
		return totalOverTimeSalary;
	}

	public void setTotalOverTimeSalary(double totalOverTimeSalary) {
		this.totalOverTimeSalary = totalOverTimeSalary;
	}

	public Map<String, Number> getSalaryDistribution() {
		return salaryDistribution;
	}

	public void setSalaryDistribution(Map<String, Number> salaryDistribution) {
		this.salaryDistribution = salaryDistribution;
	}
}
