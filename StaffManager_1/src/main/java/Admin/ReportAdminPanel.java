package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.chart.PieChartPanel;
import com.example.swingapp.model.ReportSummary;
import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

public class ReportAdminPanel extends JPanel {
	private JComboBox<String> monthFilter;
	private JComboBox<Restaurant> resFilter;
	private boolean isInitializing = true;
	private JLabel lblTotalSalary;
	private JLabel lblTotalHours;
	private JLabel lblTotalOvertime;
	private JLabel lblTotalOvertimeSalary;
	private JLabel lblEmployeeCount;
	private JLabel lblOntimePercentage;
	private ReportSummary report;
	private PieChartPanel pieChartSalary;
	private PieChartPanel pieChartOnTime;
	private PieChartPanel pieChartEmployee;
	private Map<String, Number> salaryDistribution;
	// Colors
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public ReportAdminPanel() {
		if (!java.beans.Beans.isDesignTime()) {
			initUI();
		} else {
			setLayout(new BorderLayout());
			add(new JPanel());
		}
	}

	private void initUI() {
		setLayout(new BorderLayout(15, 15));
		setBackground(BG_LIGHT);

		// ===== HEADER =====
		var header = new JLabel("Báo Cáo & Thống Kê", SwingConstants.CENTER);
		header.setFont(new Font("Segoe UI", Font.BOLD, 24));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(20, 0, 10, 0));
		add(header, BorderLayout.NORTH);

		// ===== CONTENT CENTER PANEL =====
		var centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0, 10));
		centerPanel.setBackground(BG_LIGHT);

		// ===== PANEL LỌC =====
		var filterPanel = new JPanel();
		filterPanel.setBackground(BG_LIGHT);
		filterPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

		var lblMonth = new JLabel("Chọn tháng: ");
		lblMonth.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		monthFilter = new JComboBox<String>();
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
		monthFilter.addActionListener(e -> changeMonth());

		var lblRestaurant = new JLabel("Chọn Nhà Hàng: ");
		lblRestaurant.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		resFilter = new JComboBox<Restaurant>();
		resFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		resFilter.setBackground(new Color(248, 250, 252));
		resFilter.setPreferredSize(new Dimension(200, 36));
		resFilter.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));

		renderRestaurant();
		resFilter.setSelectedIndex(0);
		resFilter.addActionListener(e -> changeRestaurant());

		filterPanel.add(lblMonth);
		filterPanel.add(monthFilter);
		filterPanel.add(lblRestaurant);
		filterPanel.add(resFilter);

		centerPanel.add(filterPanel, BorderLayout.NORTH);

		// ===== KPI + CHART CONTAINER =====
		var contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(BG_LIGHT);
		contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

		// ===== KPI CONTAINER (2 hàng) =====
		var kpiContainer = new JPanel();
		kpiContainer.setLayout(new BoxLayout(kpiContainer, BoxLayout.Y_AXIS));
		kpiContainer.setOpaque(false);

		// ===== HÀNG 1 =====
		var row1 = new JPanel();
		row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
		row1.setOpaque(false);

		lblTotalSalary = new JLabel();
		lblTotalHours = new JLabel();
		lblTotalOvertime = new JLabel();
		lblTotalOvertimeSalary = new JLabel();

		row1.add(createKPIBlock("Tổng chi phí lương", new Color(230, 230, 255), Color.MAGENTA, lblTotalSalary));
		row1.add(Box.createRigidArea(new Dimension(15, 0)));
		row1.add(createKPIBlock("Tổng giờ làm", new Color(255, 230, 230), Color.RED, lblTotalHours));
		row1.add(Box.createRigidArea(new Dimension(15, 0)));
		row1.add(createKPIBlock("Tổng giờ tăng ca", new Color(230, 245, 255), Color.BLUE, lblTotalOvertime));
		row1.add(Box.createRigidArea(new Dimension(15, 0)));
		row1.add(createKPIBlock("Tổng lương tăng ca", new Color(255, 250, 230), new Color(255, 200, 0), lblTotalOvertimeSalary));

		kpiContainer.add(row1);
		kpiContainer.add(Box.createRigidArea(new Dimension(0, 15)));

		// ===== HÀNG 2 =====
		var row2 = new JPanel();
		row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
		row2.setOpaque(false);

		lblEmployeeCount = new JLabel();
		lblOntimePercentage = new JLabel();

		row2.add(createKPIBlock("Tổng số nhân viên", new Color(235, 255, 235), new Color(0, 150, 136), lblEmployeeCount));
		row2.add(Box.createRigidArea(new Dimension(15, 0)));
		row2.add(createKPIBlock("Tỉ lệ đúng giờ", new Color(255, 240, 240), DANGER_RED, lblOntimePercentage));

		kpiContainer.add(row2);

		// ===== BIỂU ĐỒ =====
		var chartContainer = new JPanel();
		chartContainer.setOpaque(false);
		chartContainer.setLayout(new BoxLayout(chartContainer, BoxLayout.X_AXIS));
		chartContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

		pieChartSalary = new PieChartPanel();
		pieChartSalary.setPreferredSize(new Dimension(300, 250));
		pieChartSalary.setTitle("Tỷ lệ chi phí lương giữa các nhà hàng");
		chartContainer.add(pieChartSalary);
		chartContainer.add(Box.createRigidArea(new Dimension(20, 0)));

		pieChartOnTime = new PieChartPanel();
		pieChartOnTime.setPreferredSize(new Dimension(300, 250));
		pieChartOnTime.setTitle("Tỷ lệ đúng giờ giữa các nhà hàng");
		chartContainer.add(pieChartOnTime);
		chartContainer.add(Box.createRigidArea(new Dimension(20, 0)));

		pieChartEmployee = new PieChartPanel();
		pieChartEmployee.setPreferredSize(new Dimension(300, 250));
		pieChartEmployee.setTitle("Tỷ lệ nhân sự giữa các nhà hàng");
		chartContainer.add(pieChartEmployee);

		contentPanel.add(kpiContainer);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		contentPanel.add(chartContainer);

		centerPanel.add(contentPanel, BorderLayout.CENTER);
		add(centerPanel, BorderLayout.CENTER);
	}

	private JPanel createKPIBlock(String title, Color bgColor, Color valueColor, JLabel valueLabel) {
		var panel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(bgColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setPreferredSize(new Dimension(0, 100));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		panel.setAlignmentY(TOP_ALIGNMENT);

		var lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitle.setForeground(PRIMARY_BLUE);
		panel.add(lblTitle, BorderLayout.NORTH);

		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		valueLabel.setForeground(valueColor);
		valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		valueLabel.setText("..."); // giá trị mặc định
		panel.add(valueLabel, BorderLayout.CENTER);

		var lblIcon = new JLabel("\u23F0");
		lblIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
		lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblIcon, BorderLayout.EAST);

		return panel;
	}


	public static void main(String[] args) {
		var frame = new JFrame("Report Dashboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 350);
		frame.add(new ReportAdminPanel());
		frame.setVisible(true);
	}

	private void changeMonth() {
		loadStatistic();
	}
	private void changeRestaurant() {
		loadStatistic();
	}

	private void loadStatistic() {
		if (isInitializing) {
			return;
		}
		try {
			report = getReportSummary();
			var totalSalary = report.getTotalSalary();
			var totalHours = report.getTotalHours();
			var totalOvertime = report.getTotalOverTime();
			var totalOvertimeSalary = report.getTotalOverTimeSalary();
			var totalEmployee = report.getTotalEmployee();
			var ontimePercentage = report.getOntimePercentage();

			lblTotalSalary.setText(String.format("%,.0f VNĐ", totalSalary));
			lblTotalHours.setText(String.format("%,.0f giờ", totalHours));
			lblTotalOvertime.setText(String.format("%,.0f giờ", totalOvertime));
			lblTotalOvertimeSalary.setText(String.format("%,.0f VNĐ", totalOvertimeSalary));
			lblEmployeeCount.setText(String.format("%d Người", totalEmployee));
			lblOntimePercentage.setText(String.format("%.1f %%", ontimePercentage));

			if (pieChartSalary != null && report.getSalaryDistribution() != null) {
				pieChartSalary.updateData(report.getSalaryDistribution());
				pieChartSalary.repaint();
			}
			if (pieChartOnTime != null && report.getOnTimeDistribution() != null) {
				pieChartOnTime.updateData(report.getOnTimeDistribution());
				pieChartOnTime.repaint();
			}
			if (pieChartEmployee != null && report.getEmployeeDistribution() != null) {
				pieChartEmployee.updateData(report.getEmployeeDistribution());
				pieChartEmployee.repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Lỗi tải dữ liệu thống kê: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
	private ReportSummary getReportSummary() {
		var summary = new ReportSummary();
		var selectedRestaurant = (Restaurant) resFilter.getSelectedItem();
		var restaurantId = selectedRestaurant.getId();
		var monthStr = (String) monthFilter.getSelectedItem();

		int month = 0, year = 0;
		if (monthStr != null && monthStr.contains("/")) {
			var parts = monthStr.replace("Tháng", "").split("/");
			month = Integer.parseInt(parts[0].trim());
			year = Integer.parseInt(parts[1].trim());
		}


		try (var conn = DBConnection.getConnection()) {

			try (var stmt = conn.prepareCall("{CALL SP_GetTotalSalary(?,?,?)}")) {
				stmt.setInt(1, month);
				stmt.setInt(2, year);
				stmt.setInt(3, restaurantId);
				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						summary.setTotalSalary(rs.getDouble("total_salary"));
						var totalWorkMinutes = rs.getDouble("total_work_time");
						var totalOverMinutes = rs.getDouble("total_over_time");
						summary.setTotalHours(totalWorkMinutes / 60.0);
						summary.setTotalOverTime(totalOverMinutes / 60.0);
						summary.setTotalOverTimeSalary(rs.getDouble("total_ot_salary"));
					}
				}
			}

			try (var stmt = conn.prepareCall("{CALL SP_GetCountEmployee(?)}")) {
				stmt.setInt(1, restaurantId);

				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						summary.setTotalEmployee(rs.getInt("number_employee"));
					}
				}
			}

			try (var stmt = conn.prepareCall("{CALL SP_GetOnTimeByRestaurantId(?,?,?)}")) {
				stmt.setInt(1, month);
				stmt.setInt(2, year);
				stmt.setInt(3, restaurantId);

				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						summary.setOntimePercentage(rs.getDouble("on_time_percentage"));
					}
				}
			}

			try (var stmt = conn.prepareCall("{CALL SP_GetResMontlySalary(?,?)}")) {
				stmt.setInt(1, month);
				stmt.setInt(2, year);
				Map<String, Number> salaryMap = new LinkedHashMap<>();

				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						salaryMap.put(rs.getString("restaurant_name"), rs.getDouble("total_salary"));
					}
				}
				summary.setSalaryDistribution(salaryMap);
			}

			try (var stmt = conn.prepareCall("{CALL SP_GetOnTimeAllByRestaurant(?,?)}")) {
				stmt.setInt(1, month);
				stmt.setInt(2, year);
				Map<String, Number> onTimeMap = new LinkedHashMap<>();

				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						onTimeMap.put(rs.getString("restaurant_name"), rs.getDouble("on_time_percentage"));
					}
				}
				summary.setOnTimeDistribution(onTimeMap);
			}

			try (var stmt = conn.prepareCall("{CALL SP_GetEmployeeCount}")) {
				Map<String, Number> employeeMap = new LinkedHashMap<>();

				try (var rs = stmt.executeQuery()) {
					while (rs.next()) {
						employeeMap.put(rs.getString("restaurant_name"), rs.getDouble("employee_amount"));
					}
				}
				summary.setEmployeeDistribution(employeeMap);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return summary;
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
}
