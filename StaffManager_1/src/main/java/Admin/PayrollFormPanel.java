package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PayrollFormPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color DANGER_RED = new Color(244, 67, 54);

	private JTextField txtEmployee, txtBaseSalary, txtWorkDays, txtOTHours, txtOTRate, txtTotalSalary;
	private JButton btnSave, btnCancel;
	private boolean addMode = true;
	private int editingRow = -1;

	private final DecimalFormat formatter = new DecimalFormat("#,###");

	public PayrollFormPanel(ActionListener onSave, ActionListener onCancel) {
		if (!java.beans.Beans.isDesignTime()) {
			initUI(onSave, onCancel);
		} else {
			// Placeholder panel để WindowBuilder nhận diện layout
			setLayout(new BorderLayout());
			add(new JPanel());
		}
	}

	private void initUI(ActionListener onSave, ActionListener onCancel) {
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 25, 10, 25));

		var card = new JPanel(new GridBagLayout());
		card.setOpaque(false);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(20, 20, 15, 20)));

		txtEmployee = createField();
		txtBaseSalary = createField();
		txtWorkDays = createField();
		txtOTHours = createField();
		txtOTRate = createField();
		txtTotalSalary = createField();
		txtTotalSalary.setEditable(false);
		txtTotalSalary.setForeground(new Color(0, 128, 0));

		// Khi nhập thay đổi => tự động tính lương
		DocumentListener listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { calcTotal(); }
			@Override public void removeUpdate(DocumentEvent e) { calcTotal(); }
			@Override public void changedUpdate(DocumentEvent e) { calcTotal(); }
		};
		txtBaseSalary.getDocument().addDocumentListener(listener);
		txtWorkDays.getDocument().addDocumentListener(listener);
		txtOTHours.getDocument().addDocumentListener(listener);
		txtOTRate.getDocument().addDocumentListener(listener);

		var gc = new GridBagConstraints();
		gc.insets = new Insets(8, 8, 8, 8);
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;

		addField(card, gc, 0, "Tên Nhân Viên", txtEmployee);
		addField(card, gc, 1, "Lương Cơ Bản", txtBaseSalary);
		addField(card, gc, 2, "Số Ngày Làm Việc", txtWorkDays);
		addField(card, gc, 3, "Số Giờ Tăng Ca (OT)", txtOTHours);
		addField(card, gc, 4, "Hệ Số OT", txtOTRate);
		addField(card, gc, 5, "Tổng Lương (Tự tính)", txtTotalSalary);

		var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		actions.setOpaque(false);
		btnCancel = createButton("Hủy", DANGER_RED);
		btnSave = createButton("Lưu", PRIMARY_BLUE);

		btnSave.addActionListener(e -> {
			e.setSource(btnSave);
			btnSave.setActionCommand(addMode ? "add" : "update");
			onSave.actionPerformed(e);
		});
		btnCancel.addActionListener(onCancel);

		actions.add(btnCancel);
		actions.add(btnSave);

		add(card, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}

	private void addField(JPanel p, GridBagConstraints gc, int row, String title, JComponent field) {
		gc.gridx = 0; gc.gridy = row; gc.weightx = 0.3;
		var lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		p.add(lb, gc);

		gc.gridx = 1; gc.gridy = row; gc.weightx = 0.7;
		p.add(field, gc);
	}

	private JTextField createField() {
		var f = new JTextField();
		f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		f.setBackground(new Color(248, 250, 252));
		f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				new EmptyBorder(8, 12, 8, 12)));
		return f;
	}

	private JButton createButton(String t, Color bg) {
		JButton b = new JButton(t) {
			@Override protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getModel().isPressed()) {
					g2d.setColor(bg.darker());
				} else if (getModel().isRollover()) {
					g2d.setColor(bg.brighter());
				} else {
					g2d.setColor(bg);
				}
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.setColor(Color.WHITE);
				var fm = g2d.getFontMetrics(getFont());
				var x = (getWidth() - fm.stringWidth(getText())) / 2;
				var y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
				g2d.drawString(getText(), x, y);
			}
		};
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setForeground(Color.WHITE);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setPreferredSize(new Dimension(100, 36));
		return b;
	}

	private void calcTotal() {
		if (!java.beans.Beans.isDesignTime()) {
			try {
				var base = Double.parseDouble(txtBaseSalary.getText().trim());
				var days = Double.parseDouble(txtWorkDays.getText().trim());
				var otHours = Double.parseDouble(txtOTHours.getText().trim());
				var otRate = Double.parseDouble(txtOTRate.getText().trim());
				var perDay = base / 26.0;
				var total = perDay * days + (otHours * otRate * perDay / 8.0);
				txtTotalSalary.setText(formatter.format(total));
			} catch (Exception e) {
				txtTotalSalary.setText("0");
			}
		}
	}

	public void setAddMode(boolean add) {
		addMode = add; editingRow = -1;
		txtEmployee.setText(""); txtBaseSalary.setText(""); txtWorkDays.setText("");
		txtOTHours.setText(""); txtOTRate.setText(""); txtTotalSalary.setText("");
	}

	public void setEditMode(int row, Object[] data) {
		addMode = false; editingRow = row;
		txtEmployee.setText(String.valueOf(data[1]));
		txtBaseSalary.setText(String.valueOf(data[2]));
		txtWorkDays.setText(String.valueOf(data[3]));
		txtOTHours.setText(String.valueOf(data[4]));
		txtOTRate.setText(String.valueOf(data[5]));
		txtTotalSalary.setText(String.valueOf(data[6]));
	}

	public Object[] getFormData() {
		return new Object[] { null, txtEmployee.getText().trim(), parseDouble(txtBaseSalary.getText()),
				parseDouble(txtWorkDays.getText()), parseDouble(txtOTHours.getText()), parseDouble(txtOTRate.getText()),
				parseFormatted(txtTotalSalary.getText()) };
	}

	private double parseDouble(String v) {
		try { return Double.parseDouble(v.trim()); } catch (Exception e) { return 0; }
	}

	private double parseFormatted(String v) {
		try { return Double.parseDouble(v.replace(",", "").trim()); } catch (Exception e) { return 0; }
	}

	public int getEditingRow() { return editingRow; }
	public void focusFirst() { txtEmployee.requestFocusInWindow(); }
}
