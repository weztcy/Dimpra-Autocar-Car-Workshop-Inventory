package com.workshop.ui.common;

import com.workshop.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

public class CurrencyCellRenderer extends DefaultTableCellRenderer {

    public CurrencyCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        String formatted = "Rp 0";
        if (value instanceof BigDecimal) {
            formatted = CurrencyUtil.format((BigDecimal) value);
        } else if (value instanceof Number) {
            formatted = CurrencyUtil.format(((Number) value).doubleValue());
        } else if (value != null) {
            formatted = value.toString();
        }
        return super.getTableCellRendererComponent(table, formatted, isSelected, hasFocus, row, column);
    }
}
