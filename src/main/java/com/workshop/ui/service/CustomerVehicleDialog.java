package com.workshop.ui.service;

import com.workshop.dao.CustomerDao;
import com.workshop.dao.VehicleDao;
import com.workshop.model.Customer;
import com.workshop.model.Vehicle;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CustomerVehicleDialog extends JDialog {
    private final CustomerDao customerDao = new CustomerDao();
    private final VehicleDao vehicleDao = new VehicleDao();

    // Customer fields
    private final JComboBox<Customer> cmbExistingCustomer = new JComboBox<>();
    private final JCheckBox chkNewCustomer = new JCheckBox("Daftarkan Pelanggan Baru");
    private final JTextField txtCustCode = new JTextField();
    private final JTextField txtCustName = new JTextField();
    private final JTextField txtCustPhone = new JTextField();
    private final JTextField txtCustEmail = new JTextField();
    private final JComboBox<String> cmbCustType = new JComboBox<>(new String[]{"INDIVIDUAL", "CORPORATE"});
    private final JTextField txtCustAddress = new JTextField();

    // Vehicle fields
    private final JTextField txtPlateNumber = new JTextField();
    private final JTextField txtVin = new JTextField();
    private final JTextField txtMake = new JTextField("Toyota");
    private final JTextField txtModel = new JTextField("Avanza 1.3 G");
    private final JSpinner spnYear = new JSpinner(new SpinnerNumberModel(2020, 1990, 2026, 1));
    private final JTextField txtColor = new JTextField("Hitam");
    private final JComboBox<String> cmbTransmission = new JComboBox<>(new String[]{"AUTOMATIC", "MANUAL", "CVT"});
    private final JSpinner spnOdometer = new JSpinner(new SpinnerNumberModel(45000, 0, 999999, 1000));

    private boolean saved = false;

    public CustomerVehicleDialog(Frame owner) {
        super(owner, "Pendaftaran Pelanggan & Kendaraan", true);

        setSize(600, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        loadCustomers();
        initUI();
    }

    private void loadCustomers() {
        List<Customer> list = customerDao.findAll(null);
        for (Customer c : list) cmbExistingCustomer.addItem(c);
    }

    private void initUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Section 1: Customer
        JLabel lblSec1 = new JLabel("1. DATA PELANGGAN (CUSTOMER)");
        lblSec1.setFont(UIStyle.FONT_HEADER);
        lblSec1.setForeground(UIStyle.PRIMARY);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(lblSec1, gbc);
        gbc.gridwidth = 1;

        addFormField(form, gbc, row++, "Pilih Pelanggan Terdaftar:", cmbExistingCustomer);
        addFormField(form, gbc, row++, "", chkNewCustomer);

        addFormField(form, gbc, row++, "Kode Pelanggan Baru:", txtCustCode);
        addFormField(form, gbc, row++, "Nama Lengkap:*", txtCustName);
        addFormField(form, gbc, row++, "Nomor Telepon:*", txtCustPhone);
        addFormField(form, gbc, row++, "Email:", txtCustEmail);
        addFormField(form, gbc, row++, "Tipe Pelanggan:", cmbCustType);
        addFormField(form, gbc, row++, "Alamat:", txtCustAddress);

        toggleCustomerFields(false);
        chkNewCustomer.addActionListener(e -> toggleCustomerFields(chkNewCustomer.isSelected()));

        // Section 2: Vehicle
        JLabel lblSec2 = new JLabel("2. DATA KENDARAAN (VEHICLE)");
        lblSec2.setFont(UIStyle.FONT_HEADER);
        lblSec2.setForeground(UIStyle.PRIMARY);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = row++;
        form.add(lblSec2, gbc);
        gbc.gridwidth = 1;

        addFormField(form, gbc, row++, "Nomor Polisi (Plat):*", txtPlateNumber);
        addFormField(form, gbc, row++, "Nomor Rangka (VIN):", txtVin);
        addFormField(form, gbc, row++, "Merek Mobil:*", txtMake);
        addFormField(form, gbc, row++, "Model / Tipe Mobil:*", txtModel);
        addFormField(form, gbc, row++, "Tahun Pembuatan:*", spnYear);
        addFormField(form, gbc, row++, "Warna Kendaraan:", txtColor);
        addFormField(form, gbc, row++, "Transmisi:", cmbTransmission);
        addFormField(form, gbc, row++, "Odometer Saat Ini (KM):", spnOdometer);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = UIStyle.createPrimaryButton("Simpan Data Kendaraan");
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

    private void toggleCustomerFields(boolean enabled) {
        cmbExistingCustomer.setEnabled(!enabled);
        txtCustCode.setEnabled(enabled);
        txtCustName.setEnabled(enabled);
        txtCustPhone.setEnabled(enabled);
        txtCustEmail.setEnabled(enabled);
        cmbCustType.setEnabled(enabled);
        txtCustAddress.setEnabled(enabled);
    }

    private void onSave() {
        int customerId;
        if (chkNewCustomer.isSelected()) {
            String name = txtCustName.getText().trim();
            String phone = txtCustPhone.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap lengkapi Nama dan No Telepon Pelanggan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer c = new Customer();
            c.setCustomerCode(txtCustCode.getText().trim().isEmpty() ? "CUST-" + System.currentTimeMillis() % 10000 : txtCustCode.getText().trim());
            c.setName(name);
            c.setPhone(phone);
            c.setEmail(txtCustEmail.getText().trim());
            c.setCustomerType((String) cmbCustType.getSelectedItem());
            c.setAddress(txtCustAddress.getText().trim());

            if (!customerDao.create(c)) {
                JOptionPane.showMessageDialog(this, "Gagal membuat data pelanggan baru.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            customerId = c.getId();
        } else {
            Customer sel = (Customer) cmbExistingCustomer.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Pilih pelanggan dari daftar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            customerId = sel.getId();
        }

        String plate = txtPlateNumber.getText().trim();
        String make = txtMake.getText().trim();
        String model = txtModel.getText().trim();

        if (plate.isEmpty() || make.isEmpty() || model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi No. Polisi, Merek, dan Model kendaraan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle v = new Vehicle();
        v.setCustomerId(customerId);
        v.setPlateNumber(plate);
        v.setVin(txtVin.getText().trim().isEmpty() ? "VIN-" + System.currentTimeMillis() % 100000 : txtVin.getText().trim());
        v.setMake(make);
        v.setModel(model);
        v.setYear((int) spnYear.getValue());
        v.setColor(txtColor.getText().trim());
        v.setTransmission((String) cmbTransmission.getSelectedItem());
        v.setOdometer((int) spnOdometer.getValue());

        if (vehicleDao.create(v)) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Data kendaraan " + plate + " berhasil didaftarkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mendaftarkan kendaraan. Pastikan No. Polisi belum terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
