package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.service.RestaurantService;
import com.example.swingapp.util.DBConnection;

public class RestaurantAdminPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private DefaultTableModel model;
	private JTable tableRestaurant;
	private JTextField txtSearch;
	private JButton btnAdd;
	private JPanel tableCard;
	private RestaurantFormPanel formPanel;

	// ==== COLORS ====
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public RestaurantAdminPanel() {
		this(null, null);
	}

	public RestaurantAdminPanel(java.awt.event.ActionListener saveListener, java.awt.event.ActionListener cancelListener) {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));

		// =============== SEARCH PANEL ===============
		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setPreferredSize(new Dimension(0, 70));

		txtSearch = new JTextField("Search for restaurant name...");
		txtSearch.setPreferredSize(new Dimension(400, 36));
		txtSearch.setColumns(30);
		txtSearch.setBackground(new Color(248, 250, 252));
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setForeground(TEXT_PRIMARY);
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)
				));
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals("Search for restaurant name...")) {
					txtSearch.setText("");
					txtSearch.setForeground(TEXT_PRIMARY);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText("Search for restaurant name...");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});
		txtSearch.setOpaque(true);

		var btnSearch = createButton("Search", PRIMARY_BLUE, 110);
		btnAdd = createButton("+ Add new", ACCENT_BLUE, 110);
		btnSearch.addActionListener(e -> searchRestaurant());
		btnAdd.addActionListener(e -> addRestaurant());

		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		searchPanel.add(btnAdd);
		add(searchPanel, BorderLayout.NORTH);

		// =============== CONTENT PANEL ===============
		var contentPanel = new JPanel(new BorderLayout(0, 15));
		contentPanel.setBackground(BG_LIGHT);

		formPanel = new RestaurantFormPanel(this::handleFormSave, this::handleFormCancel);
		formPanel.setVisible(false);
		contentPanel.add(formPanel, BorderLayout.NORTH);

		tableCard = new JPanel(new BorderLayout());
		tableCard.setBackground(CARD_WHITE);
		tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("RESTAURANT LIST", SwingConstants.LEFT);
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		tableCard.add(header, BorderLayout.NORTH);

		String[] cols = {
				"Restaurant ID", "Restaurant Name", "Street","Total Employees","rawId","streetId"
		};
		model = new DefaultTableModel(cols, 0);
		loadRestaurantData();
		tableRestaurant = new JTable(model);
		styleTable(tableRestaurant);
		setupColumnWidths(tableRestaurant);
		tableRestaurant.removeColumn(tableRestaurant.getColumnModel().getColumn(4));
		tableRestaurant.removeColumn(tableRestaurant.getColumnModel().getColumn(4));

		tableRestaurant.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var row = tableRestaurant.getSelectedRow();
				if (row != -1) {
					formPanel.setEditMode(row, getRowData(row));
					formPanel.setVisible(true);
					tableCard.setVisible(false);
					btnAdd.setVisible(false);
					formPanel.focusNameField();
				}
			}
		});

		var scroll = new JScrollPane(tableRestaurant);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		scroll.getViewport().setBackground(CARD_WHITE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableCard.add(scroll, BorderLayout.CENTER);

		contentPanel.add(tableCard, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		// =============== ACTION BUTTONS ===============
		var actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actionPanel.setBackground(BG_LIGHT);

		var btnDelete = createButton("Delete", DANGER_RED, 110);
		btnDelete.addActionListener(e -> deleteRestaurant());
		var btnManager = createButton("Manage Employees", PRIMARY_BLUE, 160);
		btnManager.addActionListener(e -> manageEmployees());
		var btnPDF = createButton("Export PDF", TEAL, 120);
		btnPDF.addActionListener(e -> printPDF());

		actionPanel.add(btnDelete);
		actionPanel.add(btnManager);
		actionPanel.add(btnPDF);
		add(actionPanel, BorderLayout.SOUTH);
	}

	private void loadRestaurantData() {
		var keyword = txtSearch.getText().trim();
		if (keyword.isEmpty() || "Search for restaurant name...".equals(keyword)) {
			keyword = "";
		}
		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall("{CALL SP_GetRestaurant (?)}")) {
			stmt.setString(1, keyword);
			var rs = stmt.executeQuery();
			model.setRowCount(0);

			while (rs.next()) {
				var row = new Object[6];
				row[0] = "NH" + String.format("%03d", rs.getInt("id"));
				row[1] = rs.getString("name");
				row[2] = rs.getString("street_name");
				row[3] = rs.getInt("tongsonhanvien");
				row[4] = rs.getInt("id");
				row[5] = rs.getInt("street_id");
				model.addRow(row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
		}
	}

	private void setupColumnWidths(JTable table) {
		int[] widths = {80, 180, 150, 120};
		for (var i = 0; i < widths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	public static JButton createButton(String text, Color bg, int w) {
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


	private void styleTable(JTable t) {
		// --- Header ---
		var header = t.getTableHeader();
		header.setPreferredSize(new Dimension(0, 45));
		header.setReorderingAllowed(false);
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));

		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				var lbl = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);

				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
				lbl.setOpaque(true);
				lbl.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

				return lbl;
			}
		};
		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}

		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(45);
		t.setSelectionBackground(new Color(232, 240, 254));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(new Color(220, 220, 220));
		t.setShowVerticalLines(true);
		t.setShowHorizontalLines(true);
		t.getTableHeader().setPreferredSize(new Dimension(0, 45));
		t.getTableHeader().setReorderingAllowed(false);

		var center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(center);
		}
	}


	// ======================= ACTION HANDLERS =======================
	private void addRestaurant() {
		formPanel.setAddMode(true);
		formPanel.setVisible(true);
		tableCard.setVisible(false);
		btnAdd.setVisible(false);
		formPanel.focusNameField();
	}

	private void handleFormSave(ActionEvent e) {
		var command = e.getActionCommand();
		var data = formPanel.getFormData();

		try (var conn = DBConnection.getConnection()) {
			var resService = new RestaurantService();
			if ("add".equals(command)) {
				var r = new Restaurant();
				r.setName((String) data[1]);
				r.setStreetId((int) data[2]);
				var checkAdd = resService.add(r);
				if(checkAdd) {
					JOptionPane.showMessageDialog(this, "New restaurant added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
			} else if ("update".equals(command)) {
				var row = tableRestaurant.getSelectedRow();
				var rowId = (int)model.getValueAt(row, 4);
				var r = new Restaurant();
				r.setId(rowId);
				r.setName((String) data[1]);
				r.setStreetId((int) data[2]);
				var checkEdit = resService.update(r);
				if(checkEdit) {
					JOptionPane.showMessageDialog(this, "Restaurant updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
			}



		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		formPanel.setVisible(false);
		tableCard.setVisible(true);
		btnAdd.setVisible(true);
		loadRestaurantData();
	}

	private void handleFormCancel(ActionEvent e) {
		formPanel.setVisible(false);
		tableCard.setVisible(true);
		btnAdd.setVisible(true);
	}

	private Object[] getRowData(int row) {
		var d = new Object[model.getColumnCount()];
		for (var i = 0; i < d.length; i++) {
			d[i] = model.getValueAt(row, i);
		}
		return d;
	}

	private void searchRestaurant() {
		loadRestaurantData();

	}

	private void deleteRestaurant() {
		try {
			var row = tableRestaurant.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Please select a restaurant to delete!");
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "Delete this restaurant?", "Confirm",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				var rowId = (int)model.getValueAt(row, 4);
				var resService = new RestaurantService();
				var checkDel = resService.delete(rowId);
				if(checkDel) {
					JOptionPane.showMessageDialog(this, "Restaurant deleted successfully!");
				}
				formPanel.setVisible(false);
				tableCard.setVisible(true);
				loadRestaurantData();
			}
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "Error " + ex.getMessage());
		}
	}

	private void manageEmployees() {
		JOptionPane.showMessageDialog(this, "Restaurant employee management feature (demo)");
	}

	private void printPDF() {
		try {
			tableRestaurant.print();
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage());
		}
	}
}
