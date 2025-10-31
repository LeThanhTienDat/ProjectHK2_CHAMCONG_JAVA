package com.example.swingapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AccountPanel extends JPanel {

	private JLabel lblTitle;

	public AccountPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE); // Nền phẳng, sáng

		lblTitle = new JLabel("Quản lý tài khoản", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitle.setForeground(new Color(33, 33, 33)); // Màu chữ tối, phẳng

		add(lblTitle, BorderLayout.CENTER);
	}
}
