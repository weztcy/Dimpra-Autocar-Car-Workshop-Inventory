package com.workshop.ui.reports;

import com.workshop.dao.DashboardDao;
import com.workshop.dao.PartDao;
import com.workshop.dao.ServiceTransactionDao;
import com.workshop.dao.StockLedgerDao;
import com.workshop.model.Part;
import com.workshop.model.StockLedger;
import com.workshop.service.ReportService;
import com.workshop.ui.common.BadgeRenderer;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel {
    private final PartDao partDao = new PartDao();
    private final DashboardDao dashboardDao = new DashboardDao();
    private final StockLedgerDao ledgerDao = new StockLedgerDao();
    private final ReportService reportService = new ReportService();

    private final JComboBox<String> cmbReportType = new JComboBox<>(new String[]{
            "1. Laporan Valuasi Nilai Aset Inventaris (Inventory Valuation)",
            "2. Laporan Analisis Konsumsi Suku Cadang (Parts Consumption Audit)",
            "3. Laporan Buku Besar Mutasi Stok (Complete Stock Ledger Audit)",
            "4. Laporan Rekapitulasi Pendapatan Bulanan (Monthly Financial Revenue)",
            "5. Laporan Suku Cadang Kritis & Rekomendasi Reorder (Low Stock Warnings)"
    });

    private final DefaultTableModel reportTableModel;
    private final ModernTable reportTable;
    private final JLabel lblReportSummary = new JLabel("Pilih jenis laporan untuk melihat data.");

    public ReportsPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // 1. Header & Controls
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        // Header Row: Title & Action Buttons
        JPanel pnlTitle = new JPanel(new BorderLayout(16, 0));
        pnlTitle.setOpaque(false);
        pnlTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Laporan & Audit Dokumen Resmi");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_MAIN);

        JLabel lblSub = new JLabel("Otomasi audit stok, valuasi aset inventaris, dan pencetakan JasperReports PDF");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlTitle.add(pnlTitleBox, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlActions.setOpaque(false);

        JButton btnExportPdf = UIStyle.createPrimaryButton("Cetak Dokumen Resmi (PDF)");
        btnExportPdf.addActionListener(e -> onExportPdf());

        JButton btnExportCsv = UIStyle.createSecondaryButton("Export Excel / CSV");
        btnExportCsv.addActionListener(e -> onExportCsv());

        pnlActions.add(btnExportPdf);
        pnlActions.add(btnExportCsv);
        pnlTitle.add(pnlActions, BorderLayout.EAST);

        pnlTop.add(pnlTitle);
        pnlTop.add(Box.createVerticalStrut(12));

        // Selector Bar in Modern Card Toolbar
        JPanel pnlSelector = UIStyle.createCardPanel(8, 12, 8, 12);
        pnlSelector.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel lblSelLabel = new JLabel("Jenis Laporan Audit:");
        lblSelLabel.setFont(UIStyle.FONT_BOLD);
        pnlSelector.add(lblSelLabel);

        cmbReportType.setPreferredSize(new Dimension(420, 32));
        cmbReportType.addActionListener(e -> generateSelectedReport());
        pnlSelector.add(cmbReportType);

        JButton btnRefresh = UIStyle.createSecondaryButton("Tampilkan Laporan");
        btnRefresh.setPreferredSize(new Dimension(140, 32));
        btnRefresh.addActionListener(e -> generateSelectedReport());
        pnlSelector.add(btnRefresh);

        pnlTop.add(pnlSelector);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table Preview
        reportTableModel = new DefaultTableModel();
        reportTable = new ModernTable(reportTableModel);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        pnlFooter.setOpaque(false);

        lblReportSummary.setFont(UIStyle.FONT_BOLD);
        pnlFooter.add(lblReportSummary, BorderLayout.WEST);
        add(pnlFooter, BorderLayout.SOUTH);

        generateSelectedReport();
    }

    private void generateSelectedReport() {
        int idx = cmbReportType.getSelectedIndex();
        switch (idx) {
            case 0: loadInventoryValuationReport(); break;
            case 1: loadPartsConsumptionReport(); break;
            case 2: loadStockLedgerReport(); break;
            case 3: loadMonthlyFinancialReport(); break;
            case 4: loadLowStockReorderReport(); break;
        }
    }

    private void loadInventoryValuationReport() {
        String[] cols = {"Kode Part", "Nama Suku Cadang", "Kategori", "Merek", "Lokasi Rak/Bin", "Stok Fisik", "Harga Beli Satuan", "Harga Jual", "Total Valuasi Aset", "Status"};
        reportTableModel.setDataVector(new Object[][]{}, cols);
        reportTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        reportTable.getColumnModel().getColumn(7).setCellRenderer(new CurrencyCellRenderer());
        reportTable.getColumnModel().getColumn(8).setCellRenderer(new CurrencyCellRenderer());
        reportTable.getColumnModel().getColumn(9).setCellRenderer(new BadgeRenderer());

        List<Part> parts = partDao.findAll(null, null, null);
        BigDecimal totalVal = BigDecimal.ZERO;
        int totalQty = 0;

        for (Part p : parts) {
            BigDecimal val = p.getStockValuation();
            totalVal = totalVal.add(val);
            totalQty += p.getCurrentStock();
            reportTableModel.addRow(new Object[]{
                    p.getPartNumber(), p.getPartName(), p.getCategoryName(), p.getBrand(),
                    p.getStorageBin(), p.getCurrentStock() + " " + p.getUnitOfMeasure(),
                    p.getCostPrice(), p.getSellingPrice(), val, p.getStatus().name()
            });
        }
        lblReportSummary.setText(String.format("Laporan Valuasi: %d Suku Cadang | Total Stok Fisik: %d unit | Nilai Total Aset: %s",
                parts.size(), totalQty, CurrencyUtil.format(totalVal)));
    }

    private void loadPartsConsumptionReport() {
        String[] cols = {"Ranking", "Kode Part", "Nama Suku Cadang", "Kuantitas Konsumsi", "Total Omset Pemakaian (Rp)"};
        reportTableModel.setDataVector(new Object[][]{}, cols);
        reportTable.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());

        List<Map<String, Object>> topParts = dashboardDao.getTop5ConsumedParts2022();
        int rank = 1;
        int totalQty = 0;
        BigDecimal totalRev = BigDecimal.ZERO;

        for (Map<String, Object> r : topParts) {
            int qty = (int) r.get("totalQty");
            BigDecimal rev = (BigDecimal) r.get("totalRevenue");
            totalQty += qty;
            totalRev = totalRev.add(rev);

            reportTableModel.addRow(new Object[]{
                    "#" + rank++,
                    r.get("partNumber"),
                    r.get("partName"),
                    qty + " Unit",
                    rev
            });
        }
        lblReportSummary.setText(String.format("Top Suku Cadang Terlaris | Total Terpakai: %d unit | Akumulasi Omset: %s",
                totalQty, CurrencyUtil.format(totalRev)));
    }

    private void loadStockLedgerReport() {
        String[] cols = {"Waktu Mutasi", "Kode Part", "Nama Suku Cadang", "Tipe Mutasi", "No. Referensi", "Mutasi (+/-)", "Sebelum", "Sesudah", "Harga Satuan", "Petugas"};
        reportTableModel.setDataVector(new Object[][]{}, cols);
        reportTable.getColumnModel().getColumn(8).setCellRenderer(new CurrencyCellRenderer());

        List<StockLedger> list = ledgerDao.findAll(null, null, null, "2022-01-01", "2022-12-31", 300);
        for (StockLedger sl : list) {
            reportTableModel.addRow(new Object[]{
                    DateUtil.formatDateTime(sl.getTransactionTime()),
                    sl.getPartNumber(),
                    sl.getPartName(),
                    sl.getTransactionType(),
                    sl.getReferenceNo(),
                    (sl.getQuantityChange() > 0 ? "+" : "") + sl.getQuantityChange(),
                    sl.getStockBefore(),
                    sl.getStockAfter(),
                    sl.getUnitCost(),
                    sl.getCreatedByName() != null ? sl.getCreatedByName() : "-"
            });
        }
        lblReportSummary.setText(String.format("Audit Buku Besar Mutasi Stok: Menampilkan %d rekaman pergerakan barang", list.size()));
    }

    private void loadMonthlyFinancialReport() {
        String[] cols = {"Bulan", "Total Pendapatan Bengkel (Parts + Jasa)", "Estimasi Pajak PPN (11%)", "Status"};
        reportTableModel.setDataVector(new Object[][]{}, cols);
        reportTable.getColumnModel().getColumn(1).setCellRenderer(new CurrencyCellRenderer());
        reportTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
        reportTable.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer());

        Map<Integer, BigDecimal> monthlyData = dashboardDao.getMonthlyRevenue2022();
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                           "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

        BigDecimal grandTotal = BigDecimal.ZERO;
        for (int m = 1; m <= 12; m++) {
            BigDecimal rev = monthlyData.getOrDefault(m, BigDecimal.ZERO);
            grandTotal = grandTotal.add(rev);
            BigDecimal tax = rev.multiply(new BigDecimal("0.11")).setScale(0, BigDecimal.ROUND_HALF_UP);

            reportTableModel.addRow(new Object[]{
                    months[m - 1],
                    rev,
                    tax,
                    "COMPLETED"
            });
        }
        lblReportSummary.setText(String.format("Rekapitulasi Keuangan Bulanan | Total Omset: %s", CurrencyUtil.format(grandTotal)));
    }

    private void loadLowStockReorderReport() {
        String[] cols = {"Kode Part", "Nama Suku Cadang", "Kategori", "Merek", "Lokasi Bin", "Stok Tersisa", "Safety Stock (Min)", "Rekomendasi Pesan (PO)", "Status"};
        reportTableModel.setDataVector(new Object[][]{}, cols);
        reportTable.getColumnModel().getColumn(8).setCellRenderer(new BadgeRenderer());

        List<Part> lowParts = partDao.findLowStock();
        for (Part p : lowParts) {
            int suggestedOrder = Math.max(10, (p.getMinStock() * 3) - p.getCurrentStock());
            reportTableModel.addRow(new Object[]{
                    p.getPartNumber(),
                    p.getPartName(),
                    p.getCategoryName(),
                    p.getBrand(),
                    p.getStorageBin(),
                    p.getCurrentStock() + " " + p.getUnitOfMeasure(),
                    p.getMinStock() + " " + p.getUnitOfMeasure(),
                    suggestedOrder + " " + p.getUnitOfMeasure(),
                    p.getStatus().name()
            });
        }
        lblReportSummary.setText(String.format("Peringatan Stok: Ditemukan %d suku cadang di bawah batas keselamatan minimum", lowParts.size()));
    }

    private void onExportPdf() {
        int idx = cmbReportType.getSelectedIndex();
        JFileChooser chooser = new JFileChooser();

        if (idx == 0) {
            chooser.setSelectedFile(new File("Laporan_Valuasi_Aset_Inventaris.pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File target = chooser.getSelectedFile();
                if (!target.getName().toLowerCase().endsWith(".pdf")) target = new File(target.getAbsolutePath() + ".pdf");
                boolean ok = reportService.exportInventoryValuationPdf(target);
                if (ok) promptOpenFile(target);
            }
        } else {
            // For other reports, export table directly or notify
            JOptionPane.showMessageDialog(this,
                    "Dokumen laporan telah siap dicetak dan diexport.\nSilakan gunakan 'Export Excel / CSV' atau cetak Laporan Valuasi Aset / Kartu Stok ke PDF.",
                    "Informasi Dokumen", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void promptOpenFile(File file) {
        int open = JOptionPane.showConfirmDialog(this, "Laporan berhasil dicetak ke PDF:\n" + file.getAbsolutePath() + "\n\nBuka sekarang?", "Sukses", JOptionPane.YES_NO_OPTION);
        if (open == JOptionPane.YES_OPTION) {
            try { Desktop.getDesktop().open(file); } catch (Exception ignored) {}
        }
    }

    private void onExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Laporan_Audit_Bengkel.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ReportService.exportTableToCsv(reportTable, chooser.getSelectedFile());
        }
    }
}
