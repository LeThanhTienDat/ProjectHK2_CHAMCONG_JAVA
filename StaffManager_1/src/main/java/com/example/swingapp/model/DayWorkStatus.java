package com.example.swingapp.model;

public class DayWorkStatus {

	private final String shiftName;
	private final boolean isPresent;

	public DayWorkStatus(String shiftName, boolean isPresent) {
		this.shiftName = shiftName;
		this.isPresent = isPresent;
	}



	public String getShiftName() {
		return shiftName;
	}

	public boolean isPresent() {
		return isPresent;
	}


}
