package hotel.ui.admin;

import hotel.model.User;
import javax.swing.*;

public class AdminDashboardTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User(1, "admin", "", "ADMIN", "2026-06-01");
            AdminDashboard dashboard = new AdminDashboard(testUser);
            dashboard.setVisible(true);
        });
    }
}