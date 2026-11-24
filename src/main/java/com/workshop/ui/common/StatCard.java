package com.workshop.ui.common;

import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StatCard extends JPanel {
    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtitle;
    private final Color accentColor;
    private final String iconSymbol;

    public StatCard(String title, String value, String subtitle, String pillText, Color accentColor, String iconSymbol) {
        this.accentColor = accentColor;
        this.iconSymbol = iconSymbol;

        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 14, 12, 14));
        setOpaque(false);
        setPreferredSize(new Dimension(210, 115));
        setMinimumSize(new Dimension(160, 105));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        // Top Row: Title on Left, Icon Avatar on Right (Zero Overlap Guaranteed!)
        JPanel pnlTop = new JPanel(new BorderLayout(6, 0));
        pnlTop.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_SMALL_BOLD);
        lblTitle.setForeground(UIStyle.getTextSecondary());
        pnlTop.add(lblTitle, BorderLayout.WEST);

        // Circular/Rounded Icon Avatar Badge
        JLabel lblIcon = new JLabel(iconSymbol, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color tint = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 32);
                g2.setColor(tint);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setPreferredSize(new Dimension(30, 30));
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblIcon.setForeground(accentColor);
        pnlTop.add(lblIcon, BorderLayout.EAST);

        add(pnlTop, BorderLayout.NORTH);

        // Center: Bold KPI Value
        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblValue.setForeground(UIStyle.getTextMain());
        add(lblValue, BorderLayout.CENTER);

        // Bottom Row: Subtitle with status dot
        JPanel pnlBottom = new JPanel(new BorderLayout(6, 0));
        pnlBottom.setOpaque(false);

        JLabel lblDot = new JLabel("●");
        lblDot.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        lblDot.setForeground(accentColor);
        pnlBottom.add(lblDot, BorderLayout.WEST);

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(UIStyle.FONT_SMALL);
        lblSubtitle.setForeground(UIStyle.getTextMuted());
        pnlBottom.add(lblSubtitle, BorderLayout.CENTER);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtitle(String sub) {
        lblSubtitle.setText(sub);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card background (Dynamic Light / Dark mode)
        g2.setColor(UIStyle.getCardBg());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

        // Subtle Card border (Dynamic Light / Dark mode)
        g2.setColor(UIStyle.getCardBorder());
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));

        // Top accent bar (3px)
        g2.setColor(accentColor);
        g2.fill(new RoundRectangle2D.Float(14, 0, 32, 3, 2, 2));

        g2.dispose();
        super.paintComponent(g);
    }
}
