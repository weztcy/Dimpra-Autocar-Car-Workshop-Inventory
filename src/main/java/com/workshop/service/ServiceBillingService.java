package com.workshop.service;

import com.workshop.config.DatabaseConnection;
import com.workshop.dao.PartDao;
import com.workshop.dao.ServiceTransactionDao;
import com.workshop.dao.StockLedgerDao;
import com.workshop.model.Part;
import com.workshop.model.ServicePartUsed;
import com.workshop.model.ServiceTransaction;
import com.workshop.model.StockLedger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ServiceBillingService {
    private final ServiceTransactionDao transactionDao = new ServiceTransactionDao();
    private final PartDao partDao = new PartDao();
    private final StockLedgerDao ledgerDao = new StockLedgerDao();

    public boolean createWorkOrder(ServiceTransaction trx, boolean deductStockImmediately) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            trx.recalculateTotals();

            // 1. Create transaction and line items
            if (!transactionDao.createWithItems(trx)) {
                conn.rollback();
                return false;
            }

            // 2. If deduct stock immediately (or if completed)
            if (deductStockImmediately || "COMPLETED".equals(trx.getStatus())) {
                Timestamp now = trx.getTransactionDate() != null ? trx.getTransactionDate() : new Timestamp(System.currentTimeMillis());
                for (ServicePartUsed pu : trx.getPartsUsed()) {
                    Part part = partDao.findById(pu.getPartId());
                    int stockBefore = (part != null) ? part.getCurrentStock() : 0;
                    int stockAfter = Math.max(0, stockBefore - pu.getQuantity());

                    partDao.updateStockAndRecalculate(conn, pu.getPartId(), -pu.getQuantity());

                    StockLedger sl = new StockLedger();
                    sl.setTransactionTime(now);
                    sl.setPartId(pu.getPartId());
                    sl.setTransactionType("SERVICE_OUT");
                    sl.setReferenceNo(trx.getTransactionCode());
                    sl.setQuantityChange(-pu.getQuantity());
                    sl.setStockBefore(stockBefore);
                    sl.setStockAfter(stockAfter);
                    BigDecimal unitCost = (part != null) ? part.getCostPrice() : BigDecimal.ZERO;
                    sl.setUnitCost(unitCost);
                    sl.setTotalCost(unitCost.multiply(BigDecimal.valueOf(pu.getQuantity())));
                    sl.setNotes("Pemakaian suku cadang PKB " + trx.getTransactionCode());
                    sl.setCreatedBy(trx.getTechnicianId());
                    ledgerDao.createWithConnection(conn, sl);
                }
            }

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

    public boolean updateStatusAndPayment(int trxId, String status, String paymentMethod, String paymentStatus) {
        return transactionDao.updateStatusAndPayment(trxId, status, paymentMethod, paymentStatus);
    }
}
