package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.example.swingapp.dao.WorkScheduleDAO;
import com.example.swingapp.model.DayWorkStatus;
import com.example.swingapp.model.OTJunction;
import com.example.swingapp.model.Shift;
import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.service.AttendanceService;
import com.example.swingapp.service.OTJunctionService;
import com.example.swingapp.service.OTTypeService;
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
	private final OTJunctionService otJunctionService = new OTJunctionService();
	private final OTTypeService otTypeService = new OTTypeService();
	private int currentEmployeeId;

	public void setOnDataChanged(Runnable r) {
		onDataChanged = r;
	}
	public AttendanceFormPanel(ActionListener onSave, ActionListener onCancel) {
		initUI(onSave, onCancel);
	}

	public void initUI(ActionListener onSave, ActionListener onCancel) {
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		addPanel.setOpaque(false);

		btnAddShift = createButton("Add shift", PRIMARY_BLUE, 110);
		btnAddShift.addActionListener(e -> onAddShift());

		btnAddOT = createButton("Add OT", PRIMARY_BLUE, 110);
		btnAddOT.addActionListener(e -> onAddOT());

		addPanel.add(btnAddShift);
		addPanel.add(btnAddOT);

		shiftListPanel = new JPanel();
		shiftListPanel.setOpaque(false);
		shiftListPanel.setLayout(new BoxLayout(shiftListPanel, BoxLayout.Y_AXIS));
		shiftListPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

		var wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(shiftListPanel, BorderLayout.NORTH);

		var scroll = new JScrollPane(wrapper);
		scroll.setBorder(null);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);


		var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		actions.setOpaque(false);
		btnCancel = createButton("Close", DANGER_RED,110);
		btnSave = createButton("Save", PRIMARY_BLUE,110);
		btnSave.addActionListener(onSave);
		btnCancel.addActionListener(onCancel);
		actions.add(btnCancel);

		var centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(addPanel, BorderLayout.NORTH);
		centerPanel.add(scroll, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}

	public void onAddShift() {
		var checkWorkSchedule = service.checkWorkScheduleId(currentEmployeeId, currentDate);
		var checkShift = service.checkShiftId(currentEmployeeId, currentDate);
		var shiftTypes = new ShiftService().getAll();

		if(checkWorkSchedule > 0 && checkShift >0) {
			JOptionPane.showMessageDialog(this, "Shift already exists!");
			return;
		}else if(checkWorkSchedule > 0 && checkShift == 0){
			var currentOtList = otTypeService.getAllByWorkScheduleId(checkWorkSchedule);
			if (currentOtList != null && !currentOtList.isEmpty()) {
				shiftTypes.removeIf(shift -> {
					var shiftStart = shift.getStartTime().toLocalTime();
					var shiftEnd = shift.getEndTime().toLocalTime();

					for (var ot : currentOtList) {
						var otStart = ot.getOtStart().toLocalTime();
						var otEnd = ot.getOtEnd().toLocalTime();
						var overlap = shiftStart.isBefore(otEnd) && shiftEnd.isAfter(otStart);
						if (overlap) {
							return true;
						}
					}

					return false;
				});
			}
			showAddDialog("Shift ", shiftTypes,
					s -> s.getShiftName() + " (" + s.getStartTime() + " - " + s.getEndTime() + ")",
					selectedShift -> {
						try {
							var success = workScheduleService.addShift(checkWorkSchedule, selectedShift.getId());
							if (success) {
								JOptionPane.showMessageDialog(this, "Add shift successfully!");
								var dateObj = java.time.LocalDate.parse(currentDate);
								service.clearCache(dateObj.getYear(), dateObj.getMonthValue());
								var newStatus = new DayWorkStatus(
										selectedShift.getShiftName() + " (" + selectedShift.getStartTime() + " - " + selectedShift.getEndTime() + ")", false);
								var workScheduleInfo = service.getWorkSheduleByIdDate(currentEmployeeId, currentDate);
								shiftListPanel.add(Box.createVerticalStrut(10), 1);
								shiftListPanel.setAlignmentY(TOP_ALIGNMENT);
								shiftListPanel.add(createShiftPanel(newStatus, workScheduleInfo), 1);
								shiftListPanel.revalidate();
								shiftListPanel.repaint();

								if (onDataChanged != null) {
									onDataChanged.run();
								}
							} else {
								JOptionPane.showMessageDialog(this, "Add shift failed!");
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(this, "Error adding shift!");
						}
					});
		}else {
			var currentOtList = otTypeService.getAllByWorkScheduleId(checkWorkSchedule);
			if (currentOtList != null && !currentOtList.isEmpty()) {
				shiftTypes.removeIf(shift -> {
					var shiftStart = shift.getStartTime().toLocalTime();
					var shiftEnd = shift.getEndTime().toLocalTime();

					for (var ot : currentOtList) {
						var otStart = ot.getOtStart().toLocalTime();
						var otEnd = ot.getOtEnd().toLocalTime();
						var overlap = shiftStart.isBefore(otEnd) && shiftEnd.isAfter(otStart);
						if (overlap) {
							return true;
						}
					}

					return false;
				});
			}
			showAddDialog("Shift ", shiftTypes,
					s -> s.getShiftName() + " (" + s.getStartTime() + " - " + s.getEndTime() + ")",
					selectedShift -> {
						try {
							var workSchedule = new com.example.swingapp.model.WorkSchedule();
							workSchedule.setEmployeeId(currentEmployeeId);
							workSchedule.setShiftId(selectedShift.getId());
							workSchedule.setWorkDate(java.sql.Date.valueOf(currentDate));
							var success = workScheduleService.add(workSchedule);
							if (success) {
								JOptionPane.showMessageDialog(this, "Add shift successfully!");
								var dateObj = java.time.LocalDate.parse(currentDate);
								service.clearCache(dateObj.getYear(), dateObj.getMonthValue());
								var newStatus = new DayWorkStatus(
										selectedShift.getShiftName() + " (" + selectedShift.getStartTime() + " - " + selectedShift.getEndTime() + ")", false);
								var workScheduleInfo = service.getWorkSheduleByIdDate(currentEmployeeId, currentDate);
								shiftListPanel.add(Box.createVerticalStrut(10), 1);
								shiftListPanel.setAlignmentY(TOP_ALIGNMENT);
								shiftListPanel.add(createShiftPanel(newStatus, workScheduleInfo), 1);
								shiftListPanel.revalidate();
								shiftListPanel.repaint();

								if (onDataChanged != null) {
									onDataChanged.run();
								}
							} else {
								JOptionPane.showMessageDialog(this, "Add shift failed!");
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(this, "Error adding shift!");
						}
					});
		}


	}

	public void onAddOT() {
		var workScheduleInfo = service.getWorkSheduleByIdDate(currentEmployeeId, currentDate);
		List<OTJunction> existOt = new ArrayList<>();
		if (workScheduleInfo != null) {
			existOt = otJunctionService.getByWorkScheduleId(workScheduleInfo.getId());
		}

		var shiftId = 0;
		Shift shiftInfo = null;
		if (workScheduleInfo != null) {
			shiftId = workScheduleInfo.getShiftId() != null ? workScheduleInfo.getShiftId() : 0 ;
		}
		if (shiftId > 0) {
			var shiftService = new ShiftService();
			shiftInfo = shiftService.getById(shiftId);
		}

		var otTypes = new OTTypeService().getAll();
		if (existOt != null && !existOt.isEmpty()) {
			var existOtIds = existOt.stream()
					.map(OTJunction::getOtTypeId)
					.toList();
			otTypes.removeIf(ot -> existOtIds.contains(ot.getId()));
		}
		if (shiftInfo != null && shiftInfo.getStartTime() != null && shiftInfo.getEndTime() != null) {
			var shiftStart = shiftInfo.getStartTime().toLocalTime();
			var shiftEnd = shiftInfo.getEndTime().toLocalTime();

			otTypes.removeIf(ot -> {
				var otStart = ot.getOtStart().toLocalTime();
				var otEnd = ot.getOtEnd().toLocalTime();
				var overlap = otStart.isBefore(shiftEnd) && otEnd.isAfter(shiftStart);
				return overlap;
			});
		}
		if (existOt != null && !existOt.isEmpty()) {
			var otTypeService = new OTTypeService();
			for (var existing : existOt) {
				var existingType = otTypeService.getById(existing.getOtTypeId());
				if (existingType != null) {
					var existStart = existingType.getOtStart().toLocalTime();
					var existEnd = existingType.getOtEnd().toLocalTime();

					otTypes.removeIf(ot -> {
						var otStart = ot.getOtStart().toLocalTime();
						var otEnd = ot.getOtEnd().toLocalTime();
						// true nếu giao nhau về thời gian
						return otStart.isBefore(existEnd) && otEnd.isAfter(existStart);
					});
				}
			}
		}

		// 🔹 Hiển thị dialog chọn OT
		showAddDialog("OT", otTypes,
				ot -> ot.getOtName() + " (" + ot.getOtStart() + " - " + ot.getOtEnd() + ")",
				selectedOT -> {
					try {
						var workScheduleId = 0;
						var otWork = new WorkSchedule();
						otWork.setEmployeeId(currentEmployeeId);
						otWork.setId(null);
						otWork.setWorkDate(java.sql.Date.valueOf(currentDate));

						var otJunction = new OTJunction();
						otJunction.setOtTypeId(selectedOT.getId());
						otJunction.setOtConfirm("confirmed");

						var getWorkScheduleId = service.checkWorkScheduleId(currentEmployeeId, currentDate);
						if (getWorkScheduleId > 0) {
							otJunction.setWorkScheduleId(getWorkScheduleId);
						} else {
							workScheduleId = workScheduleService.addAndReturnId(otWork);
							if (workScheduleId == -1) {
								JOptionPane.showMessageDialog(this, "Create WorkSchedule for OT failed!");
								return;
							}
							otJunction.setWorkScheduleId(workScheduleId);
						}

						var success = new OTJunctionService().add(otJunction);
						if (success) {
							JOptionPane.showMessageDialog(this, "Add OT successfully!");
							shiftListPanel.add(Box.createVerticalStrut(10), 1);
							shiftListPanel.add(createOTPanel(otJunction), 1);
							shiftListPanel.setAlignmentY(TOP_ALIGNMENT);
							shiftListPanel.revalidate();
							shiftListPanel.repaint();

							if (onDataChanged != null) {
								onDataChanged.run();
							}
						} else {
							JOptionPane.showMessageDialog(this, "Add OT failed!");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this, "Error adding OT!");
					}
				});
	}


	// ✅ Hiển thị danh sách ca làm
	public void showEmployeeSchedule(int employeeId, String employeeName, String date, List<DayWorkStatus> dayStatusList) {
		currentEmployeeId = employeeId;
		currentEmployeeName = employeeName;
		currentDate = date;

		shiftListPanel.removeAll();
		var workScheduleInfo = service.getWorkSheduleByIdDate(currentEmployeeId, currentDate);

		var headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		headerPanel.setOpaque(false);
		var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		var formattedDate = LocalDate.parse(date).format(fmt);
		var header = new JLabel("Work schedule of " + employeeName + " on " + formattedDate);
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		headerPanel.add(header);
		shiftListPanel.add(headerPanel);

		if (dayStatusList == null || dayStatusList.isEmpty()) {
			var empty = new JLabel("No shifts on this day.");
			empty.setForeground(Color.GRAY);
			empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
			shiftListPanel.add(empty);
		} else {
			for (DayWorkStatus s : dayStatusList) {
				shiftListPanel.add(createShiftPanel(s, workScheduleInfo));
				shiftListPanel.add(Box.createVerticalStrut(10));
			}
		}

		var otService = new com.example.swingapp.service.OTJunctionService();
		var otList = otService.getByWorkScheduleId(workScheduleInfo.getId());
		for (com.example.swingapp.model.OTJunction ot : otList) {
			shiftListPanel.add(createOTPanel(ot));
			shiftListPanel.add(Box.createVerticalStrut(10));
		}

		shiftListPanel.revalidate();
		shiftListPanel.repaint();
	}

	public JPanel createOTPanel(OTJunction ot) {
		var otType = new OTTypeService().getById(ot.getOtTypeId());
		if (otType == null) {
			var empty = new JPanel();
			empty.setBackground(Color.WHITE);
			empty.setBorder(BorderFactory.createLineBorder(new Color(224, 235, 250), 1, true));
			empty.add(new JLabel("OT type not found"));
			return empty;
		}


		var otName = otType.getOtName();
		var otStart = otType.getOtStart();
		var otEnd = otType.getOtEnd();

		var otFullName = String.format("%s (%s - %s)",
				otName,
				otStart != null ? otStart.toString() : "--:--",
						otEnd != null ? otEnd.toString() : "--:--");

		var ws = new WorkScheduleDAO().getById(ot.getWorkScheduleId());
		var otDetailsPanel = new OtDetailsPanel(ws, otFullName,
				new ShiftService().getById(ws.getShiftId()),ot, otType, this);
		var fixedSize = new Dimension(817, 150);
		otDetailsPanel.setPreferredSize(fixedSize);
		otDetailsPanel.setMinimumSize(fixedSize);
		otDetailsPanel.setMaximumSize(fixedSize);

		return otDetailsPanel;
	}

	// --- Panel hiển thị ca làm
	public JPanel createShiftPanel(DayWorkStatus status,WorkSchedule ws) {
		var shiftName = status.getShiftName();
		var isPresent = status.isPresent();

		if (ws == null || ws.getShiftId() == null) {
			var p = new JPanel(new BorderLayout());
			p.setBackground(Color.WHITE);
			p.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
					new EmptyBorder(16, 20, 16, 20)));
			p.setPreferredSize(new Dimension(600, 70));
			p.setMinimumSize(new Dimension(600, 600));
			var l = new JLabel(status.getShiftName() + (status.isPresent() ? " - X" : ""));
			l.setFont(new Font("Segoe UI", Font.BOLD, 13));
			l.setForeground(new Color(50, 50, 50));
			p.add(l, BorderLayout.CENTER);
			return p;
		}

		var p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(10, 30, 10, 30)));

		var l = new JLabel(shiftName + (isPresent ? " - X" : ""));
		l.setFont(new Font("Segoe UI", Font.BOLD, 13));
		l.setForeground(new Color(50, 50, 50));
		p.add(l, BorderLayout.CENTER);

		var shift = new ShiftService().getById(ws.getShiftId());
		var shiftFullName = (shift != null) ?
				shift.getShiftName() + " (" + shift.getStartTime() + " - " + shift.getEndTime() + ")"
				: status.getShiftName();
		ActionListener removeAction = e -> {

		};

		var shiftPanel = new ShiftDetailsPanel(ws, shiftFullName, shift, this);
		var fixedSize = new Dimension(817, 150);
		shiftPanel.setPreferredSize(fixedSize);
		shiftPanel.setMinimumSize(fixedSize);
		shiftPanel.setMaximumSize(fixedSize);

		return shiftPanel;

	}

	public String extractShiftName(String fullName) {
		var idx = fullName.indexOf(" (");
		return (idx > 0) ? fullName.substring(0, idx) : fullName;
	}

	public static JButton createButton(String text, Color bg, int w) {
		JButton b = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var fillColor = bg;
				if (getModel().isPressed()) {
					fillColor = bg.darker();
				} else if (getModel().isRollover()) {
					fillColor = bg.brighter();
				}
				g2.setColor(fillColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
				g2.setColor(new Color(0, 0, 0, 20));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				var textWidth = fm.stringWidth(getText());
				var textHeight = fm.getAscent();
				g2.drawString(getText(), (getWidth() - textWidth) / 2,
						(getHeight() + textHeight - fm.getDescent()) / 2);
			}
		};
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 36));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setRolloverEnabled(true);
		b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		return b;
	}

	// --- Generic helper
	private <T> void showAddDialog(String title, List<T> items, Function<T, String> displayFunc, Consumer<T> saveAction) {
		if (currentEmployeeId == 0 || currentDate == null) {
			JOptionPane.showMessageDialog(this, "Employee or date not selected!");
			return;
		}

		if (items == null || items.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No suitable shifts available, please check OT shifts!");
			return;
		}

		var displayArr = items.stream().map(displayFunc).toArray(String[]::new);
		var combo = new JComboBox<String>(displayArr);

		combo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
			var label = new JLabel(value);
			label.setOpaque(true);

			if (isSelected) {
				label.setBackground(new Color(220, 235, 255));
			} else {
				label.setBackground(Color.WHITE);
			}
			if (index >= 0 && items.get(index) instanceof com.example.swingapp.model.OTType otType && otType.isDisabled()) {
				label.setForeground(Color.GRAY);
				label.setEnabled(false);
			} else {
				label.setForeground(Color.BLACK);
			}

			return label;
		});





		var panel = new JPanel();
		panel.add(new JLabel("Select " + title + ":"));
		panel.add(combo);

		var result = JOptionPane.showConfirmDialog(this, panel, "Add " + title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			var selectedIndex = combo.getSelectedIndex();
			var selectedItem = items.get(selectedIndex);
			if (selectedItem instanceof com.example.swingapp.model.OTType otType && otType.isDisabled()) {
				JOptionPane.showMessageDialog(this, "Cannot select OT overlapping with shift!");
				return;
			}
			saveAction.accept(selectedItem);

			shiftListPanel.removeAll();
			showEmployeeSchedule(currentEmployeeId, currentEmployeeName, currentDate,
					service.getDayWorkStatus(currentEmployeeName, currentDate));
		}
	}

	public void notifyDataChanged() {
		if (onDataChanged != null) {
			onDataChanged.run();
		}
	}

	public void reloadForm() {
		removeAll();
		initUI(btnSave.getActionListeners()[0], btnCancel.getActionListeners()[0]);

		if (currentEmployeeId > 0 && currentDate != null && currentEmployeeName != null) {
			var dayStatusList = service.getDayWorkStatus(currentEmployeeName, currentDate);
			showEmployeeSchedule(currentEmployeeId, currentEmployeeName, currentDate, dayStatusList);
			revalidate();
			repaint();
		}
	}
}
