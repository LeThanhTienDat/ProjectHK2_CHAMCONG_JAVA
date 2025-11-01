package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.util.DBConnection;

public class AttendanceAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private JTextField txtSearch;
	private JComboBox<String> cmbMonthYear;
	private JComboBox<String> cmbRestaurantList;

	// ===== Màu sắc =====
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
		setLayout(new BorderLayout(0, 15));
		initComponents();
	}

	// ========== WindowBuilder-Ready Init ==========
	private void initComponents() {
		add(createSearchPanel(), BorderLayout.NORTH);
		add(createTableCard(), BorderLayout.CENTER);
		add(createActionPanel(), BorderLayout.SOUTH);
	}

	// ======= Thanh tìm kiếm =======
	private JPanel createSearchPanel() {

		var p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		p.setOpaque(true);
		p.setBackground(CARD_WHITE);
		p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		p.setPreferredSize(new Dimension(0, 70));

		var restaurantNameSql = "{CALL SP_GetRestaurantName}";
		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall(restaurantNameSql)) {
			var rs = stmt.executeQuery();
			List<String> restaurantNames = new ArrayList<>();
			while (rs.next()) {
				var name = rs.getString("name");
				restaurantNames.add(name);
			}

			String[] months = { "01/2025", "02/2025", "03/2025", "04/2025", "05/2025", "06/2025",
					"07/2025", "08/2025", "09/2025", "10/2025", "11/2025", "12/2025" };
			cmbMonthYear = new JComboBox<String>(months);
			cmbMonthYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			cmbMonthYear.setBackground(CARD_WHITE);
			cmbMonthYear.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
			cmbMonthYear.setPreferredSize(new Dimension(140, 36));
			cmbMonthYear.addActionListener(e -> updateTableHeaderAndData());

			cmbRestaurantList = new JComboBox<>(restaurantNames.toArray(new String[0]));
			cmbRestaurantList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			cmbRestaurantList.setBackground(CARD_WHITE);
			cmbRestaurantList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
			cmbRestaurantList.setPreferredSize(new Dimension(140, 36));
			//			cmbRestaurantList.addActionListener(e -> updateTableHeaderAndData());

			txtSearch = styledField("Tìm kiếm theo tên nhân viên...", 400);
			txtSearch.setColumns(30);
			var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 110);
			btnSearch.addActionListener(e -> performSearch());

			p.add(txtSearch);
			p.add(new JLabel("Tháng/Năm: "));
			p.add(cmbMonthYear);
			p.add(new JLabel("Thuộc nhà hàng: "));
			p.add(cmbRestaurantList);
			p.add(Box.createHorizontalStrut(10));
			p.add(btnSearch);
			return p;

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu: " + ex.getMessage());
		}
		return p;
	}

	private void updateTableHeaderAndData() {
		var selectedMonth = (String) cmbMonthYear.getSelectedItem();
		JOptionPane.showMessageDialog(this,
				"Cập nhật bảng chấm công cho kỳ: " + selectedMonth + " (Demo nghiệp vụ quản lý theo tháng)");
	}

	private void performSearch() {
		var query = txtSearch.getText().toLowerCase();
		if (query.isEmpty()) {
			return;
		}
		for (var i = 0; i < model.getRowCount(); i++) {
			var name = model.getValueAt(i, 2).toString().toLowerCase();
			if (name.contains(query)) {
				table.setRowSelectionInterval(i, i);
				break;
			}
		}
		JOptionPane.showMessageDialog(this, "Tìm thấy kết quả cho: " + query + " (Demo lọc theo tên nhân viên)");
	}

	private JTextField styledField(String ph, int w) {
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

	private JPanel createTableCard() {
		var card = new JPanel(new BorderLayout());
		card.setBorder(new EmptyBorder(15, 15, 15, 15));
		card.setOpaque(false);

		// tạo panel headerVertical chứa header text + legend (stacked)
		var headerVertical = new JPanel();
		headerVertical.setLayout(new BorderLayout());
		headerVertical.setOpaque(false);

		var header = new JLabel("BẢNG CHẤM CÔNG TỔNG HỢP THÁNG 01/2025");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		headerVertical.add(header, BorderLayout.NORTH);

		var legendPanel = createLegendPanel();
		headerVertical.add(legendPanel, BorderLayout.CENTER);

		// Add chỉ một lần: headerVertical
		card.add(headerVertical, BorderLayout.NORTH);

		String[] infoCols = { "STT", "MSNV", "Họ và tên", "Chức danh", "Phòng ban" };
		var dayCols = new String[31];
		for (var i = 0; i < 31; i++) {
			dayCols[i] = String.format("%02d/01", i + 1);
		}
		String[] summaryCols = { "Công Ngày", "Công Đêm", "Tổng Công",
				"Tăng ca Ngày", "Tăng ca Nghỉ", "Tăng ca Lễ", "Tổng Tăng Ca",
				"WFH", "Tăng ca WFH", "Tổng WFH",
				"Phép", "Lễ", "Nghỉ riêng", "Nghỉ bù", "Ngừng việc", "Tổng CN Có Lương",
				"BHXH", "Không lương", "Không phép", "Tổng CN Không Lương" };

		var cols = new String[infoCols.length + dayCols.length + summaryCols.length];
		System.arraycopy(infoCols, 0, cols, 0, infoCols.length);
		System.arraycopy(dayCols, 0, cols, infoCols.length, dayCols.length);
		System.arraycopy(summaryCols, 0, cols, infoCols.length + dayCols.length, summaryCols.length);

		Object[][] data = { { 1, "LMS00184", "Nguyễn Văn A", "Nhân viên", "Kinh doanh",
			"X", "X", "X", "X", "P", "X", "X", "X", "X", "X", "P", "X", "X", "X", "X", "X",
			"X", "X", "X", "P", "X", "X", "X", "X", "X", "X", "X",
			20, 5, 25, 1, 1, 0, 2, 1, 0, 1, 2, 1, 1, 0, 1, 0, 0, 2 } };

		model = new DefaultTableModel(data, cols) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		table = new JTable(model);
		styleTable(table);

		for (var i = 0; i < table.getColumnCount(); i++) {
			var width = (i < 5) ? 110 : (i < 36 ? 45 : 90);
			table.getColumnModel().getColumn(i).setPreferredWidth(width);
		}

		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		card.add(sp, BorderLayout.CENTER);
		return card;
	}

	private JPanel createLegendPanel() {
		var legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		legend.setBackground(CARD_WHITE);
		legend.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(10, 0, 10, 0)));

		String[][] legends = { { "X", "Làm việc bình thường (Có mặt)" },
				{ "P", "Phép năm" },
				{ "L", "Lễ/Tết" },
				{ "N", "Nghỉ không lương" },
				{ "W", "WFH (Làm việc từ xa)" },
				{ "T", "Tăng ca" } };

		for (String[] legend2 : legends) {
			var icon = new JLabel(legend2[0]);
			icon.setFont(new Font("Segoe UI", Font.BOLD, 12));
			icon.setForeground(PRIMARY_BLUE);
			icon.setPreferredSize(new Dimension(20, 20));
			icon.setToolTipText(legend2[1]);

			var desc = new JLabel(legend2[1]);
			desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			desc.setForeground(TEXT_PRIMARY);

			var item = new JPanel(new BorderLayout(5, 0));
			item.add(icon, BorderLayout.WEST);
			item.add(desc, BorderLayout.CENTER);
			legend.add(item);
		}
		return legend;
	}

	private void styleTable(JTable t) {
		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(45);
		t.setGridColor(BORDER_COLOR);
		t.setShowGrid(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		var h = t.getTableHeader();
		h.setPreferredSize(new Dimension(0, 45));
		h.setReorderingAllowed(false);

		var headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		headerRenderer.setForeground(Color.WHITE);
		headerRenderer.setBackground(PRIMARY_BLUE);
		headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
		headerRenderer.setOpaque(true);

		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}

		var center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(JLabel.CENTER);
		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(center);
		}
	}

	private JPanel createActionPanel() {
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

	private void showLegendDialog() {
		JOptionPane.showMessageDialog(this, "Hiển thị ký hiệu chấm công (demo)");
	}

	private void approveAttendance() {
		var selectedRows = table.getSelectedRowCount();
		if (selectedRows == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần duyệt!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this,
				"Duyệt chấm công cho " + selectedRows + " nhân viên (Demo: cập nhật DB)");
	}

	private static JButton createButton(String text, Color bg, int w) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setPreferredSize(new Dimension(w, 36));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(true);
		b.setOpaque(true);
		return b;
	}

	private void deleteRow() {
		var r = table.getSelectedRow();
		if (r == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (JOptionPane.showConfirmDialog(this,
				"Xóa dòng này? (Sẽ xóa dữ liệu chấm công của nhân viên)", "Xác nhận",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			model.removeRow(r);
		}
	}

	private void printPDF() {
		try {
			var h = new MessageFormat("BẢNG CHẤM CÔNG NHÂN VIÊN - THÁNG " + cmbMonthYear.getSelectedItem());
			var f = new MessageFormat("Trang {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage());
		}
	}
}
