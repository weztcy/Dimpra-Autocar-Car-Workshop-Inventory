package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.Part;
import com.workshop.model.PartStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartDao {

    public List<Part> findAll(String keyword, Integer categoryId, PartStatus statusFilter) {
        List<Part> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.name AS category_name " +
                "FROM parts p " +
                "JOIN categories c ON p.category_id = c.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.part_number LIKE ? OR p.part_name LIKE ? OR p.brand LIKE ? OR p.compatibility LIKE ? OR p.storage_bin LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (statusFilter != null) {
            sql.append("AND p.status = ? ");
            params.add(statusFilter.name());
        }

        sql.append("ORDER BY p.part_number ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPart(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Part> findLowStock() {
        return findAll(null, null, PartStatus.LOW_STOCK);
    }

    public Part findById(int id) {
        String sql = "SELECT p.*, c.name AS category_name FROM parts p JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPart(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Part findByPartNumber(String partNumber) {
        String sql = "SELECT p.*, c.name AS category_name FROM parts p JOIN categories c ON p.category_id = c.id WHERE p.part_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, partNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPart(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Part p) {
        p.recalculateStatus();
        String sql = "INSERT INTO parts (part_number, part_name, category_id, brand, compatibility, unit_of_measure, storage_bin, cost_price, selling_price, min_stock, current_stock, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPartNumber());
            ps.setString(2, p.getPartName());
            ps.setInt(3, p.getCategoryId());
            ps.setString(4, p.getBrand());
            ps.setString(5, p.getCompatibility());
            ps.setString(6, p.getUnitOfMeasure());
            ps.setString(7, p.getStorageBin());
            ps.setBigDecimal(8, p.getCostPrice());
            ps.setBigDecimal(9, p.getSellingPrice());
            ps.setInt(10, p.getMinStock());
            ps.setInt(11, p.getCurrentStock());
            ps.setString(12, p.getStatus().name());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Part p) {
        p.recalculateStatus();
        String sql = "UPDATE parts SET part_number = ?, part_name = ?, category_id = ?, brand = ?, compatibility = ?, unit_of_measure = ?, " +
                     "storage_bin = ?, cost_price = ?, selling_price = ?, min_stock = ?, current_stock = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPartNumber());
            ps.setString(2, p.getPartName());
            ps.setInt(3, p.getCategoryId());
            ps.setString(4, p.getBrand());
            ps.setString(5, p.getCompatibility());
            ps.setString(6, p.getUnitOfMeasure());
            ps.setString(7, p.getStorageBin());
            ps.setBigDecimal(8, p.getCostPrice());
            ps.setBigDecimal(9, p.getSellingPrice());
            ps.setInt(10, p.getMinStock());
            ps.setInt(11, p.getCurrentStock());
            ps.setString(12, p.getStatus().name());
            ps.setInt(13, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStockAndRecalculate(Connection conn, int partId, int quantityChange) throws SQLException {
        // Read current stock and min_stock
        String selectSql = "SELECT current_stock, min_stock FROM parts WHERE id = ? FOR UPDATE";
        int currentStock = 0;
        int minStock = 5;
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentStock = rs.getInt("current_stock");
                    minStock = rs.getInt("min_stock");
                } else {
                    return false;
                }
            }
        }

        int newStock = currentStock + quantityChange;
        if (newStock < 0) newStock = 0;

        String statusStr;
        if (newStock == 0) statusStr = "OUT_OF_STOCK";
        else if (newStock <= minStock) statusStr = "LOW_STOCK";
        else statusStr = "IN_STOCK";

        String updateSql = "UPDATE parts SET current_stock = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, newStock);
            ps.setString(2, statusStr);
            ps.setInt(3, partId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM parts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Part mapPart(ResultSet rs) throws SQLException {
        Part p = new Part();
        p.setId(rs.getInt("id"));
        p.setPartNumber(rs.getString("part_number"));
        p.setPartName(rs.getString("part_name"));
        p.setCategoryId(rs.getInt("category_id"));
        try {
            p.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {}
        p.setBrand(rs.getString("brand"));
        p.setCompatibility(rs.getString("compatibility"));
        p.setUnitOfMeasure(rs.getString("unit_of_measure"));
        p.setStorageBin(rs.getString("storage_bin"));
        p.setCostPrice(rs.getBigDecimal("cost_price"));
        p.setSellingPrice(rs.getBigDecimal("selling_price"));
        p.setMinStock(rs.getInt("min_stock"));
        p.setCurrentStock(rs.getInt("current_stock"));
        try {
            p.setStatus(PartStatus.valueOf(rs.getString("status")));
        } catch (Exception e) {
            p.setStatus(PartStatus.IN_STOCK);
        }
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}
