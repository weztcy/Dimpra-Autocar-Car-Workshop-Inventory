package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.StockAdjustment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockAdjustmentDao {

    public List<StockAdjustment> findAll(String keyword) {
        List<StockAdjustment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sa.*, p.part_number, p.part_name, u.full_name AS approved_by_name " +
                "FROM stock_adjustments sa " +
                "JOIN parts p ON sa.part_id = p.id " +
                "JOIN users u ON sa.approved_by = u.id " +
                "WHERE 1=1 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sa.adjustment_code LIKE ? OR p.part_number LIKE ? OR p.part_name LIKE ? OR sa.reason LIKE ?) ");
        }
        sql.append("ORDER BY sa.adjustment_date DESC, sa.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String term = "%" + keyword.trim() + "%";
                for (int i = 1; i <= 4; i++) ps.setString(i, term);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockAdjustment a = new StockAdjustment();
                    a.setId(rs.getInt("id"));
                    a.setAdjustmentCode(rs.getString("adjustment_code"));
                    a.setAdjustmentDate(rs.getDate("adjustment_date"));
                    a.setPartId(rs.getInt("part_id"));
                    a.setPartNumber(rs.getString("part_number"));
                    a.setPartName(rs.getString("part_name"));
                    a.setAdjustmentType(rs.getString("adjustment_type"));
                    a.setQuantity(rs.getInt("quantity"));
                    a.setReason(rs.getString("reason"));
                    a.setNotes(rs.getString("notes"));
                    a.setApprovedBy(rs.getInt("approved_by"));
                    a.setApprovedByName(rs.getString("approved_by_name"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean create(StockAdjustment sa) {
        String sql = "INSERT INTO stock_adjustments (adjustment_code, adjustment_date, part_id, adjustment_type, quantity, reason, notes, approved_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sa.getAdjustmentCode());
            ps.setDate(2, sa.getAdjustmentDate());
            ps.setInt(3, sa.getPartId());
            ps.setString(4, sa.getAdjustmentType());
            ps.setInt(5, sa.getQuantity());
            ps.setString(6, sa.getReason());
            ps.setString(7, sa.getNotes());
            ps.setInt(8, sa.getApprovedBy());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) sa.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateNextAdjustmentCode() {
        String sql = "SELECT COUNT(*) FROM stock_adjustments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("ADJ-2022-%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ADJ-2022-0999";
    }
}
