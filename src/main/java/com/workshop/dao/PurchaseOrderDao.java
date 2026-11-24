package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.PurchaseOrder;
import com.workshop.model.PurchaseOrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDao {

    public List<PurchaseOrder> findAll(String keyword, String status, String dateFrom, String dateTo) {
        List<PurchaseOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT po.*, s.name AS supplier_name, s.phone AS supplier_phone, u.full_name AS created_by_name " +
                "FROM purchase_orders po " +
                "JOIN suppliers s ON po.supplier_id = s.id " +
                "LEFT JOIN users u ON po.created_by = u.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (po.po_number LIKE ? OR s.name LIKE ? OR po.notes LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append("AND po.status = ? ");
            params.add(status.trim());
        }

        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND po.order_date >= ? ");
            params.add(dateFrom.trim());
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND po.order_date <= ? ");
            params.add(dateTo.trim());
        }

        sql.append("ORDER BY po.order_date DESC, po.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPurchaseOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public PurchaseOrder findById(int id) {
        String sql = "SELECT po.*, s.name AS supplier_name, s.phone AS supplier_phone, u.full_name AS created_by_name " +
                     "FROM purchase_orders po " +
                     "JOIN suppliers s ON po.supplier_id = s.id " +
                     "LEFT JOIN users u ON po.created_by = u.id " +
                     "WHERE po.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder po = mapPurchaseOrder(rs);
                    po.setItems(getItems(id));
                    return po;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PurchaseOrderItem> getItems(int poId) {
        List<PurchaseOrderItem> items = new ArrayList<>();
        String sql = "SELECT poi.*, p.part_number, p.part_name, p.brand, p.unit_of_measure " +
                     "FROM purchase_order_items poi " +
                     "JOIN parts p ON poi.part_id = p.id " +
                     "WHERE poi.po_id = ? ORDER BY poi.id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderItem it = new PurchaseOrderItem();
                    it.setId(rs.getInt("id"));
                    it.setPoId(rs.getInt("po_id"));
                    it.setPartId(rs.getInt("part_id"));
                    it.setPartNumber(rs.getString("part_number"));
                    it.setPartName(rs.getString("part_name"));
                    it.setBrand(rs.getString("brand"));
                    it.setUnitOfMeasure(rs.getString("unit_of_measure"));
                    it.setQuantityOrdered(rs.getInt("quantity_ordered"));
                    it.setQuantityReceived(rs.getInt("quantity_received"));
                    it.setUnitCost(rs.getBigDecimal("unit_cost"));
                    it.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean createWithItems(PurchaseOrder po) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO purchase_orders (po_number, supplier_id, order_date, expected_date, received_date, status, payment_status, total_amount, notes, created_by) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, po.getPoNumber());
                ps.setInt(2, po.getSupplierId());
                ps.setDate(3, po.getOrderDate());
                ps.setDate(4, po.getExpectedDate());
                ps.setDate(5, po.getReceivedDate());
                ps.setString(6, po.getStatus());
                ps.setString(7, po.getPaymentStatus());
                ps.setBigDecimal(8, po.getTotalAmount());
                ps.setString(9, po.getNotes());
                ps.setInt(10, po.getCreatedBy());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) po.setId(keys.getInt(1));
                }
            }

            String itemSql = "INSERT INTO purchase_order_items (po_id, part_id, quantity_ordered, quantity_received, unit_cost, subtotal) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                for (PurchaseOrderItem it : po.getItems()) {
                    psItem.setInt(1, po.getId());
                    psItem.setInt(2, it.getPartId());
                    psItem.setInt(3, it.getQuantityOrdered());
                    psItem.setInt(4, it.getQuantityReceived());
                    psItem.setBigDecimal(5, it.getUnitCost());
                    psItem.setBigDecimal(6, it.getSubtotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
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

    public boolean updateStatusAndPayment(int poId, String status, String paymentStatus, Date receivedDate) {
        String sql = "UPDATE purchase_orders SET status = ?, payment_status = ?, received_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, paymentStatus);
            ps.setDate(3, receivedDate);
            ps.setInt(4, poId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateNextPoNumber() {
        String sql = "SELECT COUNT(*) FROM purchase_orders";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("PO-2022-%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PO-2022-0999";
    }

    private PurchaseOrder mapPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(rs.getInt("id"));
        po.setPoNumber(rs.getString("po_number"));
        po.setSupplierId(rs.getInt("supplier_id"));
        po.setSupplierName(rs.getString("supplier_name"));
        try { po.setSupplierPhone(rs.getString("supplier_phone")); } catch (SQLException ignored) {}
        po.setOrderDate(rs.getDate("order_date"));
        po.setExpectedDate(rs.getDate("expected_date"));
        po.setReceivedDate(rs.getDate("received_date"));
        po.setStatus(rs.getString("status"));
        po.setPaymentStatus(rs.getString("payment_status"));
        po.setTotalAmount(rs.getBigDecimal("total_amount"));
        po.setNotes(rs.getString("notes"));
        po.setCreatedBy(rs.getInt("created_by"));
        try { po.setCreatedByName(rs.getString("created_by_name")); } catch (SQLException ignored) {}
        po.setCreatedAt(rs.getTimestamp("created_at"));
        return po;
    }
}
