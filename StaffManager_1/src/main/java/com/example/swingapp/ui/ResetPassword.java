package com.example.swingapp.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;

import com.example.swingapp.helper.RandomStringHelper;
import com.example.swingapp.helper.SendMailHelper;
import com.example.swingapp.model.Account;
import com.example.swingapp.service.AccountService;

public class ResetPassword extends JFrame {

	private JTextField txtAccountName;
	private JTextField txtEmail;
	private JButton btnSignUp;
	private JLabel cmdToLogin;
	private final AccountService accountService = new AccountService();
	private final RandomStringHelper randomString = new RandomStringHelper();
	private final SendMailHelper sendMail = new SendMailHelper();

	private static final Color PRIMARY = new Color(67, 97, 238);
	private static final Color PRIMARY_HOVER = new Color(57, 87, 228);
	private static final Color TEXT_LIGHT = new Color(117, 117, 117);
	private static final Color BORDER_DEFAULT = new Color(200, 200, 200);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color WARNING_ORANGE = new Color(255, 152, 0);
	private final Color BORDER_FOCUS = PRIMARY;

	public ResetPassword() {
		initUI();
		setLocationRelativeTo(null);
	}

	private void initUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Reset Password - Attendance System");
		setResizable(false);
		setMinimumSize(new Dimension(450, 580));

		var mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(109, 213, 250));

		var card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(450, 580));
		card.setBackground(Color.WHITE);
		card.setBorder(new RoundedShadowBorder(25, 12));
		card.setAlignmentX(Component.CENTER_ALIGNMENT);

		var title = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setForeground(new Color(33, 33, 33));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		var subtitle = new JLabel("Enter your email", SwingConstants.CENTER);
		subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		subtitle.setForeground(TEXT_LIGHT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		var lblAccountName = new JLabel("Your Account Name");
		lblAccountName.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblAccountName.setForeground(new Color(33, 33, 33));
		lblAccountName.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblAccountName.setPreferredSize(new Dimension(330, 50));
		txtAccountName = createInputField();
		var lblEmail = new JLabel("Your Email");
		lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblEmail.setForeground(new Color(33, 33, 33));
		lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblEmail.setPreferredSize(new Dimension(330, 50));
		txtEmail = createInputField();

		btnSignUp = createButton("Send",PRIMARY_BLUE,330);
		btnSignUp.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnSignUp.setMaximumSize(new Dimension(330, 55));
		btnSignUp.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSignUp.addActionListener(e -> handleResetPassword());
		btnSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { btnSignUp.setBackground(PRIMARY_HOVER); }
			@Override
			public void mouseExited(MouseEvent e) { btnSignUp.setBackground(PRIMARY); }
		});

		var bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		var lblHave = new JLabel("Already have an account? ");
		lblHave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblHave.setForeground(TEXT_LIGHT);

		cmdToLogin = new JLabel("Login now");
		cmdToLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
		cmdToLogin.setForeground(PRIMARY);
		cmdToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cmdToLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new LoginForm().setVisible(true);
				dispose();
			}
		});

		bottomPanel.add(lblHave);
		bottomPanel.add(cmdToLogin);

		card.add(Box.createVerticalGlue());
		card.add(title);
		card.add(Box.createRigidArea(new Dimension(0, 10)));
		card.add(subtitle);
		card.add(Box.createRigidArea(new Dimension(0, 35)));
		card.add(lblAccountName);
		card.add(Box.createRigidArea(new Dimension(0, 5)));
		card.add(txtAccountName);
		card.add(Box.createRigidArea(new Dimension(0, 10)));
		card.add(lblEmail);
		card.add(Box.createRigidArea(new Dimension(0, 5)));
		card.add(txtEmail);
		card.add(Box.createRigidArea(new Dimension(0, 35)));
		card.add(btnSignUp);
		card.add(Box.createRigidArea(new Dimension(0, 30)));
		card.add(bottomPanel);
		card.add(Box.createVerticalGlue());

		var gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(card, gbc);

		setContentPane(mainPanel);
		pack();
	}

	private JTextField createInputField() {
		var field = new JTextField();
		styleTextField(field);
		return field;
	}

	private JPasswordField createPasswordField() {
		var field = new JPasswordField();
		styleTextField(field);
		return field;
	}

	private void styleTextField(JTextField field) {
		field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		field.setPreferredSize(new Dimension(330, 50));
		field.setMaximumSize(new Dimension(330, 50));
		field.setAlignmentX(Component.CENTER_ALIGNMENT);
		field.setBorder(new RoundedBorder(12, BORDER_DEFAULT));
		field.setCaretColor(PRIMARY);
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) { field.setBorder(new RoundedBorder(12, BORDER_FOCUS, 2)); }
			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().isEmpty()) {
					field.setBorder(new RoundedBorder(12, BORDER_DEFAULT, 1));
				}
			}
		});
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
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, 50));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setRolloverEnabled(true);
		b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		return b;
	}

	private void handleResetPassword() {
		if (!validateForm()) {
			return;
		}

		var accountName = txtAccountName.getText().trim();
		var email = txtEmail.getText().trim();
		var auth = "ADMIN";
		var checkAccountInfo = accountService.checkAccountInfo(accountName, auth, email);
		if(checkAccountInfo == null) {
			JOptionPane.showMessageDialog(this,
					"Account Name or Email doesn't match, please try again!",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}else {
			this.handleSendMail(email,checkAccountInfo);

		}
	}
	private void handleSendMail(String email, Account accountInfo) {
		var tokenCode = randomString.RandomString();
		var from = "datleoffice264@gmail.com";
		var to = email;
		var subject = "Reset Your Password";
		var html = """
				<h3>Hello!</h3>
				<p>This is your reset password code: %s.</p>
				""";
		var finalHtml = String.format(html, tokenCode);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		var checkSend = false;
		SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				var checkSendMail = false;
				try {
					checkSendMail = sendMail.SendMail(from, to, subject, finalHtml);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return checkSendMail;
			}
			@Override
			protected void done() {
				// 3. Đặt lại con trỏ về mặc định sau khi hoàn thành
				ResetPassword.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				try {
					// Lấy kết quả (true/false) từ doInBackground
					boolean checkSendMail = get();

					// 4. Hiển thị thông báo dựa trên kết quả
					if (checkSendMail) {
						JOptionPane.showMessageDialog(ResetPassword.this,
								"A Code has been sent to your email, please check your email!",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						var accountId = accountInfo.getAccountId();
						var code = tokenCode;
						var checkInsertCode = accountService.insertResetCode(code, accountId);
						new ConfirmFormPanel(from, to, subject, html, accountInfo).setVisible(true);
						dispose();

					} else {
						JOptionPane.showMessageDialog(ResetPassword.this,
								"There are something wrong when send EMail, please try again!",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(ResetPassword.this,
							"An error occurred: " + ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
	}

	private boolean validateForm() {
		if (txtAccountName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Warning", JOptionPane.WARNING_MESSAGE); return false;
		}
		return true;
	}

	// Rounded border class
	private static class RoundedBorder extends AbstractBorder {
		private int radius; private Color color; private int thickness;
		RoundedBorder(int radius, Color color) { this(radius, color, 1); }
		RoundedBorder(int radius, Color color, int thickness) { this.radius = radius; this.color = color; this.thickness = thickness; }
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			var g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
			g2d.dispose();
		}
		@Override public Insets getBorderInsets(Component c) { return new Insets(thickness + radius / 2, thickness + radius / 2, thickness + radius / 2, thickness + radius / 2); }
		@Override public boolean isBorderOpaque() { return false; }
	}

	// Simple shadow border for WindowBuilder
	private static class RoundedShadowBorder extends AbstractBorder {
		private int radius; private int size;
		RoundedShadowBorder(int radius, int size) { this.radius = radius; this.size = size; }
		@Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {}
		@Override public Insets getBorderInsets(Component c) { return new Insets(size, size, size, size); }
		@Override public boolean isBorderOpaque() { return false; }
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new ResetPassword().setVisible(true));
	}
}
