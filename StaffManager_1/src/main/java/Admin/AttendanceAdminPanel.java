package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.AttendanceLockStatusService;
import com.example.swingapp.service.AttendanceService;
import com.example.swingapp.service.OTJunctionService;
import com.example.swingapp.service.RestaurantService;

public class AttendanceAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private JTextField txtSearch;
	private JComboBox<String> cmbMonthYear;
	private JComboBox<Restaurant> resFilter;
	private boolean isInitializing = true;
	private NotifiedButtonPanel btnApproveWrapper;
	private final AttendanceService service = new AttendanceService();
	private final OTJunctionService otJunctionService = new OTJunctionService();
	private final AttendanceLockStatusService lockService = new AttendanceLockStatusService(); // Giả định có LockService
	private String lockMonthYear = "";


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
		var lockTime = lockService.getLast();
		if(lockTime != null) {
			lockMonthYear = lockTime.getLockedMonth() +" / "+lockTime.getLockedYear();
		}
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

		cmbMonthYear = new JComboBox<>();
		cmbMonthYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbMonthYear.setBackground(new Color(248, 250, 252));
		cmbMonthYear.setPreferredSize(new Dimension(200, 36));
		cmbMonthYear.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		var now = LocalDate.now();
		for (var i = 14; i >= 0; i--) {
			var month = now.minusMonths(i);
			var item = String.format("%d / %d", month.getMonthValue(), month.getYear());
			cmbMonthYear.addItem(item);
		}
		cmbMonthYear.setSelectedIndex(14);
		//		cmbMonthYear.addActionListener(e -> onRestaurantSelected());

		txtSearch = styledField("Search by employee name...", 300);
		txtSearch.setColumns(30);
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals("Search by employee name...")) {
					txtSearch.setText("");
					txtSearch.setForeground(TEXT_PRIMARY);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText("Search by employee name...");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});

		var btnSearch = createButton("Search", PRIMARY_BLUE, 120);
		btnSearch.addActionListener(e -> updateTableHeaderAndData());

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(Color.WHITE);
		resFilter.setPreferredSize(new Dimension(140, 36));
		resFilter.addActionListener(e -> updateTableHeaderAndData());
		renderRestaurant();


		p.add(txtSearch);
		p.add(new JLabel("Month/Year: "));
		p.add(cmbMonthYear);
		p.add(new JLabel("Restaurant: "));
		p.add(resFilter);
		p.add(Box.createHorizontalStrut(10));
		p.add(btnSearch);
		return p;
	}

	public void updateTableHeaderAndData() {
		var monthStr = (String) cmbMonthYear.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Month", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = selectedRestaurant != null ? selectedRestaurant.getId() : 0;

		List<String> headers = service.buildAttendanceHeader(year, month);
		var allData = service.getAttendanceByMonth(year, month);
		var query = txtSearch.getText();
		if (query == null) {
			query = "";
		}
		if (query.isEmpty() || "Search by employee name...".equalsIgnoreCase(query)) {
			query = null;
		}
		List<Object[]> displayData = new ArrayList<>();
		for (Object[] row : allData) {
			var tmp = row.clone();
			Integer rowRestaurantId = null;
			if (row[5] instanceof Integer) {
				rowRestaurantId = (Integer) row[5];
			}

			if (restaurantId != 0) {
				if (rowRestaurantId == null || !rowRestaurantId.equals(restaurantId)) {
					continue;
				}
			}


			if (query != null && !query.isEmpty()) {
				var employeeName = row[2] != null ? normalizeString(row[2].toString()) : "";
				if (!employeeName.contains(query)) {
					continue;
				}
			}
			displayData.add(tmp);
		}


		SwingUtilities.invokeLater(() -> {
			model.setDataVector(displayData.toArray(new Object[0][]), headers.toArray());
			autoResizeColumns(table);
			var restaurantIdColIndex = -1;
			for (var i = 0; i < table.getColumnCount(); i++) {
				if ("restaurant_id".equalsIgnoreCase(table.getColumnName(i))) {
					restaurantIdColIndex = i;
					break;
				}
			}
			if (restaurantIdColIndex != -1) {
				table.removeColumn(table.getColumnModel().getColumn(restaurantIdColIndex));
			}

			var totalCols = table.getColumnCount();
			table.getColumnModel().getColumn(0).setPreferredWidth(40);
			table.getColumnModel().getColumn(1).setPreferredWidth(70);
			table.getColumnModel().getColumn(2).setPreferredWidth(150);
			table.getColumnModel().getColumn(3).setPreferredWidth(70);
			table.getColumnModel().getColumn(4).setPreferredWidth(80);
			var startDayColumn = 5;
			var endDayColumn = totalCols - 4;
			var dayWidth = 60;

			for (var i = startDayColumn; i < endDayColumn; i++) {
				table.getColumnModel().getColumn(i).setPreferredWidth(dayWidth);
			}
			var summaryWidth = 80;
			table.getColumnModel().getColumn(totalCols - 4).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 3).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 2).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 1).setPreferredWidth(summaryWidth);

			// 1. Tạo Renderer Căn Giữa
			var centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			centerRenderer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

			DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
					lbl.setForeground(Color.BLACK);

					var cellText = value != null ? value.toString().toUpperCase() : "";
					var shiftNameDisplay = cellText;
					var statusKey = "";

					if (cellText.contains("|")) {
						var parts = cellText.split("\\|");
						if (parts.length == 2) {
							shiftNameDisplay = parts[0];
							statusKey = parts[1];
						}
					}

					var isFuture = isFutureDateColumn(table, column);
					lbl.setBackground(Color.WHITE);
					lbl.setForeground(Color.BLACK);

					switch (statusKey.toUpperCase()) {
					case "T":
						lbl.setBackground(DANGER_RED.brighter());
						lbl.setForeground(Color.WHITE);
						break;
					case "X":
						lbl.setBackground(SUCCESS_GREEN.brighter().brighter());
						lbl.setForeground(TEXT_PRIMARY);
						break;
					case "V":
						lbl.setBackground(WARNING_ORANGE.brighter().brighter());
						lbl.setForeground(TEXT_PRIMARY);
						break;
					case "*":
						lbl.setBackground(new Color(248, 215, 218));
						lbl.setForeground(TEXT_PRIMARY);
						break;
					default:
						break;
					}

					if (shiftNameDisplay.equals("OT")) {
						if (statusKey.equalsIgnoreCase("X")) {
							lbl.setBackground(TEAL.brighter());
							lbl.setForeground(Color.WHITE);
						}
					}

					var totalCols = table.getColumnCount();
					if (column >= 5 && column < totalCols - 4 && !cellText.isEmpty()) {
						var totalLate = parseIntSafe(table.getValueAt(row, totalCols - 4));
						var totalEarly = parseIntSafe(table.getValueAt(row, totalCols - 3));

						if (totalLate > 6 || totalEarly > 6) {
							lbl.setBackground(new Color(255, 102, 102));
						}
					}

					if (isSelected) {
						lbl.setBackground(new Color(227, 242, 253));
					}

					lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY));
					lbl.setText(shiftNameDisplay);
					return lbl;
				}
			};
			endDayColumn = table.getColumnCount() - 4;
			for (var i = 5; i < endDayColumn; i++) {
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
		card.setBorder(new EmptyBorder(15, 15, 15, 15));
		card.setBackground(CARD_WHITE);

		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		var headerLabel = new JLabel("ATTENDANCE SUMMARY TABLE", SwingConstants.LEFT);
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
		sp.getVerticalScrollBar().setUnitIncrement(16);
		var tableWrapper = new JPanel(new BorderLayout());
		tableWrapper.setOpaque(false);
		tableWrapper.add(sp, BorderLayout.CENTER);
		card.add(tableWrapper, BorderLayout.CENTER);
		card.add(tableWrapper, BorderLayout.CENTER);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (isCurrentMonthLocked()) {
						JOptionPane.showMessageDialog(AttendanceAdminPanel.this,
								"Cannot modify attendance. The data for this month is **LOCKED**.",
								"Data Locked", JOptionPane.WARNING_MESSAGE);
						return;
					}
					var row = table.getSelectedRow();
					var col = table.getSelectedColumn();

					var startDayColumn = 5;
					if (row >= 0 && col >= startDayColumn) {
						var selected = (String) cmbMonthYear.getSelectedItem();
						int month = 0, year = 0;
						if (selected != null && selected.contains("/")) {
							var parts = selected.replace("Month", "").split("/");
							month = Integer.parseInt(parts[0].trim());
							year = Integer.parseInt(parts[1].trim());
						}
						var ym = java.time.YearMonth.of(year, month);
						var daysInMonth = ym.lengthOfMonth();
						var dayIndex = col - startDayColumn + 1;
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
				{"A1,A2,...: ", "Shift code"}
		};

		for (String[] lg : legends) {
			var icon = new JLabel(lg[0]);
			icon.setFont(new Font("Segoe UI", Font.BOLD, 12));
			icon.setForeground(PRIMARY_BLUE);
			icon.setPreferredSize(new Dimension(60, 20));
			icon.setToolTipText(lg[1]);

			var desc = new JLabel(lg[1]);
			desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			desc.setForeground(TEXT_PRIMARY);

			var item = new JPanel(new BorderLayout(5, 0));
			item.add(icon, BorderLayout.WEST);
			item.add(desc, BorderLayout.CENTER);
			legend.add(item);
		}

		var summaryLegend = new JLabel("Note: ");
		summaryLegend.setFont(new Font("Segoe UI", Font.BOLD, 12));
		summaryLegend.setForeground(SUCCESS_GREEN);
		legend.add(summaryLegend);

		var cnHasCheckInOut = new JLabel("On-time attendance (Green)");
		cnHasCheckInOut.setForeground(SUCCESS_GREEN);
		legend.add(cnHasCheckInOut);

		var cnCheckMissTime = new JLabel("Late/Early leave (Red)");
		cnCheckMissTime.setForeground(DANGER_RED);
		legend.add(cnCheckMissTime);

		var cnEmptyCheckIn = new JLabel("New Shift (empty check in) (Pink)");
		cnEmptyCheckIn.setForeground(new Color(248, 215, 218).darker());
		legend.add(cnEmptyCheckIn);

		var cnWaiting = new JLabel("Missing attendance (Yellow)");
		cnWaiting.setForeground(WARNING_ORANGE.brighter());
		legend.add(cnWaiting);

		return legend;
	}

	public void styleTable(JTable t) {
		var h = t.getTableHeader();
		var cols = t.getColumnCount();
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
		var now = java.time.LocalDate.now();
		var previousMonth = now.minusMonths(1);
		var getMonth = previousMonth.getMonthValue();
		var showMonth = returnMonth(getMonth);
		var btnLockAttendance = createButton("Lock for "+ showMonth, TEAL, 130);
		btnLockAttendance.addActionListener(e -> handleLockAttendance());
		var btnDelete = createButton("Delete", DANGER_RED, 130);
		btnDelete.addActionListener(e -> deleteRow());
		var btnApprove = createButton("Approve Attendance", SUCCESS_GREEN, 150);
		btnApprove.addActionListener(e -> openOtConfirmForm());
		btnApproveWrapper = new NotifiedButtonPanel(btnApprove);

		var btnLegend = createButton("Attendance Legend", WARNING_ORANGE, 150);
		btnLegend.addActionListener(e -> showLegendDialog());

		panel.add(btnDelete);
		panel.add(btnLockAttendance);
		panel.add(btnApproveWrapper);
		panel.add(btnLegend);
		updateApprovalBadgeCount();
		return panel;
	}

	public static JButton createButton(String text, Color bg, int w) {
		JButton b = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var fillColor = bg;
				if (getModel().isPressed()) {
					fillColor = bg.darker();
				} else if (getModel().isRollover()) {
					fillColor = bg.brighter();
				}
				g2.setColor(fillColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
				g2.setColor(new Color(0, 0, 0, 20));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				var textWidth = fm.stringWidth(getText());
				var textHeight = fm.getAscent();
				g2.drawString(getText(), (getWidth() - textWidth) / 2,
						(getHeight() + textHeight - fm.getDescent()) / 2);
			}
		};
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 36));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setRolloverEnabled(true);
		b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		return b;
	}


	public void showLegendDialog() {
		var dialog = new AttendanceLegendDialog(this);
		dialog.setVisible(true);
	}

	public void deleteRow() {
		var r = table.getSelectedRow();
		if (r == -1) {
			JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Delete this row? (This will remove employee attendance data)", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			model.removeRow(r);
		}
	}

	public void approveAttendance() {

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

	public void openOtConfirmForm() {
		var dialog = new javax.swing.JDialog(SwingUtilities.getWindowAncestor(this),
				"Duyệt Ot",ModalityType.APPLICATION_MODAL);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.pack();
		dialog.setSize(1000, 1000);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		Runnable refreshAction = () -> {
			updateApprovalBadgeCount();
			updateTableHeaderAndData();
		};
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				refreshAction.run();
			}
		});
		var formPanel = new AttendanceOtConfirmPanel(e -> {
			JOptionPane.showMessageDialog(dialog,
					"Đã xác nhận chấm công cho ngày",
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			dialog.dispose();
			updateApprovalBadgeCount();
		}, e -> {
			dialog.dispose();
			updateApprovalBadgeCount();
		});
		formPanel.setOnDataChanged(refreshAction);
		formPanel.setPreferredSize(new Dimension(800, 600));
		dialog.getContentPane().add(formPanel);
		dialog.setSize(1700, 700);
		dialog.setMinimumSize(new Dimension(900, 700));
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public void openAttendanceForm(int row, int col) {
		var modelCol = table.convertColumnIndexToModel(col);
		var employeeName = model.getValueAt(row, 2).toString();
		var dateHeader = model.getColumnName(modelCol);
		var empId = service.getEmployeeIdByName(employeeName);
		var monthStr = (String) cmbMonthYear.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Month", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}
		var headerParts = dateHeader.split("/");
		var day = Integer.parseInt(headerParts[0]);
		var monthFromHeader = Integer.parseInt(headerParts[1]);
		if (month != monthFromHeader) {
			month = monthFromHeader;
		}
		var formattedDate = String.format("%04d-%02d-%02d", year, month, day);
		var shifts = service.getShiftsForEmployee(employeeName, formattedDate);

		Runnable refreshAction = () -> {
			updateTableHeaderAndData();
			updateApprovalBadgeCount();
		};

		var dialog = new javax.swing.JDialog(SwingUtilities.getWindowAncestor(this),
				"Work schedule of " + employeeName + " (" + formattedDate + ")",ModalityType.APPLICATION_MODAL);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				refreshAction.run();
			}
		});
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.pack();
		dialog.setSize(1000, 1000);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		var formPanel = new AttendanceFormPanel(e -> {
			JOptionPane.showMessageDialog(this,
					"OT attendance confirmed for " + employeeName + " Day " + formattedDate,
					"Thông báo", JOptionPane.INFORMATION_MESSAGE);
			dialog.dispose();
		}, e -> dialog.dispose());
		final var finalYear = year;
		final var finalMonth = month;
		formPanel.setOnDataChanged(() -> {
			service.clearCache(finalYear, finalMonth);
			refreshAction.run();
		});
		var dayStatusList = service.getDayWorkStatus(employeeName, formattedDate);
		formPanel.showEmployeeSchedule(empId,employeeName, formattedDate, dayStatusList);

		formPanel.setPreferredSize(new Dimension(800, 600));
		dialog.getContentPane().add(formPanel);
		dialog.setSize(900, 700);
		dialog.setMinimumSize(new Dimension(900, 700));
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

	}

	private void renderRestaurant() {

		try {
			var restaurantService = new RestaurantService();
			var restaurants = restaurantService.getAll();
			resFilter.removeAllItems();
			resFilter.addItem(new Restaurant(0, "All restaurant", 0));
			for (Restaurant r : restaurants) {
				resFilter.addItem(r);
			}
			resFilter.setSelectedIndex(0);
			isInitializing = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Error when trying to load Restaurant: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String normalizeString(String input) {
		if (input == null) {
			return "";
		}
		var normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		// Loại bỏ các ký tự dấu
		normalized = normalized.replaceAll("\\p{M}", "");
		return normalized.toLowerCase();
	}

	private boolean isFutureDateColumn(JTable table, int column) {
		var headerValue = table.getColumnModel().getColumn(column).getHeaderValue();
		if (headerValue == null) {
			return false;
		}
		var headerText = headerValue.toString().trim();
		if (!headerText.matches("\\d{1,2}/\\d{1,2}")) {
			return false;
		}

		try {
			var monthStr = (String) cmbMonthYear.getSelectedItem();
			var selectedYear = java.time.Year.now().getValue();
			if (monthStr != null && monthStr.contains("/")) {
				var parts = monthStr.replace("Month", "").split("/");
				selectedYear = Integer.parseInt(parts[1].trim());
			}
			var fullDateStr = headerText + "/" + selectedYear;
			var formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
			var columnDate = LocalDate.parse(fullDateStr, formatter);
			var today = LocalDate.now();
			return columnDate.isAfter(today);

		} catch (Exception e) {
			return false;
		}
	}

	public class NotifiedButtonPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JButton button;
		private int notificationCount = 0;
		private static final Color BADGE_COLOR = Color.RED;
		private static final Color BADGE_TEXT_COLOR = Color.WHITE;

		public NotifiedButtonPanel(JButton button) {
			this.button = button;
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			setOpaque(false);
			add(button);
			setPreferredSize(button.getPreferredSize());
		}

		public void setNotificationCount(int count) {
			notificationCount = count;
			repaint();
		}

		public JButton getButton() {
			return button;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (notificationCount > 0) {
				var g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var text = String.valueOf(notificationCount);
				var wrapperWidth = getWidth();
				var wrapperHeight = getHeight();
				var badgeSize = 18;
				var x = wrapperWidth - badgeSize;
				var y = 0;
				g2.setColor(BADGE_COLOR);
				g2.fillOval(x, y, badgeSize, badgeSize);
				g2.dispose();
			}
		}
	}
	public void updateApprovalBadgeCount() {
		var count = 0;
		var dateStr = (String) cmbMonthYear.getSelectedItem();
		LocalDate selectedDate;
		try {
			var inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
			selectedDate = LocalDate.parse(dateStr.trim(), inputFormatter);
		} catch (Exception e) {
			selectedDate = LocalDate.now();
			System.err.println("Failed to parse date from ComboBox, using current date.");
		}
		var safeFromDate = selectedDate.minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);
		var safeToDate = selectedDate.plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);

		try {
			// Lấy số lượng bản ghi OT đang chờ duyệt
			var pendingList = otJunctionService.getAllOtConfirmList( safeFromDate, safeToDate);
			count = pendingList != null ? pendingList.size() : 0;
		} catch (Exception e) {
			System.err.println("Lỗi khi đếm OT chờ duyệt: " + e.getMessage());
		}

		if (btnApproveWrapper != null) {
			btnApproveWrapper.setNotificationCount(count);

			if (count > 0) {
				btnApproveWrapper.getButton().setText("Approve OT (" + count + ")");
			} else {
				btnApproveWrapper.getButton().setText("Approve Attendance");
			}
		}
	}
	public void handleLockAttendance() {
		var now = java.time.LocalDate.now();
		var previousMonth = now.minusMonths(1);
		var showMonth = returnMonth(previousMonth.getMonthValue());
		var targetMonthYear = String.format("%d / %d", previousMonth.getMonthValue(), previousMonth.getYear());
		var confirmMsg = "Are you sure you want to LOCK attendance for " + showMonth + " ? All editing functions will be disabled for this month and all preceding months.";
		if (JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm Lock", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			// Gọi service để khóa tháng mục tiêu
			lockService.lockMonthYear(previousMonth.getMonthValue(), previousMonth.getYear());

			// Cập nhật biến cờ toàn cục
			lockMonthYear = targetMonthYear;

			JOptionPane.showMessageDialog(this, "Attendance for " + targetMonthYear + " has been **LOCKED**.", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	public String  returnMonth(int month) {
		return switch (month) {
		case 1 -> "January";
		case 2 -> "February";
		case 3 -> "March";
		case 4 -> "April";
		case 5 -> "May";
		case 6 -> "June";
		case 7 -> "July";
		case 8 -> "August";
		case 9 -> "September";
		case 10 -> "October";
		case 11 -> "November";
		case 12 -> "December";
		default -> "Invalid month";
		};
	}
	private java.time.YearMonth parseMonthYear(String monthYearStr) {
		if (monthYearStr == null || !monthYearStr.matches("\\d{1,2}\\s/\\s\\d{4}")) {
			return null;
		}
		try {
			var parts = monthYearStr.split("\\s/\\s");
			var month = Integer.parseInt(parts[0].trim());
			var year = Integer.parseInt(parts[1].trim());
			return java.time.YearMonth.of(year, month);
		} catch (Exception e) {
			return null;
		}
	}
	private boolean isMonthLocked(String targetMonthYearStr) {
		if (lockMonthYear == null || lockMonthYear.isEmpty()) {
			return false;
		}

		var targetYM = parseMonthYear(targetMonthYearStr);
		var lockedYM = parseMonthYear(lockMonthYear);

		if (targetYM == null || lockedYM == null) {
			return false;
		}

		// Khóa nếu tháng đang xem nhỏ hơn hoặc bằng tháng đã khóa
		return !targetYM.isAfter(lockedYM);
	}
	private boolean isCurrentMonthLocked() {
		var selected = (String) cmbMonthYear.getSelectedItem();
		return isMonthLocked(selected);
	}

}
