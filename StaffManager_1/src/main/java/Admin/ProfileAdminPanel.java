package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.example.swingapp.ui.LoginForm;

public class ProfileAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Color CARD_BG = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color HOVER_BLUE = new Color(227, 242, 253);

	private JButton logoutBtn;

	// ✅ Constructor rỗng để WindowBuilder có thể mở được
	public ProfileAdminPanel() {
		initProfileUI();
	}

	private void initProfileUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(CARD_BG);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		var headerPanel = createProfileHeader();
		add(headerPanel);
		add(Box.createRigidArea(new Dimension(0, 30)));

		var infoPanel = createProfileInfo();
		add(infoPanel);
		add(Box.createRigidArea(new Dimension(0, 30)));

		var logoutWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		logoutWrapper.setOpaque(false);
		logoutBtn = createLogoutButton();
		logoutWrapper.add(logoutBtn);
		add(logoutWrapper);
	}

	private JPanel createProfileHeader() {
		var header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		var avatar = new JLabel("👤");
		avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
		avatar.setHorizontalAlignment(SwingConstants.CENTER);

		var nameLabel = new JLabel("Quản Trị Viên Chính");
		nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
		nameLabel.setForeground(TEXT_PRIMARY);
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

		var roleLabel = new JLabel("Admin - Quản lý hệ thống");
		roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		roleLabel.setForeground(TEXT_SECONDARY);
		roleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		var titleSection = new JPanel();
		titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
		titleSection.setOpaque(false);
		titleSection.add(nameLabel);
		titleSection.add(Box.createRigidArea(new Dimension(0, 5)));
		titleSection.add(roleLabel);

		header.add(avatar, BorderLayout.NORTH);
		header.add(titleSection, BorderLayout.CENTER);
		return header;
	}

	private JPanel createProfileInfo() {
		var infoContainer = new JPanel();
		infoContainer.setLayout(new BoxLayout(infoContainer, BoxLayout.Y_AXIS));
		infoContainer.setOpaque(false);
		infoContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

		var emailPanel = createInfoRow("📧 Email:", "admin@restaurant.com");
		infoContainer.add(emailPanel);

		var separator1 = new JSeparator(SwingConstants.HORIZONTAL);
		separator1.setForeground(new Color(224, 235, 250));
		infoContainer.add(separator1);

		var phonePanel = createInfoRow("📱 Số điện thoại:", "+84 123 456 789");
		infoContainer.add(phonePanel);

		var separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		separator2.setForeground(new Color(224, 235, 250));
		infoContainer.add(separator2);

		var joinedPanel = createInfoRow("📅 Ngày tham gia:", "01/01/2023");
		infoContainer.add(joinedPanel);

		return infoContainer;
	}

	private JPanel createInfoRow(String labelText, String valueText) {
		var row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setBorder(new EmptyBorder(15, 0, 15, 0));

		var label = new JLabel(labelText);
		label.setFont(new Font("Segoe UI", Font.BOLD, 14));
		label.setForeground(TEXT_PRIMARY);
		label.setPreferredSize(new Dimension(150, 20));

		var value = new JLabel(valueText);
		value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		value.setForeground(TEXT_SECONDARY);

		row.add(label, BorderLayout.WEST);
		row.add(value, BorderLayout.CENTER);
		return row;
	}

	private JButton createLogoutButton() {
		var btn = new JButton("Đăng Xuất");
		btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btn.setForeground(Color.WHITE);
		btn.setBackground(PRIMARY_BLUE);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(200, 50));

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(HOVER_BLUE);
				btn.setForeground(TEXT_PRIMARY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(PRIMARY_BLUE);
				btn.setForeground(Color.WHITE);
			}
		});

		btn.addActionListener(e -> {
			if (JOptionPane.showConfirmDialog(ProfileAdminPanel.this, "Bạn có chắc chắn muốn đăng xuất?",
					"Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				new LoginForm().setVisible(true);
				SwingUtilities.getWindowAncestor(ProfileAdminPanel.this).dispose();
			}
		});
		return btn;
	}
}
