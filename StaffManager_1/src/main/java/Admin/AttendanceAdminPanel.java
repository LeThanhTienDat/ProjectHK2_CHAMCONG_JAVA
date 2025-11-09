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
import java.util.ArrayList;
import java.util.Arrays;
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
			var item = String.format("Tháng %d / %d", month.getMonthValue(), month.getYear());
			cmbMonthYear.addItem(item);
		}
		cmbMonthYear.setSelectedIndex(14);
		//		cmbMonthYear.addActionListener(e -> onRestaurantSelected());

		txtSearch = styledField("Tìm kiếm theo tên nhân viên...", 300);
		txtSearch.setColumns(30);
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals("Tìm kiếm theo tên nhân viên...")) {
					txtSearch.setText("");
					txtSearch.setForeground(TEXT_PRIMARY);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText("Tìm kiếm theo tên nhân viên...");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 120);
		btnSearch.addActionListener(e -> updateTableHeaderAndData());

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(Color.WHITE);
		resFilter.setPreferredSize(new Dimension(140, 36));
		resFilter.addActionListener(e -> updateTableHeaderAndData());
		renderRestaurant();


		p.add(txtSearch);
		p.add(new JLabel("Tháng/Năm: "));
		p.add(cmbMonthYear);
		p.add(new JLabel("Nhà hàng: "));
		p.add(resFilter);
		p.add(Box.createHorizontalStrut(10));
		p.add(btnSearch);
		return p;
	}

	public void updateTableHeaderAndData() {
		var monthStr = (String) cmbMonthYear.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Tháng", "").split("/");
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
		if (query.isEmpty() || "tìm kiếm theo tên nhân viên...".equalsIgnoreCase(query)) {
			query = null;
		}
		List<Object[]> displayData = new ArrayList<>();
		for (Object[] row : allData) {
			var tmp = row.clone();
			if (allData.length > 0) {
				System.out.println("Cấu trúc 1 dòng allData:");
				System.out.println(Arrays.toString(allData[0]));
			}
			Integer rowRestaurantId = null;
			if (row[5] instanceof Integer) {
				rowRestaurantId = (Integer) row[5];
			}

			// ✅ Nếu có chọn nhà hàng, chỉ giữ dòng khớp id
			if (restaurantId != 0) {
				if (rowRestaurantId == null || !rowRestaurantId.equals(restaurantId)) {
					continue; // bỏ qua nhân viên không thuộc nhà hàng được chọn
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
			if (!displayData.isEmpty()) {
				System.out.println("🔹 Dòng đầu tiên trong displayData: " + Arrays.toString(displayData.get(0)));
			} else {
				System.out.println("⚠️ displayData đang trống!");
			}
			model.setDataVector(displayData.toArray(new Object[0][]), headers.toArray());
			autoResizeColumns(table);
			// 🧹 Xóa cột restaurant_id ra khỏi bảng hiển thị
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

			// 1. Đặt độ rộng cố định cho các cột thông tin ban đầu (0 đến 4)
			// Các cột: ID, Mã NV, Tên NV, Chức vụ, Phòng ban
			table.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
			table.getColumnModel().getColumn(1).setPreferredWidth(70);  // Mã NV
			table.getColumnModel().getColumn(2).setPreferredWidth(150); // Tên NV
			table.getColumnModel().getColumn(3).setPreferredWidth(70); // Chức vụ
			table.getColumnModel().getColumn(4).setPreferredWidth(80); // Phòng ban (index 5 đã bị xóa/ẩn)
			// Cột thứ 5 hiện tại là cột ngày đầu tiên

			// 2. Đặt độ rộng cố định cho Cột Ngày (từ index 5 đến totalCols - 5)
			var startDayColumn = 5;
			var endDayColumn = totalCols - 4; // Cột cuối cùng trước 4 cột tổng hợp
			var dayWidth = 60; // Độ rộng mong muốn cho cột ngày

			for (var i = startDayColumn; i < endDayColumn; i++) {
				table.getColumnModel().getColumn(i).setPreferredWidth(dayWidth);
			}

			// 3. Đặt độ rộng cố định cho các Cột Tổng Hợp (4 cột cuối)
			// Các cột: Trễ, Sớm, Nghỉ P, Nghỉ K/L
			var summaryWidth = 80; // Độ rộng cho các cột tổng hợp
			table.getColumnModel().getColumn(totalCols - 4).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 3).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 2).setPreferredWidth(summaryWidth);
			table.getColumnModel().getColumn(totalCols - 1).setPreferredWidth(summaryWidth);

			// --- KẾT THÚC LOGIC ĐẶT ĐỘ RỘNG CỐ ĐỊNH ---

			// 1. Tạo Renderer Căn Giữa
			var centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			centerRenderer.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Tăng font cho dễ nhìn

			// 2. Áp dụng Renderer cho các cột: ID (0), Mã NV (1), Chức vụ (3), Phòng ban (4)

			// Cột 0: ID
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

			// Cột 1: Mã NV
			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

			table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
			// Cột 3: Chức vụ
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

			// Cột 4: Phòng ban
			table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

			// Lưu ý: Cột Tên NV (2) sẽ giữ nguyên căn trái (default) hoặc bạn có thể đặt căn trái nếu muốn chắc chắn.

			// --- KẾT THÚC: LOGIC CĂN GIỮA CỘT THÔNG TIN ---



			DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
					lbl.setForeground(Color.BLACK);

					var cellText = value != null ? value.toString().toUpperCase() : "";
					var shiftNameDisplay = cellText; // Mặc định là chuỗi đầy đủ
					var statusKey = "";

					// --- LOGIC PHÂN TÁCH CHUỖI MỚI (Xử lý NAME|STATUS) ---
					if (cellText.contains("|")) {
						var parts = cellText.split("\\|");
						if (parts.length == 2) {
							shiftNameDisplay = parts[0]; // Lấy "OT" hoặc "CA SÁNG"
							statusKey = parts[1];        // Lấy "X", "V", "*", "T"
						}
					}
					// --- KẾT THÚC LOGIC PHÂN TÁCH CHUỖI MỚI ---

					// --- LOGIC XỬ LÝ MÀU SẮC MỚI ---

					var isFuture = isFutureDateColumn(table, column);
					lbl.setBackground(Color.WHITE);
					lbl.setForeground(Color.BLACK);

					// 1. Xác định màu nền và chữ dựa trên TRẠNG THÁI (STATUS KEY)
					switch (statusKey.toUpperCase()) {
					case "T": // Trễ/Sớm (Mức cảnh báo cao nhất - Đỏ)
						lbl.setBackground(DANGER_RED.brighter());
						lbl.setForeground(Color.WHITE); // Chữ trắng cho nổi bật
						break;
					case "X": // Đủ Check-in/out (Xanh lá - Thành công)
						lbl.setBackground(SUCCESS_GREEN.brighter().brighter());
						lbl.setForeground(TEXT_PRIMARY);
						break;
					case "V": // Thiếu 1 trong 2 (Vàng - Cảnh báo)
						lbl.setBackground(WARNING_ORANGE.brighter().brighter());
						lbl.setForeground(TEXT_PRIMARY);
						break;
					case "*": // Chưa chấm công (Hồng/Xám - Thiếu sót)
						lbl.setBackground(new Color(248, 215, 218));
						lbl.setForeground(TEXT_PRIMARY);
						break;
					default:
						// Giữ màu mặc định (Trắng/Đen)
						break;
					}

					// 2. Ưu tiên: Nếu là OT (Tăng ca), TÙY CHỈNH MÀU NỀN DỰA TRÊN MÀU TRẠNG THÁI VỪA ÁP DỤNG
					if (shiftNameDisplay.equals("OT")) {
						// Nếu OT mà ĐỦ công (X), dùng màu Teal để phân biệt với ca thường
						if (statusKey.equalsIgnoreCase("X")) {
							lbl.setBackground(TEAL.brighter());
							lbl.setForeground(Color.WHITE);
						}
						// Nếu OT mà có vấn đề (V, *, T), giữ nguyên màu cảnh báo (Đỏ/Vàng/Hồng)
						// ví dụ: OT|T sẽ màu Đỏ, OT|V sẽ màu Vàng, OT|* sẽ màu Hồng
					}
					// --- KẾT THÚC LOGIC XỬ LÝ MÀU SẮC MỚI ---

					// Cột ngày (5 → totalCols-4) highlight đỏ nếu đi trễ >6 hoặc về sớm >6
					// ⚠️ Lưu ý: Việc highlight này có thể ghi đè màu Teal/Đỏ/Vàng nếu không được quản lý cẩn thận.
					// Tôi giữ nguyên logic này của bạn ở đây:
					var totalCols = table.getColumnCount();
					if (column >= 5 && column < totalCols - 4 && !cellText.isEmpty()) { // Sửa column >= 6 thành >= 5 (vì cột ngày bắt đầu từ index 5)
						// Lấy giá trị tổng hợp (totalLate, totalEarly)
						var totalLate = parseIntSafe(table.getValueAt(row, totalCols - 4));
						var totalEarly = parseIntSafe(table.getValueAt(row, totalCols - 3));

						if (totalLate > 6 || totalEarly > 6) {
							// Đây là màu cảnh báo tổng hợp, có thể ghi đè màu X/V
							lbl.setBackground(new Color(255, 102, 102));
						}
					}

					if (isSelected) {
						// Highlight khi được chọn luôn được ưu tiên
						lbl.setBackground(new Color(227, 242, 253));
					}

					lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY));
					lbl.setText(shiftNameDisplay); // <-- Hiển thị "OT" hoặc "Ca Sáng"
					return lbl;
				}
			};
			endDayColumn = table.getColumnCount() - 4;
			//			for (var i = 5; i < table.getColumnCount(); i++) {
			//				table.getColumnModel().getColumn(i).setCellRenderer(dayRenderer);
			//			}
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

		var headerLabel = new JLabel("BẢNG CHẤM CÔNG TỔNG HỢP", SwingConstants.LEFT);
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
						int month = 0, year = 0;
						if (selected != null && selected.contains("/")) {
							var parts = selected.replace("Tháng", "").split("/");
							month = Integer.parseInt(parts[0].trim());
							year = Integer.parseInt(parts[1].trim());
						}
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
				{"A1,A2,...: ", "Mã ca"}
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

		var summaryLegend = new JLabel("Tổng hợp: ");
		summaryLegend.setFont(new Font("Segoe UI", Font.BOLD, 12));
		summaryLegend.setForeground(SUCCESS_GREEN);
		legend.add(summaryLegend);

		var cnHasCheckInOut = new JLabel("Chấm công đúng giờ (Xanh)");
		cnHasCheckInOut.setForeground(SUCCESS_GREEN);
		legend.add(cnHasCheckInOut);

		var cnCheckMissTime = new JLabel("Chấm công trễ/ra sớm (Đỏ)");
		cnCheckMissTime.setForeground(DANGER_RED);
		legend.add(cnCheckMissTime);

		var cnWaiting = new JLabel("Thiếu chấm công (Vàng)");
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

		var btnPDF = createButton("Xuất PDF", TEAL, 130);
		btnPDF.addActionListener(e -> printPDF());
		var btnDelete = createButton("Xóa", DANGER_RED, 130);
		btnDelete.addActionListener(e -> deleteRow());

		// 👇 Thay đổi cách tạo và thêm nút Duyệt Chấm Công
		var btnApprove = createButton("Duyệt Chấm Công", SUCCESS_GREEN, 150);
		btnApprove.addActionListener(e -> openOtConfirmForm());
		btnApproveWrapper = new NotifiedButtonPanel(btnApprove);

		var btnLegend = createButton("Ký Hiệu Chấm Công", WARNING_ORANGE, 150);
		btnLegend.addActionListener(e -> showLegendDialog());

		panel.add(btnDelete);
		panel.add(btnPDF);
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
			JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Xóa dòng này? (Sẽ xóa dữ liệu chấm công của nhân viên)", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
		dialog.pack();           // Tính toán kích thước nội dung
		dialog.setSize(1000, 1000);
		dialog.setResizable(false); // Không cho người dùng thay đổi kích thước
		dialog.setLocationRelativeTo(null);
		Runnable refreshAction = () -> {
			// 1. Cập nhật số lượng Badge (Cần Duyệt)
			updateApprovalBadgeCount();
			// 2. Cập nhật lại Bảng tổng hợp (Phòng trường hợp trạng thái OT đã thay đổi)
			updateTableHeaderAndData();
		};

		// 1. Gắn hành động refresh vào event đóng cửa sổ (Windows Listener)
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				// Hành động này chạy khi cửa sổ bị đóng (bằng nút X hoặc dialog.dispose())
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
		dialog.setMinimumSize(new Dimension(900, 700)); // tránh bị co lại
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public void openAttendanceForm(int row, int col) {
		var modelCol = table.convertColumnIndexToModel(col);
		var employeeName = model.getValueAt(row, 2).toString();
		var dateHeader = model.getColumnName(modelCol); // ví dụ "01/11"

		// Lấy employeeId từ tên
		var empId = service.getEmployeeIdByName(employeeName);

		// Lấy năm hiện tại từ combo
		var monthStr = (String) cmbMonthYear.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Tháng", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}

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

		Runnable refreshAction = () -> {
			updateTableHeaderAndData();
			updateApprovalBadgeCount();
		};

		// Hiển thị form (chỉ xem, không nhập tay)
		var dialog = new javax.swing.JDialog(SwingUtilities.getWindowAncestor(this),
				"Lịch làm của " + employeeName + " (" + formattedDate + ")",ModalityType.APPLICATION_MODAL);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				// Hành động này chạy khi cửa sổ bị đóng (bằng nút X hoặc dialog.dispose())
				refreshAction.run();
			}
		});
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.pack();           // Tính toán kích thước nội dung
		dialog.setSize(1000, 1000);
		dialog.setResizable(false); // Không cho người dùng thay đổi kích thước
		dialog.setLocationRelativeTo(null);
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
			refreshAction.run(); // Cập nhật ngay lập tức
		});

		// Gửi danh sách ca làm để hiển thị
		var dayStatusList = service.getDayWorkStatus(employeeName, formattedDate);
		formPanel.showEmployeeSchedule(empId,employeeName, formattedDate, dayStatusList);

		formPanel.setPreferredSize(new Dimension(800, 600));
		dialog.getContentPane().add(formPanel);
		dialog.setSize(900, 700);
		dialog.setMinimumSize(new Dimension(900, 700)); // tránh bị co lại
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

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
			resFilter.setSelectedIndex(0);
			isInitializing = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Lỗi tải danh sách Nhà Hàng: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
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
		// 1. Lấy tiêu đề cột
		var headerValue = table.getColumnModel().getColumn(column).getHeaderValue();
		if (headerValue == null) {
			return false;
		}
		var headerText = headerValue.toString().trim();

		// 2. Kiểm tra định dạng (phải là ngày/tháng, ví dụ: 07/11)
		if (!headerText.matches("\\d{1,2}/\\d{1,2}")) {
			return false;
		}

		try {
			// 3. Lấy tháng/năm đang được chọn từ JComboBox (cmbMonthYear)
			var monthStr = (String) cmbMonthYear.getSelectedItem();
			var selectedYear = java.time.Year.now().getValue();
			if (monthStr != null && monthStr.contains("/")) {
				var parts = monthStr.replace("Tháng", "").split("/");
				selectedYear = Integer.parseInt(parts[1].trim());
			}

			// 4. Tạo ngày cột (sử dụng năm được chọn)
			var fullDateStr = headerText + "/" + selectedYear;
			var formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
			var columnDate = LocalDate.parse(fullDateStr, formatter);

			// 5. So sánh với ngày hôm nay (chỉ cần kiểm tra xem ngày có >= ngày mai không)
			var today = LocalDate.now();

			// Nếu ngày của cột LỚN HƠN ngày hiện tại (là ngày mai trở đi)
			return columnDate.isAfter(today);

		} catch (Exception e) {
			// Lỗi parse, không tô màu
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
			// Bắt buộc phải là FlowLayout để nút nằm gọn
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			setOpaque(false); // Quan trọng để nền JPanel trong suốt
			add(button);
			setPreferredSize(button.getPreferredSize());
		}

		public void setNotificationCount(int count) {
			notificationCount = count;
			repaint(); // Yêu cầu vẽ lại để hiển thị/ẩn badge
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

				// Lấy kích thước của chính NotifiedButtonPanel
				var wrapperWidth = getWidth();
				var wrapperHeight = getHeight();

				// Tính vị trí biểu tượng (góc trên bên phải của wrapper)
				var badgeSize = 18;

				// Tọa độ x: Đặt ở góc phải của wrapper, lùi lại 1/2 kích thước badge để badge không bị cắt
				var x = wrapperWidth - badgeSize;
				// Tọa độ y: Đặt ở mép trên của wrapper (y=0)
				var y = 0;

				// Hoặc: Nếu muốn badge nằm hoàn toàn trong nút:
				// var x = wrapperWidth - badgeSize / 2;
				// var y = 0 - badgeSize / 2;

				// 1. Vẽ hình tròn nền
				g2.setColor(BADGE_COLOR);
				g2.fillOval(x, y, badgeSize, badgeSize);
				// ... (phần vẽ viền và text không đổi) ...

				g2.dispose();
			}
		}
	}
	public void updateApprovalBadgeCount() {
		var count = 0;
		var monthStr = (String) cmbMonthYear.getSelectedItem();
		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Tháng", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}

		var keyword = txtSearch.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = 0;
		if (selectedRestaurant != null) {
			restaurantId = selectedRestaurant.getId();
		}
		if (keyword.isEmpty() || "Tìm kiếm theo tên nhân viên...".equals(keyword)) {
			keyword = "";
		}
		try {
			// Lấy số lượng bản ghi OT đang chờ duyệt
			var pendingList = otJunctionService.getOtConfirmList(keyword, restaurantId, month, year);
			count = pendingList != null ? pendingList.size() : 0;
		} catch (Exception e) {
			System.err.println("Lỗi khi đếm OT chờ duyệt: " + e.getMessage());
		}

		if (btnApproveWrapper != null) {
			btnApproveWrapper.setNotificationCount(count);

			if (count > 0) {
				btnApproveWrapper.getButton().setText("Duyệt OT (" + count + ")");
			} else {
				btnApproveWrapper.getButton().setText("Duyệt Chấm Công");
			}
		}
	}
}
