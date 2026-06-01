package hotel.ui.staff;

/*
 StaffDashboard.java
 -------------------
 Central shell for the staff-facing UI. This file creates the main
 application window (the `JFrame`) and hosts all top-level staff screens:
 dashboard, pending bookings, check-in/out, and room status.

 Light-mode implementation with consistent color palette across all screens.
*/

import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class StaffDashboard {

    // ── Declare ALL panels here ────────────────────────────────────────────
    public static JPanel dashboardPanel;
    public static JPanel pendingBookingsPanel;
    public static JPanel checkInOutPanel;
    public static JPanel roomStatusPanel;

    // ── Palette (Light Mode) ───────────────────────────────────────────────
    static final Color BG_MAIN       = new Color(250, 250, 250); // app root (light)
    static final Color BG_SIDEBAR    = new Color(245, 245, 245); // sidebar (slightly off-white)
    static final Color BG_TOPBAR     = new Color(245, 245, 245); // topbar
    static final Color BG_CARD       = new Color(250, 250, 250); // card background
    static final Color BG_ELEVATED   = new Color(235, 241, 255); // elevated banner (light blue)
    static final Color BG_CONTENT    = new Color(250, 250, 250); // content area
    static final Color BG_ROW        = new Color(240, 240, 245); // row background

    static final Color BLUE          = new Color(59, 130, 246);  // stat card 1 — blue
    static final Color BLUE_DIM      = new Color(225, 235, 255);  // stat card 1 bg (light)
    static final Color ORANGE        = new Color(249, 115, 22);  // stat card 2 — orange
    static final Color ORANGE_DIM    = new Color(255, 244, 230);  // stat card 2 bg (light)
    static final Color PURPLE        = new Color(167, 139, 250); // stat card 3 — purple
    static final Color PURPLE_DIM    = new Color(245, 240, 255);  // stat card 3 bg
    static final Color TEAL          = new Color(52, 211, 153);  // stat card 4 — teal
    static final Color TEAL_DIM      = new Color(235, 255, 245);  // stat card 4 bg

    static final Color NAV_ACTIVE_BG   = new Color(220, 230, 255); // active nav (pale blue)
    static final Color NAV_ACTIVE_TEXT = new Color(37, 99, 235);   // active nav text (blue)
    static final Color NAV_HOVER_BG    = new Color(245, 245, 245); // hover bg (slight)

    static final Color TXT_PRIMARY   = new Color(20, 20, 20);     // primary text (dark)
    static final Color TXT_SECONDARY = new Color(90, 90, 90);     // secondary text
    static final Color TXT_MUTED     = new Color(140, 140, 140);  // muted
    static final Color BORDER        = new Color(220, 220, 220);  // border color (light)
    static final Color BORDER_BLUE   = new Color(37, 99, 235);    // blue content border

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ── Fonts ──────────────────────────────────────────────────────────────
    public static final Font F_LARGE = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font F_MED   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_TINY  = new Font("Segoe UI", Font.PLAIN, 10);

    // ── Layout ─────────────────────────────────────────────────────────────
    public static final Dimension SCREEN    = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int W               = SCREEN.width;
    public static final int H               = SCREEN.height;
    public static final int SIDEBAR_W       = 180;
    public static final int TOPBAR_H        = 56;
    public static final int CONTENT_X       = SIDEBAR_W;
    public static final int CONTENT_Y       = TOPBAR_H;
    public static final int CONTENT_W       = W - SIDEBAR_W;
    public static final int CONTENT_H       = H - TOPBAR_H;

    // =========================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {

            // ── Create the Window ──────────────────────────────────────
            JFrame frame = new JFrame("HMS - Hotel Management System (Staff)");
            frame.setSize(W, H);
            frame.setLayout(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(BG_MAIN);

            // ── Create Content Panels ──────────────────────────────────
            dashboardPanel = new JPanel(null);
            pendingBookingsPanel = new JPanel(null);
            checkInOutPanel = new JPanel(null);
            roomStatusPanel = new JPanel(null);

            // ── Size all panels ────────────────────────────────────────
            for (JPanel p : new JPanel[]{dashboardPanel, pendingBookingsPanel, checkInOutPanel, roomStatusPanel}) {
                p.setBounds(0, 0, W, H);
                p.setBackground(BG_MAIN);
                p.setOpaque(true);
            }

            // ── Add all panels to the frame ────────────────────────────
            frame.add(dashboardPanel);
            frame.add(pendingBookingsPanel);
            frame.add(checkInOutPanel);
            frame.add(roomStatusPanel);

            // ── Call each panel builder ────────────────────────────────
            buildDashboardScreen();
            buildPendingBookingsPanel();
            buildCheckInOutPanel();
            buildRoomStatusPanel();

            // ── Show only Dashboard at the start ───────────────────────
            switchTo("dashboard");

            frame.setVisible(true);
        });
    }

    private static void buildDashboardScreen() {
        dashboardPanel.removeAll();
        addTopbar(dashboardPanel, "Dashboard", "Staff Overview");
        addSidebar(dashboardPanel, "dashboard");

        JLabel placeholder = new JLabel("Staff Dashboard - Coming Soon");
        placeholder.setFont(F_TITLE);
        placeholder.setForeground(TXT_PRIMARY);
        placeholder.setBounds(CONTENT_X + 20, CONTENT_Y + 20, 300, 40);
        dashboardPanel.add(placeholder);

        dashboardPanel.repaint();
    }

    private static void buildPendingBookingsPanel() {
        pendingBookingsPanel.removeAll();
        addTopbar(pendingBookingsPanel, "Pending Bookings", "Bookings");
        addSidebar(pendingBookingsPanel, "pending");

        JLabel placeholder = new JLabel("Pending Bookings - Coming Soon");
        placeholder.setFont(F_TITLE);
        placeholder.setForeground(TXT_PRIMARY);
        placeholder.setBounds(CONTENT_X + 20, CONTENT_Y + 20, 300, 40);
        pendingBookingsPanel.add(placeholder);

        pendingBookingsPanel.repaint();
    }

    private static void buildCheckInOutPanel() {
        checkInOutPanel.removeAll();
        addTopbar(checkInOutPanel, "Check In/Out", "Operations");
        addSidebar(checkInOutPanel, "checkin");

        JLabel placeholder = new JLabel("Check In/Out - Coming Soon");
        placeholder.setFont(F_TITLE);
        placeholder.setForeground(TXT_PRIMARY);
        placeholder.setBounds(CONTENT_X + 20, CONTENT_Y + 20, 300, 40);
        checkInOutPanel.add(placeholder);

        checkInOutPanel.repaint();
    }

    private static void buildRoomStatusPanel() {
        roomStatusPanel.removeAll();
        addTopbar(roomStatusPanel, "Room Status", "Rooms");
        addSidebar(roomStatusPanel, "status");

        JLabel placeholder = new JLabel("Room Status - Coming Soon");
        placeholder.setFont(F_TITLE);
        placeholder.setForeground(TXT_PRIMARY);
        placeholder.setBounds(CONTENT_X + 20, CONTENT_Y + 20, 300, 40);
        roomStatusPanel.add(placeholder);

        roomStatusPanel.repaint();
    }

    // ── Screen Switching ───────────────────────────────────────────────────
    public static void switchTo(String screen) {
        dashboardPanel.setVisible(false);
        pendingBookingsPanel.setVisible(false);
        checkInOutPanel.setVisible(false);
        roomStatusPanel.setVisible(false);

        switch (screen) {
            case "dashboard" -> dashboardPanel.setVisible(true);
            case "pending" -> pendingBookingsPanel.setVisible(true);
            case "checkin" -> checkInOutPanel.setVisible(true);
            case "status" -> roomStatusPanel.setVisible(true);
        }
    }

    // ── Helper: Add Topbar ─────────────────────────────────────────────────
    public static void addTopbar(JPanel parent, String title, String subtitle) {
        JPanel topbar = new JPanel(null);
        topbar.setBounds(SIDEBAR_W, 0, CONTENT_W, TOPBAR_H);
        topbar.setBackground(BG_TOPBAR);
        topbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(F_TITLE);
        titleLabel.setForeground(TXT_PRIMARY);
        titleLabel.setBounds(20, 8, 300, 20);
        topbar.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(F_SMALL);
        subtitleLabel.setForeground(TXT_SECONDARY);
        subtitleLabel.setBounds(20, 28, 300, 16);
        topbar.add(subtitleLabel);

        parent.add(topbar);
    }

    // ── Helper: Add Sidebar ────────────────────────────────────────────────
    public static void addSidebar(JPanel parent, String active) {
        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, TOPBAR_H, SIDEBAR_W, CONTENT_H);
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        String[] items = {"Dashboard", "Pending", "Check In/Out", "Room Status"};
        String[] screens = {"dashboard", "pending", "checkin", "status"};

        for (int i = 0; i < items.length; i++) {
            final String screen = screens[i];
            JButton btn = new JButton(items[i]);
            btn.setBounds(10, 20 + i * 50, SIDEBAR_W - 20, 40);
            btn.setFont(F_REG);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);

            if (screen.equals(active)) {
                btn.setBackground(NAV_ACTIVE_BG);
                btn.setForeground(NAV_ACTIVE_TEXT);
            } else {
                btn.setBackground(BG_SIDEBAR);
                btn.setForeground(TXT_PRIMARY);
            }

            btn.addActionListener(e -> switchTo(screen));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!screen.equals(active)) {
                        btn.setBackground(NAV_HOVER_BG);
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!screen.equals(active)) {
                        btn.setBackground(BG_SIDEBAR);
                    }
                }
            });

            sidebar.add(btn);
        }

        parent.add(sidebar);
    }
}
