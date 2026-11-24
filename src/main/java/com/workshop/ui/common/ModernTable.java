package com.workshop.ui.common;

import com.workshop.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(DefaultTableModel model) {
        super(model);
        initModernStyle();
    }

    public ModernTable() {
        super();
        initModernStyle();
    }

    private void initModernStyle() {
        setRowHeight(34);
        setFont(UIStyle.FONT_REGULAR);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(241, 245, 249));
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);

        JTableHeader header = getTableHeader();
        header.setFont(UIStyle.FONT_BOLD);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
