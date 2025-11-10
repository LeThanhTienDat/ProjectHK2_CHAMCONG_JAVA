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

import javax.swing.BorderFactory;
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
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.dao.AccountDAO;
import com.example.swingapp.util.PasswordUtils;

public class LoginForm extends JFrame {

	private JTextField txtUser;
	private JPasswordField txtPassword;
	private JButton btnLogin;
	private JLabel lblSignUp;

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

	public LoginForm() {
		initModernComponents();
		setLocationRelativeTo(null);
	}

	private void initModernComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Login - Timekeeping System");
		setResizable(false);
		setMinimumSize(new Dimension(450, 580));

		var mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(240, 240, 240));

		var card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(450, 580));
		card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
		card.setAlignmentX(Component.CENTER_ALIGNMENT);

		var title = new JLabel("TIMEKEEPING SYSTEM", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setForeground(new Color(33, 33, 33));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		var subtitle = new JLabel("Login to start your program", SwingConstants.CENTER);
		subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		subtitle.setForeground(TEXT_LIGHT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		txtUser = styledTextField("Account name",330);
		txtUser.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtUser.getText().equals("Account name")) {
					txtUser.setText("");
					txtUser.setForeground(TEXT_PRIMARY);
					txtUser.setBorder(new RoundedBorder(12, BORDER_FOCUS, 2));
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtUser.getText().isEmpty()) {
					txtUser.setText("Account name");
					txtUser.setForeground(Color.GRAY);
					txtUser.setBorder(new RoundedBorder(12, BORDER_DEFAULT, 1));
				}
			}
		});
		txtPassword = (JPasswordField) styledPasswordField("Password", 330);
		txtPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtPassword.getText().equals("Password")) {
					txtPassword.setText("");
					txtPassword.setForeground(TEXT_PRIMARY);
					txtPassword.setBorder(new RoundedBorder(12, BORDER_FOCUS, 2));
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtPassword.getText().isEmpty()) {
					txtPassword.setText("Password");
					txtPassword.setForeground(Color.GRAY);
					txtPassword.setBorder(new RoundedBorder(12, BORDER_DEFAULT, 1));
				}
			}
		});
		txtPassword.addActionListener(e -> handleLogin());
		btnLogin = createButton("Login",PRIMARY_BLUE,330);
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLogin.setMaximumSize(new Dimension(330, 55));
		btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLogin.addActionListener(e -> handleLogin());
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnLogin.setBackground(PRIMARY_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnLogin.setBackground(PRIMARY);
			}
		});

		var signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		signUpPanel.setBackground(Color.WHITE);
		signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		var lblNoAccount = new JLabel("Forgot password? ");
		lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNoAccount.setForeground(TEXT_LIGHT);

		lblSignUp = new JLabel("Click here");
		lblSignUp.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSignUp.setForeground(PRIMARY);
		lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new ResetPassword().setVisible(true);
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY_HOVER);
				lblSignUp.setText("<html><u>Click here</u></html>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY);
				lblSignUp.setText("Click here");
			}
		});

		signUpPanel.add(lblNoAccount);
		signUpPanel.add(lblSignUp);

		// Layout card
		card.add(Box.createVerticalGlue());
		card.add(title);
		card.add(Box.createRigidArea(new Dimension(0, 15)));
		card.add(subtitle);
		card.add(Box.createRigidArea(new Dimension(0, 50)));
		card.add(txtUser);
		card.add(Box.createRigidArea(new Dimension(0, 20)));
		card.add(txtPassword);
		card.add(Box.createRigidArea(new Dimension(0, 40)));
		card.add(btnLogin);
		card.add(Box.createRigidArea(new Dimension(0, 35)));
		card.add(signUpPanel);
		card.add(Box.createVerticalGlue());

		var gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(card, gbc);

		setContentPane(mainPanel);
		pack();
	}

	//	private void styleTextField(JTextField field, String placeholder) {
	//
	//		field.setAlignmentX(Component.CENTER_ALIGNMENT);
	//		field.setBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1, true));
	//		field.setToolTipText(placeholder);
	//		field.setCaretColor(PRIMARY);
	//	}
	public JTextField styledTextField(String ph, int w) {
		var f = new JTextField(ph);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		f.setPreferredSize(new Dimension(w, 50));
		f.setMaximumSize(new Dimension(w, 50));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		return f;
	}
	public JTextField styledPasswordField(String ph, int w) {
		var f = new JPasswordField(ph);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		f.setPreferredSize(new Dimension(w, 50));
		f.setMaximumSize(new Dimension(w, 50));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		return f;
	}

	private void handleLogin() {
		var username = txtUser.getText().trim();
		var password = new String(txtPassword.getPassword());

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please enter the full account and password!",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		var dao = new AccountDAO();
		var account = dao.findByUsername(username); // phương thức đơn giản hơn

		if (account == null) {
			JOptionPane.showMessageDialog(this,
					"Account does not exist!",
					"Login Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Hash mật khẩu người dùng nhập vào
		var hashInput = PasswordUtils.hashPassword(password, account.getSalt());

		if (!hashInput.equals(account.getPassword())) {
			JOptionPane.showMessageDialog(this,
					"Incorrect password!",
					"Login Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Đăng nhập thành công
		JOptionPane.showMessageDialog(this,
				"Login successful!",
				"Success",
				JOptionPane.INFORMATION_MESSAGE);

		if ("ADMIN".equalsIgnoreCase(account.getAuth())) {
			new Admin.Admin().setVisible(true);
		} else {
			new JFrame("Employee Page").setVisible(true);
		}

		dispose();
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
}
