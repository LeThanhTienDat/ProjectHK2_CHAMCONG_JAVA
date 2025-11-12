package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.model.Employee;
import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.ContractService;
import com.example.swingapp.service.EmployeeService;
import com.example.swingapp.service.RestaurantService;

@SuppressWarnings("serial")
public class ContractFormPanel extends JPanel {
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	private JTextField txtStart, txtEnd, txtSalary;
	private JComboBox<String> cmbStatus;
	private JComboBox<Restaurant> cmbRestaurant;
	private JComboBox<Employee> cmbEmployee;
	private JComboBox<String> cmbPosition;
	private JButton btnSave, btnCancel;
	private boolean isInitializing = true;
	private int employeeId;
	private boolean addMode = true;
	private int editingRow = -1;
	private final ContractService contractService = new ContractService();

	public ContractFormPanel(ActionListener onSave, ActionListener onCancel) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var card = new JPanel();
		card.setLayout(new GridBagLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(20, 20, 15, 20)));

		txtStart = createField();
		txtEnd = createField();
		txtSalary = createField();
		cmbStatus = new JComboBox<>(new String[] { "Active", "Expired"});

		cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbStatus.setBackground(new Color(248, 250, 252));
		cmbStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)
				));
		cmbStatus.setPreferredSize(new Dimension(0, 36));

		var gc = new GridBagConstraints();
		gc.insets = new Insets(8, 8, 8, 8);
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;

		cmbRestaurant = new JComboBox<Restaurant>();
		cmbRestaurant.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbRestaurant.setBackground(new Color(248, 250, 252));
		cmbRestaurant.addActionListener(e -> onRestaurantSelected());
		cmbRestaurant.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		renderRestaurant();
		var gbc_cmbRestaurant = new GridBagConstraints();
		gbc_cmbRestaurant.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbRestaurant.insets = new Insets(0, 0, 15, 0);
		gbc_cmbRestaurant.gridx = 1;
		gbc_cmbRestaurant.gridy = 3;


		cmbEmployee = new JComboBox<Employee>();
		cmbEmployee.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbEmployee.setBackground(new Color(248, 250, 252));
		cmbEmployee.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));

		cmbEmployee.setRenderer(new EmployeeListRenderer());
		var gbc_cmbEmployee = new GridBagConstraints();
		gbc_cmbEmployee.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbEmployee.insets = new Insets(0, 0, 15, 0);
		gbc_cmbEmployee.gridx = 1;
		gbc_cmbEmployee.gridy = 3;

		String[] positionList = {"Employee", "Supervisor", "Manager"};
		cmbPosition = new JComboBox<>(positionList);
		cmbPosition.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cmbPosition.setBackground(new Color(248, 250, 252));
		cmbPosition.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		var gbc_cmbPosition = new GridBagConstraints();
		gbc_cmbPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbPosition.insets = new Insets(0, 0, 15, 0);
		gbc_cmbPosition.gridx = 1;
		gbc_cmbPosition.gridy = 3;


		addField(card, gc, 0, "Restaurant Name", cmbRestaurant);
		addField(card, gc, 1, "Employee Name",cmbEmployee);
		addField(card, gc, 2, "Position", cmbPosition);
		addField(card, gc, 3, "Start Date (dd/MM/yyyy)", txtStart);
		addField(card, gc, 4, "End Date (dd/MM/yyyy)", txtEnd);
		addField(card, gc, 5, "Basic Salary", txtSalary);
		addField(card, gc, 6, "Contract Status", cmbStatus);

		var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnCancel = createButton("Cancel", DANGER_RED);
		btnSave = createButton("Add", PRIMARY_BLUE);

		btnSave.addActionListener(e -> {
			try {
				var selectedEmployee = (Employee) cmbEmployee.getSelectedItem();
				if (selectedEmployee == null) {
					JOptionPane.showMessageDialog(this, "Please select an employee!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				var cmd = addMode ? "add" : "update";
				var event = new ActionEvent(
						btnSave,
						ActionEvent.ACTION_PERFORMED,
						cmd
						);

				System.out.println("[DEBUG] addMode = " + addMode);
				System.out.println("[DEBUG] ActionCommand = " + cmd);
				onSave.actionPerformed(event);

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"Error while processing Save button: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		btnCancel.addActionListener(onCancel);

		actions.add(btnCancel);
		actions.add(btnSave);

		add(card, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}


	private void addField(JPanel p, GridBagConstraints gc, int row, String title, JComponent field) {
		var labelConstraints = (GridBagConstraints) gc.clone();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = row;
		labelConstraints.weightx = 0.3;

		var lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		p.add(lb, labelConstraints);

		var fieldConstraints = (GridBagConstraints) gc.clone();
		fieldConstraints.gridx = 1;
		fieldConstraints.gridy = row;
		fieldConstraints.weightx = 0.7;

		p.add(field, fieldConstraints);
	}

	private JTextField createField() {
		var f = new JTextField();
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		return f;
	}

	private static JButton createButton(String text, Color bg) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(bg.darker(), 1, true),
				new EmptyBorder(5, 10, 5, 10)));
		b.setPreferredSize(new Dimension(100, 36));
		return b;
	}

	public void setAddMode(boolean add) {
		addMode = add;
		editingRow = -1;
		cmbPosition.setSelectedIndex(-1);
		txtStart.setText("");
		txtEnd.setText("");
		txtSalary.setText("");
		cmbStatus.setSelectedIndex(0);
		btnSave.setText(addMode ? "Add" : "Save");
	}

	public void setEditMode(int row, Object[] data) {
		addMode = false;
		editingRow = row;
		btnSave.setText("Save");

		var restaurantName = (String) data[7];
		for (var i = 0; i < cmbRestaurant.getItemCount(); i++) {
			var r = cmbRestaurant.getItemAt(i);
			if (r.getName().equals(restaurantName)) {
				cmbRestaurant.setSelectedIndex(i);
				break;
			}
		}

		var selectedRestaurant = (Restaurant) cmbRestaurant.getSelectedItem();
		if (selectedRestaurant != null) {
			try {
				var employeeService = new EmployeeService();
				var employees = employeeService.getByRestaurantId(selectedRestaurant.getId());
				cmbEmployee.removeAllItems();
				for (Employee emp : employees) {
					cmbEmployee.addItem(emp);
				}

				var employeeName = String.valueOf(data[1]);
				for (var i = 0; i < cmbEmployee.getItemCount(); i++) {
					var emp = cmbEmployee.getItemAt(i);
					if (emp.getName().equals(employeeName)) {
						cmbEmployee.setSelectedIndex(i);
						break;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"Error loading employees for restaurant: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		txtStart.setText(String.valueOf(data[2]));
		txtEnd.setText(String.valueOf(data[3]));
		txtSalary.setText(String.valueOf(data[5]));
		cmbPosition.setSelectedItem(mapRoleDisplay(String.valueOf(data[4])));
		cmbStatus.setSelectedItem(String.valueOf(data[6]));
	}

	public Object[] getFormData() {
		var selectedRestaurant = (Restaurant) cmbRestaurant.getSelectedItem();
		if (selectedRestaurant == null) {
			JOptionPane.showMessageDialog(this, "Please select an Restaurant!", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		var selectedEmployee = (Employee) cmbEmployee.getSelectedItem();
		if (selectedEmployee == null) {
			JOptionPane.showMessageDialog(this, "Please select an employee!", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		var rawStartTimeText = txtStart.getText().trim();
		var rawEndTimeText = txtEnd.getText().trim();
		var salaryTextRaw = txtSalary.getText().trim();
		var salaryText = salaryTextRaw.replace(",", "").replace(" ", "");
		if (rawStartTimeText.isEmpty() || rawEndTimeText.isEmpty() || salaryTextRaw.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Date and Salary fields cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		java.util.Date startDateUtil = null;
		java.util.Date endDateUtil = null;
		java.sql.Date startDateSql = null;
		java.sql.Date endDateSql = null;
		var DATE_FORMAT = "dd/MM/yyyy";
		var rawDate = new SimpleDateFormat(DATE_FORMAT);
		rawDate.setLenient(false);
		try {
			// Kiểm tra định dạng và tính hợp lệ của ngày tháng (ví dụ: 30/02/2025)
			startDateUtil = rawDate.parse(rawStartTimeText);
			endDateUtil = rawDate.parse(rawEndTimeText);

			// Kiểm tra Ngày kết thúc sau Ngày bắt đầu
			if (endDateUtil.before(startDateUtil)) {
				JOptionPane.showMessageDialog(this, "End Date cannot be before Start Date!", "Input Error", JOptionPane.ERROR_MESSAGE);
				txtEnd.requestFocusInWindow();
				return null;
			}

			startDateSql = new java.sql.Date(startDateUtil.getTime());
			endDateSql = new java.sql.Date(endDateUtil.getTime());

		} catch (java.text.ParseException ex) {
			JOptionPane.showMessageDialog(this,
					"Invalid date format! Please enter date in " + DATE_FORMAT + " format.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			txtStart.requestFocusInWindow();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An unexpected error occurred during date validation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}


		try {
			var salary = Double.parseDouble(salaryText);
			if (salary <= 0) {
				JOptionPane.showMessageDialog(this, "Salary must be a positive number!", "Input Error", JOptionPane.ERROR_MESSAGE);
				txtSalary.requestFocusInWindow();
				return null;
			}
			var position = mapRoleCode((String) cmbPosition.getSelectedItem());

			return new Object[] {
					null,
					selectedEmployee.getId(),
					startDateSql,
					endDateSql,
					salary,
					position,
					cmbStatus.getSelectedItem().toString(),
					cmbRestaurant.getSelectedItem()
			};
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Please enter the correct date format (dd/MM/yyyy) and salary as a number!",
					"Data Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public int getEditingRow() {
		return editingRow;
	}

	public void focusFirst() {
		cmbRestaurant.requestFocusInWindow();
	}

	private void renderRestaurant() {

		try {
			var restaurantService = new RestaurantService();
			var restaurants = restaurantService.getAll();
			cmbRestaurant.removeAllItems();

			for (Restaurant r : restaurants) {
				cmbRestaurant.addItem(r);
			}
			cmbRestaurant.setSelectedIndex(-1);
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
		var selectedRestaurant = (Restaurant) cmbRestaurant.getSelectedItem();
		if (selectedRestaurant == null) {
			return;
		}

		try {
			var employeeService = new EmployeeService();
			var employees = employeeService.getByRestaurantId(selectedRestaurant.getId());

			cmbEmployee.removeAllItems();
			for (Employee emp : employees) {
				cmbEmployee.addItem(emp);
			}

			cmbEmployee.setSelectedIndex(-1);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					this,
					"Error loading employees for this restaurant: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE
					);
		}
	}
	public String mapRoleDisplay(String roleCode) {
		return switch (roleCode) {
		case "quanly" -> "Manager";
		case "giamsat" -> "Supervisor";
		case "nhanvien" -> "Employee";
		default -> roleCode;
		};
	}

	public String mapRoleCode(String displayName) {
		return switch (displayName) {
		case "Manager" -> "quanly";
		case "Supervisor" -> "giamsat";
		case "Employee" -> "nhanvien";
		default -> displayName.toLowerCase();
		};
	}

	/**
	 * Renderer tùy chỉnh để hiển thị tên nhân viên và tô màu nhân viên đã có HĐ.
	 */
	private class EmployeeListRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(
				JList<?> list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (value instanceof Employee employee) {
				setText(employee.getName());

				try {
					// 🔥 LOGIC KIỂM TRA TRẠNG THÁI HỢP ĐỒNG
					var hasActiveContract = contractService.hasActiveContract(employee.getId());

					if (hasActiveContract) {
						var disabledTextColor = new Color(150, 150, 150);

						if (!isSelected) {
							setForeground(disabledTextColor);
						} else {
							setBackground(list.getSelectionBackground());
							setForeground(list.getSelectionForeground());
						}
					} else {
						if (!isSelected) {
							setBackground(list.getBackground());
							setForeground(list.getForeground());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return this;
		}
	}
}
