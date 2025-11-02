package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.model.DayWorkStatus;
import com.example.swingapp.service.AttendanceService;
import com.example.swingapp.service.ShiftService;
import com.example.swingapp.service.WorkScheduleService;

public class AttendanceFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	private JPanel shiftListPanel;
	private JButton btnSave, btnCancel;
	private final AttendanceService service = new AttendanceService();

	private String currentEmployeeName;
	private String currentDate;

	private JButton btnAddShift, btnAddOT;
	private Runnable onDataChanged;

	private final WorkScheduleService workScheduleService = new WorkScheduleService();
	private int currentEmployeeId;

	public void setOnDataChanged(Runnable r) {
		onDataChanged = r;
	}

	public AttendanceFormPanel(ActionListener onSave, ActionListener onCancel) {
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		addPanel.setOpaque(false);

		btnAddShift = button("Thêm ca làm", PRIMARY_BLUE);
		btnAddShift.addActionListener(e -> onAddShift());

		btnAddOT = button("Thêm OT", PRIMARY_BLUE);
		btnAddOT.addActionListener(e -> onAddOT());

		addPanel.add(btnAddShift);
		addPanel.add(btnAddOT);

		shiftListPanel = new JPanel();
		shiftListPanel.setOpaque(false);
		shiftListPanel.setLayout(new BoxLayout(shiftListPanel, BoxLayout.Y_AXIS));
		shiftListPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

		var scroll = new JScrollPane(shiftListPanel);
		scroll.setBorder(null);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		actions.setOpaque(false);
		btnCancel = button("Hủy", DANGER_RED);
		btnSave = button("Lưu", PRIMARY_BLUE);
		btnSave.addActionListener(onSave);
		btnCancel.addActionListener(onCancel);
		actions.add(btnCancel);
		actions.add(btnSave);

		var centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(addPanel, BorderLayout.NORTH);
		centerPanel.add(scroll, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}

	// --- Separate logic methods (để WindowBuilder không lỗi lambda)
	private void onAddShift() {
		var shiftTypes = new ShiftService().getAll();
		showAddDialog("ca làm", shiftTypes,
				s -> s.getShiftName() + " (" + s.getStartTime() + " - " + s.getEndTime() + ")",
				selectedShift -> {
					try {
						var workSchedule = new com.example.swingapp.model.WorkSchedule();
						workSchedule.setEmployeeId(currentEmployeeId);
						workSchedule.setShiftId(selectedShift.getId());
						workSchedule.setWorkDate(java.sql.Date.valueOf(currentDate));
						var success = workScheduleService.add(workSchedule);
						if (success) {
							JOptionPane.showMessageDialog(this, "Thêm ca làm thành công!");
							var dateObj = java.time.LocalDate.parse(currentDate);
							service.clearCache(dateObj.getYear(), dateObj.getMonthValue());
							var newStatus = new DayWorkStatus(
									selectedShift.getShiftName() + " (" + selectedShift.getStartTime() + " - " + selectedShift.getEndTime() + ")", false);
							shiftListPanel.add(createShiftPanel(newStatus));
							shiftListPanel.add(Box.createVerticalStrut(10));
							shiftListPanel.revalidate();
							shiftListPanel.repaint();

							if (onDataChanged != null) {
								onDataChanged.run();
							}
						} else {
							JOptionPane.showMessageDialog(this, "Thêm ca làm thất bại!");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this, "Lỗi khi thêm ca làm!");
					}
				});
	}

	private void onAddOT() {
		var otTypes = new com.example.swingapp.service.OTTypeService().getAll();
		showAddDialog("OT", otTypes,
				ot -> ot.getOtName() + " (" + ot.getOtStart() + " - " + ot.getOtEnd() + ")",
				selectedOT -> {
					try {
						var otWork = new com.example.swingapp.model.WorkSchedule();
						otWork.setEmployeeId(currentEmployeeId);
						otWork.setId(null);
						otWork.setWorkDate(java.sql.Date.valueOf(currentDate));

						var workScheduleId = workScheduleService.addAndReturnId(otWork);
						if (workScheduleId == -1) {
							JOptionPane.showMessageDialog(this, "Tạo WorkSchedule cho OT thất bại!");
							return;
						}

						var otJunction = new com.example.swingapp.model.OTJunction();
						otJunction.setWorkScheduleId(workScheduleId);
						otJunction.setOtTypeId(selectedOT.getId());
						otJunction.setOtConfirm(false);

						var success = new com.example.swingapp.service.OTJunctionService().add(otJunction);
						if (success) {
							JOptionPane.showMessageDialog(this, "Thêm OT thành công!");
							shiftListPanel.add(Box.createVerticalStrut(10));
							shiftListPanel.add(createOTPanel(otJunction));
							shiftListPanel.revalidate();
							shiftListPanel.repaint();

							if (onDataChanged != null) {
								onDataChanged.run();
							}
						} else {
							JOptionPane.showMessageDialog(this, "Thêm OT thất bại!");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this, "Lỗi khi thêm OT!");
					}
				});
	}

	// ✅ Hiển thị danh sách ca làm
	public void showEmployeeSchedule(int employeeId, String employeeName, String date, List<DayWorkStatus> dayStatusList) {
		currentEmployeeId = employeeId;
		currentEmployeeName = employeeName;
		currentDate = date;

		shiftListPanel.removeAll();

		var headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		headerPanel.setOpaque(false);
		var header = new JLabel("Lịch làm của " + employeeName + " ngày " + date);
		header.setFont(new Font("Segoe UI", Font.BOLD, 14));
		header.setForeground(PRIMARY_BLUE);
		headerPanel.add(header);
		shiftListPanel.add(headerPanel);

		if (dayStatusList == null || dayStatusList.isEmpty()) {
			var empty = new JLabel("Không có ca làm trong ngày này.");
			empty.setForeground(Color.GRAY);
			empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
			shiftListPanel.add(empty);
		} else {
			for (DayWorkStatus s : dayStatusList) {
				shiftListPanel.add(createShiftPanel(s));
				shiftListPanel.add(Box.createVerticalStrut(10));
			}
		}

		var otService = new com.example.swingapp.service.OTJunctionService();
		var otList = otService.getByEmployeeAndDate(employeeId, date);
		for (com.example.swingapp.model.OTJunction ot : otList) {
			shiftListPanel.add(createOTPanel(ot));
			shiftListPanel.add(Box.createVerticalStrut(10));
		}

		shiftListPanel.revalidate();
		shiftListPanel.repaint();
	}

	// --- Panel hiển thị OT
	private JPanel createOTPanel(com.example.swingapp.model.OTJunction ot) {
		var p = new JPanel(new BorderLayout());
		p.setBackground(new Color(255, 250, 240));
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(16, 20, 16, 20)));

		var otType = new com.example.swingapp.service.OTTypeService().getById(ot.getOtTypeId());
		var otName = (otType != null) ? otType.getOtName() : "OT";
		var start = (otType != null && otType.getOtStart() != null) ? otType.getOtStart() : "--:--";
		var end = (otType != null && otType.getOtEnd() != null) ? otType.getOtEnd() : "--:--";

		var label = new JLabel("OT: " + otName + " (" + start + " - " + end + ")");
		label.setFont(new Font("Segoe UI", Font.BOLD, 13));
		label.setForeground(new Color(80, 80, 80));
		p.add(label, BorderLayout.CENTER);

		return p;
	}

	// --- Panel hiển thị ca làm
	private JPanel createShiftPanel(DayWorkStatus status) {
		var shiftName = status.getShiftName();
		var isPresent = status.isPresent();

		var p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(16, 20, 16, 20)));

		var l = new JLabel(shiftName + (isPresent ? " - X" : ""));
		l.setFont(new Font("Segoe UI", Font.BOLD, 13));
		l.setForeground(new Color(50, 50, 50));
		p.add(l, BorderLayout.CENTER);

		return p;
	}

	private String extractShiftName(String fullName) {
		var idx = fullName.indexOf(" (");
		return (idx > 0) ? fullName.substring(0, idx) : fullName;
	}

	private JButton button(String text, Color bg) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setPreferredSize(new Dimension(100, 36));
		return b;
	}

	// --- Generic helper
	private <T> void showAddDialog(String title, List<T> items, Function<T, String> displayFunc, Consumer<T> saveAction) {
		if (currentEmployeeId == 0 || currentDate == null) {
			JOptionPane.showMessageDialog(this, "Chưa chọn nhân viên hoặc ngày!");
			return;
		}

		if (items == null || items.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Chưa có mục nào để chọn!");
			return;
		}

		var displayArr = items.stream().map(displayFunc).toArray(String[]::new);
		var combo = new JComboBox<String>(displayArr);

		var panel = new JPanel();
		panel.add(new JLabel("Chọn " + title + ":"));
		panel.add(combo);

		var result = JOptionPane.showConfirmDialog(this, panel, "Thêm " + title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			var selectedIndex = combo.getSelectedIndex();
			var selectedItem = items.get(selectedIndex);
			saveAction.accept(selectedItem);

			shiftListPanel.removeAll();
			showEmployeeSchedule(currentEmployeeId, currentEmployeeName, currentDate,
					service.getDayWorkStatus(currentEmployeeName, currentDate));
		}
	}
}
