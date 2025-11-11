package com.example.swingapp.model;

import java.util.Date;

public class AttendanceLockStatus {
	private int id;
	private int lockedMonth;
	private int lockedYear;
	private int lockedByUserId;
	private Date lockedAt;
	public AttendanceLockStatus(int id, int lockedMonth, int lockedYear, int lockedByUserId, Date lockedAt) {
		super();
		this.id = id;
		this.lockedMonth = lockedMonth;
		this.lockedYear = lockedYear;
		this.lockedByUserId = lockedByUserId;
		this.lockedAt = lockedAt;
	}
	public AttendanceLockStatus() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLockedMonth() {
		return lockedMonth;
	}
	public void setLockedMonth(int lockedMonth) {
		this.lockedMonth = lockedMonth;
	}
	public int getLockedYear() {
		return lockedYear;
	}
	public void setLockedYear(int lockedYear) {
		this.lockedYear = lockedYear;
	}
	public int getLockedByUserId() {
		return lockedByUserId;
	}
	public void setLockedByUserId(int lockedByUserId) {
		this.lockedByUserId = lockedByUserId;
	}
	public Date getLockedAt() {
		return lockedAt;
	}
	public void setLockedAt(Date lockedAt) {
		this.lockedAt = lockedAt;
	}
	@Override
	public String toString() {
		return "AttendanceLockStatus [id=" + id + ", lockedMonth=" + lockedMonth + ", lockedYear=" + lockedYear
				+ ", lockedByUserId=" + lockedByUserId + ", lockedAt=" + lockedAt + "]";
	}


}
