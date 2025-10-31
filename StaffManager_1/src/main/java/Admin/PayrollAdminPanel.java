package Admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

@SuppressWarnings("serial")
public class PayrollAdminPanel extends JPanel {
	private DefaultTableModel model;
	private JTable table;
	private JTextField txtSearch;
	private JButton btnAdd;
	private PayrollFormPanel formPanel;

	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color ACCENT_BLUE = new Color(33, 150, 243);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color TEAL = new Color(0, 150, 136);

	public PayrollAdminPanel() {
		if (!java.beans.Beans.isDesignTime()) {
			initUI();
		} else {
			// WindowBuilder placeholder
			setLayout(new BorderLayout());
			add(new JPanel());
		}
	}

	private void initUI() {
		setBackground(BG_LIGHT);
		setLayout(new BorderLayout(0, 20));

		add(createSearchPanel(), BorderLayout.NORTH);

		JPanel content = new JPanel(new BorderLayout(0, 15));
		content.setBackground(BG_LIGHT);

		formPanel = new PayrollFormPanel(e -> onSave(e), e -> onCancel(e));
		formPanel.setVisible(false);
		content.add(formPanel, BorderLayout.NORTH);

		content.add(createTableCard(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		actions.setBackground(BG_LIGHT);
		JButton btnDelete = createButton("Xóa", DANGER_RED, 110, 36);
		if (!java.beans.Beans.isDesignTime()) btnDelete.addActionListener(e -> deleteRow());
		JButton btnPDF = createButton("Xuất PDF", TEAL, 110, 36);
		if (!java.beans.Beans.isDesignTime()) btnPDF.addActionListener(e -> printPDF());
		actions.add(btnDelete);
		actions.add(btnPDF);
		add(actions, BorderLayout.SOUTH);
	}

	private JPanel createSearchPanel() {
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		p.setPreferredSize(new Dimension(0, 70));

		txtSearch = styledField("Tìm kiếm theo tên nhân viên...", 400);
		p.add(txtSearch);

		JButton btnSearch = createButton("Tìm Kiếm", PRIMARY_BLUE, 110, 36);
		if (!java.beans.Beans.isDesignTime()) btnSearch.addActionListener(e -> search());
		p.add(btnSearch);

		p.add(Box.createHorizontalStrut(200));

		btnAdd = createButton("+ Thêm Mới", ACCENT_BLUE, 130, 36);
		if (!java.beans.Beans.isDesignTime()) btnAdd.addActionListener(e -> addNew());
		p.add(btnAdd);
		return p;
	}

	private JPanel createTableCard() {
		JPanel card = new JPanel(new BorderLayout());
		card.setOpaque(true);
		card.setBackground(CARD_WHITE);
		card.setBorder(new EmptyBorder(20, 25, 20, 25));

		JLabel header = new JLabel("BẢNG LƯƠNG NHÂN VIÊN");
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setForeground(PRIMARY_BLUE);
		header.setBorder(new EmptyBorder(0, 0, 12, 0));

		String[] cols = { "ID", "Nhân Viên", "Lương Cơ Bản", "Số Ngày Làm", "Giờ OT", "Hệ Số OT", "Tổng Lương" };
		Object[][] data = { { 1, "Nguyễn Văn A", 20000000, 26, 10, 1.5, 21538461 },
				{ 2, "Trần Thị B", 15000000, 24, 8, 1.5, 16615384 } };
		model = new DefaultTableModel(data, cols) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(model);
		styleTable(table);

		if (!java.beans.Beans.isDesignTime()) {
			table.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					int r = table.getSelectedRow();
					if (r != -1) {
						formPanel.setEditMode(r, getRow(r));
						formPanel.setVisible(true);
						btnAdd.setVisible(false);
						formPanel.focusFirst();
					}
				}
			});
		}

		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);

		card.add(header, BorderLayout.NORTH);
		card.add(sp, BorderLayout.CENTER);
		return card;
	}

	private JTextField styledField(String placeholder, int w) {
		JTextField f = new JTextField(placeholder);
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setForeground(TEXT_PRIMARY);
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		f.setPreferredSize(new Dimension(w, 36));
		return f;
	}

	private JButton createButton(String text, Color bg, int w, int h) {
		JButton b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setPreferredSize(new Dimension(w, h));
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		return b;
	}

	private void styleTable(JTable t) {
		JTableHeader h = t.getTableHeader();
		h.setFont(new Font("Segoe UI", Font.BOLD, 13));
		h.setBackground(PRIMARY_BLUE);
		h.setForeground(Color.WHITE);
		h.setPreferredSize(new Dimension(0, 40));
		DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
				setBackground(PRIMARY_BLUE);
				setForeground(Color.WHITE);
				setFont(new Font("Segoe UI", Font.BOLD, 13));
				setHorizontalAlignment(JLabel.CENTER);
				return this;
			}
		};
		for (int i = 0; i < t.getColumnCount(); i++)
			t.getColumnModel().getColumn(i).setHeaderRenderer(hr);

		t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		t.setRowHeight(35);
		t.setGridColor(BORDER_COLOR);
		t.setSelectionBackground(new Color(227, 242, 253));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setIntercellSpacing(new Dimension(1, 1));
	}

	private Object[] getRow(int r) {
		Object[] d = new Object[model.getColumnCount()];
		for (int i = 0; i < d.length; i++)
			d[i] = model.getValueAt(r, i);
		return d;
	}

	private void search() {
		if (!java.beans.Beans.isDesignTime()) {
			String q = txtSearch.getText().trim();
			if (q.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông Báo",
						JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Đang tìm kiếm: " + q + " (Demo)", "Tìm Kiếm",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void addNew() {
		if (!java.beans.Beans.isDesignTime()) {
			formPanel.setAddMode(true);
			formPanel.setVisible(true);
			btnAdd.setVisible(false);
			formPanel.focusFirst();
		}
	}

	private void deleteRow() {
		if (!java.beans.Beans.isDesignTime()) {
			int r = table.getSelectedRow();
			if (r != -1) {
				int cf = JOptionPane.showConfirmDialog(this, "Xóa bản ghi lương này?", "Xác nhận",
						JOptionPane.YES_NO_OPTION);
				if (cf == JOptionPane.YES_OPTION) {
					model.removeRow(r);
					formPanel.setVisible(false);
					btnAdd.setVisible(true);
					JOptionPane.showMessageDialog(this, "Đã xóa thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void printPDF() {
		if (!java.beans.Beans.isDesignTime()) {
			try {
				MessageFormat h = new MessageFormat("BẢNG LƯƠNG NHÂN VIÊN");
				MessageFormat f = new MessageFormat("Trang {0}");
				table.print(JTable.PrintMode.FIT_WIDTH, h, f);
				JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void onSave(ActionEvent e) {
		if (!java.beans.Beans.isDesignTime()) {
			String cmd = e.getActionCommand();
			Object[] data = formPanel.getFormData();
			if ("add".equals(cmd)) {
				int newId = model.getRowCount() + 1;
				data[0] = newId;
				model.addRow(data);
				JOptionPane.showMessageDialog(this, "Thêm bảng lương thành công!", "Thành Công",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				int r = formPanel.getEditingRow();
				if (r != -1) {
					for (int i = 1; i < model.getColumnCount(); i++)
						model.setValueAt(data[i], r, i);
					JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành Công",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
			formPanel.setVisible(false);
			btnAdd.setVisible(true);
		}
	}

	private void onCancel(ActionEvent e) {
		if (!java.beans.Beans.isDesignTime()) {
			formPanel.setVisible(false);
			btnAdd.setVisible(true);
		}
	}
}
