package hotel.ui.staff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StaffDashboard extends JFrame {
    // ── Declare all panels here so buttons can access them ──
    static JPanel sidebarPanel;
    static JPanel mainPanel;

    // ── Colors matching AdminDashboard dark theme ──
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

    private String staffName = "Heaheang";

    public StaffDashboard() {
        setupFrame();
        createSidebar();
        createMainContent();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void setupFrame() {
        setTitle("Staff Dashboard - Hotel Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, FRAME_HEIGHT));
        sidebarPanel.setBackground(BG_DARK);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // HMS Header
        JLabel hmsLabel = new JLabel("HMS");
        hmsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        hmsLabel.setForeground(TEXT_WHITE);
        hmsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(Box.createVerticalStrut(30));
        sidebarPanel.add(hmsLabel);
        sidebarPanel.add(Box.createVerticalStrut(40));

        // Staff Label
        JLabel staffLabel = new JLabel("Staff");
        staffLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        staffLabel.setForeground(TEXT_GRAY);
        staffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        staffLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        sidebarPanel.add(staffLabel);

        // Menu Items
        addSidebarMenuItem("🏠 Dashboard", true);
        addSidebarMenuItem("📅 Bookings", false);
        addSidebarMenuItem("🔑 Check-in/Out", false);
        addSidebarMenuItem("🏨 Rooms", false);

        sidebarPanel.add(Box.createVerticalGlue());

        // User Info at Bottom
        JPanel userPanel = new JPanel();
        userPanel.setBackground(BG_DARK);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 80));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel userInitial = new JLabel("ST");
        userInitial.setFont(new Font("Arial", Font.BOLD, 16));
        userInitial.setForeground(TEXT_WHITE);
        userInitial.setBackground(ACCENT_RED);
        userInitial.setOpaque(true);
        userInitial.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(40, 40);
        userInitial.setPreferredSize(size);
        userInitial.setMaximumSize(size);
        userInitial.setMinimumSize(size);
        userPanel.add(userInitial);
        userPanel.add(Box.createVerticalStrut(8));

        JLabel userName = new JLabel("Staff");
        userName.setFont(new Font("Arial", Font.BOLD, 13));
        userName.setForeground(TEXT_WHITE);
        userName.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userName);

        JLabel userEmail = new JLabel("staff@gmail.com");
        userEmail.setFont(new Font("Arial", Font.PLAIN, 11));
        userEmail.setForeground(TEXT_GRAY);
        userEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userEmail);

        sidebarPanel.add(userPanel);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void addSidebarMenuItem(String text, boolean isActive) {
        JButton menuItem = new JButton(text);
        menuItem.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 45));
        menuItem.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 45));
        menuItem.setBackground(isActive ? ACCENT_RED : BG_DARK);
        menuItem.setForeground(TEXT_WHITE);
        menuItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        menuItem.setFont(new Font("Arial", Font.PLAIN, 14));
        menuItem.setFocusPainted(false);
        menuItem.setContentAreaFilled(true);
        menuItem.setHorizontalAlignment(SwingConstants.LEFT);

        menuItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!menuItem.getBackground().equals(ACCENT_RED)) {
                    menuItem.setBackground(BG_HOVER);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!menuItem.getBackground().equals(ACCENT_RED)) {
                    menuItem.setBackground(BG_DARK);
                }
            }
        });

        sidebarPanel.add(menuItem);
    }

    private void createMainContent() {
        mainPanel = new JPanel();
        mainPanel.setBackground(BG_DARK);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // ── Header ──────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setAlignmentX(0f);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel dashboardTitle = new JLabel("DASHBOARD");
        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 28));
        dashboardTitle.setForeground(ACCENT_RED);

        JLabel dateLabel = new JLabel(getCurrentDate());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_GRAY);

        headerPanel.add(dashboardTitle, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel);

        // ── Greeting ─────────────────────────────────────────────────
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setBackground(BG_CARD);
        greetingPanel.setAlignmentX(0f);
        greetingPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        greetingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JPanel greetingText = new JPanel();
        greetingText.setBackground(BG_CARD);
        greetingText.setLayout(new BoxLayout(greetingText, BoxLayout.Y_AXIS));

        JLabel greeting = new JLabel("Good morning, " + staffName);
        greeting.setFont(new Font("Arial", Font.BOLD, 24));
        greeting.setForeground(TEXT_WHITE);
        greeting.setAlignmentX(0f);

        JLabel greetingSubtitle = new JLabel("Here is your overview for today, " + getCurrentDateShort());
        greetingSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        greetingSubtitle.setForeground(TEXT_GRAY);
        greetingSubtitle.setAlignmentX(0f);
        greetingSubtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        greetingText.add(greeting);
        greetingText.add(greetingSubtitle);
        greetingPanel.add(greetingText, BorderLayout.WEST);
        mainPanel.add(greetingPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ── Stat Cards ───────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BG_DARK);
        statsPanel.setAlignmentX(0f);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        statsPanel.add(createStatCard("PENDING BOOKINGS", "1", new Color(120, 100, 180)));
        statsPanel.add(createStatCard("TODAY'S CHECK-INS", "4", new Color(80, 150, 200)));
        statsPanel.add(createStatCard("TODAY'S CHECK-OUTS", "4", new Color(200, 100, 100)));
        statsPanel.add(createStatCard("OCCUPIED ROOMS", "15", new Color(220, 180, 80)));
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // ── Table + Room Status ──────────────────────────────────────
        JPanel contentArea = new JPanel(new GridLayout(1, 2, 15, 0));
        contentArea.setBackground(BG_DARK);
        contentArea.setAlignmentX(0f);
        contentArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        contentArea.add(createSchedulePanel());
        contentArea.add(createRoomStatusPanel());
        mainPanel.add(contentArea);
        mainPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color borderColor) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, borderColor),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setForeground(TEXT_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(borderColor);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel titleLabel = new JLabel("Upcoming schedule");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 15));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "GUEST", "ROOM", "TIME", "STATUS" };
        Object[][] data = {
                { "Tony Start", "Suite 401", "3:00 PM", "Pending" },
                { "Chhi Laykorng", "Deluxe 302", "1:00 PM", "Approved" },
                { "Bruce Wang", "Standard 108", "5:00 PM", "Pending" },
                { "Octopus Prime", "Family 200", "4:50 PM", "Checked In" }
        };

        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setBackground(BG_CARD);
        table.setForeground(TEXT_WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setBackground(BG_DARK);
        table.getTableHeader().setForeground(TEXT_GRAY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BG_CARD);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel titleLabel = new JLabel("Room Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 15));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel statusGrid = new JPanel();
        statusGrid.setBackground(BG_CARD);
        statusGrid.setLayout(new GridLayout(2, 2, 15, 15));
        statusGrid.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        statusGrid.add(createRoomStatusBox("20", "Occupied", new Color(220, 180, 80)));
        statusGrid.add(createRoomStatusBox("20", "Available", new Color(100, 180, 100)));
        statusGrid.add(createRoomStatusBox("20", "Cleaning", new Color(220, 150, 100)));
        statusGrid.add(createRoomStatusBox("20", "Maintenance", new Color(220, 100, 100)));

        panel.add(statusGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoomStatusBox(String number, String status, Color bgColor) {
        JPanel box = new JPanel();
        box.setBackground(bgColor);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 32));
        numberLabel.setForeground(Color.BLACK);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        box.add(numberLabel);
        box.add(statusLabel);

        return box;
    }

    private String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
    }

    private String getCurrentDateShort() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("MMM d"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StaffDashboard dashboard = new StaffDashboard();
            dashboard.setVisible(true);
        });
    }
}
