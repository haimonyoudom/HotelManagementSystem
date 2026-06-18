package hotel.ui.admin;

import hotel.model.User;
import hotel.ui.common.HeaderPanel;
import hotel.ui.common.LoginFrame;
import hotel.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private final CardLayout cardLayout   = new CardLayout();
    private final JPanel     contentPanel = new JPanel(cardLayout);
    private final User       currentUser;

    public AdminDashboard(User user) {
        this.currentUser = user;
        UITheme.applyGlobalFont();

        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 640));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        // Use our custom admin sidebar
        AdminSidebarPanel sidebar = new AdminSidebarPanel();

        AdminHomePanel       homePanel      = new AdminHomePanel();
        ManageRoomsPanel     roomsPanel     = new ManageRoomsPanel();
        ManageCustomersPanel customersPanel = new ManageCustomersPanel();
        ManageStaffPanel     staffPanel     = new ManageStaffPanel();
        ManageBookingsPanel  bookingsPanel  = new ManageBookingsPanel();
        ManagePaymentsPanel  paymentsPanel  = new ManagePaymentsPanel();
        IncomeReportPanel    reportPanel    = new IncomeReportPanel();

        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(wrapPage("Overview",      homePanel),      "HOME");
        contentPanel.add(wrapPage("Rooms",         roomsPanel),     "ROOMS");
        contentPanel.add(wrapPage("Customers",     customersPanel), "CUSTOMERS");
        contentPanel.add(wrapPage("Staff",         staffPanel),     "STAFF");
        contentPanel.add(wrapPage("Bookings",      bookingsPanel),  "BOOKINGS");
        contentPanel.add(wrapPage("Transactions",  paymentsPanel),  "TRANSACTIONS");
        contentPanel.add(wrapPage("Income Report", reportPanel),    "REPORTS");

        sidebar.addSection("Summary");
        sidebar.addNavItem("HOME",      AdminSidebarPanel.IconType.OVERVIEW,      "Overview",      () -> { homePanel.reload();      showPage("HOME");      });
        sidebar.addNavItem("STAFF",     AdminSidebarPanel.IconType.STAFF,         "Staff",         () -> { staffPanel.reload();     showPage("STAFF");     });
        sidebar.addNavItem("CUSTOMERS", AdminSidebarPanel.IconType.CUSTOMERS,     "Customers",     () -> { customersPanel.reload(); showPage("CUSTOMERS"); });
        sidebar.addNavItem("ROOMS",     AdminSidebarPanel.IconType.ROOMS,         "Rooms",         () -> { roomsPanel.reload();     showPage("ROOMS");     });

        sidebar.addSection("Bookings");
        sidebar.addNavItem("BOOKINGS",  AdminSidebarPanel.IconType.BOOKINGS,      "Bookings",      () -> { bookingsPanel.reload();  showPage("BOOKINGS");  });

        sidebar.addSection("Finances");
        sidebar.addNavItem("REPORTS",      AdminSidebarPanel.IconType.INCOME,        "Income Report", () -> showPage("REPORTS"));
        sidebar.addNavItem("TRANSACTIONS", AdminSidebarPanel.IconType.TRANSACTIONS, "Transactions", () -> showPage("TRANSACTIONS"));

        sidebar.setUser(currentUser);
        sidebar.addGlue();
        sidebar.addLogout(this::logout);

        // Wrap sidebar with margin so border is visible
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setBackground(UITheme.BG);
        sidebarWrapper.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 0));
        sidebarWrapper.add(sidebar, BorderLayout.CENTER);

        getContentPane().add(sidebarWrapper, BorderLayout.WEST);
        getContentPane().add(contentPanel,   BorderLayout.CENTER);

        showPage("HOME");
    }

    private JPanel wrapPage(String title, JPanel page) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.add(new HeaderPanel(title, currentUser), BorderLayout.NORTH);
        wrapper.add(page, BorderLayout.CENTER);
        return wrapper;
    }

    private void showPage(String key) {
        cardLayout.show(contentPanel, key);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}