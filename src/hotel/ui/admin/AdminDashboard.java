package hotel.ui.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard {

    // ── Declare all panels here so buttons can access them ──
    static JPanel sidebarPanel;
    static ManageRoomsPanel roomsPanel;
    // static ManageCustomersPanel customersPanel;
    // static ManageStaffPanel staffPanel;
    // static IncomeReportPanel incomePanel;
    static JPanel currentContentPanel;

    // ── Colors matching your dark theme screenshot ──
    static final Color BG_DARK = new Color(18, 18, 18);
    static final Color BG_CARD = new Color(28, 28, 28);
    static final Color BG_HOVER = new Color(40, 40, 40);
    static final Color ACCENT_RED = new Color(200, 50, 50);
    static final Color TEXT_WHITE = new Color(240, 240, 240);
    static final Color TEXT_GRAY = new Color(150, 150, 150);
    static final Color BORDER_COLOR = new Color(50, 50, 50);

    // ── Frame dimensions ──
    static final int FRAME_WIDTH = 1400;
    static final int FRAME_HEIGHT = 900;
    static final int SIDEBAR_WIDTH = 220;
    static final int CONTENT_X = SIDEBAR_WIDTH;
    static final int CONTENT_WIDTH = FRAME_WIDTH - SIDEBAR_WIDTH;

    public static void main(String[] args) {

        // ── Create the Window ──────────────────────────────────────────
        JFrame frame = new JFrame("HMS - Hotel Management System");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BG_DARK);

        // ── Create Sidebar Panel ─────────────────────────────────────
        sidebarPanel = createSidebar();
        frame.add(sidebarPanel);

        // ── Create Content Panels (Screens) ───────────────────────────
        roomsPanel = new ManageRoomsPanel();
        roomsPanel.setBounds(CONTENT_X, 0, CONTENT_WIDTH, FRAME_HEIGHT);

        // customersPanel = new ManageCustomersPanel();
        // customersPanel.setBounds(CONTENT_X, 0, CONTENT_WIDTH, FRAME_HEIGHT);

        // staffPanel = new ManageStaffPanel();
        // staffPanel.setBounds(CONTENT_X, 0, CONTENT_WIDTH, FRAME_HEIGHT);

        // incomePanel = new IncomeReportPanel();
        // incomePanel.setBounds(CONTENT_X, 0, CONTENT_WIDTH, FRAME_HEIGHT);

        // ── Add all content panels to frame ───────────────────────────
        frame.add(roomsPanel);
        // frame.add(customersPanel);
        // frame.add(staffPanel);
        // frame.add(incomePanel);

        // ── Show only Rooms screen at the start ─────────────────────
        roomsPanel.setVisible(true);
        // customersPanel.setVisible(false);
        // staffPanel.setVisible(false);
        // incomePanel.setVisible(false);
        // currentContentPanel = roomsPanel;

        frame.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════════
    // SIDEBAR CREATION
    // ═══════════════════════════════════════════════════════════════
    static JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, SIDEBAR_WIDTH, FRAME_HEIGHT);
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // HMS Title
        JLabel hmsTitle = new JLabel("HMS");
        hmsTitle.setBounds(20, 20, 180, 30);
        hmsTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        hmsTitle.setForeground(TEXT_WHITE);
        panel.add(hmsTitle);

        // Subtitle
        JLabel hmsSub = new JLabel("Hotel Management System");
        hmsSub.setBounds(20, 50, 180, 20);
        hmsSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hmsSub.setForeground(TEXT_GRAY);
        panel.add(hmsSub);

        // Navigation Items
        String[] items = {"Dashboard", "Rooms", "Customers", "Staff", "Reports"};
        String[] icons = {"◈", "◈", "◈", "◈", "◈"};
        int yPos = 120;

        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            final int index = i;

            JPanel navItem = new JPanel();
            navItem.setLayout(null);
            navItem.setBounds(10, yPos, 200, 45);
            navItem.setBackground(index == 1 ? new Color(60, 30, 30) : BG_DARK);
            navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Left accent bar for selected
            if (index == 1) {
                JPanel accent = new JPanel();
                accent.setBounds(0, 0, 3, 45);
                accent.setBackground(ACCENT_RED);
                navItem.add(accent);
            }

            JLabel iconLabel = new JLabel(icons[i]);
            iconLabel.setBounds(15, 12, 25, 20);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            iconLabel.setForeground(index == 1 ? ACCENT_RED : TEXT_GRAY);
            navItem.add(iconLabel);

            JLabel textLabel = new JLabel(item);
            textLabel.setBounds(45, 12, 140, 20);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textLabel.setForeground(index == 1 ? TEXT_WHITE : TEXT_GRAY);
            navItem.add(textLabel);

            // Hover effects
            navItem.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (currentContentPanel != getPanelForItem(item)) {
                        navItem.setBackground(BG_HOVER);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (currentContentPanel != getPanelForItem(item)) {
                        navItem.setBackground(BG_DARK);
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    switchToPanel(item);
                }
            });

            panel.add(navItem);
            yPos += 55;
        }

        // Bottom section - Staff info
        JPanel staffSection = new JPanel();
        staffSection.setLayout(null);
        staffSection.setBounds(10, FRAME_HEIGHT - 120, 200, 100);
        staffSection.setBackground(BG_CARD);
        staffSection.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JLabel staffLabel = new JLabel("Staff");
        staffLabel.setBounds(15, 10, 100, 20);
        staffLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffLabel.setForeground(TEXT_GRAY);
        staffSection.add(staffLabel);

        // Staff avatar circle
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ACCENT_RED);
                g2d.fillOval(0, 0, 36, 36);
                g2d.setColor(TEXT_WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "S";
                int textX = (36 - fm.stringWidth(text)) / 2;
                int textY = ((36 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, textX, textY);
            }
        };
        avatar.setBounds(15, 40, 36, 36);
        avatar.setOpaque(false);
        staffSection.add(avatar);

        JLabel staffName = new JLabel("Admin User");
        staffName.setBounds(60, 42, 130, 18);
        staffName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffName.setForeground(TEXT_WHITE);
        staffSection.add(staffName);

        JLabel staffRole = new JLabel("Manager");
        staffRole.setBounds(60, 60, 130, 16);
        staffRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        staffRole.setForeground(TEXT_GRAY);
        staffSection.add(staffRole);

        panel.add(staffSection);

        return panel;
    }

    static JPanel getPanelForItem(String item) {
        switch(item) {
            case "Rooms": return roomsPanel;
            // case "Customers": return customersPanel;
            // case "Staff": return staffPanel;
            // case "Reports": return incomePanel;
            default: return roomsPanel;
        }
    }

    static void switchToPanel(String item) {
        JPanel target = getPanelForItem(item);
        if (target == currentContentPanel) return;

        currentContentPanel.setVisible(false);
        target.setVisible(true);
        currentContentPanel = target;
    }
}