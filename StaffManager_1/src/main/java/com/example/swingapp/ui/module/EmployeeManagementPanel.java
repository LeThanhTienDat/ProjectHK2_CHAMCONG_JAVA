package com.example.swingapp.ui.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class EmployeeManagementPanel extends JPanel {

	private DefaultTableModel model;
	private JTable tableEmployee;
	private JTextField txtSearch;
	private EmployeeFormPanel formPanel;
	private JButton btnAdd;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEAL = new Color(0, 150, 136);

	public EmployeeManagementPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Top: Search Panel
		var searchPanel = createSearchPanel();
		add(searchPanel, BorderLayout.NORTH);

		// Center: Content Panel
		var contentPanel = new JPanel(new BorderLayout(0, 15));
		contentPanel.setBackground(BG_LIGHT);

		formPanel = new EmployeeFormPanel(this::handleFormSave, this::handleFormCancel);
		formPanel.setVisible(false);
		contentPanel.add(formPanel, BorderLayout.NORTH);

		var tableCard = createTablePanel();
		contentPanel.add(tableCard, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		// Bottom: Action Buttons
		var actionPanel = createActionPanel();
		add(actionPanel, BorderLayout.SOUTH);
	}

	private JPanel createSearchPanel() {
		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setPreferredSize(new Dimension(0, 70));
		searchPanel.setBackground(BG_LIGHT);

		txtSearch = new JTextField("Tìm kiếm theo tên, email, số điện thoại...", 30);
		txtSearch.setPreferredSize(new Dimension(400, 38));
		searchPanel.add(txtSearch);

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 120, 38);
		btnSearch.addActionListener(e -> searchEmployee());
		searchPanel.add(btnSearch);

		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 130, 38);
		btnAdd.addActionListener(e -> addEmployee());
		searchPanel.add(btnAdd);

		return searchPanel;
	}

	private JPanel createTablePanel() {
		var cardPanel = new JPanel(new BorderLayout());
		cardPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
		cardPanel.setBackground(BG_LIGHT);

		var headerLabel = new JLabel("DANH SÁCH NHÂN VIÊN");
		headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		headerLabel.setForeground(PRIMARY_BLUE);
		headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

		String[] columns = { "Mã NV", "Họ Tên", "Chức Vụ", "Số ĐT", "Email", "Ngày Sinh", "Giới Tính" };
		Object[][] data = {
				{1, "Nguyễn Văn A", "Phục vụ", "0123456789", "nva@example.com", "01/01/1990", "Nam"},
				{2, "Trần Thị B", "Thu ngân", "0987654321", "ttb@example.com", "15/05/1992", "Nữ"},
				{3, "Lê Văn C", "Đầu bếp", "0345678901", "lvc@example.com", "20/08/1988", "Nam"},
				{4, "Phạm Thị D", "Quản lý", "0765432109", "ptd@example.com", "10/03/1985", "Nữ"},
				{5, "Hoàng Văn E", "Bảo vệ", "0876543210", "hve@example.com", "25/12/1995", "Nam"}
		};

		model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableEmployee = new JTable(model);
		styleTable(tableEmployee);
		tableEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tableEmployeeMouseClicked(evt);
			}
		});

		var scrollPane = new JScrollPane(tableEmployee);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		scrollPane.getViewport().setBackground(CARD_WHITE);

		cardPanel.add(headerLabel, BorderLayout.NORTH);
		cardPanel.add(scrollPane, BorderLayout.CENTER);
		return cardPanel;
	}

	private JPanel createActionPanel() {
		var actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actionPanel.setBackground(BG_LIGHT);

		var btnPDF = createButton("Xuất PDF", TEAL, 120, 38);
		btnPDF.addActionListener(e -> printPDF());
		actionPanel.add(btnPDF);

		return actionPanel;
	}

	private void styleTable(JTable table) {
		var header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(PRIMARY_BLUE);
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(0, 40));

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setBackground(PRIMARY_BLUE);
				setForeground(Color.WHITE);
				setHorizontalAlignment(JLabel.CENTER);
				return this;
			}
		};
		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}

		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setGridColor(BORDER_COLOR);
		table.setSelectionBackground(new Color(227, 242, 253));
		table.setSelectionForeground(TEXT_PRIMARY);
	}

	private JButton createButton(String text, Color color, int width, int height) {
		var btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setForeground(Color.WHITE);
		btn.setBackground(color);
		btn.setPreferredSize(new Dimension(width, height));
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	// ===== Logic =====
	private void searchEmployee() {
		var key = txtSearch.getText().trim();
		if (key.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Đang tìm kiếm: " + key + "\n(Demo)", "Tìm Kiếm", JOptionPane.INFORMATION_MESSAGE);
	}

	private void addEmployee() {
		formPanel.setAddMode(true);
		formPanel.setVisible(true);
		btnAdd.setVisible(false);
		formPanel.focusNameField();
	}

	private void tableEmployeeMouseClicked(java.awt.event.MouseEvent evt) {
		var selectedRow = tableEmployee.getSelectedRow();
		if (selectedRow != -1) {
			formPanel.setEditMode(selectedRow, getRowData(selectedRow));
			formPanel.setVisible(true);
			btnAdd.setVisible(false);
			formPanel.focusNameField();
		}
	}

	private Object[] getRowData(int row) {
		var data = new Object[7];
		for (var i = 0; i < 7; i++) {
			data[i] = model.getValueAt(row, i);
		}
		return data;
	}

	private void handleFormSave(ActionEvent e) {
		var cmd = e.getActionCommand();
		var formData = formPanel.getFormData();

		if ("add".equals(cmd)) {
			var newId = model.getRowCount() + 1;
			formData[0] = newId;
			model.addRow(formData);
		} else if ("update".equals(cmd)) {
			var row = formPanel.getSelectedRowForEdit();
			if (row != -1) {
				for (var i = 1; i < formData.length; i++) {
					model.setValueAt(formData[i], row, i);
				}
			}
		}
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}

	private void handleFormCancel(ActionEvent e) {
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}

	private void printPDF() {
		try {
			tableEmployee.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("DANH SÁCH NHÂN VIÊN"), new java.text.MessageFormat("Trang {0,number,integer}"));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage());
		}
	}

	// ================= Inner Class EmployeeFormPanel =================
	private static class EmployeeFormPanel extends JPanel {
		private JTextField txtID, txtName, txtPhone, txtEmail, txtDOB;
		private JComboBox<String> cbxGender, cbxRole;
		private boolean isAddMode = true;
		private int selectedRowForEdit = -1;
		private ActionListener saveListener, cancelListener;

		public EmployeeFormPanel(ActionListener save, ActionListener cancel) {
			saveListener = save;
			cancelListener = cancel;
			initUI();
		}

		private void initUI() {
			setLayout(new BorderLayout());
			setBackground(Color.WHITE);
			setBorder(new EmptyBorder(20, 25, 20, 25));

			var form = new JPanel(new GridBagLayout());
			form.setBackground(Color.WHITE);
			var gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(8,5,8,15);

			gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Mã NV:"), gbc);
			gbc.gridx=1; txtID = new JTextField("Auto"); form.add(txtID, gbc);

			gbc.gridx=2; form.add(new JLabel("Họ và Tên:"), gbc);
			gbc.gridx=3; txtName = new JTextField(""); form.add(txtName, gbc);

			gbc.gridx=4; form.add(new JLabel("Số ĐT:"), gbc);
			gbc.gridx=5; txtPhone = new JTextField(""); form.add(txtPhone, gbc);

			gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Chức Vụ:"), gbc);
			gbc.gridx=1; cbxRole = new JComboBox<>(new String[]{"Phục vụ","Thu ngân","Đầu bếp","Quản lý","Bảo vệ"}); form.add(cbxRole, gbc);

			gbc.gridx=2; form.add(new JLabel("Email:"), gbc);
			gbc.gridx=3; txtEmail = new JTextField(""); form.add(txtEmail, gbc);

			gbc.gridx=4; form.add(new JLabel("Giới Tính:"), gbc);
			gbc.gridx=5; cbxGender = new JComboBox<>(new String[]{"Nam","Nữ","Khác"}); form.add(cbxGender, gbc);

			gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("Ngày Sinh:"), gbc);
			gbc.gridx=1; txtDOB = new JTextField("01/01/1990 (dd/MM/yyyy)"); form.add(txtDOB, gbc);

			add(form, BorderLayout.CENTER);

			var btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			var btnSave = new JButton("Lưu");
			var btnCancel = new JButton("Hủy");
			btnSave.addActionListener(this::handleSave);
			btnCancel.addActionListener(this::handleCancel);
			btnPanel.add(btnSave); btnPanel.add(btnCancel);
			add(btnPanel, BorderLayout.SOUTH);
		}

		public void setAddMode(boolean add) { isAddMode = add; if(add) {
			clearForm();
		} txtID.setText("Auto"); }
		public void setEditMode(int row, Object[] data) {
			isAddMode = false; selectedRowForEdit=row;
			txtID.setText(data[0].toString()); txtName.setText(data[1].toString());
			cbxRole.setSelectedItem(data[2]); txtPhone.setText(data[3].toString());
			txtEmail.setText(data[4].toString()); txtDOB.setText(data[5].toString());
			cbxGender.setSelectedItem(data[6]);
		}
		public Object[] getFormData() { return new Object[]{txtID.getText(), txtName.getText(), cbxRole.getSelectedItem(), txtPhone.getText(), txtEmail.getText(), txtDOB.getText(), cbxGender.getSelectedItem()}; }
		public int getSelectedRowForEdit(){ return selectedRowForEdit; }
		public void focusNameField(){ txtName.requestFocusInWindow(); }
		private void clearForm(){ txtName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtDOB.setText("01/01/1990 (dd/MM/yyyy)"); cbxRole.setSelectedIndex(0); cbxGender.setSelectedIndex(0); }
		private void handleSave(ActionEvent e){ if(saveListener!=null) {
			saveListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, isAddMode?"add":"update"));
		} }
		private void handleCancel(ActionEvent e){ if(cancelListener!=null) {
			cancelListener.actionPerformed(e);
		} }
	}
}
