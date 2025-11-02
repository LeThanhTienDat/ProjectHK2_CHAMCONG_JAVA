package com.example.swingapp.model;

import java.sql.Time;

public class OTType {
	private int id;
	private String otName;
	private Time otStart;
	private Time otEnd;

	public OTType() {}

	public OTType(int id, String otName, Time otStart, Time otEnd) {
		this.id = id;
		this.otName = otName;
		this.otStart = otStart;
		this.otEnd = otEnd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOtName() {
		return otName;
	}

	public void setOtName(String otName) {
		this.otName = otName;
	}

	public Time getOtStart() {
		return otStart;
	}

	public void setOtStart(Time otStart) {
		this.otStart = otStart;
	}

	public Time getOtEnd() {
		return otEnd;
	}

	public void setOtEnd(Time otEnd) {
		this.otEnd = otEnd;
	}
}
