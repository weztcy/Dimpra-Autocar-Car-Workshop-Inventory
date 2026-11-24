package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.ServiceLaborItem;
import com.workshop.model.ServicePartUsed;
import com.workshop.model.ServiceTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransactionDao {

    public List<ServiceTransaction> findAll(String keyword, String status, String dateFrom, String dateTo) {
        List<ServiceTransaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT st.*, v.plate_number, CONCAT(v.make, ' ', v.model) AS vehicle_model, " +
                "c.name AS customer_name, c.phone AS customer_phone, u.full_name AS technician_name, uc.full_name AS created_by_name " +
                "FROM service_transactions st " +
                "JOIN vehicles v ON st.vehicle_id = v.id " +
                "JOIN customers c ON v.customer_id = c.id " +
                "JOIN users u ON st.technician_id = u.id " +
                "LEFT JOIN users uc ON st.created_by = uc.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (st.transaction_code LIKE ? OR v.plate_number LIKE ? OR c.name LIKE ? OR st.service_type LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            for (int i = 0; i < 4; i++) params.add(term);
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append("AND st.status = ? ");
            params.add(status.trim());
        }

        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND DATE(st.transaction_date) >= ? ");
            params.add(dateFrom.trim());
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND DATE(st.transaction_date) <= ? ");
            params.add(dateTo.trim());
        }

        sql.append("ORDER BY st.transaction_date DESC, st.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ServiceTransaction findById(int id) {
        String sql = "SELECT st.*, v.plate_number, CONCAT(v.make, ' ', v.model) AS vehicle_model, " +
                     "c.name AS customer_name, c.phone AS customer_phone, u.full_name AS technician_name, uc.full_name AS created_by_name " +
                     "FROM service_transactions st " +
                     "JOIN vehicles v ON st.vehicle_id = v.id " +
                     "JOIN customers c ON v.customer_id = c.id " +
                     "JOIN users u ON st.technician_id = u.id " +
                     "LEFT JOIN users uc ON st.created_by = uc.id " +
                     "WHERE st.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceTransaction trx = mapTransaction(rs);
                    trx.setPartsUsed(getPartsUsed(id));
                    trx.setLaborItems(getLaborItems(id));
                    return trx;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ServicePartUsed> getPartsUsed(int transactionId) {
        List<ServicePartUsed> items = new ArrayList<>();
        String sql = "SELECT spu.*, p.part_number, p.part_name, p.brand, p.unit_of_measure " +
                     "FROM service_parts_used spu " +
                     "JOIN parts p ON spu.part_id = p.id " +
                     "WHERE spu.service_transaction_id = ? ORDER BY spu.id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServicePartUsed pu = new ServicePartUsed();
                    pu.setId(rs.getInt("id"));
                    pu.setServiceTransactionId(rs.getInt("service_transaction_id"));
                    pu.setPartId(rs.getInt("part_id"));
                    pu.setPartNumber(rs.getString("part_number"));
                    pu.setPartName(rs.getString("part_name"));
                    pu.setBrand(rs.getString("brand"));
                    pu.setUnitOfMeasure(rs.getString("unit_of_measure"));
                    pu.setQuantity(rs.getInt("quantity"));
                    pu.setUnitPrice(rs.getBigDecimal("unit_price"));
                    pu.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(pu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<ServiceLaborItem> getLaborItems(int transactionId) {
        List<ServiceLaborItem> items = new ArrayList<>();
        String sql = "SELECT * FROM service_labor_items WHERE service_transaction_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceLaborItem li = new ServiceLaborItem();
                    li.setId(rs.getInt("id"));
                    li.setServiceTransactionId(rs.getInt("service_transaction_id"));
                    li.setServiceName(rs.getString("service_name"));
                    li.setHours(rs.getBigDecimal("hours"));
                    li.setRate(rs.getBigDecimal("rate"));
                    li.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(li);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean createWithItems(ServiceTransaction trx) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO service_transactions (transaction_code, transaction_date, vehicle_id, technician_id, service_type, " +
                         "odometer_in, customer_complaint, technician_diagnosis, parts_total, labor_total, discount, tax_amount, grand_total, " +
                         "status, payment_method, payment_status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, trx.getTransactionCode());
                ps.setTimestamp(2, trx.getTransactionDate());
                ps.setInt(3, trx.getVehicleId());
                ps.setInt(4, trx.getTechnicianId());
                ps.setString(5, trx.getServiceType());
                ps.setInt(6, trx.getOdometerIn());
                ps.setString(7, trx.getCustomerComplaint());
                ps.setString(8, trx.getTechnicianDiagnosis());
                ps.setBigDecimal(9, trx.getPartsTotal());
                ps.setBigDecimal(10, trx.getLaborTotal());
                ps.setBigDecimal(11, trx.getDiscount());
                ps.setBigDecimal(12, trx.getTaxAmount());
                ps.setBigDecimal(13, trx.getGrandTotal());
                ps.setString(14, trx.getStatus());
                ps.setString(15, trx.getPaymentMethod());
                ps.setString(16, trx.getPaymentStatus());
                ps.setInt(17, trx.getCreatedBy());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) trx.setId(keys.getInt(1));
                }
            }

            // Insert parts used
            if (!trx.getPartsUsed().isEmpty()) {
                String pSql = "INSERT INTO service_parts_used (service_transaction_id, part_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psPart = conn.prepareStatement(pSql)) {
                    for (ServicePartUsed pu : trx.getPartsUsed()) {
                        psPart.setInt(1, trx.getId());
                        psPart.setInt(2, pu.getPartId());
                        psPart.setInt(3, pu.getQuantity());
                        psPart.setBigDecimal(4, pu.getUnitPrice());
                        psPart.setBigDecimal(5, pu.getSubtotal());
                        psPart.addBatch();
                    }
                    psPart.executeBatch();
                }
            }

            // Insert labor items
            if (!trx.getLaborItems().isEmpty()) {
                String lSql = "INSERT INTO service_labor_items (service_transaction_id, service_name, hours, rate, subtotal) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psLab = conn.prepareStatement(lSql)) {
                    for (ServiceLaborItem li : trx.getLaborItems()) {
                        psLab.setInt(1, trx.getId());
                        psLab.setString(2, li.getServiceName());
                        psLab.setBigDecimal(3, li.getHours());
                        psLab.setBigDecimal(4, li.getRate());
                        psLab.setBigDecimal(5, li.getSubtotal());
                        psLab.addBatch();
                    }
                    psLab.executeBatch();
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
        String sql = "UPDATE service_transactions SET status = ?, payment_method = ?, payment_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, paymentMethod);
            ps.setString(3, paymentStatus);
            ps.setInt(4, trxId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateNextTrxCode() {
        String sql = "SELECT COUNT(*) FROM service_transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("TRX-2022-%04d", count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "TRX-2022-0999";
    }

    private ServiceTransaction mapTransaction(ResultSet rs) throws SQLException {
        ServiceTransaction t = new ServiceTransaction();
        t.setId(rs.getInt("id"));
        t.setTransactionCode(rs.getString("transaction_code"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));
        t.setVehicleId(rs.getInt("vehicle_id"));
        t.setPlateNumber(rs.getString("plate_number"));
        t.setVehicleModel(rs.getString("vehicle_model"));
        t.setCustomerName(rs.getString("customer_name"));
        t.setCustomerPhone(rs.getString("customer_phone"));
        t.setTechnicianId(rs.getInt("technician_id"));
        t.setTechnicianName(rs.getString("technician_name"));
        t.setServiceType(rs.getString("service_type"));
        t.setOdometerIn(rs.getInt("odometer_in"));
        t.setCustomerComplaint(rs.getString("customer_complaint"));
        t.setTechnicianDiagnosis(rs.getString("technician_diagnosis"));
        t.setPartsTotal(rs.getBigDecimal("parts_total"));
        t.setLaborTotal(rs.getBigDecimal("labor_total"));
        t.setDiscount(rs.getBigDecimal("discount"));
        t.setTaxAmount(rs.getBigDecimal("tax_amount"));
        t.setGrandTotal(rs.getBigDecimal("grand_total"));
        t.setStatus(rs.getString("status"));
        t.setPaymentMethod(rs.getString("payment_method"));
        t.setPaymentStatus(rs.getString("payment_status"));
        t.setCreatedBy(rs.getInt("created_by"));
        try { t.setCreatedByName(rs.getString("created_by_name")); } catch (SQLException ignored) {}
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
