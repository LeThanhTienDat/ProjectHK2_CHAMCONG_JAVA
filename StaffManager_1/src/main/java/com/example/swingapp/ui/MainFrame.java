package com.example.swingapp.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {

	private String role;
	private int employeeId;

	// UI Components
	private JPanel content;

	public MainFrame(String role, int employeeId) {
		this.role = role;
		this.employeeId = employeeId;

		initComponents();
		layoutComponents();

		setTitle("Trang chính - " + role);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 700);
		setLocationRelativeTo(null);
	}

	private void initComponents() {
		content = new JPanel(new BorderLayout());

		// Chọn panel theo role
		JPanel centerPanel;
		if ("ADMIN".equalsIgnoreCase(role)) {
			centerPanel = new MainDashboard(employeeId);
		} else {
			centerPanel = new EmployeePanel(employeeId);
		}

		content.add(centerPanel, BorderLayout.CENTER);
	}

	private void layoutComponents() {
		setContentPane(content);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// Giả lập user nhân viên có ID = 1
			new MainFrame("EMPLOYEE", 1).setVisible(true);
		});
	}
}
