package com.workshop.ui.procurement;

import com.workshop.dao.PartDao;
import com.workshop.dao.PurchaseOrderDao;
import com.workshop.dao.SupplierDao;
import com.workshop.model.Part;
import com.workshop.model.PurchaseOrder;
import com.workshop.model.PurchaseOrderItem;
import com.workshop.model.Supplier;
import com.workshop.service.AuthService;
import com.workshop.service.ProcurementService;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDialog extends JDialog {
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final SupplierDao supplierDao = new SupplierDao();
    private final PartDao partDao = new PartDao();
    private final ProcurementService procurementService = new ProcurementService();

    private final JTextField txtPoNumber = new JTextField();
    private final JComboBox<Supplier> cmbSupplier = new JComboBox<>();
    private final JTextField txtOrderDate = new JTextField(DateUtil.toIsoDate(new java.util.Date()));
    private final JTextField txtExpectedDate = new JTextField(DateUtil.toIsoDate(new java.util.Date(System.currentTimeMillis() + 86400000L * 3)));
    private final JTextArea txtNotes = new JTextArea(2, 20);

    private final DefaultTableModel itemsModel;
    private final ModernTable itemsTable;
    private final JLabel lblGrandTotal = new JLabel("Total PO: Rp 0");

    private final List<PurchaseOrderItem> orderItems = new ArrayList<>();
    private boolean saved = false;

    public PurchaseOrderDialog(Frame owner) {
        super(owner, "Buat Dokumen Pengadaan (Purchase Order)", true);

        setSize(780, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        txtPoNumber.setText(poDao.generateNextPoNumber());
        txtPoNumber.setEditable(false);

        loadSuppliers();

        // 1. Header Information Panel
        JPanel pnlHeader = new JPanel(new GridBagLayout());
        pnlHeader.setBorder(new EmptyBorder(16, 20, 10, 20));
        pnlHeader.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        pnlHeader.add(new JLabel("No. PO:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        pnlHeader.add(txtPoNumber, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15;
        pnlHeader.add(new JLabel("Supplier / Vendor:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        pnlHeader.add(cmbSupplier, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        pnlHeader.add(new JLabel("Tgl Pesanan (YYYY-MM-DD):*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        pnlHeader.add(txtOrderDate, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.15;
        pnlHeader.add(new JLabel("Estimasi Tiba:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        pnlHeader.add(txtExpectedDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        pnlHeader.add(new JLabel("Catatan Tambahan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 0.85;
        pnlHeader.add(new JScrollPane(txtNotes), gbc);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. Center Table Section (Line Items)
        JPanel pnlCenter = new JPanel(new BorderLayout(8, 8));
        pnlCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        pnlCenter.setOpaque(false);

        JPanel pnlItemActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlItemActions.setOpaque(false);
        JButton btnAddItem = UIStyle.createPrimaryButton("+ Tambah Baris Suku Cadang");
        btnAddItem.addActionListener(e -> onAddItem());

        JButton btnRemoveItem = UIStyle.createSecondaryButton("Hapus Baris");
        btnRemoveItem.addActionListener(e -> onRemoveItem());

        pnlItemActions.add(btnAddItem);
        pnlItemActions.add(btnRemoveItem);
        pnlCenter.add(pnlItemActions, BorderLayout.NORTH);

        String[] itemCols = {"Kode Part", "Nama Suku Cadang", "Merek", "Qty Pesan", "Satuan", "Harga Beli Satuan", "Subtotal"};
        itemsModel = new DefaultTableModel(itemCols, 0);
        itemsTable = new ModernTable(itemsModel);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
        itemsTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());

        pnlCenter.add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // 3. Footer Section (Total + Buttons)
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(10, 20, 16, 20));
        pnlFooter.setOpaque(false);

        lblGrandTotal.setFont(UIStyle.FONT_TITLE);
        lblGrandTotal.setForeground(UIStyle.PRIMARY);
        pnlFooter.add(lblGrandTotal, BorderLayout.WEST);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSaveDraft = UIStyle.createSecondaryButton("Simpan Draft");
        btnSaveDraft.addActionListener(e -> savePo("DRAFT"));

        JButton btnSaveOrder = UIStyle.createPrimaryButton("Kirim Pesanan (ORDERED)");
        btnSaveOrder.addActionListener(e -> savePo("ORDERED"));

        pnlBtns.add(btnCancel);
        pnlBtns.add(btnSaveDraft);
        pnlBtns.add(btnSaveOrder);
        pnlFooter.add(pnlBtns, BorderLayout.EAST);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadSuppliers() {
        for (Supplier s : supplierDao.findAll()) {
            cmbSupplier.addItem(s);
        }
    }

    private void onAddItem() {
        List<Part> allParts = partDao.findAll(null, null, null);
        if (allParts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data suku cadang di katalog.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Part> cmbPart = new JComboBox<>(allParts.toArray(new Part[0]));
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(5, 1, 5000, 1));
        JTextField txtCost = new JTextField();

        cmbPart.addActionListener(e -> {
            Part sel = (Part) cmbPart.getSelectedItem();
            if (sel != null) {
                txtCost.setText(sel.getCostPrice().toPlainString());
            }
        });
        if (cmbPart.getItemCount() > 0) {
            cmbPart.setSelectedIndex(0);
            txtCost.setText(cmbPart.getItemAt(0).getCostPrice().toPlainString());
        }

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Pilih Suku Cadang:"));
        pnl.add(cmbPart);
        pnl.add(new JLabel("Jumlah Dipesan (Qty):"));
        pnl.add(spnQty);
        pnl.add(new JLabel("Harga Beli Satuan (Rp):"));
        pnl.add(txtCost);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Tambah Item Pengadaan", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Part selPart = (Part) cmbPart.getSelectedItem();
            if (selPart == null) return;
            int qty = (int) spnQty.getValue();
            BigDecimal cost = CurrencyUtil.parse(txtCost.getText());
            BigDecimal subtotal = cost.multiply(BigDecimal.valueOf(qty));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPartId(selPart.getId());
            item.setPartNumber(selPart.getPartNumber());
            item.setPartName(selPart.getPartName());
            item.setBrand(selPart.getBrand());
            item.setUnitOfMeasure(selPart.getUnitOfMeasure());
            item.setQuantityOrdered(qty);
            item.setUnitCost(cost);
            item.setSubtotal(subtotal);

            orderItems.add(item);
            itemsModel.addRow(new Object[]{
                    item.getPartNumber(), item.getPartName(), item.getBrand(),
                    item.getQuantityOrdered(), item.getUnitOfMeasure(), item.getUnitCost(), item.getSubtotal()
            });

            recalculateTotal();
        }
    }

    private void onRemoveItem() {
        int row = itemsTable.getSelectedRow();
        if (row >= 0 && row < orderItems.size()) {
            orderItems.remove(row);
            itemsModel.removeRow(row);
            recalculateTotal();
        }
    }

    private void recalculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (PurchaseOrderItem it : orderItems) {
            sum = sum.add(it.getSubtotal());
        }
        lblGrandTotal.setText("Total PO: " + CurrencyUtil.format(sum));
    }

    private void savePo(String status) {
        Supplier sup = (Supplier) cmbSupplier.getSelectedItem();
        if (sup == null) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tambahkan minimal 1 suku cadang ke dalam PO!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(txtPoNumber.getText().trim());
        po.setSupplierId(sup.getId());
        try {
            po.setOrderDate(Date.valueOf(txtOrderDate.getText().trim()));
            po.setExpectedDate(Date.valueOf(txtExpectedDate.getText().trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan YYYY-MM-DD", "Format Tanggal Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        po.setStatus(status);
        po.setPaymentStatus("UNPAID");
        po.setNotes(txtNotes.getText().trim());
        po.setCreatedBy(AuthService.getInstance().getCurrentUser() != null ? AuthService.getInstance().getCurrentUser().getId() : 1);
        po.setItems(orderItems);
        po.recalculateTotal();

        boolean ok = procurementService.createPurchaseOrder(po);
        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Purchase Order berhasil disimpan dengan nomor: " + po.getPoNumber(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan Purchase Order ke database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
