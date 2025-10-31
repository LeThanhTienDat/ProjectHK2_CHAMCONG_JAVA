// File: Admin.java
package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Admin extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JPanel currentView;
	private SidebarButton selectedButton;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color LIGHT_BLUE = new Color(66, 165, 245);
	private static final Color ACCENT_BLUE = new Color(41, 182, 246);
	private static final Color BG_WHITE = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color HOVER_BLUE = new Color(227, 242, 253);
	private static final Color SIDEBAR_BG_START = new Color(240, 244, 249);
	private static final Color SIDEBAR_BG_END = new Color(228, 236, 245);

	private static final Class<?>[] PANELS = { OverviewAdminPanel.class, EmployeeAdminPanel.class,
			RestaurantAdminPanel.class, AttendanceAdminPanel.class, ShiftAdminPanel.class, ContractAdminPanel.class,
			OvertimeAdminPanel.class, PayrollAdminPanel.class, ReportAdminPanel.class, ProfileAdminPanel.class,
			SettingAdminPanel.class };



	private static final String[] PANEL_TITLES = { "Overview", "Employees", "Restaurants", "Attendance", "Shifts",
			"Contracts", "Overtime", "Payroll Summary", "Reports & Statistics", "Profiles", "System Settings" };

	public Admin() {
		initModernUI();
	}

	private void initModernUI() {
		setTitle("Admin Panel - Restaurant Management System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1400, 850);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1200, 700));

		var mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(BG_WHITE);

		var headerPanel = createModernHeaderPanel();
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		var bodyContainer = new JPanel(new BorderLayout());
		bodyContainer.setBackground(BG_WHITE);

		var sidebar = createModernSidebar();
		bodyContainer.add(sidebar, BorderLayout.WEST);

		var contentWrapper = new JPanel(new BorderLayout());
		contentWrapper.setBackground(BG_WHITE);
		contentWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));

		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(true);
		contentPanel.setBackground(CARD_WHITE);
		contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

		switchView(new OverviewAdminPanel(), PANEL_TITLES[0]);

		contentWrapper.add(contentPanel, BorderLayout.CENTER);
		bodyContainer.add(contentWrapper, BorderLayout.CENTER);
		mainPanel.add(bodyContainer, BorderLayout.CENTER);

		var menuBar = createModernMenuBar();
		setJMenuBar(menuBar);

		setContentPane(mainPanel);
	}

	// ===== HEADER =====
	private JPanel createModernHeaderPanel() {
		var header = new JPanel(new BorderLayout());
		header.setPreferredSize(new Dimension(0, 85));
		header.setBorder(new EmptyBorder(0, 35, 0, 35));
		header.setBackground(PRIMARY_BLUE);

		var titleLabel = new JLabel("ADMIN PANEL");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titleLabel.setForeground(Color.WHITE);

		var subtitleLabel = new JLabel("Restaurant Management System");
		subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitleLabel.setForeground(new Color(255, 255, 255, 190));

		var titleSection = new JPanel();
		titleSection.setOpaque(false);
		titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
		titleSection.add(titleLabel);
		titleSection.add(Box.createRigidArea(new Dimension(0, 4)));
		titleSection.add(subtitleLabel);

		var infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		infoPanel.setOpaque(false);
		infoPanel.add(createClickableInfoBadge("Quản Trị Viên", new Color(255, 255, 255, 25)));

		header.add(titleSection, BorderLayout.WEST);
		header.add(infoPanel, BorderLayout.EAST);
		return header;
	}

	private JLabel createClickableInfoBadge(String text, Color bgColor) {
		var badge = new JLabel(text);
		badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
		badge.setForeground(Color.WHITE);
		badge.setOpaque(true);
		badge.setBackground(bgColor);
		badge.setBorder(new EmptyBorder(8, 20, 8, 20));
		badge.setCursor(new Cursor(Cursor.HAND_CURSOR));
		badge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					var panel = (JPanel) PANELS[9].getDeclaredConstructor().newInstance();
					switchView(panel, PANEL_TITLES[9]);
					if (selectedButton != null) {
						selectedButton.setSelected(false);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Admin.this, "Lỗi tải profile: " + ex.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		return badge;
	}


	private JPanel createModernSidebar() {
		var sidebar = new JPanel(new GridBagLayout());
		sidebar.setPreferredSize(new Dimension(280, 0));
		sidebar.setBorder(new EmptyBorder(25, 0, 25, 0));
		sidebar.setBackground(SIDEBAR_BG_START);

		var gbcTitle = new GridBagConstraints();
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 0;
		gbcTitle.weightx = 1;
		gbcTitle.fill = GridBagConstraints.HORIZONTAL;
		gbcTitle.anchor = GridBagConstraints.NORTH;

		var sidebarTitle = new JLabel("MAIN MENU");
		sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		sidebarTitle.setForeground(TEXT_SECONDARY);
		sidebarTitle.setBorder(new EmptyBorder(10, 25, 15, 25));
		sidebar.add(sidebarTitle, gbcTitle);

		// --- Divider ---
		var gbcDivider = (GridBagConstraints) gbcTitle.clone();
		gbcDivider.gridy = 1;
		var divider = new JSeparator();
		divider.setForeground(BORDER_COLOR);
		sidebar.add(divider, gbcDivider);

		// --- Buttons ---
		String[] buttonTexts = { "Overview", "Employees", "Restaurants", "Attendance", "Shifts",
				"Contracts", "Overtime", "Payroll Summary", "Reports & Statistics", "Profiles", "System Settings" };

		var sidebarButtons = new SidebarButton[PANELS.length];
		var row = 2; // bắt đầu từ hàng thứ 2

		for (var i = 0; i < PANELS.length; i++) {
			final var index = i;
			var gbcButton = new GridBagConstraints();
			gbcButton.gridx = 0;
			gbcButton.gridy = row++;
			gbcButton.weightx = 1;
			gbcButton.fill = GridBagConstraints.HORIZONTAL;
			gbcButton.anchor = GridBagConstraints.NORTH;

			var btn = createSidebarButton(buttonTexts[i], i == 0);
			sidebarButtons[i] = btn;

			btn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						var panel = (JPanel) PANELS[index].getDeclaredConstructor().newInstance();
						switchView(panel, PANEL_TITLES[index]);
						updateSelectedButton(btn);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(Admin.this, "Lỗi tải panel: " + ex.getMessage(), "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			sidebar.add(btn, gbcButton);
		}

		var gbcFiller = new GridBagConstraints();
		gbcFiller.gridx = 0;
		gbcFiller.gridy = row;
		gbcFiller.weighty = 1;
		gbcFiller.fill = GridBagConstraints.VERTICAL;
		var filler = new JPanel();
		filler.setOpaque(false);
		sidebar.add(filler, gbcFiller);

		selectedButton = sidebarButtons[0];
		return sidebar;
	}

	private SidebarButton createSidebarButton(String text, boolean selected) {
		var btn = new SidebarButton(text, selected);
		btn.setAlignmentX(Component.LEFT_ALIGNMENT);
		btn.setBorder(new EmptyBorder(12, 25, 12, 25));
		return btn;
	}

	private void updateSelectedButton(SidebarButton newSelected) {
		if (selectedButton != null) {
			selectedButton.setSelected(false);
		}
		selectedButton = newSelected;
		selectedButton.setSelected(true);
	}

	private void switchView(JPanel newView, String title) {
		contentPanel.removeAll();
		var titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);
		titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

		var contentTitle = new JLabel(title);
		contentTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		contentTitle.setForeground(TEXT_PRIMARY);

		titlePanel.add(contentTitle, BorderLayout.WEST);
		contentPanel.add(titlePanel, BorderLayout.NORTH);
		contentPanel.add(newView, BorderLayout.CENTER);
		currentView = newView;
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private class SidebarButton extends JPanel {
		private static final long serialVersionUID = 1L;
		private boolean selected;
		private boolean hovered;
		private JLabel label;

		public SidebarButton(String text, boolean selected) {
			this.selected = selected;
			setLayout(new BorderLayout());
			setOpaque(true);
			setBackground(selected ? ACCENT_BLUE : BG_WHITE);
			setMaximumSize(new Dimension(280, 50));
			setPreferredSize(new Dimension(280, 50));
			setCursor(new Cursor(Cursor.HAND_CURSOR));

			label = new JLabel(text);
			label.setFont(new Font("Segoe UI", Font.BOLD, 14));
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setForeground(selected ? Color.WHITE : TEXT_PRIMARY);
			add(label, BorderLayout.CENTER);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					hovered = true;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					hovered = false;
					repaint();
				}
			});
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (selected) {
				setBackground(ACCENT_BLUE);
				label.setForeground(Color.WHITE);
			} else if (hovered) {
				setBackground(HOVER_BLUE);
				label.setForeground(TEXT_PRIMARY);
			} else {
				setBackground(BG_WHITE);
				label.setForeground(TEXT_PRIMARY);
			}
		}
	}

	// ===== MENU =====
	private JMenuBar createModernMenuBar() {
		var menuBar = new JMenuBar();
		menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
		menuBar.setPreferredSize(new Dimension(0, 45));

		var fileMenu = createStyledMenu("File");
		var exitItem = createStyledMenuItem("Thoát", Color.RED);
		exitItem.addActionListener(e -> System.exit(0));
		fileMenu.add(exitItem);

		var toolsMenu = createStyledMenu("Công Cụ");
		var refreshItem = createStyledMenuItem("Làm Mới Dữ Liệu", new Color(76, 175, 80));
		refreshItem.addActionListener(e -> {
			if (currentView != null) {
				currentView.revalidate();
				currentView.repaint();
			}
		});
		toolsMenu.add(refreshItem);

		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		return menuBar;
	}

	private JMenu createStyledMenu(String text) {
		var menu = new JMenu(text);
		menu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		menu.setForeground(TEXT_PRIMARY);
		menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return menu;
	}

	private JMenuItem createStyledMenuItem(String text, Color color) {
		var item = new JMenuItem(text);
		item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		item.setForeground(color);
		item.setBackground(Color.WHITE);
		item.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return item;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			new Admin().setVisible(true);
		});
	}
}
