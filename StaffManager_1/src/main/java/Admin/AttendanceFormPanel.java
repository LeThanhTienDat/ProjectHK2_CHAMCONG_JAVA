package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AttendanceFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ==== Màu sắc ====
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	// ==== Các biến gốc ====
	private JTextField txtEmployee, txtDate, txtShift, txtComeLate, txtEarlyLeave, txtOT, txtIn, txtOut;
	private JButton btnSave, btnCancel;
	private int editingRow = -1;

	// ==== Constructor ====
	public AttendanceFormPanel(ActionListener onSave, ActionListener onCancel) {
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));
		initComponents(onSave, onCancel);
	}

	// ==== Phần WindowBuilder có thể nhận diện ====
	private void initComponents(ActionListener onSave, ActionListener onCancel) {
		JPanel form = new JPanel();
		form.setOpaque(false);
		form.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(20, 20, 20, 20)));
		form.setLayout(new GridLayout(8, 2, 12, 12)); // Sửa lại đúng số hàng

		// ==== Khởi tạo field ====
		txtEmployee = field(false);
		txtDate = field(false);
		txtShift = field(false);
		txtComeLate = field(true);
		txtEarlyLeave = field(true);
		txtOT = field(true);
		txtIn = field(true);
		txtOut = field(true);

		// ==== Thêm label & field ====
		form.add(label("Nhân Viên"));
		form.add(txtEmployee);
		form.add(label("Ngày Làm"));
		form.add(txtDate);
		form.add(label("Ca Làm"));
		form.add(txtShift);
		form.add(label("Đi Trễ (Có/Không)"));
		form.add(txtComeLate);
		form.add(label("Về Sớm (Có/Không)"));
		form.add(txtEarlyLeave);
		form.add(label("Giờ OT"));
		form.add(txtOT);
		form.add(label("Check In"));
		form.add(txtIn);
		form.add(label("Check Out"));
		form.add(txtOut);

		// ==== Thanh hành động ====
		JPanel actions = new JPanel();
		actions.setOpaque(false);
		actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

		btnCancel = button("Hủy", DANGER_RED);
		btnSave = button("Lưu", PRIMARY_BLUE);

		btnSave.addActionListener(e -> {
			e.setSource(btnSave);
			btnSave.setActionCommand("update");
			onSave.actionPerformed(e);
		});
		btnCancel.addActionListener(onCancel);

		actions.add(btnCancel);
		actions.add(btnSave);

		// ==== Add vào panel chính ====
		add(form, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}

	// ==== Label helper ====
	private JLabel label(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lbl.setForeground(new Color(33, 33, 33));
		return lbl;
	}

	// ==== Field helper ====
	private JTextField field(boolean editable) {
		JTextField f = new JTextField();
		f.setEditable(editable);
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(editable ? new Color(248, 250, 252) : new Color(240, 240, 240));
		return f;
	}

	// ==== Button helper ====
	private JButton button(String text, Color bg) {
		JButton b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(true);
		b.setPreferredSize(new Dimension(100, 36));
		return b;
	}

	// ==== Form logic ====
	public void setEditMode(int row, Object[] data) {
		editingRow = row;
		txtEmployee.setText(String.valueOf(data[1]));
		txtDate.setText(String.valueOf(data[2]));
		txtShift.setText(String.valueOf(data[3]));
		txtComeLate.setText(String.valueOf(data[4]));
		txtEarlyLeave.setText(String.valueOf(data[5]));
		txtOT.setText(String.valueOf(data[7]));
		txtIn.setText(String.valueOf(data[8]));
		txtOut.setText(String.valueOf(data[9]));
	}

	public Object[] getFormData() {
		return new Object[] {
				null,
				txtEmployee.getText().trim(),
				txtDate.getText().trim(),
				txtShift.getText().trim(),
				txtComeLate.getText().trim(),
				txtEarlyLeave.getText().trim(),
				"",
				txtOT.getText().trim(),
				txtIn.getText().trim(),
				txtOut.getText().trim()
		};
	}

	public int getEditingRow() {
		return editingRow;
	}

	public void focusFirst() {
		txtIn.requestFocusInWindow();
	}
}
