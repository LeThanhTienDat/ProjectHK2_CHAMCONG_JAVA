package com.example.swingapp.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class PieChartPanel extends JPanel {
	private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
	private DefaultPieDataset dataset;
	private String chartTitle;
	private JFreeChart chart;

	public PieChartPanel() {
		setLayout(new BorderLayout());
		dataset = new DefaultPieDataset();
		chart = ChartFactory.createPieChart(
				"",
				dataset,
				true, true, false
				);
		chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

		var plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
		plot.setLabelBackgroundPaint(new Color(255, 255, 255, 180));
		plot.setOutlineVisible(false);

		PieSectionLabelGenerator labelGenerator =
				new StandardPieSectionLabelGenerator(
						"{0} - {2}",
						new DecimalFormat("0"),
						new DecimalFormat("0.0%")
						);
		plot.setLabelGenerator(labelGenerator);




		var chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
	}


	public void updateData(Map<String, Number> data) {
		dataset.clear();
		for (Map.Entry<String, Number> entry : data.entrySet()) {
			dataset.setValue(entry.getKey(), entry.getValue());
		}
	}

	public void setTitle(String title) {
		chartTitle = title;
		if (chart != null) {
			chart.setTitle(title);
		}
	}
}
