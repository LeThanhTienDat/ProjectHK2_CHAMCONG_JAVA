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
import java.text.MessageFormat;

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

import com.example.swingapp.model.OTType;
import com.example.swingapp.service.OTTypeService;
import com.example.swingapp.util.DBConnection;

@SuppressWarnings("serial")
public class OvertimeAdminPanel extends JPanel {

	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private OvertimeFormPanel formPanel;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public OvertimeAdminPanel() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 15));

		add(createSearchPanel(), BorderLayout.NORTH);

		var content = new JPanel(new BorderLayout(0, 15));
		content.setBackground(BG_LIGHT);
		formPanel = new OvertimeFormPanel();
		formPanel.setOnSaveListener(this::onSave);
		formPanel.setOnCancelListener(this::onCancel);
		formPanel.setVisible(false);
		content.add(formPanel, BorderLayout.NORTH);

		content.add(createTableCard(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);

		var actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actions.setBackground(BG_LIGHT);
		var btnDelete = createButton("Delete", DANGER_RED, 110);
		btnDelete.addActionListener(e -> deleteRow());
		var btnPDF = createButton("Export PDF", TEAL, 110);
		btnPDF.addActionListener(e -> printPDF());
		actions.add(btnDelete);
		//		actions.add(btnPDF);
		add(actions, BorderLayout.SOUTH);
	}

	private JPanel createSearchPanel() {
		var p = new JPanel();
		p.setBackground(CARD_WHITE);
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		p.setPreferredSize(new Dimension(0, 70));
		p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));

		txtSearch = styledField("Search OT type...", 400);
		txtSearch.setColumns(30);
		p.add(txtSearch);

		var btnSearch = createButton("Search", PRIMARY_BLUE, 110);
		btnSearch.addActionListener(e -> search());
		p.add(btnSearch);

		btnAdd = createButton("+ Add new", ACCENT_BLUE, 110);
		btnAdd.addActionListener(e -> addNew());
		p.add(btnAdd);
		return p;
	}

	private JPanel createTableCard() {
		var card = new JPanel(new BorderLayout());
		card.setBackground(CARD_WHITE);
		card.setBorder(new EmptyBorder(15, 15, 15, 15));

		var header = new JLabel("OVERTIME LIST");
		header.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 15, 0));

		String[] cols = { "ID", "OT Type Name", "Start Time", "EndTime", "rawId" };
		model = new DefaultTableModel(cols, 0);
		loadOtData("");
		table = new JTable(model);
		styleTable(table);
		table.removeColumn(table.getColumnModel().getColumn(4));

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				var r = table.getSelectedRow();
				if (r != -1) {
					formPanel.setEditMode(r, getRow(r));
					formPanel.setVisible(true);
					btnAdd.setVisible(false);
					formPanel.focusFirst();
				}
			}
		});

		var sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);

		card.add(header, BorderLayout.NORTH);
		card.add(sp, BorderLayout.CENTER);
		return card;
	}

	private JTextField styledField(String placeholder, int w) {
		var f = new JTextField(placeholder);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		f.setPreferredSize(new Dimension(w, 36));
		return f;
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
		var h = t.getTableHeader();
		h.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		h.setBackground(PRIMARY_BLUE);
		h.setForeground(Color.WHITE);
		h.setOpaque(true);
		h.setPreferredSize(new Dimension(0, 45));
		DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {

				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, col);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setForeground(Color.WHITE);
				lbl.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f)); // ✅ ép font đậm thật sự
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setOpaque(true);
				return lbl;
			}
		};
		for (var i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setHeaderRenderer(hr);
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

	private Object[] getRow(int r) {
		var d = new Object[model.getColumnCount()];
		for (var i = 0; i < d.length; i++) {
			d[i] = model.getValueAt(r, i);
		}
		return d;
	}

	private void search() {
		var q = txtSearch.getText().trim();
		if (q.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a search keyword!", "Notification",
					JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Searching: " + q + " (Demo)", "Search",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addNew() {
		formPanel.setAddMode(true);
		formPanel.setVisible(true);
		btnAdd.setVisible(false);
		formPanel.focusFirst();
	}

	private void deleteRow() {
		var r = table.getSelectedRow();
		if (r != -1) {
			var rowId = (int)model.getValueAt(r, 4);
			var cf = JOptionPane.showConfirmDialog(this, "Delete this OT record?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (cf == JOptionPane.YES_OPTION) {
				var ottService = new OTTypeService();
				var checkDel = ottService.delete(rowId);
				if(checkDel) {
					loadOtData("");
					formPanel.setVisible(false);
					btnAdd.setVisible(true);
					JOptionPane.showMessageDialog(this, "Deleted successfully!", "Notification", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void printPDF() {
		try {
			var h = new MessageFormat("OVERTIME LIST");
			var f = new MessageFormat("Page {0}");
			table.print(JTable.PrintMode.FIT_WIDTH, h, f);
			JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onSave(ActionEvent e) {
		var cmd = e.getActionCommand();
		var data = formPanel.getFormData();
		var otService = new OTTypeService();

		if ("add".equals(cmd)) {

			var s = new OTType();
			s.setOtName((String) data[1]);
			s.setOtStart(java.sql.Time.valueOf(((String) data[2])));
			s.setOtEnd(java.sql.Time.valueOf(((String) data[3])));

			var checkAdd = otService.add(s);

			if(checkAdd) {
				loadOtData("");
				JOptionPane.showMessageDialog(this, "Overtime added successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Error while adding overtime!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			var r = formPanel.getEditingRow();
			if (r != -1) {
				var rowId = (int)model.getValueAt(r, 4);
				var o = new OTType();
				o.setId(rowId);
				o.setOtName((String) data[1]);
				o.setOtStart(java.sql.Time.valueOf(((String) data[2])));
				o.setOtEnd(java.sql.Time.valueOf(((String) data[3])));

				var checkEdit = otService.update(o);

				if(checkEdit) {
					loadOtData("");
					JOptionPane.showMessageDialog(this, "Updated successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(this, "Update failed!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}

	private void onCancel(ActionEvent e) {
		formPanel.setVisible(false);
		btnAdd.setVisible(true);
	}

	private void loadOtData(String keyword) {
		var sql = """
				    SELECT *
				    FROM dbo.tbl_Ot_Type ott
				    where active = 1
				""";

		try (var conn = DBConnection.getConnection();
				var stmt = conn.prepareStatement(sql)) {

			var rs = stmt.executeQuery();
			model.setRowCount(0);
			while (rs.next()) {
				var row = new Object[5];
				row[0] = "OT" + String.format("%03d", rs.getInt("id"));
				row[1] = rs.getString("ot_name");
				row[2] = rs.getTime("ot_start");
				row[3] = rs.getTime("ot_end");
				row[4] = rs.getInt("id");
				model.addRow(row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed loading data: " + ex.getMessage());
		}
	}
}
