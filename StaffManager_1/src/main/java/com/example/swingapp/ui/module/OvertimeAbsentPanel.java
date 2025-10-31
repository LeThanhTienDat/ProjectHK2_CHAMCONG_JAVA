package com.example.swingapp.ui.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.dao.AbsentDAO;
import com.example.swingapp.dao.OTJunctionDAO;
import com.example.swingapp.model.Absent;
import com.example.swingapp.model.OTJunction;

// Gradient panel cho header
class GradientPanel extends JPanel {
	private Color start;
	private Color end;

	public GradientPanel(Color start, Color end) {
		this.start = start;
		this.end = end;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		var gp = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
		g2.setPaint(gp);
		g2.fillRect(0, 0, getWidth(), getHeight());
	}
}

// Card panel với shadow
class CardPanel extends JPanel {
	public CardPanel() {
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		var g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(0, 0, 0, 8));
		g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
		g2d.setColor(Color.WHITE);
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
	}
}

// Modern button
class ModernButton extends JButton {
	private Color color;

	public ModernButton(String text, Color color) {
		super(text);
		this.color = color;
		setFont(new Font("Segoe UI", Font.BOLD, 13));
		setForeground(Color.WHITE);
		setBorderPainted(false);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	protected void paintComponent(Graphics g) {
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel().isPressed()) {
			g2.setColor(color.darker());
		} else if (getModel().isRollover()) {
			g2.setColor(color.brighter());
		} else {
			g2.setColor(color);
		}
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		g2.setColor(Color.WHITE);
		var fm = g2.getFontMetrics();
		var x = (getWidth() - fm.stringWidth(getText())) / 2;
		var y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
		g2.drawString(getText(), x, y);
	}
}

public class OvertimeAbsentPanel extends JPanel {

	private JTable tblOT, tblAbsent;
	private DefaultTableModel modelOT, modelAbsent;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(245, 247, 250);
	private static final Color CARD_WHITE = Color.WHITE;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEAL = new Color(0, 150, 136);

	public OvertimeAbsentPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Header
		var headerPanel = new GradientPanel(PRIMARY_BLUE, ACCENT_BLUE);
		headerPanel.setPreferredSize(new Dimension(0, 70));
		var lblTitle = new JLabel("QUẢN LÝ OT & NGHỈ PHÉP", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(Color.WHITE);
		headerPanel.add(lblTitle, BorderLayout.CENTER);
		add(headerPanel, BorderLayout.NORTH);

		// Content
		var content = new JPanel(new GridLayout(2, 1, 0, 15));
		content.setOpaque(false);
		content.setBorder(new EmptyBorder(15, 25, 20, 25));

		// OT Section
		var otCard = createCardPanel("DANH SÁCH LÀM THÊM GIỜ (OT)");
		modelOT = new DefaultTableModel(
				new String[]{"ID", "WorkScheduleID", "OTTypeID", "CheckIn", "CheckOut", "Xác nhận"}, 0) {
			@Override
			public boolean isCellEditable(int row, int col) { return false; }
		};
		tblOT = new JTable(modelOT);
		styleTable(tblOT);
		otCard.add(new JScrollPane(tblOT), BorderLayout.CENTER);

		var btnReloadOT = new ModernButton("Tải lại OT", TEAL);
		btnReloadOT.setPreferredSize(new Dimension(130, 35));
		btnReloadOT.addActionListener(e -> loadOT());
		var otBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		otBtnPanel.setOpaque(false);
		otBtnPanel.add(btnReloadOT);
		otCard.add(otBtnPanel, BorderLayout.SOUTH);

		content.add(otCard);

		// Absent Section
		var abCard = createCardPanel("DANH SÁCH NGHỈ PHÉP");
		modelAbsent = new DefaultTableModel(
				new String[]{"ID", "Loại", "Lý do", "Mô tả"}, 0) {
			@Override
			public boolean isCellEditable(int row, int col) { return false; }
		};
		tblAbsent = new JTable(modelAbsent);
		styleTable(tblAbsent);
		abCard.add(new JScrollPane(tblAbsent), BorderLayout.CENTER);

		var btnReloadAb = new ModernButton("Tải lại nghỉ phép", TEAL);
		btnReloadAb.setPreferredSize(new Dimension(160, 35));
		btnReloadAb.addActionListener(e -> loadAbsent());
		var abBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		abBtnPanel.setOpaque(false);
		abBtnPanel.add(btnReloadAb);
		abCard.add(abBtnPanel, BorderLayout.SOUTH);

		content.add(abCard);

		add(content, BorderLayout.CENTER);

		loadData();
	}

	private CardPanel createCardPanel(String title) {
		var panel = new CardPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		var lbl = new JLabel(title);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl.setForeground(PRIMARY_BLUE);
		lbl.setBorder(new EmptyBorder(0, 10, 10, 0));
		panel.add(lbl, BorderLayout.NORTH);
		return panel;
	}

	private void styleTable(JTable table) {
		var header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(PRIMARY_BLUE);
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(0, 40));

		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setGridColor(BORDER_COLOR);
		table.setSelectionBackground(new Color(227, 242, 253));
		table.setSelectionForeground(Color.BLACK);
	}

	// ========= Logic =========
	private void loadData() {
		loadOT();
		loadAbsent();
	}

	private void loadOT() {
		modelOT.setRowCount(0);
		var list = new OTJunctionDAO().getAll();
		for (OTJunction o : list) {
			modelOT.addRow(new Object[]{
					o.getId(), o.getWorkScheduleId(), o.getOtTypeId(),
					o.getOtCheckInTime(), o.getOtCheckOutTime(),
					o.isOtConfirm() ? "✅ Xác nhận" : "❌ Chưa duyệt"
			});
		}
	}

	private void loadAbsent() {
		modelAbsent.setRowCount(0);
		var list = new AbsentDAO().getAll();
		for (Absent a : list) {
			modelAbsent.addRow(new Object[]{
					a.getId(), a.getAbsentType(), a.getReason(), a.getDescription()
			});
		}
	}
}
