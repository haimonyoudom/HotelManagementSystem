package hotel.ui.staff;

import hotel.model.User;
import hotel.ui.common.HeaderPanel;
import hotel.ui.common.LoginFrame;
import hotel.ui.common.SidebarPanel;
import hotel.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class StaffDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final User currentUser;

    public StaffDashboard(User user) {
        this.currentUser = user;

        UITheme.applyGlobalFont();

        setTitle("Staff Dashboard - " + user.getUsername());
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

        RoomStatusPanel roomStatusPanel = new RoomStatusPanel();
        PendingBookingsPanel pendingBookingsPanel = new PendingBookingsPanel();
        CheckInOutPanel checkInOutPanel = new CheckInOutPanel();

        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(wrapPage("Room Status", roomStatusPanel), "ROOM_STATUS");
        contentPanel.add(wrapPage("Pending Bookings", pendingBookingsPanel), "PENDING_BOOKINGS");
        contentPanel.add(wrapPage("Check In / Check Out", checkInOutPanel), "CHECK_IN_OUT");

        sidebar.addSection("Staff");
        sidebar.addNavigationButton("ROOM_STATUS", "Room Status", () -> {
            roomStatusPanel.reload();
            showPage("ROOM_STATUS");
        });
        sidebar.addNavigationButton("PENDING_BOOKINGS", "Pending Bookings", () -> {
            pendingBookingsPanel.reload();
            showPage("PENDING_BOOKINGS");
        });
        sidebar.addNavigationButton("CHECK_IN_OUT", "Check In / Out", () -> {
            checkInOutPanel.reload();
            showPage("CHECK_IN_OUT");
        });

        sidebar.addBottomGlue();
        sidebar.addNavigationButton("LOGOUT", "Logout", this::logout);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPage("ROOM_STATUS");
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
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}