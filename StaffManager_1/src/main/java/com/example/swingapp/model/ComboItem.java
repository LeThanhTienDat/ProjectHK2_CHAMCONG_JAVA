
package com.example.swingapp.model;
public class ComboItem {
	private int value;
	private String label;

	public ComboItem(int value, String label) {
		this.value = value;
		this.label = label;
	}

	public int getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label; // hiển thị label trong JComboBox
	}
}