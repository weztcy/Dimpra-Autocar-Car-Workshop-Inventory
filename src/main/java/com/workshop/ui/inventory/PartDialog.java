package com.workshop.ui.inventory;

import com.workshop.dao.CategoryDao;
import com.workshop.dao.PartDao;
import com.workshop.model.Category;
import com.workshop.model.Part;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PartDialog extends JDialog {
    private final PartDao partDao = new PartDao();
    private final CategoryDao categoryDao = new CategoryDao();

    private final JTextField txtPartNumber = new JTextField();
    private final JTextField txtPartName = new JTextField();
    private final JComboBox<Category> cmbCategory = new JComboBox<>();
    private final JTextField txtBrand = new JTextField();
    private final JTextField txtCompatibility = new JTextField();
    private final JComboBox<String> cmbUom = new JComboBox<>(new String[]{"PCS", "SET", "PAIR", "KIT", "GAL", "BTL", "UNIT", "TNK", "BAG"});
    private final JTextField txtStorageBin = new JTextField("A-01-01");
    private final JTextField txtCostPrice = new JTextField("0");
    private final JTextField txtSellingPrice = new JTextField("0");
    private final JSpinner spnMinStock = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));
    private final JSpinner spnCurrentStock = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));

    private Part editingPart;
    private boolean saved = false;

    public PartDialog(Frame owner, Part partToEdit) {
        super(owner, partToEdit == null ? "Tambah Suku Cadang Baru" : "Edit Suku Cadang", true);
        this.editingPart = partToEdit;

        setSize(580, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        loadCategories();
        initUI();

        if (editingPart != null) {
            populateData();
        }
    }

    private void loadCategories() {
        List<Category> cats = categoryDao.findAll();
        for (Category c : cats) cmbCategory.addItem(c);
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

        JLabel lblTitle = new JLabel(editingPart == null ? "Tambah Suku Cadang Baru" : "Edit Suku Cadang: " + editingPart.getPartNumber());
        lblTitle.setFont(UIStyle.FONT_HEADER);
        lblTitle.setForeground(UIStyle.PRIMARY);

        JLabel lblSub = new JLabel("Spesifikasi teknis, harga beli/jual, batas minimum stok, dan lokasi bin gudang");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // Center Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(18, 24, 18, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(form, gbc, row++, "Nomor / Kode Part (SKU):*", txtPartNumber);
        addFormField(form, gbc, row++, "Nama Suku Cadang:*", txtPartName);
        addFormField(form, gbc, row++, "Kategori:*", cmbCategory);
        addFormField(form, gbc, row++, "Merek / Produsen:*", txtBrand);
        addFormField(form, gbc, row++, "Kompatibilitas Kendaraan:", txtCompatibility);
        addFormField(form, gbc, row++, "Satuan Ukuran (UoM):", cmbUom);
        addFormField(form, gbc, row++, "Lokasi Bin / Rak Gudang:", txtStorageBin);
        addFormField(form, gbc, row++, "Harga Beli (Cost Price Rp):*", txtCostPrice);
        addFormField(form, gbc, row++, "Harga Jual (Retail Price Rp):*", txtSellingPrice);
        addFormField(form, gbc, row++, "Batas Minimum Stok (Safety):*", spnMinStock);
        addFormField(form, gbc, row++, "Stok Fisik Saat Ini:*", spnCurrentStock);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons Footer
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBtns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER_LIGHT));
        pnlBtns.setBackground(new Color(248, 250, 252));

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = UIStyle.createPrimaryButton("Simpan Suku Cadang");
        btnSave.addActionListener(e -> onSave());

        pnlBtns.add(btnCancel);
        pnlBtns.add(btnSave);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel pnl, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BOLD);
        pnl.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        pnl.add(comp, gbc);
    }

    private void populateData() {
        txtPartNumber.setText(editingPart.getPartNumber());
        txtPartNumber.setEnabled(false);
        txtPartName.setText(editingPart.getPartName());
        txtBrand.setText(editingPart.getBrand());
        txtCompatibility.setText(editingPart.getCompatibility());
        cmbUom.setSelectedItem(editingPart.getUnitOfMeasure());
        txtStorageBin.setText(editingPart.getStorageBin());
        txtCostPrice.setText(editingPart.getCostPrice().toPlainString());
        txtSellingPrice.setText(editingPart.getSellingPrice().toPlainString());
        spnMinStock.setValue(editingPart.getMinStock());
        spnCurrentStock.setValue(editingPart.getCurrentStock());

        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            Category c = cmbCategory.getItemAt(i);
            if (c.getId() == editingPart.getCategoryId()) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onSave() {
        String num = txtPartNumber.getText().trim();
        String name = txtPartName.getText().trim();
        Category cat = (Category) cmbCategory.getSelectedItem();
        String brand = txtBrand.getText().trim();

        if (num.isEmpty() || name.isEmpty() || cat == null || brand.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua field yang wajib (*)", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal cost, sell;
        try {
            cost = new BigDecimal(txtCostPrice.getText().trim());
            sell = new BigDecimal(txtSellingPrice.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format harga harus angka!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingPart == null) {
            Part newPart = new Part();
            newPart.setPartNumber(num);
            newPart.setPartName(name);
            newPart.setCategoryId(cat.getId());
            newPart.setBrand(brand);
            newPart.setCompatibility(txtCompatibility.getText().trim());
            newPart.setUnitOfMeasure(cmbUom.getSelectedItem().toString());
            newPart.setStorageBin(txtStorageBin.getText().trim());
            newPart.setCostPrice(cost);
            newPart.setSellingPrice(sell);
            newPart.setMinStock((Integer) spnMinStock.getValue());
            newPart.setCurrentStock((Integer) spnCurrentStock.getValue());

            boolean ok = partDao.create(newPart);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan suku cadang!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            editingPart.setPartName(name);
            editingPart.setCategoryId(cat.getId());
            editingPart.setBrand(brand);
            editingPart.setCompatibility(txtCompatibility.getText().trim());
            editingPart.setUnitOfMeasure(cmbUom.getSelectedItem().toString());
            editingPart.setStorageBin(txtStorageBin.getText().trim());
            editingPart.setCostPrice(cost);
            editingPart.setSellingPrice(sell);
            editingPart.setMinStock((Integer) spnMinStock.getValue());
            editingPart.setCurrentStock((Integer) spnCurrentStock.getValue());

            boolean ok = partDao.update(editingPart);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui suku cadang!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
