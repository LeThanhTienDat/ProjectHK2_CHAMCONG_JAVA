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
import javax.swing.JComponent;
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
import javax.swing.border.EmptyBorder;

import com.example.swingapp.helper.RandomStringHelper;
import com.example.swingapp.helper.SendMailHelper;
import com.example.swingapp.model.Account;
import com.example.swingapp.service.AccountService;

public class ConfirmFormPanel extends JFrame {
	private JTextField txtInputConfirm;
	private JPasswordField txtPassword;
	private JButton btnConfirm;
	private JLabel lblSignUp;
	private JLabel lblBackToLogin;
	private String mailFrom;
	private String mailTo;
	private String mailSubject;
	private String mailHtml;
	private Account accountInfo;

	private final RandomStringHelper randomString = new RandomStringHelper();
	private final AccountService accountService = new AccountService();
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

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ConfirmFormPanel(String from, String to,String subject, String html, Account accountInfo) {
		mailFrom = from;
		mailSubject = subject;
		mailTo = to;
		mailHtml = html;
		this.accountInfo = accountInfo;
		initModernComponents();
		setLocationRelativeTo(null);
	}
	private void initModernComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Input Code - Timekeeping System");
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

		var subtitle = new JLabel("Input your confirm code", SwingConstants.CENTER);
		subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		subtitle.setForeground(TEXT_LIGHT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		txtInputConfirm = styledTextField("Input code here",330);
		txtInputConfirm.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtInputConfirm.getText().equals("Input code here")) {
					txtInputConfirm.setText("");
					txtInputConfirm.setForeground(TEXT_PRIMARY);
					txtInputConfirm.setBorder(new RoundedBorder(12, BORDER_FOCUS, 2));
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtInputConfirm.getText().isEmpty()) {
					txtInputConfirm.setText("Input code here");
					txtInputConfirm.setForeground(Color.GRAY);
					txtInputConfirm.setBorder(new RoundedBorder(12, BORDER_DEFAULT, 1));
				}
			}
		});

		btnConfirm = createButton("Confirm",PRIMARY_BLUE,330);
		btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnConfirm.setMaximumSize(new Dimension(330, 55));
		btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnConfirm.addActionListener(e -> handleConfirm(accountInfo));
		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnConfirm.setBackground(PRIMARY_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnConfirm.setBackground(PRIMARY);
			}
		});

		var signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		signUpPanel.setBackground(Color.WHITE);
		signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		signUpPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		setMaxHeightToPreferred(signUpPanel);

		var lblNoAccount = new JLabel("Can't get your code? ");
		lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNoAccount.setForeground(TEXT_LIGHT);

		var lblOr = new JLabel("OR ");
		lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNoAccount.setForeground(TEXT_LIGHT);

		var orPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		orPanel.setBackground(Color.WHITE);
		orPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		orPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		setMaxHeightToPreferred(orPanel);
		orPanel.add(lblOr);


		var backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		backPanel.setBackground(Color.WHITE);
		backPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		backPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		setMaxHeightToPreferred(backPanel);

		var lblBack = new JLabel("Back to Login? ");
		lblBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblBack.setForeground(TEXT_LIGHT);

		lblBackToLogin = new JLabel("Click here");
		lblBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblBackToLogin.setForeground(PRIMARY);
		lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblBackToLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new LoginForm().setVisible(true);
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblBackToLogin.setForeground(PRIMARY_HOVER);
				lblBackToLogin.setText("Click here");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblBackToLogin.setForeground(PRIMARY);
				lblBackToLogin.setText("Click here");
			}
		});

		backPanel.add(lblBack);
		backPanel.add(lblBackToLogin);

		lblSignUp = new JLabel("Re-send new code");
		lblSignUp.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSignUp.setForeground(PRIMARY);
		lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleResend(mailFrom, mailTo, mailSubject, mailHtml, accountInfo);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY_HOVER);
				lblSignUp.setText("<html><u>Re-send new code</u></html>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblSignUp.setForeground(PRIMARY);
				lblSignUp.setText("Re-send new code");
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
		card.add(txtInputConfirm);
		card.add(Box.createRigidArea(new Dimension(0, 20)));
		card.add(btnConfirm);
		card.add(Box.createRigidArea(new Dimension(0, 10)));
		card.add(signUpPanel);
		card.add(Box.createRigidArea(new Dimension(0, 10)));
		card.add(orPanel);
		card.add(Box.createRigidArea(new Dimension(0, 10)));
		card.add(backPanel);
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

	private void handleConfirm(Account accountInfo) {
		var getCode = txtInputConfirm.getText().trim();

		if (getCode.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please enter your reset code!",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var getTokenFromAccount = accountService.getTokenByAccountId(accountInfo.getAccountId());
		if(getTokenFromAccount.equals(getCode)) {
			new ChangePasswordPanel(accountInfo).setVisible(true);
			dispose();
		}else {
			JOptionPane.showMessageDialog(this,
					"Invalid code, please try again!",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		dispose();
	}


	public static void main(String[] args) {
		var mockFrom = "noreply@app.com";
		var mockTo = "test@user.com";
		var mockSubject = "text";
		var mockHtml = "<html>...</html>";
		var mockAccount = new Account();
		SwingUtilities.invokeLater(() -> new ConfirmFormPanel(
				mockFrom,
				mockTo,
				mockSubject,
				mockHtml,
				mockAccount
				).setVisible(true));
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
	private void setMaxHeightToPreferred(JComponent component) {
		var preferred = component.getPreferredSize();
		component.setMaximumSize(new Dimension(component.getMaximumSize().width, preferred.height));
	}
	private void handleResend(String from, String to,String subject, String html, Account AccountInfo) {
		var tokenCode = randomString.RandomString();
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
				ConfirmFormPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				try {
					// Lấy kết quả (true/false) từ doInBackground
					boolean checkSendMail = get();

					// 4. Hiển thị thông báo dựa trên kết quả
					if (checkSendMail) {
						JOptionPane.showMessageDialog(ConfirmFormPanel.this,
								"A Code has been sent to your email, please check your email!",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						var accountId = accountInfo.getAccountId();
						var code = tokenCode;
						var checkInsertCode = accountService.insertResetCode(code, accountId);


					} else {
						JOptionPane.showMessageDialog(ConfirmFormPanel.this,
								"There are something wrong when send EMail, please try again!",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(ConfirmFormPanel.this,
							"An error occurred: " + ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
	}

}

