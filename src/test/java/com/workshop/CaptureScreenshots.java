package com.workshop;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.workshop.config.DatabaseConnection;
import com.workshop.dao.PartDao;
import com.workshop.dao.PurchaseOrderDao;
import com.workshop.model.Part;
import com.workshop.model.PurchaseOrder;
import com.workshop.service.AuthService;
import com.workshop.ui.auth.LoginFrame;
import com.workshop.ui.inventory.PartDialog;
import com.workshop.ui.inventory.StockAdjustmentDialog;
import com.workshop.ui.inventory.StockLedgerDialog;
import com.workshop.ui.main.MainFrame;
import com.workshop.ui.procurement.PurchaseOrderDialog;
import com.workshop.ui.procurement.ReceiveGoodsDialog;
import com.workshop.ui.procurement.SupplierDialog;
import com.workshop.ui.service.CustomerVehicleDialog;
import com.workshop.ui.service.WorkOrderDialog;
import com.workshop.ui.theme.ThemeManager;
import com.workshop.ui.users.UserDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureScreenshots {
    private static final int SCREEN_W = 1600;
    private static final int SCREEN_H = 900;

    private static final File DIR_ROOT = new File("screenshots");
    private static final File DIR_16_9_FULLWIDTH = new File("screenshots/16：9"); // Shows as "16:9" in Explorer
    private static final File DIR_16_9_SAFE = new File("screenshots/16-9");       // Safe ASCII path

    public static void main(String[] args) {
        DIR_ROOT.mkdirs();
        DIR_16_9_FULLWIDTH.mkdirs();
        DIR_16_9_SAFE.mkdirs();

        System.out.println("Starting full automated 16:9 screenshot generation (1600x900)...");

        ThemeManager.setup();
        DatabaseConnection.testConnection();
        AuthService.getInstance().login("admin", "admin123");

        try {
            // 1. Login Screen (16:9 Aspect Ratio)
            System.out.println("Generating 01_login_screen.png...");
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setUndecorated(true);
            renderFullScreen(loginFrame.getContentPane(), "01_login_screen.png");

            // Main Frame (Supervisor view)
            MainFrame mainFrame = new MainFrame();
            mainFrame.setUndecorated(true);
            mainFrame.setSize(SCREEN_W, SCREEN_H);
            mainFrame.getContentPane().setSize(SCREEN_W, SCREEN_H);
            mainFrame.getContentPane().setBounds(0, 0, SCREEN_W, SCREEN_H);
            mainFrame.addNotify();
            mainFrame.validate();
            flushEdt();

            // 2. Dashboard
            System.out.println("Generating 02_dashboard_2022.png...");
            mainFrame.showCard("DASHBOARD");
            renderFullScreen(mainFrame.getContentPane(), "02_dashboard_2022.png");

            // 3. Inventory Catalog Panel
            System.out.println("Generating 03_inventory_catalog.png...");
            mainFrame.showCard("INVENTORY");
            renderFullScreen(mainFrame.getContentPane(), "03_inventory_catalog.png");

            // 4. Stock Card / Ledger Dialog
            PartDao partDao = new PartDao();
            Part samplePart = partDao.findById(1);
            if (samplePart != null) {
                System.out.println("Generating 04_stock_card_ledger.png...");
                StockLedgerDialog ledgerDlg = new StockLedgerDialog(mainFrame, samplePart);
                renderModalOverlay(mainFrame, ledgerDlg.getContentPane(), 1020, 620, "04_stock_card_ledger.png");

                // 5. Stock Adjustment Dialog
                System.out.println("Generating 05_stock_adjustment_dialog.png...");
                StockAdjustmentDialog adjDlg = new StockAdjustmentDialog(mainFrame, samplePart);
                renderModalOverlay(mainFrame, adjDlg.getContentPane(), 520, 440, "05_stock_adjustment_dialog.png");

                // 5b. Edit Part Dialog
                System.out.println("Generating 05b_edit_part_dialog.png...");
                PartDialog partDlg = new PartDialog(mainFrame, samplePart);
                renderModalOverlay(mainFrame, partDlg.getContentPane(), 580, 580, "05b_edit_part_dialog.png");
            }

            // 6. Procurement Panel
            System.out.println("Generating 06_procurement_po_list.png...");
            mainFrame.showCard("PROCUREMENT");
            renderFullScreen(mainFrame.getContentPane(), "06_procurement_po_list.png");

            // 7. Purchase Order Dialog
            System.out.println("Generating 07_create_purchase_order_dialog.png...");
            PurchaseOrderDialog poDlg = new PurchaseOrderDialog(mainFrame);
            renderModalOverlay(mainFrame, poDlg.getContentPane(), 1000, 640, "07_create_purchase_order_dialog.png");

            // 8. Receive Goods Dialog
            PurchaseOrderDao poDao = new PurchaseOrderDao();
            PurchaseOrder samplePo = poDao.findById(1);
            if (samplePo != null) {
                System.out.println("Generating 08_receive_goods_dialog.png...");
                ReceiveGoodsDialog recDlg = new ReceiveGoodsDialog(mainFrame, samplePo);
                renderModalOverlay(mainFrame, recDlg.getContentPane(), 960, 580, "08_receive_goods_dialog.png");
            }

            // 8b. Supplier Dialog
            System.out.println("Generating 08b_supplier_dialog.png...");
            SupplierDialog supDlg = new SupplierDialog(mainFrame, null);
            renderModalOverlay(mainFrame, supDlg.getContentPane(), 540, 520, "08b_supplier_dialog.png");

            // 9. Service Repair Orders Panel
            System.out.println("Generating 09_service_orders_list.png...");
            mainFrame.showCard("SERVICE");
            renderFullScreen(mainFrame.getContentPane(), "09_service_orders_list.png");

            // 10. Work Order Dialog
            System.out.println("Generating 10_work_order_dialog.png...");
            WorkOrderDialog woDlg = new WorkOrderDialog(mainFrame);
            renderModalOverlay(mainFrame, woDlg.getContentPane(), 1040, 700, "10_work_order_dialog.png");

            // 11. Customer Vehicle Dialog
            System.out.println("Generating 11_customer_vehicle_dialog.png...");
            CustomerVehicleDialog custDlg = new CustomerVehicleDialog(mainFrame);
            renderModalOverlay(mainFrame, custDlg.getContentPane(), 640, 640, "11_customer_vehicle_dialog.png");

            // 12. Reports Panel - Valuation
            System.out.println("Generating 12_reports_valuation.png...");
            mainFrame.showCard("REPORTS");
            setReportSelection(mainFrame, 0);
            renderFullScreen(mainFrame.getContentPane(), "12_reports_valuation.png");

            // 13. Reports Panel - Consumption Audit
            System.out.println("Generating 13_reports_consumption_audit.png...");
            setReportSelection(mainFrame, 1);
            renderFullScreen(mainFrame.getContentPane(), "13_reports_consumption_audit.png");

            // 14. Reports Panel - Monthly Revenue
            System.out.println("Generating 14_reports_monthly_revenue.png...");
            setReportSelection(mainFrame, 3);
            renderFullScreen(mainFrame.getContentPane(), "14_reports_monthly_revenue.png");

            // 15. Reports Panel - Low Stock Reorder
            System.out.println("Generating 15_reports_low_stock_reorder.png...");
            setReportSelection(mainFrame, 4);
            renderFullScreen(mainFrame.getContentPane(), "15_reports_low_stock_reorder.png");

            // 16. User Management Panel (RBAC)
            System.out.println("Generating 16_user_management_rbac.png...");
            mainFrame.showCard("USERS");
            renderFullScreen(mainFrame.getContentPane(), "16_user_management_rbac.png");

            // 16b. User Dialog
            System.out.println("Generating 16b_user_dialog.png...");
            UserDialog userDlg = new UserDialog(mainFrame, null);
            renderModalOverlay(mainFrame, userDlg.getContentPane(), 520, 500, "16b_user_dialog.png");

            // 17. Dark Mode Dashboard (Rendered under FlatDarkLaf)
            System.out.println("Generating 17_dashboard_dark_mode.png...");
            FlatDarkLaf.setup();
            MainFrame darkFrame = new MainFrame();
            darkFrame.setUndecorated(true);
            darkFrame.setSize(SCREEN_W, SCREEN_H);
            darkFrame.getContentPane().setSize(SCREEN_W, SCREEN_H);
            darkFrame.getContentPane().setBounds(0, 0, SCREEN_W, SCREEN_H);
            darkFrame.addNotify();
            darkFrame.validate();
            darkFrame.showCard("DASHBOARD");
            renderFullScreen(darkFrame.getContentPane(), "17_dashboard_dark_mode.png");

            System.out.println("========================================================");
            System.out.println("All 16:9 screenshots successfully captured!");
            System.out.println("Saved to: " + DIR_16_9_FULLWIDTH.getAbsolutePath());
            System.out.println("Saved to: " + DIR_16_9_SAFE.getAbsolutePath());
            System.out.println("========================================================");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void flushEdt() {
        try {
            SwingUtilities.invokeAndWait(() -> {});
            Thread.sleep(300);
        } catch (Exception ignored) {}
    }

    private static void renderFullScreen(Container comp, String filename) throws Exception {
        flushEdt();
        comp.setSize(SCREEN_W, SCREEN_H);
        comp.setBounds(0, 0, SCREEN_W, SCREEN_H);
        comp.addNotify();
        comp.validate();
        comp.doLayout();
        layoutTree(comp);

        BufferedImage img = new BufferedImage(SCREEN_W, SCREEN_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(comp.getBackground() != null ? comp.getBackground() : new Color(248, 250, 252));
        g2.fillRect(0, 0, SCREEN_W, SCREEN_H);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        comp.printAll(g2);
        g2.dispose();

        saveToAllDirs(img, filename);
    }

    private static void renderModalOverlay(MainFrame mainFrame, Container dlgContent, int dlgW, int dlgH, String filename) throws Exception {
        flushEdt();
        mainFrame.getContentPane().setSize(SCREEN_W, SCREEN_H);
        mainFrame.getContentPane().setBounds(0, 0, SCREEN_W, SCREEN_H);
        mainFrame.validate();
        mainFrame.doLayout();
        layoutTree(mainFrame.getContentPane());

        BufferedImage img = new BufferedImage(SCREEN_W, SCREEN_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. Paint underlying mainFrame (16:9)
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        mainFrame.getContentPane().printAll(g);

        // 2. Soft modal backdrop dimming
        g.setColor(new Color(15, 23, 42, 130));
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // 3. Layout and prepare dialog content
        dlgContent.setSize(dlgW, dlgH);
        dlgContent.setBounds(0, 0, dlgW, dlgH);
        dlgContent.addNotify();
        dlgContent.validate();
        dlgContent.doLayout();
        layoutTree(dlgContent);

        int dx = (SCREEN_W - dlgW) / 2;
        int dy = (SCREEN_H - dlgH) / 2;

        // Realistic drop shadows
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(dx - 10, dy - 6, dlgW + 20, dlgH + 20, 20, 20);
        g.setColor(new Color(0, 0, 0, 35));
        g.fillRoundRect(dx - 18, dy - 12, dlgW + 36, dlgH + 36, 26, 26);

        // Draw dialog card container background
        g.setColor(dlgContent.getBackground() != null ? dlgContent.getBackground() : Color.WHITE);
        g.fillRoundRect(dx, dy, dlgW, dlgH, 14, 14);

        // Print dialog content onto canvas
        Graphics2D gDlg = (Graphics2D) g.create(dx, dy, dlgW, dlgH);
        dlgContent.printAll(gDlg);
        gDlg.dispose();

        // Subtle clean dialog border
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(dx, dy, dlgW - 1, dlgH - 1, 14, 14);

        g.dispose();

        saveToAllDirs(img, filename);
    }

    private static void saveToAllDirs(BufferedImage img, String filename) throws Exception {
        File[] targets = {
            new File(DIR_ROOT, filename),
            new File(DIR_16_9_FULLWIDTH, filename),
            new File(DIR_16_9_SAFE, filename)
        };
        for (File f : targets) {
            f.getParentFile().mkdirs();
            ImageIO.write(img, "png", f);
        }
        System.out.println("Saved " + filename + " (16:9 - 1600x900) across all target folders");
    }

    private static void layoutTree(Component c) {
        c.doLayout();
        c.validate();
        if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            sp.validate();
            if (sp.getColumnHeader() != null) {
                sp.getColumnHeader().doLayout();
                sp.getColumnHeader().validate();
                if (sp.getColumnHeader().getView() != null) {
                    sp.getColumnHeader().getView().doLayout();
                    sp.getColumnHeader().getView().validate();
                }
            }
            if (sp.getViewport() != null) {
                sp.getViewport().doLayout();
                sp.getViewport().validate();
                if (sp.getViewport().getView() != null) {
                    sp.getViewport().getView().doLayout();
                    sp.getViewport().getView().validate();
                }
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                layoutTree(child);
            }
        }
    }

    private static void setReportSelection(MainFrame mainFrame, int index) {
        JComboBox<?> cb = findReportComboBox(mainFrame.getContentPane());
        if (cb != null && index >= 0 && index < cb.getItemCount()) {
            cb.setSelectedIndex(index);
        }
    }

    private static JComboBox<?> findReportComboBox(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                if (cb.getItemCount() > 0 && cb.getItemAt(0).toString().contains("Valuasi")) {
                    return cb;
                }
            }
            if (c instanceof Container) {
                JComboBox<?> found = findReportComboBox((Container) c);
                if (found != null) return found;
            }
        }
        return null;
    }
}
