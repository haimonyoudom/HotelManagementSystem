package hotel.ui.customer;

import hotel.model.User;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class CustomerDashboard extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────
    public static final Color BG_MAIN      = new Color(250, 250, 250);
    public static final Color BG_SIDEBAR   = new Color(255, 255, 255);
    public static final Color BG_TOPBAR    = new Color(255, 255, 255);
    public static final Color BG_CARD      = new Color(250, 250, 250);
    public static final Color BG_ELEVATED  = new Color(235, 241, 255);
    public static final Color BG_CONTENT   = new Color(250, 250, 250);

    public static final Color BLUE         = new Color(59,  130, 246);
    public static final Color BLUE_DIM     = new Color(225, 235, 255);
    public static final Color ORANGE       = new Color(249, 115,  22);
    public static final Color ORANGE_DIM   = new Color(255, 244, 230);
    public static final Color PURPLE       = new Color(167, 139, 250);
    public static final Color PURPLE_DIM   = new Color(245, 240, 255);
    public static final Color TEAL         = new Color(52,  211, 153);
    public static final Color TEAL_DIM     = new Color(235, 255, 245);
    public static final Color NAVY         = new Color(30,   58, 110);

    public static final Color NAV_ACTIVE_BG   = new Color(230, 240, 255);
    public static final Color NAV_ACTIVE_TEXT  = NAVY;
    public static final Color NAV_HOVER_BG    = new Color(240, 240, 240);

    public static final Color TXT_PRIMARY  = new Color(20,  20,  20);
    public static final Color TXT_SECONDARY= new Color(90,  90,  90);
    public static final Color TXT_MUTED    = new Color(140, 140, 140);
    public static final Color BORDER       = new Color(220, 220, 220);
    public static final Color BORDER_BLUE  = new Color(37,   99, 235);

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ── Fonts ──────────────────────────────────────────────────────────
    public static final Font F_LARGE = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font F_MED   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_TINY  = new Font("Segoe UI", Font.PLAIN, 10);

    // ── Sidebar width constant (topbar height is now driven by preferred size) ─
    public static final int SIDEBAR_W = 200;

    // ── Card names (used by CardLayout) ───────────────────────────────
    private static final String CARD_DASHBOARD = "dashboard";
    private static final String CARD_ROOMS     = "rooms";
    private static final String CARD_BOOKING   = "booking";
    private static final String CARD_PAYMENT   = "payment";
    private static final String CARD_HISTORY   = "history";

    // ── Shared state ──────────────────────────────────────────────────
    private static CardLayout  cardLayout;
    private static JPanel      cardPanel;
    private static JButton     activeSidebarBtn = null;

    // ── Public panel references (kept for child panels that need them) ─
    public static JPanel           dashboardPanel;
    public static BrowseRoomsPanel roomsPanel;
    public static JPanel           bookingPanel;
    public static JPanel           paymentPanel;
    public static JPanel           historyPanel;

    // =========================================================================
    public CustomerDashboard() {
        this(null);
    }

    public CustomerDashboard(User currentUser) {
        CustomerData.setCurrentUser(currentUser);
        initializeFrame();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new CustomerDashboard().setVisible(true));
    }

    private void initializeFrame() {
        setTitle("HMS - Hotel Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        JPanel sidebar = buildSidebar(this);
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_MAIN);
        add(cardPanel, BorderLayout.CENTER);

        dashboardPanel = buildDashboardCard();
        roomsPanel     = new BrowseRoomsPanel();
        bookingPanel   = new JPanel(new BorderLayout());
        paymentPanel   = new JPanel(new BorderLayout());
        historyPanel   = new JPanel(new BorderLayout());

        cardPanel.add(dashboardPanel, CARD_DASHBOARD);
        cardPanel.add(roomsPanel,     CARD_ROOMS);
        cardPanel.add(bookingPanel,   CARD_BOOKING);
        cardPanel.add(paymentPanel,   CARD_PAYMENT);
        cardPanel.add(historyPanel,   CARD_HISTORY);

        BookingPanel.build(bookingPanel);
        PaymentPanel.build(paymentPanel);
        BookingHistoryPanel.build(historyPanel);

        switchTo(CARD_DASHBOARD);
    }

    // =========================================================================
    // SIDEBAR  (matches StaffDashboard.createSidebar exactly in structure)
    // =========================================================================
    private static JPanel buildSidebar(JFrame frame) {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setMinimumSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        // ── Brand ──────────────────────────────────────────────────────
        sidebar.add(Box.createVerticalStrut(24));

        JLabel hmsLabel = new JLabel("HMS");
        hmsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hmsLabel.setForeground(NAVY);
        hmsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hmsLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        sidebar.add(hmsLabel);

        sidebar.add(Box.createVerticalStrut(22));

        // ── Section label ──────────────────────────────────────────────
        JLabel sectionLabel = new JLabel("Customer");
        sectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sectionLabel.setForeground(TXT_MUTED);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 8, 0));
        sidebar.add(sectionLabel);

        // ── Nav items ──────────────────────────────────────────────────
        String[] keys   = { CARD_DASHBOARD, CARD_ROOMS,  CARD_HISTORY };
        String[] labels = { "Dashboard",    "Rooms",     "Booking History" };

        for (int i = 0; i < keys.length; i++) {
            final String key   = keys[i];
            boolean isFirst    = (i == 0);
            JButton btn = buildNavButton(labels[i], key, isFirst);
            btn.addActionListener(e -> {
                switchTo(key);
                if (activeSidebarBtn != null) deactivateBtn(activeSidebarBtn);
                markActiveBtn(btn);
                activeSidebarBtn = btn;
            });
            sidebar.add(btn);
            if (isFirst) {
                activeSidebarBtn = btn;
            }
        }

        // ── Push footer down ───────────────────────────────────────────
        sidebar.add(Box.createVerticalGlue());

        // ── User chip ──────────────────────────────────────────────────
        sidebar.add(buildUserChip());

        // ── Logout button ──────────────────────────────────────────────
        JButton logoutBtn = new JButton("  Logout", createLogoutIcon(new Color(180, 60, 60)));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setForeground(new Color(180, 60, 60));
        logoutBtn.setBackground(BG_SIDEBAR);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setIconTextGap(10);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { logoutBtn.setBackground(new Color(255, 240, 240)); }
            @Override public void mouseExited (MouseEvent e) { logoutBtn.setBackground(BG_SIDEBAR); }
        });
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            // SwingUtilities.invokeLater(() -> new hotel.ui.common.LoginFrame().setVisible(true));
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    // ── Nav button (mirrors StaffDashboard.buildNavButton) ────────────
    private static JButton buildNavButton(String label, String iconKey, boolean active) {
        final boolean[] isActive = { active };

        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BorderLayout());
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setMinimumSize(new Dimension(0, 46));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBackground(active ? NAV_ACTIVE_BG : BG_SIDEBAR);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Inner FlowLayout panel (icon + text)
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        // Drawn icon box
        JLabel iconBox = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color ic = isActive[0] ? NAVY : new Color(110, 118, 132);
                g2.setColor(ic);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int x = 6, y = 6;
                switch (iconKey) {
                    case CARD_DASHBOARD:
                        int cell = 6, gap = 3;
                        for (int row = 0; row < 2; row++)
                            for (int col = 0; col < 2; col++)
                                g2.fillRoundRect(x + col*(cell+gap), y + row*(cell+gap), cell, cell, 3, 3);
                        break;
                    case CARD_ROOMS:
                        g2.drawRoundRect(x+1, y+1, 14, 14, 4, 4);
                        g2.fillRect(x+10, y+6, 3, 3);
                        g2.fillRect(x+5,  y+10, 3, 3);
                        break;
                    case CARD_HISTORY:
                        g2.drawOval(x+1, y+1, 14, 14);
                        g2.drawLine(x+8, y+4, x+8,  y+9);
                        g2.drawLine(x+8, y+8, x+12, y+8);
                        break;
                    default:
                        g2.fillOval(x+3, y+3, 10, 10);
                }
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        Dimension iconSize = new Dimension(32, 32);
        iconBox.setPreferredSize(iconSize);
        iconBox.setMinimumSize(iconSize);
        iconBox.setMaximumSize(iconSize);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(active ? F_MED : F_REG);
        textLabel.setForeground(active ? NAVY : new Color(80, 80, 80));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        inner.add(iconBox);
        inner.add(textLabel);
        btn.add(inner, BorderLayout.CENTER);

        btn.putClientProperty("iconBox",   iconBox);
        btn.putClientProperty("textLabel", textLabel);
        btn.putClientProperty("isActive",  isActive);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeSidebarBtn) { btn.setBackground(NAV_HOVER_BG); btn.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeSidebarBtn) { btn.setBackground(BG_SIDEBAR);   btn.repaint(); }
            }
        });

        return btn;
    }

    private static void markActiveBtn(JButton btn) {
        btn.setBackground(NAV_ACTIVE_BG);
        boolean[] ia = (boolean[]) btn.getClientProperty("isActive");
        if (ia != null) ia[0] = true;
        JLabel tl = (JLabel) btn.getClientProperty("textLabel");
        if (tl != null) { tl.setForeground(NAVY); tl.setFont(F_MED); }
        btn.repaint();
    }

    private static void deactivateBtn(JButton btn) {
        btn.setBackground(BG_SIDEBAR);
        boolean[] ia = (boolean[]) btn.getClientProperty("isActive");
        if (ia != null) ia[0] = false;
        JLabel tl = (JLabel) btn.getClientProperty("textLabel");
        if (tl != null) { tl.setForeground(new Color(80, 80, 80)); tl.setFont(F_REG); }
        btn.repaint();
    }

    // =========================================================================
    // TOPBAR  (returned as a panel, added to card NORTH — matches StaffDashboard)
    // =========================================================================
    public static JPanel buildTopbar(String pageTitle) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_TOPBAR);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BORDER);
                g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 68));
        bar.setMinimumSize(new Dimension(0, 68));
        bar.setBorder(BorderFactory.createEmptyBorder(18, 30, 10, 30));

        JLabel title = new JLabel(pageTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(NAVY);
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JLabel dateLbl = new JLabel(LocalDate.now().format(DATE_FMT));
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLbl.setForeground(TXT_SECONDARY);

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        right.add(dateLbl);
        right.add(bell);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // =========================================================================
    // SCREEN 1 — DASHBOARD CARD
    // Each card is BorderLayout: NORTH=topbar, CENTER=content
    // =========================================================================
    private static JPanel buildDashboardCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_MAIN);

        card.add(buildTopbar("DASHBOARD"), BorderLayout.NORTH);

        // Content wrapper with padding (matches StaffDashboard contentWrapper)
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 16));
        contentWrapper.setBackground(BG_MAIN);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        card.add(contentWrapper, BorderLayout.CENTER);

        // Greeting card (like StaffDashboard.buildGreetingCard)
        contentWrapper.add(buildGreetingCard(), BorderLayout.NORTH);

        // Inner body: stat cards on top, bottom split below
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG_MAIN);
        body.add(buildStatCardsRow(), BorderLayout.NORTH);
        body.add(buildBottomRow(),    BorderLayout.CENTER);
        contentWrapper.add(body, BorderLayout.CENTER);

        return card;
    }

    // ── Greeting card ─────────────────────────────────────────────────
    private static JPanel buildGreetingCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 90));
        card.setMinimumSize(new Dimension(0, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240), 1),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        String nameText = "Guest";
        User user = CustomerData.getCurrentUser();
        if (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            nameText = user.getUsername().trim();
        }
        JLabel greet = new JLabel("Good morning, " + nameText);
        greet.setFont(F_LARGE);
        greet.setForeground(TXT_PRIMARY);

        JLabel sub = new JLabel("Here is your booking overview for today, " + LocalDate.now().format(DATE_FMT));
        sub.setFont(F_SMALL);
        sub.setForeground(TXT_SECONDARY);
        sub.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        text.add(greet);
        text.add(sub);
        card.add(text, BorderLayout.WEST);
        return card;
    }

    // ── Stat cards row (GridLayout 1x4 — same as StaffDashboard) ──────
    private static JPanel buildStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(BG_MAIN);
        row.setPreferredSize(new Dimension(0, 90));
        row.setMinimumSize(new Dimension(0, 80));

        CustomerData.DashboardStats stats = loadDashboardStats();
        row.add(makeStatCard(String.valueOf(stats.totalBookings), "Total Bookings", BLUE, BLUE_DIM));
        row.add(makeStatCard(CustomerData.money(stats.totalSpent), "Total Spent", ORANGE, ORANGE_DIM));
        row.add(makeStatCard(String.valueOf(stats.pendingApproval), "Pending Approval", PURPLE, PURPLE_DIM));
        row.add(makeStatCard(String.valueOf(stats.checkedInNow), "Checked In Now", TEAL, TEAL_DIM));
        return row;
    }

    private static JPanel makeStatCard(String value, String label, Color accent, Color bg) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel stack = new JPanel(new GridLayout(2, 1, 0, 4));
        stack.setOpaque(false);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLbl.setForeground(accent);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(F_TINY);
        lblLbl.setForeground(TXT_SECONDARY);

        stack.add(valLbl);
        stack.add(lblLbl);
        card.add(stack, BorderLayout.CENTER);
        return card;
    }

    // ── Bottom row: recent bookings (left) + chart+actions (right) ────
    private static JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(BG_MAIN);

        row.add(buildRecentBookingsPanel());
        row.add(buildRightColumn());
        return row;
    }

    private static JPanel buildRecentBookingsPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));

        JLabel title = new JLabel("Recent Bookings");
        title.setFont(F_MED);
        title.setForeground(TXT_SECONDARY);
        title.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        card.add(title, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setBackground(BG_CARD);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));

        List<CustomerData.BookingRow> data = loadDashboardStats().recentBookings;
        if (data.isEmpty()) {
            JLabel empty = new JLabel("No bookings found in database");
            empty.setFont(F_SMALL);
            empty.setForeground(TXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(12, 8, 0, 8));
            rows.add(empty);
        }
        for (int i = 0; i < Math.min(4, data.size()); i++) {
            CustomerData.BookingRow booking = data.get(i);
            String status = CustomerData.normalizeStatus(booking.booking.getStatus());
            String name = CustomerData.roomTitle(booking.room) + " - "
                    + (booking.room != null ? "Rm " + booking.room.getRoomNumber() : "Room unavailable");
            String dates = CustomerData.formatDate(booking.booking.getCheckInDate()) + " - "
                    + CustomerData.formatDate(booking.booking.getCheckOutDate());
            Color[] colors = statusColors(status);
            rows.add(makeBookingRow(name, dates, status, colors[0], colors[1]));
            rows.add(Box.createVerticalStrut(6));
        }

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private static JPanel makeBookingRow(String name, String dates, String status, Color bgBadge, Color fgBadge) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel n = new JLabel(name);
        n.setFont(F_MED);
        n.setForeground(TXT_PRIMARY);

        JLabel d = new JLabel(dates);
        d.setFont(F_TINY);
        d.setForeground(TXT_SECONDARY);

        left.add(n);
        left.add(d);
        row.add(left, BorderLayout.CENTER);

        JLabel badge = new JLabel(status, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgBadge);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(F_TINY);
        badge.setForeground(fgBadge);
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(96, 22));
        row.add(badge, BorderLayout.EAST);

        return row;
    }

    // ── Right column: chart + quick actions ───────────────────────────
    private static JPanel buildRightColumn() {
        JPanel col = new JPanel(new BorderLayout(0, 12));
        col.setBackground(BG_MAIN);

        // Monthly spending chart
        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(BG_CARD);
        chartCard.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 240), 1));

        JLabel chartTitle = new JLabel("Monthly Spending");
        chartTitle.setFont(F_MED);
        chartTitle.setForeground(TXT_SECONDARY);
        chartTitle.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(buildBarChart(), BorderLayout.CENTER);

        // Quick actions
        JPanel actionCard = new JPanel(new BorderLayout());
        actionCard.setBackground(BG_CARD);
        actionCard.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        actionCard.setPreferredSize(new Dimension(0, 100));
        actionCard.setMinimumSize(new Dimension(0, 90));

        JLabel actionTitle = new JLabel("Quick Actions");
        actionTitle.setFont(F_MED);
        actionTitle.setForeground(TXT_SECONDARY);
        actionTitle.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        actionCard.add(actionTitle, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 16, 14, 16));

        JButton btnBrowse = makeActionBtn("Browse Rooms", TXT_PRIMARY, ORANGE_DIM, ORANGE);
        JButton btnBook   = makeActionBtn("New Booking",  TXT_PRIMARY, BLUE_DIM,   BLUE);
        btnBrowse.addActionListener(e -> switchTo(CARD_ROOMS));
        btnBook.addActionListener(e   -> switchTo(CARD_ROOMS));

        btnRow.add(btnBrowse);
        btnRow.add(btnBook);
        actionCard.add(btnRow, BorderLayout.CENTER);

        col.add(chartCard,  BorderLayout.CENTER);
        col.add(actionCard, BorderLayout.SOUTH);
        return col;
    }

    // =========================================================================
    // SHARED — switchTo  (CardLayout swap instead of setVisible)
    // =========================================================================
    public static void switchTo(String page) {
        cardLayout.show(cardPanel, page);
    }

    public static void refreshCustomerScreens() {
        BookingHistoryPanel.refresh();
        if (cardPanel != null && dashboardPanel != null) {
            cardPanel.remove(dashboardPanel);
            dashboardPanel = buildDashboardCard();
            cardPanel.add(dashboardPanel, CARD_DASHBOARD);
            cardPanel.revalidate();
            cardPanel.repaint();
        }
    }

    // =========================================================================
    // HELPERS — user chip, logout icon, bar chart, action button
    // =========================================================================
    private static JPanel buildUserChip() {
        JPanel chip = new JPanel();
        chip.setBackground(new Color(245, 245, 245));
        chip.setLayout(new BoxLayout(chip, BoxLayout.X_AXIS));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        User user = CustomerData.getCurrentUser();
        String displayName = user != null && user.getUsername() != null ? user.getUsername() : "Guest";
        String emailText = user != null && user.getEmail() != null ? user.getEmail() : "";
        String initials = makeInitials(displayName);

        JLabel avatar = new JLabel(initials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1E3A6E));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avatar.setForeground(new Color(0x93C5FD));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setOpaque(false);
        Dimension av = new Dimension(36, 36);
        avatar.setPreferredSize(av);
        avatar.setMinimumSize(av);
        avatar.setMaximumSize(av);

        JPanel info = new JPanel();
        info.setBackground(new Color(245, 245, 245));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(displayName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(NAVY);

        JLabel email = new JLabel(emailText);
        email.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        email.setForeground(TXT_MUTED);

        info.add(name);
        info.add(email);

        chip.add(avatar);
        chip.add(Box.createHorizontalStrut(10));
        chip.add(info);
        return chip;
    }

    private static Icon createLogoutIcon(Color c) {
        return new Icon() {
            @Override public void paintIcon(Component comp, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRect(x+1, y+3, 9, 12);
                g2.drawLine(x+7,  y+9, x+16, y+9);
                g2.drawLine(x+13, y+6, x+16, y+9);
                g2.drawLine(x+13, y+12,x+16, y+9);
                g2.dispose();
            }
            @Override public int getIconWidth()  { return 18; }
            @Override public int getIconHeight() { return 18; }
        };
    }

    public static JPanel buildBarChart() {
        JPanel chart = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int[] vals      = { 70, 55, 90, 22, 12 };
                Color[] colors  = { new Color(0x22C55E), new Color(0xF97316),
                                    new Color(0x3B82F6), new Color(0x6B7280), new Color(0x94A3B8) };
                String[] labels = { "Apr","May","Jun","Jul","Aug" };
                int pw = getWidth(), ph = getHeight();
                int maxBarH = ph - 28, bw = 28, gap = 20;
                int totalW  = vals.length * bw + (vals.length-1) * gap;
                int startX  = (pw - totalW) / 2;
                for (int i = 0; i < vals.length; i++) {
                    int barH = (int)(vals[i] / 100.0 * maxBarH);
                    int bx   = startX + i * (bw + gap);
                    int by   = ph - barH - 24;
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(bx, by, bw, barH, 6, 6);
                    g2.setFont(F_TINY);
                    g2.setColor(TXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], bx + (bw - fm.stringWidth(labels[i]))/2, ph-6);
                }
                g2.dispose();
            }
        };
        chart.setBackground(BG_CARD);
        return chart;
    }

    public static JButton makeActionBtn(String text, Color fg, Color bgDim, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bgDim.brighter() : bgDim);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_MED);
        btn.setForeground(accent);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton makeActionBtn(String text, Color fg, Color bg) {
        return makeActionBtn(text, fg, bg.darker(), bg);
    }

    // ── Kept for child panels that still call it ───────────────────────
    public static JPanel makeRoundPanel(Color bg) {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
    }

    // ── Kept for backward compat (child panels that call addTopbar) ────
    public static void addTopbar(JPanel panel, String pageTitle, String subtitle) {
        JPanel bar = buildTopbar(pageTitle);
        panel.add(bar, BorderLayout.NORTH);
    }

    // ── Kept for backward compat (child panels that call addSidebar) ───
    public static void addSidebar(JPanel panel, String activePage) {
        // No-op: sidebar is now in the frame WEST, not duplicated per card
    }

    private static CustomerData.DashboardStats loadDashboardStats() {
        try {
            return CustomerData.getDashboardStats();
        } catch (Exception e) {
            return new CustomerData.DashboardStats(0, 0, 0, 0, java.util.Collections.emptyList());
        }
    }

    private static Color[] statusColors(String status) {
        switch (status) {
            case "Checked In":
                return new Color[] { new Color(0x065F46), new Color(0x6EE7B7) };
            case "Pending":
                return new Color[] { new Color(0x92400E), new Color(0xFCD34D) };
            case "Approved":
                return new Color[] { new Color(0x1E3A8A), new Color(0x93C5FD) };
            case "Cancelled":
                return new Color[] { new Color(0x991B1B), new Color(0xFCA5A5) };
            default:
                return new Color[] { new Color(0x374151), new Color(0x9CA3AF) };
        }
    }

    private static String makeInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "G";
        }
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String second = parts.length > 1 ? parts[1].substring(0, 1) : "";
        return (first + second).toUpperCase();
    }
}
