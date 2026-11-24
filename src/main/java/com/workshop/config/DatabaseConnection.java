package com.workshop.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource dataSource;
    private static String jdbcUrl;
    private static String dbUser;
    private static String dbPass;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        try {
            String host = AppConfig.getProperty("db.host", "127.0.0.1");
            String port = AppConfig.getProperty("db.port", "3306");
            String database = AppConfig.getProperty("db.database", "car_workshop_db");
            dbUser = AppConfig.getProperty("db.username", "root");
            dbPass = AppConfig.getProperty("db.password", "");

            jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8",
                    host, port, database);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.setMaximumPoolSize(Integer.parseInt(AppConfig.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(AppConfig.getProperty("db.pool.minimumIdle", "2")));
            config.setIdleTimeout(Long.parseLong(AppConfig.getProperty("db.pool.idleTimeout", "30000")));
            config.setConnectionTimeout(Long.parseLong(AppConfig.getProperty("db.pool.connectionTimeout", "10000")));
            config.setPoolName("CarWorkshopPool");

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            System.err.println("HikariCP init failed, will use direct DriverManager: " + e.getMessage());
            dataSource = null;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                System.err.println("Hikari connection failed, falling back to direct connection: " + e.getMessage());
            }
        }
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
