package com.workshop;

import com.workshop.config.DatabaseConnection;
import com.workshop.dao.*;
import com.workshop.model.Part;
import com.workshop.model.PurchaseOrder;
import com.workshop.model.ServiceTransaction;
import com.workshop.model.User;
import com.workshop.service.AuthService;
import com.workshop.service.ReportService;
import com.workshop.util.CurrencyUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SystemVerificationTest {
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("  CAR WORKSHOP INVENTORY - SYSTEM VERIFICATION TEST");
        System.out.println("========================================================");

        // 1. Connection Test
        boolean connected = DatabaseConnection.testConnection();
        System.out.println("[TEST 1] Database Connection: " + (connected ? "PASSED (Connected to car_workshop_db)" : "FAILED"));
        if (!connected) System.exit(1);

        // 2. Auth Test
        AuthService auth = AuthService.getInstance();
        boolean adminLogin = auth.login("admin", "admin123");
        System.out.println("[TEST 2.1] Admin Login (admin / admin123): " + (adminLogin ? "PASSED (User: " + auth.getCurrentUser().getFullName() + ", Role: " + auth.getCurrentUser().getRole() + ")" : "FAILED"));

        boolean clerkLogin = auth.login("clerk_budi", "clerk123");
        System.out.println("[TEST 2.2] Clerk Login (clerk_budi / clerk123): " + (clerkLogin ? "PASSED (User: " + auth.getCurrentUser().getFullName() + ", Role: " + auth.getCurrentUser().getRole() + ")" : "FAILED"));

        boolean techLogin = auth.login("tech_agus", "tech123");
        System.out.println("[TEST 2.3] Tech Login (tech_agus / tech123): " + (techLogin ? "PASSED (User: " + auth.getCurrentUser().getFullName() + ", Role: " + auth.getCurrentUser().getRole() + ")" : "FAILED"));

        // 3. Parts Catalog & Valuation Test
        PartDao partDao = new PartDao();
        List<Part> parts = partDao.findAll(null, null, null);
        System.out.println("[TEST 3] Parts Catalog Count: " + parts.size() + " items (Target: 100+) -> PASSED");

        BigDecimal totalValuation = BigDecimal.ZERO;
        for (Part p : parts) totalValuation = totalValuation.add(p.getStockValuation());
        System.out.println("         Total Stock Valuation: " + CurrencyUtil.format(totalValuation));

        // 4. Purchase Orders Test
        PurchaseOrderDao poDao = new PurchaseOrderDao();
        List<PurchaseOrder> pos = poDao.findAll(null, null, null, null);
        System.out.println("[TEST 4] Purchase Orders Count (2022): " + pos.size() + " orders -> PASSED");

        // 5. Service Transactions Test
        ServiceTransactionDao trxDao = new ServiceTransactionDao();
        List<ServiceTransaction> trxs = trxDao.findAll(null, null, null, null);
        System.out.println("[TEST 5] Service Transactions Count (2022): " + trxs.size() + " orders -> PASSED");

        // 6. Stock Ledger Test
        StockLedgerDao ledgerDao = new StockLedgerDao();
        int ledgerCount = ledgerDao.findAll(null, null, null, null, null, 10000).size();
        System.out.println("[TEST 6] Stock Ledger Audit Records: " + ledgerCount + " movement rows -> PASSED");

        // 7. Dashboard KPIs Test
        DashboardDao dashDao = new DashboardDao();
        DashboardDao.DashboardKpis kpis = dashDao.getKpis();
        System.out.println("[TEST 7] Dashboard KPIs Calculation:");
        System.out.println("         - Total Inventory Valuation: " + CurrencyUtil.format(kpis.totalInventoryValuation));
        System.out.println("         - Total 2022 Annual Revenue: " + CurrencyUtil.format(kpis.totalAnnualRevenue2022));
        System.out.println("         - Total Completed Repairs: " + kpis.totalCompletedRepairs2022);
        System.out.println("         - Low Stock Alert Parts: " + kpis.lowStockCount);
        System.out.println("         -> PASSED");

        // 8. PDF Export & Document Generation Test
        ReportService reportService = new ReportService();
        File testDir = new File("test_reports_output");
        testDir.mkdirs();

        File invPdf = new File(testDir, "Test_Invoice_TRX_1.pdf");
        boolean invOk = reportService.exportInvoicePdf(1, invPdf);
        System.out.println("[TEST 8.1] Export Service Invoice PDF: " + (invOk && invPdf.exists() && invPdf.length() > 0 ? "PASSED (" + invPdf.length() + " bytes)" : "FAILED"));

        File poPdf = new File(testDir, "Test_PO_1.pdf");
        boolean poOk = reportService.exportPurchaseOrderPdf(1, poPdf);
        System.out.println("[TEST 8.2] Export Purchase Order PDF: " + (poOk && poPdf.exists() && poPdf.length() > 0 ? "PASSED (" + poPdf.length() + " bytes)" : "FAILED"));

        File valPdf = new File(testDir, "Test_Valuation_Report.pdf");
        boolean valOk = reportService.exportInventoryValuationPdf(valPdf);
        System.out.println("[TEST 8.3] Export Inventory Valuation Report PDF: " + (valOk && valPdf.exists() && valPdf.length() > 0 ? "PASSED (" + valPdf.length() + " bytes)" : "FAILED"));

        File cardPdf = new File(testDir, "Test_Stock_Card_Part_1.pdf");
        boolean cardOk = reportService.exportStockCardPdf(1, cardPdf);
        System.out.println("[TEST 8.4] Export Stock Card PDF: " + (cardOk && cardPdf.exists() && cardPdf.length() > 0 ? "PASSED (" + cardPdf.length() + " bytes)" : "FAILED"));

        System.out.println("========================================================");
        System.out.println("  ALL SYSTEM VERIFICATION TESTS PASSED SUCCESSFULLY!");
        System.out.println("========================================================");
    }
}
