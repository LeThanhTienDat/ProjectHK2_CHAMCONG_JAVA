package Admin;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SettingAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public SettingAdminPanel() {
		if (!java.beans.Beans.isDesignTime()) {
			initUI();
		} else {
			// placeholder để WindowBuilder nhận diện layout
			setLayout(new BorderLayout());
			add(new JPanel());
		}
	}

	private void initUI() {
		setLayout(new BorderLayout());
		var label = new JLabel("Cài Đặt Hệ Thống", SwingConstants.CENTER);
		label.setFont(new Font("Segoe UI", Font.BOLD, 22));
		add(label, BorderLayout.CENTER);
	}
}
