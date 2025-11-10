package com.example.swingapp.model;

public class Account {
	private int accountId;
	private String accountName;
	private String password;
	private String salt;
	private boolean active;
	private int employeeId;
	private String auth;
	private String resetCode;

	public Account() {}

	public Account(int accountId, String accountName, String password, String salt, boolean active, int employeeId, String auth, String resetCode) {
		this.accountId = accountId;
		this.accountName = accountName;
		this.password = password;
		this.salt = salt;
		this.active = active;
		this.employeeId = employeeId;
		this.auth = auth;
		this.resetCode = resetCode;
	}

	public String getResetCode() {
		return resetCode;
	}

	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
}
