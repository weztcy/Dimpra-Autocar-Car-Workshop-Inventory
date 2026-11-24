package com.workshop.ui.service;

import com.workshop.dao.ServiceTransactionDao;
import com.workshop.model.ServiceTransaction;
import com.workshop.service.ReportService;
import com.workshop.service.ServiceBillingService;
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

public class ServicePanel extends JPanel {
    private final ServiceTransactionDao trxDao = new ServiceTransactionDao();
    private final ServiceBillingService billingService = new ServiceBillingService();
    private final ReportService reportService = new ReportService();

    private final SearchTextField txtSearch = new SearchTextField("Cari No PKB, plat mobil, pelanggan, jenis servis...");
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ALL", "COMPLETED", "IN_PROGRESS", "PENDING", "CANCELLED"});
    private final JTextField txtDateFrom = new JTextField("2022-01-01", 10);
    private final JTextField txtDateTo = new JTextField("2022-12-31", 10);

    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private final JLabel lblSummary = new JLabel("Memuat data transaksi servis...");

    private List<ServiceTransaction> currentTrxList = new ArrayList<>();

    public ServicePanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // 1. Top Section (Title + Toolbar)
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        // Header Row: Title & Primary Actions
        JPanel pnlHeader = new JPanel(new BorderLayout(16, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Transaksi Servis Kendaraan (PKB)");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_MAIN);

        JLabel lblSub = new JLabel("Perintah Kerja Bengkel, pemakaian spare parts, dan invoice penagihan kasir");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);

        JPanel pnlGlobalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlGlobalActions.setOpaque(false);

        JButton btnNewWo = UIStyle.createPrimaryButton("+ Buka Servis (PKB)");
        btnNewWo.addActionListener(e -> onNewWorkOrder());

        JButton btnComplete = UIStyle.createSuccessButton("Selesaikan & Bayar");
        btnComplete.addActionListener(e -> onCompleteOrder());

        JButton btnExport = UIStyle.createSecondaryButton("Export CSV");
        btnExport.addActionListener(e -> onExportCsv());

        pnlGlobalActions.add(btnNewWo);
        pnlGlobalActions.add(btnComplete);
        pnlGlobalActions.add(btnExport);
        pnlHeader.add(pnlGlobalActions, BorderLayout.EAST);

        pnlTop.add(pnlHeader);
        pnlTop.add(Box.createVerticalStrut(12));

        // Toolbar: Search/Filters on Left, Actions on Right
        JPanel pnlToolbar = UIStyle.createCardPanel(8, 12, 8, 12);
        pnlToolbar.setLayout(new BorderLayout(10, 0));

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFilters.setOpaque(false);

        txtSearch.setPreferredSize(new Dimension(145, 32));
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

        JButton btnApplyFilter = UIStyle.createSecondaryButton("Filter");
        btnApplyFilter.setPreferredSize(new Dimension(55, 32));
        btnApplyFilter.addActionListener(e -> loadData());
        pnlFilters.add(btnApplyFilter);

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

        // Actions on Right of Toolbar
        JPanel pnlRowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlRowActions.setOpaque(false);

        JButton btnPrintInvoice = UIStyle.createSecondaryButton("Cetak Faktur");
        btnPrintInvoice.setPreferredSize(new Dimension(100, 32));
        btnPrintInvoice.addActionListener(e -> onPrintInvoice());

        JButton btnRegVehicle = UIStyle.createSecondaryButton("Kendaraan");
        btnRegVehicle.setPreferredSize(new Dimension(90, 32));
        btnRegVehicle.addActionListener(e -> onRegisterVehicle());

        pnlRowActions.add(btnPrintInvoice);
        pnlRowActions.add(btnRegVehicle);
        pnlToolbar.add(pnlRowActions, BorderLayout.EAST);

        pnlTop.add(pnlToolbar);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table Section
        String[] cols = {
                "No. PKB", "Waktu Transaksi", "No. Polisi", "Tipe Mobil", "Pelanggan",
                "Jenis Servis", "Teknisi", "Biaya Part", "Biaya Jasa", "Grand Total", "Status Servis", "Status Bayar"
        };
        tableModel = new DefaultTableModel(cols, 0);
        table = new ModernTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(95);
        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(95);
        table.getColumnModel().getColumn(4).setPreferredWidth(105);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(85);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        table.getColumnModel().getColumn(9).setPreferredWidth(90);
        table.getColumnModel().getColumn(10).setPreferredWidth(85);
        table.getColumnModel().getColumn(11).setPreferredWidth(80);

        table.getColumnModel().getColumn(7).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(10).setCellRenderer(new BadgeRenderer());
        table.getColumnModel().getColumn(11).setCellRenderer(new BadgeRenderer());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onPrintInvoice();
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

            currentTrxList = trxDao.findAll(keyword, status, dateFrom, dateTo);
            tableModel.setRowCount(0);

            BigDecimal grandTotalRevenue = BigDecimal.ZERO;
            for (ServiceTransaction t : currentTrxList) {
                grandTotalRevenue = grandTotalRevenue.add(t.getGrandTotal());
                tableModel.addRow(new Object[]{
                        t.getTransactionCode(),
                        DateUtil.formatDateTime(t.getTransactionDate()),
                        t.getPlateNumber(),
                        t.getVehicleModel(),
                        t.getCustomerName(),
                        t.getServiceType(),
                        t.getTechnicianName(),
                        t.getPartsTotal(),
                        t.getLaborTotal(),
                        t.getGrandTotal(),
                        t.getStatus(),
                        t.getPaymentStatus()
                });
            }

            lblSummary.setText(String.format("Menampilkan %d transaksi servis | Total Akumulasi Omset: %s",
                    currentTrxList.size(), CurrencyUtil.format(grandTotalRevenue)));
        });
    }

    private ServiceTransaction getSelectedTransaction() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow >= 0 && modelRow < currentTrxList.size()) {
            return trxDao.findById(currentTrxList.get(modelRow).getId());
        }
        return null;
    }

    private void onNewWorkOrder() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        WorkOrderDialog dlg = new WorkOrderDialog(owner);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void onPrintInvoice() {
        ServiceTransaction trx = getSelectedTransaction();
        if (trx == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih transaksi servis yang ingin dicetak fakturnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Faktur_" + trx.getTransactionCode() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".pdf")) {
                target = new File(target.getAbsolutePath() + ".pdf");
            }
            boolean ok = reportService.exportInvoicePdf(trx.getId(), target);
            if (ok) {
                int open = JOptionPane.showConfirmDialog(this, "Faktur Servis berhasil dicetak ke PDF:\n" + target.getAbsolutePath() + "\n\nBuka sekarang?", "Sukses", JOptionPane.YES_NO_OPTION);
                if (open == JOptionPane.YES_OPTION) {
                    try { Desktop.getDesktop().open(target); } catch (Exception ignored) {}
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencetak Faktur Servis.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onCompleteOrder() {
        ServiceTransaction trx = getSelectedTransaction();
        if (trx == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih transaksi servis dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("COMPLETED".equals(trx.getStatus()) && "PAID".equals(trx.getPaymentStatus())) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah berstatus SELESAI & LUNAS.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> cmbPayMethod = new JComboBox<>(new String[]{"CASH", "TRANSFER", "DEBIT_CARD", "QRIS", "CREDIT_CARD"});
        JPanel pnl = new JPanel(new GridLayout(2, 2, 8, 8));
        pnl.add(new JLabel("Tagihan:"));
        pnl.add(new JLabel(CurrencyUtil.format(trx.getGrandTotal())));
        pnl.add(new JLabel("Metode Pembayaran:"));
        pnl.add(cmbPayMethod);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Pelunasan Transaksi Servis " + trx.getTransactionCode(), JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String method = (String) cmbPayMethod.getSelectedItem();
            boolean ok = billingService.updateStatusAndPayment(trx.getId(), "COMPLETED", method, "PAID");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diselesaikan dan status diperbarui menjadi LUNAS (PAID)!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui status transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRegisterVehicle() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerVehicleDialog dlg = new CustomerVehicleDialog(owner);
        dlg.setVisible(true);
    }

    private void onExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Laporan_Transaksi_Servis.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ReportService.exportTableToCsv(table, chooser.getSelectedFile());
        }
    }
}
