package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ShiftFormPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	private JTextField txtName;
	private JTextField txtStart;
	private JTextField txtEnd;
	private JButton btnSave;
	private JButton btnCancel;
	private boolean addMode = true;
	private int editingRow = -1;

	public ShiftFormPanel(ActionListener onSave, ActionListener onCancel) {
		setBackground(new Color(245, 247, 250));
		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
		add(card, BorderLayout.CENTER);
		var gbl_card = new GridBagLayout();
		gbl_card.columnWidths = new int[] { 100, 300 };
		gbl_card.rowHeights = new int[] { 40, 40, 40 };
		gbl_card.columnWeights = new double[] { 0.3, 0.7 };
		gbl_card.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		card.setLayout(gbl_card);

		// ===== Row 1 =====
		var lblName = new JLabel("Tên Ca Làm");
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(10, 10, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		card.add(lblName, gbc_lblName);

		txtName = new JTextField();
		applyFieldStyle(txtName);
		var gbc_txtName = new GridBagConstraints();
		gbc_txtName.insets = new Insets(10, 0, 5, 10);
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 0;
		card.add(txtName, gbc_txtName);

		// ===== Row 2 =====
		var lblStart = new JLabel("Giờ Bắt Đầu (HH:mm)");
		lblStart.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.insets = new Insets(10, 10, 5, 5);
		gbc_lblStart.anchor = GridBagConstraints.WEST;
		gbc_lblStart.gridx = 0;
		gbc_lblStart.gridy = 1;
		card.add(lblStart, gbc_lblStart);

		txtStart = new JTextField();
		applyFieldStyle(txtStart);
		var gbc_txtStart = new GridBagConstraints();
		gbc_txtStart.insets = new Insets(10, 0, 5, 10);
		gbc_txtStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtStart.gridx = 1;
		gbc_txtStart.gridy = 1;
		card.add(txtStart, gbc_txtStart);

		// ===== Row 3 =====
		var lblEnd = new JLabel("Giờ Kết Thúc (HH:mm)");
		lblEnd.setFont(new Font("Segoe UI", Font.BOLD, 13));
		var gbc_lblEnd = new GridBagConstraints();
		gbc_lblEnd.insets = new Insets(10, 10, 10, 5);
		gbc_lblEnd.anchor = GridBagConstraints.WEST;
		gbc_lblEnd.gridx = 0;
		gbc_lblEnd.gridy = 2;
		card.add(lblEnd, gbc_lblEnd);

		txtEnd = new JTextField();
		applyFieldStyle(txtEnd);
		var gbc_txtEnd = new GridBagConstraints();
		gbc_txtEnd.insets = new Insets(10, 0, 10, 10);
		gbc_txtEnd.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEnd.gridx = 1;
		gbc_txtEnd.gridy = 2;
		card.add(txtEnd, gbc_txtEnd);

		// ===== Bottom Buttons =====
		var panelActions = new JPanel();
		panelActions.setBackground(new Color(245, 247, 250));
		var flowLayout = (FlowLayout) panelActions.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setHgap(10);
		add(panelActions, BorderLayout.SOUTH);

		btnCancel = createButton("Hủy", DANGER_RED);
		btnSave = createButton("Lưu", PRIMARY_BLUE);

		btnSave.addActionListener(e -> {
			var cmd = addMode ? "add" : "update";
			var newEvent = new ActionEvent(btnSave, ActionEvent.ACTION_PERFORMED, cmd);
			onSave.actionPerformed(newEvent);
		});
		btnCancel.addActionListener(onCancel);

		panelActions.add(btnCancel);
		panelActions.add(btnSave);
	}

	private void applyFieldStyle(JTextField f) {
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
	}

	private JButton createButton(String text, Color bg) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setPreferredSize(new Dimension(100, 36));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return b;
	}

	// ===== Logic giữ nguyên =====
	public void setAddMode(boolean add) {
		addMode = add;
		editingRow = -1;
		txtName.setText("");
		txtStart.setText("");
		txtEnd.setText("");
	}

	public void setEditMode(int row, Object[] data) {
		addMode = false;
		editingRow = row;
		txtName.setText(String.valueOf(data[1]));
		txtStart.setText(String.valueOf(data[2]));
		txtEnd.setText(String.valueOf(data[3]));
	}

	public Object[] getFormData() {
		return new Object[] { null, txtName.getText().trim(), txtStart.getText().trim(), txtEnd.getText().trim() };
	}

	public int getEditingRow() {
		return editingRow;
	}

	public void focusFirst() {
		txtName.requestFocusInWindow();
	}
}
