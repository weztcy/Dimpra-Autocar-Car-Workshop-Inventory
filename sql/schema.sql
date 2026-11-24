-- ========================================================
-- CAR WORKSHOP INVENTORY MANAGEMENT SYSTEM
-- Database Schema for MySQL 8.x
-- ========================================================

DROP TABLE IF EXISTS service_labor_items;
DROP TABLE IF EXISTS service_parts_used;
DROP TABLE IF EXISTS service_transactions;
DROP TABLE IF EXISTS stock_adjustments;
DROP TABLE IF EXISTS stock_ledger;
DROP TABLE IF EXISTS purchase_order_items;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS parts;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

-- 1. Users Table (RBAC: Supervisor, Clerk, Technician)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30),
    role ENUM('WORKSHOP_SUPERVISOR', 'INVENTORY_CLERK', 'SERVICE_TECHNICIAN') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Categories Table
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Suppliers Table
CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    payment_terms VARCHAR(50) DEFAULT 'Net 30',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Spare Parts Catalog
CREATE TABLE parts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    part_number VARCHAR(50) NOT NULL UNIQUE,
    part_name VARCHAR(200) NOT NULL,
    category_id INT NOT NULL,
    brand VARCHAR(100) NOT NULL,
    compatibility VARCHAR(255),
    unit_of_measure VARCHAR(20) NOT NULL DEFAULT 'PCS',
    storage_bin VARCHAR(50) NOT NULL DEFAULT 'A-01-01',
    cost_price DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    selling_price DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    min_stock INT NOT NULL DEFAULT 5,
    current_stock INT NOT NULL DEFAULT 0,
    status ENUM('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED') NOT NULL DEFAULT 'IN_STOCK',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_part_number (part_number),
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Stock Ledger (Relational Stock Audit Trail & Card)
CREATE TABLE stock_ledger (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_time DATETIME NOT NULL,
    part_id INT NOT NULL,
    transaction_type ENUM('PROCUREMENT_IN', 'SERVICE_OUT', 'ADJUSTMENT_ADD', 'ADJUSTMENT_SUB', 'RETURN_IN', 'SCRAP_OUT') NOT NULL,
    reference_no VARCHAR(50) NOT NULL,
    quantity_change INT NOT NULL,
    stock_before INT NOT NULL,
    stock_after INT NOT NULL,
    unit_cost DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    total_cost DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    notes VARCHAR(255),
    created_by INT,
    FOREIGN KEY (part_id) REFERENCES parts(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_part_time (part_id, transaction_time),
    INDEX idx_ref (reference_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Purchase Orders (Procurement)
CREATE TABLE purchase_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id INT NOT NULL,
    order_date DATE NOT NULL,
    expected_date DATE,
    received_date DATE,
    status ENUM('DRAFT', 'ORDERED', 'RECEIVED_PARTIAL', 'RECEIVED_FULL', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    payment_status ENUM('UNPAID', 'PARTIALLY_PAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
    total_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    notes TEXT,
    created_by INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_po_number (po_number),
    INDEX idx_order_date (order_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Purchase Order Items
CREATE TABLE purchase_order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    po_id INT NOT NULL,
    part_id INT NOT NULL,
    quantity_ordered INT NOT NULL,
    quantity_received INT NOT NULL DEFAULT 0,
    unit_cost DECIMAL(15, 2) NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (po_id) REFERENCES purchase_orders(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES parts(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_po_id (po_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Customers Table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    customer_type ENUM('INDIVIDUAL', 'CORPORATE') NOT NULL DEFAULT 'INDIVIDUAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer_code (customer_code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Vehicles Table
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    vin VARCHAR(50) UNIQUE,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(50),
    transmission ENUM('MANUAL', 'AUTOMATIC', 'CVT') NOT NULL DEFAULT 'AUTOMATIC',
    odometer INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_plate (plate_number),
    INDEX idx_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Service Transactions (Repair Work Orders & Billing)
CREATE TABLE service_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(50) NOT NULL UNIQUE,
    transaction_date DATETIME NOT NULL,
    vehicle_id INT NOT NULL,
    technician_id INT NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    odometer_in INT NOT NULL,
    customer_complaint TEXT,
    technician_diagnosis TEXT,
    parts_total DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    labor_total DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    grand_total DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    payment_method ENUM('CASH', 'TRANSFER', 'DEBIT_CARD', 'CREDIT_CARD', 'QRIS') NOT NULL DEFAULT 'CASH',
    payment_status ENUM('UNPAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
    created_by INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (technician_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_trx_code (transaction_code),
    INDEX idx_trx_date (transaction_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Service Parts Used (Parts Consumption)
CREATE TABLE service_parts_used (
    id INT AUTO_INCREMENT PRIMARY KEY,
    service_transaction_id INT NOT NULL,
    part_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (service_transaction_id) REFERENCES service_transactions(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES parts(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_service_trx (service_transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Service Labor Items (Service Fees)
CREATE TABLE service_labor_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    service_transaction_id INT NOT NULL,
    service_name VARCHAR(200) NOT NULL,
    hours DECIMAL(4, 1) NOT NULL DEFAULT 1.0,
    rate DECIMAL(15, 2) NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (service_transaction_id) REFERENCES service_transactions(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_service_labor_trx (service_transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Stock Adjustments (Physical Audits & Write-offs)
CREATE TABLE stock_adjustments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    adjustment_code VARCHAR(50) NOT NULL UNIQUE,
    adjustment_date DATE NOT NULL,
    part_id INT NOT NULL,
    adjustment_type ENUM('INCREASE', 'DECREASE') NOT NULL,
    quantity INT NOT NULL,
    reason ENUM('DAMAGED_PART', 'EXPIRED_FLUID', 'PHYSICAL_COUNT_VARIANCE', 'RETURN_FROM_CUSTOMER', 'SCRAP_OBSOLETE', 'OTHER') NOT NULL,
    notes TEXT,
    approved_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (part_id) REFERENCES parts(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_adj_code (adjustment_code),
    INDEX idx_adj_date (adjustment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
