package com.workshop;

import com.workshop.config.DatabaseConnection;
import com.workshop.ui.auth.LoginFrame;
import com.workshop.ui.theme.ThemeManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize modern FlatLaf Look and Feel
        ThemeManager.setup();

        // Verify MySQL database connection
        System.out.println("Connecting to Car Workshop MySQL Database...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("Successfully connected to MySQL database: car_workshop_db");
        } else {
            System.err.println("WARNING: Could not connect to MySQL database. Please verify MySQL service is running on port 3306.");
        }

        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
