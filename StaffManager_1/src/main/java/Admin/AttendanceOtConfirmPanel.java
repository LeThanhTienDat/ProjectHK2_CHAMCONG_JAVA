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
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.OTJunctionService;
import com.example.swingapp.service.RestaurantService; // Cần thêm import này để dùng RestaurantService

// Lưu ý: Các thuộc tính và phương thức từ OverviewAdminPanel đã được tích hợp/giữ lại
public class AttendanceOtConfirmPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Đồng bộ các hằng số màu sắc
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);

	private JPanel otConfirmListPanel; // Panel chứa dữ liệu (nếu dùng OtConfirmDetailsPanel)
	private JButton btnSave, btnCancel;
	private Runnable onDataChanged;
	private JTable tableOtConfirm; // Đổi tên biến để tránh nhầm lẫn với table cũ
	private DefaultTableModel modelOtConfirm;
	private JTextField txtSearch;
	private JComboBox<String> cmbMonthYear;
	private JComboBox<Restaurant> resFilter;
	private JLabel headerLabel;
	private boolean isInitializing = true;
	private final RestaurantService restaurantService = new RestaurantService(); // Thêm service
	private final OTJunctionService otJunctionService = new OTJunctionService(); // Dùng để load dữ liệu OT

	public void setOnDataChanged(Runnable r) {
		onDataChanged = r;
	}

	public AttendanceOtConfirmPanel(ActionListener onSave, ActionListener onCancel) {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));
		setBorder(new EmptyBorder(10, 25, 10, 25));
		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		var headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		headerPanel.setOpaque(false);
		headerLabel = new JLabel("OT Attendance Approval", SwingConstants.CENTER);
		headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		headerLabel.setForeground(PRIMARY_BLUE);
		headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		headerPanel.add(headerLabel);
		topPanel.add(headerPanel, BorderLayout.NORTH);
		topPanel.add(createSearchFilterPanel(), BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		add(createTableCard(onCancel), BorderLayout.CENTER);
		loadOtConfirmListPanel();
	}

	public JPanel createSearchFilterPanel() {
		var p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		p.setOpaque(true);
		p.setBackground(CARD_WHITE);
		p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		p.setPreferredSize(new Dimension(0, 70));
		cmbMonthYear = new JComboBox<>();
		cmbMonthYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbMonthYear.setBackground(new Color(248, 250, 252));
		cmbMonthYear.setPreferredSize(new Dimension(200, 36));
		cmbMonthYear.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		var now = LocalDate.now();
		for (var i = -3; i <= 3; i++) {
			var date = now.plusDays(i);
			var item = String.format("%d/%d/%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
			cmbMonthYear.addItem(item);
		}
		cmbMonthYear.setSelectedIndex(3);
		cmbMonthYear.addActionListener(e -> loadOtConfirmListPanel());

		// Sử dụng styledField (giả định bạn có hàm này, nếu không thì dùng JTextField)
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
		btnSearch.addActionListener(e -> loadOtConfirmListPanel());

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.setPreferredSize(new Dimension(140, 36));
		resFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		renderRestaurant();
		resFilter.addActionListener(e -> loadOtConfirmListPanel());


		p.add(txtSearch);
		p.add(new JLabel("Month/Year: "));
		p.add(cmbMonthYear);
		p.add(new JLabel("Restaurant: "));
		p.add(resFilter);
		p.add(Box.createHorizontalStrut(10));
		p.add(btnSearch);
		return p;
	}
	public JPanel createTableCard(ActionListener onCancel) {
		var card = new JPanel(new BorderLayout());
		card.setBorder(new EmptyBorder(15, 15, 15, 15));
		card.setBackground(CARD_WHITE);

		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		var tableHeaderLabel = new JLabel("OT List Pending Approval", SwingConstants.LEFT);
		tableHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
		tableHeaderLabel.setForeground(TEXT_PRIMARY);
		tableHeaderLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

		topPanel.add(tableHeaderLabel, BorderLayout.NORTH); // Không cần Legend Panel

		card.add(topPanel, BorderLayout.NORTH);

		// Khởi tạo Model và Table
		int[] columnWidths = { 50, 110, 70, 80, 80, 180, 150, 150, 80, 250, 50 };
		String[] columns = { "Employee Id", "Name", "Restaurant","Phone", "Date", "OT Name", "In Time", "Out Time", "Status", "Action", "ot_type_id"};
		modelOtConfirm = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 9;
			}
		};
		tableOtConfirm = new JTable(modelOtConfirm);
		tableOtConfirm.getTableHeader().setReorderingAllowed(false);
		styleTable(tableOtConfirm);
		tableOtConfirm.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
		tableOtConfirm.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(tableOtConfirm));
		tableOtConfirm.getColumnModel().getColumn(10).setMinWidth(0);
		tableOtConfirm.getColumnModel().getColumn(10).setMaxWidth(0);
		tableOtConfirm.getColumnModel().getColumn(10).setPreferredWidth(0);
		tableOtConfirm.getColumnModel().getColumn(9).setPreferredWidth(150);

		for (var i = 0; i < tableOtConfirm.getColumnCount(); i++) {
			if (i < columnWidths.length) {
				tableOtConfirm.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
			}
		}

		var sp = new JScrollPane(tableOtConfirm);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		card.add(sp, BorderLayout.CENTER);

		var actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		actionPanel.setBackground(Color.WHITE);

		var btnRefresh = createButton("Làm Mới", ACCENT_BLUE, 110);
		btnRefresh.addActionListener(e -> loadOtConfirmListPanel());
		actionPanel.add(btnRefresh);

		btnCancel = createButton("Đóng", DANGER_RED, 110);
		btnCancel.addActionListener(onCancel);
		actionPanel.add(btnCancel);

		card.add(actionPanel, BorderLayout.SOUTH);

		return card;
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
					"Loading restaurant error: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	private JTextField styledField(String placeholder, int width) {
		var txt = new JTextField(placeholder);
		txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txt.setForeground(Color.GRAY);
		txt.setBackground(new Color(248, 250, 252));
		txt.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		txt.setPreferredSize(new Dimension(width, 36));
		return txt;
	}

	public void loadOtConfirmListPanel() {
		var dateStr = (String) cmbMonthYear.getSelectedItem();
		String safeSqlDate;
		try {
			var inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
			var dateObj = LocalDate.parse(dateStr.trim(), inputFormatter);
			safeSqlDate = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE);

		} catch (Exception e) {
			System.err.println("Error parsing selected date: " + dateStr);
			e.printStackTrace();
			safeSqlDate = null;
		}

		var keyword = txtSearch.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = 0;
		if (selectedRestaurant != null) {
			restaurantId = selectedRestaurant.getId();
		}
		if (keyword.isEmpty() || "Search by employee name...".equals(keyword)) {
			keyword = "";
		}
		var otConfirmList = otJunctionService.getOtConfirmList(keyword,restaurantId, safeSqlDate);
		modelOtConfirm.setRowCount(0);
		var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (otConfirmList.isEmpty()) {
			return;
		}
		for (Object[] item : otConfirmList) {
			var dateObject = item[6];
			var formattedDate = "N/A";
			if (dateObject instanceof java.sql.Date sqlDate) {
				formattedDate = sqlDate.toLocalDate().format(formatter);

			} else if (dateObject instanceof java.time.LocalDate localDate) {
				formattedDate = localDate.format(formatter);

			}
			var row = new Object[11];
			row[0] = "NH" + String.format("%03d", item[2]);
			row[1] = item[3];
			row[2] = item[1];
			row[3] = item[4];
			row[4] = formattedDate;
			row[5] = item[12]+": "+item[13]+" - "+item[14];
			row[6] = item[8]!=null ? item[8] : "Not recorded";
			row[7] = item[9]!=null ? item[9] : "Not recorded";
			row[8] = item[10];
			row[9] = "Approve/Reject";
			row[10] = item[7];
			modelOtConfirm.addRow(row);
		}
	}

	public void notifyDataChanged() {
		if (onDataChanged != null) {
			onDataChanged.run();
		}
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

				lbl.setText((value != null) ? value.toString() : "");
				return lbl;
			}
		};
		table.getTableHeader().setDefaultRenderer(headerRenderer);
		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
	}
	private class ButtonRenderer extends DefaultTableCellRenderer {
		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		private final JButton btnApprove = createButton("Approve", SUCCESS_GREEN, 110);
		private final JButton btnReject = createButton("Reject", DANGER_RED, 110);

		public ButtonRenderer() {
			panel.setOpaque(true);
			panel.setBackground(CARD_WHITE);
			panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			panel.add(btnApprove);
			panel.add(btnReject);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				panel.setBackground(table.getSelectionBackground());
			} else {
				panel.setBackground(table.getBackground());
			}
			return panel;
		}
	}

	private class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		private final JButton btnApprove = createButton("Approve", SUCCESS_GREEN, 110);
		private final JButton btnReject = createButton("Reject", DANGER_RED, 110);

		public ButtonEditor(JTable table) {
			panel.setOpaque(true);
			panel.setBackground(CARD_WHITE);

			btnApprove.addActionListener(e -> {
				var modelRow = table.convertRowIndexToModel(table.getEditingRow());
				var otJunctionId = (int) modelOtConfirm.getValueAt(modelRow, 10);
				var confirmDialog = JOptionPane.showConfirmDialog(table,
						"Are you sure you want to approve this OT?",
						"Confirm OT Approval",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
						);
				if (confirmDialog == JOptionPane.YES_OPTION) {
					var success = otJunctionService.confirmOt(otJunctionId);
					if (success) {
						JOptionPane.showMessageDialog(table, "OT approved successfully!");
					} else {
						JOptionPane.showMessageDialog(table, "OT approval failed, please check again!");
					}
				}
				fireEditingStopped();
				loadOtConfirmListPanel();
			});

			btnReject.addActionListener(e -> {
				var modelRow = table.convertRowIndexToModel(table.getEditingRow());
				var otJunctionId = (int) modelOtConfirm.getValueAt(modelRow, 10);
				var confirmDialog = JOptionPane.showConfirmDialog(table,
						"Are you sure you want to reject this OT?",
						"Confirm OT Rejection",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
						);
				if (confirmDialog == JOptionPane.YES_OPTION) {
					var success = otJunctionService.rejectOt(otJunctionId);
					if (success) {
						JOptionPane.showMessageDialog(table, "OT rejected successfully!");
					} else {
						JOptionPane.showMessageDialog(table, "OT rejection failed, please check again!");
					}
				}
				fireEditingStopped();
				loadOtConfirmListPanel();
			});

			panel.add(btnApprove);
			panel.add(btnReject);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (isSelected) {
				panel.setBackground(table.getSelectionBackground());
			} else {
				panel.setBackground(table.getBackground());
			}
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return "Action";
		}
	}
}