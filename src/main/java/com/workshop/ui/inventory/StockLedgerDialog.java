package com.workshop.ui.inventory;

import com.workshop.dao.StockLedgerDao;
import com.workshop.model.Part;
import com.workshop.model.StockLedger;
import com.workshop.service.ReportService;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StockLedgerDialog extends JDialog {
    private final StockLedgerDao ledgerDao = new StockLedgerDao();
    private final ReportService reportService = new ReportService();
    private final Part part;
    private final DefaultTableModel tableModel;

    public StockLedgerDialog(Frame owner, Part part) {
        super(owner, "Kartu Stok Relasional (Stock Card Audit Trail): " + part.getPartNumber(), true);
        this.part = part;

        setSize(920, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));

        // Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(16, 20, 8, 20));
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel(part.getPartNumber() + " - " + part.getPartName() + " (" + part.getBrand() + ")");
        lblTitle.setFont(UIStyle.FONT_HEADER);

        JLabel lblDetails = new JLabel(String.format("Kategori: %s | Lokasi Bin: %s | Stok Saat Ini: %d %s | Min Safety: %d %s",
                part.getCategoryName(), part.getStorageBin(), part.getCurrentStock(), part.getUnitOfMeasure(),
                part.getMinStock(), part.getUnitOfMeasure()));
        lblDetails.setFont(UIStyle.FONT_SMALL);
        lblDetails.setForeground(UIStyle.TEXT_MUTED);

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);
        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(4));
        pnlTitleBox.add(lblDetails);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);

        JButton btnExportPdf = UIStyle.createPrimaryButton("Cetak Kartu Stok (PDF)");
        btnExportPdf.addActionListener(e -> onExportPdf());
        pnlHeader.add(btnExportPdf, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // Table
        String[] cols = {"Waktu Transaksi", "Jenis Mutasi", "No. Referensi", "Mutasi (+/-)", "Sebelum", "Sesudah", "Harga Satuan", "Keterangan", "Petugas"};
        tableModel = new DefaultTableModel(cols, 0);
        ModernTable table = new ModernTable(tableModel);

        // Render quantity change colored
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(UIStyle.FONT_BOLD);
                if (val != null && !isSel) {
                    String str = val.toString();
                    if (str.startsWith("+")) {
                        l.setForeground(new Color(22, 101, 52)); // Green
                    } else if (str.startsWith("-")) {
                        l.setForeground(new Color(185, 28, 28)); // Red
                    }
                }
                return l;
            }
        });

        table.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        JButton btnClose = UIStyle.createSecondaryButton("Tutup");
        btnClose.addActionListener(e -> dispose());
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);

        loadLedgerData();
    }

    private void loadLedgerData() {
        tableModel.setRowCount(0);
        List<StockLedger> list = ledgerDao.findByPartId(part.getId());
        for (StockLedger sl : list) {
            tableModel.addRow(new Object[]{
                    DateUtil.formatDateTime(sl.getTransactionTime()),
                    sl.getTransactionType(),
                    sl.getReferenceNo(),
                    (sl.getQuantityChange() > 0 ? "+" : "") + sl.getQuantityChange(),
                    sl.getStockBefore(),
                    sl.getStockAfter(),
                    sl.getUnitCost(),
                    sl.getNotes(),
                    sl.getCreatedByName() != null ? sl.getCreatedByName() : "-"
            });
        }
    }

    private void onExportPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Kartu_Stok_" + part.getPartNumber() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            boolean success = reportService.exportStockCardPdf(part.getId(), file);
            if (success) {
                int open = JOptionPane.showConfirmDialog(this, "Kartu stok berhasil dicetak ke PDF:\n" + file.getAbsolutePath() + "\n\nBuka file sekarang?", "Sukses", JOptionPane.YES_NO_OPTION);
                if (open == JOptionPane.YES_OPTION) {
                    try { Desktop.getDesktop().open(file); } catch (Exception ignored) {}
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencetak file PDF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
