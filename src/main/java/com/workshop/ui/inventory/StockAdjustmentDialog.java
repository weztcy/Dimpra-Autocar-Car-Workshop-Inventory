package com.workshop.ui.inventory;

import com.workshop.model.Part;
import com.workshop.model.User;
import com.workshop.service.AuthService;
import com.workshop.service.InventoryService;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StockAdjustmentDialog extends JDialog {
    private final InventoryService inventoryService = new InventoryService();
    private final Part part;

    private final JComboBox<String> cmbType = new JComboBox<>(new String[]{"DECREASE", "INCREASE"});
    private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JComboBox<String> cmbReason = new JComboBox<>(new String[]{
            "PHYSICAL_COUNT_VARIANCE", "DAMAGED_PART", "EXPIRED_FLUID", "RETURN_FROM_CUSTOMER", "SCRAP_OBSOLETE", "OTHER"
    });
    private final JTextArea txtNotes = new JTextArea(3, 20);
    private boolean adjusted = false;

    public StockAdjustmentDialog(Frame owner, Part part) {
        super(owner, "Penyesuaian Stok (Stock Adjustment): " + part.getPartNumber(), true);
        this.part = part;

        setSize(500, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Top Header Banner
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER_LIGHT),
                new EmptyBorder(16, 20, 16, 20)
        ));
        pnlHeader.setBackground(Color.WHITE);

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Penyesuaian Stok Fisik (Stock Adjustment Audit)");
        lblTitle.setFont(UIStyle.FONT_HEADER);
        lblTitle.setForeground(UIStyle.PRIMARY);

        JLabel lblSub = new JLabel("Audit opname stok, barang rusak, retur, atau selisih fisik gudang");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // Center Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 24, 16, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addLabelValue(form, gbc, row++, "Suku Cadang:", part.getPartNumber() + " - " + part.getPartName());
        addLabelValue(form, gbc, row++, "Stok Tercatat Saat Ini:", part.getCurrentStock() + " " + part.getUnitOfMeasure());

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l1 = new JLabel("Jenis Penyesuaian:*");
        l1.setFont(UIStyle.FONT_BOLD);
        form.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(cmbType, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l2 = new JLabel("Jumlah Penyesuaian (Qty):*");
        l2.setFont(UIStyle.FONT_BOLD);
        form.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(spnQty, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l3 = new JLabel("Alasan / Reason:*");
        l3.setFont(UIStyle.FONT_BOLD);
        form.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(cmbReason, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l4 = new JLabel("Keterangan Tambahan:");
        l4.setFont(UIStyle.FONT_BOLD);
        form.add(l4, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtNotes.setLineWrap(true);
        form.add(new JScrollPane(txtNotes), gbc);

        add(form, BorderLayout.CENTER);

        // Buttons Footer
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBtns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER_LIGHT));
        pnlBtns.setBackground(new Color(248, 250, 252));

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnApply = UIStyle.createPrimaryButton("Terapkan Penyesuaian");
        btnApply.addActionListener(e -> onApply());

        pnlBtns.add(btnCancel);
        pnlBtns.add(btnApply);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void addLabelValue(JPanel pnl, GridBagConstraints gbc, int row, String lbl, String val) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel l = new JLabel(lbl);
        l.setFont(UIStyle.FONT_BOLD);
        pnl.add(l, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JLabel v = new JLabel(val);
        v.setFont(UIStyle.FONT_BOLD);
        v.setForeground(UIStyle.PRIMARY);
        pnl.add(v, gbc);
    }

    private void onApply() {
        String type = (String) cmbType.getSelectedItem();
        int qty = (int) spnQty.getValue();
        String reason = (String) cmbReason.getSelectedItem();
        String notes = txtNotes.getText().trim();

        User cur = AuthService.getInstance().getCurrentUser();
        int userId = cur != null ? cur.getId() : 1;

        boolean success = inventoryService.adjustStock(part.getId(), type, qty, reason, notes, userId);
        if (success) {
            adjusted = true;
            JOptionPane.showMessageDialog(this, "Stok berhasil disesuaikan dan tercatat di Kartu Stok Audit!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal melakukan penyesuaian stok.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAdjusted() {
        return adjusted;
    }
}
