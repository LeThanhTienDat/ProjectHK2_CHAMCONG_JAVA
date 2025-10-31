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
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.dao.MonthlySummaryDAO;
import com.example.swingapp.model.MonthlySummary;

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

// ModernButton với paintComponent custom
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

public class MonthlySummaryPanel extends JPanel {

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(245, 247, 250);
	private static final Color CARD_WHITE = Color.WHITE;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public MonthlySummaryPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Header gradient
		var headerPanel = new GradientPanel(PRIMARY_BLUE, ACCENT_BLUE);
		headerPanel.setPreferredSize(new Dimension(0, 70));
		var lblTitle = new JLabel("QUẢN LÝ BẢNG LƯƠNG THÁNG", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(Color.WHITE);
		headerPanel.add(lblTitle, BorderLayout.CENTER);
		add(headerPanel, BorderLayout.NORTH);

		// Content area
		var content = new JPanel(new BorderLayout(15, 15));
		content.setOpaque(false);
		content.setBorder(new EmptyBorder(20, 25, 20, 25));

		content.add(createSearchPanel(), BorderLayout.NORTH);
		content.add(createTableCard(), BorderLayout.CENTER);
		content.add(createActionPanel(), BorderLayout.SOUTH);

		add(content, BorderLayout.CENTER);

		loadData();
	}

	private JPanel createSearchPanel() {
		var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		panel.setOpaque(false);

		txtSearch = new JTextField("Tìm theo nhân viên, tháng hoặc trạng thái...");
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setPreferredSize(new Dimension(400, 38));
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		panel.add(txtSearch);

		var btnSearch = new ModernButton("Tìm kiếm", PRIMARY_BLUE);
		btnSearch.setPreferredSize(new Dimension(120, 38));
		btnSearch.addActionListener(e -> searchMonthly());
		panel.add(btnSearch);

		panel.add(Box.createHorizontalStrut(200));

		var btnReload = new ModernButton("Tải lại", ACCENT_BLUE);
		btnReload.setPreferredSize(new Dimension(120, 38));
		btnReload.addActionListener(e -> loadData());
		panel.add(btnReload);

		return panel;
	}

	private JPanel createTableCard() {
		var card = new CardPanel();
		card.setLayout(new BorderLayout());
		card.setBorder(new EmptyBorder(10, 10, 10, 10));

		var lbl = new JLabel("Danh sách bảng lương tháng");
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lbl.setForeground(PRIMARY_BLUE);
		lbl.setBorder(new EmptyBorder(5, 10, 10, 0));
		card.add(lbl, BorderLayout.NORTH);

		String[] columns = { "ID", "Mã NV", "Tháng", "Năm", "Ca", "Giờ OT", "Lương Cuối", "Trạng Thái" };
		model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int col) { return false; }
		};

		table = new JTable(model);
		styleTable(table);

		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		card.add(sp, BorderLayout.CENTER);

		return card;
	}

	private JPanel createActionPanel() {
		var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panel.setOpaque(false);

		var btnDelete = new ModernButton("Xóa", DANGER_RED);
		btnDelete.setPreferredSize(new Dimension(120, 38));
		btnDelete.addActionListener(e -> deleteRow());

		var btnExport = new ModernButton("Xuất PDF", TEAL);
		btnExport.setPreferredSize(new Dimension(120, 38));
		btnExport.addActionListener(e -> exportPDF());

		panel.add(btnDelete);
		panel.add(btnExport);
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

	// ================= LOGIC ==================

	private void loadData() {
		model.setRowCount(0);
		var dao = new MonthlySummaryDAO();
		var list = dao.getAll();
		for (MonthlySummary m : list) {
			model.addRow(new Object[]{
					m.getId(), m.getEmployeeId(), m.getMonth(), m.getYear(),
					m.getTotalShift(), m.getTotalOverTime(),
					String.format("%,.0f VNĐ", m.getFinalSalary()),
					m.getStatus()
			});
		}
	}

	private void searchMonthly() {
		var key = txtSearch.getText().trim().toLowerCase();
		if (key.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		model.setRowCount(0);
		var dao = new MonthlySummaryDAO();
		var list = dao.getAll();
		for (MonthlySummary m : list) {
			if (String.valueOf(m.getEmployeeId()).contains(key)
					|| m.getStatus().toLowerCase().contains(key)
					|| String.valueOf(m.getMonth()).contains(key)) {
				model.addRow(new Object[]{
						m.getId(), m.getEmployeeId(), m.getMonth(), m.getYear(),
						m.getTotalShift(), m.getTotalOverTime(),
						String.format("%,.0f VNĐ", m.getFinalSalary()),
						m.getStatus()
				});
			}
		}
	}

	private void deleteRow() {
		var row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var id = (int) model.getValueAt(row, 0);
		if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bảng lương ID: " + id + "?",
				"Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (new MonthlySummaryDAO().delete(id)) {
				JOptionPane.showMessageDialog(this, "Xóa thành công!");
				loadData();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void exportPDF() {
		try {
			var header = new java.text.MessageFormat("BẢNG LƯƠNG THÁNG");
			var footer = new java.text.MessageFormat("Trang {0,number,integer}");
			table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
