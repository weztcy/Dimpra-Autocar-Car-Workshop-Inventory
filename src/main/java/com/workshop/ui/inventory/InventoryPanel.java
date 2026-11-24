package com.workshop.ui.inventory;

import com.workshop.dao.CategoryDao;
import com.workshop.dao.PartDao;
import com.workshop.model.Category;
import com.workshop.model.Part;
import com.workshop.model.PartStatus;
import com.workshop.service.AuthService;
import com.workshop.service.ReportService;
import com.workshop.ui.common.BadgeRenderer;
import com.workshop.ui.common.CurrencyCellRenderer;
import com.workshop.ui.common.ModernTable;
import com.workshop.ui.common.SearchTextField;
import com.workshop.ui.theme.UIStyle;
import com.workshop.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends JPanel {
    private final PartDao partDao = new PartDao();
    private final CategoryDao categoryDao = new CategoryDao();

    private final SearchTextField txtSearch = new SearchTextField("Cari part number, nama, merek, bin...");
    private final JComboBox<CategoryWrapper> cmbCategory = new JComboBox<>();
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"SEMUA STATUS", "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"});

    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private final JLabel lblSummary = new JLabel("Memuat data suku cadang...");

    private List<Part> currentPartList = new ArrayList<>();

    private static class CategoryWrapper {
        Category category;
        CategoryWrapper(Category c) { this.category = c; }
        @Override
        public String toString() { return category == null ? "Semua Kategori" : category.getName(); }
    }

    public InventoryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // 1. Top Section (Title Row + Clean Toolbar Row)
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        // Header Row: Title & Global Actions
        JPanel pnlHeader = new JPanel(new BorderLayout(16, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pnlTitleBox = new JPanel();
        pnlTitleBox.setLayout(new BoxLayout(pnlTitleBox, BoxLayout.Y_AXIS));
        pnlTitleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Inventaris Suku Cadang (Spare Parts)");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_MAIN);

        JLabel lblSub = new JLabel("Katalog suku cadang otomotif, safety stock, dan nilai valuasi aset fisik");
        lblSub.setFont(UIStyle.FONT_REGULAR);
        lblSub.setForeground(UIStyle.TEXT_MUTED);

        pnlTitleBox.add(lblTitle);
        pnlTitleBox.add(Box.createVerticalStrut(2));
        pnlTitleBox.add(lblSub);
        pnlHeader.add(pnlTitleBox, BorderLayout.WEST);

        JPanel pnlGlobalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlGlobalActions.setOpaque(false);

        JButton btnAdd = UIStyle.createPrimaryButton("+ Tambah Suku Cadang");
        btnAdd.addActionListener(e -> onAddPart());

        JButton btnExport = UIStyle.createSecondaryButton("Export CSV");
        btnExport.addActionListener(e -> onExportCsv());

        pnlGlobalActions.add(btnAdd);
        pnlGlobalActions.add(btnExport);
        pnlHeader.add(pnlGlobalActions, BorderLayout.EAST);

        pnlTop.add(pnlHeader);
        pnlTop.add(Box.createVerticalStrut(12));

        // Toolbar: Search/Filters on Left, Row Actions on Right
        JPanel pnlToolbar = UIStyle.createCardPanel(8, 12, 8, 12);
        pnlToolbar.setLayout(new BorderLayout(12, 0));

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFilters.setOpaque(false);

        txtSearch.setPreferredSize(new Dimension(200, 32));
        pnlFilters.add(txtSearch);

        cmbCategory.addItem(new CategoryWrapper(null));
        for (Category c : categoryDao.findAll()) {
            cmbCategory.addItem(new CategoryWrapper(c));
        }
        cmbCategory.setPreferredSize(new Dimension(160, 32));
        cmbCategory.addActionListener(e -> loadData());
        pnlFilters.add(cmbCategory);

        cmbStatus.setPreferredSize(new Dimension(130, 32));
        cmbStatus.addActionListener(e -> loadData());
        pnlFilters.add(cmbStatus);

        JButton btnReset = UIStyle.createSecondaryButton("Reset Filter");
        btnReset.setPreferredSize(new Dimension(95, 32));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbCategory.setSelectedIndex(0);
            cmbStatus.setSelectedIndex(0);
            loadData();
        });
        pnlFilters.add(btnReset);
        pnlToolbar.add(pnlFilters, BorderLayout.WEST);

        // Row Actions on Right of Toolbar
        JPanel pnlRowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlRowActions.setOpaque(false);

        JButton btnEdit = UIStyle.createSecondaryButton("Edit");
        btnEdit.setPreferredSize(new Dimension(65, 32));
        btnEdit.addActionListener(e -> onEditPart());

        JButton btnAdjust = UIStyle.createSecondaryButton("Penyesuaian Stok");
        btnAdjust.setPreferredSize(new Dimension(135, 32));
        btnAdjust.addActionListener(e -> onAdjustStock());

        JButton btnLedger = UIStyle.createSecondaryButton("Kartu Stok");
        btnLedger.setPreferredSize(new Dimension(100, 32));
        btnLedger.addActionListener(e -> onViewLedger());

        pnlRowActions.add(btnEdit);
        pnlRowActions.add(btnAdjust);
        pnlRowActions.add(btnLedger);
        pnlToolbar.add(pnlRowActions, BorderLayout.EAST);

        pnlTop.add(pnlToolbar);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Table Section
        String[] cols = {
                "Kode Part", "Nama Suku Cadang", "Kategori", "Merek", "Kompatibilitas",
                "Lokasi Bin", "Harga Beli", "Harga Jual", "Min Stok", "Stok Fisik", "Status"
        };
        tableModel = new DefaultTableModel(cols, 0);
        table = new ModernTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(85);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(105);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(85);
        table.getColumnModel().getColumn(7).setPreferredWidth(85);
        table.getColumnModel().getColumn(8).setPreferredWidth(65);
        table.getColumnModel().getColumn(9).setPreferredWidth(65);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);

        table.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new CurrencyCellRenderer());
        table.getColumnModel().getColumn(10).setCellRenderer(new BadgeRenderer());

        // Double click to view ledger
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onViewLedger();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Bar
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        pnlFooter.setOpaque(false);

        lblSummary.setFont(UIStyle.FONT_BOLD);
        pnlFooter.add(lblSummary, BorderLayout.WEST);

        add(pnlFooter, BorderLayout.SOUTH);

        // Search live listener
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadData(); }
            public void removeUpdate(DocumentEvent e) { loadData(); }
            public void changedUpdate(DocumentEvent e) { loadData(); }
        });

        loadData();
    }

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            String keyword = txtSearch.getText().trim();
            CategoryWrapper cw = (CategoryWrapper) cmbCategory.getSelectedItem();
            Integer catId = (cw != null && cw.category != null) ? cw.category.getId() : null;

            String statusStr = (String) cmbStatus.getSelectedItem();
            PartStatus statusFilter = null;
            if (statusStr != null && !statusStr.equals("SEMUA STATUS")) {
                try { statusFilter = PartStatus.valueOf(statusStr); } catch (Exception ignored) {}
            }

            currentPartList = partDao.findAll(keyword, catId, statusFilter);
            tableModel.setRowCount(0);

            BigDecimal totalVal = BigDecimal.ZERO;
            int totalPhysical = 0;

            for (Part p : currentPartList) {
                totalVal = totalVal.add(p.getStockValuation());
                totalPhysical += p.getCurrentStock();

                tableModel.addRow(new Object[]{
                        p.getPartNumber(),
                        p.getPartName(),
                        p.getCategoryName(),
                        p.getBrand(),
                        p.getCompatibility(),
                        p.getStorageBin(),
                        p.getCostPrice(),
                        p.getSellingPrice(),
                        p.getMinStock() + " " + p.getUnitOfMeasure(),
                        p.getCurrentStock() + " " + p.getUnitOfMeasure(),
                        p.getStatus().name()
                });
            }

            lblSummary.setText(String.format("Menampilkan %d suku cadang | Total Stok Fisik: %d unit | Total Nilai Aset Inventaris: %s",
                    currentPartList.size(), totalPhysical, CurrencyUtil.format(totalVal)));
        });
    }

    private Part getSelectedPart() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow >= 0 && modelRow < currentPartList.size()) {
            return currentPartList.get(modelRow);
        }
        return null;
    }

    private void onAddPart() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        PartDialog dlg = new PartDialog(owner, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void onEditPart() {
        Part p = getSelectedPart();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih suku cadang yang ingin diedit dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        PartDialog dlg = new PartDialog(owner, p);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void onAdjustStock() {
        Part p = getSelectedPart();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih suku cadang yang akan disesuaikan stoknya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        StockAdjustmentDialog dlg = new StockAdjustmentDialog(owner, p);
        dlg.setVisible(true);
        if (dlg.isAdjusted()) {
            loadData();
        }
    }

    private void onViewLedger() {
        Part p = getSelectedPart();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih suku cadang untuk melihat riwayat Kartu Stok.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        StockLedgerDialog dlg = new StockLedgerDialog(owner, p);
        dlg.setVisible(true);
    }

    private void onExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Data_Inventaris_Suku_Cadang.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ReportService.exportTableToCsv(table, chooser.getSelectedFile());
        }
    }
}
