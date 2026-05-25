package hotel.test;

import javax.swing.*;
import java.awt.*;
import hotel.ui.common.LoginFrame;

public class staffmain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hotel Management System");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new CardLayout());

            LoginFrame.loginPanel = new JPanel();
            LoginFrame.signupPanel = new JPanel();

            LoginFrame.buildLoginScreen();
            LoginFrame.buildSignupScreen();

            frame.add(LoginFrame.loginPanel, "login");
            frame.add(LoginFrame.signupPanel, "signup");

            LoginFrame.loginPanel.setVisible(true);
            LoginFrame.signupPanel.setVisible(false);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}