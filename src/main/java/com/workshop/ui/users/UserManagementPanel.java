package com.workshop.ui.users;

import com.workshop.dao.UserDao;
import com.workshop.model.User;
import com.workshop.ui.common.BadgeRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserDao userDao = new UserDao();
    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private List<User> userList = new ArrayList<>();

    public UserManagementPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout(16, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Manajemen Pengguna & Hak Akses (RBAC)");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_MAIN);

        JLabel lblSub = new JLabel("Kelola akun pengguna dan peran otorisasi sistem (Supervisor, Clerk, Technician)");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlActions.setOpaque(false);

        JButton btnAdd = UIStyle.createPrimaryButton("+ Tambah Pengguna");
        btnAdd.addActionListener(e -> onAddUser());

        JButton btnEdit = UIStyle.createSecondaryButton("Edit Pengguna");
        btnEdit.addActionListener(e -> onEditUser());

        JButton btnRefresh = UIStyle.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());

        pnlActions.add(btnAdd);
        pnlActions.add(btnEdit);
        pnlActions.add(btnRefresh);

        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Username", "Nama Lengkap", "Email", "Nomor Telepon", "Peran / Hak Akses (Role)", "Status Akun", "Tgl Registrasi"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new ModernTable(tableModel);

        table.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new BadgeRenderer());

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            userList = userDao.findAll();
            tableModel.setRowCount(0);
            for (User u : userList) {
                tableModel.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole().getDisplayName(),
                        u.isActive() ? "ACTIVE" : "INACTIVE",
                        DateUtil.formatDateTime(u.getCreatedAt())
                });
            }
        });
    }

    private User getSelectedUser() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        int m = table.convertRowIndexToModel(r);
        if (m >= 0 && m < userList.size()) return userList.get(m);
        return null;
    }

    private void onAddUser() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        UserDialog dlg = new UserDialog(owner, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onEditUser() {
        User u = getSelectedUser();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna dari tabel yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        UserDialog dlg = new UserDialog(owner, u);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }
}
