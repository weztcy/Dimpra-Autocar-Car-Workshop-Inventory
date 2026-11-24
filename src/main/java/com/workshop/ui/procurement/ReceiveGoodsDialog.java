package com.workshop.ui.procurement;

import com.workshop.model.PurchaseOrder;
import com.workshop.model.PurchaseOrderItem;
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
import java.sql.Date;

public class ReceiveGoodsDialog extends JDialog {
    private final ProcurementService procurementService = new ProcurementService();
    private final PurchaseOrder po;

    private final JTextField txtReceivedDate = new JTextField(DateUtil.toIsoDate(new java.util.Date()));
    private final JComboBox<String> cmbPayment = new JComboBox<>(new String[]{"PAID", "PARTIALLY_PAID", "UNPAID"});
    private boolean received = false;

    public ReceiveGoodsDialog(Frame owner, PurchaseOrder po) {
        super(owner, "Penerimaan Barang Fisik (Goods Receipt): " + po.getPoNumber(), true);
        this.po = po;

        setSize(750, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel pnlHeader = new JPanel(new GridLayout(2, 4, 8, 8));
        pnlHeader.setBorder(new EmptyBorder(16, 20, 10, 20));
        pnlHeader.setOpaque(false);

        pnlHeader.add(new JLabel("No. PO:"));
        JLabel lblPo = new JLabel(po.getPoNumber());
        lblPo.setFont(UIStyle.FONT_BOLD);
        pnlHeader.add(lblPo);

        pnlHeader.add(new JLabel("Supplier / Vendor:"));
        pnlHeader.add(new JLabel(po.getSupplierName()));

        pnlHeader.add(new JLabel("Tgl Penerimaan (YYYY-MM-DD):*"));
        pnlHeader.add(txtReceivedDate);

        pnlHeader.add(new JLabel("Status Pembayaran Tagihan:*"));
        pnlHeader.add(cmbPayment);

        add(pnlHeader, BorderLayout.NORTH);

        // Center Table (Items being received)
        JPanel pnlCenter = new JPanel(new BorderLayout(6, 6));
        pnlCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        pnlCenter.setOpaque(false);

        JLabel lblItemHeader = new JLabel("Verifikasi Suku Cadang yang Diterima:");
        lblItemHeader.setFont(UIStyle.FONT_BOLD);
        pnlCenter.add(lblItemHeader, BorderLayout.NORTH);

        String[] cols = {"Kode Part", "Nama Suku Cadang", "Merek", "Qty Dipesan", "Qty Diterima (Masuk)", "Satuan", "Harga Beli", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        ModernTable table = new ModernTable(model);
        table.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new CurrencyCellRenderer());

        for (PurchaseOrderItem it : po.getItems()) {
            model.addRow(new Object[]{
                    it.getPartNumber(),
                    it.getPartName(),
                    it.getBrand(),
                    it.getQuantityOrdered(),
                    it.getQuantityOrdered(), // default full
                    it.getUnitOfMeasure(),
                    it.getUnitCost(),
                    it.getSubtotal()
            });
        }
        pnlCenter.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Footer Panel
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(10, 20, 16, 20));
        pnlFooter.setOpaque(false);

        JLabel lblTotal = new JLabel("Total Nilai Tagihan: " + CurrencyUtil.format(po.getTotalAmount()));
        lblTotal.setFont(UIStyle.FONT_HEADER);
        pnlFooter.add(lblTotal, BorderLayout.WEST);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirm = UIStyle.createSuccessButton("Konfirmasi Terima Barang & Update Stok");
        btnConfirm.addActionListener(e -> onConfirmReceive());

        pnlBtns.add(btnCancel);
        pnlBtns.add(btnConfirm);
        pnlFooter.add(pnlBtns, BorderLayout.EAST);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void onConfirmReceive() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin memverifikasi penerimaan barang ini?\n" +
                "Stok fisik suku cadang akan bertambah secara otomatis dan dicatat dalam Kartu Stok Audit.",
                "Konfirmasi Penerimaan Barang", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        Date recDate;
        try {
            recDate = Date.valueOf(txtReceivedDate.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan YYYY-MM-DD", "Format Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String payStatus = (String) cmbPayment.getSelectedItem();
        int userId = AuthService.getInstance().getCurrentUser() != null ? AuthService.getInstance().getCurrentUser().getId() : 1;

        boolean ok = procurementService.receiveGoods(po.getId(), recDate, payStatus, userId);
        if (ok) {
            received = true;
            JOptionPane.showMessageDialog(this, "Barang berhasil diterima! Stok inventaris telah diperbarui secara otomatis.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses penerimaan barang.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isReceived() {
        return received;
    }
}
