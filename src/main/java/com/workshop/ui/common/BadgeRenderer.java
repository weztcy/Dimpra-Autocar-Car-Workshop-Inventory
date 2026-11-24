package com.workshop.ui.common;

import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(UIStyle.FONT_SMALL);
        label.setBorder(new EmptyBorder(3, 8, 3, 8));

        if (value == null) return label;
        String text = value.toString().trim();

        Color bg = new Color(241, 245, 249);
        Color fg = new Color(71, 85, 105);

        switch (text.toUpperCase()) {
            case "IN_STOCK":
            case "IN STOCK":
            case "RECEIVED_FULL":
            case "COMPLETED":
            case "PAID":
            case "ACTIVE":
                bg = new Color(220, 252, 231); // Light emerald
                fg = new Color(22, 101, 52);   // Dark emerald
                break;

            case "LOW_STOCK":
            case "LOW STOCK":
            case "RECEIVED_PARTIAL":
            case "PARTIALLY_PAID":
            case "IN_PROGRESS":
            case "ORDERED":
                bg = new Color(254, 243, 199); // Light amber
                fg = new Color(146, 64, 14);   // Dark amber
                break;

            case "OUT_OF_STOCK":
            case "OUT OF STOCK":
            case "UNPAID":
            case "CANCELLED":
            case "INACTIVE":
                bg = new Color(254, 226, 226); // Light red
                fg = new Color(153, 27, 27);   // Dark red
                break;

            case "PENDING":
            case "DRAFT":
                bg = new Color(241, 245, 249);
                fg = new Color(100, 116, 139);
                break;
        }

        if (!isSelected) {
            label.setBackground(bg);
            label.setForeground(fg);
            label.setOpaque(true);
        }

        return label;
    }
}
