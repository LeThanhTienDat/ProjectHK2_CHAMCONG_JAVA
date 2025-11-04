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
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

@SuppressWarnings("serial")
public class PayrollAdminPanel extends JPanel {
	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private PayrollFormPanel formPanel;
	private JComboBox<Restaurant> resFilter;
	private JComboBox<String> monthFilter;
	private boolean isInitializing = true;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public PayrollAdminPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));
		initUI();
	}

	private void initUI() {
		// ==== SEARCH PANEL ====
		var searchPanel = new JPanel();
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setPreferredSize(new Dimension(0, 70));
		add(searchPanel, BorderLayout.NORTH);

		txtSearch = styledField("Tìm kiếm theo tên nhân viên...", 400);
		txtSearch.setColumns(30);
		searchPanel.add(txtSearch);

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.addActionListener(e -> onRestaurantSelected());
		resFilter.setPreferredSize(new Dimension(200, 36));
		resFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		renderRestaurant();

		monthFilter = new JComboBox<>();
		monthFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		monthFilter.setBackground(new Color(248, 250, 252));
		monthFilter.setPreferredSize(new Dimension(200, 36));
		monthFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		var now = LocalDate.now();
		for (var i = 14; i >= 0; i--) {
			var month = now.minusMonths(i);
			var item = String.format("Tháng %d / %d", month.getMonthValue(), month.getYear());
			monthFilter.addItem(item);
		}
		monthFilter.setSelectedIndex(14);
		monthFilter.addActionListener(e -> onRestaurantSelected());

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 110);
		if (!java.beans.Beans.isDesignTime()) {
			btnSearch.addActionListener(e -> search());
		}
		searchPanel.add(new JLabel("Tháng: "));
		searchPanel.add(monthFilter);
		searchPanel.add(new JLabel("Theo Nhà Hàng: "));
		searchPanel.add(resFilter);
		searchPanel.add(btnSearch);

		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 110);
		if (!java.beans.Beans.isDesignTime()) {
			btnAdd.addActionListener(e -> addNew());
		}
		searchPanel.add(btnAdd);

		// ==== CONTENT ====
		var content = new JPanel(new BorderLayout(0, 15));
		content.setBackground(BG_LIGHT);
		add(content, BorderLayout.CENTER);

		formPanel = new PayrollFormPanel(this::onSave, this::onCancel);
		formPanel.setVisible(false);
		content.add(formPanel, BorderLayout.NORTH);

		var tableCard = createTableCard();
		content.add(tableCard, BorderLayout.CENTER);

		// ==== ACTIONS (BOTTOM) ====
		var actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actions.setBackground(BG_LIGHT);
		var btnDelete = createButton("Xóa", DANGER_RED, 110);
		if (!java.beans.Beans.isDesignTime()) {
			btnDelete.addActionListener(e -> deleteRow());
		}
		var btnPDF = createButton("Xuất PDF", TEAL, 110);
		if (!java.beans.Beans.isDesignTime()) {
			btnPDF.addActionListener(e -> printPDF());
		}
		actions.add(btnDelete);
		actions.add(btnPDF);
		add(actions, BorderLayout.SOUTH);
	}

	private JPanel createTableCard() {
		var card = new JPanel(new BorderLayout());
		card.setBackground(CARD_WHITE);
		card.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("BẢNG LƯƠNG NHÂN VIÊN");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		card.add(header, BorderLayout.NORTH);

		String[] cols = { "Mã nhân viên", "Họ Tên","Ngày sinh", "Điện thoại" , "Nhà hàng", "Tháng",  "Đi trễ(min)", "Về sớm(min)","Tổng tăng ca", "Tổng giờ làm(hour)", "Tổng Lương" };
		model = new DefaultTableModel(cols, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return switch (columnIndex) {
				case 2 -> java.util.Date.class;
				case 6, 7, 8, 9, 10 -> Double.class;
				default -> String.class;
				};
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		styleTable(table);
		setupColumnWidths(table);
		loadPayrollTable();

		if (!java.beans.Beans.isDesignTime()) {
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
		}

		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		card.add(sp, BorderLayout.CENTER);

		return card;
	}

	private JTextField styledField(String placeholder, int w) {
		var f = new JTextField(placeholder);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		f.setPreferredSize(new Dimension(w, 36));
		f.setMinimumSize(new Dimension(40, 36));
		return f;
	}

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
		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(45);
		t.setSelectionBackground(new Color(232, 240, 254));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(new Color(220, 220, 220));
		t.setShowVerticalLines(true);
		t.setShowHorizontalLines(true);
		t.setAutoCreateRowSorter(true);

		//căn giữa
		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		//format cho cột ngày sinh
		var dateRenderer = new DefaultTableCellRenderer() {
			private final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

				if (value instanceof java.util.Date) {
					value = df.format((java.util.Date) value);
				}

				var cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				cell.setHorizontalAlignment(SwingConstants.CENTER);
				cell.setBackground(isSelected ? new Color(232, 240, 254) : Color.WHITE);
				cell.setForeground(Color.BLACK);
				return cell;
			}
		};
		t.getColumnModel().getColumn(2).setCellRenderer(dateRenderer);

		//định dạng cho tổng lương
		var salaryRenderer = new DefaultTableCellRenderer() {
			private final java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");

			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// Chỉ format khi hiển thị, vẫn giữ value trong model là Number
				var text = "";
				if (value instanceof Number) {
					text = df.format(value) + " ₫";
				} else if (value != null) {
					text = value.toString();
				}

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, text, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setBackground(isSelected ? new Color(232, 240, 254) : Color.WHITE);
				lbl.setForeground(Color.BLACK);

				return lbl;
			}
		};
		table.getColumnModel().getColumn(10).setCellRenderer(salaryRenderer);

		//định dạng tổng giờ làm
		var decimalHourRenderer = new DefaultTableCellRenderer() {
			private final java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				var text = "";
				if (value instanceof Number) {
					text = df.format(((Number) value).doubleValue());
				} else if (value != null) {
					text = value.toString();
				}

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, text, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setBackground(isSelected ? new Color(232, 240, 254) : Color.WHITE);
				lbl.setForeground(Color.BLACK);
				return lbl;
			}
		};

		table.getColumnModel().getColumn(9).setCellRenderer(decimalHourRenderer);


		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
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
				lbl.setOpaque(true);

				RowSorter<?> sorter = table.getRowSorter();
				if (sorter != null && !sorter.getSortKeys().isEmpty()) {
					var sortKey = sorter.getSortKeys().get(0);
					if (sortKey.getColumn() == column) {
						switch (sortKey.getSortOrder()) {
						case ASCENDING -> lbl.setText(value + " ▲");
						case DESCENDING -> lbl.setText(value + " ▼");
						default -> lbl.setText(value.toString());
						}
					} else {
						lbl.setText(value.toString());
					}
				}

				return lbl;
			}
		};

		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}
		t.getTableHeader().setPreferredSize(new Dimension(0, 45));
	}


	private Object[] getRow(int r) {
		var d = new Object[model.getColumnCount()];
		for (var i = 0; i < d.length; i++) {
			d[i] = model.getValueAt(r, i);
		}
		return d;
	}

	private void setupColumnWidths(JTable table) {
		int[] widths = {40, 100, 40, 50, 50, 50, 50, 50, 80, 80};
		for (var i = 0; i < widths.length; i++) {
			var col = table.getColumnModel().getColumn(i);
			col.setMinWidth(20);
			col.setPreferredWidth(widths[i]);
			col.setMaxWidth(Integer.MAX_VALUE);
		}
	}

	private void search() {
		if (isInitializing) {
			return;
		}

		loadPayrollTable();
	}


	private void loadPayrollTable() {
		var keyword = txtSearch.getText().trim();
		if (keyword.isEmpty() || "Tìm kiếm theo tên nhân viên...".equals(keyword)) {
			keyword = null;
		}

		var monthStr = (String) monthFilter.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Tháng", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}

		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		Integer restaurantId = null;
		if (selectedRestaurant != null && selectedRestaurant.getId() != 0) {
			restaurantId = selectedRestaurant.getId();
		}
		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall("{CALL SP_GetMonthlySummary(?,?,?,?)}")) {

			stmt.setInt(1, month);
			stmt.setInt(2, year);
			if (keyword == null || keyword.trim().isEmpty()) {
				stmt.setNull(3, java.sql.Types.NVARCHAR);
			} else {
				stmt.setString(3, keyword.trim());
			}
			if (restaurantId == null) {
				stmt.setNull(4, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(4, restaurantId);
			}
			var rs = stmt.executeQuery();
			model.setRowCount(0);

			while (rs.next()) {
				var row = new Object[11];
				row[0] = "NV" + String.format("%03d", rs.getInt("employee_id"));
				row[1] = rs.getString("name");
				var sqlDate = rs.getDate("dob");
				row[2] = (sqlDate != null) ? new java.util.Date(sqlDate.getTime()) : null;
				row[3] = rs.getString("phone");
				row[4] =rs.getString("restaurant_name");
				row[5] = rs.getInt("month") + " / " + rs.getInt("year");
				row[6] = rs.getDouble("total_come_late");
				row[7] = rs.getString("total_early_leave");
				row[8] = rs.getString("total_over_time");
				var totalWorkTimeMinutes = rs.getDouble("total_work_time");
				var totalWorkTimeHours = totalWorkTimeMinutes / 60.0;
				row[9] = totalWorkTimeHours;
				row[10] = rs.getDouble("final_salary");
				model.addRow(row);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu bảng lương: " + ex.getMessage());
		}
	}
	private void loadPayrollDataByRestaurant(int restaurantId, String keyword) {

	}

	private void addNew() {
		if (!java.beans.Beans.isDesignTime()) {
			formPanel.setAddMode(true);
			formPanel.setVisible(true);
			btnAdd.setVisible(false);
			formPanel.focusFirst();
		}
	}

	private void deleteRow() {
		if (!java.beans.Beans.isDesignTime()) {
			var r = table.getSelectedRow();
			if (r != -1) {
				var cf = JOptionPane.showConfirmDialog(this, "Xóa bản ghi lương này?", "Xác nhận",
						JOptionPane.YES_NO_OPTION);
				if (cf == JOptionPane.YES_OPTION) {
					model.removeRow(r);
					formPanel.setVisible(false);
					btnAdd.setVisible(true);
					JOptionPane.showMessageDialog(this, "Đã xóa thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void printPDF() {
		if (!java.beans.Beans.isDesignTime()) {
			try {
				var h = new MessageFormat("BẢNG LƯƠNG NHÂN VIÊN");
				var f = new MessageFormat("Trang {0}");
				table.print(JTable.PrintMode.FIT_WIDTH, h, f);
				JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void onSave(ActionEvent e) {
		if (!java.beans.Beans.isDesignTime()) {
			var cmd = e.getActionCommand();
			var data = formPanel.getFormData();
			if ("add".equals(cmd)) {
				var newId = model.getRowCount() + 1;
				data[0] = newId;
				model.addRow(data);
				JOptionPane.showMessageDialog(this, "Thêm bảng lương thành công!", "Thành Công",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				var r = formPanel.getEditingRow();
				if (r != -1) {
					for (var i = 1; i < model.getColumnCount(); i++) {
						model.setValueAt(data[i], r, i);
					}
					JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành Công",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
			formPanel.setVisible(false);
			btnAdd.setVisible(true);
		}
	}

	private void onCancel(ActionEvent e) {
		if (!java.beans.Beans.isDesignTime()) {
			formPanel.setVisible(false);
			btnAdd.setVisible(true);
		}
	}
	private void renderRestaurant() {

		try {
			var restaurantService = new RestaurantService();
			var restaurants = restaurantService.getAll();
			resFilter.removeAllItems();
			resFilter.addItem(new Restaurant(0, "Tất Cả Nhà Hàng", 0));
			for (Restaurant r : restaurants) {
				resFilter.addItem(r);
			}
			resFilter.setSelectedIndex(-1);
			isInitializing = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Lỗi tải danh sách Nhà Hàng: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void onRestaurantSelected() {
		if (isInitializing) {
			return;
		}
		loadPayrollTable();
	}
}
