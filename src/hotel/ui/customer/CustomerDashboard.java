package hotel.ui.customer;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

// CustomerDashboard: main shell for customer-facing UI.
// - Declares all panels at the top (leader's format)
// - Calls each panel class to build its screen (admin pattern)
// - Exposes switchTo() and shared helper methods for all child panels

public class CustomerDashboard {

    // ── Declare ALL panels here (leader's format) ──────────────────────
    public static JPanel dashboardPanel;
    public static BrowseRoomsPanel roomsPanel;       // separate file
    public static JPanel bookingPanel;
    public static JPanel paymentPanel;
    public static JPanel historyPanel;

    // ── Palette ────────────────────────────────────────────────────────
static final Color BG_MAIN       = new Color(18, 18, 18);   // BG_DARK — app root
static final Color BG_SIDEBAR    = new Color(13, 13, 30);   // dark navy sidebar
static final Color BG_TOPBAR     = new Color(18, 18, 18);   // BG_DARK — topbar
static final Color BG_CARD       = new Color(20, 20, 40);   // dark navy card
static final Color BG_ELEVATED   = new Color(22, 22, 50);   // welcome banner
static final Color BG_CONTENT    = new Color(18, 18, 18);   // BG_DARK — content area
static final Color BG_ROW        = new Color(28, 28, 50);   // booking row bg

static final Color BLUE          = new Color(59, 130, 246);  // stat card 1 — blue
static final Color BLUE_DIM      = new Color(15, 30, 70);    // stat card 1 bg
static final Color ORANGE        = new Color(249, 115, 22);  // stat card 2 — orange
static final Color ORANGE_DIM    = new Color(60, 25, 8);     // stat card 2 bg
static final Color PURPLE        = new Color(167, 139, 250); // stat card 3 — purple
static final Color PURPLE_DIM    = new Color(35, 20, 80);    // stat card 3 bg
static final Color TEAL          = new Color(52, 211, 153);  // stat card 4 — teal
static final Color TEAL_DIM      = new Color(6, 40, 30);     // stat card 4 bg

static final Color NAV_ACTIVE_BG   = new Color(30, 40, 100); // blue active nav
static final Color NAV_ACTIVE_TEXT = new Color(147, 197, 253);// light blue text
static final Color NAV_HOVER_BG    = new Color(40, 40, 40);  // BG_HOVER

static final Color TXT_PRIMARY   = new Color(240, 240, 240); // TEXT_WHITE
static final Color TXT_SECONDARY = new Color(150, 150, 150); // TEXT_GRAY
static final Color TXT_MUTED     = new Color(80, 80, 80);    // muted
static final Color BORDER        = new Color(50, 50, 50);    // BORDER_COLOR
static final Color BORDER_BLUE   = new Color(37, 99, 235);   // blue content border

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ── Fonts ──────────────────────────────────────────────────────────
    public static final Font F_LARGE = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font F_MED   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_TINY  = new Font("Segoe UI", Font.PLAIN, 10);

    // ── Layout ─────────────────────────────────────────────────────────
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
            JFrame frame = new JFrame("HMS - Hotel Management System");
            frame.setSize(W, H);
            frame.setLayout(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(BG_MAIN);

            // ── Create Content Panels (one per screen) ─────────────────
            dashboardPanel = new JPanel(null);
            roomsPanel     = new BrowseRoomsPanel();   // separate file
            bookingPanel   = new JPanel(null);
            paymentPanel   = new JPanel(null);
            historyPanel   = new JPanel(null);

            // ── Size all plain panels ──────────────────────────────────
            for (JPanel p : new JPanel[]{dashboardPanel, bookingPanel, paymentPanel, historyPanel}) {
                p.setBounds(0, 0, W, H);
                p.setBackground(BG_MAIN);
                p.setOpaque(true);
            }
            roomsPanel.setBounds(0, 0, W, H);

            // ── Add all panels to the frame ────────────────────────────
            frame.add(dashboardPanel);
            frame.add(roomsPanel);
            frame.add(bookingPanel);
            frame.add(paymentPanel);
            frame.add(historyPanel);

            // ── Call each panel class to build its screen ──────────────
            buildDashboardScreen();   // built right here below
            // BrowseRoomsPanel builds itself inside its constructor
            buildBookingPanel();      // placeholder
            buildPaymentPanel();      // placeholder
            buildHistoryPanel();      // placeholder

            // ── Show only Dashboard at the start ───────────────────────
            switchTo("dashboard");

            frame.setVisible(true);
        });
    }

    // =========================================================================
    // SCREEN 1 — DASHBOARD
    // =========================================================================
    public static void buildDashboardScreen() {
        addTopbar(dashboardPanel, "Dashboard", "Welcome back, John");
        addSidebar(dashboardPanel, "dashboard");

        // Content card
        int pad = 14;
        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + pad, CONTENT_Y + pad, CONTENT_W - pad * 2, CONTENT_H - pad * 2);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        dashboardPanel.add(content);

        int cx = 18, cy = 18;
        int innerW = content.getWidth() - cx * 2;

        // ── Greeting banner ────────────────────────────────────────────
        JPanel banner = makeRoundPanel(BG_ELEVATED);
        banner.setLayout(null);
        banner.setBounds(cx, cy, innerW, 100);
        content.add(banner);

        JLabel greet = new JLabel("Good morning, John");
        greet.setBounds(22, 18, innerW - 40, 36);
        greet.setFont(F_LARGE);
        greet.setForeground(TXT_PRIMARY);
        banner.add(greet);

        JLabel greetSub = new JLabel("Here is your booking overview for today, " + LocalDate.now().format(DATE_FMT));
        greetSub.setBounds(22, 54, innerW - 40, 18);
        greetSub.setFont(F_SMALL);
        greetSub.setForeground(TXT_SECONDARY);
        banner.add(greetSub);

        cy += 100 + 14;

        // ── Stat cards ─────────────────────────────────────────────────
        int statH = 88, statGap = 10;
        int statW = (innerW - statGap * 3) / 4;

        addStatCard(content, cx,                         cy, statW, statH, "12",    "Total Bookings",   BLUE,   BLUE_DIM);
        addStatCard(content, cx + (statW + statGap),     cy, statW, statH, "$1,234","Total Spent",      ORANGE, ORANGE_DIM);
        addStatCard(content, cx + (statW + statGap) * 2, cy, statW, statH, "1",    "Pending Approval", PURPLE, PURPLE_DIM);
        addStatCard(content, cx + (statW + statGap) * 3, cy, statW, statH, "2",    "Checked In Now",   TEAL,   TEAL_DIM);

        cy += statH + 14;

        // ── Bottom — left: recent bookings | right: chart + quick actions
        int bottomH = content.getHeight() - cy - 18;
        int leftW   = (innerW * 56) / 100;
        int rightW  = innerW - leftW - 14;
        int rightX  = cx + leftW + 14;

        // Recent Bookings card
        JPanel bookCard = makeRoundPanel(BG_CARD);
        bookCard.setLayout(null);
        bookCard.setBounds(cx, cy, leftW, bottomH);
        content.add(bookCard);

        JLabel bookTitle = new JLabel("Recent Bookings");
        bookTitle.setBounds(18, 16, 200, 18);
        bookTitle.setFont(F_MED);
        bookTitle.setForeground(TXT_SECONDARY);
        bookCard.add(bookTitle);

        Object[][] rows = {
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Checked In",  new Color(0x065F46), new Color(0x6EE7B7)},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Pending",     new Color(0x92400E), new Color(0xFCD34D)},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Approved",    new Color(0x1E3A8A), new Color(0x93C5FD)},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Checked Out", new Color(0x374151), new Color(0x9CA3AF)},
        };
        int ry = 46;
        for (Object[] r : rows) {
            addBookingRow(bookCard, 14, ry, leftW - 28,
                (String) r[0], (String) r[1], (String) r[2],
                (Color)  r[3], (Color)  r[4]);
            ry += 54;
        }

        // Monthly Spending chart card
        int chartH  = (bottomH - 12) * 55 / 100;
        int actionH = bottomH - chartH - 12;

        JPanel chartCard = makeRoundPanel(BG_CARD);
        chartCard.setLayout(null);
        chartCard.setBounds(rightX, cy, rightW, chartH);
        chartCard.setBorder(BorderFactory.createLineBorder(new Color(0x1E3A6E), 1));
        content.add(chartCard);

        JLabel chartTitle = new JLabel("Monthly Spending");
        chartTitle.setBounds(16, 14, 200, 18);
        chartTitle.setFont(F_MED);
        chartTitle.setForeground(TXT_SECONDARY);
        chartCard.add(chartTitle);

        JPanel chart = buildBarChart();
        chart.setBounds(12, 40, rightW - 24, chartH - 56);
        chartCard.add(chart);

        // Quick Actions card
        JPanel actionCard = makeRoundPanel(BG_CARD);
        actionCard.setLayout(null);
        actionCard.setBounds(rightX, cy + chartH + 12, rightW, actionH);
        content.add(actionCard);

        JLabel actionTitle = new JLabel("Quick Actions");
        actionTitle.setBounds(16, 14, 200, 18);
        actionTitle.setFont(F_MED);
        actionTitle.setForeground(TXT_SECONDARY);
        actionCard.add(actionTitle);

        int btnW = (rightW - 48) / 2;

        JButton btnBrowse = makeActionBtn("Browse Rooms", TXT_PRIMARY, ORANGE_DIM, ORANGE);
        btnBrowse.setBounds(16, 42, btnW, 36);
        actionCard.add(btnBrowse);

        JButton btnBook = makeActionBtn("New Booking", TXT_PRIMARY, BLUE_DIM, BLUE);
        btnBook.setBounds(16 + btnW + 16, 42, btnW, 36);
        actionCard.add(btnBook);

        // ── Button Actions ─────────────────────────────────────────────
        btnBrowse.addActionListener(e -> switchTo("rooms"));
        btnBook.addActionListener(e   -> switchTo("booking"));
    }

    // =========================================================================
    // SCREEN 3 — BOOKING  (placeholder — your teammate fills this)
    // =========================================================================
    public static void buildBookingPanel() {
        addTopbar(bookingPanel, "Bookings", "Create or manage your booking");
        addSidebar(bookingPanel, "booking");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 14, CONTENT_Y + 14, CONTENT_W - 28, CONTENT_H - 28);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        bookingPanel.add(content);

        JLabel lbl = new JLabel("Booking panel — coming soon.");
        lbl.setBounds(24, 24, 400, 24);
        lbl.setFont(F_MED);
        lbl.setForeground(TXT_PRIMARY);
        content.add(lbl);
    }

    // =========================================================================
    // SCREEN 4 — PAYMENT  (placeholder — your teammate fills this)
    // =========================================================================
    public static void buildPaymentPanel() {
        addTopbar(paymentPanel, "Payment", "Scan or view payment codes");
        addSidebar(paymentPanel, "payment");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 14, CONTENT_Y + 14, CONTENT_W - 28, CONTENT_H - 28);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        paymentPanel.add(content);

        JLabel lbl = new JLabel("Payment panel — coming soon.");
        lbl.setBounds(24, 24, 400, 24);
        lbl.setFont(F_MED);
        lbl.setForeground(TXT_PRIMARY);
        content.add(lbl);
    }

    // =========================================================================
    // SCREEN 5 — HISTORY  (placeholder — your teammate fills this)
    // =========================================================================
    public static void buildHistoryPanel() {
        addTopbar(historyPanel, "History", "View your past bookings");
        addSidebar(historyPanel, "history");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 14, CONTENT_Y + 14, CONTENT_W - 28, CONTENT_H - 28);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        historyPanel.add(content);

        JLabel lbl = new JLabel("History panel — coming soon.");
        lbl.setBounds(24, 24, 400, 24);
        lbl.setFont(F_MED);
        lbl.setForeground(TXT_PRIMARY);
        content.add(lbl);
    }

    // =========================================================================
    // SHARED — TOPBAR  (used by all screens)
    // =========================================================================
    public static void addTopbar(JPanel panel, String pageTitle, String subtitle) {
        JPanel bar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_TOPBAR);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BORDER);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        bar.setBounds(0, 0, W, TOPBAR_H);
        bar.setOpaque(false);
        panel.add(bar);

        JLabel brand = new JLabel("HMS");
        brand.setBounds(18, 0, 80, TOPBAR_H);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brand.setForeground(TXT_SECONDARY);
        bar.add(brand);

        JLabel title = new JLabel(pageTitle, SwingConstants.CENTER);
        title.setBounds(W / 2 - 200, 8, 400, 26);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ORANGE);
        bar.add(title);

        JLabel sub = new JLabel(subtitle, SwingConstants.CENTER);
        sub.setBounds(W / 2 - 200, 32, 400, 16);
        sub.setFont(F_TINY);
        sub.setForeground(TXT_SECONDARY);
        bar.add(sub);

        JLabel dateLbl = new JLabel(LocalDate.now().format(DATE_FMT));
        dateLbl.setBounds(W - 200, 0, 140, TOPBAR_H);
        dateLbl.setFont(F_SMALL);
        dateLbl.setForeground(TXT_SECONDARY);
        dateLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        bar.add(dateLbl);

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setBounds(W - 52, 0, 40, TOPBAR_H);
        bell.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bell.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(bell);
    }

    // =========================================================================
    // SHARED — SIDEBAR  (used by all screens)
    // =========================================================================
    public static void addSidebar(JPanel panel, String activePage) {
        JPanel side = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_SIDEBAR);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BORDER);
                g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            }
        };
        side.setBounds(0, TOPBAR_H, SIDEBAR_W, H - TOPBAR_H);
        side.setOpaque(false);
        panel.add(side);

        JLabel hmsLbl = new JLabel("HMS");
        hmsLbl.setBounds(18, 14, 100, 18);
        hmsLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hmsLbl.setForeground(TXT_PRIMARY);
        side.add(hmsLbl);

        JLabel custLbl = new JLabel("Customer");
        custLbl.setBounds(18, 34, 120, 14);
        custLbl.setFont(F_TINY);
        custLbl.setForeground(TXT_MUTED);
        side.add(custLbl);

        // Nav items — same order as leader's code
        Object[][] navItems = {
            {"\u229E", "Dashboard", "dashboard", 58},
            {"\u2394",  "Rooms",     "rooms",     102},
            {"\u2612",  "Bookings",  "booking",   146},
            {"\u25A4",  "Payment",   "payment",   190},
            {"\u25A1",  "History",   "history",   234},
        };

        for (Object[] item : navItems) {
            String  icon   = (String) item[0];
            String  label  = (String) item[1];
            String  key    = (String) item[2];
            int     y      = (int)    item[3];
            boolean active = key.equals(activePage);

            JButton btn = makeNavBtn(icon, label, active, y);
            btn.addActionListener(e -> switchTo(key));
            side.add(btn);
        }

        // Footer — avatar + name
        JPanel footer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDER);
                g.drawLine(0, 0, getWidth(), 0);
                g.setColor(BG_SIDEBAR);
                g.fillRect(0, 1, getWidth(), getHeight() - 1);
            }
        };
        footer.setBounds(0, H - TOPBAR_H - 58, SIDEBAR_W, 58);
        footer.setOpaque(false);
        side.add(footer);

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1E3A6E));
                g2.fillOval(0, 0, 34, 34);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(0x93C5FD));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("ST", (34 - fm.stringWidth("ST")) / 2,
                    (34 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(12, 12, 34, 34);
        footer.add(avatar);

        JLabel uname = new JLabel("Customer");
        uname.setBounds(54, 10, 120, 16);
        uname.setFont(F_MED);
        uname.setForeground(TXT_PRIMARY);
        footer.add(uname);

        JLabel uemail = new JLabel("staff@gmail.com");
        uemail.setBounds(54, 28, 130, 14);
        uemail.setFont(F_TINY);
        uemail.setForeground(TXT_MUTED);
        footer.add(uemail);
    }

    // =========================================================================
    // SHARED HELPERS  (all child panels call these)
    // =========================================================================

    public static JButton makeNavBtn(String icon, String label, boolean active, int y) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(NAV_ACTIVE_BG);
                    g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(NAV_HOVER_BG);
                    g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 10, 10);
                }
                int boxSize = 26, boxX = 18;
                int boxY = (getHeight() - boxSize) / 2;
                g2.setColor(active ? new Color(0x7C2D12) : new Color(0x1A1A30));
                g2.fillRoundRect(boxX, boxY, boxSize, boxSize, 6, 6);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(active ? NAV_ACTIVE_TEXT : TXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, boxX + (boxSize - fm.stringWidth(icon)) / 2,
                    boxY + (boxSize + fm.getAscent() - fm.getDescent()) / 2);
                g2.setFont(active ? F_MED : F_REG);
                g2.setColor(active ? NAV_ACTIVE_TEXT : TXT_SECONDARY);
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(label, boxX + boxSize + 10,
                    (getHeight() + fm2.getAscent() - fm2.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setBounds(0, y, SIDEBAR_W, 40);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void addStatCard(JPanel p, int x, int y, int w, int h,
                            String num, String label, Color accent, Color bg) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
            }
        };
        card.setBounds(x, y, w, h);
        card.setOpaque(false);

        JLabel numLbl = new JLabel(num);
        numLbl.setBounds(14, 12, w - 20, 32);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        numLbl.setForeground(accent);
        card.add(numLbl);

        JLabel lbl = new JLabel(label);
        lbl.setBounds(14, 46, w - 20, 16);
        lbl.setFont(F_TINY);
        lbl.setForeground(TXT_SECONDARY);
        card.add(lbl);

        p.add(card);
    }

    public static void addBookingRow(JPanel parent, int x, int y, int w,
                              String name, String dates, String status,
                              Color bgBadge, Color fgBadge) {
        JPanel row = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        row.setBounds(x, y, w, 46);
        row.setOpaque(false);

        JLabel n = new JLabel(name);
        n.setBounds(12, 6, w - 120, 18);
        n.setFont(F_MED);
        n.setForeground(TXT_PRIMARY);
        row.add(n);

        JLabel d = new JLabel(dates);
        d.setBounds(12, 25, w - 120, 14);
        d.setFont(F_TINY);
        d.setForeground(TXT_SECONDARY);
        row.add(d);

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
        badge.setBounds(w - 100, 13, 92, 20);
        badge.setFont(F_TINY);
        badge.setForeground(fgBadge);
        badge.setOpaque(false);
        row.add(badge);

        parent.add(row);
    }

    public static JPanel buildBarChart() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int[] vals      = {70, 55, 90, 22, 12};
                Color[] colors  = {
                    new Color(0x22C55E), new Color(0xF97316),
                    new Color(0x3B82F6), new Color(0x6B7280), new Color(0x94A3B8)
                };
                String[] labels = {"Apr","May","Jun","Jul","Aug"};
                int pw = getWidth(), ph = getHeight();
                int maxBarH = ph - 28;
                int bw = 28, gap = 20;
                int totalW = vals.length * bw + (vals.length - 1) * gap;
                int startX = (pw - totalW) / 2;
                for (int i = 0; i < vals.length; i++) {
                    int barH = (int)(vals[i] / 100.0 * maxBarH);
                    int bx   = startX + i * (bw + gap);
                    int by   = ph - barH - 24;
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(bx, by, bw, barH, 6, 6);
                    g2.setFont(F_TINY);
                    g2.setColor(TXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], bx + (bw - fm.stringWidth(labels[i])) / 2, ph - 6);
                }
                g2.dispose();
            }
        };
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
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
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

    // Overload kept for BrowseRoomsPanel compatibility
    public static JButton makeActionBtn(String text, Color fg, Color bg) {
        return makeActionBtn(text, fg, bg.darker(), bg);
    }

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

    // ── Switch between screens ─────────────────────────────────────────
    public static void switchTo(String page) {
        dashboardPanel.setVisible(page.equals("dashboard"));
        roomsPanel.setVisible(page.equals("rooms"));
        bookingPanel.setVisible(page.equals("booking"));
        paymentPanel.setVisible(page.equals("payment"));
        historyPanel.setVisible(page.equals("history"));
    }
}