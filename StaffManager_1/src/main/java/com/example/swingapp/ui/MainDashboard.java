package com.example.swingapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.example.swingapp.ui.module.AccountManagementPanel;
import com.example.swingapp.ui.module.ContractManagementPanel;
import com.example.swingapp.ui.module.EmployeeManagementPanel;
import com.example.swingapp.ui.module.MonthlySummaryPanel;
import com.example.swingapp.ui.module.OvertimeAbsentPanel;
import com.example.swingapp.ui.module.RestaurantManagementPanel;
import com.example.swingapp.ui.module.ShiftManagementPanel;
import com.example.swingapp.ui.module.StreetManagementPanel;

public class MainDashboard extends JPanel {

	private JPanel contentPanel;
	private int employeeId;
	private JButton currentButton = null;

	// Màu chủ đạo
	private final Color NAVY = new Color(44, 62, 80);
	private final Color BLUE = new Color(52, 152, 219);
	private final Color LIGHT = new Color(236, 240, 241);
	private final Color HOVER = new Color(52, 73, 94);

	public MainDashboard(int employeeId) {
		this.employeeId = employeeId;
		setLayout(new BorderLayout());

		// ===== HEADER =====
		JPanel headerPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, new Color(33, 150, 243),
						getWidth(), getHeight(), new Color(25, 118, 210));
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		headerPanel.setPreferredSize(new Dimension(0, 70));

		var header = new JLabel("HỆ THỐNG QUẢN LÝ CHẤM CÔNG", SwingConstants.CENTER);
		header.setFont(new Font("Segoe UI", Font.BOLD, 22));
		header.setForeground(Color.WHITE);
		headerPanel.add(header, BorderLayout.CENTER);
		add(headerPanel, BorderLayout.NORTH);

		// ===== SIDEBAR =====
		var sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(0, 1, 0, 10));
		sidebar.setBackground(NAVY);
		sidebar.setPreferredSize(new Dimension(240, 0));
		sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		// Map menu và icon
		Map<String, String> menuItems = new LinkedHashMap<>();
		menuItems.put("Nhân viên", "icons/employee.png");
		menuItems.put("Tài khoản", "icons/account.png");
		menuItems.put("Hợp đồng", "icons/contract.png");
		menuItems.put("Lương tháng", "icons/salary.png");
		menuItems.put("Ca làm", "icons/shift.png");
		menuItems.put("OT & Nghỉ phép", "icons/ot.png");
		menuItems.put("Nhà hàng", "icons/restaurant.png");
		menuItems.put("Đường phố", "icons/street.png");
		menuItems.put("Đăng xuất", "icons/logout.png");

		JButton btnLogout = null;
		for (Map.Entry<String, String> entry : menuItems.entrySet()) {
			var btn = createMenuButton(entry.getKey(), entry.getValue());
			switch (entry.getKey()) {
			case "Nhân viên" -> btn.addActionListener(e -> switchPanel(btn, new EmployeeManagementPanel()));
			case "Tài khoản" -> btn.addActionListener(e -> switchPanel(btn, new AccountManagementPanel()));
			case "Hợp đồng" -> btn.addActionListener(e -> switchPanel(btn, new ContractManagementPanel()));
			case "Lương tháng" -> btn.addActionListener(e -> switchPanel(btn, new MonthlySummaryPanel()));
			case "Ca làm" -> btn.addActionListener(e -> switchPanel(btn, new ShiftManagementPanel()));
			case "OT & Nghỉ phép" -> btn.addActionListener(e -> switchPanel(btn, new OvertimeAbsentPanel()));
			case "Nhà hàng" -> btn.addActionListener(e -> switchPanel(btn, new RestaurantManagementPanel()));
			case "Đường phố" -> btn.addActionListener(e -> switchPanel(btn, new StreetManagementPanel()));
			case "Đăng xuất" -> btnLogout = btn;
			}
			sidebar.add(btn);
		}

		// Đăng xuất
		if (btnLogout != null) {
			sidebar.add(Box.createVerticalStrut(10));
			btnLogout.addActionListener(e -> {
				SwingUtilities.getWindowAncestor(this).dispose();
				JOptionPane.showMessageDialog(null, "Đăng xuất thành công!");
				new LoginForm().setVisible(true);
			});
		}

		add(sidebar, BorderLayout.WEST);

		// ===== CONTENT PANEL =====
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(LIGHT);

		var welcome = new JLabel("<html><center>Chào mừng đến HỆ THỐNG QUẢN LÝ NHÂN SỰ<br><br>"
				+ "<font color='#1976d2'>Vui lòng chọn một mục bên trái để bắt đầu làm việc</font></center></html>",
				SwingConstants.CENTER);
		welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
		welcome.setForeground(new Color(52, 73, 94));
		contentPanel.add(welcome, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
	}

	// ===== MENU BUTTON =====
	private JButton createMenuButton(String text, String iconPath) {
		var btn = new JButton(text);
		btn.setFocusPainted(false);
		btn.setBackground(NAVY);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		try {
			var icon = new ImageIcon(getClass().getClassLoader().getResource(iconPath));
			if (icon.getImage() != null) {
				var scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
				btn.setIcon(new ImageIcon(scaled));
				btn.setIconTextGap(15);
			}
		} catch (Exception e) {
			System.out.println("Không tìm thấy icon: " + iconPath);
		}

		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				if (btn != currentButton) {
					btn.setBackground(HOVER);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				if (btn != currentButton) {
					btn.setBackground(NAVY);
				}
			}
		});
		return btn;
	}

	// ===== SWITCH PANEL =====
	private void switchPanel(JButton clickedButton, JPanel panel) {
		if (currentButton != null) {
			currentButton.setBackground(NAVY);
		}
		clickedButton.setBackground(BLUE);
		currentButton = clickedButton;

		contentPanel.removeAll();
		contentPanel.add(panel, BorderLayout.CENTER);
		contentPanel.revalidate();
		contentPanel.repaint();
	}
}
