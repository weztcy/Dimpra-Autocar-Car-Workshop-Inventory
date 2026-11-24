package com.workshop.ui.procurement;

import com.workshop.dao.PurchaseOrderDao;
import com.workshop.model.PurchaseOrder;
import com.workshop.service.ReportService;
import com.workshop.ui.common.BadgeRenderer;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.common.SearchTextField;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProcurementPanel extends JPanel {
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final ReportService reportService = new ReportService();

    private final SearchTextField txtSearch = new SearchTextField("Cari No PO, supplier, catatan...");
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ALL", "ORDERED", "RECEIVED_FULL", "RECEIVED_PARTIAL", "DRAFT"});
    private final JTextField txtDateFrom = new JTextField("2022-01-01", 10);
    private final JTextField txtDateTo = new JTextField("2022-12-31", 10);

    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private final JLabel lblSummary = new JLabel("Memuat data pengadaan...");

    private List<PurchaseOrder> currentPoList = new ArrayList<>();

    public ProcurementPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // 1. Top Section (Title + Actions + Filters)
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        // Header Row: Title & Global Actions
        JPanel pnlHeader = new JPanel(new BorderLayout(16, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Pengadaan Suku Cadang (Purchase Order)");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_MAIN);

        JLabel lblSub = new JLabel("Penerbitan PO ke distributor resmi & pencatatan penerimaan barang gudang");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);

        JPanel pnlGlobalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlGlobalActions.setOpaque(false);

        JButton btnNewPo = UIStyle.createPrimaryButton("+ Buat Purchase Order (PO)");
        btnNewPo.addActionListener(e -> onCreatePo());

        JButton btnReceive = UIStyle.createSuccessButton("Terima Barang (GRN)");
        btnReceive.addActionListener(e -> onReceiveGoods());

        JButton btnExport = UIStyle.createSecondaryButton("Export CSV");
        btnExport.addActionListener(e -> onExportCsv());

        pnlGlobalActions.add(btnNewPo);
        pnlGlobalActions.add(btnReceive);
        pnlGlobalActions.add(btnExport);
        pnlHeader.add(pnlGlobalActions, BorderLayout.EAST);

        pnlTop.add(pnlHeader);
        pnlTop.add(Box.createVerticalStrut(12));

        // Toolbar: Search/Filters on Left, Specific Actions on Right
        JPanel pnlToolbar = UIStyle.createCardPanel(8, 12, 8, 12);
        pnlToolbar.setLayout(new BorderLayout(10, 0));

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFilters.setOpaque(false);

        txtSearch.setPreferredSize(new Dimension(150, 32));
        pnlFilters.add(txtSearch);

        cmbStatus.setPreferredSize(new Dimension(120, 32));
        cmbStatus.addActionListener(e -> loadData());
        pnlFilters.add(cmbStatus);

        pnlFilters.add(new JLabel("Dari:"));
        txtDateFrom.setPreferredSize(new Dimension(80, 32));
        pnlFilters.add(txtDateFrom);

        pnlFilters.add(new JLabel("Sampai:"));
        txtDateTo.setPreferredSize(new Dimension(80, 32));
        pnlFilters.add(txtDateTo);

        JButton btnFilterDate = UIStyle.createSecondaryButton("Filter");
        btnFilterDate.setPreferredSize(new Dimension(55, 32));
        btnFilterDate.addActionListener(e -> loadData());
        pnlFilters.add(btnFilterDate);

        JButton btnReset = UIStyle.createSecondaryButton("Reset");
        btnReset.setPreferredSize(new Dimension(55, 32));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatus.setSelectedIndex(0);
            txtDateFrom.setText("2022-01-01");
            txtDateTo.setText("2022-12-31");
            loadData();
        });
        pnlFilters.add(btnReset);
        pnlToolbar.add(pnlFilters, BorderLayout.WEST);

        // Specific actions on Right of Toolbar
        JPanel pnlRowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlRowActions.setOpaque(false);

        JButton btnPrintPo = UIStyle.createSecondaryButton("Cetak PO");
        btnPrintPo.setPreferredSize(new Dimension(85, 32));
        btnPrintPo.addActionListener(e -> onPrintPo());

        JButton btnSuppliers = UIStyle.createSecondaryButton("Kelola Supplier");
        btnSuppliers.setPreferredSize(new Dimension(105, 32));
        btnSuppliers.addActionListener(e -> onManageSuppliers());

        pnlRowActions.add(btnPrintPo);
        pnlRowActions.add(btnSuppliers);
        pnlToolbar.add(pnlRowActions, BorderLayout.EAST);

        pnlTop.add(pnlToolbar);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table Section
        String[] cols = {
                "No. PO", "Supplier / Vendor", "Tgl Pesanan", "Estimasi Tiba", "Tgl Diterima",
                "Status Barang", "Status Bayar", "Total Nilai PO", "Petugas Input"
        };
        tableModel = new DefaultTableModel(cols, 0);
        table = new ModernTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(95);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(85);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(105);
        table.getColumnModel().getColumn(8).setPreferredWidth(95);

        table.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new BadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new CurrencyCellRenderer());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onPrintPo();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Summary
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        pnlFooter.setOpaque(false);

        lblSummary.setFont(UIStyle.FONT_BOLD);
        pnlFooter.add(lblSummary, BorderLayout.WEST);

        add(pnlFooter, BorderLayout.SOUTH);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadData(); }
            public void removeUpdate(DocumentEvent e) { loadData(); }
            public void changedUpdate(DocumentEvent e) { loadData(); }
        });

        loadData();
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            String keyword = txtSearch.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();
            String dateFrom = txtDateFrom.getText().trim();
            String dateTo = txtDateTo.getText().trim();

            currentPoList = poDao.findAll(keyword, status, dateFrom, dateTo);
            tableModel.setRowCount(0);

            BigDecimal grandTotal = BigDecimal.ZERO;
            for (PurchaseOrder po : currentPoList) {
                grandTotal = grandTotal.add(po.getTotalAmount());
                tableModel.addRow(new Object[]{
                        po.getPoNumber(),
                        po.getSupplierName(),
                        DateUtil.formatDate(po.getOrderDate()),
                        DateUtil.formatDate(po.getExpectedDate()),
                        DateUtil.formatDate(po.getReceivedDate()),
                        po.getStatus(),
                        po.getPaymentStatus(),
                        po.getTotalAmount(),
                        po.getCreatedByName() != null ? po.getCreatedByName() : "-"
                });
            }

            lblSummary.setText(String.format("Menampilkan %d Purchase Order | Total Nilai Pengadaan: %s",
                    currentPoList.size(), CurrencyUtil.format(grandTotal)));
        });
    }

    private PurchaseOrder getSelectedPo() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow >= 0 && modelRow < currentPoList.size()) {
            return poDao.findById(currentPoList.get(modelRow).getId());
        }
        return null;
    }

    private void onCreatePo() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        PurchaseOrderDialog dlg = new PurchaseOrderDialog(owner);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void onReceiveGoods() {
        PurchaseOrder po = getSelectedPo();
        if (po == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih Purchase Order dari tabel yang ingin diterima barangnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("RECEIVED_FULL".equals(po.getStatus())) {
            JOptionPane.showMessageDialog(this, "PO ini sudah berstatus DITERIMA LENGKAP (RECEIVED_FULL).", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ReceiveGoodsDialog dlg = new ReceiveGoodsDialog(owner, po);
        dlg.setVisible(true);
        if (dlg.isReceived()) {
            loadData();
        }
    }

    private void onPrintPo() {
        PurchaseOrder po = getSelectedPo();
        if (po == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih Purchase Order yang ingin dicetak.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("PO_" + po.getPoNumber() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".pdf")) {
                target = new File(target.getAbsolutePath() + ".pdf");
            }
            boolean ok = reportService.exportPurchaseOrderPdf(po.getId(), target);
            if (ok) {
                int open = JOptionPane.showConfirmDialog(this, "Dokumen Purchase Order berhasil dicetak ke PDF:\n" + target.getAbsolutePath() + "\n\nBuka sekarang?", "Sukses", JOptionPane.YES_NO_OPTION);
                if (open == JOptionPane.YES_OPTION) {
                    try { Desktop.getDesktop().open(target); } catch (Exception ignored) {}
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencetak dokumen Purchase Order.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onManageSuppliers() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        SupplierDialog dlg = new SupplierDialog(owner, null);
        dlg.setVisible(true);
    }

    private void onExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Laporan_Purchase_Orders.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ReportService.exportTableToCsv(table, chooser.getSelectedFile());
        }
    }
}
