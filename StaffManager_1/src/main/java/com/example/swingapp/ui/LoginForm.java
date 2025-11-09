package com.example.swingapp.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.SwingUtilities;

import com.example.swingapp.dao.AccountDAO;
import com.example.swingapp.util.PasswordUtils;

public class LoginForm extends JFrame {

	private JTextField txtUser;
	private JPasswordField txtPassword;
	private JButton btnLogin;
	private JLabel lblSignUp;

	private final Color PRIMARY = new Color(67, 97, 238);
	private final Color PRIMARY_HOVER = new Color(57, 87, 228);
	private final Color TEXT_LIGHT = new Color(117, 117, 117);
	private final Color BORDER_DEFAULT = new Color(200, 200, 200);

	public LoginForm() {
		initModernComponents();
		setLocationRelativeTo(null);
	}

	private void initModernComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Đăng Nhập - Hệ Thống Chấm Công");
		setResizable(false);
		setMinimumSize(new Dimension(950, 700));

		var mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(240, 240, 240));

		var card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(450, 580));
		card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
		card.setAlignmentX(Component.CENTER_ALIGNMENT);

		var title = new JLabel("HỆ THỐNG CHẤM CÔNG", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setForeground(new Color(33, 33, 33));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		var subtitle = new JLabel("Đăng nhập để bắt đầu chấm công của bạn", SwingConstants.CENTER);
		subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		subtitle.setForeground(TEXT_LIGHT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		txtUser = new JTextField();
		styleTextField(txtUser, "Tên đăng nhập");

		txtPassword = new JPasswordField();
		styleTextField(txtPassword, "Mật khẩu");

		btnLogin = new JButton("ĐĂNG NHẬP");
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setBackground(PRIMARY);
		btnLogin.setFocusPainted(false);
		btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogin.setPreferredSize(new Dimension(380, 55));
		btnLogin.setMaximumSize(new Dimension(380, 55));
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

		var lblNoAccount = new JLabel("Chưa có tài khoản? ");
		lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNoAccount.setForeground(TEXT_LIGHT);

		lblSignUp = new JLabel("Đăng ký ngay");
		lblSignUp.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSignUp.setForeground(PRIMARY);
		lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new SignUp().setVisible(true);
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY_HOVER);
				lblSignUp.setText("<html><u>Đăng ký ngay</u></html>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY);
				lblSignUp.setText("Đăng ký ngay");
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

	private void styleTextField(JTextField field, String placeholder) {
		field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		field.setPreferredSize(new Dimension(380, 50));
		field.setMaximumSize(new Dimension(380, 50));
		field.setAlignmentX(Component.CENTER_ALIGNMENT);
		field.setBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1, true));
		field.setToolTipText(placeholder);
		field.setCaretColor(PRIMARY);
	}

	private void handleLogin() {
		var username = txtUser.getText().trim();
		var password = new String(txtPassword.getPassword());

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng nhập đầy đủ tài khoản và mật khẩu!",
					"Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		var dao = new AccountDAO();
		var account = dao.findByUsername(username); // phương thức đơn giản hơn

		if (account == null) {
			JOptionPane.showMessageDialog(this,
					"Tài khoản không tồn tại!",
					"Lỗi đăng nhập",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Hash mật khẩu người dùng nhập vào
		var hashInput = PasswordUtils.hashPassword(password, account.getSalt());

		if (!hashInput.equals(account.getPassword())) {
			JOptionPane.showMessageDialog(this,
					"Sai mật khẩu!",
					"Lỗi đăng nhập",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Đăng nhập thành công
		JOptionPane.showMessageDialog(this,
				"Đăng nhập thành công!",
				"Thành công",
				JOptionPane.INFORMATION_MESSAGE);

		if ("ADMIN".equalsIgnoreCase(account.getAuth())) {
			new Admin.Admin().setVisible(true);
		} else {
			new JFrame("Trang nhân viên").setVisible(true);
		}

		dispose();
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
	}
}
