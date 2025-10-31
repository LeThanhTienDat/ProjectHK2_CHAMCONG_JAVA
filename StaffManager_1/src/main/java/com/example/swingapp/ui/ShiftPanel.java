package com.example.swingapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Time;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.example.swingapp.model.Shift;
import com.example.swingapp.service.ShiftService;

public class ShiftPanel extends JPanel {
	private final ShiftService service = new ShiftService();
	private final DefaultTableModel model = new DefaultTableModel();
	private final JTable table = new JTable(model);

	// WindowBuilder cần constructor trống
	public ShiftPanel() {
		if (!java.beans.Beans.isDesignTime()) {
			initUI();
		}
	}

	private void initUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		initTable();
		initButtons();
		loadData();
	}

	private void initTable() {
		model.setColumnIdentifiers(new Object[]{"ID", "Tên ca", "Giờ bắt đầu", "Giờ kết thúc"});
		table.setFillsViewportHeight(true);
		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		var scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createTitledBorder("Danh sách ca làm việc"));
		add(scroll, BorderLayout.CENTER);
	}

	private void initButtons() {
		var panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

		var btnAdd = new JButton("➕ Thêm");
		var btnEdit = new JButton("✏️ Sửa");
		var btnDelete = new JButton("🗑️ Xóa");
		var btnRefresh = new JButton("🔄 Làm mới");

		panelButtons.add(btnAdd);
		panelButtons.add(btnEdit);
		panelButtons.add(btnDelete);
		panelButtons.add(btnRefresh);

		add(panelButtons, BorderLayout.SOUTH);

		// Runtime events
		if (!java.beans.Beans.isDesignTime()) {
			btnAdd.addActionListener(e -> openForm(null));
			btnEdit.addActionListener(e -> {
				var selected = table.getSelectedRow();
				if (selected == -1) {
					JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng để sửa");
					return;
				}
				var id = (int) model.getValueAt(selected, 0);
				var name = (String) model.getValueAt(selected, 1);
				var start = Time.valueOf(model.getValueAt(selected, 2).toString());
				var end = Time.valueOf(model.getValueAt(selected, 3).toString());
				openForm(new Shift(id, name, start, end));
			});
			btnDelete.addActionListener(e -> deleteShift());
			btnRefresh.addActionListener(e -> loadData());
		}
	}

	private void loadData() {
		model.setRowCount(0);
		var list = service.getAll();
		for (Shift s : list) {
			model.addRow(new Object[]{
					s.getId(), s.getShiftName(), s.getStartTime(), s.getEndTime()
			});
		}
	}

	private void deleteShift() {
		var selected = table.getSelectedRow();
		if (selected == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm để xóa");
			return;
		}
		var id = (int) model.getValueAt(selected, 0);
		var confirm = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn xóa ca này?",
				"Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			if (service.delete(id)) {
				JOptionPane.showMessageDialog(this, "Xóa thành công!");
				loadData();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa!");
			}
		}
	}

	private void openForm(Shift shift) {
		if (java.beans.Beans.isDesignTime()) {
			// WindowBuilder: tạo placeholder panel để hiển thị layout
			var placeholder = new JPanel(new GridLayout(5, 2, 10, 10));
			add(placeholder);
			return;
		}

		var dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
		dialog.setTitle(shift == null ? "Thêm ca làm mới" : "Cập nhật ca làm");
		dialog.setSize(400, 300);
		dialog.setLayout(new GridLayout(5, 2, 10, 10));
		dialog.setLocationRelativeTo(this);

		var lblName = new JLabel("Tên ca:");
		var txtName = new JTextField();
		var lblStart = new JLabel("Giờ bắt đầu (HH:MM:SS):");
		var txtStart = new JTextField();
		var lblEnd = new JLabel("Giờ kết thúc (HH:MM:SS):");
		var txtEnd = new JTextField();

		var btnSave = new JButton("Lưu");
		var btnCancel = new JButton("Hủy");

		if (shift != null) {
			txtName.setText(shift.getShiftName());
			txtStart.setText(shift.getStartTime().toString());
			txtEnd.setText(shift.getEndTime().toString());
		}

		dialog.add(lblName);
		dialog.add(txtName);
		dialog.add(lblStart);
		dialog.add(txtStart);
		dialog.add(lblEnd);
		dialog.add(txtEnd);
		dialog.add(new JLabel());
		dialog.add(new JLabel());
		dialog.add(btnSave);
		dialog.add(btnCancel);

		btnCancel.addActionListener(e -> dialog.dispose());
		btnSave.addActionListener(e -> {
			try {
				var name = txtName.getText().trim();
				var start = Time.valueOf(txtStart.getText().trim());
				var end = Time.valueOf(txtEnd.getText().trim());

				if (name.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Tên ca không được để trống!");
					return;
				}

				var s = new Shift();
				s.setShiftName(name);
				s.setStartTime(start);
				s.setEndTime(end);

				boolean success;
				if (shift == null) {
					success = service.add(s);
				} else {
					s.setId(shift.getId());
					success = service.update(s);
				}

				if (success) {
					JOptionPane.showMessageDialog(dialog, "Lưu thành công!");
					loadData();
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Lưu thất bại!");
				}

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(dialog, "Lỗi định dạng giờ! Dạng hợp lệ: 08:00:00");
			}
		});

		dialog.setVisible(true);
	}
}
