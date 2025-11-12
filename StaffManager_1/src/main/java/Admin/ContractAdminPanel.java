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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.ComboItem;
import com.example.swingapp.model.Contract;
import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.ContractService;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

public class ContractAdminPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private ContractFormPanel formPanel;
	private JComboBox<String> cmbDate;
	private JComboBox<Restaurant> resFilter;
	private JComboBox<ComboItem> expiringContracts;
	private boolean isInitializing = true;
	private int totalContracts = 0;
	private int totalExpiringContracts = 0;
	private JLabel lblTotalContracts;
	private JLabel lblTotalExpiringContracts;
	private JPanel currentLegendPanel;
	private JPanel tableCard;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public ContractAdminPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));
		initComponents();
	}

	private void initComponents() {
		// ==== SEARCH PANEL ====
		var searchPanel = new JPanel();
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setPreferredSize(new Dimension(0, 70));
		add(searchPanel, BorderLayout.NORTH);

		txtSearch = styledField("Search by employee name...", 400);
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
		txtSearch.setColumns(30);

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.addActionListener(e -> onRestaurantSelected());
		resFilter.setPreferredSize(new Dimension(200, 36));
		resFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));

		renderRestaurant();

		expiringContracts = new JComboBox<ComboItem>();
		expiringContracts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		expiringContracts.setBackground(new Color(248, 250, 252));
		expiringContracts.setPreferredSize(new Dimension(200, 36));
		expiringContracts.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		expiringContracts.removeAllItems();
		expiringContracts.addItem(new ComboItem(0, "All contracts"));
		expiringContracts.addItem(new ComboItem(1, "Expiring in 1 month"));

		var btnSearch = createButton("Search", PRIMARY_BLUE, 110);
		btnSearch.addActionListener(e -> search());
		searchPanel.add(txtSearch);
		searchPanel.add(new JLabel("Restaurant: "));
		searchPanel.add(resFilter);
		searchPanel.add(new JLabel("Expiring Contracts: "));
		searchPanel.add(expiringContracts);
		searchPanel.add(btnSearch);

		btnAdd = createButton("+ Add new", ACCENT_BLUE, 110);
		btnAdd.addActionListener(e -> addNew());
		searchPanel.add(btnAdd);

		// ==== CONTENT ====
		var content = new JPanel();
		content.setBackground(BG_LIGHT);
		content.setLayout(new BorderLayout(0, 15));


		formPanel = new ContractFormPanel(this::onSave, this::onCancel);
		formPanel.setVisible(false);
		content.add(formPanel, BorderLayout.NORTH);

		tableCard = new JPanel(new BorderLayout());
		tableCard.setBackground(CARD_WHITE);
		tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));
		var topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		var header = new JLabel("EMPLOYMENT CONTRACT LIST");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));

		currentLegendPanel = createLegendPanel();

		var northContentPanel = new JPanel();
		northContentPanel.setLayout(new BorderLayout());
		northContentPanel.setOpaque(false);
		northContentPanel.add(header, BorderLayout.NORTH);
		northContentPanel.add(currentLegendPanel, BorderLayout.CENTER);
		tableCard.add(northContentPanel, BorderLayout.NORTH);

		String[] cols = { "Contract ID", "Employee Name", "Start Date", "End Date", "Role", "Salary", "Contract Status", "Restaurant Name" };
		model = new DefaultTableModel(cols, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return switch (columnIndex) {
				case 5 -> Integer.class;
				case 2, 3 -> Date.class;
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
		loadContractTable();
		var scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		scroll.getViewport().setBackground(CARD_WHITE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableCard.add(scroll, BorderLayout.CENTER);

		content.add(tableCard, BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);

		// ==== ACTIONS (BOTTOM) ====
		var actions = new JPanel();
		actions.setBackground(BG_LIGHT);
		actions.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		add(actions, BorderLayout.SOUTH);

		var btnDelete = createButton("Delete", DANGER_RED, 110);
		btnDelete.addActionListener(e -> deleteRow());
		actions.add(btnDelete);

		var btnPDF = createButton("Xuất PDF", TEAL, 110);
		btnPDF.addActionListener(e -> printPDF());
		//		actions.add(btnPDF);
	}

	private JPanel createTableCard() {
		var card = new JPanel();
		card.setLayout(new BorderLayout());
		card.setBackground(CARD_WHITE);
		card.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("EMPLOYMENT CONTRACT LIST");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		card.add(header, BorderLayout.NORTH);

		table = new JTable(model);
		styleTable(table);

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

		var salaryRenderer = new DefaultTableCellRenderer() {
			private final java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");

			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				if (value instanceof Number) {
					value = df.format(value) + " ₫";
				}
				var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
				return c;
			}
		};
		table.getColumnModel().getColumn(5).setCellRenderer(salaryRenderer);

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

				var sorter = table.getRowSorter();
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
				} else {
					lbl.setText(value.toString());
				}

				return lbl;
			}
		};

		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}

		t.getTableHeader().setPreferredSize(new Dimension(0, 45));

		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (var i = 0; i < t.getColumnCount(); i++) {
			if (i != 5) {
				t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}
		}
	}





	private Object[] getRow(int r) {
		var d = new Object[model.getColumnCount()];
		for (var i = 0; i < d.length; i++) {
			d[i] = model.getValueAt(r, i);
		}
		return d;
	}

	private void search() {
		loadContractTable();
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
			JOptionPane.showMessageDialog(
					this,
					"Contract cannot be deleted!",
					"Warning",
					JOptionPane.WARNING_MESSAGE
					);
			return;
		} else {
			JOptionPane.showMessageDialog(this, "Select a row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void printPDF() {
		try {
			var h = new MessageFormat("EMPLOYMENT CONTRACT LIST");
			var f = new MessageFormat("Page {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadContractTable() {

		var keyword = txtSearch.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = 0;
		var getFlag = (ComboItem) expiringContracts.getSelectedItem();
		var flagInt = (getFlag != null) ? getFlag.getValue() : 0;
		if (selectedRestaurant != null) {
			restaurantId = selectedRestaurant.getId();
		}
		if (keyword.isEmpty() || "Search by employee name...".equals(keyword)) {
			keyword = "";
		}

		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall("{CALL SP_GetContractInfo(?,?,?)}")) {

			if (keyword == null || keyword.trim().isEmpty()) {
				stmt.setNull(1, java.sql.Types.NVARCHAR);
			} else {
				stmt.setString(1, keyword.trim());
			}
			stmt.setInt(2, restaurantId);
			stmt.setInt(3, flagInt);
			var rs = stmt.executeQuery();
			model.setRowCount(0);
			var currentTotalContracts = 0;
			while (rs.next()) {
				currentTotalContracts  ++;
				var row = new Object[8];
				row[0] = "HD" + String.format("%03d", rs.getInt("contract_id"));
				row[1] = rs.getString("employee_name");
				row[2] = rs.getDate("start_date");
				row[3] = rs.getDate("end_date");
				row[4] = rs.getString("position");
				row[5] = rs.getDouble("salary");
				row[6] = rs.getString("status");
				row[7] = rs.getString("restaurant_name");
				model.addRow(row);
			}

			lblTotalContracts.setText(String.valueOf(currentTotalContracts));


		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading contract data: " + ex.getMessage());
		}
	}

	private void onSave(ActionEvent e) {
		var cmd = e.getActionCommand();
		var data = formPanel.getFormData();
		System.out.println("[DEBUG] onSave called, cmd=" + e.getActionCommand());
		System.out.println("[DEBUG] Form data = " + Arrays.toString(data));
		if (data == null) {
			return;
		}
		if ("add".equals(cmd)) {
			try {
				var contractService = new ContractService();
				var hasActive = contractService.hasActiveContract((int) data[1]);
				if (hasActive) {
					JOptionPane.showMessageDialog(this,
							"This employee already has an active contract!",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}


				var c = new Contract();
				c.setEmployeeId((int) data[1]);
				c.setStartDate((Date) data[2]);
				c.setEndDate((Date) data[3]);
				c.setSalary((double) data[4]);
				c.setPosition(formPanel.mapRoleCode((String) data[5]));
				c.setStatus((String) data[6]);
				var checkAdd = contractService.add(c);
				if(checkAdd) {
					JOptionPane.showMessageDialog(this, "Add new Contract successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
			}catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu check: " + ex.getMessage());
			}

		} else {
			var r = formPanel.getEditingRow();
			if (r != -1) {
				for (var i = 1; i < model.getColumnCount(); i++) {
					model.setValueAt(data[i], r, i);
				}
				JOptionPane.showMessageDialog(
						this,
						"Contract cannot be edited!",
						"Warning",
						JOptionPane.WARNING_MESSAGE
						);
			}
		}
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
		loadContractTable();
	}

	private void onCancel(ActionEvent e) {
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
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
					"Error loading restaurant list: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	private void onRestaurantSelected() {
		if (isInitializing) {
			return;
		}
		loadContractTable();
	}

	public JPanel createLegendPanel() {
		var legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		legend.setBackground(CARD_WHITE);
		legend.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(10, 0, 10, 0)));
		var lblTotalTitle = new JLabel("Total Contracts: ");
		lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblTotalTitle.setForeground(PRIMARY_BLUE);
		lblTotalTitle.setPreferredSize(new Dimension(100, 20));
		lblTotalContracts = new JLabel(String.valueOf(totalContracts));
		lblTotalContracts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTotalContracts.setForeground(TEXT_PRIMARY);
		var itemTotal = new JPanel(new BorderLayout(5, 0));
		itemTotal.setOpaque(false);
		itemTotal.add(lblTotalTitle, BorderLayout.WEST);
		itemTotal.add(lblTotalContracts, BorderLayout.CENTER); // Thêm JLabel đã gán
		legend.add(itemTotal);

		//		var lblExpiringTitle = new JLabel("Total Expiring Contracts: ");
		//		lblExpiringTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//		lblExpiringTitle.setForeground(PRIMARY_BLUE);
		//		lblExpiringTitle.setPreferredSize(new Dimension(170, 20));
		//
		//		// ✅ GÁN ĐỐI TƯỢNG JLabel MỚI cho biến thành viên lblTotalExpiringContracts
		//		lblTotalExpiringContracts = new JLabel(String.valueOf(totalExpiringContracts));
		//		lblTotalExpiringContracts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		//		lblTotalExpiringContracts.setForeground(TEXT_PRIMARY);
		//
		//		var itemExpiring = new JPanel(new BorderLayout(5, 0));
		//		itemExpiring.setOpaque(false);
		//		itemExpiring.add(lblExpiringTitle, BorderLayout.WEST);
		//		itemExpiring.add(lblTotalExpiringContracts, BorderLayout.CENTER); // Thêm JLabel đã gán
		//		legend.add(itemExpiring);

		//		var summaryLegend = new JLabel("Note: ");
		//		summaryLegend.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//		summaryLegend.setForeground(PRIMARY_BLUE);
		//		legend.add(summaryLegend);


		return legend;
	}

}
