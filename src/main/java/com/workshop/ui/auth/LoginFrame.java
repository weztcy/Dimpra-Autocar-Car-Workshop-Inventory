package com.workshop.ui.auth;

import com.workshop.config.AppConfig;
import com.workshop.service.AuthService;
import com.workshop.ui.main.MainFrame;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private final JTextField txtUsername = new JTextField("admin");
    private final JPasswordField txtPassword = new JPasswordField("admin123");
    private final JLabel lblStatus = new JLabel(" ");

    public LoginFrame() {
        setTitle(AppConfig.APP_NAME + " - Autentikasi Pengguna");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(15, 23, 42)); // Slate 900 Canvas

        // Center White Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(226, 232, 240));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 32, 24, 32));
        card.setPreferredSize(new Dimension(400, 510));
        card.setMaximumSize(new Dimension(400, 510));

        // 1. Branding Section (Centered)
        JComponent logoBadge = UIStyle.createCarLogoBadge(52);
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoBadge);
        card.add(Box.createVerticalStrut(10));

        JLabel lblTitle = new JLabel("DIMPRA AUTOCAR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42)); // Slate 900
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(2));

        JLabel lblSub = new JLabel("Workshop Inventory & Management");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(new Color(100, 116, 139)); // Slate 500
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(6));

        JLabel lblBadge = UIStyle.createBadge("Enterprise Edition • v1.0.0", new Color(238, 242, 255), UIStyle.PRIMARY);
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblBadge);
        card.add(Box.createVerticalStrut(16));

        // 2. Form Inputs Section (Encapsulated in centered panel)
        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);
        pnlForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlForm.setMaximumSize(new Dimension(336, 215));
        pnlForm.setPreferredSize(new Dimension(336, 215));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIStyle.FONT_BOLD);
        lblUser.setForeground(new Color(51, 65, 85));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblUser);
        pnlForm.add(Box.createVerticalStrut(4));

        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtUsername.setPreferredSize(new Dimension(336, 36));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(txtUsername);
        pnlForm.add(Box.createVerticalStrut(10));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIStyle.FONT_BOLD);
        lblPass.setForeground(new Color(51, 65, 85));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblPass);
        pnlForm.add(Box.createVerticalStrut(4));

        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtPassword.setPreferredSize(new Dimension(336, 36));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(txtPassword);
        pnlForm.add(Box.createVerticalStrut(4));

        lblStatus.setFont(UIStyle.FONT_SMALL);
        lblStatus.setForeground(UIStyle.DANGER);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblStatus);
        pnlForm.add(Box.createVerticalStrut(6));

        JButton btnLogin = UIStyle.createPrimaryButton("Masuk ke Sistem (Login)");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogin.setPreferredSize(new Dimension(336, 38));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> performLogin());
        pnlForm.add(btnLogin);

        card.add(pnlForm);
        card.add(Box.createVerticalStrut(14));

        // 3. Quick Role Access Section (Clean Pills)
        JPanel pnlDivider = new JPanel(new BorderLayout());
        pnlDivider.setOpaque(false);
        pnlDivider.setMaximumSize(new Dimension(336, 16));
        pnlDivider.setPreferredSize(new Dimension(336, 16));
        pnlDivider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblOr = new JLabel("AKSES CEPAT DEMO", SwingConstants.CENTER);
        lblOr.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblOr.setForeground(new Color(148, 163, 184));
        pnlDivider.add(lblOr, BorderLayout.CENTER);
        card.add(pnlDivider);
        card.add(Box.createVerticalStrut(8));

        JPanel pnlPills = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlPills.setOpaque(false);
        pnlPills.setMaximumSize(new Dimension(336, 32));
        pnlPills.setPreferredSize(new Dimension(336, 32));
        pnlPills.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAdmin = createRolePill("Admin", "admin", "admin123");
        JButton btnClerk = createRolePill("Gudang", "clerk_budi", "clerk123");
        JButton btnTech = createRolePill("Teknisi", "tech_agus", "tech123");

        pnlPills.add(btnAdmin);
        pnlPills.add(btnClerk);
        pnlPills.add(btnTech);
        card.add(pnlPills);
        card.add(Box.createVerticalStrut(8));

        JLabel lblHint = new JLabel("Klik salah satu tombol peran untuk login instan");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHint.setForeground(new Color(148, 163, 184));
        lblHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblHint);

        root.add(card);
        add(root);
    }

    private JButton createRolePill(String title, String user, String pass) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(30, 41, 59));
        btn.setBackground(new Color(248, 250, 252));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 4, 5, 4)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(241, 245, 249));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyle.PRIMARY, 1, true),
                        new EmptyBorder(5, 4, 5, 4)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                        new EmptyBorder(5, 4, 5, 4)
                ));
            }
        });
        btn.addActionListener(e -> setCredentials(user, pass));
        return btn;
    }

    private void setCredentials(String user, String pass) {
        txtUsername.setText(user);
        txtPassword.setText(pass);
        lblStatus.setText(" ");
        performLogin();
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Masukkan username dan password!");
            return;
        }

        boolean ok = AuthService.getInstance().login(username, password);
        if (ok) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } else {
            lblStatus.setText("Username atau password salah / akun non-aktif!");
        }
    }
}
