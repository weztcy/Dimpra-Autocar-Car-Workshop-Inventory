package com.workshop.service;

import com.workshop.config.DatabaseConnection;
import com.workshop.dao.PartDao;
import com.workshop.dao.StockAdjustmentDao;
import com.workshop.dao.StockLedgerDao;
import com.workshop.model.Part;
import com.workshop.model.StockAdjustment;
import com.workshop.model.StockLedger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

public class InventoryService {
    private final PartDao partDao = new PartDao();
    private final StockAdjustmentDao adjustmentDao = new StockAdjustmentDao();
    private final StockLedgerDao ledgerDao = new StockLedgerDao();

    public boolean adjustStock(int partId, String type, int quantity, String reason, String notes, int approvedBy) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Part part = partDao.findById(partId);
            if (part == null) return false;

            int stockBefore = part.getCurrentStock();
            int qtyChange = type.equalsIgnoreCase("INCREASE") ? quantity : -quantity;
            int stockAfter = Math.max(0, stockBefore + qtyChange);

            // 1. Update parts table
            partDao.updateStockAndRecalculate(conn, partId, qtyChange);

            // 2. Insert stock_adjustments
            String adjCode = adjustmentDao.generateNextAdjustmentCode();
            StockAdjustment adj = new StockAdjustment();
            adj.setAdjustmentCode(adjCode);
            adj.setAdjustmentDate(new Date(System.currentTimeMillis()));
            adj.setPartId(partId);
            adj.setAdjustmentType(type);
            adj.setQuantity(quantity);
            adj.setReason(reason);
            adj.setNotes(notes);
            adj.setApprovedBy(approvedBy);
            adjustmentDao.create(adj);

            // 3. Insert stock_ledger audit entry
            StockLedger ledger = new StockLedger();
            ledger.setTransactionTime(new Timestamp(System.currentTimeMillis()));
            ledger.setPartId(partId);
            ledger.setTransactionType(type.equalsIgnoreCase("INCREASE") ? "ADJUSTMENT_ADD" : "ADJUSTMENT_SUB");
            ledger.setReferenceNo(adjCode);
            ledger.setQuantityChange(qtyChange);
            ledger.setStockBefore(stockBefore);
            ledger.setStockAfter(stockAfter);
            ledger.setUnitCost(part.getCostPrice());
            ledger.setTotalCost(part.getCostPrice().multiply(BigDecimal.valueOf(quantity)));
            ledger.setNotes("Penyesuaian stok: " + reason + (notes != null && !notes.isEmpty() ? " - " + notes : ""));
            ledger.setCreatedBy(approvedBy);
            ledgerDao.createWithConnection(conn, ledger);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        return false;
    }
}
