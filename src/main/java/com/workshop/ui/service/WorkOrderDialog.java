package com.workshop.ui.service;

import com.workshop.dao.PartDao;
import com.workshop.dao.ServiceTransactionDao;
import com.workshop.dao.UserDao;
import com.workshop.dao.VehicleDao;
import com.workshop.model.*;
import com.workshop.service.AuthService;
import com.workshop.service.ServiceBillingService;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDialog extends JDialog {
    private final ServiceTransactionDao trxDao = new ServiceTransactionDao();
    private final VehicleDao vehicleDao = new VehicleDao();
    private final UserDao userDao = new UserDao();
    private final PartDao partDao = new PartDao();
    private final ServiceBillingService billingService = new ServiceBillingService();

    private final JTextField txtTrxCode = new JTextField();
    private final JComboBox<Vehicle> cmbVehicle = new JComboBox<>();
    private final JComboBox<User> cmbTechnician = new JComboBox<>();
    private final JComboBox<String> cmbServiceType = new JComboBox<>(new String[]{
            "Periodic Maintenance 10,000 KM",
            "Periodic Maintenance 20,000 KM",
            "Periodic Maintenance 40,000 KM Major",
            "Front & Rear Brake Overhaul",
            "Diesel Major Service & Fuel Filter",
            "Automatic Transmission Flushing & Service",
            "Manual Clutch System Overhaul",
            "Front Suspension & Steering Overhaul",
            "Complete AC Service & Evaporator Clean",
            "Battery Replacement & Electrical Check",
            "Cooling System Overhaul & Radiator Service",
            "Engine Tune-Up & Throttle Body Clean",
            "Tire Replacement & 4-Wheel Balancing",
            "General Diagnostic & Check-Up"
    });
    private final JTextField txtOdometer = new JTextField("45000");
    private final JTextField txtDate = new JTextField(DateUtil.toIsoDate(new java.util.Date()) + " 10:00:00");
    private final JTextArea txtComplaint = new JTextArea(2, 20);
    private final JTextArea txtDiagnosis = new JTextArea(2, 20);

    // Line items
    private final DefaultTableModel partsModel;
    private final ModernTable partsTable;
    private final List<ServicePartUsed> partsUsed = new ArrayList<>();

    private final DefaultTableModel laborModel;
    private final ModernTable laborTable;
    private final List<ServiceLaborItem> laborItems = new ArrayList<>();

    // Totals display
    private final JLabel lblPartsSum = new JLabel("Parts: Rp 0");
    private final JLabel lblLaborSum = new JLabel("Jasa: Rp 0");
    private final JTextField txtDiscount = new JTextField("0", 8);
    private final JLabel lblTax = new JLabel("PPN 11%: Rp 0");
    private final JLabel lblGrandTotal = new JLabel("Grand Total: Rp 0");

    private final JComboBox<String> cmbPaymentMethod = new JComboBox<>(new String[]{"CASH", "TRANSFER", "DEBIT_CARD", "QRIS", "CREDIT_CARD"});
    private final JComboBox<String> cmbPaymentStatus = new JComboBox<>(new String[]{"PAID", "UNPAID"});

    private boolean saved = false;

    public WorkOrderDialog(Frame owner) {
        super(owner, "Buka Perintah Kerja Bengkel (Work Order / PKB)", true);

        setSize(880, 680);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        txtTrxCode.setText(trxDao.generateNextTrxCode());
        txtTrxCode.setEditable(false);

        loadVehicles();
        loadTechnicians();

        // 1. Header Information Panel
        JPanel pnlHeader = new JPanel(new GridBagLayout());
        pnlHeader.setBorder(new EmptyBorder(14, 20, 8, 20));
        pnlHeader.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("No. PKB:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.38;
        pnlHeader.add(txtTrxCode, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Waktu Servis:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.38;
        pnlHeader.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Pilih Kendaraan:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.38;
        pnlHeader.add(cmbVehicle, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Odometer (KM):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.38;
        pnlHeader.add(txtOdometer, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Teknisi Servis:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.38;
        pnlHeader.add(cmbTechnician, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Jenis Pekerjaan:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.38;
        pnlHeader.add(cmbServiceType, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Keluhan Pelanggan:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.38;
        txtComplaint.setLineWrap(true);
        pnlHeader.add(new JScrollPane(txtComplaint), gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.12;
        pnlHeader.add(new JLabel("Diagnosa Teknisi:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.38;
        txtDiagnosis.setLineWrap(true);
        pnlHeader.add(new JScrollPane(txtDiagnosis), gbc);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. Center Tabs (Parts Used & Labor Items)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Tab 1: Suku Cadang (Parts Used)
        JPanel pnlParts = new JPanel(new BorderLayout(6, 6));
        pnlParts.setOpaque(false);

        JPanel pnlPartBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlPartBtns.setOpaque(false);
        JButton btnAddPart = UIStyle.createPrimaryButton("+ Tambah Suku Cadang");
        btnAddPart.addActionListener(e -> onAddPart());
        JButton btnDelPart = UIStyle.createSecondaryButton("Hapus Baris");
        btnDelPart.addActionListener(e -> onRemovePart());
        pnlPartBtns.add(btnAddPart);
        pnlPartBtns.add(btnDelPart);
        pnlParts.add(pnlPartBtns, BorderLayout.NORTH);

        String[] pCols = {"Kode Part", "Nama Suku Cadang", "Merek", "Qty", "Satuan", "Harga Jual", "Subtotal"};
        partsModel = new DefaultTableModel(pCols, 0);
        partsTable = new ModernTable(partsModel);
        partsTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
        partsTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        pnlParts.add(new JScrollPane(partsTable), BorderLayout.CENTER);
        tabbedPane.addTab("1. Suku Cadang yang Digunakan", pnlParts);

        // Tab 2: Biaya Jasa (Labor Items)
        JPanel pnlLabor = new JPanel(new BorderLayout(6, 6));
        pnlLabor.setOpaque(false);

        JPanel pnlLaborBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlLaborBtns.setOpaque(false);
        JButton btnAddLabor = UIStyle.createPrimaryButton("+ Tambah Jasa Servis");
        btnAddLabor.addActionListener(e -> onAddLabor());
        JButton btnDelLabor = UIStyle.createSecondaryButton("Hapus Baris");
        btnDelLabor.addActionListener(e -> onRemoveLabor());
        pnlLaborBtns.add(btnAddLabor);
        pnlLaborBtns.add(btnDelLabor);
        pnlLabor.add(pnlLaborBtns, BorderLayout.NORTH);

        String[] lCols = {"Deskripsi Layanan / Perbaikan", "Waktu (Jam)", "Tarif per Jam", "Subtotal"};
        laborModel = new DefaultTableModel(lCols, 0);
        laborTable = new ModernTable(laborModel);
        laborTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
        laborTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyCellRenderer());
        pnlLabor.add(new JScrollPane(laborTable), BorderLayout.CENTER);
        tabbedPane.addTab("2. Jasa Perbaikan / Mekanik", pnlLabor);

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Footer (Financial Breakdown + Save Buttons)
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(10, 20, 16, 20));
        pnlFooter.setOpaque(false);

        // Totals Box
        JPanel pnlTotals = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        pnlTotals.setOpaque(false);
        pnlTotals.add(lblPartsSum);
        pnlTotals.add(lblLaborSum);
        pnlTotals.add(new JLabel("Diskon (Rp):"));
        txtDiscount.addActionListener(e -> recalculateAll());
        pnlTotals.add(txtDiscount);
        pnlTotals.add(lblTax);
        lblGrandTotal.setFont(UIStyle.FONT_TITLE);
        lblGrandTotal.setForeground(UIStyle.PRIMARY);
        pnlTotals.add(lblGrandTotal);
        pnlFooter.add(pnlTotals, BorderLayout.NORTH);

        // Actions and Payment
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlActions.setOpaque(false);

        pnlActions.add(new JLabel("Metode Bayar:"));
        pnlActions.add(cmbPaymentMethod);
        pnlActions.add(new JLabel("Status:"));
        pnlActions.add(cmbPaymentStatus);

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSaveOrder = UIStyle.createPrimaryButton("Simpan Work Order (IN_PROGRESS)");
        btnSaveOrder.addActionListener(e -> saveWorkOrder("IN_PROGRESS"));

        JButton btnCompletePay = UIStyle.createSuccessButton("Selesaikan & Bayar (COMPLETED)");
        btnCompletePay.addActionListener(e -> saveWorkOrder("COMPLETED"));

        pnlActions.add(btnCancel);
        pnlActions.add(btnSaveOrder);
        pnlActions.add(btnCompletePay);
        pnlFooter.add(pnlActions, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadVehicles() {
        for (Vehicle v : vehicleDao.findAll(null)) {
            cmbVehicle.addItem(v);
        }
    }

    private void loadTechnicians() {
        for (User u : userDao.findByRole(Role.SERVICE_TECHNICIAN)) {
            cmbTechnician.addItem(u);
        }
    }

    private void onAddPart() {
        List<Part> allParts = partDao.findAll(null, null, null);
        JComboBox<Part> cmbPart = new JComboBox<>(allParts.toArray(new Part[0]));
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JLabel lblAvail = new JLabel("Stok Tersedia: -");

        cmbPart.addActionListener(e -> {
            Part p = (Part) cmbPart.getSelectedItem();
            if (p != null) {
                lblAvail.setText(String.format("Stok Tersedia: %d %s (Harga Jual: %s)", p.getCurrentStock(), p.getUnitOfMeasure(), CurrencyUtil.format(p.getSellingPrice())));
                if (p.getCurrentStock() <= 0) {
                    lblAvail.setForeground(UIStyle.DANGER);
                } else if (p.getCurrentStock() <= p.getMinStock()) {
                    lblAvail.setForeground(UIStyle.WARNING);
                } else {
                    lblAvail.setForeground(UIStyle.SUCCESS);
                }
            }
        });
        if (cmbPart.getItemCount() > 0) cmbPart.setSelectedIndex(0);

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Pilih Suku Cadang:"));
        pnl.add(cmbPart);
        pnl.add(new JLabel("Kuantitas Dipakai (Qty):"));
        pnl.add(spnQty);
        pnl.add(new JLabel("Status Stok:"));
        pnl.add(lblAvail);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Tambah Pemakaian Suku Cadang", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Part sel = (Part) cmbPart.getSelectedItem();
            if (sel == null) return;
            int qty = (int) spnQty.getValue();

            if (sel.getCurrentStock() < qty) {
                int warn = JOptionPane.showConfirmDialog(this,
                        String.format("Peringatan: Stok fisik %s hanya tersisa %d %s.\nTetap lanjutkan pemakaian?",
                                sel.getPartName(), sel.getCurrentStock(), sel.getUnitOfMeasure()),
                        "Peringatan Stok Kurang", JOptionPane.YES_NO_OPTION);
                if (warn != JOptionPane.YES_OPTION) return;
            }

            ServicePartUsed pu = new ServicePartUsed();
            pu.setPartId(sel.getId());
            pu.setPartNumber(sel.getPartNumber());
            pu.setPartName(sel.getPartName());
            pu.setBrand(sel.getBrand());
            pu.setUnitOfMeasure(sel.getUnitOfMeasure());
            pu.setQuantity(qty);
            pu.setUnitPrice(sel.getSellingPrice());
            pu.recalculateSubtotal();

            partsUsed.add(pu);
            partsModel.addRow(new Object[]{
                    pu.getPartNumber(), pu.getPartName(), pu.getBrand(),
                    pu.getQuantity(), pu.getUnitOfMeasure(), pu.getUnitPrice(), pu.getSubtotal()
            });

            recalculateAll();
        }
    }

    private void onRemovePart() {
        int r = partsTable.getSelectedRow();
        if (r >= 0 && r < partsUsed.size()) {
            partsUsed.remove(r);
            partsModel.removeRow(r);
            recalculateAll();
        }
    }

    private void onAddLabor() {
        JTextField txtDesc = new JTextField("Jasa Perbaikan / Pemeriksaan Berkala");
        JSpinner spnHours = new JSpinner(new SpinnerNumberModel(1.5, 0.5, 50.0, 0.5));
        JTextField txtRate = new JTextField("150000");

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Deskripsi Jasa:"));
        pnl.add(txtDesc);
        pnl.add(new JLabel("Waktu Pengerjaan (Jam):"));
        pnl.add(spnHours);
        pnl.add(new JLabel("Tarif per Jam (Rp):"));
        pnl.add(txtRate);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Tambah Item Jasa", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String desc = txtDesc.getText().trim();
            BigDecimal hrs = BigDecimal.valueOf((Double) spnHours.getValue());
            BigDecimal rate = CurrencyUtil.parse(txtRate.getText());
            BigDecimal subtotal = hrs.multiply(rate);

            ServiceLaborItem li = new ServiceLaborItem();
            li.setServiceName(desc);
            li.setHours(hrs);
            li.setRate(rate);
            li.setSubtotal(subtotal);

            laborItems.add(li);
            laborModel.addRow(new Object[]{li.getServiceName(), li.getHours(), li.getRate(), li.getSubtotal()});
            recalculateAll();
        }
    }

    private void onRemoveLabor() {
        int r = laborTable.getSelectedRow();
        if (r >= 0 && r < laborItems.size()) {
            laborItems.remove(r);
            laborModel.removeRow(r);
            recalculateAll();
        }
    }

    private void recalculateAll() {
        BigDecimal pSum = BigDecimal.ZERO;
        for (ServicePartUsed pu : partsUsed) pSum = pSum.add(pu.getSubtotal());

        BigDecimal lSum = BigDecimal.ZERO;
        for (ServiceLaborItem li : laborItems) lSum = lSum.add(li.getSubtotal());

        BigDecimal disc = CurrencyUtil.parse(txtDiscount.getText());
        BigDecimal sub = pSum.add(lSum).subtract(disc);
        if (sub.compareTo(BigDecimal.ZERO) < 0) sub = BigDecimal.ZERO;

        BigDecimal tax = sub.multiply(new BigDecimal("0.11")).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal grand = sub.add(tax);

        lblPartsSum.setText("Parts: " + CurrencyUtil.format(pSum));
        lblLaborSum.setText("Jasa: " + CurrencyUtil.format(lSum));
        lblTax.setText("PPN 11%: " + CurrencyUtil.format(tax));
        lblGrandTotal.setText("Grand Total: " + CurrencyUtil.format(grand));
    }

    private void saveWorkOrder(String status) {
        Vehicle v = (Vehicle) cmbVehicle.getSelectedItem();
        User tech = (User) cmbTechnician.getSelectedItem();

        if (v == null || tech == null) {
            JOptionPane.showMessageDialog(this, "Pilih kendaraan dan teknisi pemeriksa!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (partsUsed.isEmpty() && laborItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap tambahkan minimal 1 suku cadang atau 1 jasa perbaikan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServiceTransaction trx = new ServiceTransaction();
        trx.setTransactionCode(txtTrxCode.getText().trim());
        try {
            trx.setTransactionDate(Timestamp.valueOf(txtDate.getText().trim()));
        } catch (Exception e) {
            trx.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        }

        trx.setVehicleId(v.getId());
        trx.setTechnicianId(tech.getId());
        trx.setServiceType((String) cmbServiceType.getSelectedItem());
        try {
            trx.setOdometerIn(Integer.parseInt(txtOdometer.getText().trim()));
        } catch (Exception ignored) {
            trx.setOdometerIn(v.getOdometer());
        }

        trx.setCustomerComplaint(txtComplaint.getText().trim());
        trx.setTechnicianDiagnosis(txtDiagnosis.getText().trim());
        trx.setDiscount(CurrencyUtil.parse(txtDiscount.getText()));
        trx.setStatus(status);
        trx.setPaymentMethod((String) cmbPaymentMethod.getSelectedItem());
        trx.setPaymentStatus("COMPLETED".equals(status) ? "PAID" : (String) cmbPaymentStatus.getSelectedItem());
        trx.setCreatedBy(AuthService.getInstance().getCurrentUser() != null ? AuthService.getInstance().getCurrentUser().getId() : 1);

        trx.setPartsUsed(partsUsed);
        trx.setLaborItems(laborItems);
        trx.recalculateTotals();

        boolean deductStock = "COMPLETED".equals(status) || !partsUsed.isEmpty();
        boolean ok = billingService.createWorkOrder(trx, deductStock);
        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Work Order " + trx.getTransactionCode() + " berhasil diproses!\n" +
                    "Stok suku cadang telah disesuaikan dan dicatat dalam Kartu Stok Audit.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan Work Order ke database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
