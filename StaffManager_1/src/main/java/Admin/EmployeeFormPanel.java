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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.model.Employee;
import com.example.swingapp.util.DBConnection;

public class EmployeeFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField txtID;
	private JTextField txtName;
	private JTextField txtDOB;
	private JComboBox<String> cmbRole;
	private JTextField txtEmail;
	private JComboBox<String> cmbGender;
	private JComboBox<String> cmbActive;
	private JTextField txtPhone;
	private JComboBox<String> cmbRestaurant;
	private JButton btnSave, btnCancel;

	private ActionListener saveListener, cancelListener;
	private boolean isAddMode = true;
	private int editingEmployeeId = -1;

	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	public EmployeeFormPanel(ActionListener save, ActionListener cancel) {
		saveListener = save;
		cancelListener = cancel;
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 25, 20, 25));
		setLayout(new BorderLayout());

		// Header
		var lblHeader = new JLabel("EMPLOYEE INFORMATION");
		lblHeader.setForeground(PRIMARY_BLUE);
		lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
		add(lblHeader, BorderLayout.NORTH);

		var form = new JPanel(new GridBagLayout());
		form.setBackground(Color.WHITE);
		var gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		var row = 0;

		// --- Row 0 ---
		gbc.gridy = row;

		gbc.gridx = 0; gbc.weightx = 0; // label không giãn
		form.add(label("Employee ID:"), gbc);
		txtID = field("Auto", false);
		gbc.gridx = 1; gbc.weightx = 1.0; // field giãn
		form.add(txtID, gbc);

		gbc.gridx = 2; gbc.weightx = 0; // spacer
		form.add(Box.createHorizontalStrut(40), gbc);

		gbc.gridx = 3; gbc.weightx = 0; // label không giãn
		form.add(label("Full name:"), gbc);
		txtName = field("", true);
		gbc.gridx = 4; gbc.weightx = 1.0; // field giãn
		form.add(txtName, gbc);

		row++;

		// --- Row 1 ---
		gbc.gridy = row;

		gbc.gridx = 0; gbc.weightx = 0;
		form.add(label("Date of Birth (dd/MM/YYYY):"), gbc);
		txtDOB = field("01/01/1990", true);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form.add(txtDOB, gbc);

		gbc.gridx = 2; gbc.weightx = 0;

		gbc.gridx = 3; gbc.weightx = 0;
		form.add(label("Position:"), gbc);
		cmbRole = combo(new String[] {"Employee", "Supervisor", "Manager"});
		gbc.gridx = 4; gbc.weightx = 1.0;
		form.add(cmbRole, gbc);

		row++;

		// --- Row 2 ---
		gbc.gridy = row;

		gbc.gridx = 0; gbc.weightx = 0;
		form.add(label("Email:"), gbc);
		txtEmail = field("0", true);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form.add(txtEmail, gbc);

		gbc.gridx = 2; gbc.weightx = 0;

		gbc.gridx = 3; gbc.weightx = 0;
		form.add(label("Gender:"), gbc);
		cmbGender = combo(new String[] {"Male","Female"});
		gbc.gridx = 4; gbc.weightx = 1.0;
		form.add(cmbGender, gbc);

		row++;

		// --- Row 3 ---
		gbc.gridy = row;

		gbc.gridx = 0; gbc.weightx = 0;
		form.add(label("Phone:"), gbc);
		txtPhone = field("", true);
		gbc.gridx = 1; gbc.weightx = 1.0;
		form.add(txtPhone, gbc);

		gbc.gridx = 2; gbc.weightx = 0;

		gbc.gridx = 3; gbc.weightx = 0;
		form.add(label("Restaurant:"), gbc);
		cmbRestaurant = combo(new String[] {});
		loadRestaurants();
		gbc.gridx = 4; gbc.weightx = 1.0;
		form.add(cmbRestaurant, gbc);

		// --- Row 4 ---
		row++;
		gbc.gridy = row;

		gbc.gridx = 0; gbc.weightx = 0;
		form.add(label("Status:"), gbc);
		cmbActive = combo(new String[] {"Active", "Inactive"});
		gbc.gridx = 1; gbc.weightx = 1.0;
		form.add(cmbActive, gbc);

		add(form, BorderLayout.CENTER);

		// Buttons
		var btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnPanel.setBackground(Color.WHITE);
		btnSave = button("Save", SUCCESS_GREEN, this::handleSave);
		btnCancel = button("Cancel", DANGER_RED, this::handleCancel);
		btnPanel.add(btnCancel);
		btnPanel.add(btnSave);
		add(btnPanel, BorderLayout.SOUTH);
	}

	public static JLabel label(String text) {
		var lbl = new JLabel(text);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
		return lbl;
	}

	public static JTextField field(String text, boolean editable) {
		var f = new JTextField(text);
		f.setEditable(editable);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248,250,252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8,12,8,12)));
		f.setPreferredSize(new Dimension(250,38));
		return f;
	}

	public static JComboBox<String> combo(String[] items) {
		var cb = new JComboBox<String>(items);
		cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cb.setBackground(new Color(248,250,252));
		cb.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR,1,true),
				new EmptyBorder(8,12,8,12)));
		cb.setPreferredSize(new Dimension(150,38));
		return cb;
	}

	public static JButton button(String text, Color color, ActionListener act) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setBackground(color);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setPreferredSize(new Dimension(100,38));
		b.setBorderPainted(false);
		b.addActionListener(act);
		return b;
	}

	public void loadRestaurants() {
		try {
			cmbRestaurant.removeAllItems();
			var conn = DBConnection.getConnection();
			var stmt = conn.prepareStatement("SELECT name FROM tbl_Restaurant ORDER BY name");
			var rs = stmt.executeQuery();
			while(rs.next()) {
				cmbRestaurant.addItem(rs.getString("name"));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setAddMode(boolean addMode) {
		isAddMode = addMode;
		editingEmployeeId = -1;
		txtID.setText("Auto");
		txtID.setEditable(false);
		clearForm();
	}

	public void clearForm() {
		txtName.setText("");
		txtDOB.setText("01/01/1990");
		txtEmail.setText("0");
		txtPhone.setText("");
		cmbRole.setSelectedIndex(0);
		cmbGender.setSelectedIndex(0);
		cmbRestaurant.setSelectedIndex(-1);
	}

	public void handleSave(ActionEvent e) {
		var dobText = txtDOB.getText().trim();
		Date dob = null;
		try {
			dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobText);

			var today = new Date();
			if (dob.after(today)) {
				throw new Exception("Date of birth cannot be later than today.");
			}
		} catch (Exception ex) {
			javax.swing.JOptionPane.showMessageDialog(
					this,
					"Invalid date of birth! Please enter in dd/MM/yyyy format.",
					"Input Error",
					javax.swing.JOptionPane.ERROR_MESSAGE
					);
			txtDOB.requestFocus();
			return;
		}
		var emp = new Employee();
		emp.setName(txtName.getText());
		emp.setPhone(txtPhone.getText());
		try { emp.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(txtDOB.getText())); }
		catch(Exception ex) { emp.setDob(new Date()); }
		emp.setRole(getRoleCode((String)cmbRole.getSelectedItem()));
		emp.setGender(getGenderCode((String)cmbGender.getSelectedItem()));

		var activeStatus = (String) cmbActive.getSelectedItem();
		var activeVal = "Active".equals(activeStatus) ? 1 : 0;
		emp.setActive(activeVal);

		if(saveListener != null) {
			saveListener.actionPerformed(
					new ActionEvent(new Object[]{emp, cmbRestaurant.getSelectedItem()}, ActionEvent.ACTION_PERFORMED, isAddMode ? "add":"update")
					);
		}
	}

	public void handleCancel(ActionEvent e) {
		if(cancelListener != null) {
			cancelListener.actionPerformed(e);
		}
	}
	public void setEditMode(int row, Object[] rowData) {
		try {
			editingEmployeeId = Integer.parseInt(rowData[0].toString().replaceAll("[^0-9]", ""));
		} catch (Exception e) {
			editingEmployeeId = -1;
		}

		txtName.setText(rowData[1] != null ? rowData[1].toString() : "");

		try {
			Date dob = null;
			if (rowData[3] instanceof Date) {
				dob = (Date) rowData[3];
			} else if (rowData[3] instanceof String) {
				dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) rowData[3]);
			}
			txtDOB.setText(dob != null ? new SimpleDateFormat("dd/MM/yyyy").format(dob) : "01/01/1990");
		} catch (Exception ex) {
			txtDOB.setText("01/01/1990");
		}

		var dbRole = rowData[4] != null ? rowData[4].toString() : "";
		var roleDisplay = getDisplayRole(dbRole);
		if (!comboContains(cmbRole, roleDisplay) && !roleDisplay.isEmpty()) {
			cmbRole.addItem(roleDisplay);
		}
		cmbRole.setSelectedItem(roleDisplay);

		txtEmail.setText(rowData[10] != null ? rowData[10].toString() : "0");

		// --- Giới tính ---
		var gender = rowData[2] != null ? rowData[2].toString() : "";
		var genderDisplay = mapGenderDisplay(gender);
		if (!comboContains(cmbGender, genderDisplay) && !genderDisplay.isEmpty()) {
			cmbGender.addItem(genderDisplay);
		}
		cmbGender.setSelectedItem(genderDisplay);

		// --- Điện thoại ---
		txtPhone.setText(rowData[9] != null ? rowData[9].toString() : "");

		// --- Nhà hàng ---
		var restaurant = rowData[10] != null ? rowData[10].toString() : "";
		if (!comboContains(cmbRestaurant, restaurant) && !restaurant.isEmpty()) {
			cmbRestaurant.addItem(restaurant);
		}
		cmbRestaurant.setSelectedItem(restaurant);

		// --- Active ---
		var activeStr = "";
		try {
			var activeObj = rowData[8];
			if (activeObj != null) {
				var activeVal = 0;
				if (activeObj instanceof Number) {
					activeVal = ((Number) activeObj).intValue();
				} else {
					activeVal = Integer.parseInt(activeObj.toString());
				}
				activeStr = (activeVal == 1) ? "Active" : "Inactive";
			}
		} catch (Exception ex) {
			activeStr = "Active";
		}
		cmbActive.setSelectedItem(activeStr);

		// --- Chế độ ---
		isAddMode = false;
	}
	public static boolean comboContains(JComboBox<String> cb, String item) {
		for (var i = 0; i < cb.getItemCount(); i++) {
			if (cb.getItemAt(i).equals(item)) {
				return true;
			}
		}
		return false;
	}

	public Employee getEmployeeData() {
		var emp = new Employee();
		emp.setName(txtName.getText().trim());
		try {
			var utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(txtDOB.getText().trim());
			emp.setDob(new java.sql.Date(utilDate.getTime()));
		} catch (Exception ex) {
			emp.setDob(new java.sql.Date(System.currentTimeMillis()));
		}
		emp.setRole(getRoleCode((String) cmbRole.getSelectedItem()));
		emp.setEmail(txtEmail.getText().trim());
		emp.setGender(getGenderCode((String)cmbGender.getSelectedItem()));
		emp.setPhone(txtPhone.getText().trim());

		return emp;
	}
	public void focusNameField() {
		if (txtName != null) {
			txtName.requestFocus();
		}
	}

	public int getSelectedRestaurantId() {
		var restaurantId = 0;
		var selectedName = (String) cmbRestaurant.getSelectedItem();

		if (selectedName == null || selectedName.isEmpty()) {
			return 0;
		}

		try (var conn = DBConnection.getConnection();
				var pst = conn.prepareStatement("SELECT id FROM tbl_Restaurant WHERE name = ?")) {
			pst.setString(1, selectedName);
			try (var rs = pst.executeQuery()) {
				if (rs.next()) {
					restaurantId = rs.getInt("id");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return restaurantId;
	}


	public boolean isEditMode() {
		return !isAddMode;
	}

	public int getEditingEmployeeId() {
		return editingEmployeeId;
	}

	public String getDisplayRole(String roleCode) {
		if (roleCode == null) {
			return "Employee";
		}
		return switch (roleCode.toLowerCase()) {
		case "giamsat" -> "Supervisor";
		case "quanly" -> "Manager";
		default -> "Employee";
		};
	}

	public String getRoleCode(String displayName) {
		if (displayName == null) {
			return "nhanvien";
		}
		return switch (displayName) {
		case "Supervisor" -> "giamsat";
		case "Manager" -> "quanly";
		default -> "nhanvien";
		};
	}
	private String getGenderCode(String display) {
		if ("Male".equalsIgnoreCase(display)) {
			return "nam";
		}
		if ("Female".equalsIgnoreCase(display)) {
			return "nu";
		}
		return null;
	}
	private String mapGenderDisplay(String genderCode) {
		if ("nam".equalsIgnoreCase(genderCode)) {
			return "Male";
		}
		if ("nu".equalsIgnoreCase(genderCode)) {
			return "Female";
		}
		return "";
	}


}
