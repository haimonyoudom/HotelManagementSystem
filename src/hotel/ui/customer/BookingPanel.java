package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.ChangeListener;

// BookingPanel — layout matches the Figma screenshot exactly, light mode.
//
// LEFT card (62%)  — BoxLayout Y_AXIS, NO scroll, NO photo:
//   "New Reservation" label
//   Check-in | Check-out  (GridLayout 1×2, fixed 56 px)
//   "Room Type" label
//   2×2 room grid         (GridLayout 2×2, fixed 136 px)
//   "Special requests" label + field (fixed 36 px)
//   Summary box           (GridLayout 4×2, fixed 92 px)
//   Confirm button        (fixed 40 px)
//
// RIGHT card (38%) — BoxLayout Y_AXIS:
//   "Booking Notes" title
//   Policies list
//   "Deposit options" header
//   20% / 30% boxes
//
// Key layout rule:  every fixed-height item uses sizeBox() so BoxLayout
// honours both the height cap AND fills the full available width.

public class BookingPanel {

    // ── Colors ─────────────────────────────────────────────────────────
    private static final Color GREEN       = new Color(0x22C55E);
    private static final Color GREEN_DIM   = new Color(0xEAF8EE);
    private static final Color C_INPUT_BG  = new Color(0xFFFFFF);
    private static final Color C_INPUT_BOR = new Color(0xDDDDDD);
    private static final Color C_SUMM_BG   = new Color(0xF0FFF4);
    private static final Color C_SEL_BG    = new Color(0xEAF8EE);
    private static final Color C_SEL_BOR   = new Color(0x22C55E);
    private static final Color C_UNSEL_BG  = new Color(0xFFFFFF);
    private static final Color C_UNSEL_BOR = new Color(0xDDDDDD);
    private static final Color C_DEP_BG    = new Color(0xEAF8EE);
    private static final Color C_CARD_BG   = new Color(0xFFFFFF);

    // ── Fonts ──────────────────────────────────────────────────────────
    private static final Font F_SECTION = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SUMM    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_CONFIRM = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_POLICY  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BOLD    = new Font("Segoe UI", Font.BOLD,  13);

    // ── Room data ──────────────────────────────────────────────────────
    private static final String[][] ROOMS = {
        {"Standard Twin",  "$85/night"},
        {"Deluxe King",    "$120/night"},
        {"Premier Suite",  "$160/night"},
        {"Family Room",    "$210/night"},
    };

    // ── State ──────────────────────────────────────────────────────────
    private static int       selectedRoomIndex = 0;
    private static JLabel[]  summaryValues     = new JLabel[4];
    private static JButton[] roomBtns          = new JButton[ROOMS.length];
    private static JSpinner  checkinField;
    private static JSpinner  checkoutField;

    // =========================================================================
    public static void build(JPanel panel) {
        panel.setBackground(BG_MAIN);
        panel.add(buildTopbar("BOOKINGS"), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BG_MAIN);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        panel.add(contentWrapper, BorderLayout.CENTER);

        JPanel splitPane = new JPanel(new GridBagLayout());
        splitPane.setBackground(BG_MAIN);
        contentWrapper.add(splitPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridy   = 0;

        JPanel formCard = makeCard(C_CARD_BG);
        formCard.setLayout(new BorderLayout());
        gbc.gridx   = 0;
        gbc.weightx = 0.62;
        gbc.insets  = new Insets(0, 0, 0, 12);
        splitPane.add(formCard, gbc);

        JPanel notesCard = makeCard(C_CARD_BG);
        notesCard.setLayout(new BorderLayout());
        gbc.gridx   = 1;
        gbc.weightx = 0.38;
        gbc.insets  = new Insets(0, 0, 0, 0);
        splitPane.add(notesCard, gbc);

        buildFormPanel(formCard);
        buildNotesPanel(notesCard);
    }

    // =========================================================================
    // FORM PANEL  — no photo, no scroll; matches Figma 1:1
    // =========================================================================
    private static void buildFormPanel(JPanel card) {
        // Use BorderLayout on the card; put the content panel in CENTER
        // so it fills the card but doesn't scroll.
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(C_CARD_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.add(inner, BorderLayout.CENTER);

        // ── "New Reservation" ─────────────────────────────────────────
        JLabel secTitle = lbl("New Reservation", F_SECTION, TXT_SECONDARY);
        secTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(secTitle);
        inner.add(Box.createVerticalStrut(12));

        // ── Check-in | Check-out ──────────────────────────────────────
        JSpinner ciField = makeDateSpinner();
        JSpinner coField = makeDateSpinner();

        JPanel ciCol = labeledField("Check-in",  makeDateField(ciField));
        JPanel coCol = labeledField("Check-out", makeDateField(coField));

        JPanel dateRow = new JPanel(new GridLayout(1, 2, 14, 0));
        dateRow.setBackground(C_CARD_BG);
        dateRow.add(ciCol);
        dateRow.add(coCol);
        inner.add(sizeBox(dateRow, 54));
        inner.add(Box.createVerticalStrut(14));

        // ── "Room Type" ───────────────────────────────────────────────
        JLabel roomLabel = lbl("Room Type", F_LABEL, TXT_PRIMARY);
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(roomLabel);
        inner.add(Box.createVerticalStrut(8));

        // 2×2 grid — each button is 60 px tall; 2 rows + 8 px gap = 128 px
        JLabel[] summVals = new JLabel[4];

        JPanel roomGrid = new JPanel(new GridLayout(2, 2, 10, 8));
        roomGrid.setBackground(C_CARD_BG);

        for (int i = 0; i < ROOMS.length; i++) {
            final int idx = i;
            JButton btn = makeRoomBtn(ROOMS[i][0], ROOMS[i][1], i == selectedRoomIndex);
            roomBtns[i] = btn;
            roomGrid.add(btn);
            btn.addActionListener(e -> {
                selectedRoomIndex = idx;
                for (int j = 0; j < ROOMS.length; j++)
                    setRoomBtnState(roomBtns[j], j == idx);
                if (summVals[0] != null) {
                    summVals[0].setText(ROOMS[idx][0]);
                    summVals[2].setText(ROOMS[idx][1]);
                    updateTotal(summVals, ciField, coField, ROOMS[idx][1]);
                }
            });
        }
        inner.add(sizeBox(roomGrid, 128));
        inner.add(Box.createVerticalStrut(14));

        // ── Special requests ──────────────────────────────────────────
        JLabel srLabel = lbl("Special requests", F_LABEL, TXT_PRIMARY);
        srLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(srLabel);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sizeBox(makeInputField("e.g. high floor, extra pillows..."), 36));
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

        String[] summLabels   = {"Room", "Duration", "Rate", "Total"};
        String[] summDefaults = {ROOMS[selectedRoomIndex][0], "— nights",
                                  ROOMS[selectedRoomIndex][1], "—"};
        for (int i = 0; i < 4; i++) {
            summBox.add(lbl(summLabels[i], F_SUMM, TXT_SECONDARY));
            summVals[i] = lbl(summDefaults[i], F_SUMM, BLUE);
            summVals[i].setHorizontalAlignment(SwingConstants.RIGHT);
            summBox.add(summVals[i]);
        }
        inner.add(sizeBox(summBox, 92));
        inner.add(Box.createVerticalStrut(14));

        // Wire date listeners
        ChangeListener dl = e ->
            updateTotal(summVals, ciField, coField, ROOMS[selectedRoomIndex][1]);
        ciField.addChangeListener(dl);
        coField.addChangeListener(dl);
        checkinField  = ciField;
        checkoutField = coField;
        summaryValues = summVals;

        // ── Confirm Booking ───────────────────────────────────────────
        JButton confirmBtn = new JButton("Confirm Booking") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_DIM : C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_INPUT_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setFont(F_CONFIRM);
        confirmBtn.setForeground(GREEN);
        confirmBtn.setContentAreaFilled(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> {
            String room = ROOMS[selectedRoomIndex][0];
            String rate = ROOMS[selectedRoomIndex][1];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ci = sdf.format((Date) ciField.getValue());
            String co = sdf.format((Date) coField.getValue());
            int baseRate = Integer.parseInt(rate.replace("$","").replace("/night",""));
            int nights = 1;
            try {
                long diff = ChronoUnit.DAYS.between(
                    toLocalDate((Date) ciField.getValue()),
                    toLocalDate((Date) coField.getValue()));
                if (diff > 0) nights = (int) diff;
            } catch (Exception ignored) {}
            String ref = "#BK-" + (System.currentTimeMillis() % 100000);
            PaymentPanel.setBookingDetails(ref, room, ci, co, baseRate * nights);
            CustomerDashboard.switchTo("payment");
        });
        inner.add(sizeBox(confirmBtn, 40));

        // Vertical glue pushes content to top when card is taller than content
        inner.add(Box.createVerticalGlue());
    }

    // =========================================================================
    // NOTES PANEL (right card)
    // =========================================================================
    private static void buildNotesPanel(JPanel card) {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(C_CARD_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.add(inner, BorderLayout.CENTER);

        JLabel title = lbl("Booking Notes", F_LABEL, TXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(16));

        JLabel polHeader = lbl("Policies", F_BOLD, TXT_PRIMARY);
        polHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(polHeader);
        inner.add(Box.createVerticalStrut(10));

        for (String pol : new String[]{
                "Check-in from 2:00 PM",
                "Check-out by 12:00 PM",
                "Free cancellation up to 48h before arrival",
                "20% or 30% deposit required on confirmation"}) {
            JLabel pl = lbl("• " + pol, F_POLICY, TXT_SECONDARY);
            pl.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(pl);
            inner.add(Box.createVerticalStrut(5));
        }
        inner.add(Box.createVerticalStrut(20));

        JLabel depLabel = lbl("Deposit options", F_BOLD, GREEN);
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
     * sizeBox — wraps any component in a full-width, fixed-height BorderLayout
     * panel. BoxLayout sees the wrapper's fixed preferred/max height and the
     * wrapper expands to the full available width, so the child always gets
     * both dimensions correctly.
     */
    private static JPanel sizeBox(Component c, int height) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension d = new Dimension(Integer.MAX_VALUE, height);
        p.setMaximumSize(d);
        p.setPreferredSize(new Dimension(100, height)); // width hint; layout overrides it
        p.setMinimumSize(new Dimension(0, height));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /** Label stacked above a field component. */
    private static JPanel labeledField(String labelText, JComponent field) {
        JPanel col = new JPanel(new BorderLayout(0, 4));
        col.setBackground(C_CARD_BG);
        col.add(lbl(labelText, F_LABEL, TXT_PRIMARY), BorderLayout.NORTH);
        col.add(field, BorderLayout.CENTER);
        return col;
    }

    private static JPanel buildDepositBox(String pct, String labelText) {
        JPanel box = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_DEP_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JLabel pctLbl = lbl(pct, new Font("Segoe UI", Font.BOLD, 22), GREEN);
        pctLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(pctLbl);
        box.add(Box.createVerticalStrut(4));

        JLabel descLbl = lbl(labelText, new Font("Segoe UI", Font.PLAIN, 11), GREEN);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(descLbl);
        return box;
    }

    static JButton makeRoomBtn(String name, String price, boolean selected) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Boolean prop = (Boolean) getClientProperty("selected");
                boolean sel = prop != null ? prop : selected;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sel ? C_SEL_BG : C_UNSEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(sel ? C_SEL_BOR : C_UNSEL_BOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(TXT_PRIMARY);
                g2.drawString(name, 12, 26);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(TXT_SECONDARY);
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
        btn.putClientProperty("selected", selected);
        btn.repaint();
    }

    public static void selectBookingRoom(String roomName, String price) {
        for (int i = 0; i < ROOMS.length; i++)
            if (ROOMS[i][0].equals(roomName)) { selectedRoomIndex = i; break; }
        for (int i = 0; i < roomBtns.length; i++)
            if (roomBtns[i] != null) setRoomBtnState(roomBtns[i], i == selectedRoomIndex);
        if (summaryValues[0] != null) {
            summaryValues[0].setText(roomName);
            summaryValues[2].setText(price);
            updateTotal(summaryValues, checkinField, checkoutField, price);
        }
    }

    static void updateTotal(JLabel[] sv, JSpinner ci, JSpinner co, String priceStr) {
        try {
            long n = ChronoUnit.DAYS.between(toLocalDate((Date)ci.getValue()),
                                             toLocalDate((Date)co.getValue()));
            if (n <= 0) { sv[1].setText("— nights"); sv[3].setText("—"); return; }
            int rate = Integer.parseInt(priceStr.replace("$","").replace("/night",""));
            sv[1].setText(n + " nights");
            sv[3].setText("$" + (n * rate));
        } catch (Exception ignored) {}
    }

    private static LocalDate toLocalDate(Date d) {
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    static JSpinner makeDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null,
            java.util.Calendar.DAY_OF_MONTH);
        JSpinner sp = new JSpinner(model);
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
        sp.setEditor(ed);
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sp.setOpaque(false);
        sp.setBackground(C_INPUT_BG);
        JFormattedTextField tf = ed.getTextField();
        tf.setForeground(TXT_PRIMARY);
        tf.setBackground(C_INPUT_BG);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        tf.setOpaque(true);
        tf.setCaretColor(TXT_PRIMARY);
        Component ec = sp.getEditor();
        if (ec instanceof JComponent) { ((JComponent)ec).setOpaque(false); ((JComponent)ec).setBackground(C_INPUT_BG); }
        return sp;
    }

    static JPanel makeDateField(JSpinner spinner) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_INPUT_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.add(spinner, BorderLayout.CENTER);

        JButton calIcon = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0x2F343A));
                g2.fillRoundRect(4, 6, w-8, h-12, 6, 6);
                g2.setColor(new Color(0x5A6670));
                g2.fillRect(4, 6, w-8, 8);
                g2.setColor(new Color(240, 240, 240));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(8, 12, 12, 12);
                g2.drawLine(w-12, 12, w-8, 12);
                g2.fillOval(w/2-3, h/2-2, 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        calIcon.setPreferredSize(new Dimension(34, 36));
        calIcon.setOpaque(false);
        calIcon.setContentAreaFilled(false);
        calIcon.setBorderPainted(false);
        calIcon.setFocusPainted(false);
        calIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calIcon.addActionListener(e -> showCalendarPopup(spinner, calIcon));
        wrapper.add(calIcon, BorderLayout.EAST);
        return wrapper;
    }

    static void showCalendarPopup(JSpinner spinner, Component invoker) {
        LocalDate sel = toLocalDate((Date) spinner.getValue());
        int[] year  = { sel.getYear() };
        int[] month = { sel.getMonthValue() };

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(C_INPUT_BOR));

        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(TXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(6, 12, 4, 12));

        JButton prev = calNavBtn("<");
        JButton next = calNavBtn(">");

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(prev, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 4, 4));
        daysPanel.setBackground(Color.WHITE);
        daysPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        panel.add(daysPanel, BorderLayout.CENTER);

        Runnable refresh = () -> {
            YearMonth ym = YearMonth.of(year[0], month[0]);
            String mn = ym.getMonth().name();
            title.setText(mn.charAt(0) + mn.substring(1).toLowerCase() + " " + ym.getYear());
            daysPanel.removeAll();
            for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setForeground(TXT_MUTED);
                daysPanel.add(l);
            }
            int offset = ym.atDay(1).getDayOfWeek().getValue() % 7;
            for (int i = 0; i < offset; i++) daysPanel.add(new JLabel(""));
            for (int d = 1; d <= ym.lengthOfMonth(); d++) {
                LocalDate date = ym.atDay(d);
                JButton db = new JButton(String.valueOf(d));
                db.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                db.setForeground(date.equals(sel) ? GREEN : TXT_PRIMARY);
                db.setOpaque(false);
                db.setContentAreaFilled(false);
                db.setFocusPainted(false);
                if (date.equals(sel))
                    db.setBorder(BorderFactory.createLineBorder(GREEN));
                else
                    db.setBorderPainted(false);
                db.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                db.addActionListener(ae -> {
                    spinner.setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    popup.setVisible(false);
                });
                daysPanel.add(db);
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        };

        prev.addActionListener(e -> { if (--month[0] < 1)  { month[0]=12; year[0]--; } refresh.run(); });
        next.addActionListener(e -> { if (++month[0] > 12) { month[0]=1;  year[0]++; } refresh.run(); });
        refresh.run();
        popup.add(panel);
        popup.show(invoker, 0, invoker.getHeight());
    }

    private static JButton calNavBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(TXT_PRIMARY);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static ImageIcon loadRoomIcon(String path, int w, int h) {
        try {
            java.net.URL url = BookingPanel.class.getResource(path);
            Image img = url != null ? new ImageIcon(url).getImage() : new ImageIcon(path).getImage();
            if (img != null) return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception ignored) {}
        return null;
    }

    static JTextField makeInputField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_INPUT_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setText(placeholder);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TXT_SECONDARY);
        tf.setCaretColor(TXT_PRIMARY);
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TXT_PRIMARY); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(TXT_SECONDARY); }
            }
        });
        return tf;
    }

    static JPanel makeCard(Color bg) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0xDDDDDD));
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