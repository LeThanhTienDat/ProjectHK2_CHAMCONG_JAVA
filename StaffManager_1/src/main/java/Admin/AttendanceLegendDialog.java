package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AttendanceLegendDialog extends JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private static final Color BG_LIGHT = new Color(250, 251, 255);
	private static final Color CARD_WHITE = new Color(255, 255, 255);
	private static final Color BORDER_COLOR = new Color(224, 235, 250);
	private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
	private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
	private static final Color DANGER_RED = new Color(244, 67, 54);
	private static final Color WARNING_ORANGE = new Color(255, 152, 0);

	public AttendanceLegendDialog(JPanel parent) {
		super(SwingUtilities.getWindowAncestor(parent), "Ký Hiệu Chấm Công", ModalityType.APPLICATION_MODAL);
		setSize(1200, 700); // Tăng size để fit bảng rộng và hai phần
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout(0, 20));
		setBackground(BG_LIGHT);

		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createLegendTablePanel(), BorderLayout.CENTER);
		add(createFooterPanel(), BorderLayout.SOUTH);
	}

	private JPanel createHeaderPanel() {
		var header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		header.setBackground(BG_LIGHT);
		header.setPreferredSize(new Dimension(0, 60));

		var title = new JLabel("HƯỚNG DẪN KÝ HIỆU CHẤM CÔNG VÀ TĂNG CA");
		title.setFont(new Font("Segoe UI", Font.BOLD, 20));
		title.setForeground(PRIMARY_BLUE);
		header.add(title);

		return header;
	}

	private JPanel createLegendTablePanel() {
		var mainPanel = new JPanel(new BorderLayout(0, 20));
		mainPanel.setBackground(BG_LIGHT);
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Phần 1: Ký hiệu cơ bản (6 cột)
		var basicPanel = createBasicLegendTable();
		var basicTitle = new JLabel("KÝ HIỆU CƠ BẢN");
		basicTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		basicTitle.setForeground(PRIMARY_BLUE);
		basicTitle.setHorizontalAlignment(SwingConstants.CENTER);

		var basicWrapper = new JPanel(new BorderLayout());
		basicWrapper.setBackground(CARD_WHITE);
		basicWrapper.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		basicWrapper.add(basicTitle, BorderLayout.NORTH);
		basicWrapper.add(basicPanel, BorderLayout.CENTER);

		// Phần 2: Tăng ca (2 cột: Diễn giải, Hệ số)
		var overtimePanel = createOvertimeTable();
		var overtimeTitle = new JLabel("HỆ SỐ TĂNG CA");
		overtimeTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		overtimeTitle.setForeground(PRIMARY_BLUE);
		overtimeTitle.setHorizontalAlignment(SwingConstants.CENTER);

		var overtimeWrapper = new JPanel(new BorderLayout());
		overtimeWrapper.setBackground(CARD_WHITE);
		overtimeWrapper.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		overtimeWrapper.add(overtimeTitle, BorderLayout.NORTH);
		overtimeWrapper.add(overtimePanel, BorderLayout.CENTER);

		// Layout hai phần song song
		var tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
		tablesPanel.setBackground(BG_LIGHT);
		tablesPanel.add(basicWrapper);
		tablesPanel.add(overtimeWrapper);

		mainPanel.add(tablesPanel, BorderLayout.CENTER);
		return mainPanel;
	}

	private JPanel createBasicLegendTable() {
		var tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(CARD_WHITE);
		tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Dữ liệu ký hiệu cơ bản mới theo yêu cầu
		String[][] legends = {
				{"PH", "Nghỉ phép", "PH4, PH8", "Lịch đăng ký/bố trí", "100%", ""},
				{"NL", "Nghỉ lễ, tết", "NL4, NL8", "Thông báo nghỉ lễ, tết", "100%", ""},
				{"KL", "Nghỉ không lương", "KL4, KL8", "Lịch đăng ký nghỉ không lương", "0%", ""},
				{"KP", "Nghỉ không phép", "KP4,KP8", "Theo phát sinh", "0%", ""},
				{"NB", "Nghỉ bù", "NB4, NB8", "Lịch đăng ký/bố trí", "100%", ""},
				{"NV", "Ngừng việc", "NV4, NV8", "Lịch đăng ký/bố trí", "0%", ""},
				{"NO", "Nghỉ hưởng BHXH (Thai sản, ốm đau, con ốm, dưỡng sức…..........)", "NO4, NO8", "Theo phát sinh", "0%", ""},
				{"VR", "Nghỉ việc riêng có hưởng lương (Hiếu, hỉ)", "VR4, VR8", "Theo phát sinh", "100%", ""},
				{"X", "Làm việc tại cty, thị trường, đi công tác, đào tạo", "X04, X08", "MCC/Bảng tổng hợp", "100%", ""},
				{"D", "Làm việc vào giờ đêm", "D01, D02", "MCC/Bảng tổng hợp", "130%", ""},
				{"O", "Làm thêm giờ đêm", "O01,O02", "MCC/Bảng tổng hợp", "", ""},
				{"H", "Làm việc từ xa", "H04, H08", "Lịch đăng ký/bố trí", "70%", ""}
		};

		// Tạo bảng hiển thị ký hiệu với 6 cột
		String[] cols = {"Ký hiệu", "Diễn giải", "Các trường hợp chấm công", "Nguồn đầu vào", "Hệ số lương", "Ghi chú"};
		var data = new Object[legends.length][6];
		for (var i = 0; i < legends.length; i++) {
			for (var j = 0; j < 6; j++) {
				data[i][j] = legends[i][j];
			}
		}

		DefaultTableModel model = new DefaultTableModel(data, cols) {
			@Override
			public boolean isCellEditable(int r, int c) { return false; }
		};

		var legendTable = new JTable(model);
		styleLegendTable(legendTable);

		// Renderer tùy chỉnh cho cột ký hiệu với màu sắc (dựa trên hệ số lương)
		legendTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lbl.setForeground(PRIMARY_BLUE);

				// Màu nền dựa trên hệ số lương của row (lấy từ cột 4)
				var salaryCoeff = (String) model.getValueAt(row, 4);
				if (salaryCoeff != null) {
					switch (salaryCoeff) {
					case "100%":
						lbl.setBackground(new Color(200, 230, 201)); // Xanh nhạt cho có lương đầy đủ
						break;
					case "130%":
						lbl.setBackground(new Color(255, 235, 59)); // Vàng cho làm đêm
						break;
					case "70%":
						lbl.setBackground(new Color(255, 193, 7)); // Cam cho WFH
						break;
					default:
						if ("0%".equals(salaryCoeff) || salaryCoeff.isEmpty()) {
							lbl.setBackground(new Color(248, 215, 218)); // Đỏ nhạt cho không lương
						} else {
							lbl.setBackground(CARD_WHITE);
						}
						break;
					}
				} else if ("0%".equals(salaryCoeff) || salaryCoeff.isEmpty()) {
					lbl.setBackground(new Color(248, 215, 218)); // Đỏ nhạt cho không lương
				} else {
					lbl.setBackground(CARD_WHITE);
				}

				if (isSelected) {
					lbl.setBackground(new Color(227, 242, 253));
				}

				lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
				return lbl;
			}
		});

		// Renderer cho cột hệ số lương: Màu chữ theo hệ số
		legendTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

				var coeff = value != null ? value.toString() : "";
				if (coeff != null) {
					switch (coeff) {
					case "100%":
						lbl.setForeground(SUCCESS_GREEN);
						break;
					case "130%":
						lbl.setForeground(WARNING_ORANGE);
						break;
					case "70%":
						lbl.setForeground(WARNING_ORANGE);
						break;
					case "0%":
						lbl.setForeground(DANGER_RED);
						break;
					default:
						lbl.setForeground(TEXT_PRIMARY);
						break;
					}
				} else {
					lbl.setForeground(TEXT_PRIMARY);
				}

				lbl.setBackground(CARD_WHITE);
				if (isSelected) {
					lbl.setBackground(new Color(227, 242, 253));
				}
				return lbl;
			}
		});

		// Set width cho các cột để fit nội dung
		legendTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Ký hiệu
		legendTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Diễn giải
		legendTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Các trường hợp
		legendTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Nguồn đầu vào
		legendTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Hệ số lương
		legendTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Ghi chú

		var sp = new JScrollPane(legendTable);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		sp.setPreferredSize(new Dimension(0, 300));

		tablePanel.add(sp, BorderLayout.CENTER);
		return tablePanel;
	}

	private JPanel createOvertimeTable() {
		var tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(CARD_WHITE);
		tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Dữ liệu tăng ca mới (2 cột)
		String[][] overtimeData = {
				{"Tăng ca ngày ngày thường", "1.50"},
				{"Tăng ca đêm sau thời gian làm việc (không làm ngoài giờ ban ngày)", "2.00"},
				{"Tăng ca đêm sau thời gian làm việc (có làm ngoài giờ ban ngày)", "2.10"},
				{"Tăng ca Ngày Ngày nghỉ", "2.00"},
				{"Tăng ca đêm Ngày nghỉ", "2.70"},
				{"Tăng ca Ngày Ngày Lễ", "3.00"},
				{"Tăng ca Đêm Ngày Lễ", "3.90"}
		};

		String[] cols = {"Diễn giải", "Hệ số"};
		var data = new Object[overtimeData.length][2];
		for (var i = 0; i < overtimeData.length; i++) {
			data[i][0] = overtimeData[i][0];
			data[i][1] = overtimeData[i][1];
		}

		DefaultTableModel model = new DefaultTableModel(data, cols) {
			@Override
			public boolean isCellEditable(int r, int c) { return false; }
		};

		var overtimeTable = new JTable(model);
		styleOvertimeTable(overtimeTable);

		// Renderer cho cột hệ số tăng ca: Màu chữ theo giá trị (cao hơn = cam đậm hơn)
		overtimeTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
				lbl.setBackground(CARD_WHITE);

				var coeff = value != null ? value.toString() : "";
				double numCoeff;
				try {
					numCoeff = Double.parseDouble(coeff);
				} catch (NumberFormatException e) {
					numCoeff = 0;
				}
				if (numCoeff >= 3.0) {
					lbl.setForeground(new Color(255, 87, 34)); // Đỏ cam cho hệ số cao
				} else if (numCoeff >= 2.0) {
					lbl.setForeground(WARNING_ORANGE); // Cam cho trung bình
				} else {
					lbl.setForeground(SUCCESS_GREEN); // Xanh cho thấp
				}

				if (isSelected) {
					lbl.setBackground(new Color(227, 242, 253));
				}
				return lbl;
			}
		});

		// Set width cho cột tăng ca
		overtimeTable.getColumnModel().getColumn(0).setPreferredWidth(500); // Diễn giải dài
		overtimeTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Hệ số

		var sp = new JScrollPane(overtimeTable);
		sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		sp.getViewport().setBackground(CARD_WHITE);
		sp.setPreferredSize(new Dimension(0, 300));

		tablePanel.add(sp, BorderLayout.CENTER);
		return tablePanel;
	}

	private JPanel createFooterPanel() {
		var footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		footer.setBackground(BG_LIGHT);
		footer.setPreferredSize(new Dimension(0, 60));

		JButton btnClose = new JButton("Đóng") {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getModel().isPressed() ? PRIMARY_BLUE.darker() : getModel().isRollover() ? PRIMARY_BLUE.brighter() : PRIMARY_BLUE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.setColor(Color.WHITE);
				var fm = g2.getFontMetrics();
				g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
						(getHeight() + fm.getAscent() - fm.getDescent()) / 2);
			}
		};
		btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnClose.setForeground(Color.WHITE);
		btnClose.setPreferredSize(new Dimension(120, 36));
		btnClose.setContentAreaFilled(false);
		btnClose.setBorderPainted(false);
		btnClose.setFocusPainted(false);
		btnClose.addActionListener(e -> dispose());

		footer.add(btnClose);
		return footer;
	}

	private void styleLegendTable(JTable t) {
		t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		t.setRowHeight(45); // Tăng height cho nội dung dài
		t.setSelectionBackground(new Color(227, 242, 253));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(BORDER_COLOR);
		t.setShowGrid(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		var h = t.getTableHeader();
		h.setFont(new Font("Segoe UI", Font.BOLD, 12));
		h.setBackground(PRIMARY_BLUE);
		h.setForeground(Color.WHITE);
		h.setPreferredSize(new Dimension(0, 40));

		// Renderer cho header
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
				return lbl;
			}
		};
		h.setDefaultRenderer(headerRenderer);
	}

	private void styleOvertimeTable(JTable t) {
		t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		t.setRowHeight(35);
		t.setSelectionBackground(new Color(227, 242, 253));
		t.setSelectionForeground(TEXT_PRIMARY);
		t.setGridColor(BORDER_COLOR);
		t.setShowGrid(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		var h = t.getTableHeader();
		h.setFont(new Font("Segoe UI", Font.BOLD, 12));
		h.setBackground(PRIMARY_BLUE);
		h.setForeground(Color.WHITE);
		h.setPreferredSize(new Dimension(0, 40));

		// Renderer cho header tăng ca
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				var lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(PRIMARY_BLUE);
				lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
				return lbl;
			}
		};
		h.setDefaultRenderer(headerRenderer);
	}
}