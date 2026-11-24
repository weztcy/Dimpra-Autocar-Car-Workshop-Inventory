package com.workshop.ui.procurement;

import com.workshop.dao.SupplierDao;
import com.workshop.model.Supplier;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SupplierDialog extends JDialog {
    private final SupplierDao supplierDao = new SupplierDao();

    private final JTextField txtCode = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextField txtContact = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtCity = new JTextField();
    private final JComboBox<String> cmbTerms = new JComboBox<>(new String[]{"Net 30", "Net 14", "Net 45", "Net 60", "COD", "Cash Advance"});
    private final JTextArea txtAddress = new JTextArea(3, 20);

    private Supplier editingSupplier;
    private boolean saved = false;

    public SupplierDialog(Frame owner, Supplier sup) {
        super(owner, sup == null ? "Tambah Supplier Baru" : "Edit Supplier: " + sup.getName(), true);
        this.editingSupplier = sup;

        setSize(540, 540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initUI();
        if (editingSupplier != null) {
            populateData();
        }
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

        JLabel lblTitle = new JLabel(editingSupplier == null ? "Tambah Mitra Supplier Baru" : "Edit Supplier: " + editingSupplier.getName());
        lblTitle.setFont(UIStyle.FONT_HEADER);
        lblTitle.setForeground(UIStyle.PRIMARY);

        JLabel lblSub = new JLabel("Pendaftaran distributor resmi, kontak PIC, dan syarat pembayaran");
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
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(form, gbc, row++, "Kode Supplier (ID):*", txtCode);
        addFormField(form, gbc, row++, "Nama Perusahaan / Supplier:*", txtName);
        addFormField(form, gbc, row++, "Kontak Person (PIC):", txtContact);
        addFormField(form, gbc, row++, "Nomor Telepon:*", txtPhone);
        addFormField(form, gbc, row++, "Email:", txtEmail);
        addFormField(form, gbc, row++, "Kota:", txtCity);
        addFormField(form, gbc, row++, "Syarat Pembayaran (Terms):", cmbTerms);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblAddr = new JLabel("Alamat Lengkap:");
        lblAddr.setFont(UIStyle.FONT_BOLD);
        form.add(lblAddr, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        txtAddress.setLineWrap(true);
        form.add(new JScrollPane(txtAddress), gbc);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons Footer
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBtns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER_LIGHT));
        pnlBtns.setBackground(new Color(248, 250, 252));

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = UIStyle.createPrimaryButton("Simpan Supplier");
        btnSave.addActionListener(e -> onSave());

        pnlBtns.add(btnCancel);
        pnlBtns.add(btnSave);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel pnl, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BOLD);
        pnl.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        pnl.add(comp, gbc);
    }

    private void populateData() {
        txtCode.setText(editingSupplier.getSupplierCode());
        txtCode.setEnabled(false);
        txtName.setText(editingSupplier.getName());
        txtContact.setText(editingSupplier.getContactPerson());
        txtPhone.setText(editingSupplier.getPhone());
        txtEmail.setText(editingSupplier.getEmail());
        txtCity.setText(editingSupplier.getCity());
        cmbTerms.setSelectedItem(editingSupplier.getPaymentTerms());
        txtAddress.setText(editingSupplier.getAddress());
    }

    private void onSave() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (code.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi kode, nama, dan telepon!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingSupplier == null) {
            Supplier s = new Supplier();
            s.setSupplierCode(code);
            s.setName(name);
            s.setContactPerson(txtContact.getText().trim());
            s.setPhone(phone);
            s.setEmail(txtEmail.getText().trim());
            s.setCity(txtCity.getText().trim());
            s.setPaymentTerms((String) cmbTerms.getSelectedItem());
            s.setAddress(txtAddress.getText().trim());
            s.setStatus("ACTIVE");

            boolean ok = supplierDao.create(s);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan supplier!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            editingSupplier.setName(name);
            editingSupplier.setContactPerson(txtContact.getText().trim());
            editingSupplier.setPhone(phone);
            editingSupplier.setEmail(txtEmail.getText().trim());
            editingSupplier.setCity(txtCity.getText().trim());
            editingSupplier.setPaymentTerms((String) cmbTerms.getSelectedItem());
            editingSupplier.setAddress(txtAddress.getText().trim());

            boolean ok = supplierDao.update(editingSupplier);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui supplier!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
