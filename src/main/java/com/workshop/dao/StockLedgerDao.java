package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.StockLedger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockLedgerDao {

    public List<StockLedger> findAll(String keyword, Integer partId, String transactionType, String dateFrom, String dateTo, int limit) {
        List<StockLedger> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sl.*, p.part_number, p.part_name, u.full_name AS created_by_name " +
                "FROM stock_ledger sl " +
                "JOIN parts p ON sl.part_id = p.id " +
                "LEFT JOIN users u ON sl.created_by = u.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sl.reference_no LIKE ? OR p.part_number LIKE ? OR p.part_name LIKE ? OR sl.notes LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            for (int i = 0; i < 4; i++) params.add(term);
        }

        if (partId != null && partId > 0) {
            sql.append("AND sl.part_id = ? ");
            params.add(partId);
        }

        if (transactionType != null && !transactionType.trim().isEmpty() && !transactionType.equalsIgnoreCase("ALL")) {
            sql.append("AND sl.transaction_type = ? ");
            params.add(transactionType.trim());
        }

        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND DATE(sl.transaction_time) >= ? ");
            params.add(dateFrom.trim());
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND DATE(sl.transaction_time) <= ? ");
            params.add(dateTo.trim());
        }

        sql.append("ORDER BY sl.transaction_time DESC, sl.id DESC ");
        if (limit > 0) {
            sql.append("LIMIT ").append(limit);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLedger(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StockLedger> findByPartId(int partId) {
        return findAll(null, partId, null, null, null, 200);
    }

    public boolean create(StockLedger entry) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createWithConnection(conn, entry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createWithConnection(Connection conn, StockLedger entry) throws SQLException {
        String sql = "INSERT INTO stock_ledger (transaction_time, part_id, transaction_type, reference_no, " +
                     "quantity_change, stock_before, stock_after, unit_cost, total_cost, notes, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, entry.getTransactionTime());
            ps.setInt(2, entry.getPartId());
            ps.setString(3, entry.getTransactionType());
            ps.setString(4, entry.getReferenceNo());
            ps.setInt(5, entry.getQuantityChange());
            ps.setInt(6, entry.getStockBefore());
            ps.setInt(7, entry.getStockAfter());
            ps.setBigDecimal(8, entry.getUnitCost());
            ps.setBigDecimal(9, entry.getTotalCost());
            ps.setString(10, entry.getNotes());
            if (entry.getCreatedBy() > 0) {
                ps.setInt(11, entry.getCreatedBy());
            } else {
                ps.setNull(11, Types.INTEGER);
            }
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) entry.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    private StockLedger mapLedger(ResultSet rs) throws SQLException {
        StockLedger sl = new StockLedger();
        sl.setId(rs.getInt("id"));
        sl.setTransactionTime(rs.getTimestamp("transaction_time"));
        sl.setPartId(rs.getInt("part_id"));
        sl.setPartNumber(rs.getString("part_number"));
        sl.setPartName(rs.getString("part_name"));
        sl.setTransactionType(rs.getString("transaction_type"));
        sl.setReferenceNo(rs.getString("reference_no"));
        sl.setQuantityChange(rs.getInt("quantity_change"));
        sl.setStockBefore(rs.getInt("stock_before"));
        sl.setStockAfter(rs.getInt("stock_after"));
        sl.setUnitCost(rs.getBigDecimal("unit_cost"));
        sl.setTotalCost(rs.getBigDecimal("total_cost"));
        sl.setNotes(rs.getString("notes"));
        sl.setCreatedBy(rs.getInt("created_by"));
        try { sl.setCreatedByName(rs.getString("created_by_name")); } catch (SQLException ignored) {}
        return sl;
    }
}
