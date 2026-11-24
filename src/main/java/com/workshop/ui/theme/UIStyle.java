package com.workshop.ui.theme;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UIStyle {
    // --- Modern Premium Color Palette ---
    // Primary & Accents
    public static final Color PRIMARY = new Color(79, 70, 229);         // Indigo 600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);   // Indigo 700
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255); // Indigo 50
    public static final Color ACCENT = new Color(14, 165, 233);         // Sky 500
    public static final Color ACCENT_HOVER = new Color(2, 132, 199);    // Sky 600

    // Semantic Status Colors
    public static final Color SUCCESS = new Color(16, 185, 129);        // Emerald 500
    public static final Color SUCCESS_BG = new Color(236, 253, 245);    // Emerald 50
    public static final Color SUCCESS_TEXT = new Color(6, 95, 70);      // Emerald 800

    public static final Color WARNING = new Color(245, 158, 11);        // Amber 500
    public static final Color WARNING_BG = new Color(254, 243, 199);    // Amber 100
    public static final Color WARNING_TEXT = new Color(146, 64, 14);    // Amber 800

    public static final Color DANGER = new Color(239, 68, 68);          // Rose 500
    public static final Color DANGER_BG = new Color(254, 226, 226);     // Rose 100
    public static final Color DANGER_TEXT = new Color(153, 27, 27);     // Rose 800

    // Neutral Shades (Light defaults)
    public static final Color BG_LIGHT = new Color(248, 250, 252);      // Slate 50
    public static final Color CARD_BG = new Color(255, 255, 255);       // White
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);       // Slate 900
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);    // Slate 800
    public static final Color SIDEBAR_ACTIVE = new Color(30, 58, 138);   // Blue 900

    public static final Color BORDER_LIGHT = new Color(226, 232, 240);  // Slate 200
    public static final Color TEXT_MAIN = new Color(15, 23, 42);        // Slate 900
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);  // Slate 600
    public static final Color TEXT_MUTED = new Color(148, 163, 184);    // Slate 400

    // Adaptive Theme Helpers
    public static Color getCardBg() {
        return FlatLaf.isLafDark() ? new Color(30, 41, 59) : Color.WHITE;
    }

    public static Color getCardBorder() {
        return FlatLaf.isLafDark() ? new Color(51, 65, 85) : new Color(226, 232, 240);
    }

    public static Color getTextMain() {
        return FlatLaf.isLafDark() ? new Color(248, 250, 252) : new Color(15, 23, 42);
    }

    public static Color getTextSecondary() {
        return FlatLaf.isLafDark() ? new Color(148, 163, 184) : new Color(71, 85, 105);
    }

    public static Color getTextMuted() {
        return FlatLaf.isLafDark() ? new Color(100, 116, 139) : new Color(148, 163, 184);
    }

    // --- Typography (Clean Segoe UI / System Font) ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_STAT_NUMBER = new Font("Segoe UI", Font.BOLD, 22);

    // --- Vector White Car Logo Badge Component ---
    public static JComponent createCarLogoBadge(int size) {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(size, size));
                setMinimumSize(new Dimension(size, size));
                setMaximumSize(new Dimension(size, size));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Indigo squircle background
                g2.setColor(PRIMARY);
                float r = size * 0.28f;
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));

                // Pure White Car Silhouette
                g2.setColor(Color.WHITE);
                float s = size / 48.0f;

                // Cabin (Roof & Windows)
                g2.fill(new RoundRectangle2D.Float(16 * s, 14 * s, 17 * s, 11 * s, 4 * s, 4 * s));

                // Lower Body
                g2.fill(new RoundRectangle2D.Float(9 * s, 22 * s, 30 * s, 10 * s, 5 * s, 5 * s));

                // Windows cutout (Dark / Primary)
                g2.setColor(PRIMARY);
                g2.fill(new RoundRectangle2D.Float(18 * s, 16 * s, 6 * s, 6 * s, 2 * s, 2 * s));
                g2.fill(new RoundRectangle2D.Float(25.5f * s, 16 * s, 6 * s, 6 * s, 2 * s, 2 * s));

                // Wheels (Pure White)
                g2.setColor(new Color(248, 250, 252));
                g2.fillOval((int)(13 * s), (int)(28 * s), (int)(7 * s), (int)(7 * s));
                g2.fillOval((int)(28 * s), (int)(28 * s), (int)(7 * s), (int)(7 * s));

                // Wheel Hub
                g2.setColor(new Color(30, 41, 59));
                g2.fillOval((int)(15 * s), (int)(30 * s), (int)(3 * s), (int)(3 * s));
                g2.fillOval((int)(30 * s), (int)(30 * s), (int)(3 * s), (int)(3 * s));

                // Headlight glow
                g2.setColor(new Color(56, 189, 248)); // Sky 400
                g2.fillOval((int)(36 * s), (int)(24 * s), (int)(2.5f * s), (int)(3 * s));

                g2.dispose();
            }
        };
    }

    // --- Button Factory Methods ---

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(5, 150, 105)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(SUCCESS); }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_REGULAR);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");

        if (FlatLaf.isLafDark()) {
            btn.setBackground(new Color(30, 41, 59));
            btn.setForeground(new Color(241, 245, 249));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT_MAIN);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (FlatLaf.isLafDark()) {
                    btn.setBackground(new Color(51, 65, 85));
                } else {
                    btn.setBackground(new Color(241, 245, 249));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (FlatLaf.isLafDark()) {
                    btn.setBackground(new Color(30, 41, 59));
                } else {
                    btn.setBackground(Color.WHITE);
                }
            }
        });
        return btn;
    }

    // --- Card Panel Wrapper with Clean Rounded Border ---

    public static JPanel createCardPanel(int top, int left, int bottom, int right) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getCardBg());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(getCardBorder());
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(top, left, bottom, right));
        return card;
    }

    // --- Pill Badge Component ---

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(fg);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setOpaque(false);
        return badge;
    }
}
