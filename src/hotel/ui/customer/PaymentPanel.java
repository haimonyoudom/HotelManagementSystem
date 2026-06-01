package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import javax.swing.*;

// PaymentPanel: QR deposit payment screen.
// - Left: QR code card with 20%/30% toggle, QR image, amount, mark as paid button
// - Right: Payment summary — due amount, booking details table, payment status badges
// - Uses shared theme tokens from CustomerDashboard for consistent look.

public class PaymentPanel extends JPanel {

    // ── Colors ─────────────────────────────────────────────────────────
    // Light theme tokens for PaymentPanel
    private static final Color C_BG         = new Color(250, 250, 250 );
    private static final Color C_CARD_BG    = new Color(255, 255, 255 );
    private static final Color C_CARD_BOR   = new Color(220, 220, 220 );
    private static final Color C_ROW_BG     = new Color(245, 245, 245 );
    private static final Color C_ROW_BOR    = new Color(230, 230, 230 );

    private static final Color RED          = new Color(200, 50,  50 );
    private static final Color RED_DIM      = new Color(255, 235, 235 );
    private static final Color RED_BOR      = new Color(255, 200, 200 );
    private static final Color RED_HOVER    = new Color(230, 80, 80 );

    private static final Color GREEN        = new Color(34,  197, 94 );
    private static final Color GREEN_DIM    = new Color(235, 255, 240 );
    private static final Color GREEN_BOR    = new Color(200, 235, 200 );

    private static final Color DEP_ACTIVE_BG  = new Color(245, 245, 245 );
    private static final Color DEP_ACTIVE_BOR = new Color(200, 200, 200);
    private static final Color DEP_IDLE_BG    = new Color(255, 255, 255 );
    private static final Color DEP_IDLE_BOR   = new Color(230, 230, 230 );

    private static final Color TXT_WHITE    = new Color(20, 20, 20);
    private static final Color TXT_GRAY     = new Color(100, 100, 100);
    private static final Color TXT_MUTED    = new Color(140, 140, 140 );

    // ── Fonts ──────────────────────────────────────────────────────────
    private static final Font F_SCAN_TITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_SCAN_SUB   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_DEP_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_AMOUNT     = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font F_AMOUNT_SUB = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MARK       = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_SUM_TITLE  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_DUE_AMT    = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font F_DUE_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_ROW_KEY    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_ROW_VAL    = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font F_STATUS_LBL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BADGE      = new Font("Segoe UI", Font.BOLD,  12);

    // ── State ──────────────────────────────────────────────────────────
    private static int    depositPct   = 20;
    private static int    roomTotal    = 0;
    private static String bookingRef   = "#BK-2026-0001";
    private static String bookingRoom  = "Room not selected";
    private static String bookingCheckin  = "—";
    private static String bookingCheckout = "—";

    private static JLabel scanSubLbl;
    private static JLabel amountLbl;
    private static JLabel amountSubLbl;
    private static JLabel dueAmtLbl;
    private static JLabel dueLbl;
    private static JLabel remainLbl;
    private static JLabel detailRefValue;
    private static JLabel detailRoomValue;
    private static JLabel detailCheckinValue;
    private static JLabel detailCheckoutValue;
    private static JLabel detailRoomTotalValue;
    private static JButton btn20;
    private static JButton btn30;

    // =========================================================================
    public PaymentPanel() {
        setLayout(null);
        setBounds(0, 0, W, H);
        setBackground(C_BG);
        setOpaque(true);
    }

    // =========================================================================
    // Called from CustomerDashboard — same pattern as leader
    // =========================================================================
    public static void build(JPanel panel) {
        addTopbar(panel, "Payment", "Deposit payment screen");
        addSidebar(panel, "payment");

        // ── OUTER CONTENT AREA ────────────────────────────────────────
        int pad = 14;
        int cx  = CONTENT_X + pad;
        int cy  = CONTENT_Y + pad;
        int cw  = CONTENT_W - pad * 2;
        int ch  = CONTENT_H - pad * 2;

        // ── LEFT QR CARD ──────────────────────────────────────────────
        int leftW  = (int)(cw * 0.55);
        int rightW = cw - leftW - 16;
        int rightX = cx + leftW + 16;

        JPanel qrCard = makeCard(C_CARD_BG, C_CARD_BOR);
        qrCard.setBounds(cx, cy, leftW, ch);
        panel.add(qrCard);
        buildQRCard(qrCard, leftW, ch);

        // ── RIGHT SUMMARY PANEL ───────────────────────────────────────
        JPanel summaryCol = new JPanel(null);
        summaryCol.setOpaque(false);
        summaryCol.setBounds(rightX, cy, rightW, ch);
        panel.add(summaryCol);
        buildSummaryPanel(summaryCol, rightW, ch);
    }

    // =========================================================================
    // LEFT — QR CARD
    // =========================================================================
    static void buildQRCard(JPanel card, int w, int h) {
        int px = 0, innerW = w;

        // ── Title ─────────────────────────────────────────────────────
        JLabel scanTitle = lbl("Scan to Pay Deposit", F_SCAN_TITLE, TXT_WHITE);
        scanTitle.setHorizontalAlignment(SwingConstants.CENTER);
        scanTitle.setBounds(0, 20, innerW, 20);
        card.add(scanTitle);

        scanSubLbl = lbl("Booking " + bookingRef + " · " + bookingRoom, F_SCAN_SUB, TXT_GRAY);
        scanSubLbl.setHorizontalAlignment(SwingConstants.CENTER);
        scanSubLbl.setBounds(0, 42, innerW, 16);
        card.add(scanSubLbl);

        // ── Deposit Toggle Buttons ────────────────────────────────────
        int btnW  = (int)(innerW * 0.38);
        int btnH  = 36;
        int btnY  = 70;
        int gap   = 10;
        int totalBtnW = btnW * 2 + gap;
        int startX = (innerW - totalBtnW) / 2;

        btn20 = makeDepBtn("20% Deposit", true);
        btn20.setBounds(startX, btnY, btnW, btnH);
        card.add(btn20);

        btn30 = makeDepBtn("30% Deposit", false);
        btn30.setBounds(startX + btnW + gap, btnY, btnW, btnH);
        card.add(btn30);

        btn20.addActionListener(e -> setDeposit(20));
        btn30.addActionListener(e -> setDeposit(30));

        // ── QR Code Image ─────────────────────────────────────────────
        int qrSize = (int)(Math.min(w, h) * 0.42);
        int qrX    = (innerW - qrSize) / 2;
        int qrY    = btnY + btnH + 18;

        JPanel qrBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Outer rounded border
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_CARD_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // Checkerboard QR pattern
                int cells  = 14;
                int cellSz = (getWidth() - 16) / cells;
                int offX   = 8, offY = 8;
                boolean[][] qr = generateQRPattern(cells);
                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        g2.setColor(qr[r][c] ? new Color(80,80,80) : new Color(160,160,160));
                        g2.fillRect(offX + c * cellSz, offY + r * cellSz, cellSz, cellSz);
                    }
                }
                g2.dispose();
            }
        };
        qrBox.setBounds(qrX, qrY, qrSize, qrSize);
        qrBox.setOpaque(false);
        card.add(qrBox);

        // ── Amount ────────────────────────────────────────────────────
        int amtY = qrY + qrSize + 16;
        amountLbl = lbl("$347.00", F_AMOUNT, RED);
        amountLbl.setHorizontalAlignment(SwingConstants.CENTER);
        amountLbl.setBounds(0, amtY, innerW, 36);
        card.add(amountLbl);

        amountSubLbl = lbl((100 - depositPct) + "% of $" + roomTotal + " total", F_AMOUNT_SUB, TXT_GRAY);
        amountSubLbl.setHorizontalAlignment(SwingConstants.CENTER);
        amountSubLbl.setBounds(0, amtY + 38, innerW, 16);
        card.add(amountSubLbl);

        // ── Mark as Paid Button ───────────────────────────────────────
        int markY = amtY + 64;
        int markW = (int)(innerW * 0.72);
        int markX = (innerW - markW) / 2;

        JButton markBtn = new JButton("Mark as Paid") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_ROW_BG : C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_CARD_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        markBtn.setBounds(markX, markY, markW, 42);
        markBtn.setFont(F_MARK);
        markBtn.setForeground(TXT_WHITE);
        markBtn.setContentAreaFilled(false);
        markBtn.setBorderPainted(false);
        markBtn.setFocusPainted(false);
        markBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        markBtn.addActionListener(e -> CustomerDashboard.switchTo("history"));
        card.add(markBtn);
    }

    // =========================================================================
    // RIGHT — SUMMARY PANEL
    // =========================================================================
    static void buildSummaryPanel(JPanel col, int w, int h) {
        int py = 0;

        // ── Section label ─────────────────────────────────────────────
        JLabel sumTitle = lbl("Payment Summary", F_SUM_TITLE, TXT_GRAY);
        sumTitle.setBounds(0, py, w, 16);
        col.add(sumTitle);
        py += 26;

        // ── Due Amount Card ───────────────────────────────────────────
        int dueCardH = 74;
        JPanel dueCard = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Red left border
                g2.setColor(RED);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        dueCard.setBounds(0, py, w, dueCardH);
        dueCard.setOpaque(false);
        col.add(dueCard);

        dueAmtLbl = lbl("$347", F_DUE_AMT, RED);
        dueAmtLbl.setBounds(16, 10, w - 24, 36);
        dueCard.add(dueAmtLbl);

        dueLbl = lbl("Due Now (20% Deposit)", F_DUE_SUB, TXT_GRAY);
        dueLbl.setBounds(16, 46, w - 24, 16);
        dueCard.add(dueLbl);

        py += dueCardH + 12;

        // ── Booking Details Card ──────────────────────────────────────
        int detailCardH = 5 * 38 + 16;
        JPanel detailCard = makeCard(C_CARD_BG, C_CARD_BOR);
        detailCard.setBounds(0, py, w, detailCardH);
        col.add(detailCard);

        detailRefValue = addDetailRow(detailCard, "Booking ref", bookingRef, w, 10);
        detailRoomValue = addDetailRow(detailCard, "Room", bookingRoom, w, 48);
        detailCheckinValue = addDetailRow(detailCard, "Check-in", bookingCheckin, w, 86);
        detailCheckoutValue = addDetailRow(detailCard, "Check-out", bookingCheckout, w, 124);
        detailRoomTotalValue = addDetailRow(detailCard, "Room Total", "$0", w, 162);

        py += detailCardH + 8;

        // Remaining row (standalone inside same card area)
        remainLbl = lbl("Remaining$1,387 due at check-in", F_ROW_KEY, TXT_GRAY);
        remainLbl.setBounds(0, py, w, 16);
        col.add(remainLbl);
        py += 28;

        // ── Payment Status ────────────────────────────────────────────
        JLabel statusTitle = lbl("Payment Status", F_STATUS_LBL, TXT_WHITE);
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusTitle.setBounds(0, py, w, 18);
        col.add(statusTitle);
        py += 26;

        int badgeW = (w - 10) / 2;
        int badgeH = 52;

        // Deposit Pending — light red
        JPanel depBadge = makeBadge("Deposit\nPending",
            RED_DIM, RED_BOR);
        depBadge.setBounds(0, py, badgeW, badgeH);
        col.add(depBadge);

        // Balance Pending — light green
        JPanel balBadge = makeBadge("Balance\nPending",
            GREEN_DIM, GREEN_BOR);
        balBadge.setBounds(badgeW + 10, py, badgeW, badgeH);
        col.add(balBadge);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    static void setDeposit(int pct) {
        depositPct = pct;
        int payPct = 100 - pct; // user pays the remainder after discount
        int amt    = (int) Math.round(roomTotal * payPct / 100.0);
        int remain = roomTotal - amt;
        int remainPct = pct; // remaining (discount) percent due later

        // Update toggle button states
        updateDepBtn(btn20, pct == 20);
        updateDepBtn(btn30, pct == 30);

        // Update labels
        if (amountLbl    != null) amountLbl.setText("$" + String.format("%.2f", (double)amt));
        if (amountSubLbl != null) amountSubLbl.setText(payPct + "% of $" + roomTotal + " total");
        if (dueAmtLbl    != null) dueAmtLbl.setText("$" + amt);
        if (dueLbl       != null) dueLbl.setText("Due Now (" + payPct + "% Payment)");
        if (remainLbl    != null) remainLbl.setText("Remaining " + remainPct + "% ($" + remain + ") due at check-in");
    }

    public static void setBookingDetails(String ref, String room, String checkin, String checkout, int total) {
        bookingRef = ref;
        bookingRoom = room;
        bookingCheckin = checkin != null && !checkin.isEmpty() ? checkin : "—";
        bookingCheckout = checkout != null && !checkout.isEmpty() ? checkout : "—";
        roomTotal = total;
        depositPct = 20;

        if (scanSubLbl != null) {
            scanSubLbl.setText("Booking " + bookingRef + " · " + bookingRoom);
        }
        if (detailRefValue != null) detailRefValue.setText(bookingRef);
        if (detailRoomValue != null) detailRoomValue.setText(bookingRoom);
        if (detailCheckinValue != null) detailCheckinValue.setText(bookingCheckin);
        if (detailCheckoutValue != null) detailCheckoutValue.setText(bookingCheckout);
        if (detailRoomTotalValue != null) detailRoomTotalValue.setText("$" + roomTotal);
        setDeposit(depositPct);
    }

    static void updateDepBtn(JButton btn, boolean active) {
        btn.putClientProperty("active", active);
        btn.repaint();
    }

    static JButton makeDepBtn(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                boolean act = Boolean.TRUE.equals(getClientProperty("active"));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(act ? DEP_ACTIVE_BG : DEP_IDLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(act ? DEP_ACTIVE_BOR : DEP_IDLE_BOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("active", active);
        btn.setFont(F_DEP_BTN);
        btn.setForeground(TXT_WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static JLabel addDetailRow(JPanel parent, String key, String val, int w, int y) {
        // Separator line
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_ROW_BOR);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        sep.setBounds(12, y, w - 24, 1);
        sep.setOpaque(false);
        parent.add(sep);

        JLabel k = lbl(key, F_ROW_KEY, TXT_GRAY);
        k.setBounds(14, y + 4, (w / 2) - 10, 18);
        parent.add(k);

        // Highlight certain values
        Color valColor = TXT_WHITE;
        if (val.contains("Jun") || val.startsWith("$")) valColor = new Color(200, 50, 50);

        JLabel v = lbl(val, F_ROW_VAL, valColor);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setBounds(w / 2, y + 4, (w / 2) - 14, 18);
        parent.add(v);
        return v;
    }

    static JPanel makeBadge(String text, Color bg, Color border) {
        String[] lines = text.split("\n");
        JPanel badge = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        badge.setOpaque(false);

        for (int i = 0; i < lines.length; i++) {
            JLabel l = lbl(lines[i], F_BADGE, TXT_WHITE);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setBounds(0, 10 + i * 18, badge.getPreferredSize().width, 18);
            badge.add(l);
        }
        // Fix label bounds after badge size known — use full width
        for (Component c : badge.getComponents()) {
            c.setBounds(0, ((JLabel)c).getBounds().y, 200, 18);
        }
        return badge;
    }

    static JPanel makeCard(Color bg, Color border) {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
    }

    static JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Simple QR checkerboard pattern ────────────────────────────────
    static boolean[][] generateQRPattern(int size) {
        boolean[][] p = new boolean[size][size];
        // Corner finder patterns
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                boolean border = (i==0||i==6||j==0||j==6);
                boolean inner  = (i>=2&&i<=4&&j>=2&&j<=4);
                p[i][j]             = border || inner;
                p[i][size-7+j]      = border || inner;
                p[size-7+i][j]      = border || inner;
            }
        }
        // Data modules (pseudo-random)
        for (int i = 8; i < size - 8; i++) {
            for (int j = 8; j < size; j++) {
                p[i][j] = ((i * 3 + j * 7) % 5 < 2);
            }
        }
        return p;
    }
}