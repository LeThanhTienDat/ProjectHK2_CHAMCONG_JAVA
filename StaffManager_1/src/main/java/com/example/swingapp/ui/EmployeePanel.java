package com.example.swingapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.util.DBConnection;

public class EmployeePanel extends JPanel {

	private int employeeId;
	private JLabel lblName, lblGender, lblDob, lblEmail, lblPhone, lblRole, lblStatus, lblUsername, lblAvatar;
	private JTextField txtEmail, txtPhone;
	private JButton btnUpdate;

	private DefaultTableModel contractModel, salaryModel, scheduleModel, otModel, restaurantModel;

	// Modern Color Palette
	private static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Indigo
	private static final Color PRIMARY_HOVER = new Color(67, 56, 202);
	private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
	private static final Color WARNING_COLOR = new Color(245, 158, 11);
	private static final Color DANGER_COLOR = new Color(239, 68, 68);
	private static final Color BACKGROUND = new Color(249, 250, 251);
	private static final Color CARD_BG = Color.WHITE;
	private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
	private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
	private static final Color BORDER_COLOR = new Color(229, 231, 235);

	public EmployeePanel(int employeeId) {
		this.employeeId = employeeId;
		setLayout(new BorderLayout(0, 0));
		setBackground(BACKGROUND);

		// Header Panel
		var headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);

		// Tabbed Pane
		var tabPane = createStyledTabbedPane();
		tabPane.addTab("👤 Thông tin cá nhân", createPersonalInfoPanel());
		tabPane.addTab("📄 Hợp đồng & Lương", createContractSalaryPanel());
		tabPane.addTab("📅 Lịch làm & Chấm công", createWorkSchedulePanel());
		tabPane.addTab("⏰ Làm thêm giờ", createOvertimePanel());
		tabPane.addTab("🏢 Nhà hàng", createRestaurantPanel());

		var contentWrapper = new JPanel(new BorderLayout());
		contentWrapper.setBackground(BACKGROUND);
		contentWrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
		contentWrapper.add(tabPane, BorderLayout.CENTER);

		add(contentWrapper, BorderLayout.CENTER);
	}

	private JPanel createHeaderPanel() {
		var header = new JPanel(new BorderLayout());
		header.setBackground(PRIMARY_COLOR);
		header.setBorder(new EmptyBorder(20, 30, 20, 30));

		var title = new JLabel("Hồ Sơ Nhân Viên");
		title.setFont(new Font("Segoe UI", Font.BOLD, 28));
		title.setForeground(Color.WHITE);

		var subtitle = new JLabel("Quản lý thông tin và theo dõi hoạt động");
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitle.setForeground(new Color(224, 231, 255));

		var titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
		titlePanel.setOpaque(false);
		titlePanel.add(title);
		titlePanel.add(subtitle);

		header.add(titlePanel, BorderLayout.WEST);
		return header;
	}

	private JTabbedPane createStyledTabbedPane() {
		var tabPane = new JTabbedPane();
		tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tabPane.setBackground(CARD_BG);
		tabPane.setForeground(TEXT_PRIMARY);
		return tabPane;
	}

	// ---------------------------- 1. Thông tin cá nhân ----------------------------
	private JPanel createPersonalInfoPanel() {
		var mainPanel = new JPanel(new BorderLayout(0, 20));
		mainPanel.setBackground(BACKGROUND);
		mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

		// Top card (Avatar + Info)
		var topCard = createCardPanel();
		topCard.setLayout(new BorderLayout(30, 0));

		// Avatar
		var avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		avatarPanel.setOpaque(false);

		lblAvatar = new JLabel("👤");
		lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 80));
		lblAvatar.setHorizontalAlignment(JLabel.CENTER);
		lblAvatar.setPreferredSize(new Dimension(150, 150));
		lblAvatar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 3),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
				));
		lblAvatar.setBackground(new Color(243, 244, 246));
		lblAvatar.setOpaque(true);

		avatarPanel.add(lblAvatar);

		// Info Grid
		var infoGrid = new JPanel(new GridLayout(8, 2, 20, 15));
		infoGrid.setOpaque(false);
		infoGrid.setBorder(new EmptyBorder(10, 0, 10, 0));

		lblUsername = createDataLabel();
		lblName = createDataLabel();
		lblGender = createDataLabel();
		lblDob = createDataLabel();
		lblEmail = createDataLabel();
		lblPhone = createDataLabel();
		lblRole = createDataLabel();
		lblStatus = createDataLabel();

		addInfoRow(infoGrid, "Tên đăng nhập", lblUsername);
		addInfoRow(infoGrid, "Họ và tên", lblName);
		addInfoRow(infoGrid, "Giới tính", lblGender);
		addInfoRow(infoGrid, "Ngày sinh", lblDob);
		addInfoRow(infoGrid, "Email", lblEmail);
		addInfoRow(infoGrid, "Số điện thoại", lblPhone);
		addInfoRow(infoGrid, "Vai trò", lblRole);
		addInfoRow(infoGrid, "Trạng thái", lblStatus);

		topCard.add(avatarPanel, BorderLayout.WEST);
		topCard.add(infoGrid, BorderLayout.CENTER);

		// Update Card
		var updateCard = createCardPanel();
		updateCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

		var lblTitle = new JLabel("Cập nhật thông tin liên hệ");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTitle.setForeground(TEXT_PRIMARY);
		updateCard.add(lblTitle);

		var formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		formPanel.setOpaque(false);

		txtEmail = createStyledTextField(20);
		txtPhone = createStyledTextField(15);

		formPanel.add(createFieldLabel("Email mới:"));
		formPanel.add(txtEmail);
		formPanel.add(createFieldLabel("SĐT mới:"));
		formPanel.add(txtPhone);

		btnUpdate = createStyledButton("Cập nhật thông tin", PRIMARY_COLOR);
		btnUpdate.addActionListener(e -> updatePersonalInfo());
		formPanel.add(btnUpdate);

		updateCard.add(formPanel);

		mainPanel.add(topCard, BorderLayout.NORTH);
		mainPanel.add(updateCard, BorderLayout.CENTER);

		loadPersonalInfo();
		return mainPanel;
	}

	private void addInfoRow(JPanel panel, String label, JLabel valueLabel) {
		var lbl = new JLabel(label);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbl.setForeground(TEXT_SECONDARY);
		panel.add(lbl);
		panel.add(valueLabel);
	}

	private void loadPersonalInfo() {
		var sql = """
				SELECT e.name, e.gender, e.dob, e.email, e.phone, e.role, a.active, a.account_name
				FROM tbl_employee e
				JOIN tbl_account a ON e.id = a.employee_id
				WHERE e.id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			var rs = ps.executeQuery();
			if (rs.next()) {
				lblName.setText(rs.getString("name"));
				lblGender.setText(rs.getString("gender"));
				lblDob.setText(String.valueOf(rs.getDate("dob")));
				lblEmail.setText(rs.getString("email"));
				lblPhone.setText(rs.getString("phone"));
				lblRole.setText(rs.getString("role"));

				var isActive = rs.getBoolean("active");
				lblStatus.setText(isActive ? "Active" : "Inactive");
				lblStatus.setForeground(isActive ? SUCCESS_COLOR : DANGER_COLOR);
				lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));

				lblUsername.setText(rs.getString("account_name"));
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	private void updatePersonalInfo() {
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(
						"UPDATE tbl_employee SET email=?, phone=? WHERE id=?")) {
			ps.setString(1, txtEmail.getText().trim());
			ps.setString(2, txtPhone.getText().trim());
			ps.setInt(3, employeeId);
			var updated = ps.executeUpdate();
			if (updated > 0) {
				showSuccessDialog("Cập nhật thành công!");
				loadPersonalInfo();
				txtEmail.setText("");
				txtPhone.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog("Có lỗi xảy ra khi cập nhật!");
		}
	}

	// ---------------------------- 2. Hợp đồng & Lương ----------------------------
	private JPanel createContractSalaryPanel() {
		var panel = new JPanel(new GridLayout(2, 1, 0, 20));
		panel.setBackground(BACKGROUND);
		panel.setBorder(new EmptyBorder(25, 25, 25, 25));

		// Contract Card
		var contractCard = createCardPanel();
		contractCard.setLayout(new BorderLayout(0, 15));
		var lblContract = createSectionTitle("📄 Danh sách hợp đồng");
		contractCard.add(lblContract, BorderLayout.NORTH);

		String[] col1 = {"Mã HĐ", "Vị trí", "Lương cơ bản", "Bắt đầu", "Kết thúc", "Trạng thái"};
		contractModel = new DefaultTableModel(col1, 0);
		var tblContract = createStyledTable(contractModel);
		contractCard.add(new JScrollPane(tblContract), BorderLayout.CENTER);

		// Salary Card
		var salaryCard = createCardPanel();
		salaryCard.setLayout(new BorderLayout(0, 15));
		var lblSalary = createSectionTitle("💰 Bảng lương tháng");
		salaryCard.add(lblSalary, BorderLayout.NORTH);

		String[] col2 = {"Tháng", "Tổng ca", "OT", "Đi trễ", "Về sớm", "Lương LV", "Lương OT", "Thưởng", "Tổng", "Trạng thái"};
		salaryModel = new DefaultTableModel(col2, 0);
		var tblSalary = createStyledTable(salaryModel);
		salaryCard.add(new JScrollPane(tblSalary), BorderLayout.CENTER);

		panel.add(contractCard);
		panel.add(salaryCard);

		loadContractSalaryData();
		return panel;
	}

	private void loadContractSalaryData() {
		try (var conn = DBConnection.getConnection()) {
			// Contracts
			var ps1 = conn.prepareStatement(
					"SELECT * FROM tbl_contract WHERE employee_id=?");
			ps1.setInt(1, employeeId);
			var rs1 = ps1.executeQuery();
			contractModel.setRowCount(0);
			while (rs1.next()) {
				contractModel.addRow(new Object[]{
						rs1.getInt("contract_id"),
						rs1.getString("position"),
						String.format("%,.0f VNĐ", rs1.getDouble("salary")),
						rs1.getDate("start_date"),
						rs1.getDate("end_date"),
						rs1.getString("status")
				});
			}

			// Salary
			var ps2 = conn.prepareStatement(
					"SELECT * FROM tbl_monthly_summary WHERE employee_id=?");
			ps2.setInt(1, employeeId);
			var rs2 = ps2.executeQuery();
			salaryModel.setRowCount(0);
			while (rs2.next()) {
				salaryModel.addRow(new Object[]{
						rs2.getInt("month") + "/" + rs2.getInt("year"),
						rs2.getInt("totalShift"),
						String.format("%.1fh", rs2.getDouble("totalOverTime")),
						rs2.getInt("totalComeLate"),
						rs2.getInt("totalEarlyLeave"),
						String.format("%,.0f", rs2.getDouble("totalWorkSalary")),
						String.format("%,.0f", rs2.getDouble("totalOtSalary")),
						String.format("%,.0f", rs2.getDouble("bonus")),
						String.format("%,.0f VNĐ", rs2.getDouble("finalSalary")),
						rs2.getString("status")
				});
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	// ---------------------------- 3. Lịch làm & chấm công ----------------------------
	private JPanel createWorkSchedulePanel() {
		var panel = createCardPanel();
		panel.setLayout(new BorderLayout(0, 15));
		panel.setBorder(new EmptyBorder(25, 25, 25, 25));

		var title = createSectionTitle("📅 Lịch sử chấm công");
		panel.add(title, BorderLayout.NORTH);

		String[] col = {"Ngày", "Ca làm", "Check-in", "Check-out", "Đi trễ", "Về sớm", "OT (giờ)", "Nghỉ phép"};
		scheduleModel = new DefaultTableModel(col, 0);
		var tbl = createStyledTable(scheduleModel);
		panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

		loadScheduleData();
		return panel;
	}

	private void loadScheduleData() {
		var sql = """
				SELECT w.work_date, s.shift_name, w.check_in_time, w.check_out_time,
				       w.come_late, w.early_leave, w.total_ot, a.absent_type
				FROM tbl_work_schedule w
				JOIN tbl_shift s ON w.shift_id = s.id
				LEFT JOIN tbl_absent a ON w.absent_id = a.id
				WHERE w.employee_id=?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			var rs = ps.executeQuery();
			scheduleModel.setRowCount(0);
			while (rs.next()) {
				scheduleModel.addRow(new Object[]{
						rs.getDate("work_date"),
						rs.getString("shift_name"),
						rs.getTime("check_in_time"),
						rs.getTime("check_out_time"),
						rs.getBoolean("come_late") ? "✓" : "-",
								rs.getBoolean("early_leave") ? "✓" : "-",
										String.format("%.1f", rs.getDouble("total_ot")),
										rs.getString("absent_type") != null ? rs.getString("absent_type") : "-"
				});
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	// ---------------------------- 4. Làm thêm giờ ----------------------------
	private JPanel createOvertimePanel() {
		var panel = createCardPanel();
		panel.setLayout(new BorderLayout(0, 15));
		panel.setBorder(new EmptyBorder(25, 25, 25, 25));

		var title = createSectionTitle("⏰ Lịch sử làm thêm giờ");
		panel.add(title, BorderLayout.NORTH);

		String[] col = {"Ngày", "Loại OT", "Giờ bắt đầu", "Giờ kết thúc", "Xác nhận"};
		otModel = new DefaultTableModel(col, 0);
		var tbl = createStyledTable(otModel);
		panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

		loadOTData();
		return panel;
	}

	private void loadOTData() {
		var sql = """
				SELECT w.work_date, t.ot_name, j.ot_check_in_time, j.ot_check_out_time, j.ot_confirm
				FROM tbl_ot_junction j
				JOIN tbl_ot_type t ON j.ot_type_id = t.id
				JOIN tbl_work_schedule w ON j.work_schedule_id = w.id
				WHERE w.employee_id=?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			var rs = ps.executeQuery();
			otModel.setRowCount(0);
			while (rs.next()) {
				otModel.addRow(new Object[]{
						rs.getDate("work_date"),
						rs.getString("ot_name"),
						rs.getTime("ot_check_in_time"),
						rs.getTime("ot_check_out_time"),
						rs.getBoolean("ot_confirm") ? "✓ Đã xác nhận" : "⏳ Chờ xác nhận"
				});
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	// ---------------------------- 5. Nhà hàng ----------------------------
	private JPanel createRestaurantPanel() {
		var panel = createCardPanel();
		panel.setLayout(new BorderLayout(0, 15));
		panel.setBorder(new EmptyBorder(25, 25, 25, 25));

		var title = createSectionTitle("🏢 Nhà hàng làm việc");
		panel.add(title, BorderLayout.NORTH);

		String[] col = {"Tên nhà hàng", "Địa chỉ"};
		restaurantModel = new DefaultTableModel(col, 0);
		var tbl = createStyledTable(restaurantModel);
		panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

		loadRestaurantData();
		return panel;
	}

	private void loadRestaurantData() {
		var sql = """
				SELECT r.name AS restaurant_name, s.street_name
				FROM tbl_employee_restaurant er
				JOIN tbl_restaurant r ON er.restaurant_id = r.id
				JOIN tbl_street s ON r.street_id = s.id
				WHERE er.employee_id=?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			var rs = ps.executeQuery();
			restaurantModel.setRowCount(0);
			while (rs.next()) {
				restaurantModel.addRow(new Object[]{
						rs.getString("restaurant_name"),
						rs.getString("street_name")
				});
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	// ---------------------------- Utility Methods ----------------------------
	private JPanel createCardPanel() {
		var card = new JPanel();
		card.setBackground(CARD_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(20, 20, 20, 20)
				));
		return card;
	}

	private JLabel createSectionTitle(String text) {
		var label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label.setForeground(TEXT_PRIMARY);
		label.setBorder(new EmptyBorder(0, 0, 10, 0));
		return label;
	}

	private JLabel createFieldLabel(String text) {
		var label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.BOLD, 13));
		label.setForeground(TEXT_SECONDARY);
		return label;
	}

	private JLabel createDataLabel() {
		var lbl = new JLabel("-");
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lbl.setForeground(TEXT_PRIMARY);
		return lbl;
	}

	private JTextField createStyledTextField(int columns) {
		var txt = new JTextField(columns);
		txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txt.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(8, 12, 8, 12)
				));
		return txt;
	}

	private JButton createStyledButton(String text, Color bgColor) {
		var btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setBackground(bgColor);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setBorder(new EmptyBorder(10, 20, 10, 20));

		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(PRIMARY_HOVER);
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(bgColor);
			}
		});

		return btn;
	}

	private JTable createStyledTable(DefaultTableModel model) {
		var table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(40);
		table.setShowGrid(true);
		table.setGridColor(BORDER_COLOR);
		table.setSelectionBackground(new Color(224, 231, 255));
		table.setSelectionForeground(TEXT_PRIMARY);
		table.setIntercellSpacing(new Dimension(10, 0));

		var header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(243, 244, 246));
		header.setForeground(TEXT_PRIMARY);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));

		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		return table;
	}

	private void showSuccessDialog(String message) {
		JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}
