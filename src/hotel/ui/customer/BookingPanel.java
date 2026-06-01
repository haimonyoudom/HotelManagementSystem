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

// BookingPanel: UI for creating a new room reservation.
// - Left panel: check-in/out date pickers, room type selector, special requests, summary, confirm button
// - Right panel: booking notes, policies, deposit options (20% / 30%)
// - Uses shared theme tokens from CustomerDashboard for consistent look.

public class BookingPanel extends JPanel {

    // ── Colors (light-mode variants) ────────────────────────────────
    private static final Color GREEN        = new Color(0x22C55E);   // accent
    private static final Color GREEN_DIM    = new Color(0xEAF8EE);   // light green background
    private static final Color GREEN_DARK   = new Color(0x166534);
    private static final Color GREEN_HOVER  = new Color(0x16A34A);
    private static final Color C_INPUT_BG   = new Color(0xFFFFFF);   // white inputs
    private static final Color C_INPUT_BOR  = new Color(0xDDDDDD);   // light border
    private static final Color C_FORM_BG    = new Color(0xFFFFFF);   // form card (light)
    private static final Color C_NOTES_BG   = new Color(0xFAFAFA);   // notes background
    private static final Color C_SUMM_BG    = new Color(0xF0FFF4);   // pale green summary
    private static final Color C_SEL_BG     = new Color(0xEAF8EE);   // selected room background
    private static final Color C_SEL_BOR    = new Color(0x22C55E);   // selected border
    private static final Color C_UNSEL_BG   = new Color(0xFFFFFF);   // unselected background
    private static final Color C_UNSEL_BOR  = new Color(0xEEEEEE);   // unselected border
    private static final Color C_DEP_BG     = new Color(0xEAF8EE);
    private static final Color C_CONFIRM_BG = new Color(0xFFFFFF);
    private static final Color C_CONFIRM_BOR= new Color(0xDDDDDD);

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
        {"Standard Twin",    "$85/night"},
        {"Deluxe King",      "$120/night"},
        {"Premier Suite",    "$160/night"},
        {"Family Room",      "$210/night"},
    };

    // ── Mutable state ──────────────────────────────────────────────────
    private static int selectedRoomIndex = 0;
    private static JLabel[] summaryValues = new JLabel[4]; // Room, Duration, Rate, Total
    private static JButton[] roomBtns     = new JButton[4];
    private static JLabel roomPhotoLabel;

    private static final String[] ROOM_IMAGE_PATHS = {
        "/hotel/images/resources/hotel1.jpg",
        "/hotel/images/resources/hotel2.jpg",
        "/hotel/images/resources/hotel3.jpg",
        "/hotel/images/resources/hotel1.jpg",
    };

    // ── Date fields ────────────────────────────────────────────────────
    private static JSpinner checkinField;
    private static JSpinner checkoutField;

    // =========================================================================
    public BookingPanel() {
        setLayout(null);
        setBounds(0, 0, W, H);
        setBackground(BG_MAIN);
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
        JPanel content = makeRoundPanel(BG_MAIN);
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

        JPanel formCard = makeCard(BG_CARD);
        formCard.setBounds(cx, cy, formW, ch - cy * 2);
        content.add(formCard);

        buildFormPanel(formCard, formW, ch - cy * 2);

        // ── RIGHT NOTES PANEL ─────────────────────────────────────────
        int rx = cx + formW + cx;
        JPanel notesCard = makeCard(BG_CARD);
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
        JLabel secTitle = lbl("New Reservation", F_SECTION, TXT_SECONDARY);
        secTitle.setBounds(px, py, 200, 16);
        card.add(secTitle);
        py += 26;

        // ── Check-in / Check-out row ──────────────────────────────────
        JLabel ciLabel = lbl("Check-in", F_LABEL, TXT_PRIMARY);
        ciLabel.setBounds(px, py, 120, 16);
        card.add(ciLabel);

        int halfW = (innerW - 14) / 2;
        JLabel coLabel = lbl("Check-out", F_LABEL, TXT_PRIMARY);
        coLabel.setBounds(px + halfW + 14, py, 120, 16);
        card.add(coLabel);
        py += 20;

        JSpinner ciField = makeDateSpinner();
        JPanel ciFieldWrapper = makeDateField(ciField, halfW);
        ciFieldWrapper.setBounds(px, py, halfW, 36);
        card.add(ciFieldWrapper);

        JSpinner coField = makeDateSpinner();
        JPanel coFieldWrapper = makeDateField(coField, halfW);
        coFieldWrapper.setBounds(px + halfW + 14, py, halfW, 36);
        card.add(coFieldWrapper);

        py += 46;

        roomPhotoLabel = new JLabel();
        roomPhotoLabel.setBounds(px, py, innerW, 140);
        roomPhotoLabel.setOpaque(true);
        roomPhotoLabel.setBackground(BG_ROW);
        roomPhotoLabel.setIcon(loadRoomIcon(ROOM_IMAGE_PATHS[selectedRoomIndex], innerW, 140));
        roomPhotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(roomPhotoLabel);
        py += 150;

        // ── Room type selector (2x2 grid) ─────────────────────────────
        int rBtnW = (innerW - 10) / 2;
        int rBtnH = 60;

        // Summary labels for live update
        JLabel[] summVals = new JLabel[4];

        for (int i = 0; i < ROOMS.length; i++) {
            final int idx = i;
            int col = i % 2;
            int row = i / 2;
            int bx = px + col * (rBtnW + 10);
            int by = py + row * (rBtnH + 8);

            boolean selected = (i == selectedRoomIndex);
            JButton btn = makeRoomBtn(ROOMS[i][0], ROOMS[i][1], selected);
            btn.putClientProperty("selected", selected);
            btn.setBounds(bx, by, rBtnW, rBtnH);
            roomBtns[i] = btn;
            card.add(btn);

            btn.addActionListener(e -> {
                selectedRoomIndex = idx;
                for (int j = 0; j < ROOMS.length; j++) {
                    setRoomBtnState(roomBtns[j], j == idx);
                }
                if (roomPhotoLabel != null) {
                    roomPhotoLabel.setIcon(loadRoomIcon(ROOM_IMAGE_PATHS[selectedRoomIndex], roomPhotoLabel.getWidth(), roomPhotoLabel.getHeight()));
                }
                // Update summary
                if (summVals[0] != null) {
                    summVals[0].setText(ROOMS[idx][0]);
                    summVals[2].setText(ROOMS[idx][1]);
                    updateTotal(summVals, ciField, coField, ROOMS[idx][1]);
                }
            });
        }
        py += rBtnH * 2 + 8 * 2 + 10;

        // ── Special Requests ──────────────────────────────────────────
        JLabel srLabel = lbl("Special requests", F_LABEL, TXT_PRIMARY);
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
                g2.setColor(C_SUMM_BG);
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
            JLabel k = lbl(summLabels[i], F_SUMM, TXT_SECONDARY);
            k.setBounds(12, sy, 100, 16);
            summBox.add(k);

            summVals[i] = lbl(summDefaults[i], F_SUMM, BLUE);
            summVals[i].setHorizontalAlignment(SwingConstants.RIGHT);
            summVals[i].setBounds(innerW - 160, sy, 148, 16);
            summBox.add(summVals[i]);
            sy += 18;
        }

        // Wire date fields to update summary
        ChangeListener dateListener = e -> updateTotal(summVals, ciField, coField, ROOMS[selectedRoomIndex][1]);
        ciField.addChangeListener(dateListener);
        coField.addChangeListener(dateListener);

        checkinField = ciField;
        checkoutField = coField;
        summaryValues = summVals;
        BookingPanel.roomBtns = roomBtns;

        py += 94;

        // ── Confirm Booking Button ────────────────────────────────────
        JButton confirmBtn = new JButton("Confirm Booking") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_CONFIRM_BG : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_CONFIRM_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setBounds(px, py, innerW, 40);
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
            String ci = sdf.format((Date)ciField.getValue());
            String co = sdf.format((Date)coField.getValue());
            int baseRate = Integer.parseInt(rate.replace("$", "").replace("/night", ""));
            int nights = 1;
            try {
                LocalDate d1 = Instant.ofEpochMilli(((Date)ciField.getValue()).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate d2 = Instant.ofEpochMilli(((Date)coField.getValue()).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                long diff = ChronoUnit.DAYS.between(d1, d2);
                if (diff > 0) nights = (int) diff;
            } catch (Exception ignored) {
            }
            int total = baseRate * nights;
            String ref = "#BK-" + (System.currentTimeMillis() % 100000);
            PaymentPanel.setBookingDetails(ref, room, ci, co, total);
            CustomerDashboard.switchTo("payment");
        });
        card.add(confirmBtn);
    }

    // =========================================================================
    // NOTES PANEL (right side)
    // =========================================================================
    static void buildNotesPanel(JPanel card, int nw) {
        int px = 18, py = 18;
        int innerW = nw - px * 2;

        // Title
        JLabel title = lbl("Booking Notes", F_LABEL, TXT_PRIMARY);
        title.setBounds(px, py, 200, 18);
        card.add(title);
        py += 30;

        // Policies header
        JLabel polHeader = lbl("Policies", F_BOLD, TXT_PRIMARY);
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
            JLabel pl = lbl(pol, F_POLICY, TXT_SECONDARY);
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
                g2.setColor(C_DEP_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setBounds(x, y, w, h);
        box.setOpaque(false);

        JLabel pctLbl = new JLabel(pct, SwingConstants.CENTER);
        pctLbl.setBounds(0, 14, w, 30);
        pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pctLbl.setForeground(GREEN);
        box.add(pctLbl);

        String[] parts = labelText.split("\n");
        int ly = 50;
        for (String part : parts) {
            JLabel ll = new JLabel(part, SwingConstants.CENTER);
            ll.setBounds(0, ly, w, 15);
            ll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            ll.setForeground(GREEN);
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
                g2.setColor(sel ? C_SEL_BG : C_UNSEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(sel ? C_SEL_BOR : C_UNSEL_BOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                // Name
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(TXT_PRIMARY);
                g2.drawString(name, 12, 26);
                // Price
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
        // Trigger repaint with new selected state via client property
        btn.putClientProperty("selected", selected);
        btn.repaint();
    }

    static ImageIcon loadRoomIcon(String path, int width, int height) {
        Image image = null;
        try {
            java.net.URL url = BookingPanel.class.getResource(path);
            if (url != null) {
                image = new ImageIcon(url).getImage();
            } else {
                image = new ImageIcon(path).getImage();
            }
        } catch (Exception ignored) {
        }
        if (image != null) {
            return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }
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

    public static void selectBookingRoom(String roomName, String price) {
        int found = -1;
        for (int i = 0; i < ROOMS.length; i++) {
            if (ROOMS[i][0].equals(roomName)) {
                found = i;
                break;
            }
        }
        if (found >= 0) {
            selectedRoomIndex = found;
        }
        for (int i = 0; i < roomBtns.length; i++) {
            setRoomBtnState(roomBtns[i], i == selectedRoomIndex);
        }
if (roomPhotoLabel != null) {
                roomPhotoLabel.setIcon(loadRoomIcon(ROOM_IMAGE_PATHS[selectedRoomIndex], roomPhotoLabel.getWidth(), roomPhotoLabel.getHeight()));
            }
            if (summaryValues[0] != null) {
            summaryValues[0].setText(roomName);
            summaryValues[2].setText(price);
            updateTotal(summaryValues, checkinField, checkoutField, price);
        }
    }

    static void updateTotal(JLabel[] summVals, JSpinner ciField,
                            JSpinner coField, String priceStr) {
        try {
            Date d1 = (Date) ciField.getValue();
            Date d2 = (Date) coField.getValue();
            LocalDate ld1 = Instant.ofEpochMilli(d1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate ld2 = Instant.ofEpochMilli(d2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            long n = ChronoUnit.DAYS.between(ld1, ld2);
            if (n <= 0) {
                summVals[1].setText("— nights");
                summVals[3].setText("—");
                return;
            }
            int rate = Integer.parseInt(priceStr.replace("$","").replace("/night",""));
            summVals[1].setText(n + " nights");
            summVals[3].setText("$" + (n * rate));
        } catch (Exception ignored) {}
    }

    static JSpinner makeDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model) {
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
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setOpaque(false);
        spinner.setBackground(C_INPUT_BG);

        JFormattedTextField tf = editor.getTextField();
        tf.setForeground(TXT_PRIMARY);
        tf.setBackground(C_INPUT_BG);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        tf.setOpaque(true);
        tf.setCaretColor(TXT_PRIMARY);

        Component editorComp = spinner.getEditor();
        if (editorComp instanceof JComponent) {
            JComponent jc = (JComponent) editorComp;
            jc.setOpaque(false);
            jc.setBackground(C_INPUT_BG);
        }
        return spinner;
    }

    static JPanel makeDateField(JSpinner spinner, int width) {
        int iconW = 34;
        JPanel wrapper = new JPanel(null) {
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

        spinner.setBounds(0, 0, width - iconW - 4, 36);
        wrapper.add(spinner);

        JButton icon = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(0x2F343A));
                g2.fillRoundRect(6, 8, w - 12, h - 16, 6, 6);
                g2.setColor(new Color(0x5A6670));
                g2.fillRect(6, 8, w - 12, 8);
                g2.setColor(new Color(240, 240, 240));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(10, 14, 14, 14);
                g2.drawLine(w - 14, 14, w - 10, 14);
                g2.fillOval(w/2 - 5, h/2 - 3, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setBounds(width - iconW, 0, iconW, 36);
        icon.setOpaque(false);
        icon.setContentAreaFilled(false);
        icon.setBorderPainted(false);
        icon.setFocusPainted(false);
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icon.addActionListener(e -> showCalendarPopup(spinner, icon));
        wrapper.add(icon);

        return wrapper;
    }

    static void showCalendarPopup(JSpinner spinner, Component invoker) {
        LocalDate selectedDate = Instant.ofEpochMilli(((Date)spinner.getValue()).getTime())
            .atZone(ZoneId.systemDefault()).toLocalDate();
        int[] year = { selectedDate.getYear() };
        int[] month = { selectedDate.getMonthValue() };

        JPopupMenu popup = new JPopupMenu();
        popup.setOpaque(false);
        popup.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(TXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        for (JButton btn : new JButton[]{prev, next}) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(new Color(240, 240, 240));
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(prev, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 4, 4));
        daysPanel.setOpaque(false);
        panel.add(daysPanel, BorderLayout.CENTER);

        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                YearMonth yearMonth = YearMonth.of(year[0], month[0]);
                title.setText(yearMonth.getMonth().name().substring(0, 1)
                    + yearMonth.getMonth().name().substring(1).toLowerCase()
                    + " " + yearMonth.getYear());
                daysPanel.removeAll();

                String[] labels = {"Su","Mo","Tu","We","Th","Fr","Sa"};
                for (String label : labels) {
                    JLabel lbl = new JLabel(label, SwingConstants.CENTER);
                    lbl.setForeground(TXT_SECONDARY);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    daysPanel.add(lbl);
                }

                LocalDate firstOfMonth = yearMonth.atDay(1);
                int offset = firstOfMonth.getDayOfWeek().getValue() % 7;
                for (int i = 0; i < offset; i++) {
                    daysPanel.add(new JLabel(""));
                }

                int daysInMonth = yearMonth.lengthOfMonth();
                for (int d = 1; d <= daysInMonth; d++) {
                    LocalDate date = yearMonth.atDay(d);
                    JButton dayBtn = new JButton(String.valueOf(d));
                    dayBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    dayBtn.setForeground(new Color(240, 240, 240));
                    dayBtn.setOpaque(false);
                    dayBtn.setContentAreaFilled(false);
                    dayBtn.setBorderPainted(false);
                    dayBtn.setFocusPainted(false);
                    dayBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    if (date.equals(selectedDate)) {
                        dayBtn.setForeground(GREEN);
                        dayBtn.setBorder(BorderFactory.createLineBorder(GREEN));
                    }
                    dayBtn.addActionListener(ae -> {
                        spinner.setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        popup.setVisible(false);
                    });
                    daysPanel.add(dayBtn);
                }

                daysPanel.revalidate();
                daysPanel.repaint();
            }
        };

        prev.addActionListener(e -> {
            month[0]--;
            if (month[0] < 1) {
                month[0] = 12;
                year[0]--;
            }
            refresh.run();
        });
        next.addActionListener(e -> {
            month[0]++;
            if (month[0] > 12) {
                month[0] = 1;
                year[0]++;
            }
            refresh.run();
        });

        refresh.run();
        popup.add(panel);
        popup.show(invoker, 0, invoker.getHeight());
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
