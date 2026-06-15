package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import javax.swing.*;

public class PaymentPanel {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color C_PAGE_BG  = new Color(250, 250, 250);
    private static final Color C_CARD_BG  = new Color(255, 255, 255);
    private static final Color C_CARD_BOR = new Color(220, 220, 220);
    private static final Color C_SUMM_BG  = new Color(240, 253, 244);
    private static final Color C_GREEN    = new Color( 34, 197,  94);
    private static final Color C_GREEN_DIM= new Color(220, 252, 231);
    private static final Color C_SEL_BG   = new Color(0xEAF8EE);
    private static final Color C_SEL_BOR  = new Color(0x22C55E);
    private static final Color C_UNSEL_BG = new Color(0xFFFFFF);
    private static final Color C_UNSEL_BOR= new Color(0xEEEEEE);
    private static final Color TXT_MAIN   = new Color( 20,  20,  20);
    private static final Color TXT_GRAY   = new Color(100, 100, 100);
    private static final Color TXT_MUTED  = new Color(150, 150, 150);

    // ── Fonts ──────────────────────────────────────────────────────────
    private static final Font F_SECTION = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_AMOUNT  = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_CONFIRM = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_SUMM    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_POLICY  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_DEP_PCT = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_DEP_LBL = new Font("Segoe UI", Font.PLAIN, 11);

    // ── QR image path — change this to point to your QR image ─────────
    // Place your QR PNG at this resource path (e.g. src/hotel/images/resources/qr.png)
    // or use an absolute path like "C:/path/to/qr.png"
    public static final String QR_IMAGE_PATH = "/hotel/images/resources/qr.png";

    // ── State ──────────────────────────────────────────────────────────
    private static int    depositPct     = 20;
    private static int    roomTotal      = 0;
    private static int    bookingId      = 0;
    private static String bookingRef     = "#BK-2026-0001";
    private static String bookingRoom    = "Room not selected";
    private static String bookingCheckin = "—";
    private static String bookingCheckout= "—";

    private static JLabel   scanSubLbl;
    private static JLabel   amountLbl;
    private static JLabel   amountSubLbl;
    private static JLabel[] summaryValues = new JLabel[4];
    private static JButton  btn20;
    private static JButton  btn30;
    private static JLabel   qrImageLabel; // holds the QR image

    // =========================================================================
    public static void build(JPanel panel) {
        panel.setBackground(C_PAGE_BG);
        panel.add(buildTopbar("PAYMENT"), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(C_PAGE_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        panel.add(contentWrapper, BorderLayout.CENTER);

        JPanel splitPane = new JPanel(new GridBagLayout());
        splitPane.setBackground(C_PAGE_BG);
        contentWrapper.add(splitPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridy   = 0;

        JPanel formCard = makeCard(C_CARD_BG, C_CARD_BOR);
        formCard.setLayout(new BorderLayout());
        gbc.gridx   = 0;
        gbc.weightx = 0.62;
        gbc.insets  = new Insets(0, 0, 0, 12);
        splitPane.add(formCard, gbc);

        JPanel notesCard = makeCard(C_CARD_BG, C_CARD_BOR);
        notesCard.setLayout(new BorderLayout());
        gbc.gridx   = 1;
        gbc.weightx = 0.38;
        gbc.insets  = new Insets(0, 0, 0, 0);
        splitPane.add(notesCard, gbc);

        buildFormCard(formCard);
        buildNotesCard(notesCard);
    }

    // =========================================================================
    // LEFT — FORM CARD
    // =========================================================================
    private static void buildFormCard(JPanel card) {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(C_CARD_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Anchor to NORTH so scroll pane scrolls rather than stretches content
        JPanel innerWrapper = new JPanel(new BorderLayout());
        innerWrapper.setBackground(C_CARD_BG);
        innerWrapper.add(inner, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(innerWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(scroll, BorderLayout.CENTER);

        // ── Section title + sub label ─────────────────────────────────
        JLabel secTitle = lbl("Scan to Pay Deposit", F_SECTION, TXT_GRAY);
        secTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(secTitle);
        inner.add(Box.createVerticalStrut(4));

        scanSubLbl = lbl("Booking " + bookingRef + " · " + bookingRoom, F_SMALL, TXT_MUTED);
        scanSubLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(scanSubLbl);
        inner.add(Box.createVerticalStrut(14));

        // ── Deposit toggle buttons (same style as BookingPanel room btns) ──
        JLabel depLabel = lbl("Select Deposit", F_LABEL, TXT_MAIN);
        depLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(depLabel);
        inner.add(Box.createVerticalStrut(8));

        JPanel depRow = new JPanel(new GridLayout(1, 2, 10, 0));
        depRow.setBackground(C_CARD_BG);
        btn20 = makeToggleBtn("20%", "20% Deposit", true);
        btn30 = makeToggleBtn("30%", "30% Deposit", false);
        btn20.addActionListener(e -> setDeposit(20));
        btn30.addActionListener(e -> setDeposit(30));
        depRow.add(btn20);
        depRow.add(btn30);
        inner.add(sizeBox(depRow, 60));
        inner.add(Box.createVerticalStrut(16));

        // ── QR image — centred via a FlowLayout wrapper ───────────────
        // To swap the QR: change QR_IMAGE_PATH at the top of this class,
        // or call PaymentPanel.setQRImage(myIcon) at runtime.
        qrImageLabel = new JLabel();
        qrImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        qrImageLabel.setOpaque(true);
        qrImageLabel.setBackground(new Color(248, 252, 248));
        qrImageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 235, 210), 1));

        // Try to load the image; show a placeholder text if not found
        loadQRImage(200, 200);

        // FlowLayout centres the label horizontally; sizeBox fixes the height
        JPanel qrFlow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        qrFlow.setBackground(C_CARD_BG);
        qrImageLabel.setPreferredSize(new Dimension(200, 200));
        qrFlow.add(qrImageLabel);
        inner.add(sizeBox(qrFlow, 210));
        inner.add(Box.createVerticalStrut(14));

        // ── Amount (centred to match QR) ──────────────────────────────
        amountLbl = lbl("$0.00", F_AMOUNT, C_GREEN);

        JPanel amountFlow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        amountFlow.setBackground(C_CARD_BG);
        amountFlow.add(amountLbl);
        inner.add(sizeBox(amountFlow, 40));
        inner.add(Box.createVerticalStrut(2));

        amountSubLbl = lbl(depositPct + "% of $" + roomTotal + " total", F_SMALL, TXT_MUTED);

        JPanel amountSubFlow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        amountSubFlow.setBackground(C_CARD_BG);
        amountSubFlow.add(amountSubLbl);
        inner.add(sizeBox(amountSubFlow, 18));
        inner.add(Box.createVerticalStrut(14));

        // ── Summary box ───────────────────────────────────────────────
        JPanel summBox = new JPanel(new GridLayout(4, 2, 0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SUMM_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        summBox.setOpaque(false);
        summBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        String[] summLabels   = {"Room", "Deposit", "Room Total", "Due Now"};
        String[] summDefaults = {bookingRoom, depositPct + "%", "$" + roomTotal, "$0"};
        for (int i = 0; i < 4; i++) {
            summBox.add(lbl(summLabels[i], F_SUMM, TXT_GRAY));
            summaryValues[i] = lbl(summDefaults[i], F_SUMM, BLUE);
            summaryValues[i].setHorizontalAlignment(SwingConstants.RIGHT);
            summBox.add(summaryValues[i]);
        }
        inner.add(sizeBox(summBox, 92));
        inner.add(Box.createVerticalStrut(14));

        // ── Mark as Paid button ───────────────────────────────────────
        JButton markBtn = new JButton("Mark as Paid") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_GREEN_DIM : C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_CARD_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        markBtn.setFont(F_CONFIRM);
        markBtn.setForeground(C_GREEN);
        markBtn.setContentAreaFilled(false);
        markBtn.setBorderPainted(false);
        markBtn.setFocusPainted(false);
        markBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        markBtn.addActionListener(e -> {
            try {
                if (bookingId <= 0) {
                    throw new IllegalStateException("No saved booking is selected.");
                }
                int amount = (int) Math.round(roomTotal * depositPct / 100.0);
                CustomerData.markBookingPaid(bookingId, amount);
                CustomerDashboard.refreshCustomerScreens();
                CustomerDashboard.switchTo("history");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(card, "Could not save payment: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inner.add(sizeBox(markBtn, 40));
        inner.add(Box.createVerticalStrut(8));
    }

    // =========================================================================
    // RIGHT — NOTES CARD
    // =========================================================================
    private static void buildNotesCard(JPanel card) {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(C_CARD_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.add(inner, BorderLayout.CENTER);

        JLabel title = lbl("Payment Notes", F_LABEL, TXT_MAIN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(16));

        JLabel polHeader = lbl("Policies", F_BOLD, TXT_MAIN);
        polHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(polHeader);
        inner.add(Box.createVerticalStrut(10));

        for (String pol : new String[]{
                "Check-in from 2:00 PM",
                "Check-out by 12:00 PM",
                "20% or 30% deposit on confirmation",
                "Remaining balance due at check-in",
                "Deposit non-refundable within 24h",
                "QR payment processed instantly"}) {
            JLabel pl = lbl("• " + pol, F_POLICY, TXT_GRAY);
            pl.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(pl);
            inner.add(Box.createVerticalStrut(5));
        }
        inner.add(Box.createVerticalStrut(20));

        JLabel depLabel = lbl("Deposit options", F_BOLD, C_GREEN);
        depLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(depLabel);
        inner.add(Box.createVerticalStrut(12));

        JPanel depRow = new JPanel(new GridLayout(1, 2, 12, 0));
        depRow.setBackground(C_CARD_BG);
        depRow.add(buildDepositBox("20%", "Basic deposit"));
        depRow.add(buildDepositBox("30%", "Full deposit"));
        inner.add(sizeBox(depRow, 90));
        inner.add(Box.createVerticalGlue());
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /**
     * Loads QR image from QR_IMAGE_PATH into qrImageLabel, scaled to size×size.
     * If the image is not found a placeholder text is shown instead.
     * You can also call setQRImage(ImageIcon) at runtime to swap the image.
     */
    private static void loadQRImage(int w, int h) {
        try {
            java.net.URL url = PaymentPanel.class.getResource(QR_IMAGE_PATH);
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
                qrImageLabel.setIcon(new ImageIcon(img));
                qrImageLabel.setText("");
                return;
            }
            // Try as a plain file path
            java.io.File f = new java.io.File(QR_IMAGE_PATH);
            if (f.exists()) {
                Image img = new ImageIcon(f.getAbsolutePath()).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
                qrImageLabel.setIcon(new ImageIcon(img));
                qrImageLabel.setText("");
                return;
            }
        } catch (Exception ignored) {}
        // Placeholder when no image found
        qrImageLabel.setIcon(null);
        qrImageLabel.setText("<html><center><font color='#22C55E'>QR Image<br>not found</font></center></html>");
        qrImageLabel.setFont(F_SMALL);
    }

    /**
     * Call this at runtime to swap the QR code image.
     * Example:  PaymentPanel.setQRImage(new ImageIcon("path/to/qr.png"));
     */
    public static void setQRImage(ImageIcon icon) {
        if (qrImageLabel != null) {
            if (icon != null) {
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                qrImageLabel.setIcon(new ImageIcon(scaled));
                qrImageLabel.setText("");
            } else {
                qrImageLabel.setIcon(null);
                qrImageLabel.setText("No QR image");
            }
        }
    }

    /** Fixed-height, full-width wrapper — the key BoxLayout sizing fix. */
    private static JPanel sizeBox(Component c, int height) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        p.setPreferredSize(new Dimension(100, height));
        p.setMinimumSize(new Dimension(0, height));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private static JPanel buildDepositBox(String pct, String labelText) {
        JPanel box = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_GREEN_DIM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JLabel pctLbl = lbl(pct, F_DEP_PCT, C_GREEN);
        pctLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(pctLbl);
        box.add(Box.createVerticalStrut(4));

        JLabel descLbl = lbl(labelText, F_DEP_LBL, C_GREEN);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(descLbl);
        return box;
    }

    private static JButton makeToggleBtn(String heading, String subText, boolean active) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                boolean act = Boolean.TRUE.equals(getClientProperty("active"));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(act ? C_SEL_BG  : C_UNSEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(act ? C_SEL_BOR : C_UNSEL_BOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(TXT_MAIN);
                g2.drawString(heading, 12, 26);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(TXT_GRAY);
                g2.drawString(subText, 12, 44);
                g2.dispose();
            }
        };
        btn.putClientProperty("active", active);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Live update ────────────────────────────────────────────────────
    static void setDeposit(int pct) {
        depositPct = pct;
        int amt    = (int) Math.round(roomTotal * pct / 100.0);

        if (btn20 != null) { btn20.putClientProperty("active", pct == 20); btn20.repaint(); }
        if (btn30 != null) { btn30.putClientProperty("active", pct == 30); btn30.repaint(); }

        if (amountLbl    != null) amountLbl.setText("$" + String.format("%.2f", (double) amt));
        if (amountSubLbl != null) amountSubLbl.setText(pct + "% of $" + roomTotal + " total");

        if (summaryValues[0] != null) summaryValues[0].setText(bookingRoom);
        if (summaryValues[1] != null) summaryValues[1].setText(pct + "%");
        if (summaryValues[2] != null) summaryValues[2].setText("$" + roomTotal);
        if (summaryValues[3] != null) summaryValues[3].setText("$" + amt);
    }

    public static void setBookingDetails(int savedBookingId, String ref, String room,
                                          String checkin, String checkout, int total) {
        bookingId        = savedBookingId;
        bookingRef      = ref;
        bookingRoom     = room;
        bookingCheckin  = (checkin  != null && !checkin.isEmpty())  ? checkin  : "—";
        bookingCheckout = (checkout != null && !checkout.isEmpty()) ? checkout : "—";
        roomTotal       = total;
        depositPct      = 20;

        if (scanSubLbl != null) scanSubLbl.setText("Booking " + ref + " · " + room);
        setDeposit(20);
    }

    public static void setBookingDetails(String ref, String room,
                                          String checkin, String checkout, int total) {
        setBookingDetails(0, ref, room, checkin, checkout, total);
    }

    private static JPanel makeCard(Color bg, Color border) {
        return new JPanel() {
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

    private static JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
}
