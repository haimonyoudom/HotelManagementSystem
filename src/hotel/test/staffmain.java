package hotel.test;

import javax.swing.*;
import java.awt.*;
import hotel.ui.common.LoginFrame;
import hotel.ui.staff.StaffDashboard;

public class staffmain {
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        LoginFrame.screenWidth = screenSize.width;
        LoginFrame.screenHeight = screenSize.height;
        LoginFrame.scaleX = screenSize.width / 980.0;
        LoginFrame.scaleY = screenSize.height / 760.0;
        LoginFrame.leftPanelWidth = (int) (screenSize.width * 0.46);

        JFrame frame = new JFrame("Hotel Management System");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LoginFrame.loginPanel = new JPanel();
        LoginFrame.loginPanel.setLayout(null);
        LoginFrame.loginPanel.setBounds(0, 0, LoginFrame.screenWidth, LoginFrame.screenHeight);
        LoginFrame.loginPanel.setBackground(Color.WHITE);

        LoginFrame.signupPanel = new JPanel();
        LoginFrame.signupPanel.setLayout(null);
        LoginFrame.signupPanel.setBounds(0, 0, LoginFrame.screenWidth, LoginFrame.screenHeight);
        LoginFrame.signupPanel.setBackground(Color.WHITE);

        LoginFrame.buildLoginScreen();
        LoginFrame.buildSignupScreen();

        frame.add(LoginFrame.loginPanel);
        frame.add(LoginFrame.signupPanel);

        LoginFrame.loginPanel.setVisible(true);
        LoginFrame.signupPanel.setVisible(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            StaffDashboard dashboard = new StaffDashboard();
            dashboard.setVisible(true);
        });
    }
}