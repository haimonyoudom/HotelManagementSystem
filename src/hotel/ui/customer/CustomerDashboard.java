package hotel.ui.customer;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class CustomerDashboard {

    // ── Declare ALL panels here (leader's format) ──────────────────────
    static JPanel dashboardPanel;
    static BrowseRoomsPanel roomsPanel;
    static JPanel bookingPanel;
    static JPanel paymentPanel;
    static JPanel historyPanel;

    // ── Colors ─────────────────────────────────────────────────────────
    static final Color BG_MAIN      = new Color(0x0D0D1A);
    static final Color BG_SIDEBAR   = new Color(0x111122);
    static final Color BG_TOPBAR    = new Color(0x0A0A16);
    static final Color BG_CARD      = new Color(0x16162A);
    static final Color BG_ELEVATED  = new Color(0x1C1C35);
    static final Color BG_CONTENT   = new Color(0x0F0F20);

    static final Color BLUE         = new Color(0x3B82F6);
    static final Color BLUE_DIM     = new Color(0x1E3A6E);
    static final Color ORANGE       = new Color(0xF97316);
    static final Color ORANGE_DIM   = new Color(0x7C2D12);
    static final Color PURPLE       = new Color(0xA78BFA);
    static final Color PURPLE_DIM   = new Color(0x2D1F6E);
    static final Color TEAL         = new Color(0x34D399);
    static final Color TEAL_DIM     = new Color(0x064E3B);

    static final Color NAV_ACTIVE_BG   = new Color(0x7C2D12);
    static final Color NAV_ACTIVE_TEXT = new Color(0xFED7AA);
    static final Color NAV_HOVER_BG    = new Color(0x1E1E3A);

    static final Color TXT_PRIMARY   = new Color(0xE8E6FF);
    static final Color TXT_SECONDARY = new Color(0x8884CC);
    static final Color TXT_MUTED     = new Color(0x4A4870);
    static final Color BORDER        = new Color(0x1E1E3F);
    static final Color BORDER_BLUE   = new Color(0x2563EB);

    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ── Fonts ───────────────────────────────────────────────────────────
    static final Font F_LARGE  = new Font("Segoe UI", Font.BOLD,  20);
    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  16);
    static final Font F_MED    = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_REG    = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_TINY   = new Font("Segoe UI", Font.PLAIN, 10);

    // ── Layout constants ────────────────────────────────────────────────
    static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    static final int W          = SCREEN.width;
    static final int H          = SCREEN.height;
    static final int SIDEBAR_W  = 170;
    static final int TOPBAR_H   = 52;
    static final int CONTENT_X  = SIDEBAR_W;
    static final int CONTENT_Y  = TOPBAR_H;
    static final int CONTENT_W  = W - SIDEBAR_W - 2;
    static final int CONTENT_H  = H - TOPBAR_H - 2;

    // ==========================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {

            // ── Create the Window ──────────────────────────────────────────
            JFrame frame = new JFrame("HMS - Hotel Management System");
            frame.setSize(W, H);
            frame.setLayout(null);
            frame.setResizable(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(BG_MAIN);

            // ── Create panels (one per screen) ─────────────────────────────
            dashboardPanel = new JPanel(null);
            roomsPanel     = new BrowseRoomsPanel();
            bookingPanel   = new JPanel(null);
            paymentPanel   = new JPanel(null);
            historyPanel   = new JPanel(null);

            dashboardPanel.setBounds(0, 0, W, H);
            dashboardPanel.setBackground(BG_MAIN);

            roomsPanel.setBounds(0, 0, W, H);
            roomsPanel.setBackground(BG_MAIN);

            bookingPanel.setBounds(0, 0, W, H);
            bookingPanel.setBackground(BG_MAIN);

            paymentPanel.setBounds(0, 0, W, H);
            paymentPanel.setBackground(BG_MAIN);

            historyPanel.setBounds(0, 0, W, H);
            historyPanel.setBackground(BG_MAIN);

            // ── Add all panels to the frame ────────────────────────────────
            frame.add(dashboardPanel);
            frame.add(roomsPanel);
            frame.add(bookingPanel);
            frame.add(paymentPanel);
            frame.add(historyPanel);

            // ── Build content for each screen ──────────────────────────────
            buildDashboardScreen();
            buildBookingPanel();
            buildPaymentPanel();
            buildHistoryPanel();

            // ── Show only Dashboard at start ───────────────────────────────
            dashboardPanel.setVisible(true);
            roomsPanel.setVisible(false);
            bookingPanel.setVisible(false);
            paymentPanel.setVisible(false);
            historyPanel.setVisible(false);

            frame.setVisible(true);
        });
    }

    // ==========================================================================
    // SCREEN 1 — DASHBOARD
    // ==========================================================================
    static void buildDashboardScreen() {

        // TOPBAR
        addTopbar(dashboardPanel, "Dashboard", "Welcome back username");

        // SIDEBAR — Dashboard is active
        addSidebar(dashboardPanel, "dashboard");

        // CONTENT AREA
        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 10, CONTENT_Y + 10, CONTENT_W - 14, CONTENT_H - 14);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        dashboardPanel.add(content);

        int cx = 14, cy = 14;
        int cw = content.getWidth() - cx * 2;

        // ── Welcome Banner ─────────────────────────────────────────────────
        JPanel banner = makeRoundPanel(BG_ELEVATED);
        banner.setLayout(null);
        banner.setBounds(cx, cy, cw, 72);
        content.add(banner);

        JLabel greet = new JLabel("Good morning, John");
        greet.setBounds(18, 12, 400, 28);
        greet.setFont(F_LARGE);
        greet.setForeground(TXT_PRIMARY);
        banner.add(greet);

        String currentDate = LocalDate.now().format(DATE_FMT);
        JLabel greetSub = new JLabel("Here is your booking overview for today, " + currentDate);
        greetSub.setBounds(18, 40, 520, 20);
        greetSub.setFont(F_SMALL);
        greetSub.setForeground(TXT_SECONDARY);
        banner.add(greetSub);

        cy += 72 + 12;

        // ── Stat Cards ─────────────────────────────────────────────────────
        int cardW = (cw - 30) / 4;

        addStatCard(content, cx,                       cy, cardW, 72, "12",     "Total Bookings",   BLUE,   new Color(0x1E3A6E));
        addStatCard(content, cx + (cardW + 10),        cy, cardW, 72, "$1,234", "Total Spent",      ORANGE, new Color(0x7C2D12));
        addStatCard(content, cx + (cardW + 10) * 2,    cy, cardW, 72, "1",      "Pending Approval", PURPLE, new Color(0x2D1F6E));
        addStatCard(content, cx + (cardW + 10) * 3,    cy, cardW, 72, "2",      "Checked In Now",   TEAL,   new Color(0x064E3B));

        cy += 72 + 12;

        // ── Bottom Two Columns ─────────────────────────────────────────────
        int halfW  = (cw - 10) / 2;
        int bottomH = CONTENT_H - cy - 28;

        // Left — Recent Bookings
        JPanel rbCard = makeRoundPanel(BG_CARD);
        rbCard.setLayout(null);
        rbCard.setBounds(cx, cy, halfW, bottomH);
        content.add(rbCard);

        JLabel rbTitle = new JLabel("Recent Bookings");
        rbTitle.setBounds(14, 10, 200, 20);
        rbTitle.setFont(F_MED);
        rbTitle.setForeground(TXT_SECONDARY);
        rbCard.add(rbTitle);

        String[][] rows = {
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Checked In",  "065F46","6EE7B7"},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Pending",     "92400E","FCD34D"},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Approved",    "1E3A8A","93C5FD"},
            {"Deluxe King · Rm 304","Apr 28 – May 2, 2026","Checked Out", "374151","9CA3AF"},
        };
        int ry = 38;
        for (String[] r : rows) {
            addBookingRow(rbCard, 10, ry, halfW - 20, r[0], r[1], r[2],
                Color.decode("#" + r[3]), Color.decode("#" + r[4]));
            ry += 52;
        }

        // Right column
        int rx   = cx + halfW + 10;
        int chH  = bottomH * 58 / 100;
        int qaH  = bottomH - chH - 10;

        // Monthly Spending chart
        JPanel chartCard = makeRoundPanel(BG_CARD);
        chartCard.setLayout(null);
        chartCard.setBounds(rx, cy, halfW, chH);
        content.add(chartCard);

        JLabel chartTitle = new JLabel("Monthly Spending");
        chartTitle.setBounds(14, 10, 200, 20);
        chartTitle.setFont(F_MED);
        chartTitle.setForeground(TXT_SECONDARY);
        chartCard.add(chartTitle);

        JPanel chart = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int[] vals  = {70, 55, 90, 30, 10};
                String[] lb = {"Apr","May","Jun","Jul","Aug"};
                Color[]  cl = {new Color(0x34D399), new Color(0xF87171),
                                new Color(0x3B82F6), new Color(0x6B7280), new Color(0x374151)};
                int pw = getWidth(), ph = getHeight();
                int maxH = ph - 24;
                int bw   = 22;
                int tot  = vals.length * bw + (vals.length - 1) * 14;
                int sx   = (pw - tot) / 2;
                for (int i = 0; i < vals.length; i++) {
                    int bh = (int)(vals[i] / 100.0 * maxH);
                    int bx = sx + i * (bw + 14);
                    g2.setColor(cl[i]);
                    g2.fillRoundRect(bx, maxH - bh, bw, bh, 5, 5);
                    g2.setFont(F_TINY);
                    g2.setColor(TXT_SECONDARY);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(lb[i], bx + (bw - fm.stringWidth(lb[i])) / 2, ph - 4);
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setBounds(10, 36, chartCard.getWidth() - 20, chartCard.getHeight() - 46);
        chartCard.add(chart);

        // Quick Actions
        JPanel qaCard = makeRoundPanel(BG_CARD);
        qaCard.setLayout(null);
        qaCard.setBounds(rx, cy + chH + 10, halfW, qaH);
        content.add(qaCard);

        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setBounds(14, 8, 200, 20);
        qaTitle.setFont(F_MED);
        qaTitle.setForeground(TXT_SECONDARY);
        qaCard.add(qaTitle);

        int bw = (qaCard.getWidth() - 38) / 2;
        JButton btnBrowse = makeActionBtn("Browse Rooms", ORANGE, new Color(0x7C2D12));
        btnBrowse.setBounds(14, 32, bw, 34);
        qaCard.add(btnBrowse);

        JButton btnBook = makeActionBtn("New Booking", BLUE, new Color(0x1E3A6E));
        btnBook.setBounds(14 + bw + 10, 32, bw, 34);
        qaCard.add(btnBook);

        // ── Button Actions ─────────────────────────────────────────────────
        btnBrowse.addActionListener(e -> switchTo("rooms"));
        btnBook.addActionListener(e -> switchTo("rooms"));
    }

    // SHARED: TOPBAR
    // ==========================================================================
    static void addTopbar(JPanel panel, String pageTitle, String subtitle) {
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
        brand.setBounds(14, 15, 60, 22);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brand.setForeground(TXT_SECONDARY);
        bar.add(brand);

        JLabel title = new JLabel(pageTitle, SwingConstants.CENTER);
        title.setBounds(W / 2 - 150, 8, 300, 22);
        title.setFont(F_TITLE);
        title.setForeground(ORANGE);
        bar.add(title);

        JLabel sub = new JLabel(subtitle, SwingConstants.CENTER);
        sub.setBounds(W / 2 - 150, 28, 300, 16);
        sub.setFont(F_TINY);
        sub.setForeground(TXT_SECONDARY);
        bar.add(sub);

        String topDate = LocalDate.now().format(DATE_FMT);
        JLabel dateBell = new JLabel(topDate + "   \uD83D\uDD14");
        dateBell.setBounds(W - 160, 16, 148, 20);
        dateBell.setFont(F_SMALL);
        dateBell.setForeground(TXT_SECONDARY);
        dateBell.setHorizontalAlignment(SwingConstants.RIGHT);
        bar.add(dateBell);
    }

    // ==========================================================================
    // SHARED: SIDEBAR
    // ==========================================================================
    static void addSidebar(JPanel panel, String activePage) {
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

        JLabel hms = new JLabel("HMS");
        hms.setBounds(14, 12, 100, 18);
        hms.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hms.setForeground(TXT_PRIMARY);
        side.add(hms);

        JLabel custLbl = new JLabel("Customer");
        custLbl.setBounds(14, 34, 120, 16);
        custLbl.setFont(F_TINY);
        custLbl.setForeground(TXT_MUTED);
        side.add(custLbl);

        // Nav items
        boolean isDash    = activePage.equals("dashboard");
        boolean isRooms   = activePage.equals("rooms");
        boolean isBook    = activePage.equals("booking");
        boolean isPayment = activePage.equals("payment");
        boolean isHistory = activePage.equals("history");

        JButton navDash    = makeNavBtn("  D  Dashboard",     isDash,    58);
        JButton navRooms   = makeNavBtn("  R  Browse Rooms",   isRooms,   96);
        JButton navBook    = makeNavBtn("  B  Booking Panel",  isBook,   134);
        JButton navPay     = makeNavBtn("  P  Payment QR",     isPayment,172);
        JButton navHistory = makeNavBtn("  H  Booking History",isHistory,210);

        side.add(navDash);
        side.add(navRooms);
        side.add(navBook);
        side.add(navPay);
        side.add(navHistory);

        navDash.addActionListener(e  -> switchTo("dashboard"));
        navRooms.addActionListener(e -> switchTo("rooms"));
        navBook.addActionListener(e -> switchTo("booking"));
        navPay.addActionListener(e -> switchTo("payment"));
        navHistory.addActionListener(e -> switchTo("history"));

        // Footer
        JPanel footer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDER);
                g.drawLine(0, 0, getWidth(), 0);
                g.setColor(BG_SIDEBAR);
                g.fillRect(0, 1, getWidth(), getHeight() - 1);
            }
        };
        footer.setBounds(0, H - TOPBAR_H - 52, SIDEBAR_W, 52);
        footer.setOpaque(false);
        side.add(footer);

        // Avatar circle
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1E3A6E));
                g2.fillOval(0, 0, 32, 32);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(0x93C5FD));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("ST", (32 - fm.stringWidth("ST")) / 2,
                    (32 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(10, 10, 32, 32);
        footer.add(avatar);

        JLabel uname = new JLabel("Staff");
        uname.setBounds(50, 8, 110, 16);
        uname.setFont(F_MED);
        uname.setForeground(TXT_PRIMARY);
        footer.add(uname);

        JLabel uemail = new JLabel("staff@gmail.com");
        uemail.setBounds(50, 24, 110, 14);
        uemail.setFont(F_TINY);
        uemail.setForeground(TXT_MUTED);
        footer.add(uemail);
    }

    // ==========================================================================
    // HELPERS
    // ==========================================================================

    static JButton makeNavBtn(String text, boolean active, int y) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(NAV_ACTIVE_BG);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                    g2.setColor(ORANGE);
                    g2.fillRoundRect(2, 6, 3, getHeight() - 12, 3, 3);
                } else if (getModel().isRollover()) {
                    g2.setColor(NAV_HOVER_BG);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBounds(0, y, SIDEBAR_W, 36);
        btn.setFont(active ? F_MED : F_REG);
        btn.setForeground(active ? NAV_ACTIVE_TEXT : TXT_SECONDARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static void addStatCard(JPanel p, int x, int y, int w, int h,
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
        numLbl.setBounds(12, 10, w - 16, 28);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        numLbl.setForeground(accent);
        card.add(numLbl);

        JLabel lbl = new JLabel(label);
        lbl.setBounds(12, 40, w - 16, 18);
        lbl.setFont(F_TINY);
        lbl.setForeground(TXT_SECONDARY);
        card.add(lbl);

        p.add(card);
    }

    static void addBookingRow(JPanel parent, int x, int y, int w,
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
        row.setBounds(x, y, w, 44);
        row.setOpaque(false);

        JLabel n = new JLabel(name);
        n.setBounds(10, 5, w - 110, 18);
        n.setFont(F_MED);
        n.setForeground(TXT_PRIMARY);
        row.add(n);

        JLabel d = new JLabel(dates);
        d.setBounds(10, 23, w - 110, 14);
        d.setFont(F_TINY);
        d.setForeground(TXT_SECONDARY);
        row.add(d);

        JLabel badge = new JLabel(status, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgBadge);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setBounds(w - 96, 12, 88, 20);
        badge.setFont(F_TINY);
        badge.setForeground(fgBadge);
        badge.setOpaque(false);
        row.add(badge);

        parent.add(row);
    }

    static void addRoomCard(JPanel parent, int x, int y, int w, int h,
                            String name, String desc, String price,
                            String badgeText, Color badgeBg, Color badgeFg) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setBounds(x, y, w, h);
        card.setOpaque(false);

        JPanel imgBox = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x232649), 0, getHeight(), new Color(0x0B0B17));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(0xFFFFFF, true));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String roomType = name.split(" ")[0];
                g2.drawString(roomType, 16, getHeight() - 20);
                g2.dispose();
            }
        };
        imgBox.setBounds(8, 8, w - 16, 110);
        imgBox.setOpaque(false);
        card.add(imgBox);

        JLabel badge = new JLabel(badgeText, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setBounds(w - 108, 16, 90, 24);
        badge.setFont(F_TINY);
        badge.setForeground(badgeFg);
        badge.setOpaque(false);
        card.add(badge);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setBounds(16, 128, w - 32, 24);
        nameLbl.setFont(F_TITLE);
        nameLbl.setForeground(TXT_PRIMARY);
        card.add(nameLbl);

        JLabel descLbl = new JLabel(desc);
        descLbl.setBounds(16, 152, w - 32, 18);
        descLbl.setFont(F_SMALL);
        descLbl.setForeground(TXT_SECONDARY);
        card.add(descLbl);

        JLabel priceLbl = new JLabel(price);
        priceLbl.setBounds(16, 174, w - 32, 22);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLbl.setForeground(ORANGE);
        card.add(priceLbl);

        JButton bookBtn = makeActionBtn("Book Now", TXT_PRIMARY, new Color(0xF97316));
        bookBtn.setBounds(16, h - 44, w - 32, 34);
        card.add(bookBtn);

        parent.add(card);
    }

    static JButton makeFilterChip(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(new Color(0x7C2D12));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(ORANGE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                } else {
                    g2.setColor(BG_ELEVATED);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(active ? F_MED : F_REG);
        btn.setForeground(active ? ORANGE : TXT_SECONDARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static JButton makeActionBtn(String text, Color fg, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_MED);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static JPanel makeRoundPanel(Color bg) {
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
    static void switchTo(String page) {
        dashboardPanel.setVisible(page.equals("dashboard"));
        roomsPanel.setVisible(page.equals("rooms"));
        bookingPanel.setVisible(page.equals("booking"));
        paymentPanel.setVisible(page.equals("payment"));
        historyPanel.setVisible(page.equals("history"));
    }

    static void buildBookingPanel() {
        addTopbar(bookingPanel, "Booking Panel", "Create or manage your booking");
        addSidebar(bookingPanel, "booking");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 10, CONTENT_Y + 10, CONTENT_W - 14, CONTENT_H - 14);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        bookingPanel.add(content);

        JLabel label = new JLabel("Booking Panel content will appear here.");
        label.setBounds(24, 24, 400, 24);
        label.setFont(F_MED);
        label.setForeground(TXT_PRIMARY);
        content.add(label);
    }

    static void buildPaymentPanel() {
        addTopbar(paymentPanel, "Payment QR", "Scan or view payment codes");
        addSidebar(paymentPanel, "payment");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 10, CONTENT_Y + 10, CONTENT_W - 14, CONTENT_H - 14);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        paymentPanel.add(content);

        JLabel label = new JLabel("Payment QR panel content will appear here.");
        label.setBounds(24, 24, 420, 24);
        label.setFont(F_MED);
        label.setForeground(TXT_PRIMARY);
        content.add(label);
    }

    static void buildHistoryPanel() {
        addTopbar(historyPanel, "Booking History", "View your past bookings");
        addSidebar(historyPanel, "history");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 10, CONTENT_Y + 10, CONTENT_W - 14, CONTENT_H - 14);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        historyPanel.add(content);

        JLabel label = new JLabel("Booking history content will appear here.");
        label.setBounds(24, 24, 420, 24);
        label.setFont(F_MED);
        label.setForeground(TXT_PRIMARY);
        content.add(label);
    }
}