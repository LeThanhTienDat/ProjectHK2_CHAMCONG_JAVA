package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class OvertimeFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	private JTextField txtName, txtStart, txtEnd;
	private JCheckBox chkConfirm;
	private JButton btnSave, btnCancel;
	private boolean addMode = true;
	private int editingRow = -1;

	private ActionListener onSaveListener;
	private ActionListener onCancelListener;

	// Constructor rỗng để WindowBuilder mở được
	public OvertimeFormPanel() {
		initComponents();
	}

	// Constructor chính để code dùng
	public OvertimeFormPanel(ActionListener onSave, ActionListener onCancel) {
		onSaveListener = onSave;
		onCancelListener = onCancel;
		initComponents();
	}

	private void initComponents() {
		setOpaque(true);
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var card = new JPanel(new GridBagLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(20, 20, 15, 20)));

		txtName = createField();
		txtStart = createField();
		txtEnd = createField();
		chkConfirm = new JCheckBox("Đã xác nhận");
		chkConfirm.setBackground(Color.WHITE);
		chkConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		addField(card, 0, "Tên Loại OT", txtName);
		addField(card, 1, "Giờ Bắt Đầu (HH:mm)", txtStart);
		addField(card, 2, "Giờ Kết Thúc (HH:mm)", txtEnd);

		var gcConfirm = new GridBagConstraints();
		gcConfirm.insets = new Insets(8, 8, 8, 8);
		gcConfirm.anchor = GridBagConstraints.WEST;
		gcConfirm.fill = GridBagConstraints.NONE;
		gcConfirm.gridx = 1;
		gcConfirm.gridy = 3;
		card.add(chkConfirm, gcConfirm);

		var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		actions.setBackground(Color.WHITE);

		btnCancel = createButton("Hủy", DANGER_RED);
		btnSave = createButton("Lưu", PRIMARY_BLUE);

		actions.add(btnCancel);
		actions.add(btnSave);

		add(card, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);

		btnSave.addActionListener(e -> {
			if (onSaveListener != null) {
				ActionEvent evt = new ActionEvent(btnSave, e.getID(), addMode ? "add" : "update");
				onSaveListener.actionPerformed(evt);
			}
		});

		btnCancel.addActionListener(e -> {
			if (onCancelListener != null) {
				onCancelListener.actionPerformed(e);
			}
		});
	}

	public void setOnSaveListener(ActionListener listener) {
		onSaveListener = listener;
	}

	public void setOnCancelListener(ActionListener listener) {
		onCancelListener = listener;
	}

	public void setAddMode(boolean add) {
		addMode = add;
		editingRow = -1;
		txtName.setText("");
		txtStart.setText("");
		txtEnd.setText("");
		chkConfirm.setSelected(false);
	}

	public void setEditMode(int row, Object[] data) {
		addMode = false;
		editingRow = row;
		txtName.setText(String.valueOf(data[1]));
		txtStart.setText(String.valueOf(data[2]));
		txtEnd.setText(String.valueOf(data[3]));
	}

	public Object[] getFormData() {
		return new Object[]{
				null,
				txtName.getText().trim(),
				txtStart.getText().trim(),
				txtEnd.getText().trim(),
				chkConfirm.isSelected() ? "Có" : "Không"
		};
	}

	public int getEditingRow() {
		return editingRow;
	}

	public void focusFirst() {
		txtName.requestFocusInWindow();
	}

	// ======== Helper Methods ========
	private static JTextField createField() {
		var f = new JTextField();
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)
				));
		return f;
	}

	private static JButton createButton(String text, Color bg) {
		var b = new JButton(text);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setBackground(bg);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
		b.setPreferredSize(new Dimension(100, 36));
		return b;
	}

	private static void addField(JPanel p, int row, String title, JComponent field) {
		var gcLabel = new GridBagConstraints();
		gcLabel.insets = new Insets(8, 8, 8, 8);
		gcLabel.anchor = GridBagConstraints.WEST;
		gcLabel.fill = GridBagConstraints.HORIZONTAL;
		gcLabel.weightx = 0.3;
		gcLabel.gridx = 0;
		gcLabel.gridy = row;
		var lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		p.add(lb, gcLabel);

		var gcField = new GridBagConstraints();
		gcField.insets = new Insets(8, 8, 8, 8);
		gcField.anchor = GridBagConstraints.WEST;
		gcField.fill = GridBagConstraints.HORIZONTAL;
		gcField.weightx = 0.7;
		gcField.gridx = 1;
		gcField.gridy = row;
		p.add(field, gcField);
	}
}
