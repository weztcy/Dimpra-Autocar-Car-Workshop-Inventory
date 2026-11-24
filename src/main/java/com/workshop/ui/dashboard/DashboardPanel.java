package com.workshop.ui.dashboard;

import com.workshop.dao.DashboardDao;
import com.workshop.dao.PartDao;
import com.workshop.dao.ServiceTransactionDao;
import com.workshop.model.Part;
import com.workshop.model.ServiceTransaction;
import com.workshop.ui.common.BadgeRenderer;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.common.StatCard;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final DashboardDao dashboardDao = new DashboardDao();
    private final ServiceTransactionDao trxDao = new ServiceTransactionDao();
    private final PartDao partDao = new PartDao();

    private StatCard cardValuation;
    private StatCard cardRevenue;
    private StatCard cardRepairs;
    private StatCard cardLowStock;

    private JPanel pnlCharts;
    private DefaultTableModel recentTrxModel;
    private DefaultTableModel lowStockModel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
        refreshData();
    }

    private void initUI() {
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. Header Section: Title on Left, Compact Refresh Button on Right
        JPanel pnlHeader = new JPanel(new BorderLayout(16, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlTitleText = new JPanel();
        pnlTitleText.setLayout(new BoxLayout(pnlTitleText, BoxLayout.Y_AXIS));
        pnlTitleText.setOpaque(false);

        JLabel lblTitle = new JLabel("Workshop Operations Dashboard");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.getTextMain());

        JLabel lblSub = new JLabel("Ikhtisar inventaris suku cadang, pengadaan supplier, dan transaksi servis kendaraan");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.getTextMuted());

        pnlTitleText.add(lblTitle);
        pnlTitleText.add(Box.createVerticalStrut(2));
        pnlTitleText.add(lblSub);
        pnlHeader.add(pnlTitleText, BorderLayout.WEST);

        // Right compact refresh button
        JPanel pnlHeaderBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        pnlHeaderBtns.setOpaque(false);

        JButton btnRefresh = UIStyle.createSecondaryButton("Refresh Data");
        btnRefresh.setPreferredSize(new Dimension(120, 32));
        btnRefresh.addActionListener(e -> refreshData());
        pnlHeaderBtns.add(btnRefresh);

        pnlHeader.add(pnlHeaderBtns, BorderLayout.EAST);
        content.add(pnlHeader);
        content.add(Box.createVerticalStrut(14));

        // 2. 4 Modern SaaS KPI Stat Cards
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 14, 0));
        pnlCards.setOpaque(false);
        pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        pnlCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardValuation = new StatCard("VALUASI ASET INVENTARIS", "Rp 0", "106 Jenis Suku Cadang", "Asset Value", UIStyle.PRIMARY, "💰");
        cardRevenue = new StatCard("TOTAL PENDAPATAN", "Rp 0", "Parts + Jasa Servis", "Akumulasi", UIStyle.SUCCESS, "📈");
        cardRepairs = new StatCard("TRANSAKSI SERVIS TUNTAS", "0 PKB", "100% Tercatat & Terverifikasi", "100% Selesai", UIStyle.ACCENT, "🔧");
        cardLowStock = new StatCard("PERINGATAN STOK MENIPIS", "0 Part", "Perlu Pengadaan Purchase Order", "Perlu Tindakan", UIStyle.WARNING, "⚠️");

        pnlCards.add(cardValuation);
        pnlCards.add(cardRevenue);
        pnlCards.add(cardRepairs);
        pnlCards.add(cardLowStock);
        content.add(pnlCards);
        content.add(Box.createVerticalStrut(14));

        // 3. Visual Charts Grid (2 Cards Side by Side)
        pnlCharts = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlCharts.setOpaque(false);
        pnlCharts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        pnlCharts.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(pnlCharts);
        content.add(Box.createVerticalStrut(14));

        // 4. Tables Container (2 Cards Side by Side)
        JPanel pnlTables = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlTables.setOpaque(false);
        pnlTables.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        pnlTables.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left Table Card: Recent Work Orders
        JPanel cardRecent = UIStyle.createCardPanel(12, 14, 12, 14);
        cardRecent.setLayout(new BorderLayout(0, 8));

        JPanel pnlRecentHeader = new JPanel(new BorderLayout());
        pnlRecentHeader.setOpaque(false);
        JLabel lblRecentTitle = new JLabel("Transaksi Servis Kendaraan Terbaru");
        lblRecentTitle.setFont(UIStyle.FONT_SUBHEADER);
        lblRecentTitle.setForeground(UIStyle.getTextMain());
        JLabel lblRecentSub = new JLabel("8 Perintah Kerja (PKB) terakhir yang selesai dikerjakan");
        lblRecentSub.setFont(UIStyle.FONT_SMALL);
        lblRecentSub.setForeground(UIStyle.getTextMuted());

        JPanel pnlRecentTitleBox = new JPanel();
        pnlRecentTitleBox.setLayout(new BoxLayout(pnlRecentTitleBox, BoxLayout.Y_AXIS));
        pnlRecentTitleBox.setOpaque(false);
        pnlRecentTitleBox.add(lblRecentTitle);
        pnlRecentTitleBox.add(Box.createVerticalStrut(2));
        pnlRecentTitleBox.add(lblRecentSub);
        pnlRecentHeader.add(pnlRecentTitleBox, BorderLayout.WEST);
        cardRecent.add(pnlRecentHeader, BorderLayout.NORTH);

        String[] trxCols = {"No. PKB", "Tanggal", "No. Polisi", "Pelanggan", "Grand Total", "Status"};
        recentTrxModel = new DefaultTableModel(trxCols, 0);
        ModernTable tblRecent = new ModernTable(recentTrxModel);
        tblRecent.getColumnModel().getColumn(0).setPreferredWidth(95);
        tblRecent.getColumnModel().getColumn(1).setPreferredWidth(75);
        tblRecent.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblRecent.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblRecent.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblRecent.getColumnModel().getColumn(5).setPreferredWidth(75);
        tblRecent.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());
        tblRecent.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());
        cardRecent.add(new JScrollPane(tblRecent), BorderLayout.CENTER);
        pnlTables.add(cardRecent);

        // Right Table Card: Low Stock Warnings
        JPanel cardLow = UIStyle.createCardPanel(12, 14, 12, 14);
        cardLow.setLayout(new BorderLayout(0, 8));

        JPanel pnlLowHeader = new JPanel(new BorderLayout());
        pnlLowHeader.setOpaque(false);
        JLabel lblLowTitle = new JLabel("Peringatan Suku Cadang Menipis / Reorder");
        lblLowTitle.setFont(UIStyle.FONT_SUBHEADER);
        lblLowTitle.setForeground(UIStyle.WARNING_TEXT);
        JLabel lblLowSub = new JLabel("Suku cadang mendekati atau di bawah batas safety stock");
        lblLowSub.setFont(UIStyle.FONT_SMALL);
        lblLowSub.setForeground(UIStyle.getTextMuted());

        JPanel pnlLowTitleBox = new JPanel();
        pnlLowTitleBox.setLayout(new BoxLayout(pnlLowTitleBox, BoxLayout.Y_AXIS));
        pnlLowTitleBox.setOpaque(false);
        pnlLowTitleBox.add(lblLowTitle);
        pnlLowTitleBox.add(Box.createVerticalStrut(2));
        pnlLowTitleBox.add(lblLowSub);
        pnlLowHeader.add(pnlLowTitleBox, BorderLayout.WEST);
        cardLow.add(pnlLowHeader, BorderLayout.NORTH);

        String[] lowCols = {"Kode Part", "Nama Suku Cadang", "Merek", "Sisa Stok", "Min Stok", "Lokasi"};
        lowStockModel = new DefaultTableModel(lowCols, 0);
        ModernTable tblLow = new ModernTable(lowStockModel);
        tblLow.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblLow.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblLow.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblLow.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblLow.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblLow.getColumnModel().getColumn(5).setPreferredWidth(55);
        cardLow.add(new JScrollPane(tblLow), BorderLayout.CENTER);
        pnlTables.add(cardLow);

        content.add(pnlTables);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            DashboardDao.DashboardKpis kpis = dashboardDao.getKpis();
            cardValuation.setValue(CurrencyUtil.format(kpis.totalInventoryValuation));
            cardValuation.setSubtitle(kpis.totalPartsCount + " Jenis Suku Cadang");

            cardRevenue.setValue(CurrencyUtil.format(kpis.totalAnnualRevenue2022));
            cardRevenue.setSubtitle("Parts + Jasa Servis");

            cardRepairs.setValue(kpis.totalCompletedRepairs2022 + " PKB");
            cardRepairs.setSubtitle("100% Tercatat & Terverifikasi");

            cardLowStock.setValue(kpis.lowStockCount + " Suku Cadang");
            cardLowStock.setSubtitle("Perlu Pengadaan Purchase Order");

            // Update Charts
            pnlCharts.removeAll();
            Map<Integer, BigDecimal> monthlyRev = dashboardDao.getMonthlyRevenue2022();
            List<Map<String, Object>> topParts = dashboardDao.getTop5ConsumedParts2022();

            pnlCharts.add(ChartPanelCustom.createMonthlyRevenueChart(monthlyRev));
            pnlCharts.add(ChartPanelCustom.createTopPartsBarChart(topParts));
            pnlCharts.revalidate();
            pnlCharts.repaint();

            // Update Recent Transactions Table
            recentTrxModel.setRowCount(0);
            List<ServiceTransaction> recentTrxs = trxDao.findAll(null, null, "2022-12-01", "2022-12-31");
            int count = 0;
            for (ServiceTransaction t : recentTrxs) {
                if (count++ >= 8) break;
                recentTrxModel.addRow(new Object[]{
                        t.getTransactionCode(),
                        DateUtil.formatDate(t.getTransactionDate()),
                        t.getPlateNumber(),
                        t.getCustomerName(),
                        t.getGrandTotal(),
                        t.getStatus()
                });
            }

            // Update Low Stock Table
            lowStockModel.setRowCount(0);
            List<Part> lowParts = partDao.findLowStock();
            int lowCount = 0;
            for (Part p : lowParts) {
                if (lowCount++ >= 8) break;
                lowStockModel.addRow(new Object[]{
                        p.getPartNumber(),
                        p.getPartName(),
                        p.getBrand(),
                        p.getCurrentStock() + " " + p.getUnitOfMeasure(),
                        p.getMinStock() + " " + p.getUnitOfMeasure(),
                        p.getStorageBin()
                });
            }
        });
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
