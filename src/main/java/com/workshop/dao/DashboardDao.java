package com.workshop.dao;

import com.workshop.config.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DashboardDao {

    public static class DashboardKpis {
        public BigDecimal totalInventoryValuation = BigDecimal.ZERO;
        public BigDecimal totalAnnualRevenue2022 = BigDecimal.ZERO;
        public int totalCompletedRepairs2022 = 0;
        public int lowStockCount = 0;
        public int totalPartsCount = 0;
        public int totalSuppliersCount = 0;
    }

    public DashboardKpis getKpis() {
        DashboardKpis kpis = new DashboardKpis();

        // 1. Inventory Valuation & Parts Count & Low Stock Count
        String sqlParts = "SELECT " +
                          "COALESCE(SUM(cost_price * current_stock), 0) AS total_val, " +
                          "COUNT(*) AS total_parts, " +
                          "SUM(CASE WHEN current_stock <= min_stock THEN 1 ELSE 0 END) AS low_stock " +
                          "FROM parts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlParts)) {
            if (rs.next()) {
                kpis.totalInventoryValuation = rs.getBigDecimal("total_val");
                kpis.totalPartsCount = rs.getInt("total_parts");
                kpis.lowStockCount = rs.getInt("low_stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. 2022 Revenue & Completed Services
        String sqlRev = "SELECT " +
                        "COALESCE(SUM(grand_total), 0) AS total_rev, " +
                        "COUNT(*) AS completed_count " +
                        "FROM service_transactions " +
                        "WHERE YEAR(transaction_date) = 2022 AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlRev)) {
            if (rs.next()) {
                kpis.totalAnnualRevenue2022 = rs.getBigDecimal("total_rev");
                kpis.totalCompletedRepairs2022 = rs.getInt("completed_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Suppliers count
        String sqlSup = "SELECT COUNT(*) FROM suppliers WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSup)) {
            if (rs.next()) {
                kpis.totalSuppliersCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kpis;
    }

    public Map<Integer, BigDecimal> getMonthlyRevenue2022() {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) map.put(m, BigDecimal.ZERO);

        String sql = "SELECT MONTH(transaction_date) AS m, COALESCE(SUM(grand_total), 0) AS total " +
                     "FROM service_transactions " +
                     "WHERE YEAR(transaction_date) = 2022 AND status = 'COMPLETED' " +
                     "GROUP BY MONTH(transaction_date) ORDER BY m ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getInt("m"), rs.getBigDecimal("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Map<String, Object>> getTop5ConsumedParts2022() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.part_number, p.part_name, SUM(spu.quantity) AS total_qty, SUM(spu.subtotal) AS total_revenue " +
                     "FROM service_parts_used spu " +
                     "JOIN parts p ON spu.part_id = p.id " +
                     "JOIN service_transactions st ON spu.service_transaction_id = st.id " +
                     "WHERE YEAR(st.transaction_date) = 2022 " +
                     "GROUP BY p.id, p.part_number, p.part_name " +
                     "ORDER BY total_qty DESC LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("partNumber", rs.getString("part_number"));
                row.put("partName", rs.getString("part_name"));
                row.put("totalQty", rs.getInt("total_qty"));
                row.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getCategoryValuation() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT c.name AS cat_name, COALESCE(SUM(p.cost_price * p.current_stock), 0) AS valuation, COUNT(p.id) AS parts_count " +
                     "FROM categories c " +
                     "LEFT JOIN parts p ON c.id = p.category_id " +
                     "GROUP BY c.id, c.name " +
                     "ORDER BY valuation DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("categoryName", rs.getString("cat_name"));
                row.put("valuation", rs.getBigDecimal("valuation"));
                row.put("partsCount", rs.getInt("parts_count"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
