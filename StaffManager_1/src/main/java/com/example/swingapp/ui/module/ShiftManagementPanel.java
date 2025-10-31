package com.example.swingapp.ui.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

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

import com.example.swingapp.dao.ShiftDAO;
import com.example.swingapp.model.Shift;

public class ShiftManagementPanel extends JPanel {

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;

	// Màu sắc chủ đạo
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(245, 247, 250);
	private static final Color CARD_WHITE = Color.WHITE;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	// Các panel và button
	private JPanel headerPanel;
	private JPanel content;
	private JPanel searchPanel;
	private JPanel tableCard;
	private JPanel actionPanel;
	private JButton btnSearch;
	private JButton btnReload;
	private JButton btnDelete;
	private JButton btnExport;

	public ShiftManagementPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));
		setBorder(new EmptyBorder(0, 0, 0, 0));

		initComponents();
		layoutComponents();
		styleComponents();
		loadData();
	}

	private void initComponents() {
		// Header
		headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(0, 70));
		headerPanel.setBackground(PRIMARY_BLUE); // WindowBuilder nhận diện
		var lblTitle = new JLabel("QUẢN LÝ CA LÀM VIỆC", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(Color.WHITE);
		headerPanel.setLayout(new BorderLayout());
		headerPanel.add(lblTitle, BorderLayout.CENTER);

		// Content
		content = new JPanel();
		content.setOpaque(false);
		content.setBorder(new EmptyBorder(20, 25, 20, 25));
		content.setLayout(new BorderLayout(15, 15));

		// Search panel
		searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		searchPanel.setOpaque(false);
		txtSearch = new JTextField("Tìm theo tên ca hoặc thời gian...");
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setPreferredSize(new Dimension(400, 38));
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		btnSearch = new JButton("Tìm kiếm");
		btnReload = new JButton("Tải lại");

		// Table card
		tableCard = new JPanel();
		tableCard.setLayout(new BorderLayout());
		var lbl = new JLabel("Danh sách ca làm việc");
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lbl.setForeground(PRIMARY_BLUE);
		lbl.setBorder(new EmptyBorder(0, 10, 10, 0));
		tableCard.add(lbl, BorderLayout.NORTH);

		String[] columns = {"ID", "Tên ca", "Bắt đầu", "Kết thúc"};
		model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table = new JTable(model);
		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		tableCard.add(sp, BorderLayout.CENTER);

		// Action panel
		actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actionPanel.setOpaque(false);
		btnDelete = new JButton("Xóa");
		btnExport = new JButton("Xuất PDF");

		// Thêm listener
		btnSearch.addActionListener(e -> searchShift());
		btnReload.addActionListener(e -> loadData());
		btnDelete.addActionListener(e -> deleteRow());
		btnExport.addActionListener(e -> exportPDF());
	}

	private void layoutComponents() {
		add(headerPanel, BorderLayout.NORTH);

		// Search panel
		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		searchPanel.add(Box.createHorizontalStrut(200));
		searchPanel.add(btnReload);
		content.add(searchPanel, BorderLayout.NORTH);

		// Table
		content.add(tableCard, BorderLayout.CENTER);

		// Action panel
		actionPanel.add(btnDelete);
		actionPanel.add(btnExport);
		content.add(actionPanel, BorderLayout.SOUTH);

		add(content, BorderLayout.CENTER);
	}

	private void styleComponents() {
		// Button style
		JButton[] buttons = {btnSearch, btnReload, btnDelete, btnExport};
		Color[] colors = {PRIMARY_BLUE, ACCENT_BLUE, DANGER_RED, TEAL};
		for (var i = 0; i < buttons.length; i++) {
			var btn = buttons[i];
			var color = colors[i];
			btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
			btn.setForeground(Color.WHITE);
			btn.setBackground(color);
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		// Table style
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

	// ================== LOGIC ==================

	private void loadData() {
		model.setRowCount(0);
		var list = new ShiftDAO().getAll();
		for (Shift s : list) {
			model.addRow(new Object[]{
					s.getId(), s.getShiftName(), s.getStartTime(), s.getEndTime()
			});
		}
	}

	private void searchShift() {
		var key = txtSearch.getText().trim().toLowerCase();
		if (key.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		model.setRowCount(0);
		var list = new ShiftDAO().getAll();
		for (Shift s : list) {
			if (s.getShiftName().toLowerCase().contains(key)
					|| String.valueOf(s.getStartTime()).contains(key)
					|| String.valueOf(s.getEndTime()).contains(key)) {
				model.addRow(new Object[]{
						s.getId(), s.getShiftName(), s.getStartTime(), s.getEndTime()
				});
			}
		}
	}

	private void deleteRow() {
		var row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm việc cần xóa!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var id = (int) model.getValueAt(row, 0);
		if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ca ID: " + id + "?",
				"Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (new ShiftDAO().delete(id)) {
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
			var header = new java.text.MessageFormat("DANH SÁCH CA LÀM VIỆC");
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
