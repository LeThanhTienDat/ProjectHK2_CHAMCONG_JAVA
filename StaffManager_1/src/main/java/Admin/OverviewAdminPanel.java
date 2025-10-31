package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class OverviewAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private DefaultTableModel attendanceModel;
	private JTable tableAttendance;
	private JTextField txtSearchAttendance;

	public OverviewAdminPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(250, 251, 255));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		var mainContent = new JPanel();
		mainContent.setBackground(new Color(250, 251, 255));
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

		var welcomeLabel = new JLabel("Chào mừng đến với Trang Tổng Quan!");
		welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		welcomeLabel.setForeground(new Color(33, 33, 33));
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(welcomeLabel);

		mainContent.add(Box.createVerticalStrut(30));
		mainContent.add(createAttendanceSection());

		var scrollPane = new JScrollPane(mainContent);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}

	private JPanel createAttendanceSection() {
		var section = new JPanel(new BorderLayout());
		section.setBackground(Color.WHITE);
		section.setBorder(new CompoundBorder(
				new LineBorder(new Color(224, 235, 250), 1, true),
				new EmptyBorder(15, 15, 15, 15)));
		section.setPreferredSize(new Dimension(900, 450));

		var headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setBackground(Color.WHITE);

		var headerLabel = new JLabel("Tổng Quan Chấm Công Hôm Nay (23/10/2025)");
		headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		headerLabel.setForeground(new Color(25, 118, 210));
		headerPanel.add(headerLabel);

		headerPanel.add(Box.createHorizontalStrut(20));

		txtSearchAttendance = new JTextField("Tìm kiếm theo tên hoặc mã NV...", 20);
		txtSearchAttendance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearchAttendance.setBackground(new Color(248, 250, 252));
		txtSearchAttendance.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(new Color(224, 235, 250), 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		txtSearchAttendance.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ("Tìm kiếm theo tên hoặc mã NV...".equals(txtSearchAttendance.getText())) {
					txtSearchAttendance.setText("");
				}
			}
		});
		headerPanel.add(txtSearchAttendance);

		var btnSearch = createButton("Tìm", new Color(25, 118, 210));
		btnSearch.addActionListener(e -> searchAttendance());
		headerPanel.add(btnSearch);

		section.add(headerPanel, BorderLayout.NORTH);

		// Table setup
		String[] columns = { "Mã NV", "Họ Tên", "Trạng Thái", "Giờ Vào", "Giờ Ra", "Ghi Chú" };
		Object[][] data = {
				{ 1, "Nguyễn Văn A", "Đúng Giờ", "08:00", "17:00", "" },
				{ 2, "Trần Thị B", "Đi Trễ", "08:15", "17:00", "Cảnh báo" },
				{ 3, "Lê Văn C", "Vắng", "-", "-", "Nghỉ phép" },
				{ 4, "Phạm Thị D", "Đúng Giờ", "08:00", "18:30", "Tăng ca" },
				{ 5, "Hoàng Văn E", "Đúng Giờ", "08:00", "17:00", "" },
				{ 6, "Vũ Thị F", "Đúng Giờ", "07:45", "16:45", "Sớm" },
				{ 7, "Đặng Văn G", "Vắng", "-", "-", "Bệnh" }
		};

		attendanceModel = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableAttendance = new JTable(attendanceModel);
		styleTable(tableAttendance);

		tableAttendance.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (tableAttendance.getSelectedRow() != -1) {
					JOptionPane.showMessageDialog(OverviewAdminPanel.this,
							"Chi tiết nhân viên: " + attendanceModel.getValueAt(tableAttendance.getSelectedRow(), 1));
				}
			}
		});

		var tableScroll = new JScrollPane(tableAttendance);
		tableScroll.setBorder(new LineBorder(new Color(224, 235, 250), 1));
		section.add(tableScroll, BorderLayout.CENTER);

		var actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		actionPanel.setBackground(Color.WHITE);

		var btnRefresh = createButton("Làm Mới", new Color(33, 150, 243));
		btnRefresh.addActionListener(e -> refreshAttendance());
		actionPanel.add(btnRefresh);

		var btnExport = createButton("Xuất PDF", new Color(0, 150, 136));
		btnExport.addActionListener(e -> exportAttendancePDF());
		actionPanel.add(btnExport);

		section.add(actionPanel, BorderLayout.SOUTH);

		return section;
	}

	private static JButton createButton(String text, Color bgColor) {
		var btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setBackground(bgColor);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(100, 35));
		btn.setBorderPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return btn;
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
		header.setBackground(new Color(25, 118, 210));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(0, 40));

		var headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setBackground(new Color(25, 118, 210));
		headerRenderer.setForeground(Color.WHITE);
		headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			var cellRenderer = new DefaultTableCellRenderer();
			cellRenderer.setHorizontalAlignment(JLabel.CENTER);
			table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
		}
	}

	// Logic giữ nguyên
	private void searchAttendance() {
		var keyword = txtSearchAttendance.getText().trim();
		if ("Tìm kiếm theo tên hoặc mã NV...".equals(keyword) || keyword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông Báo",
					JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Đang tìm: " + keyword + "\n(Demo - Implement filter logic với dữ liệu thực tế)", "Tìm Kiếm",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void refreshAttendance() {
		JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu chấm công hôm nay (23/10/2025)!", "Thành Công",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportAttendancePDF() {
		try {
			var header = new java.text.MessageFormat("TỔNG QUAN CHẤM CÔNG HÔM NAY");
			var footer = new java.text.MessageFormat("Ngày {0,date,dd/MM/yyyy} - Trang {1,number,integer}");
			tableAttendance.print(javax.swing.JTable.PrintMode.FIT_WIDTH, header, footer);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}

