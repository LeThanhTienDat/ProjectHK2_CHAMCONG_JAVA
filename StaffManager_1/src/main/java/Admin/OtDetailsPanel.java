package Admin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.example.swingapp.model.OTJunction;
import com.example.swingapp.model.OTType;
import com.example.swingapp.model.Shift;
import com.example.swingapp.model.WorkSchedule;

public class OtDetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color LIGHT_BLUE = new Color(227, 242, 253); // đúng giờ
	private static final Color LIGHT_RED = new Color(255, 235, 238);  // trễ / sớm
	private Color inBg;
	private Color outBg;
	public OtDetailsPanel(WorkSchedule ws, String shiftFullName, Shift shift, OTJunction ot, OTType otType) {
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
		var statusText = (ot.getOtCheckInTime() != null || ot.getOtCheckOutTime() != null) ? "Đã chấm công" : "Chưa chấm công";
		var otFullName = (otType != null) ?
				otType.getOtName() + " (" + otType.getOtStart() + " - " + otType.getOtEnd() + ")"
				: "";
		var otLabel = new JLabel("<html><b>Ca OT:</b> " + otFullName + "<br><i>" + statusText + "</i></html>");
		otLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		otLabel.setForeground(PRIMARY_BLUE.darker());

		var leftPanel = createColumnPanel();
		leftPanel.add(otLabel);
		leftPanel.setMinimumSize(new Dimension(170, 0));

		gbc.gridx = 0;
		gbc.weightx = 0.4;
		add(leftPanel, gbc);

		// ========== CỘT 2: CHECK IN ==========
		var otIn = otType.getOtStart() != null ? otType.getOtStart().toLocalTime() : null;
		var actualIn = ot.getOtCheckInTime() != null ? ot.getOtCheckInTime().toLocalTime() : null;

		var otInStr = otIn != null ? otIn.toString() : "--:--";
		var actualInStr = actualIn != null ? actualIn.toString() : "Chưa IN";
		var inNote = "";
		inBg = LIGHT_BLUE;

		if (actualIn != null && otIn != null) {
			if (actualIn.isAfter(otIn)) {
				var lateMinutes = Duration.between(otIn, actualIn).toMinutes();
				inNote = "(Trễ " + lateMinutes + " phút)";
				inBg = LIGHT_RED;
			} else {
				inNote = "(Đúng giờ)";
			}
		} else if (actualIn == null) {
			inNote = "(Chưa IN)";
		}

		var otInLabel = new JLabel("Ca bắt đầu: " + otInStr);
		otInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		otInLabel.setForeground(new Color(80, 120, 180));

		var actualInLabel = new JLabel("Check in thực tế: " + actualInStr + " " + inNote);
		actualInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		actualInLabel.setForeground(Color.DARK_GRAY);

		var btnCheckIn = button("Check in", PRIMARY_BLUE,110);
		btnCheckIn.setEnabled(actualIn == null);
		btnCheckIn.addActionListener(e -> {
			System.out.println("Chấm công IN cho OT ID: " + ot.getId());
		});

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
		inPanel.add(otInLabel);
		inPanel.add(actualInLabel);
		inPanel.add(btnCheckIn);

		gbc.gridx = 1;
		gbc.weightx = 0.3;
		add(inPanel, gbc);

		// ========== CỘT 3: CHECK OUT ==========
		var otOut = otType.getOtEnd() != null ? otType.getOtEnd().toLocalTime() : null;
		var actualOut = ot.getOtCheckOutTime() != null ? ot.getOtCheckOutTime().toLocalTime() : null;

		var otOutStr = otOut != null ? otOut.toString() : "--:--";
		var actualOutStr = actualOut != null ? actualOut.toString() : "Chưa OUT";
		var outNote = "";
		outBg = LIGHT_BLUE;

		if (actualOut != null && otOut != null) {
			if (actualOut.isBefore(otOut)) {
				var earlyMinutes = Duration.between(actualOut, otOut).toMinutes();
				outNote = "(Ra sớm " + earlyMinutes + " phút)";
				outBg = LIGHT_RED;
			} else {
				outNote = "(Đúng giờ)";
			}
		} else if (actualOut == null) {
			outNote = "(Chưa OUT)";
		}

		var shiftOutLabel = new JLabel("Ca kết thúc: " + otOutStr);
		shiftOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		shiftOutLabel.setForeground(new Color(80, 120, 180));

		var btnCheckOut = button("Check out", PRIMARY_BLUE,110);
		btnCheckOut.setEnabled(actualOut == null);
		btnCheckOut.addActionListener(e -> {
			System.out.println("Chấm công OUT cho OT ID: " + ot.getId());
		});

		var actualOutLabel = new JLabel("Check out thực tế: " + actualOutStr + " " + outNote);
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
		gbc.weightx = 0.3;
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
				var fillColor = bg;
				if (getModel().isPressed()) {
					fillColor = bg.darker();
				} else if (getModel().isRollover()) {
					fillColor = bg.brighter();
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
