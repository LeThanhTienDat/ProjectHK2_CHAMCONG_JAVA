package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
		setLayout(new BorderLayout(0, 20));

		// =============== SEARCH PANEL ===============
		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		searchPanel.setBackground(CARD_WHITE);
		searchPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		searchPanel.setPreferredSize(new Dimension(0, 70));

		txtSearch = new JTextField("Tìm kiếm nhà hàng...");
		txtSearch.setColumns(30);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSearch.setForeground(Color.GRAY);
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals("Tìm kiếm nhà hàng...")) {
					txtSearch.setText("");
					txtSearch.setForeground(TEXT_PRIMARY);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText("Tìm kiếm nhà hàng...");
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});

		var btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 120);
		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 130);
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
		tableCard.setBorder(new EmptyBorder(20, 25, 20, 25));

		var header = new JLabel("DANH SÁCH NHÀ HÀNG", SwingConstants.LEFT);
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));
		tableCard.add(header, BorderLayout.NORTH);

		String[] cols = {
				"Mã NH", "Tên nhà hàng", "Đường","Tổng nhân viên","rawId","streetId"
		};
		model = new DefaultTableModel(cols, 0);
		loadRestaurantData("");
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

		var btnDelete = createButton("Xóa", DANGER_RED, 110);
		btnDelete.addActionListener(e -> deleteRestaurant());
		var btnManager = createButton("Quản Lý Nhân Viên", PRIMARY_BLUE, 160);
		btnManager.addActionListener(e -> manageEmployees());
		var btnPDF = createButton("Xuất PDF", TEAL, 120);
		btnPDF.addActionListener(e -> printPDF());

		actionPanel.add(btnDelete);
		actionPanel.add(btnManager);
		actionPanel.add(btnPDF);
		add(actionPanel, BorderLayout.SOUTH);
	}

	private void loadRestaurantData(String value) {
		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareCall("{CALL SP_GetRestaurant}")) {
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
			JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu: " + ex.getMessage());
		}
	}

	private void setupColumnWidths(JTable table) {
		int[] widths = {80, 180, 150, 120};
		for (var i = 0; i < widths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	private static JButton createButton(String text, Color color, int width) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(color);
		b.setPreferredSize(new Dimension(width, 38));
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return b;
	}

	private void styleTable(JTable t) {
		// --- Header ---
		var header = t.getTableHeader();
		header.setPreferredSize(new Dimension(0, 45));
		header.setReorderingAllowed(false);
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));

		// Renderer cho header
		var headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		headerRenderer.setForeground(Color.WHITE);
		headerRenderer.setBackground(PRIMARY_BLUE);
		headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
		headerRenderer.setOpaque(true);
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
					JOptionPane.showMessageDialog(this, "Thêm nhà hàng mới thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
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
					JOptionPane.showMessageDialog(this, "Cập nhật nhà hàng thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
				}
			}



		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}

		formPanel.setVisible(false);
		tableCard.setVisible(true);
		btnAdd.setVisible(true);
		loadRestaurantData("");
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
		loadRestaurantData(txtSearch.getText().trim());

	}

	private void deleteRestaurant() {
		try {
			var row = tableRestaurant.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà hàng cần xóa!");
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "Xóa nhà hàng này?", "Xác nhận",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				var rowId = (int)model.getValueAt(row, 4);
				var resService = new RestaurantService();
				var checkDel = resService.delete(rowId);
				if(checkDel) {
					JOptionPane.showMessageDialog(this, "Đã xóa nhà hàng thành công!");
				}
				formPanel.setVisible(false);
				tableCard.setVisible(true);
				loadRestaurantData("");
			}
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
		}
	}

	private void manageEmployees() {
		JOptionPane.showMessageDialog(this, "Chức năng quản lý nhân viên của nhà hàng (demo)");
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
