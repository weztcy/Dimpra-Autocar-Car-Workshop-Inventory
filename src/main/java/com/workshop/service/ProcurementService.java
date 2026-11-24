package com.workshop.service;

import com.workshop.config.DatabaseConnection;
import com.workshop.dao.PartDao;
import com.workshop.dao.PurchaseOrderDao;
import com.workshop.dao.StockLedgerDao;
import com.workshop.model.Part;
import com.workshop.model.PurchaseOrder;
import com.workshop.model.PurchaseOrderItem;
import com.workshop.model.StockLedger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ProcurementService {
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final PartDao partDao = new PartDao();
    private final StockLedgerDao ledgerDao = new StockLedgerDao();

    public boolean createPurchaseOrder(PurchaseOrder po) {
        return poDao.createWithItems(po);
    }

    public boolean receiveGoods(int poId, Date receivedDate, String paymentStatus, int receivedById) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            PurchaseOrder po = poDao.findById(poId);
            if (po == null) return false;

            List<PurchaseOrderItem> items = po.getItems();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // 1. Update each part's stock and log to stock ledger
            for (PurchaseOrderItem it : items) {
                int partId = it.getPartId();
                int qtyReceived = it.getQuantityOrdered(); // mark full receipt
                it.setQuantityReceived(qtyReceived);

                Part part = partDao.findById(partId);
                int stockBefore = (part != null) ? part.getCurrentStock() : 0;
                int stockAfter = stockBefore + qtyReceived;

                partDao.updateStockAndRecalculate(conn, partId, qtyReceived);

                StockLedger sl = new StockLedger();
                sl.setTransactionTime(now);
                sl.setPartId(partId);
                sl.setTransactionType("PROCUREMENT_IN");
                sl.setReferenceNo(po.getPoNumber());
                sl.setQuantityChange(qtyReceived);
                sl.setStockBefore(stockBefore);
                sl.setStockAfter(stockAfter);
                sl.setUnitCost(it.getUnitCost());
                sl.setTotalCost(it.getSubtotal());
                sl.setNotes("Penerimaan barang dari PO " + po.getPoNumber());
                sl.setCreatedBy(receivedById);
                ledgerDao.createWithConnection(conn, sl);
            }

            // 2. Update purchase_order_items quantity_received
            String updateItemsSql = "UPDATE purchase_order_items SET quantity_received = quantity_ordered WHERE po_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateItemsSql)) {
                ps.setInt(1, poId);
                ps.executeUpdate();
            }

            // 3. Update PO status
            String updatePoSql = "UPDATE purchase_orders SET status = 'RECEIVED_FULL', payment_status = ?, received_date = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updatePoSql)) {
                ps.setString(1, paymentStatus);
                ps.setDate(2, receivedDate != null ? receivedDate : new Date(System.currentTimeMillis()));
                ps.setInt(3, poId);
                ps.executeUpdate();
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
}
