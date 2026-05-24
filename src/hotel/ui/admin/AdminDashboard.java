package hotel.ui.admin;

import hotel.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton[] navButton;

    private static final String DASHBOARD = "DASHBOARD";
    private static final String ROOMS = "ROOMS";
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String STAFF = "STAFF";
    private static final String REPORTS = "REPORTS";

    public AdminDashboard() {
        setTitle ("Hotel Management System - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.BG_DARK);

        contentPanel.add(new AdminDashboardPanel(), DASHBOARD);
        contentPanel.add(new ManageRoomsPanel(), ROOMS);
        // contentPanel.add(new ManageCustomerPanel(), CUSTOMERS);
        // contentPanel.add(new ManageStaffPanel(), STAFF);
        // contentPanel.add(new IncomeReportPanel(), REPORTS);

        add(contentPanel, BorderLayout.CENTER);
        showPanel(DASHBOARD, 0);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        logoPanel.setBackground(UIConstants.BG_SIDEBAR);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        logoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLabel = new JLabel("HMS Admin");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(UIConstants.ACCENT_RED);
        logoPanel.add(logoLabel);

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        String[][] navItems = {
            {"Dashboard", DASHBOARD},
            {"Rooms", ROOMS},
            {"Customers", CUSTOMERS},
            {"Staff", STAFF},
            {"Reports", REPORTS}
        };

        navButton = new JButton[navItems.length];

        for (int i = 0; i < navItems.length; i++) {
            final int index = i;
            String text = navItems[i][0];
            String panelName = navItems[i][1];

            JButton btn = createNavButton(text);
            navButton[i] = btn;
            btn.addActionListener(e -> showPanel(panelName, index));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        userPanel.setBackground(UIConstants.BG_SIDEBAR);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        userPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel ("👤 Admin");
        userLabel.setFont(UIConstants.FONT_SMALL);
        userLabel.setForeground(UIConstants.TEXT_SECONDARY);
        userPanel.add(userLabel);

        sidebar.add(userPanel);

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(UIConstants.TEXT_SECONDARY);
        btn.setBackground(UIConstants.BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getBackground().equals(UIConstants.BG_SIDEBAR)) {
                    btn.setBackground(UIConstants.BG_SIDEBAR_HOVER);
                    btn.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(UIConstants.BG_SIDEBAR_ACTIVE)) {
                    btn.setBackground(UIConstants.BG_SIDEBAR);
                    btn.setForeground(UIConstants.TEXT_SECONDARY);
                }
            }
        });

        return btn;
    }

    private void showPanel(String name, int activeIndex) {
        cardLayout.show(contentPanel, name);

        for (int i = 0; i < navButton.length; i++) {
            if (i == activeIndex) {
                navButton[i].setBackground(UIConstants.BG_SIDEBAR_ACTIVE);
                navButton[i].setForeground(UIConstants.TEXT_SECONDARY);
                navButton[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, UIConstants.ACCENT_RED),
                                       BorderFactory.createEmptyBorder(0, 17, 0, 0)));
            } else {
                navButton[i].setBackground(UIConstants.BG_SIDEBAR);
                navButton[i].setForeground(UIConstants.TEXT_SECONDARY);
                navButton[i].setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            }
        }
    }
}