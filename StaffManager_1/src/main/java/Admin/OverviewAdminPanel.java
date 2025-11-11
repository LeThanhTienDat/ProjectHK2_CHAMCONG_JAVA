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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

public class OverviewAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private DefaultTableModel attendanceModel;
	private JTable tableAttendance;
	private JTextField txtSearchAttendance;
	private JComboBox<String> cmbDate;
	private JComboBox<Restaurant> resFilter;
	private JButton btnSearch;
	private JPanel tableCard;
	private JPanel currentLegendPanel;
	private int totalEmployees = 0;
	private int totalNotContract = 0;
	private boolean isInitializing = true;
	private String showDate;
	private JLabel headerLabel;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color WARNING_ORANGE = new Color(255, 152, 0);


	public OverviewAdminPanel() {
		setLayout(new BorderLayout(0, 15));
		setBackground(BG_LIGHT);


		var mainContent = new JPanel();
		mainContent.setBackground(new Color(250, 251, 255));
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setPreferredSize(new Dimension(0, 70));
		txtSearchAttendance = new JTextField("Search by employee name...");
		txtSearchAttendance.setColumns(30);
		txtSearchAttendance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearchAttendance.setForeground(TEXT_PRIMARY);
		txtSearchAttendance.setBackground(new Color(248, 250, 252));
		txtSearchAttendance.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		txtSearchAttendance.setPreferredSize(new Dimension(400, 36));
		txtSearchAttendance.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearchAttendance.getText().equals("Search by employee name...")) {
					txtSearchAttendance.setText("");
					txtSearchAttendance.setForeground(TEXT_PRIMARY);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearchAttendance.getText().isEmpty()) {
					txtSearchAttendance.setText("Search by employee name...");
					txtSearchAttendance.setForeground(Color.GRAY);
				}
			}
		});

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.addActionListener(e -> onRestaurantSelected());
		resFilter.setPreferredSize(new Dimension(200, 36));
		resFilter.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 0), 1, true));
		renderRestaurant();


		cmbDate = new JComboBox<String>();
		cmbDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbDate.setBackground(new Color(248, 250, 252));
		cmbDate.setPreferredSize(new Dimension(200, 36));
		cmbDate.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		// Lấy 7 ngày gần nhất
		var today = java.time.LocalDate.now();
		for (var i = 0; i < 10; i++) {
			var date = today.minusDays(i);
			var formatted = date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			cmbDate.addItem(formatted);
		}
		cmbDate.setSelectedIndex(0);

		// Khi chọn ngày khác → load dữ liệu mới
		cmbDate.addActionListener(e -> {
			try {
				updateHeaderDate((String) cmbDate.getSelectedItem());
				loadOverViewData();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		btnSearch = createButton("Search", PRIMARY_BLUE, 110);


		searchPanel.add(txtSearchAttendance);
		searchPanel.add(new JLabel("By Restaurant: "));
		searchPanel.add(resFilter);
		searchPanel.add(new JLabel("On: "));
		searchPanel.add(cmbDate);
		searchPanel.add(btnSearch);
		btnSearch.addActionListener(e -> loadOverViewData());
		add(searchPanel, BorderLayout.NORTH);

		//Content
		var contentPanel = new JPanel(new BorderLayout(0, 15));
		contentPanel.setBackground(BG_LIGHT);

		tableCard = new JPanel(new BorderLayout());
		tableCard.setBackground(CARD_WHITE);
		tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));
		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		var headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setBackground(Color.WHITE);


		var initialDate = (String) cmbDate.getSelectedItem();

		headerLabel = new JLabel("ATTENDANCE OVERVIEW FOR " + (initialDate != null ? initialDate : "TO DAY"));
		headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		headerLabel.setForeground(new Color(25, 118, 210));
		headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
		headerPanel.add(headerLabel);
		headerPanel.add(Box.createHorizontalStrut(20));

		currentLegendPanel = createLegendPanel();

		var northContentPanel = new JPanel();
		northContentPanel.setLayout(new BorderLayout());
		northContentPanel.setOpaque(false);
		northContentPanel.add(headerLabel, BorderLayout.NORTH);
		northContentPanel.add(currentLegendPanel, BorderLayout.CENTER);
		tableCard.add(northContentPanel, BorderLayout.NORTH);
		contentPanel.add(tableCard, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
		String[] columns = { "Employee ID", "Full Name", "Restaurant", "Phone","Shift Info","Check-in Time","Shift Type", "Status"};


		attendanceModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		loadOverViewData();

		tableAttendance = new JTable(attendanceModel);
		styleTable(tableAttendance);
		tableAttendance.setAutoCreateRowSorter(true);
		tableAttendance.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (tableAttendance.getSelectedRow() != -1) {
					JOptionPane.showMessageDialog(OverviewAdminPanel.this,
							"Employee details: " + attendanceModel.getValueAt(tableAttendance.getSelectedRow(), 1));
				}
			}
		});

		var tableScroll = new JScrollPane(tableAttendance);
		tableScroll.setBorder(new LineBorder(new Color(224, 235, 250), 1));
		tableCard.add(tableScroll, BorderLayout.CENTER);
		var actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		actionPanel.setBackground(Color.WHITE);

		var btnRefresh = createButton("Refresh", new Color(33, 150, 243), 110);
		btnRefresh.addActionListener(e -> refreshAttendance());
		actionPanel.add(btnRefresh);
		var btnExport = createButton("Export PDF", DANGER_RED, 110);
		btnExport.addActionListener(e -> exportAttendancePDF());
		actionPanel.add(btnExport);
		tableCard.add(actionPanel, BorderLayout.SOUTH);
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

	private void styleTable(JTable table) {
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(45);
		table.setSelectionBackground(new Color(232, 240, 254));
		table.setGridColor(new Color(220, 220, 220));
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);

		var header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setPreferredSize(new Dimension(0, 45));
		header.setReorderingAllowed(false);
		var statusRowRenderer = createStatusRowRenderer();

		var headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
				lbl.setOpaque(true);

				var text = (value != null) ? value.toString() : "";

				// Thêm logic hiển thị mũi tên sắp xếp
				RowSorter<? extends TableModel> sorter = table.getRowSorter();
				if (sorter != null && !sorter.getSortKeys().isEmpty()) {
					RowSorter.SortKey sortKey = sorter.getSortKeys().get(0);
					if (sortKey.getColumn() == column) {
						switch (sortKey.getSortOrder()) {
						case ASCENDING -> text += " ▲";
						case DESCENDING -> text += " ▼";
						default -> {}
						}
					}
				}

				lbl.setText(text);
				return lbl;
			}
		};
		// Áp dụng Renderer tiêu đề cho toàn bộ bảng
		table.getTableHeader().setDefaultRenderer(headerRenderer);


		// Áp dụng Renderer tô màu dòng cho tất cả các ô
		for (var i = 0; i < table.getColumnCount(); i++) {
			// Đã áp dụng headerRenderer ở trên bằng cách dùng setDefaultRenderer
			// Giờ chỉ cần áp dụng cellRenderer cho các cột
			table.getColumnModel().getColumn(i).setCellRenderer(statusRowRenderer);
		}
	}

	// Logic giữ nguyên
	private void searchAttendance() {
		var keyword = txtSearchAttendance.getText().trim();
		if ("Search by employee name...".equals(keyword) || keyword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a search keyword!", "Notification",
					JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Searching: " + keyword + "\n(Demo - Implement filter logic with real data)", "Search",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void refreshAttendance() {
		JOptionPane.showMessageDialog(this, "Attendance data for today (23/10/2025) has been refreshed!", "Success",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportAttendancePDF() {
		try {
			var header = new java.text.MessageFormat("ATTENDANCE OVERVIEW TODAY");
			var footer = new java.text.MessageFormat("Date {0,date,dd/MM/yyyy} - Page {1,number,integer}");
			tableAttendance.print(javax.swing.JTable.PrintMode.FIT_WIDTH, header, footer);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadOverViewData() {
		var keyword = txtSearchAttendance.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = 0;
		if (selectedRestaurant != null) {
			restaurantId = selectedRestaurant.getId();
		}
		if (keyword.isEmpty() || "Search by employee name...".equals(keyword)) {
			keyword = "";
		}
		var selectedDate = (String) cmbDate.getSelectedItem();
		var parsedSelectedDate = java.time.LocalDate.parse(selectedDate, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		var sqlDate = java.sql.Date.valueOf(parsedSelectedDate);

		var proc = "{CALL SP_GetCheckInByDay (?, ?, ?)}";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareCall(proc);){
			ps.setString(1, keyword == null ? "" : keyword);
			ps.setInt(2, restaurantId);
			ps.setDate(3, sqlDate);
			var rs=ps.executeQuery();
			attendanceModel.setRowCount(0);
			while (rs.next()) {
				var row = new Object[8];
				row[0] = "NV" + String.format("%03d", rs.getInt("employee_id"));
				row[1] = rs.getString("employee_name");
				row[2] = rs.getString("restaurant_name");
				row[3] = rs.getString("employee_phone");
				row[4] = rs.getString("schedule_name") + ": " + rs.getTime("schedule_start_time") +" - "+ rs.getTime("schedule_end_time");
				row[5] = rs.getTime("event_time");
				row[6] = rs.getString("event_type");
				row[7] = rs.getString("status_flag");
				attendanceModel.addRow(row);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel createLegendPanel() {
		var legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		legend.setBackground(CARD_WHITE);
		legend.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(10, 0, 10, 0)));
		var viewTotalEmployees = String.valueOf(totalEmployees);
		var viewTotalNotContract = String.valueOf(totalNotContract);
		String[][] legends = {
				{"Total employees: ", viewTotalEmployees }

		};

		for (String[] lg : legends) {
			var icon = new JLabel(lg[0]);
			icon.setFont(new Font("Segoe UI", Font.BOLD, 12));
			icon.setForeground(PRIMARY_BLUE);
			icon.setPreferredSize(new Dimension(100, 20));
			icon.setToolTipText(lg[1]);

			var desc = new JLabel(lg[1]);
			desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			desc.setForeground(TEXT_PRIMARY);

			var item = new JPanel(new BorderLayout(5, 0));
			item.add(icon, BorderLayout.WEST);
			item.add(desc, BorderLayout.CENTER);
			legend.add(item);
		}

		var totalNotContract = new JLabel();
		totalNotContract.setFont(new Font("Segoe UI", Font.BOLD, 12));
		totalNotContract.setForeground(PRIMARY_BLUE);
		totalNotContract.setPreferredSize(new Dimension(170, 20));
		totalNotContract.setText("No contract / Expired: ");

		var desc = new JLabel();
		desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		desc.setForeground(TEXT_PRIMARY);
		desc.setText(viewTotalNotContract);

		var item = new JPanel(new BorderLayout(5, 0));
		item.add(totalNotContract, BorderLayout.WEST);
		item.add(desc, BorderLayout.CENTER);
		legend.add(item);
		var summaryLegend = new JLabel("Legend: ");
		summaryLegend.setFont(new Font("Segoe UI", Font.BOLD, 12));
		summaryLegend.setForeground(PRIMARY_BLUE);
		legend.add(summaryLegend);

		var redNote = new JLabel("Late / Early", new ColorSquareIcon(DANGER_RED.brighter().brighter()), SwingConstants.LEFT);
		redNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		redNote.setForeground(TEXT_PRIMARY);
		legend.add(redNote);


		return legend;
	}

	private void renderRestaurant() {

		try {
			var restaurantService = new RestaurantService();
			var restaurants = restaurantService.getAll();
			resFilter.removeAllItems();
			resFilter.addItem(new Restaurant(0, "All Restaurants", 0));
			for (Restaurant r : restaurants) {
				resFilter.addItem(r);
			}
			resFilter.setSelectedIndex(0);
			isInitializing = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Error loading restaurant list: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void onRestaurantSelected() {
		if (isInitializing) {
			return;
		}
		loadOverViewData();
	}

	private DefaultTableCellRenderer createStatusRowRenderer() {
		final var STATUS_COLUMN_INDEX = 7;
		final var TEXT_PRIMARY_COLOR = TEXT_PRIMARY;
		// Màu Nền cho cảnh báo (Late/Early)
		final var DEFAULT_BG = CARD_WHITE;
		final var SELECTED_BG = new Color(232, 240, 254); // Giữ màu chọn ban đầu

		return new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setHorizontalAlignment(JLabel.CENTER);

				if (row < 0 || row >= table.getRowCount()) {
					return cell;
				}

				// Lấy trạng thái của dòng hiện tại (đảm bảo lấy từ Model, không phải View)
				var modelRow = table.convertRowIndexToModel(row);
				var statusObj = table.getModel().getValueAt(modelRow, STATUS_COLUMN_INDEX);
				var status = statusObj != null ? statusObj.toString().toLowerCase().trim() : "";

				// --- LOGIC XỬ LÝ MÀU NỀN ---

				Color backgroundColor;

				if (status.equals("late") || status.equals("early")) {
					// 1. Nếu là Late/Early: Đỏ nhạt
					backgroundColor = DANGER_RED.brighter().brighter();
				} else if (status.equals("ontime")) {
					// 2. Nếu là Ontime: Màu trắng (Mặc định)
					backgroundColor = DEFAULT_BG;
				} else {
					// 3. Các trạng thái khác (ví dụ: miss, leave): Màu trắng (Mặc định)
					backgroundColor = DEFAULT_BG;
				}

				// 4. Ưu tiên màu khi được chọn
				if (isSelected) {
					backgroundColor = SELECTED_BG;
				}

				cell.setBackground(backgroundColor);
				cell.setForeground(TEXT_PRIMARY_COLOR);

				return cell;
			}
		};
	}

	private void updateHeaderDate(String dateString) {
		// Định dạng lại chuỗi ngày (nếu cần) hoặc chỉ sử dụng chuỗi ngày
		var datePart = dateString != null ? dateString : "TO DAY";

		// 🔥 Cập nhật text của JLabel
		if (headerLabel != null) {
			headerLabel.setText("ATTENDANCE OVERVIEW FOR " + datePart);
		}
	}

	private static class ColorSquareIcon implements javax.swing.Icon {
		private final Color color;
		private final int size = 12; // Kích thước của ô vuông

		public ColorSquareIcon(Color color) {
			this.color = color;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			var g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(color);
			// Vẽ hình chữ nhật với màu nền nhẹ hơn để mô phỏng màu trong bảng
			g2d.fillRect(x, y, size, size);
			g2d.setColor(Color.GRAY);
			g2d.drawRect(x, y, size, size); // Vẽ viền nhẹ
			g2d.dispose();
		}

		@Override
		public int getIconWidth() {
			return size;
		}

		@Override
		public int getIconHeight() {
			return size;
		}
	}
}

