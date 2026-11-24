# Portfolio Project Data — Dimpra Autocar - Car Workshop Inventory

- **Category:** `desktop-mobile` (`Desktop App`)
- **Category Label:** `Desktop App`
- **Accent Color:** `amber`
- **Target Assets Folder:** `public/images/projects/dimpra_autocar_car_workshop/`
- **Required Local Images:** `banner.webp`, `image01.webp` .. `image07.webp`

## TypeScript Data (`ProjectCaseStudy`)

```typescript
{
  "id": "dimpra-autocar-car-workshop-inventory",
  "code": "PRO-10 // APP-06",
  "title": "Dimpra Autocar - Car Workshop Inventory",
  "category": "desktop-mobile",
  "categoryLabel": "Desktop App",
  "year": "2022",
  "status": "Completed",
  "accentColor": "amber",
  "repoUrl": "https://github.com/weztcy/Dimpra-Autocar-Car-Workshop-Inventory",
  "license": "MIT License",
  "banner": "/images/projects/dimpra_autocar_car_workshop/banner.webp",
  "summary": "Standalone Java Swing desktop application for automotive spare parts inventory, supplier procurement, repair work orders, and financial reporting.",
  "projectDescription": "A Java Swing and FlatLaf desktop ERP for automotive repair workshops backed by MySQL and HikariCP. It unifies 106+ spare parts catalog control, stock ledger audits, supplier purchase orders, vehicle repair work orders (PKB), JFreeChart analytics, and OpenPDF/CSV reporting.",
  "architectureSummary": "Single-JVM Java Swing desktop client routing UI modules through transactional service layers and JDBC DAOs into a HikariCP-pooled MySQL relational database.",
  "techStack": [
    "Java",
    "Java Swing",
    "FlatLaf",
    "MySQL",
    "HikariCP",
    "JFreeChart",
    "OpenPDF",
    "SLF4J",
    "Apache Maven"
  ],
  "telemetryMatrix": {
    "projectType": "Standalone JVM Desktop Application",
    "targetPlatform": "Windows Desktop (JVM 17+)",
    "primaryDomain": "Automotive Workshop & Inventory ERP",
    "architecturePipeline": "Swing UI -> Services -> JDBC -> MySQL",
    "testingAndAudit": "Verified",
    "projectStatus": "Completed",
    "projectYear": "2022"
  },
  "desktopMobileProjectInfo": {
    "applicationType": "Standalone Desktop Workshop Management System",
    "targetPlatform": "Windows",
    "developmentStatus": "Completed",
    "developmentYear": "2022",
    "applicationCategory": "Automotive Inventory & Service ERP"
  },
  "desktopMobileBackground": "Automotive repair workshops frequently rely on disconnected spreadsheets and paper job cards, causing stock discrepancies, missed reorder thresholds, and slow invoice reconciliation. Dimpra Autocar centralizes parts catalogs, supplier procurement, and repair billing in a single transactional desktop application.",
  "desktopMobileProblemStatements": [
    {
      "title": "Fragmented Stock & Paper Job Card Tracking",
      "description": "Manual tracking of spare parts consumption across repair bays leads to unrecorded stock deductions, bin-location confusion, and inaccurate physical inventory valuation.",
      "issueCode": "ISSUE_01"
    },
    {
      "title": "Disconnected Supplier Procurement & Receiving",
      "description": "Without linked purchase order and goods receipt workflows, incoming vendor deliveries are not atomically reflected in inventory quantities or historical unit cost ledgers.",
      "issueCode": "ISSUE_02"
    },
    {
      "title": "Inconsistent Repair Billing & Audit Trails",
      "description": "Combining replacement parts, technician labor hours, discounts, and 11% VAT manually slows cashier checkout and leaves workshops without verifiable audit cards.",
      "issueCode": "ISSUE_03"
    }
  ],
  "desktopMobileObjectives": [
    {
      "title": "Enforce Atomic Stock Ledger Traceability",
      "description": "Record every procurement receipt, service part deduction, and manual stock adjustment in a relational stock_ledger table within database transactions.",
      "targetTag": "TARGET_01"
    },
    {
      "title": "Deliver Responsive Native Desktop Ergonomics",
      "description": "Provide a keyboard-friendly Swing interface with FlatLaf Light/Dark themes, fast table filtering, and JFreeChart operational KPI dashboards.",
      "targetTag": "TARGET_02"
    },
    {
      "title": "Automate Official PDF & CSV Document Output",
      "description": "Generate print-ready A4 service invoices, purchase orders, valuation audits, and stock cards directly on the client using OpenPDF without external services.",
      "targetTag": "TARGET_03"
    }
  ],
  "desktopMobileFeatures": [
    {
      "title": "Operations KPI & Chart Dashboard",
      "description": "Displays live inventory valuation, total revenue, completed work orders, low-stock reorder alerts, and JFreeChart monthly revenue and top-parts charts.",
      "badge": "Analytics"
    },
    {
      "title": "Spare Parts Catalog & Stock Ledger",
      "description": "Manages 106+ spare parts across 12 categories with bin locations, compatibility notes, safety-stock status badges, adjustments, and chronological stock cards.",
      "badge": "Inventory"
    },
    {
      "title": "Supplier Procurement & GRN Receiving",
      "description": "Creates multi-line purchase orders, tracks vendor payment and delivery statuses, and commits Goods Receipt Notes directly into parts inventory and ledger entries.",
      "badge": "Procurement"
    },
    {
      "title": "Vehicle Repair Work Orders & Billing",
      "description": "Registers customers and vehicles, links spare parts and labor hours to PKB tickets, computes 11% VAT totals, and settles multi-method payments.",
      "badge": "Service POS"
    },
    {
      "title": "OpenPDF Reports & CSV Export",
      "description": "Previews five structured audit reports and exports official A4 PDF invoices, purchase orders, valuation summaries, and stock cards alongside CSV tables.",
      "badge": "Reporting"
    },
    {
      "title": "Role-Based Access & Theme Switching",
      "description": "Separates supervisor, inventory clerk, and technician access via SHA-256 authentication while supporting runtime FlatLaf Light and Dark mode switching.",
      "badge": "Security & UI"
    }
  ],
  "desktopMobileInterfaceShowcase": [
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image01.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image02.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image03.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image04.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image05.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image06.webp"
    },
    {
      "imageUrl": "/images/projects/dimpra_autocar_car_workshop/image07.webp"
    }
  ],
  "desktopMobileUserFlow": {
    "summary": "Users sign in through the role-based login frame, navigate operational modules via the sidebar CardLayout shell, execute transactional inventory, procurement, or service work order workflows with real-time table feedback, and export verified PDF or CSV audit documents.",
    "steps": [
      {
        "step": "01",
        "title": "Authenticate & Resolve Role Scope",
        "description": "User submits credentials or demo role selection on LoginFrame; AuthService verifies the SHA-256 hash and opens MainFrame with role-gated sidebar navigation.",
        "actorOrNode": "LoginFrame -> AuthService"
      },
      {
        "step": "02",
        "title": "Inspect KPIs & Reorder Alerts",
        "description": "DashboardPanel queries DashboardDao on the Event Dispatch Thread to render valuation KPIs, JFreeChart bar charts, recent PKB orders, and low-stock parts.",
        "actorOrNode": "DashboardPanel -> DashboardDao"
      },
      {
        "step": "03",
        "title": "Procure & Receive Spare Parts",
        "description": "Inventory clerk issues a PurchaseOrder and confirms Goods Receipt; ProcurementService atomically increments part stock and writes PROCUREMENT_IN ledger rows.",
        "actorOrNode": "ProcurementPanel -> ProcurementService"
      },
      {
        "step": "04",
        "title": "Execute Service Work Order & Billing",
        "description": "Technician or advisor creates a PKB ticket with parts and labor; ServiceBillingService deducts stock, logs SERVICE_OUT entries, computes PPN 11%, and records payment.",
        "actorOrNode": "ServicePanel -> ServiceBillingService"
      },
      {
        "step": "05",
        "title": "Audit & Export Official Documents",
        "description": "Supervisor previews valuation, consumption, ledger, revenue, or low-stock reports and exports formatted A4 PDFs via OpenPDF or UTF-8 CSV spreadsheets.",
        "actorOrNode": "ReportsPanel -> ReportService"
      }
    ]
  },
  "desktopMobileArchitecture": {
    "summary": "Dimpra Autocar implements a four-layer Swing desktop architecture: FlatLaf-styled UI panels and modal dialogs communicate with transactional business services, which coordinate JDBC Data Access Objects over a HikariCP connection pool connected to a 13-table MySQL schema.",
    "layers": [
      {
        "name": "Presentation & Theme Layer",
        "role": "Renders CardLayout navigation, KPI charts, searchable tables, modal dialogs, and runtime Light/Dark theme switching on the Swing EDT.",
        "tech": "Java Swing & FlatLaf",
        "components": ["MainFrame", "LoginFrame", "DashboardPanel", "InventoryPanel", "ProcurementPanel", "ServicePanel", "ReportsPanel", "ThemeManager"]
      },
      {
        "name": "Business & Reporting Services",
        "role": "Enforces RBAC checks, coordinates multi-table JDBC transactions for stock/PO/service mutations, and generates OpenPDF and CSV documents.",
        "tech": "Java Services & OpenPDF",
        "components": ["AuthService", "InventoryService", "ProcurementService", "ServiceBillingService", "ReportService"]
      },
      {
        "name": "Data Access & Domain Models",
        "role": "Maps relational rows into domain POJOs and executes parameterized prepared statements for catalog, procurement, service, and ledger queries.",
        "tech": "JDBC DAO Pattern",
        "components": ["PartDao", "PurchaseOrderDao", "ServiceTransactionDao", "StockLedgerDao", "DashboardDao", "UserDao"]
      },
      {
        "name": "Connection Pool & Persistence",
        "role": "Loads externalized database.properties settings, manages HikariCP connection pooling, and persists 13 normalized InnoDB tables.",
        "tech": "HikariCP & MySQL",
        "components": ["AppConfig", "DatabaseConnection", "car_workshop_db"]
      }
    ]
  },
  "desktopMobileDataManagement": {
    "storageType": "MySQL 8.x Relational Database (13 InnoDB Tables via HikariCP Pool)",
    "stateEngine": "AuthService Singleton Session & Swing CardLayout Panel Refresh",
    "offlineSyncStrategy": "Local On-Premise MySQL Server on Port 3306 with Transaction Rollback Safety",
    "cachingMechanism": "HikariCP Prepared Statement Cache (250 Statements) & In-Memory Table Lists"
  },
  "desktopMobilePlatformImplementation": [
    {
      "title": "Swing Event Dispatch Thread & Theme Management",
      "description": "Launches UI frames via SwingUtilities.invokeLater and dynamically swaps FlatLightLaf and FlatDarkLaf look-and-feels across the live component tree.",
      "badge": "Desktop UI"
    },
    {
      "title": "JDBC Transaction & HikariCP Pooling",
      "description": "Manages CarWorkshopPool with max 10 connections, prepared-statement caching, and explicit commit/rollback boundaries for stock and billing integrity.",
      "badge": "Database"
    },
    {
      "title": "Standalone Fat JAR & Windows Batch Launchers",
      "description": "Bundles all dependencies via maven-assembly-plugin into a 25.45 MB executable JAR launched via run.bat and start-mysql-and-app.bat for Laragon MySQL.",
      "badge": "Packaging"
    }
  ],
  "desktopMobileTechSpecs": {
    "programmingLanguage": "Java",
    "framework": "Java Swing & FlatLaf",
    "localStorage": "MySQL Relational Database"
  },
  "desktopMobilePlatformSpecs": {
    "targetPlatform": "Windows Desktop",
    "packageFormat": "Executable Fat JAR",
    "targetArchitecture": "JVM x64 Architecture",
    "buildFootprint": "25.45 MB JAR"
  }
}
```
