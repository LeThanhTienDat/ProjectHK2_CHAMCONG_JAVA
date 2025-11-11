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

import com.example.swingapp.model.Shift;
import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.service.WorkScheduleService;

public class ShiftDetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color LIGHT_BLUE = new Color(227, 242, 253); // đúng giờ
	private static final Color LIGHT_RED = new Color(255, 235, 238);  // trễ / sớm
	private Color inBg;
	private Color outBg;

	private AttendanceFormPanel parent;
	private WorkSchedule workSchedule;
	private final WorkScheduleService workScheduleService = new WorkScheduleService();

	public ShiftDetailsPanel(WorkSchedule ws, String shiftFullName, Shift shift, AttendanceFormPanel getParent) {
		workSchedule = ws;
		parent = getParent;
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
		var statusText = ws.getCheckInTime() != null ? "Checked in" : "Not checked in";
		var shiftLabel = new JLabel("<html><b>Main Shift: </b> " + shiftFullName + "<br><i>" + statusText + "</i></html>");
		shiftLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		shiftLabel.setForeground(PRIMARY_BLUE.darker());

		var leftPanel = createColumnPanel();
		leftPanel.setLayout(new GridLayout(2, 1, 0, 8));
		var labelContainer = new JPanel(new GridLayout(1, 1));
		labelContainer.setOpaque(false);
		labelContainer.add(shiftLabel);

		var emptyButtonContainer = new JPanel();
		emptyButtonContainer.setOpaque(false);
		emptyButtonContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		leftPanel.setMinimumSize(new Dimension(270, 0));
		leftPanel.add(labelContainer);
		leftPanel.add(emptyButtonContainer);

		gbc.gridx = 0;
		gbc.weightx = 0;
		add(leftPanel, gbc);

		// ========== CỘT 2: CHECK IN ==========
		var shiftIn = shift.getStartTime() != null ? shift.getStartTime().toLocalTime() : null;
		var actualIn = ws.getCheckInTime() != null ? ws.getCheckInTime().toLocalTime() : null;

		var shiftInStr = shiftIn != null ? shiftIn.toString() : "--:--";
		var actualInStr = actualIn != null ? actualIn.toString() : "Not checked in";
		var inNote = "";
		inBg = LIGHT_BLUE;

		if (actualIn != null && shiftIn != null) {
			if (actualIn.isAfter(shiftIn)) {
				var lateMinutes = Duration.between(shiftIn, actualIn).toMinutes();
				inNote = "(Late " + lateMinutes + " min)";
				inBg = LIGHT_RED;
			} else {
				inNote = "(On Time)";
			}
		} else if (actualIn == null) {
			inNote = "(Not checked in)";
		}

		var shiftInLabel = new JLabel("Shift start: " + shiftInStr);
		shiftInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		shiftInLabel.setForeground(new Color(80, 120, 180));

		var btnCheckIn = button("Check in", PRIMARY_BLUE,110);
		btnCheckIn.setEnabled(actualIn == null);
		btnCheckIn.addActionListener(e -> {
			try {
				var workScheduleId = workSchedule.getId();
				if (workScheduleId <= 0) {
					JOptionPane.showMessageDialog(this, "Valid shift not found!");
					return;
				}

				var confirm = JOptionPane.showConfirmDialog(
						this,
						"Are you sure you want to check in this shift?",
						"Confirm check-in",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
						);

				if (confirm != JOptionPane.YES_OPTION) {
					return;
				}

				var success = workScheduleService.checkInShift(workScheduleId);
				if (success) {
					JOptionPane.showMessageDialog(this, "Check-in successful!");
					parent.reloadForm();
					parent.notifyDataChanged();
				} else {
					JOptionPane.showMessageDialog(this, "Check-in failed! Please try again.");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "An error occurred during check-in!");
			}
		});

		var actualInLabel = new JLabel("Actual check-in: " + actualInStr + " " + inNote);
		actualInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		actualInLabel.setForeground(Color.DARK_GRAY);

		var inPanel = new JPanel(new GridLayout(3, 1, 0, 4)) {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				var g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(inBg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo góc 20px
				g2.dispose();
				super.paintComponent(g);
			}
		};
		inPanel.setOpaque(false);
		inPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		inPanel.add(shiftInLabel);
		inPanel.add(actualInLabel);
		inPanel.add(btnCheckIn);

		gbc.gridx = 1;
		gbc.weightx = 0.5;
		add(inPanel, gbc);

		// ========== CỘT 3: CHECK OUT ==========
		var shiftOut = shift.getEndTime() != null ? shift.getEndTime().toLocalTime() : null;
		var actualOut = ws.getCheckOutTime() != null ? ws.getCheckOutTime().toLocalTime() : null;

		var shiftOutStr = shiftOut != null ? shiftOut.toString() : "--:--";
		var actualOutStr = actualOut != null ? actualOut.toString() : "Not checked out";
		var outNote = "";
		outBg = LIGHT_BLUE;

		if (actualOut != null && shiftOut != null) {
			if (actualOut.isBefore(shiftOut)) {
				var earlyMinutes = Duration.between(actualOut, shiftOut).toMinutes();
				outNote = "(Left early " + earlyMinutes + " min)";
				outBg = LIGHT_RED;
			} else {
				outNote = "(On Time)";
			}
		} else if (actualOut == null) {
			outNote = "(Not checked out)";
		}

		var shiftOutLabel = new JLabel("Shift end: " + shiftOutStr);
		shiftOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		shiftOutLabel.setForeground(new Color(80, 120, 180));

		var btnCheckOut = button("Check out", PRIMARY_BLUE,110);
		btnCheckOut.setEnabled(actualOut == null);
		btnCheckOut.addActionListener(e -> {
			try {
				var workScheduleId = workSchedule.getId();
				if (workScheduleId <= 0) {
					JOptionPane.showMessageDialog(this, "Valid shift not found!");
					return;
				}

				var confirm = JOptionPane.showConfirmDialog(
						this,
						"Are you sure you want to check out this shift?",
						"Confirm check-out",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
						);

				if (confirm != JOptionPane.YES_OPTION) {
					return;
				}

				var success = workScheduleService.checkOutShift(workScheduleId);
				if (success) {
					JOptionPane.showMessageDialog(this, "Check-out successful!");
					parent.reloadForm();
					parent.notifyDataChanged();
				} else {
					JOptionPane.showMessageDialog(this, "Check-out failed! Please try again.");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "An error occurred during check-out!");
			}
		});

		var actualOutLabel = new JLabel("Actual check-out: " + actualOutStr + " " + outNote);
		actualOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		actualOutLabel.setForeground(Color.DARK_GRAY);

		var outPanel = new JPanel(new GridLayout(3, 1, 0, 4)) {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				var g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(outBg);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo góc 20px
				g2.dispose();
				super.paintComponent(g);
			}
		};
		outPanel.setOpaque(false);
		outPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		outPanel.add(shiftOutLabel);
		outPanel.add(actualOutLabel);
		outPanel.add(btnCheckOut);

		gbc.gridx = 2;
		gbc.weightx = 0.5;
		add(outPanel, gbc);

		// Kích thước cố định
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
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo góc 20px
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
