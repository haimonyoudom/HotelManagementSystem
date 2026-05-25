package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;

// BookingPanel: UI for creating a new room reservation.
// - Left panel: check-in/out date pickers, room type selector, special requests, summary, confirm button
// - Right panel: booking notes, policies, deposit options (20% / 30%)
// - Uses shared theme tokens from CustomerDashboard for consistent look.

public class BookingPanel extends JPanel {

    // ── Colors (green accent matching design) ─────────────────────────
    private static final Color GREEN        = new Color(0x22C55E);
    private static final Color GREEN_DIM    = new Color(0x14532D);
    private static final Color GREEN_DARK   = new Color(0x166534);
    private static final Color GREEN_HOVER  = new Color(0x16A34A);
    private static final Color C_INPUT_BG   = new Color(0x1C1C1E);
    private static final Color C_INPUT_BOR  = new Color(0x3A3A3C);
    private static final Color C_FORM_BG    = new Color(0x141414);
    private static final Color C_NOTES_BG   = new Color(0x141414);
    private static final Color C_SUMM_BG    = new Color(0x1A3A1A);
    private static final Color C_SEL_BG     = new Color(0x166534);
    private static final Color C_SEL_BOR    = new Color(0x22C55E);
    private static final Color C_UNSEL_BG   = new Color(0x1C2A1C);
    private static final Color C_UNSEL_BOR  = new Color(0x2D4A2D);
    private static final Color C_DEP_BG     = new Color(0x166534);
    private static final Color C_CONFIRM_BG = new Color(0x1C1C1E);
    private static final Color C_CONFIRM_BOR= new Color(0x3A3A3C);

    // ── Fonts ──────────────────────────────────────────────────────────
    private static final Font F_SECTION = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_RNAME   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_RPRICE  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_SUMM    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_CONFIRM = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_DEP_PCT = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_DEP_LBL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_POLICY  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BOLD    = new Font("Segoe UI", Font.BOLD,  13);

    // ── Room data ──────────────────────────────────────────────────────
    private static final String[][] ROOMS = {
        {"Standard Twin",    "89"},
        {"Deluxe King",      "149"},
        {"Suite Ocean View", "89"},
        {"Family Room",      "89"},
    };

    // ── Mutable state ──────────────────────────────────────────────────
    private int    selectedRoom  = 0;
    private String checkinStr    = "";
    private String checkoutStr   = "";
    private int    nights        = 0;

    private JLabel[] summaryValues = new JLabel[4]; // Room, Duration, Rate, Total
    private JButton[] roomBtns     = new JButton[4];

    // ── Date fields ────────────────────────────────────────────────────
    private JTextField checkinField;
    private JTextField checkoutField;

    // =========================================================================
    public BookingPanel() {
        setLayout(null);
        setBounds(0, 0, W, H);
        setBackground(new Color(18, 18, 18));
        setOpaque(true);
        build(this);
    }

    // =========================================================================
    // Called from CustomerDashboard — same pattern as leader
    // =========================================================================
    public static void build(JPanel panel) {
        addTopbar(panel, "Bookings", "Create or manage your booking");
        addSidebar(panel, "booking");

        int pad = 14;
        JPanel content = makeRoundPanel(new Color(18, 18, 18));
        content.setLayout(null);
        content.setBounds(CONTENT_X + pad, CONTENT_Y + pad,
                          CONTENT_W - pad * 2, CONTENT_H - pad * 2);
        content.setBorder(BorderFactory.createLineBorder(new Color(0x22C55E), 1));
        panel.add(content);

        int cw = content.getWidth();
        int ch = content.getHeight();
        int cx = 16, cy = 16;

        // ── LEFT FORM PANEL ───────────────────────────────────────────
        int formW = (int)(cw * 0.62);
        int notesW = cw - formW - cx * 3;

        JPanel formCard = makeCard(new Color(0x141414));
        formCard.setBounds(cx, cy, formW, ch - cy * 2);
        content.add(formCard);

        buildFormPanel(formCard, formW, ch - cy * 2);

        // ── RIGHT NOTES PANEL ─────────────────────────────────────────
        int rx = cx + formW + cx;
        JPanel notesCard = makeCard(new Color(0x141414));
        notesCard.setBounds(rx, cy, notesW, ch - cy * 2);
        content.add(notesCard);

        buildNotesPanel(notesCard, notesW);
    }

    // =========================================================================
    // FORM PANEL (left side)
    // =========================================================================
    static void buildFormPanel(JPanel card, int fw, int fh) {
        int px = 18, py = 16;
        int innerW = fw - px * 2;

        // Section title
        JLabel secTitle = lbl("New Reservation", F_SECTION, new Color(150, 150, 150));
        secTitle.setBounds(px, py, 200, 16);
        card.add(secTitle);
        py += 26;

        // ── Check-in / Check-out row ──────────────────────────────────
        JLabel ciLabel = lbl("Check-in", F_LABEL, new Color(200, 200, 200));
        ciLabel.setBounds(px, py, 120, 16);
        card.add(ciLabel);

        int halfW = (innerW - 14) / 2;
        JLabel coLabel = lbl("Check-out", F_LABEL, new Color(200, 200, 200));
        coLabel.setBounds(px + halfW + 14, py, 120, 16);
        card.add(coLabel);
        py += 20;

        JTextField ciField = makeInputField("dd/mm/yyyy");
        ciField.setBounds(px, py, halfW, 36);
        card.add(ciField);

        JTextField coField = makeInputField("dd/mm/yyyy");
        coField.setBounds(px + halfW + 14, py, halfW, 36);
        card.add(coField);

        py += 46;

        // ── Room type selector (2x2 grid) ─────────────────────────────
        String[][] rooms = {
            {"Standard Twin",    "$89/night"},
            {"Deluxe King",      "$149/night"},
            {"Suite Ocean View", "$89/night"},
            {"Family Room",      "$89/night"},
        };

        int[] selRoom = {0}; // track which room selected
        JButton[] roomBtns = new JButton[4];
        int rBtnW = (innerW - 10) / 2;
        int rBtnH = 60;

        // Summary labels for live update
        JLabel[] summVals = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            int col = i % 2;
            int row = i / 2;
            int bx = px + col * (rBtnW + 10);
            int by = py + row * (rBtnH + 8);

            boolean isFirst = (i == 0);
            JButton btn = makeRoomBtn(rooms[i][0], rooms[i][1], isFirst);
            btn.setBounds(bx, by, rBtnW, rBtnH);
            roomBtns[i] = btn;
            card.add(btn);

            btn.addActionListener(e -> {
                selRoom[0] = idx;
                for (int j = 0; j < 4; j++) {
                    setRoomBtnState(roomBtns[j], j == idx);
                }
                // Update summary
                if (summVals[0] != null) {
                    summVals[0].setText(rooms[idx][0]);
                    summVals[2].setText(rooms[idx][1]);
                    updateTotal(summVals, ciField, coField, rooms[idx][1]);
                }
            });
        }
        py += rBtnH * 2 + 8 * 2 + 10;

        // ── Special Requests ──────────────────────────────────────────
        JLabel srLabel = lbl("Special requests", F_LABEL, new Color(200, 200, 200));
        srLabel.setBounds(px, py, 200, 16);
        card.add(srLabel);
        py += 22;

        JTextField srField = makeInputField("e.g high floor, extra pillows...");
        srField.setBounds(px, py, innerW, 36);
        card.add(srField);
        py += 46;

        // ── Summary Box ───────────────────────────────────────────────
        JPanel summBox = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1A3A1A));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        summBox.setBounds(px, py, innerW, 84);
        summBox.setOpaque(false);
        card.add(summBox);

        String[] summLabels = {"Room", "Duration", "Rate", "Total"};
        String[] summDefaults = {"Standard Twin", "— nights", "$89/night", "—"};
        int sy = 10;
        for (int i = 0; i < 4; i++) {
            JLabel k = lbl(summLabels[i], F_SUMM, new Color(180, 220, 180));
            k.setBounds(12, sy, 100, 16);
            summBox.add(k);

            summVals[i] = lbl(summDefaults[i], F_SUMM, new Color(220, 255, 220));
            summVals[i].setHorizontalAlignment(SwingConstants.RIGHT);
            summVals[i].setBounds(innerW - 160, sy, 148, 16);
            summBox.add(summVals[i]);
            sy += 18;
        }

        // Wire date fields to update summary
        ActionListener dateListener = e -> updateTotal(summVals, ciField, coField, rooms[selRoom[0]][1]);
        ciField.addActionListener(dateListener);
        coField.addActionListener(dateListener);

        // Also update on focus lost
        FocusAdapter focusUpdate = new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                updateTotal(summVals, ciField, coField, rooms[selRoom[0]][1]);
            }
        };
        ciField.addFocusListener(focusUpdate);
        coField.addFocusListener(focusUpdate);

        py += 94;

        // ── Confirm Booking Button ────────────────────────────────────
        JButton confirmBtn = new JButton("Confirm Booking") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1C2A1C) : new Color(0x1C1C1E));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0x3A3A3C));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setBounds(px, py, innerW, 40);
        confirmBtn.setFont(F_CONFIRM);
        confirmBtn.setForeground(new Color(240, 240, 240));
        confirmBtn.setContentAreaFilled(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> CustomerDashboard.switchTo("payment"));
        card.add(confirmBtn);
    }

    // =========================================================================
    // NOTES PANEL (right side)
    // =========================================================================
    static void buildNotesPanel(JPanel card, int nw) {
        int px = 18, py = 18;
        int innerW = nw - px * 2;

        // Title
        JLabel title = lbl("Booking Notes", F_LABEL, new Color(200, 200, 200));
        title.setBounds(px, py, 200, 18);
        card.add(title);
        py += 30;

        // Policies header
        JLabel polHeader = lbl("Policies", F_BOLD, new Color(240, 240, 240));
        polHeader.setBounds(px, py, 200, 18);
        card.add(polHeader);
        py += 24;

        // Policy lines
        String[] policies = {
            "Check-in from 2:00 PM ·",
            "Check-out by 12:00 PM",
            "Free cancellation up to 48h",
            "before arrival",
            "20% or 30% deposit",
            "required on confirmation"
        };
        for (String pol : policies) {
            JLabel pl = lbl(pol, F_POLICY, new Color(180, 180, 180));
            pl.setBounds(px, py, innerW, 16);
            card.add(pl);
            py += 17;
        }
        py += 16;

        // Deposit options label
        JLabel depLabel = lbl("Deposit options", F_BOLD, new Color(0x22C55E));
        depLabel.setBounds(px, py, 200, 18);
        card.add(depLabel);
        py += 28;

        // 20% and 30% deposit boxes
        int depW = (innerW - 12) / 2;
        int depH = 90;

        addDepositBox(card, px, py, depW, depH, "20%", "Basic\ndeposit");
        addDepositBox(card, px + depW + 12, py, depW, depH, "30%", "Full\ndeposit");
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    static void addDepositBox(JPanel parent, int x, int y, int w, int h,
                               String pct, String labelText) {
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x166534));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setBounds(x, y, w, h);
        box.setOpaque(false);

        JLabel pctLbl = new JLabel(pct, SwingConstants.CENTER);
        pctLbl.setBounds(0, 14, w, 30);
        pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pctLbl.setForeground(new Color(0x22C55E));
        box.add(pctLbl);

        String[] parts = labelText.split("\n");
        int ly = 50;
        for (String part : parts) {
            JLabel ll = new JLabel(part, SwingConstants.CENTER);
            ll.setBounds(0, ly, w, 15);
            ll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            ll.setForeground(new Color(0x22C55E));
            box.add(ll);
            ly += 15;
        }

        parent.add(box);
    }

    static JButton makeRoomBtn(String name, String price, boolean selected) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Boolean prop = (Boolean) getClientProperty("selected");
                boolean sel = (prop != null) ? prop.booleanValue() : selected;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sel ? new Color(0x166534) : new Color(0x1C2A1C));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(sel ? new Color(0x22C55E) : new Color(0x2D4A2D));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                // Name
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(new Color(240, 240, 240));
                g2.drawString(name, 12, 26);
                // Price
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(180, 180, 180));
                g2.drawString(price, 12, 44);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static void setRoomBtnState(JButton btn, boolean selected) {
        // Trigger repaint with new selected state via client property
        btn.putClientProperty("selected", selected);
        btn.repaint();
    }

    static JTextField makeInputField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1C1C1E));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0x3A3A3C));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setText(placeholder);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(new Color(100, 100, 100));
        tf.setCaretColor(new Color(240, 240, 240));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        // Clear placeholder on focus
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(new Color(240, 240, 240));
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(new Color(100, 100, 100));
                }
            }
        });
        return tf;
    }

    static void updateTotal(JLabel[] summVals, JTextField ciField,
                            JTextField coField, String priceStr) {
        try {
            String ci = ciField.getText().trim();
            String co = coField.getText().trim();
            if (ci.isEmpty() || co.isEmpty() ||
                ci.equals("dd/mm/yyyy") || co.equals("dd/mm/yyyy")) return;

            String[] ciP = ci.split("/");
            String[] coP = co.split("/");
            if (ciP.length != 3 || coP.length != 3) return;

            LocalDate d1 = LocalDate.of(
                Integer.parseInt(ciP[2]),
                Integer.parseInt(ciP[1]),
                Integer.parseInt(ciP[0])
            );
            LocalDate d2 = LocalDate.of(
                Integer.parseInt(coP[2]),
                Integer.parseInt(coP[1]),
                Integer.parseInt(coP[0])
            );
            long n = ChronoUnit.DAYS.between(d1, d2);
            if (n <= 0) return;

            int rate = Integer.parseInt(priceStr.replace("$","").replace("/night",""));
            summVals[1].setText(n + " nights");
            summVals[3].setText("$" + (n * rate));
        } catch (Exception ignored) {}
    }

    static JPanel makeCard(Color bg) {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x2A2A2A));
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
}
