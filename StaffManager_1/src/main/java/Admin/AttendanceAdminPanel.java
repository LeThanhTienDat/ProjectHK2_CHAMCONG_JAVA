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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.service.AttendanceService;

public class AttendanceAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private JTextField txtSearch;
	private JComboBox<String> cmbMonthYear;
	private final AttendanceService service = new AttendanceService();

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color WARNING_ORANGE = new Color(255, 152, 0);

	public AttendanceAdminPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));

		var searchPanel = createSearchPanel();
		var tablePanel = createTableCard();
		var actionPanel = createActionPanel();

		add(searchPanel, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(actionPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(this::initData);
	}

	public void initData() {
		if (cmbMonthYear == null) {
			return;
		}

		var currentMonthYear = java.time.LocalDate.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
		var found = false;
		for (var i = 0; i < cmbMonthYear.getItemCount(); i++) {
			if (currentMonthYear.equals(cmbMonthYear.getItemAt(i))) {
				found = true;
				break;
			}
		}
		if (!found) {
			cmbMonthYear.addItem(currentMonthYear);
		}
		cmbMonthYear.setSelectedItem(currentMonthYear);

		updateTableHeaderAndData();
		cmbMonthYear.addActionListener(e -> updateTableHeaderAndData());
	}

	public JPanel createSearchPanel() {
		var p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		p.setOpaque(true);
		p.setBackground(CARD_WHITE);
		p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		p.setPreferredSize(new Dimension(0, 70));

		var year = java.time.Year.now().getValue();
		var months = new String[12];
		for (var i = 0; i < 12; i++) {
			months[i] = String.format("%02d/%d", i + 1, year);
		}

		cmbMonthYear = new JComboBox<>(months);
		cmbMonthYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbMonthYear.setBackground(Color.WHITE);
		cmbMonthYear.setPreferredSize(new Dimension(140, 36));

		txtSearch = styledField("Tìm kiếm theo tên nhân viên...", 300);
		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
			private void update() { SwingUtilities.invokeLater(() -> updateTableHeaderAndData()); }
		});

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 120);
		btnSearch.addActionListener(e -> updateTableHeaderAndData());

		p.add(new JLabel("Tháng/Năm: "));
		p.add(cmbMonthYear);
		p.add(Box.createHorizontalStrut(10));
		p.add(txtSearch);
		p.add(btnSearch);
		return p;
	}

	public void updateTableHeaderAndData() {
		var selected = (String) cmbMonthYear.getSelectedItem();
		if (selected == null || selected.isEmpty()) {
			return;
		}

		var parts = selected.split("/");
		var month = parseIntSafe(parts[0]);
		var year = parseIntSafe(parts[1]);

		List<String> headers = service.buildAttendanceHeader(year, month);
		var allData = service.getAttendanceByMonth(year, month);
		var query = txtSearch.getText().trim().toLowerCase();

		List<Object[]> displayData = new ArrayList<>();
		for (Object[] row : allData) {
			var tmp = row.clone();
			if (!query.isEmpty()) {
				var name = row[2] != null ? row[2].toString().toLowerCase() : "";
				if (!name.contains(query)) {
					for (var i = 5; i < tmp.length; i++) {
						tmp[i] = tmp[i] != null ? tmp[i].toString().toUpperCase() : "";
					}
				}
			}
			displayData.add(tmp);
		}


		SwingUtilities.invokeLater(() -> {
			model.setDataVector(displayData.toArray(new Object[0][]), headers.toArray());
			autoResizeColumns(table);
			DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
					lbl.setForeground(Color.BLACK);

					var totalCols = table.getColumnCount();
					var cellText = value != null ? value.toString().toUpperCase() : "";

					// Highlight theo ký hiệu
					switch (cellText) {
					case "X":
						lbl.setBackground(new Color(200, 230, 201)); // xanh nhạt
						break;
					case "P":
						lbl.setBackground(new Color(255, 235, 59)); // vàng
						break;
					case "W":
						lbl.setBackground(new Color(179, 229, 252)); // xanh dương nhạt
						break;
					case "L":
					case "N":
					case "T":
						lbl.setBackground(new Color(255, 255, 255));
						break;
					default:
						lbl.setBackground(Color.WHITE);
						break;
					}

					// Cột ngày (5 → totalCols-4) highlight đỏ nếu đi trễ >5 hoặc về sớm >5
					if (column >= 5 && column < totalCols - 4 && !cellText.isEmpty()) {
						var totalLate = parseIntSafe(table.getValueAt(row, totalCols - 4));
						var totalEarly = parseIntSafe(table.getValueAt(row, totalCols - 3));

						if (totalLate > 5 || totalEarly > 5) {
							lbl.setBackground(new Color(255, 102, 102));
						}
					}

					if (isSelected) {
						lbl.setBackground(new Color(227, 242, 253));
					}

					lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY));
					lbl.setText(cellText);
					return lbl;
				}
			};

			for (var i = 5; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(dayRenderer);
			}
		});
	}


	private void autoResizeColumns(JTable table) {
		final var header = table.getTableHeader();
		final var columnModel = table.getColumnModel();

		for (var col = 0; col < table.getColumnCount(); col++) {
			var width = 50;
			var headerRenderer = header.getDefaultRenderer();
			var headerValue = table.getColumnName(col);

			var compHeader = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, -1, col);
			width = Math.max(width, compHeader.getPreferredSize().width + 16);

			for (var row = 0; row < Math.min(10, table.getRowCount()); row++) {
				var renderer = table.getCellRenderer(row, col);
				var comp = renderer.getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col);
				width = Math.max(width, comp.getPreferredSize().width + 12);
			}

			columnModel.getColumn(col).setPreferredWidth(width);
		}

		header.repaint();
	}


	public int parseIntSafe(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof Integer i) {
			return i;
		}
		try {
			return Integer.parseInt(obj.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void performSearch() {
		var query = txtSearch.getText().toLowerCase();
		if (query.isEmpty()) {
			return;
		}
		for (var i = 0; i < model.getRowCount(); i++) {
			var name = model.getValueAt(i, 2).toString().toLowerCase();
			if (name.contains(query)) {
				table.setRowSelectionInterval(i, i);
				table.scrollRectToVisible(table.getCellRect(i, 0, true));
				JOptionPane.showMessageDialog(this, "Tìm thấy kết quả cho: " + query + " (Demo lọc theo tên nhân viên)");
				return;
			}
		}
		JOptionPane.showMessageDialog(this, "Không tìm thấy: " + query);
	}

	public JTextField styledField(String ph, int w) {
		var f = new JTextField(ph);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		f.setPreferredSize(new Dimension(w, 36));
		return f;
	}

	public JPanel createTableCard() {
		var card = new JPanel(new BorderLayout());
		card.setBorder(new EmptyBorder(20, 25, 20, 25));
		card.setBackground(CARD_WHITE);

		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		var headerLabel = new JLabel("BẢNG CHẤM CÔNG TỔNG HỢP", SwingConstants.CENTER);
		headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		headerLabel.setForeground(PRIMARY_BLUE);
		headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

		var legendPanel = createLegendPanel();
		topPanel.add(headerLabel, BorderLayout.NORTH);
		topPanel.add(legendPanel, BorderLayout.CENTER);

		card.add(topPanel, BorderLayout.NORTH);

		model = new DefaultTableModel();
		table = new JTable(model) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);
		styleTable(table);

		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn

		// Panel bao ngoài scroll (để có padding + scroll riêng cho bảng)
		var tableWrapper = new JPanel(new BorderLayout());
		tableWrapper.setOpaque(false);
		tableWrapper.add(sp, BorderLayout.CENTER);

		// Thêm tableWrapper trực tiếp vào card, bỏ scroll ngoài
		card.add(tableWrapper, BorderLayout.CENTER);


		card.add(tableWrapper, BorderLayout.CENTER);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					var row = table.getSelectedRow();
					var col = table.getSelectedColumn();

					var startDayColumn = 5; // cột ngày bắt đầu từ index 5 (0-based)
					if (row >= 0 && col >= startDayColumn) {
						// Lấy tháng/năm từ combo
						var selected = (String) cmbMonthYear.getSelectedItem();
						var parts = selected.split("/");
						var month = Integer.parseInt(parts[0]);
						var year = Integer.parseInt(parts[1]);
						var ym = java.time.YearMonth.of(year, month);
						var daysInMonth = ym.lengthOfMonth();

						// Kiểm tra cột ngày
						var dayIndex = col - startDayColumn + 1; // cột đầu tiên = ngày 1
						if (dayIndex >= 1 && dayIndex <= daysInMonth) {
							openAttendanceForm(row, col);
						}
					}
				}
			}
		});
		return card;
	}

	public JPanel createLegendPanel() {
		var legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		legend.setBackground(CARD_WHITE);
		legend.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(10, 0, 10, 0)));

		String[][] legends = {
				{"X", "Làm việc bình thường (Có mặt)"},
				{"P", "Phép năm"},
				{"L", "Lễ/Tết"},
				{"N", "Nghỉ không lương"},
				{"W", "WFH (Làm việc từ xa)"},
				{"T", "Tăng ca"}
		};

		for (String[] lg : legends) {
			var icon = new JLabel(lg[0]);
			icon.setFont(new Font("Segoe UI", Font.BOLD, 12));
			icon.setForeground(PRIMARY_BLUE);
			icon.setPreferredSize(new Dimension(20, 20));
			icon.setToolTipText(lg[1]);

			var desc = new JLabel(lg[1]);
			desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			desc.setForeground(TEXT_PRIMARY);

			var item = new JPanel(new BorderLayout(5, 0));
			item.add(icon, BorderLayout.WEST);
			item.add(desc, BorderLayout.CENTER);
			legend.add(item);
		}

		var summaryLegend = new JLabel("Tổng hợp: ");
		summaryLegend.setFont(new Font("Segoe UI", Font.BOLD, 12));
		summaryLegend.setForeground(SUCCESS_GREEN);
		legend.add(summaryLegend);

		var cnLuong = new JLabel("CN Có Lương (Xanh)");
		cnLuong.setForeground(SUCCESS_GREEN);
		legend.add(cnLuong);

		var cnKhongLuong = new JLabel("CN Không Lương (Đỏ)");
		cnKhongLuong.setForeground(DANGER_RED);
		legend.add(cnKhongLuong);

		return legend;
	}

	public void styleTable(JTable t) {
		var h = t.getTableHeader();
		h.setFont(new Font("Segoe UI", Font.BOLD, 12));
		h.setBackground(PRIMARY_BLUE);
		h.setForeground(Color.WHITE);
		h.setPreferredSize(new Dimension(0, 45));

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
				return lbl;
			}
		};
		h.setDefaultRenderer(renderer);

		DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus,
					int row, int column) {

				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

				var val = value != null ? value.toString().toUpperCase() : "";

				var startDayCol = 5;
				var endDayCol = table.getColumnCount() - 5;

				if (column >= startDayCol && column < endDayCol) {
					try {
						var day = column - startDayCol + 1;
						var empId = (int) table.getValueAt(row, 0);

						var parts = ((String)cmbMonthYear.getSelectedItem()).split("/");
						var month = Integer.parseInt(parts[0]);
						var year = Integer.parseInt(parts[1]);

						var schedules = service.getWorkSchedules(empId, year, month, day);

						if (schedules != null && !schedules.isEmpty()) {
							var ws = schedules.get(0); // giả sử 1 ca/1 ngày

							var highlightRed = false;

							if (ws.getCheckInTime() != null && ws.isComeLate()) {
								// check trễ > 5 phút
								if (ws.getTimeLateMinutes() > 5) {
									highlightRed = true;
								}
							}

							if (ws.getCheckOutTime() != null && ws.isEarlyLeave()) {
								// check về sớm > 5 phút
								if (ws.getEarlyLeaveMinutes() > 5) {
									highlightRed = true;
								}
							}

							if (highlightRed) {
								lbl.setBackground(DANGER_RED);
							} else {
								// tô theo ký hiệu
								switch (val) {
								case "X": lbl.setBackground(new Color(200, 230, 201)); break;
								case "P": lbl.setBackground(new Color(255, 235, 59)); break;
								case "W": lbl.setBackground(new Color(179, 229, 252)); break;
								case "L":
								case "N":
								case "T":
									lbl.setBackground(CARD_WHITE); break;
								default: lbl.setBackground(CARD_WHITE);
								}
							}

						} else {
							lbl.setBackground(CARD_WHITE);
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						lbl.setBackground(CARD_WHITE);
					}
				} else {
					lbl.setBackground(CARD_WHITE);
				}

				if (isSelected) {
					lbl.setBackground(new Color(227, 242, 253));
				}

				lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, BORDER_COLOR));
				lbl.setText(val);
				return lbl;
			}
		};




		// Apply renderer cho các cột ngày
		for (var i = 5; i < table.getColumnCount() - 5; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dayRenderer);
		}


		// apply chỉ cho cột ngày
		for (var i = 5; i < table.getColumnCount() - 5; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dayRenderer);
		}

		// áp dụng renderer nhưng bảo vệ nếu chưa có đủ cột
		var cols = t.getColumnCount();
		for (var i = 5; i < Math.min(36, cols); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dayRenderer);
		}

		DefaultTableCellRenderer summaryRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
				lbl.setForeground(TEXT_PRIMARY);
				lbl.setBackground(CARD_WHITE);

				var colIdx = table.convertColumnIndexToModel(column);
				if (colIdx >= 42 && colIdx <= 46) {
					lbl.setBackground(new Color(200, 230, 201));
					lbl.setForeground(SUCCESS_GREEN);
				} else if (colIdx >= 48 && colIdx <= 51) {
					lbl.setBackground(new Color(248, 215, 218));
					lbl.setForeground(DANGER_RED);
				}

				if (isSelected) {
					lbl.setBackground(new Color(227, 242, 253));
				}
				lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, BORDER_COLOR));
				return lbl;
			}
		};

		for (var i = 36; i < cols; i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(summaryRenderer);
		}

		t.setRowHeight(35);
		t.setSelectionBackground(new Color(227, 242, 253));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(BORDER_COLOR);
		t.setShowGrid(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}


	public JPanel createActionPanel() {
		var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panel.setBackground(BG_LIGHT);

		var btnPDF = createButton("Xuất PDF", TEAL, 130);
		btnPDF.addActionListener(e -> printPDF());
		var btnDelete = createButton("Xóa Dòng", DANGER_RED, 130);
		btnDelete.addActionListener(e -> deleteRow());

		var btnApprove = createButton("Duyệt Chấm Công", SUCCESS_GREEN, 150);
		btnApprove.addActionListener(e -> approveAttendance());

		var btnLegend = createButton("Ký Hiệu Chấm Công", WARNING_ORANGE, 150);
		btnLegend.addActionListener(e -> showLegendDialog());

		panel.add(btnDelete);
		panel.add(btnPDF);
		panel.add(btnApprove);
		panel.add(btnLegend);
		return panel;
	}

	public static JButton createButton(String text, Color bg, int w) {
		JButton b = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
						(getHeight() + fm.getAscent() - fm.getDescent()) / 2);
			}
		};
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 36));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		return b;
	}

	public void showLegendDialog() {
		var dialog = new AttendanceLegendDialog(this);
		dialog.setVisible(true);
	}

	public void deleteRow() {
		var r = table.getSelectedRow();
		if (r == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Xóa dòng này? (Sẽ xóa dữ liệu chấm công của nhân viên)", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			model.removeRow(r);
		}
	}

	public void approveAttendance() {
		var selectedRows = table.getSelectedRowCount();
		if (selectedRows == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần duyệt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Duyệt chấm công cho " + selectedRows + " nhân viên (Demo: Cập nhật trạng thái duyệt trong DB)");
	}

	public void printPDF() {
		try {
			var h = new MessageFormat("BẢNG CHẤM CÔNG - " + cmbMonthYear.getSelectedItem());
			var f = new MessageFormat("Trang {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi PDF: " + ex.getMessage());
		}
	}

	public void openAttendanceForm(int row, int col) {
		var employeeName = model.getValueAt(row, 2).toString();
		var dateHeader = model.getColumnName(col); // ví dụ "01/11"

		// Lấy employeeId từ tên
		var empId = service.getEmployeeIdByName(employeeName);

		// Lấy năm hiện tại từ combo
		var selected = (String) cmbMonthYear.getSelectedItem();
		var parts = selected.split("/");
		var month = Integer.parseInt(parts[0]);
		var year = Integer.parseInt(parts[1]);

		// Tách ngày/tháng từ header ("01/11")
		var headerParts = dateHeader.split("/");
		var day = Integer.parseInt(headerParts[0]);
		var monthFromHeader = Integer.parseInt(headerParts[1]);

		// Dự phòng nếu tháng trong header khác tháng chọn (rất hiếm)
		if (month != monthFromHeader) {
			month = monthFromHeader;
		}

		// Tạo ngày chuẩn định dạng yyyy-MM-dd
		var formattedDate = String.format("%04d-%02d-%02d", year, month, day);

		// Lấy ca làm của nhân viên trong ngày
		var shifts = service.getShiftsForEmployee(employeeName, formattedDate);

		// Hiển thị form (chỉ xem, không nhập tay)
		var dialog = new javax.swing.JDialog(SwingUtilities.getWindowAncestor(this),
				"Lịch làm của " + employeeName + " (" + formattedDate + ")");
		dialog.getContentPane().setLayout(new BorderLayout());

		var formPanel = new AttendanceFormPanel(e -> {
			JOptionPane.showMessageDialog(this,
					"Đã xác nhận chấm công cho " + employeeName + " ngày " + formattedDate,
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			dialog.dispose();
		}, e -> dialog.dispose());

		final var finalYear = year;
		final var finalMonth = month;

		formPanel.setOnDataChanged(() -> {
			service.clearCache(finalYear, finalMonth);
			updateTableHeaderAndData();
		});

		// Gửi danh sách ca làm để hiển thị
		var dayStatusList = service.getDayWorkStatus(employeeName, formattedDate);
		formPanel.showEmployeeSchedule(empId,employeeName, formattedDate, dayStatusList);

		dialog.getContentPane().add(formPanel, BorderLayout.CENTER);
		dialog.setSize(420, 400);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
}
