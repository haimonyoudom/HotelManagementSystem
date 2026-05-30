package hotel.ui.admin;

import hotel.model.User;
import hotel.ui.common.HeaderPanel;
import hotel.ui.common.LoginFrame;
import hotel.ui.common.SidebarPanel;
import hotel.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final User currentUser;

    public AdminDashboard(User user) {
        this.currentUser = user;

        UITheme.applyGlobalFont();

        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        SidebarPanel sidebar = new SidebarPanel("HMS");

        AdminHomePanel homePanel = new AdminHomePanel();
        ManageRoomsPanel roomsPanel = new ManageRoomsPanel();
        ManageCustomersPanel customersPanel = new ManageCustomersPanel();
        ManageStaffPanel staffPanel = new ManageStaffPanel();
        ManageBookingsPanel bookingsPanel = new ManageBookingsPanel();
        ManagePaymentsPanel paymentsPanel = new ManagePaymentsPanel();
        IncomeReportPanel incomeReportPanel = new IncomeReportPanel();

        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(wrapPage("Summary", homePanel), "HOME");
        contentPanel.add(wrapPage("Rooms", roomsPanel), "ROOMS");
        contentPanel.add(wrapPage("Customers", customersPanel), "CUSTOMERS");
        contentPanel.add(wrapPage("Staff", staffPanel), "STAFF");
        contentPanel.add(wrapPage("Bookings", bookingsPanel), "BOOKINGS");
        contentPanel.add(wrapPage("Payments", paymentsPanel), "PAYMENTS");
        contentPanel.add(wrapPage("Income Report", incomeReportPanel), "REPORTS");

        sidebar.addSection("Summary");
        sidebar.addNavigationButton("HOME", "Overview", () -> {
            homePanel.reload();
            showPage("HOME");
        });

        sidebar.addSection("Management");
        sidebar.addNavigationButton("ROOMS", "Rooms", () -> {
            roomsPanel.reload();
            showPage("ROOMS");
        });
        sidebar.addNavigationButton("CUSTOMERS", "Customers", () -> {
            customersPanel.reload();
            showPage("CUSTOMERS");
        });
        sidebar.addNavigationButton("STAFF", "Staff", () -> {
            staffPanel.reload();
            showPage("STAFF");
        });
        sidebar.addNavigationButton("BOOKINGS", "Bookings", () -> {
            bookingsPanel.reload();
            showPage("BOOKINGS");
        });
        sidebar.addNavigationButton("PAYMENTS", "Payments", () -> {
            paymentsPanel.reload();
            showPage("PAYMENTS");
        });

        sidebar.addSection("Cashflow");
        sidebar.addNavigationButton("REPORTS", "Income Report", () -> showPage("REPORTS"));

        sidebar.addBottomGlue();
        sidebar.addNavigationButton("LOGOUT", "Logout", this::logout);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPage("HOME");
    }

    private JPanel wrapPage(String title, JPanel page) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);

        wrapper.add(new HeaderPanel(title, currentUser), BorderLayout.NORTH);
        wrapper.add(page, BorderLayout.CENTER);

        return wrapper;
    }

    private void showPage(String pageKey) {
        cardLayout.show(contentPanel, pageKey);
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}