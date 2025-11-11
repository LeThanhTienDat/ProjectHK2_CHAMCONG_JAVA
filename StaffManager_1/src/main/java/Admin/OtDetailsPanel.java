package Admin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.Duration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.example.swingapp.model.OTJunction;
import com.example.swingapp.model.OTType;
import com.example.swingapp.model.Shift;
import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.service.OTJunctionService;

public class OtDetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color LIGHT_BLUE = new Color(227, 242, 253);
	private static final Color LIGHT_RED = new Color(255, 235, 238);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color WARNING_ORANGE = new Color(255, 152, 0);
	private Color inBg;
	private Color outBg;
	final int FIXED_COL_WIDTH = 180;
	private final OTJunctionService otJunctionService = new OTJunctionService();
	public OtDetailsPanel(WorkSchedule ws, String shiftFullName, Shift shift, OTJunction ot, OTType otType, AttendanceFormPanel parent) {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		setBorder(new CompoundBorder(
				new LineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(10, 10, 10, 10)));

		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;

		// ========== CỘT 1 ==========
		var isConfirmed = ot.getOtConfirm();
		var isOtConfirmText = "Confirmed";
		var isNotOtConfirmText = "Pending";
		var isRejectedText = "Rejected";
		var confirmOtStatus = "";

		switch (isConfirmed) {
		case "confirmed":
			confirmOtStatus = isOtConfirmText;
			break;
		case "waiting":
			confirmOtStatus = isNotOtConfirmText;
			break;
		case "rejected":
			confirmOtStatus = isRejectedText;
			break;
		case null:
		default:
			break;
		}

		var checkInStatus = (ot.getOtCheckInTime() != null || ot.getOtCheckOutTime() != null)
				? "Checked in"
						: "Not checked in";

		var statusText = checkInStatus + " - " + confirmOtStatus;
		var otFullName = (otType != null) ?
				otType.getOtName() + " (" + otType.getOtStart() + " - " + otType.getOtEnd() + ")"
				: "";
		var otLabel = new JLabel("<html><b>Ca OT:</b> " + otFullName + "<br><i>" + statusText + "</i></html>");
		otLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		otLabel.setForeground(PRIMARY_BLUE.darker());

		//Confirm btn
		final var isFinalized = isConfirmed.equals("confirmed") || isConfirmed.equals("rejected");
		var buttonText = isConfirmed.equals("confirmed") ? "CONFIRMED"
				: isConfirmed.equals("rejected") ? "REJECTED"
						: "CONFIRM OT";
		// Màu xám khi đã duyệt, màu xanh khi chưa
		var buttonColor = isConfirmed.equals("confirmed") ? new Color(150, 150, 150) : PRIMARY_BLUE;
		var btnConfirmOrCancel = button(buttonText, buttonColor, 110);
		btnConfirmOrCancel.setEnabled(!isFinalized);

		btnConfirmOrCancel.addActionListener(e -> {
			var otJunctionId = ot.getId();
			var action = "Confirm";
			var newStatus = true;

			var confirmDialog = JOptionPane.showConfirmDialog(
					this,
					"Are you sure you want to confirm this shift?",
					action + " Confirm OT Request",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
					);

			if (confirmDialog == JOptionPane.YES_OPTION) {
				var success = otJunctionService.confirmOt(otJunctionId);

				if(success) {
					JOptionPane.showMessageDialog(this, "OT confirmed successfully!");
					parent.reloadForm();
					parent.notifyDataChanged();
				}else {
					JOptionPane.showMessageDialog(this, "OT confirmation failed, please check again!");
				}
			}
		});

		//Reject btn
		var rejectText = isConfirmed.equals("rejected") ? "REJECTED" : "REJECT";
		var rejectColor = (isConfirmed.equals("rejected") || isConfirmed.equals("confirmed"))
				? new Color(150, 150, 150) : DANGER_RED; // Màu đỏ khi đang chờ
		var btnReject = button(rejectText, rejectColor, 110);
		btnReject.setEnabled(isConfirmed.equals("waiting")); // Chỉ cho phép từ chối khi đang chờ

		btnReject.addActionListener(e -> {
			var otJunctionId = ot.getId();
			var action = "REJECT";

			var confirmDialog = JOptionPane.showConfirmDialog(
					this,
					"Are you sure you want to reject this OT shift?",
					action + "Reject OT Request",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
					);

			if (confirmDialog == JOptionPane.YES_OPTION) {
				var success = otJunctionService.rejectOt(otJunctionId);

				if(success) {
					JOptionPane.showMessageDialog(this, "OT rejected successfully!");
					parent.reloadForm();
					parent.notifyDataChanged();
				} else {
					JOptionPane.showMessageDialog(this, "OT rejection failed, please check again!");
				}
			}
		});

		var leftPanel = createColumnPanel();
		leftPanel.setLayout(new GridLayout(2, 1, 0, 8));
		var labelContainer = new JPanel(new GridLayout(1, 1));
		labelContainer.setOpaque(false);
		labelContainer.add(otLabel);
		var buttonContainer = new JPanel();
		buttonContainer.setOpaque(false);
		buttonContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		buttonContainer.add(btnConfirmOrCancel);
		buttonContainer.add(btnReject);

		leftPanel.add(labelContainer);
		leftPanel.add(buttonContainer);

		leftPanel.setMinimumSize(new Dimension(180, 0));

		gbc.gridx = 0;
		gbc.weightx = 0;
		add(leftPanel, gbc);

		// ========== CỘT 2: CHECK IN ==========
		var otIn = otType.getOtStart() != null ? otType.getOtStart().toLocalTime() : null;
		var actualIn = ot.getOtCheckInTime() != null ? ot.getOtCheckInTime().toLocalTime() : null;

		var otInStr = otIn != null ? otIn.toString() : "--:--";
		var actualInStr = actualIn != null ? actualIn.toString() : "Not checked in";
		var inNote = "";
		inBg = LIGHT_BLUE;

		if (actualIn != null && otIn != null) {
			if (actualIn.isAfter(otIn)) {
				var lateMinutes = Duration.between(otIn, actualIn).toMinutes();
				inNote = "(Late " + lateMinutes + " min)";
				inBg = LIGHT_RED;
			} else {
				inNote = "(On Time)";
			}
		} else if (actualIn == null) {
			inNote = "(Not checked in)";
		}

		var otInLabel = new JLabel("Shift Start: " + otInStr);
		otInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		otInLabel.setForeground(new Color(80, 120, 180));

		var actualInLabel = new JLabel("Actual Check-in: " + actualInStr + " " + inNote);
		actualInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		actualInLabel.setForeground(Color.DARK_GRAY);

		var btnCheckIn = button("Check in", PRIMARY_BLUE,110);
		btnCheckIn.setEnabled(actualIn == null);
		btnCheckIn.addActionListener(e -> {
			var workScheduleId = ws.getId();
			var otTypeId = otType.getId();
			var confirm = JOptionPane.showConfirmDialog(
					this,
					"Are you sure you want to check in this OT shift?",
					"Confirm OT Check-in",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
					);

			if (confirm == JOptionPane.YES_OPTION) {

				var success = otJunctionService.checkInOt(workScheduleId, otTypeId);
				JOptionPane.showMessageDialog(this, workScheduleId);
				JOptionPane.showMessageDialog(this, otTypeId);
				if (success) {
					JOptionPane.showMessageDialog(this, "OT check-in successful!");
					parent.reloadForm();
					parent.notifyDataChanged();
				} else {
					JOptionPane.showMessageDialog(this, "OT check-in failed!");
				}
			}
		});

		var inPanel = new JPanel(new GridLayout(3, 1, 0, 4)) {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				var g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(inBg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		inPanel.setOpaque(false);
		inPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		inPanel.add(otInLabel);
		inPanel.add(actualInLabel);
		inPanel.add(btnCheckIn);
		inPanel.setPreferredSize(new Dimension(FIXED_COL_WIDTH, 0));
		inPanel.setMinimumSize(new Dimension(FIXED_COL_WIDTH, 0));
		gbc.gridx = 1;
		gbc.weightx = 0.5;
		add(inPanel, gbc);

		// ========== CỘT 3: CHECK OUT ==========
		var otOut = otType.getOtEnd() != null ? otType.getOtEnd().toLocalTime() : null;
		var actualOut = ot.getOtCheckOutTime() != null ? ot.getOtCheckOutTime().toLocalTime() : null;

		var otOutStr = otOut != null ? otOut.toString() : "--:--";
		var actualOutStr = actualOut != null ? actualOut.toString() : "Not checked out";
		var outNote = "";
		outBg = LIGHT_BLUE;

		if (actualOut != null && otOut != null) {
			if (actualOut.isBefore(otOut)) {
				var earlyMinutes = Duration.between(actualOut, otOut).toMinutes();
				outNote = "(Left early " + earlyMinutes + " min)";
				outBg = LIGHT_RED;
			} else {
				outNote = "(On Time)";
			}
		} else if (actualOut == null) {
			outNote = "(Not checked out)";
		}

		var shiftOutLabel = new JLabel("Shift End: " + otOutStr);
		shiftOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		shiftOutLabel.setForeground(new Color(80, 120, 180));

		var btnCheckOut = button("Check out", PRIMARY_BLUE,110);
		btnCheckOut.setEnabled(actualOut == null);
		btnCheckOut.addActionListener(e -> {
			var workScheduleId = ws.getId();
			var otTypeId = otType.getId();
			var confirm = JOptionPane.showConfirmDialog(
					this,
					"Are you sure you want to check out this OT shift?",
					"Confirm OT Check-out",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
					);

			if (confirm == JOptionPane.YES_OPTION) {
				var success = otJunctionService.checkOutOt(workScheduleId, otTypeId);
				if (success) {
					JOptionPane.showMessageDialog(this, "OT check-out successful!");
					parent.reloadForm();
					parent.notifyDataChanged();
				} else {
					JOptionPane.showMessageDialog(this, "OT check-out failed!");
				}
			}
		});

		var actualOutLabel = new JLabel("Actual Check-out: " + actualOutStr + " " + outNote);
		actualOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		actualOutLabel.setForeground(Color.DARK_GRAY);

		var outPanel = new JPanel(new GridLayout(3, 1, 0, 4)) {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				var g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(outBg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		outPanel.setOpaque(false);
		outPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		outPanel.add(shiftOutLabel);
		outPanel.add(actualOutLabel);
		outPanel.add(btnCheckOut);
		outPanel.setPreferredSize(new Dimension(FIXED_COL_WIDTH, 0));
		outPanel.setMinimumSize(new Dimension(FIXED_COL_WIDTH, 0));
		gbc.gridx = 2;
		gbc.weightx = 0.5;
		add(outPanel, gbc);
		setPreferredSize(new Dimension(600, 100));
		setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
	}

	private JPanel createColumnPanel() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				var g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		return panel;
	}


	/** Nút tuỳ chỉnh có màu nền **/
	private static JButton createButton(String text, Color bg, Dimension size) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setPreferredSize(size);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		b.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				b.setBackground(bg.darker());
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				b.setBackground(bg);
			}
		});
		return b;
	}
	public static JButton button(String text, Color bg, int w) {
		JButton b = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Hiệu ứng hover mượt hơn
				var disabledColor = new Color(200, 200, 200); // màu xám nhạt khi disabled
				var fillColor = isEnabled() ? bg : disabledColor;

				if (isEnabled()) {
					if (getModel().isPressed()) {
						fillColor = bg.darker();
					} else if (getModel().isRollover()) {
						fillColor = bg.brighter();
					}
				}

				// Bo tròn góc
				g2.setColor(fillColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

				// Viền nhẹ nếu muốn tinh tế hơn
				g2.setColor(new Color(0, 0, 0, 20));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

				// Vẽ text giữa nút
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				var textWidth = fm.stringWidth(getText());
				var textHeight = fm.getAscent();
				g2.drawString(getText(), (getWidth() - textWidth) / 2,
						(getHeight() + textHeight - fm.getDescent()) / 2);
			}
		};

		// Cấu hình cơ bản
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 36));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setRolloverEnabled(true);

		// 👇 Thêm dòng này để con trỏ chuột đổi thành bàn tay khi hover
		b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

		return b;
	}


}
