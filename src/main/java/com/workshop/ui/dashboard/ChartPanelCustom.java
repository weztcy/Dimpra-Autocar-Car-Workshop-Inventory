package com.workshop.ui.dashboard;

import com.formdev.flatlaf.FlatLaf;
import com.workshop.ui.theme.UIStyle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ChartPanelCustom {

    public static JPanel createMonthlyRevenueChart(Map<Integer, BigDecimal> monthlyData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};

        for (int m = 1; m <= 12; m++) {
            BigDecimal val = monthlyData.getOrDefault(m, BigDecimal.ZERO);
            double valInMillions = val.doubleValue() / 1_000_000.0;
            dataset.addValue(valInMillions, "Omset (Juta Rp)", months[m - 1]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Bulan Operasional",
                "Juta Rupiah (IDR)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeCategoryChart(chart, UIStyle.PRIMARY);

        JPanel card = UIStyle.createCardPanel(12, 14, 12, 14);
        card.setLayout(new BorderLayout(0, 8));

        // Card Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Tren Pendapatan Bulanan");
        lblTitle.setFont(UIStyle.FONT_SUBHEADER);
        lblTitle.setForeground(UIStyle.getTextMain());

        JLabel lblSub = new JLabel("Akumulasi omset suku cadang & jasa servis per bulan");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(UIStyle.getTextMuted());

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);
        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);

        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);
        card.add(pnlHeader, BorderLayout.NORTH);

        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        cp.setPreferredSize(new Dimension(360, 210));
        cp.setMinimumSize(new Dimension(240, 160));
        card.add(cp, BorderLayout.CENTER);

        return card;
    }

    public static JPanel createTopPartsBarChart(List<Map<String, Object>> topParts) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> row : topParts) {
            String name = (String) row.get("partName");
            if (name.length() > 22) name = name.substring(0, 20) + "..";
            int qty = (int) row.get("totalQty");
            dataset.addValue(qty, "Volume", name);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Suku Cadang",
                "Unit Dikonsumsi",
                dataset,
                PlotOrientation.HORIZONTAL,
                false, true, false
        );

        customizeCategoryChart(chart, UIStyle.SUCCESS);

        JPanel card = UIStyle.createCardPanel(12, 14, 12, 14);
        card.setLayout(new BorderLayout(0, 8));

        // Card Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Top 5 Suku Cadang Terbanyak Dikonsumsi");
        lblTitle.setFont(UIStyle.FONT_SUBHEADER);
        lblTitle.setForeground(UIStyle.getTextMain());

        JLabel lblSub = new JLabel("Ranking fast-moving spare parts pada pengerjaan servis");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(UIStyle.getTextMuted());

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);
        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);

        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);
        card.add(pnlHeader, BorderLayout.NORTH);

        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        cp.setPreferredSize(new Dimension(360, 210));
        cp.setMinimumSize(new Dimension(240, 160));
        card.add(cp, BorderLayout.CENTER);

        return card;
    }

    private static void customizeCategoryChart(JFreeChart chart, Color barColor) {
        boolean isDark = FlatLaf.isLafDark();
        Color gridColor = isDark ? new Color(51, 65, 85) : new Color(226, 232, 240);
        Color axisLineColor = isDark ? new Color(71, 85, 105) : new Color(203, 213, 225);
        Color tickColor = isDark ? new Color(148, 163, 184) : UIStyle.TEXT_MUTED;
        Color labelColor = isDark ? new Color(226, 232, 240) : UIStyle.TEXT_SECONDARY;

        chart.setBackgroundPaint(new Color(0, 0, 0, 0));
        chart.setBorderVisible(false);
        chart.setPadding(new org.jfree.chart.ui.RectangleInsets(4, 4, 4, 4));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0, 0, 0, 0));
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(gridColor);
        plot.setRangeGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0));
        plot.setDomainGridlinesVisible(false);

        // Customize Axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(UIStyle.FONT_SMALL);
        domainAxis.setTickLabelPaint(tickColor);
        domainAxis.setLabelFont(UIStyle.FONT_SMALL_BOLD);
        domainAxis.setLabelPaint(labelColor);
        domainAxis.setAxisLinePaint(axisLineColor);
        domainAxis.setTickMarksVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(UIStyle.FONT_SMALL);
        rangeAxis.setTickLabelPaint(tickColor);
        rangeAxis.setLabelFont(UIStyle.FONT_SMALL_BOLD);
        rangeAxis.setLabelPaint(labelColor);
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);

        // Bar Renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, barColor);
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.08);
        renderer.setItemMargin(0.15);
    }
}
