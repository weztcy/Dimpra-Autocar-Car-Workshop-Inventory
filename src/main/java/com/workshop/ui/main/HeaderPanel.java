package com.workshop.ui.main;

import com.workshop.config.AppConfig;
import com.workshop.model.User;
import com.workshop.service.AuthService;
import com.workshop.ui.theme.ThemeManager;
import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JLabel lblDateTime = new JLabel();

    public HeaderPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 52));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 20, 0, 20)
        ));
        setBackground(UIManager.getColor("Panel.background"));

        initUI();
        startTimer();
    }

    private void initUI() {
        // Left info
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        pnlLeft.setOpaque(false);

        JLabel lblWorkshop = new JLabel(AppConfig.WORKSHOP_NAME);
        lblWorkshop.setFont(UIStyle.FONT_HEADER);
        lblWorkshop.setForeground(UIStyle.PRIMARY);

        JLabel lblBadge = UIStyle.createBadge("Enterprise Edition", new Color(224, 231, 255), new Color(55, 48, 163));

        pnlLeft.add(lblWorkshop);
        pnlLeft.add(lblBadge);
        add(pnlLeft, BorderLayout.WEST);

        // Right info (Date + Theme Toggle + User)
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        pnlRight.setOpaque(false);

        lblDateTime.setFont(UIStyle.FONT_SMALL);
        lblDateTime.setForeground(UIStyle.TEXT_MUTED);
        pnlRight.add(lblDateTime);

        JButton btnTheme = new JButton(ThemeManager.isDarkMode() ? "Mode Terang" : "Mode Gelap");
        btnTheme.setFont(UIStyle.FONT_SMALL);
        btnTheme.setFocusPainted(false);
        btnTheme.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTheme.addActionListener(e -> {
            ThemeManager.toggleTheme(mainFrame);
            btnTheme.setText(ThemeManager.isDarkMode() ? "Mode Terang" : "Mode Gelap");
        });
        pnlRight.add(btnTheme);

        User cur = AuthService.getInstance().getCurrentUser();
        String initials = cur != null && !cur.getFullName().isEmpty() ? cur.getFullName().substring(0, 1).toUpperCase() : "U";
        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setFont(UIStyle.FONT_BOLD);
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(UIStyle.PRIMARY);
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlRight.add(avatar);

        add(pnlRight, BorderLayout.EAST);
    }

    private void startTimer() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", new java.util.Locale("id", "ID"));
        lblDateTime.setText(sdf.format(new Date()));
        Timer timer = new Timer(60000, e -> lblDateTime.setText(sdf.format(new Date())));
        timer.start();
    }
}
