package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.model.Street;
import com.example.swingapp.service.StreetService;

public class RestaurantFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	// Components
	private JTextField txtID;
	private JTextField txtName;
	private JComboBox<Street> cmbStreet;
	private JButton btnSave;
	private JButton btnCancel;
	private int streetId;
	private ActionListener saveListener;
	private ActionListener cancelListener;
	private boolean isAddMode = true;
	private int selectedRowForEdit = -1;

	public RestaurantFormPanel(ActionListener saveCallback, ActionListener cancelCallback) {
		saveListener = saveCallback;
		cancelListener = cancelCallback;

		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(10, 25, 10, 25));
		setLayout(new BorderLayout(0, 0));

		var contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		add(contentPanel, BorderLayout.CENTER);
		var gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 100, 300, 0 };
		gbl_contentPanel.rowHeights = new int[] { 40, 40, 40, 50, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.3, 0.7, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		var lblHeader = new JLabel("THÔNG TIN NHÀ HÀNG");
		lblHeader.setForeground(new Color(25, 118, 210));
		lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
		var gbc_lblHeader = new GridBagConstraints();
		gbc_lblHeader.gridwidth = 2;
		gbc_lblHeader.insets = new Insets(0, 0, 15, 0);
		gbc_lblHeader.gridx = 0;
		gbc_lblHeader.gridy = 0;
		contentPanel.add(lblHeader, gbc_lblHeader);

		var lblId = new JLabel("ID:");
		lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.WEST;
		gbc_lblId.insets = new Insets(0, 0, 10, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 1;
		contentPanel.add(lblId, gbc_lblId);

		txtID = new JTextField();
		applyFieldStyle(txtID);
		txtID.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtID.setText("Auto");
		txtID.setEditable(false);
		var gbc_txtId = new GridBagConstraints();
		gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtId.insets = new Insets(0, 0, 10, 0);
		gbc_txtId.gridx = 1;
		gbc_txtId.gridy = 1;
		contentPanel.add(txtID, gbc_txtId);
		txtID.setColumns(10);

		var lblName = new JLabel("Tên Nhà Hàng:");
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 10, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 2;
		contentPanel.add(lblName, gbc_lblName);

		txtName = new JTextField();
		applyFieldStyle(txtName);
		txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		var gbc_txtName = new GridBagConstraints();
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.insets = new Insets(0, 0, 10, 0);
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 2;
		contentPanel.add(txtName, gbc_txtName);
		txtName.setColumns(10);

		var lblStreet = new JLabel("Tên Đường:");
		lblStreet.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblStreet = new GridBagConstraints();
		gbc_lblStreet.anchor = GridBagConstraints.WEST;
		gbc_lblStreet.insets = new Insets(0, 0, 15, 5);
		gbc_lblStreet.gridx = 0;
		gbc_lblStreet.gridy = 3;
		contentPanel.add(lblStreet, gbc_lblStreet);




		cmbStreet = new JComboBox<Street>();
		cmbStreet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbStreet.setBackground(new Color(248, 250, 252));
		cmbStreet.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		renderStreet();

		var gbc_cmbStreet = new GridBagConstraints();
		gbc_cmbStreet.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbStreet.insets = new Insets(0, 0, 15, 0);
		gbc_cmbStreet.gridx = 1;
		gbc_cmbStreet.gridy = 3;
		contentPanel.add(cmbStreet, gbc_cmbStreet);

		var buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

		btnSave = new JButton("Lưu");
		btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnSave.setBackground(new Color(76, 175, 80));
		btnSave.setForeground(Color.WHITE);
		btnSave.setFocusPainted(false);
		btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSave.setPreferredSize(new Dimension(100, 35));
		btnSave.setBorderPainted(false);
		btnSave.addActionListener(this::handleSave);
		buttonPanel.add(btnSave);

		btnCancel = new JButton("Hủy");
		btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnCancel.setBackground(new Color(244, 67, 54));
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setFocusPainted(false);
		btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCancel.setPreferredSize(new Dimension(100, 35));
		btnCancel.setBorderPainted(false);
		btnCancel.addActionListener(this::handleCancel);
		buttonPanel.add(btnCancel);
	}

	// ==== Business logic ====

	public void setAddMode(boolean addMode) {
		isAddMode = addMode;
		if (addMode) {
			txtID.setText("Auto");
			txtID.setEditable(false);
			clearForm();
		}
	}
	private void applyFieldStyle(JTextField f) {
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
	}

	public void setEditMode(int rowIndex, Object[] data) {
		isAddMode = false;
		selectedRowForEdit = rowIndex;
		txtID.setText(data[0].toString());
		txtName.setText(data[1].toString());
		var streetId = (int) data[5];
		for (var i = 0; i < cmbStreet.getItemCount(); i++) {
			var s = cmbStreet.getItemAt(i);
			if (s.getId() == streetId) {
				cmbStreet.setSelectedIndex(i);
				break;
			}
		}
		txtID.setEditable(false);
	}

	public boolean isAddMode() {
		return isAddMode;
	}

	public int getSelectedRowForEdit() {
		return selectedRowForEdit;
	}

	public Object[] getFormData() {
		return new Object[] {
				txtID.getText(),
				txtName.getText(),
				streetId
		};
	}

	public void focusNameField() {
		txtName.requestFocusInWindow();
	}

	private void clearForm() {
		txtName.setText("");
		cmbStreet.setSelectedIndex(-1);
	}

	private void handleSave(ActionEvent e) {
		if (txtName.getText().trim().isEmpty() || cmbStreet.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng nhập đầy đủ thông tin bắt buộc!",
					"Cảnh Báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var selectedStreet = (Street) cmbStreet.getSelectedItem();
		streetId = selectedStreet.getId();

		if (saveListener != null) {
			saveListener.actionPerformed(
					new ActionEvent(this, ActionEvent.ACTION_PERFORMED, isAddMode ? "add" : "update"));
		}
	}

	private void handleCancel(ActionEvent e) {
		if (cancelListener != null) {
			cancelListener.actionPerformed(e);
		}
	}
	private void renderStreet() {

		try {
			var streetService = new StreetService();
			var streets = streetService.getAll();
			cmbStreet.removeAllItems();

			for (Street s : streets) {
				cmbStreet.addItem(s);
			}
			cmbStreet.setSelectedIndex(-1);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Lỗi tải danh sách tên đường: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

}
