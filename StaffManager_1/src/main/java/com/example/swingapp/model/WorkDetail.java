package com.example.swingapp.model;

public class WorkDetail {
	public String checkIn, checkOut;
	public int late, early;
	public float workHours, otHours;

	public WorkDetail(String checkIn, String checkOut, int late, int early, float workHours, float otHours) {
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.late = late;
		this.early = early;
		this.workHours = workHours;
		this.otHours = otHours;
	}

	public String getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(String checkIn) {
		this.checkIn = checkIn;
	}

	public String getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(String checkOut) {
		this.checkOut = checkOut;
	}

	public int getLate() {
		return late;
	}

	public void setLate(int late) {
		this.late = late;
	}

	public int getEarly() {
		return early;
	}

	public void setEarly(int early) {
		this.early = early;
	}

	public float getWorkHours() {
		return workHours;
	}

	public void setWorkHours(float workHours) {
		this.workHours = workHours;
	}

	public float getOtHours() {
		return otHours;
	}

	public void setOtHours(float otHours) {
		this.otHours = otHours;
	}
}
