package com.workshop.dao;

import com.workshop.config.DatabaseConnection;
import com.workshop.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao {

    public List<Vehicle> findAll(String keyword) {
        List<Vehicle> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT v.*, c.name AS customer_name, c.phone AS customer_phone " +
                "FROM vehicles v " +
                "JOIN customers c ON v.customer_id = c.id " +
                "WHERE 1=1 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (v.plate_number LIKE ? OR v.vin LIKE ? OR v.make LIKE ? OR v.model LIKE ? OR c.name LIKE ?) ");
        }
        sql.append("ORDER BY v.plate_number ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String term = "%" + keyword.trim() + "%";
                for (int i = 1; i <= 5; i++) ps.setString(i, term);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVehicle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Vehicle> findByCustomerId(int customerId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.*, c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM vehicles v JOIN customers c ON v.customer_id = c.id WHERE v.customer_id = ? ORDER BY v.plate_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVehicle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Vehicle findById(int id) {
        String sql = "SELECT v.*, c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM vehicles v JOIN customers c ON v.customer_id = c.id WHERE v.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapVehicle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Vehicle v) {
        String sql = "INSERT INTO vehicles (customer_id, plate_number, vin, make, model, year, color, transmission, odometer) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getCustomerId());
            ps.setString(2, v.getPlateNumber());
            ps.setString(3, v.getVin());
            ps.setString(4, v.getMake());
            ps.setString(5, v.getModel());
            ps.setInt(6, v.getYear());
            ps.setString(7, v.getColor());
            ps.setString(8, v.getTransmission());
            ps.setInt(9, v.getOdometer());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) v.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Vehicle v) {
        String sql = "UPDATE vehicles SET customer_id = ?, plate_number = ?, vin = ?, make = ?, model = ?, year = ?, color = ?, " +
                     "transmission = ?, odometer = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getCustomerId());
            ps.setString(2, v.getPlateNumber());
            ps.setString(3, v.getVin());
            ps.setString(4, v.getMake());
            ps.setString(5, v.getModel());
            ps.setInt(6, v.getYear());
            ps.setString(7, v.getColor());
            ps.setString(8, v.getTransmission());
            ps.setInt(9, v.getOdometer());
            ps.setInt(10, v.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setId(rs.getInt("id"));
        v.setCustomerId(rs.getInt("customer_id"));
        v.setCustomerName(rs.getString("customer_name"));
        v.setCustomerPhone(rs.getString("customer_phone"));
        v.setPlateNumber(rs.getString("plate_number"));
        v.setVin(rs.getString("vin"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setColor(rs.getString("color"));
        v.setTransmission(rs.getString("transmission"));
        v.setOdometer(rs.getInt("odometer"));
        v.setCreatedAt(rs.getTimestamp("created_at"));
        return v;
    }
}
