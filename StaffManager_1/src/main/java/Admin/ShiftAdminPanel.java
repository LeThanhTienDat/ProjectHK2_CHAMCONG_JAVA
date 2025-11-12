package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Shift;
import com.example.swingapp.service.ShiftService;
import com.example.swingapp.util.DBConnection;

public class ShiftAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private ShiftFormPanel formPanel;

	// Color palette
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public ShiftAdminPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));

		// ==== TOP SEARCH PANEL ====
		var searchPanel = new JPanel();
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setPreferredSize(new Dimension(0, 70));
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

		txtSearch = new JTextField("Search shifts...");
		txtSearch.setPreferredSize(new Dimension(400, 36));
		txtSearch.setColumns(30);

		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setForeground(TEXT_PRIMARY);
		txtSearch.setBackground(new Color(248, 250, 252));
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		searchPanel.add(txtSearch);

		var btnSearch = createButton("Search", PRIMARY_BLUE, 110);
		btnSearch.addActionListener(e -> search());
		searchPanel.add(btnSearch);

		btnAdd = createButton("+ Add new", ACCENT_BLUE, 110);
		btnAdd.addActionListener(e -> addNew());
		searchPanel.add(btnAdd);

		add(searchPanel, BorderLayout.NORTH);

		// ==== CENTER PANEL ====
		var contentPanel = new JPanel(new BorderLayout(0, 10));
		contentPanel.setBackground(BG_LIGHT);

		formPanel = new ShiftFormPanel(this::onSave, this::onCancel);
		formPanel.setVisible(false);
		contentPanel.add(formPanel, BorderLayout.NORTH);

		// ==== TABLE ====
		var tableCard = new JPanel(new BorderLayout());
		tableCard.setBackground(CARD_WHITE);
		tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("SHIFT LIST");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		tableCard.add(header, BorderLayout.NORTH);

		model = new DefaultTableModel(new String[] { "Shift ID","Shift Name", "Start Time", "End Time" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		table = new JTable(model);
		styleTable(table);
		loadShiftTable();

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				var r = table.getSelectedRow();
				if (r != -1) {
					formPanel.setEditMode(r, getRow(r));
					formPanel.setVisible(true);
					btnAdd.setVisible(false);
					formPanel.focusFirst();
				}
			}
		});

		var scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		tableCard.add(scrollPane, BorderLayout.CENTER);

		contentPanel.add(tableCard, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		// ==== BOTTOM ACTIONS ====
		var actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actionsPanel.setBackground(BG_LIGHT);

		var btnDelete = createButton("Delete", DANGER_RED, 110);
		btnDelete.addActionListener(e -> deleteRow());
		var btnPDF = createButton("Export PDF", TEAL, 110);
		btnPDF.addActionListener(e -> printPDF());

		actionsPanel.add(btnDelete);
		//		actionsPanel.add(btnPDF);
		add(actionsPanel, BorderLayout.SOUTH);
	}

	// ==== UTILS ====
	public static JButton createButton(String text, Color bg, int w) {
		JButton b = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Hiệu ứng hover mượt hơn
				var fillColor = bg;
				if (getModel().isPressed()) {
					fillColor = bg.darker();
				} else if (getModel().isRollover()) {
					fillColor = bg.brighter();
				}

				// Bo tròn góc
				g2.setColor(fillColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

				// Viền nhẹ nếu muốn tinh tế hơn
				g2.setColor(new Color(0, 0, 0, 20));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

				// Vẽ text giữa nút
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				var textWidth = fm.stringWidth(getText());
				var textHeight = fm.getAscent();
				g2.drawString(getText(), (getWidth() - textWidth) / 2,
						(getHeight() + textHeight - fm.getDescent()) / 2);
			}
		};

		// Cấu hình cơ bản
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 36));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setRolloverEnabled(true);

		// 👇 Thêm dòng này để con trỏ chuột đổi thành bàn tay khi hover
		b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

		return b;
	}


	private void styleTable(JTable t) {
		// Header
		var header = t.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(PRIMARY_BLUE);
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(0, 40));

		var headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(JLabel.CENTER);
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
				lbl.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
				lbl.setOpaque(true);

				return lbl;
			}
		};


		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(45);
		t.setGridColor(BORDER_COLOR);
		t.setSelectionBackground(new Color(227, 242, 253));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setIntercellSpacing(new Dimension(1, 1));
		t.setShowVerticalLines(true);
		t.setShowHorizontalLines(true);
		t.getTableHeader().setPreferredSize(new Dimension(0, 45));
		var center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);

		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			t.getColumnModel().getColumn(i).setCellRenderer(center);
		}


	}

	private Object[] getRow(int r) {
		var d = new Object[model.getColumnCount()];
		for (var i = 0; i < d.length; i++) {
			d[i] = model.getValueAt(r, i);
		}
		return d;
	}

	private void loadShiftTable() {
		try (var conn = DBConnection.getConnection()) {
			var sql = "SELECT TOP (1000) id, shift_name, start_time, end_time FROM tbl_Shift ORDER BY id";
			try (var stmt = conn.prepareStatement(sql)) {
				var rs = stmt.executeQuery();
				model.setRowCount(0);

				while (rs.next()) {
					var startTime = rs.getString("start_time");
					var endTime = rs.getString("end_time");

					if (startTime != null && startTime.contains(".")) {
						startTime = startTime.substring(0, 8);
					}
					if (endTime != null && endTime.contains(".")) {
						endTime = endTime.substring(0, 8);
					}
					Object[] row = {
							rs.getInt("id"),
							rs.getString("shift_name"),
							startTime,
							endTime
					};
					model.addRow(row);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ==== ACTIONS ====
	private void search() {
		var q = txtSearch.getText().trim();
		if (q.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a search keyword!", "Notification",
					JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Searching: " + q + " (demo)", "Search",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addNew() {
		formPanel.setAddMode(true);
		formPanel.setVisible(true);
		btnAdd.setVisible(false);
		formPanel.focusFirst();
	}

	private void deleteRow() {
		var r = table.getSelectedRow();
		if (r != -1) {
			var rowId = (int)model.getValueAt(r, 0);
			var cf = JOptionPane.showConfirmDialog(this, "Delete this shift?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (cf == JOptionPane.YES_OPTION) {

				var shiftService = new ShiftService();
				var checkDel = shiftService.delete(rowId);
				if(checkDel) {
					loadShiftTable();
					formPanel.setVisible(false);
					btnAdd.setVisible(true);
					JOptionPane.showMessageDialog(this, "Deleted successfully!", "Notification",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void printPDF() {
		try {
			var h = new MessageFormat("SHIFT LIST");
			var f = new MessageFormat("Trang {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onSave(ActionEvent e) {
		var cmd = e.getActionCommand();
		var data = formPanel.getFormData();
		var shiftService = new ShiftService();
		if ("add".equals(cmd)) {
			try {
				var s = new Shift();
				s.setShiftName((String) data[1]);
				s.setStartTime(java.sql.Time.valueOf(((String) data[2])));
				s.setEndTime(java.sql.Time.valueOf(((String) data[3])));

				var checkAdd = shiftService.add(s);
				if(checkAdd) {
					loadShiftTable();
					JOptionPane.showMessageDialog(this, "Shift added successfully!", "success",
							JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(this, "Error adding shift!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			}

		} else {
			var r = formPanel.getEditingRow();
			if (r != -1) {
				var rowId = (int)model.getValueAt(r, 0);
				var s = new Shift();
				s.setId(rowId);
				s.setShiftName((String) data[1]);
				s.setStartTime(java.sql.Time.valueOf(((String) data[2])));
				s.setEndTime(java.sql.Time.valueOf(((String) data[3])));

				var checkEdit = shiftService.update(s);

				if(checkEdit) {
					loadShiftTable();
					JOptionPane.showMessageDialog(this, "Updated successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(this, "Error updating shift!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}

	private void onCancel(ActionEvent e) {
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}
}
