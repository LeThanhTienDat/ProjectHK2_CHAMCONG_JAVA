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
import java.util.Arrays;

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
import javax.swing.border.AbstractBorder;

import com.example.swingapp.dao.AccountDAO;
import com.example.swingapp.model.Account;
import com.example.swingapp.util.PasswordUtils;

public class SignUp extends JFrame {

	private JTextField txtUser;
	private JPasswordField txtPassword;
	private JPasswordField txtRepeatPassword;
	private JButton btnSignUp;
	private JLabel cmdToLogin;

	private final Color PRIMARY = new Color(67, 97, 238);
	private final Color PRIMARY_HOVER = new Color(57, 87, 228);
	private final Color TEXT_LIGHT = new Color(117, 117, 117);
	private final Color CARD_BG = new Color(255, 255, 255, 245);
	private final Color BORDER_DEFAULT = new Color(220, 220, 220);
	private final Color BORDER_FOCUS = PRIMARY;

	public SignUp() {
		initUI();
		setLocationRelativeTo(null);
	}

	private void initUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Đăng ký tài khoản - Hệ thống chấm công");
		setResizable(false);

		var mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(109, 213, 250));

		var card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(450, 650));
		card.setBackground(Color.WHITE);
		card.setBorder(new RoundedShadowBorder(25, 12));
		card.setAlignmentX(Component.CENTER_ALIGNMENT);

		var title = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setForeground(new Color(33, 33, 33));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		var subtitle = new JLabel("Tạo tài khoản mới", SwingConstants.CENTER);
		subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		subtitle.setForeground(TEXT_LIGHT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		var lblUser = new JLabel("Tên đăng nhập");
		lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblUser.setForeground(new Color(33, 33, 33));
		lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtUser = createInputField();

		var lblPass = new JLabel("Mật khẩu");
		lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPass.setForeground(new Color(33, 33, 33));
		lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtPassword = createPasswordField();

		var lblRepeat = new JLabel("Nhập lại mật khẩu");
		lblRepeat.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblRepeat.setForeground(new Color(33, 33, 33));
		lblRepeat.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtRepeatPassword = createPasswordField();

		btnSignUp = new JButton("ĐĂNG KÝ");
		btnSignUp.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnSignUp.setForeground(Color.WHITE);
		btnSignUp.setBackground(PRIMARY);
		btnSignUp.setFocusPainted(false);
		btnSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSignUp.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSignUp.setPreferredSize(new Dimension(380, 55));
		btnSignUp.setMaximumSize(new Dimension(380, 55));
		btnSignUp.addActionListener(e -> handleSignUp());
		btnSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { btnSignUp.setBackground(PRIMARY_HOVER); }
			@Override
			public void mouseExited(MouseEvent e) { btnSignUp.setBackground(PRIMARY); }
		});

		var bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		var lblHave = new JLabel("Đã có tài khoản? ");
		lblHave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblHave.setForeground(TEXT_LIGHT);

		cmdToLogin = new JLabel("Đăng nhập ngay");
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
		card.add(lblUser);
		card.add(Box.createRigidArea(new Dimension(0, 5)));
		card.add(txtUser);
		card.add(Box.createRigidArea(new Dimension(0, 20)));
		card.add(lblPass);
		card.add(Box.createRigidArea(new Dimension(0, 5)));
		card.add(txtPassword);
		card.add(Box.createRigidArea(new Dimension(0, 20)));
		card.add(lblRepeat);
		card.add(Box.createRigidArea(new Dimension(0, 5)));
		card.add(txtRepeatPassword);
		card.add(Box.createRigidArea(new Dimension(0, 40)));
		card.add(btnSignUp);
		card.add(Box.createRigidArea(new Dimension(0, 30)));
		card.add(bottomPanel);
		card.add(Box.createVerticalGlue());

		var gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(card, gbc);

		setContentPane(mainPanel);
		pack();
		setMinimumSize(new Dimension(950, 700));
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
		field.setPreferredSize(new Dimension(380, 50));
		field.setMaximumSize(new Dimension(380, 50));
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

	private void handleSignUp() {
		if (!validateForm()) {
			return;
		}

		var username = txtUser.getText().trim();
		var password = new String(txtPassword.getPassword()).trim();

		var dao = new AccountDAO();
		var accounts = dao.getAll();
		var exists = accounts.stream().anyMatch(a -> a.getAccountName().equalsIgnoreCase(username));

		if (exists) { JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }

		var salt = PasswordUtils.generateSalt();
		var hashed = PasswordUtils.hashPassword(password, salt);

		var acc = new Account();
		acc.setAccountName(username); acc.setPassword(hashed); acc.setSalt(salt);
		acc.setActive(true); acc.setEmployeeId(0); acc.setAuth("USER");

		if (dao.insert(acc)) {
			JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
			new LoginForm().setVisible(true); dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi khi lưu tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean validateForm() {
		if (txtUser.getText().trim().isEmpty() || txtPassword.getPassword().length == 0 || txtRepeatPassword.getPassword().length == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false;
		}
		if (!Arrays.equals(txtPassword.getPassword(), txtRepeatPassword.getPassword())) {
			JOptionPane.showMessageDialog(this, "Mật khẩu không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE); return false;
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
		SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
	}
}
