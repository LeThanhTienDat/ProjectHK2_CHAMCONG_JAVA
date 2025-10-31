package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
import javax.swing.table.TableModel;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

public class EmployeeAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Components
	private JTextField txtSearch;
	private JButton btnAdd, btnSearch, btnDelete, btnContract, btnPDF;
	private JTable tableEmployee;
	private DefaultTableModel model;
	private EmployeeFormPanel formPanel;
	private JPanel tableCard;
	private JComboBox<Restaurant> resFilter;
	private boolean isInitializing = true;

	// Colors
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);


	public EmployeeAdminPanel() {
		this(null, null);
	}

	public EmployeeAdminPanel(java.awt.event.ActionListener saveListener, java.awt.event.ActionListener cancelListener) {
		setLayout(new BorderLayout(0, 15));
		setBackground(BG_LIGHT);
		setBorder(new EmptyBorder(15, 15, 15, 15));

		// ===== Search Panel =====
		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setPreferredSize(new Dimension(0, 60));

		txtSearch = new JTextField("Tìm kiếm nhân viên...");
		txtSearch.setColumns(30);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setForeground(Color.GRAY);
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals("Tìm kiếm nhân viên...")) {
					txtSearch.setText("");
					txtSearch.setForeground(TEXT_PRIMARY);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText("Tìm kiếm nhân viên...");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.addActionListener(e -> onRestaurantSelected());
		resFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		renderRestaurant();

		btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 120);
		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 130);

		btnSearch.addActionListener(e -> searchEmployee());
		btnAdd.addActionListener(e -> addEmployee());

		searchPanel.add(txtSearch);
		searchPanel.add(new JLabel("Theo Nhà Hàng: "));
		searchPanel.add(resFilter);
		searchPanel.add(btnSearch);
		searchPanel.add(btnAdd);

		add(searchPanel, BorderLayout.NORTH);

		// ===== Content Panel =====
		var contentPanel = new JPanel(new BorderLayout(0, 15));
		contentPanel.setBackground(BG_LIGHT);

		// Form panel
		formPanel = new EmployeeFormPanel(this::handleFormSave, this::handleFormCancel);
		formPanel.setVisible(false);
		contentPanel.add(formPanel, BorderLayout.NORTH);

		// Table card
		tableCard = new JPanel(new BorderLayout());
		tableCard.setBackground(CARD_WHITE);
		tableCard.setBorder(new EmptyBorder(20, 25, 20, 25));

		var header = new JLabel("DANH SÁCH NHÂN VIÊN");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		tableCard.add(header, BorderLayout.NORTH);

		String[] cols = { "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "Chức danh",
				"Lương", "Ngày vào làm", "Trạng thái hợp đồng","Hoạt động", "Điện thoại", "Nhà Hàng", "Email", "rawId" };

		model = new DefaultTableModel(cols, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return switch (columnIndex) {
				case 3, 6 -> java.util.Date.class;
				case 5 -> Double.class;
				default -> String.class;
				};
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		loadEmployeeData("");

		tableEmployee = new JTable(model);
		styleTable(tableEmployee);
		setupColumnWidths(tableEmployee);
		tableEmployee.removeColumn(tableEmployee.getColumnModel().getColumn(12));
		tableEmployee.setAutoCreateRowSorter(true);

		tableEmployee.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public java.awt.Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				if (value == null) {
					value = "";
				}

				var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setHorizontalAlignment(SwingConstants.CENTER);

				// Màu mặc định
				if (!isSelected) {
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);

					var statusObj = table.getValueAt(row, 7);
					var status = (statusObj != null) ? statusObj.toString() : "";

					if (status.equalsIgnoreCase("Expired")) {
						setBackground(new Color(255, 153, 153));
						setForeground(Color.BLACK);
					}
				}

				return cell;
			}
		});





		tableEmployee.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var row = tableEmployee.getSelectedRow();
				if (row != -1) {
					formPanel.setEditMode(row, getRowData(row));
					formPanel.setVisible(true);
					tableCard.setVisible(false);
					btnAdd.setVisible(false);
					formPanel.focusNameField();
				}
			}
		});

		var scroll = new JScrollPane(tableEmployee);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		scroll.getViewport().setBackground(CARD_WHITE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		tableCard.add(scroll, BorderLayout.CENTER);
		contentPanel.add(tableCard, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);

		// ===== Action Panel =====
		var actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actionPanel.setBackground(BG_LIGHT);

		btnDelete = createButton("Xóa", DANGER_RED, 110);
		btnContract = createButton("Hợp Đồng", PRIMARY_BLUE, 130);
		btnPDF = createButton("Xuất PDF", TEAL, 120);

		btnDelete.addActionListener(e -> deleteEmployee());
		btnContract.addActionListener(e -> manageContract());
		btnPDF.addActionListener(e -> printPDF());

		actionPanel.add(btnDelete);
		actionPanel.add(btnContract);
		actionPanel.add(btnPDF);

		add(actionPanel, BorderLayout.SOUTH);
	}

	// ===== HELPER METHODS =====
	public static  JButton createButton(String text, Color color, int width) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(color);
		b.setPreferredSize(new Dimension(width, 38));
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return b;
	}

	public void styleTable(JTable t) {
		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(45);
		t.setSelectionBackground(new Color(232, 240, 254));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(new Color(220, 220, 220));
		t.setShowVerticalLines(true);
		t.setShowHorizontalLines(true);

		var header = t.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setPreferredSize(new Dimension(0, 45));
		header.setReorderingAllowed(false);

		var dateRenderer = new DefaultTableCellRenderer() {
			private final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

				if (value instanceof java.util.Date) {
					value = df.format((java.util.Date) value);
				}

				var cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// Căn giữa
				cell.setHorizontalAlignment(SwingConstants.CENTER);

				if (!isSelected) {
					// Màu nền mặc định
					cell.setBackground(Color.WHITE);
					cell.setForeground(Color.BLACK);

					// Check trạng thái hợp đồng
					var statusObj = table.getValueAt(row, 7); // cột 7 = contract_status
					var status = (statusObj != null) ? statusObj.toString() : "";

					if ("Expired".equalsIgnoreCase(status)) {
						cell.setBackground(new Color(255, 153, 153)); // đỏ nhạt
						cell.setForeground(Color.BLACK);
					}
				}

				return cell;
			}
		};

		tableEmployee.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);
		tableEmployee.getColumnModel().getColumn(6).setCellRenderer(dateRenderer);

		var salaryRenderer = new DefaultTableCellRenderer() {
			private final java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");

			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				if (value instanceof Number) {
					value = df.format(value) + " ₫";
				}

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				if (!isSelected) {
					lbl.setBackground(Color.WHITE);
					lbl.setForeground(Color.BLACK);

					var statusObj = table.getValueAt(row, 7);
					var status = (statusObj != null) ? statusObj.toString() : "";

					if ("Expired".equalsIgnoreCase(status)) {
						lbl.setBackground(new Color(255, 153, 153));
						lbl.setForeground(Color.BLACK);
					}
				}

				return lbl;
			}
		};
		t.getColumnModel().getColumn(5).setCellRenderer(salaryRenderer);

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

				// ✅ Chặn null hoàn toàn
				var text = (value != null) ? value.toString() : "";

				// Thêm icon sắp xếp ▲▼
				RowSorter<? extends TableModel> sorter = table.getRowSorter();
				if (sorter != null && !sorter.getSortKeys().isEmpty()) {
					RowSorter.SortKey sortKey = sorter.getSortKeys().get(0);
					if (sortKey.getColumn() == column) {
						switch (sortKey.getSortOrder()) {
						case ASCENDING -> text += " ▲";
						case DESCENDING -> text += " ▼";
						default -> {} // không thêm gì
						}
					}
				}

				lbl.setText(text);
				return lbl;
			}
		};
		t.getTableHeader().setDefaultRenderer(headerRenderer);
	}

	private void setupColumnWidths(JTable table) {
		int[] widths = {80, 150, 60, 80, 100, 100, 110, 120, 80, 80};
		for (var i = 0; i < widths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	private Object[] getRowData(int row) {
		var data = new Object[model.getColumnCount()];
		for (var i = 0; i < data.length; i++) {
			data[i] = model.getValueAt(row, i);
		}
		return data;
	}

	// ===== DATA METHODS =====
	private void loadEmployeeData(String keyword) {
		var list = new ArrayList<Object[]>();
		var sql = """
				SELECT e.id, e.name, e.role, e.gender, e.dob,
				       e.phone, e.email, c.salary, c.start_date, c.status AS contract_status, e.active,
				       STRING_AGG(r.name, ', ') AS restaurants,
				       STRING_AGG(s.street_name, ', ') AS addresses
				FROM dbo.tbl_Employee e
				OUTER APPLY (
					SELECT TOP 1 *
					FROM dbo.tbl_Contract c
					WHERE c.employee_id = e.id
					ORDER BY c.start_date DESC
				) c
				LEFT JOIN dbo.tbl_Employee_Restaurant er ON e.id = er.employee_id
				LEFT JOIN dbo.tbl_Restaurant r ON er.restaurant_id = r.id
				LEFT JOIN dbo.tbl_Street s ON r.street_id = s.id
				WHERE (e.name LIKE ? OR e.email LIKE ? OR e.phone LIKE ?) and e.active=1
				GROUP BY e.id, e.name, e.role, e.gender, e.dob, e.phone, e.email,
				         c.salary, c.start_date, c.status, e.active
				ORDER BY e.id desc
				""";

		try (var conn = DBConnection.getConnection();
				var pst = conn.prepareStatement(sql)) {

			var kw = "%" + (keyword != null ? keyword : "") + "%";
			pst.setString(1, kw);
			pst.setString(2, kw);
			pst.setString(3, kw);

			var rs = pst.executeQuery();
			model.setRowCount(0);
			while (rs.next()) {
				var row = new Object[13];
				Double salary = rs.getObject("salary") == null ? 0.0 : rs.getDouble("salary");
				row[0] = "NH" + String.format("%03d", rs.getInt("id"));
				row[1] = rs.getString("name");
				row[2] = "nam".equals(rs.getString("gender")) ? "Nam" : "Nữ";
				row[3] = rs.getObject("dob", java.sql.Date.class);
				row[4] = rs.getString("role");
				row[5] = salary;
				row[6] = rs.getObject("start_date", java.sql.Date.class);
				row[7] = rs.getString("contract_status");
				row[8] = rs.getInt("active") == 1 ? "Đang làm" : "Đã nghỉ";
				row[9] = rs.getString("phone");
				row[10] = rs.getString("restaurants");
				row[11] = rs.getString("email");
				row[12] = rs.getInt("id");
				model.addRow(row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu: " + ex.getMessage());
		}
	}

	// ===== ACTION METHODS =====
	private void addEmployee() {
		formPanel.setAddMode(true);
		formPanel.setVisible(true);
		tableCard.setVisible(false);
		btnAdd.setVisible(false);
		formPanel.focusNameField();
	}

	private void handleFormCancel(ActionEvent e) {
		formPanel.setVisible(false);
		tableCard.setVisible(true);
		btnAdd.setVisible(true);
	}

	private void searchEmployee() {
		var keyword = txtSearch.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();

		var hasKeyword = !(keyword.isEmpty() || "Tìm kiếm nhân viên...".equals(keyword));
		if (selectedRestaurant == null || selectedRestaurant.getId() == 0) {
			loadEmployeeData(hasKeyword ? keyword : "");
		} else {
			loadEmployeeDataByRestaurant(selectedRestaurant.getId(), hasKeyword ? keyword : "");
		}
	}
	private void loadEmployeeDataByRestaurant(int restaurantId, String keyword) {
		var hasKeyword = keyword != null && !keyword.trim().isEmpty() &&
				!"Tìm kiếm nhân viên...".equals(keyword.trim());

		var sql = new StringBuilder("""
				SELECT e.id, e.name, e.role, e.gender, e.dob,
				e.phone, e.email, c.salary, c.start_date, c.status AS contract_status, e.active,
				STRING_AGG(r.name, ', ') AS restaurants,
				STRING_AGG(s.street_name, ', ') AS addresses
				FROM dbo.tbl_Employee e
				OUTER APPLY (
					SELECT TOP 1 *
					FROM dbo.tbl_Contract c
					WHERE c.employee_id = e.id
					ORDER BY c.start_date DESC
				) c
				LEFT JOIN dbo.tbl_Employee_Restaurant er ON e.id = er.employee_id
				LEFT JOIN dbo.tbl_Restaurant r ON er.restaurant_id = r.id
				LEFT JOIN dbo.tbl_Street s ON r.street_id = s.id
				WHERE e.active = 1
				""");
		if (restaurantId != 0) {
			sql.append(" AND r.id = ? ") ;
		}

		if (hasKeyword) {
			sql.append(" AND (e.name LIKE ? OR e.email LIKE ? OR e.phone LIKE ?)");
		}

		sql.append("""
				GROUP BY e.id, e.name, e.role, e.gender, e.dob, e.phone, e.email,
				         c.salary, c.start_date, c.status, e.active
				ORDER BY e.id DESC
				""");

		try (var conn = DBConnection.getConnection();
				var pst = conn.prepareStatement(sql.toString())) {

			var index = 1;
			if (restaurantId != 0) {
				pst.setInt(index++, restaurantId);
			}

			if (hasKeyword) {
				var kw = "%" + keyword.trim() + "%";
				pst.setString(index++, kw);
				pst.setString(index++, kw);
				pst.setString(index++, kw);
			}

			var rs = pst.executeQuery();
			model.setRowCount(0);

			while (rs.next()) {
				var row = new Object[13];
				row[0] = "NH" + String.format("%03d", rs.getInt("id"));
				row[1] = rs.getString("name");
				row[2] = "nam".equalsIgnoreCase(rs.getString("gender")) ? "Nam" : "Nữ";
				row[3] = rs.getObject("dob", java.sql.Date.class);
				row[4] = rs.getString("role");
				Double salary = rs.getObject("salary") == null ? 0.0 : rs.getDouble("salary");
				row[5] = salary;
				row[6] = rs.getObject("start_date", java.sql.Date.class);
				row[7] = rs.getString("contract_status");
				row[8] = rs.getInt("active") == 1 ? "Đang làm" : "Đã nghỉ";
				row[9] = rs.getString("phone");
				row[10] = rs.getString("restaurants");
				row[11] = rs.getString("email");
				row[12] = rs.getInt("id");
				model.addRow(row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu theo nhà hàng: " + ex.getMessage());
		}
	}


	private void deleteEmployee() {
		var row = tableEmployee.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Chọn nhân viên cần xóa!");
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?", "Xác nhận",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			var id = (int) model.getValueAt(row, 10);
			try (var conn = DBConnection.getConnection();
					var pst = conn.prepareStatement("DELETE FROM tbl_Employee WHERE id=?")) {
				pst.setInt(1, id);
				pst.executeUpdate();
				JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
				reloadTable();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
			}
		}
	}

	private void handleFormSave(ActionEvent e) {
		var emp = formPanel.getEmployeeData();
		var restaurantId = formPanel.getSelectedRestaurantId();

		if (emp.getName().isEmpty() || emp.getPhone().isEmpty() ||
				emp.getEmail().isEmpty() || emp.getDob() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}

		try (var conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);
			var sqlDob = new java.sql.Date(emp.getDob().getTime());

			if (formPanel.isEditMode()) {
				var empId = formPanel.getEditingEmployeeId();

				var sqlEmp = "UPDATE tbl_Employee SET name=?, role=?, phone=?, email=?, dob=?, gender=? WHERE id=?";
				try (var pst = conn.prepareStatement(sqlEmp)) {
					pst.setString(1, emp.getName());
					pst.setString(2, emp.getRole());
					pst.setString(3, emp.getPhone());
					pst.setString(4, emp.getEmail());
					pst.setDate(5, sqlDob);
					pst.setString(6, emp.getGender());
					pst.setInt(7, empId);
					pst.executeUpdate();
				}

				try (var pst = conn.prepareStatement("DELETE FROM tbl_Employee_Restaurant WHERE employee_id=?")) {
					pst.setInt(1, empId);
					pst.executeUpdate();
				}
				try (var pst = conn.prepareStatement(
						"INSERT INTO tbl_Employee_Restaurant (employee_id, restaurant_id) VALUES (?,?)")) {
					pst.setInt(1, empId);
					pst.setInt(2, restaurantId);
					pst.executeUpdate();
				}
			} else {
				int empId;
				var sqlInsert = "INSERT INTO tbl_Employee (name, role, phone, email, dob, gender) VALUES (?,?,?,?,?,?)";
				try (var pst = conn.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS)) {
					pst.setString(1, emp.getName());
					pst.setString(2, emp.getRole());
					pst.setString(3, emp.getPhone());
					pst.setString(4, emp.getEmail());
					pst.setDate(5, sqlDob);
					pst.setString(6, emp.getGender());
					pst.executeUpdate();
					var rs = pst.getGeneratedKeys();
					rs.next();
					empId = rs.getInt(1);
				}

				try (var pst = conn.prepareStatement(
						"INSERT INTO tbl_Employee_Restaurant (employee_id, restaurant_id) VALUES (?,?)")) {
					pst.setInt(1, empId);
					pst.setInt(2, restaurantId);
					pst.executeUpdate();
				}
			}

			conn.commit();
			JOptionPane.showMessageDialog(this, "Lưu thành công!");
			reloadTable();
			handleFormCancel(null);

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + ex.getMessage());
		}
	}

	private void manageContract() {
		JOptionPane.showMessageDialog(this, "Chức năng quản lý hợp đồng (demo)");
	}

	private void printPDF() {
		JOptionPane.showMessageDialog(this, "Tính năng xuất PDF đang được phát triển!");
	}

	private void reloadTable() {
		loadEmployeeData("");
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

		var keyword = txtSearch.getText().trim();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();

		var hasKeyword = !(keyword.isEmpty() || "Tìm kiếm nhân viên...".equals(keyword));

		if (selectedRestaurant == null || selectedRestaurant.getId() == 0) {
			if (hasKeyword) {
				loadEmployeeData(keyword);
			} else {
				loadEmployeeData("");
			}
		} else {
			loadEmployeeDataByRestaurant(selectedRestaurant.getId(), hasKeyword ? keyword : "");
		}
	}
}
