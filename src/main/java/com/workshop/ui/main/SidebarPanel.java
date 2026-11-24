package com.workshop.ui.main;

import com.workshop.config.AppConfig;
import com.workshop.model.User;
import com.workshop.service.AuthService;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
    private final MainFrame mainFrame;
    private final List<NavButton> menuButtons = new ArrayList<>();
    private String activeCard = "DASHBOARD";

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(245, 720));
        setMinimumSize(new Dimension(245, 600));
        setLayout(new BorderLayout());
        setBackground(UIStyle.SIDEBAR_BG);

        initUI();
    }

    private void initUI() {
        // 1. Top Brand Section
        JPanel pnlBrand = new JPanel();
        pnlBrand.setLayout(new BoxLayout(pnlBrand, BoxLayout.Y_AXIS));
        pnlBrand.setOpaque(false);
        pnlBrand.setBorder(new EmptyBorder(20, 18, 16, 18));

        JPanel pnlLogoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLogoRow.setOpaque(false);

        pnlLogoRow.add(UIStyle.createCarLogoBadge(32));

        JPanel pnlBrandTitles = new JPanel();
        pnlBrandTitles.setLayout(new BoxLayout(pnlBrandTitles, BoxLayout.Y_AXIS));
        pnlBrandTitles.setOpaque(false);

        JLabel lblApp = new JLabel("DIMPRA AUTOCAR");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblApp.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Workshop Inventory");
        lblSub.setFont(UIStyle.FONT_SMALL);
        lblSub.setForeground(UIStyle.ACCENT);

        pnlBrandTitles.add(lblApp);
        pnlBrandTitles.add(lblSub);
        pnlLogoRow.add(pnlBrandTitles);

        pnlBrand.add(pnlLogoRow);
        pnlBrand.add(Box.createVerticalStrut(10));

        JLabel lblVersionBadge = UIStyle.createBadge("v1.0.0 Enterprise", new Color(30, 41, 59), new Color(148, 163, 184));
        lblVersionBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBrand.add(lblVersionBadge);

        add(pnlBrand, BorderLayout.NORTH);

        // 2. Navigation Items
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(new EmptyBorder(6, 12, 6, 12));

        addSectionHeader(pnlMenu, "MENU OPERASIONAL");
        addNavButton(pnlMenu, "📊", "Dashboard", "DASHBOARD");
        addNavButton(pnlMenu, "📦", "Inventaris Suku Cadang", "INVENTORY");
        addNavButton(pnlMenu, "🛒", "Pengadaan Supplier (PO)", "PROCUREMENT");
        addNavButton(pnlMenu, "🔧", "Transaksi Servis (PKB)", "SERVICE");
        addNavButton(pnlMenu, "📑", "Laporan & Dokumen", "REPORTS");

        if (AuthService.getInstance().isSupervisor()) {
            pnlMenu.add(Box.createVerticalStrut(12));
            addSectionHeader(pnlMenu, "SISTEM & HAK AKSES");
            addNavButton(pnlMenu, "👥", "Manajemen Pengguna", "USERS");
        }

        add(pnlMenu, BorderLayout.CENTER);

        // 3. User Profile Card at Bottom
        JPanel pnlUser = new JPanel();
        pnlUser.setLayout(new BoxLayout(pnlUser, BoxLayout.Y_AXIS));
        pnlUser.setOpaque(false);
        pnlUser.setBorder(new EmptyBorder(12, 14, 18, 14));

        JPanel userCard = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59)); // Slate 800
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userCard.setOpaque(false);
        userCard.setBorder(new EmptyBorder(8, 10, 8, 10));
        userCard.setPreferredSize(new Dimension(215, 54));
        userCard.setMaximumSize(new Dimension(215, 54));
        userCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        User cur = AuthService.getInstance().getCurrentUser();
        String name = cur != null ? cur.getFullName() : "Administrator";
        String roleStr = cur != null ? cur.getRole().getDisplayName() : "Supervisor";
        String initial = cur != null && !cur.getFullName().isEmpty() ? cur.getFullName().substring(0, 1).toUpperCase() : "A";

        // Circle avatar
        JLabel lblAvatar = new JLabel(initial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIStyle.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblAvatar.setPreferredSize(new Dimension(30, 30));
        lblAvatar.setFont(UIStyle.FONT_BOLD);
        lblAvatar.setForeground(Color.WHITE);
        userCard.add(lblAvatar, BorderLayout.WEST);

        JPanel pnlUserInfo = new JPanel();
        pnlUserInfo.setLayout(new BoxLayout(pnlUserInfo, BoxLayout.Y_AXIS));
        pnlUserInfo.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(UIStyle.FONT_BOLD);
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(roleStr);
        lblRole.setFont(UIStyle.FONT_SMALL);
        lblRole.setForeground(new Color(148, 163, 184));

        pnlUserInfo.add(lblName);
        pnlUserInfo.add(Box.createVerticalStrut(1));
        pnlUserInfo.add(lblRole);
        userCard.add(pnlUserInfo, BorderLayout.CENTER);

        JButton btnLogout = new JButton("🚪  Keluar (Logout)");
        btnLogout.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnLogout.setForeground(new Color(248, 113, 113));
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(215, 34));
        btnLogout.setMaximumSize(new Dimension(215, 34));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> mainFrame.logout());

        pnlUser.add(userCard);
        pnlUser.add(Box.createVerticalStrut(8));
        pnlUser.add(btnLogout);

        add(pnlUser, BorderLayout.SOUTH);

        highlightActiveButton("DASHBOARD");
    }

    private void addSectionHeader(JPanel container, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(new EmptyBorder(6, 8, 4, 8));
        container.add(lbl);
        container.add(Box.createVerticalStrut(2));
    }

    private void addNavButton(JPanel container, String iconEmoji, String title, String cardName) {
        NavButton btn = new NavButton(iconEmoji, title, cardName);
        btn.addActionListener(e -> {
            mainFrame.showCard(cardName);
            highlightActiveButton(cardName);
        });

        menuButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(3));
    }

    public void highlightActiveButton(String cardName) {
        this.activeCard = cardName;
        for (NavButton b : menuButtons) {
            b.setActive(cardName.equals(b.cardName));
        }
        repaint();
    }

    private class NavButton extends JButton {
        final String cardName;
        final JLabel lblIcon;
        final JLabel lblText;
        private boolean isActive = false;

        NavButton(String iconEmoji, String title, String cardName) {
            this.cardName = cardName;
            setLayout(new BorderLayout(10, 0));
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(7, 10, 7, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setPreferredSize(new Dimension(220, 38));

            lblIcon = new JLabel(iconEmoji, SwingConstants.CENTER);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            lblIcon.setForeground(Color.WHITE);
            lblIcon.setPreferredSize(new Dimension(24, 24));

            lblText = new JLabel(title);
            lblText.setFont(UIStyle.FONT_REGULAR);
            lblText.setForeground(new Color(203, 213, 225));

            add(lblIcon, BorderLayout.WEST);
            add(lblText, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            lblIcon.setForeground(Color.WHITE);
            if (active) {
                lblText.setForeground(Color.WHITE);
                lblText.setFont(UIStyle.FONT_BOLD);
            } else {
                lblText.setForeground(new Color(203, 213, 225));
                lblText.setFont(UIStyle.FONT_REGULAR);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(UIStyle.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            } else if (getModel().isRollover()) {
                g2.setColor(UIStyle.SIDEBAR_HOVER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
