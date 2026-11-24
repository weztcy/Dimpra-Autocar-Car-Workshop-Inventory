package com.workshop.ui.users;

import com.workshop.dao.UserDao;
import com.workshop.model.Role;
import com.workshop.model.User;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.SecurityUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDialog extends JDialog {
    private final UserDao userDao = new UserDao();

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JTextField txtFullName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JComboBox<Role> cmbRole = new JComboBox<>(Role.values());
    private final JCheckBox chkActive = new JCheckBox("Akun Pengguna Aktif", true);

    private User editingUser;
    private boolean saved = false;

    public UserDialog(Frame owner, User user) {
        super(owner, user == null ? "Tambah Pengguna Sistem Baru" : "Edit Pengguna: " + user.getUsername(), true);
        this.editingUser = user;

        setSize(500, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initUI();
        if (editingUser != null) {
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

        JLabel lblTitle = new JLabel(editingUser == null ? "Tambah Pengguna Sistem (RBAC)" : "Edit Pengguna: " + editingUser.getUsername());
        lblTitle.setFont(UIStyle.FONT_HEADER);
        lblTitle.setForeground(UIStyle.PRIMARY);

        JLabel lblSub = new JLabel("Konfigurasi kredensial login, peran otorisasi, dan status aktif akun");
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
        addFormField(form, gbc, row++, "Username Login:*", txtUsername);
        addFormField(form, gbc, row++, "Password:*", txtPassword);
        addFormField(form, gbc, row++, "Nama Lengkap:*", txtFullName);
        addFormField(form, gbc, row++, "Email:", txtEmail);
        addFormField(form, gbc, row++, "Nomor Telepon:", txtPhone);
        addFormField(form, gbc, row++, "Hak Akses / Peran (Role):*", cmbRole);
        addFormField(form, gbc, row++, "Status:", chkActive);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons Footer
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBtns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER_LIGHT));
        pnlBtns.setBackground(new Color(248, 250, 252));

        JButton btnCancel = UIStyle.createSecondaryButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = UIStyle.createPrimaryButton("Simpan Pengguna");
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
        txtUsername.setText(editingUser.getUsername());
        txtUsername.setEnabled(false);
        txtFullName.setText(editingUser.getFullName());
        txtEmail.setText(editingUser.getEmail());
        txtPhone.setText(editingUser.getPhone());
        cmbRole.setSelectedItem(editingUser.getRole());
        chkActive.setSelected(editingUser.isActive());
        txtPassword.setToolTipText("Biarkan kosong jika tidak ingin mengubah password.");
    }

    private void onSave() {
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();

        if (username.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan nama lengkap harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingUser == null) {
            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password harus diisi untuk pengguna baru!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(SecurityUtil.hashPassword(pass));
            u.setFullName(fullName);
            u.setEmail(txtEmail.getText().trim());
            u.setPhone(txtPhone.getText().trim());
            u.setRole((Role) cmbRole.getSelectedItem());
            u.setActive(chkActive.isSelected());

            boolean ok = userDao.create(u);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal membuat pengguna! Username mungkin sudah terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            editingUser.setFullName(fullName);
            editingUser.setEmail(txtEmail.getText().trim());
            editingUser.setPhone(txtPhone.getText().trim());
            editingUser.setRole((Role) cmbRole.getSelectedItem());
            editingUser.setActive(chkActive.isSelected());

            if (!pass.isEmpty()) {
                editingUser.setPasswordHash(SecurityUtil.hashPassword(pass));
            }

            boolean ok = userDao.update(editingUser);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui pengguna!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
