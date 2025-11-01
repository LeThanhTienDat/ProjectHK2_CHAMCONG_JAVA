package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Contract;
import com.example.swingapp.service.ContractService;
import com.example.swingapp.util.DBConnection;

public class ContractAdminPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private ContractFormPanel formPanel;

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

		txtSearch = styledField("Tìm kiếm theo tên nhân viên hoặc vị trí...", 400);
		txtSearch.setColumns(30);
		searchPanel.add(txtSearch);

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 110, 36);
		btnSearch.addActionListener(e -> search());
		searchPanel.add(btnSearch);

		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 110, 36);
		btnAdd.addActionListener(e -> addNew());
		searchPanel.add(btnAdd);

		// ==== CONTENT ====
		var content = new JPanel();
		content.setBackground(BG_LIGHT);
		content.setLayout(new BorderLayout(0, 15));
		add(content, BorderLayout.CENTER);

		formPanel = new ContractFormPanel(this::onSave, this::onCancel);
		formPanel.setVisible(false);
		content.add(formPanel, BorderLayout.NORTH);

		var tableCard = createTableCard();
		content.add(tableCard, BorderLayout.CENTER);

		// ==== ACTIONS (BOTTOM) ====
		var actions = new JPanel();
		actions.setBackground(BG_LIGHT);
		actions.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		add(actions, BorderLayout.SOUTH);

		var btnDelete = createButton("Xóa", DANGER_RED, 110, 36);
		btnDelete.addActionListener(e -> deleteRow());
		actions.add(btnDelete);

		var btnPDF = createButton("Xuất PDF", TEAL, 110, 36);
		btnPDF.addActionListener(e -> printPDF());
		actions.add(btnPDF);
	}

	private JPanel createTableCard() {
		var card = new JPanel();
		card.setLayout(new BorderLayout());
		card.setBackground(CARD_WHITE);
		card.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("DANH SÁCH HỢP ĐỒNG LAO ĐỘNG");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		card.add(header, BorderLayout.NORTH);

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
		loadContractTable("");
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

	private static JButton createButton(String text, Color bg, int w, int h) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setPreferredSize(new Dimension(w, h));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
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
		var q = txtSearch.getText().trim();
		if (q.isEmpty()) {
			loadContractTable("");
		} else {
			loadContractTable(q);
		}
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
					"Hợp đồng không thể xóa!",
					"Cảnh Báo",
					JOptionPane.WARNING_MESSAGE
					);
			return;
		} else {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void printPDF() {
		try {
			var h = new MessageFormat("DANH SÁCH HỢP ĐỒNG LAO ĐỘNG");
			var f = new MessageFormat("Trang {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadContractTable(String keyword) {
		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall("{CALL SP_GetContractInfo(?)}")) {

			if (keyword == null || keyword.trim().isEmpty()) {
				stmt.setNull(1, java.sql.Types.NVARCHAR);
			} else {
				stmt.setString(1, keyword.trim());
			}

			var rs = stmt.executeQuery();
			model.setRowCount(0);

			while (rs.next()) {
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

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu hợp đồng: " + ex.getMessage());
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
							"Nhân viên này hiện đã có hợp đồng còn hiệu lực!",
							"Cảnh báo",
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
					JOptionPane.showMessageDialog(this, "Add new Contract successful!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
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
						"Hợp đồng không thể chỉnh sửa!",
						"Cảnh Báo",
						JOptionPane.WARNING_MESSAGE
						);
			}
		}
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
		loadContractTable("");
	}

	private void onCancel(ActionEvent e) {
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}
}
