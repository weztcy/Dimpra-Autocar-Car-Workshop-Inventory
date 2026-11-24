package com.workshop.ui.main;

import com.workshop.config.AppConfig;
import com.workshop.service.AuthService;
import com.workshop.ui.auth.LoginFrame;
import com.workshop.ui.dashboard.DashboardPanel;
import com.workshop.ui.inventory.InventoryPanel;
import com.workshop.ui.procurement.ProcurementPanel;
import com.workshop.ui.reports.ReportsPanel;
import com.workshop.ui.service.ServicePanel;
import com.workshop.ui.users.UserManagementPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlCards = new JPanel(cardLayout);

    private SidebarPanel sidebar;
    private HeaderPanel header;

    private DashboardPanel dashboardPanel;
    private InventoryPanel inventoryPanel;
    private ProcurementPanel procurementPanel;
    private ServicePanel servicePanel;
    private ReportsPanel reportsPanel;
    private UserManagementPanel userPanel;

    public MainFrame() {
        setTitle("DIMPRA AUTOCAR - Enterprise Workshop Management System [" + AppConfig.APP_VERSION + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        sidebar = new SidebarPanel(this);
        header = new HeaderPanel(this);

        add(sidebar, BorderLayout.WEST);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.add(header, BorderLayout.NORTH);

        // Cards
        dashboardPanel = new DashboardPanel();
        inventoryPanel = new InventoryPanel();
        procurementPanel = new ProcurementPanel();
        servicePanel = new ServicePanel();
        reportsPanel = new ReportsPanel();

        pnlCards.add(dashboardPanel, "DASHBOARD");
        pnlCards.add(inventoryPanel, "INVENTORY");
        pnlCards.add(procurementPanel, "PROCUREMENT");
        pnlCards.add(servicePanel, "SERVICE");
        pnlCards.add(reportsPanel, "REPORTS");

        if (AuthService.getInstance().isSupervisor()) {
            userPanel = new UserManagementPanel();
            pnlCards.add(userPanel, "USERS");
        }

        pnlCenter.add(pnlCards, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    public void showCard(String cardName) {
        cardLayout.show(pnlCards, cardName);

        if (sidebar != null) {
            sidebar.highlightActiveButton(cardName);
        }

        // Auto-refresh panel data on navigation
        switch (cardName) {
            case "DASHBOARD":
                dashboardPanel.refreshData();
                break;
            case "INVENTORY":
                inventoryPanel.loadData();
                break;
            case "PROCUREMENT":
                procurementPanel.loadData();
                break;
            case "SERVICE":
                servicePanel.loadData();
                break;
            case "USERS":
                if (userPanel != null) userPanel.loadData();
                break;
        }
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari sistem?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
