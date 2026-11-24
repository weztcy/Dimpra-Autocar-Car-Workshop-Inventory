package com.workshop.ui.common;

import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchTextField extends JTextField {
    private final String placeholder;

    public SearchTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UIStyle.FONT_REGULAR);
        setPreferredSize(new Dimension(240, 34));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        putClientProperty("JTextField.placeholderText", placeholder);
        putClientProperty("JTextField.showClearButton", true);
        putClientProperty("Component.arc", 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !(FocusManager.getCurrentManager().getFocusOwner() == this)) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UIStyle.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 10, y);
            g2.dispose();
        }
    }
}
