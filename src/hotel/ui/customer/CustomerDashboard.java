package hotel.ui.customer;

import hotel.model.User;
import hotel.ui.staff.util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    public CustomerDashboard(User user) {
        setTitle("Customer Dashboard – Hotel Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.THEME_WHITE_BG);

        // Placeholder — replace with full customer UI
        JLabel label = new JLabel("Customer Dashboard — Coming Soon", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(UIConstants.THEME_NAVY);
        add(label, BorderLayout.CENTER);

        JLabel userLbl = new JLabel("Logged in as: " + user.getUsername() + " (customer)", SwingConstants.CENTER);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLbl.setForeground(new Color(120, 120, 120));
        add(userLbl, BorderLayout.SOUTH);
    }
}