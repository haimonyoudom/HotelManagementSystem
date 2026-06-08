package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

// BookingHistoryPanel: shows past and current bookings in a filterable table.
// Layout mirrors BrowseRoomsPanel / CustomerDashboard:
//   JPanel (card)  -> BorderLayout
//     NORTH        -> topbar
//     CENTER       -> contentWrapper (padded BorderLayout)
//       CENTER     -> box (rounded card, BorderLayout)
//         NORTH    -> header (filter chips row)
//         CENTER   -> tableCard (BorderLayout)
//           NORTH  -> column headers
//           CENTER -> scrollable rows

public class BookingHistoryPanel {

    // ── Colors ─────────────────────────────────────────────────────────
    private static final Color C_BG         = BG_CONTENT;
    private static final Color C_CARD_BG    = BG_CARD;
    private static final Color C_CARD_BOR   = BORDER;
    private static final Color C_HEADER_BG  = BG_ELEVATED;
    private static final Color C_ROW_BG     = BG_CONTENT;
    private static final Color C_ROW_ALT    = new Color(240, 244, 255);
    private static final Color C_ROW_BOR    = BORDER;

    private static final Color C_CHIP_ACT_BG  = NAV_ACTIVE_BG;
    private static final Color C_CHIP_ACT_BOR = CustomerDashboard.NAVY;
    private static final Color C_CHIP_BG      = NAV_HOVER_BG;
    private static final Color C_CHIP_BOR     = BORDER;

    private static final Color BADGE_IN_BG      = TEAL_DIM;
    private static final Color BADGE_IN_FG      = TEAL;
    private static final Color BADGE_PENDING_BG = ORANGE_DIM;
    private static final Color BADGE_PENDING_FG = ORANGE;
    private static final Color BADGE_APPR_BG    = BLUE_DIM;
    private static final Color BADGE_APPR_FG    = BLUE;
    private static final Color BADGE_OUT_BG     = new Color(240, 240, 245);
    private static final Color BADGE_OUT_FG     = TXT_MUTED;
    private static final Color BADGE_CANC_BG    = new Color(255, 230, 230);
    private static final Color BADGE_CANC_FG    = new Color(180, 40, 40);

    private static final Color C_PRICE = ORANGE;

    // ── Fonts ──────────────────────────────────────────────────────────
    private static final Font F_CHIP   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_HEADER = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_RNAME  = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_RNUM   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_DATE   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_PRICE  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_BADGE  = new Font("Segoe UI", Font.BOLD,  11);

    // ── Booking data ───────────────────────────────────────────────────
    // { roomName, roomNum, checkIn, checkOut, price, status }
    private static final Object[][] ALL_BOOKINGS = {
        {"Deluxe King",     "Room 304", "Apr 28, 2026", "May 2, 2026",  "$596",   "Checked In"},
        {"Standard Twin",   "Room 112", "May 10, 2026", "May 13, 2026", "$267",   "Pending"},
        {"Suite Ocean View","Room 501", "Jun 1, 2026",  "Jun 7, 2026",  "$1,734", "Approved"},
        {"Family Room",     "Room 210", "Mar 15, 2026", "Mar 17, 2026", "$398",   "Checked Out"},
        {"Standard Queen",  "Room 108", "Feb 20, 2026", "Feb 22, 2026", "$198",   "Checked Out"},
        {"Deluxe Double",   "Room 215", "Jan 5, 2026",  "Jan 8, 2026",  "$507",   "Cancelled"},
    };

    private static final String[] FILTERS = {
        "All", "Pending", "Approved", "Checked In", "Checked Out", "Cancelled"
    };

    // ── Mutable state ──────────────────────────────────────────────────
    private static String      activeFilter = "All";
    private static JPanel      tableBody;
    private static FilterChip[] chips;

    // =========================================================================
    // Entry point — called from CustomerDashboard
    // =========================================================================
    public static void build(JPanel panel) {
        panel.setBackground(C_BG);

        // ── NORTH: topbar ─────────────────────────────────────────────
        panel.add(buildTopbar("BOOKING HISTORY"), BorderLayout.NORTH);

        // ── CENTER: padded content wrapper ────────────────────────────
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(C_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        panel.add(contentWrapper, BorderLayout.CENTER);

        // ── Outer rounded box (matches BrowseRoomsPanel's "box") ──────
        JPanel box = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_CARD_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        contentWrapper.add(box, BorderLayout.CENTER);

        // ── NORTH of box: filter chips ────────────────────────────────
        JPanel chipsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chipsRow.setOpaque(false);
        chipsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chips = new FilterChip[FILTERS.length];
        for (int i = 0; i < FILTERS.length; i++) {
            final String f = FILTERS[i];
            FilterChip chip = new FilterChip(f, f.equals(activeFilter));
            FontMetrics fm = chip.getFontMetrics(F_CHIP);
            chip.setPreferredSize(new Dimension(fm.stringWidth(f) + 32, 34));
            chips[i] = chip;
            chipsRow.add(chip);
            chip.addActionListener(e -> {
                activeFilter = f;
                for (FilterChip c : chips) c.setActive(c.getText().equals(f));
                renderRows();
            });
        }
        box.add(chipsRow, BorderLayout.NORTH);

        // ── CENTER of box: table card ─────────────────────────────────
        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_CARD_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        tableCard.setOpaque(false);
        box.add(tableCard, BorderLayout.CENTER);

        // ── NORTH of tableCard: column header row ─────────────────────
        tableCard.add(buildTableHeader(), BorderLayout.NORTH);

        // ── CENTER of tableCard: scrollable rows ──────────────────────
        tableBody = new JPanel();
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));
        tableBody.setOpaque(false);

        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.setOpaque(false);
        bodyWrapper.add(tableBody, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableCard.add(scroll, BorderLayout.CENTER);

        renderRows();
    }

    // =========================================================================
    // TABLE HEADER  (fixed, not scrolled)
    // Uses GridBagLayout to mirror the proportional column widths
    // =========================================================================
    private static JPanel buildTableHeader() {
        JPanel header = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_ROW_BOR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        header.setPreferredSize(new Dimension(0, 44));
        header.setMinimumSize(new Dimension(0, 44));

        // Column names + proportional weights matching the row renderer
        String[] cols    = {"Room",   "Check-in", "Check-out", "Price", "Status"};
        double[] weights = { 0.30,     0.16,        0.16,        0.12,    0.12  };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 0, 8);

        for (int i = 0; i < cols.length; i++) {
            gbc.gridx   = i;
            gbc.weightx = weights[i];
            JLabel h = new JLabel(cols[i]);
            h.setFont(F_HEADER);
            h.setForeground(TXT_SECONDARY);
            header.add(h, gbc);
        }
        // Spacer for remaining width
        gbc.gridx   = cols.length;
        gbc.weightx = 1.0 - sumWeights(weights);
        header.add(new JLabel(), gbc);

        return header;
    }

    // =========================================================================
    // TABLE ROWS
    // =========================================================================
    private static void renderRows() {
        if (tableBody == null) return;
        tableBody.removeAll();

        List<Object[]> visible = new ArrayList<>();
        for (Object[] b : ALL_BOOKINGS) {
            String status = (String) b[5];
            if (activeFilter.equals("All") || status.equals(activeFilter)) visible.add(b);
        }

        if (visible.isEmpty()) {
            JLabel empty = new JLabel("No bookings found for \"" + activeFilter + "\"",
                SwingConstants.CENTER);
            empty.setFont(F_DATE);
            empty.setForeground(TXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableBody.add(Box.createVerticalStrut(32));
            tableBody.add(empty);
        } else {
            for (int i = 0; i < visible.size(); i++) {
                tableBody.add(buildRow(visible.get(i), i % 2 == 1));
            }
        }

        tableBody.revalidate();
        tableBody.repaint();
    }

    private static JPanel buildRow(Object[] b, boolean alt) {
        String roomName = (String) b[0];
        String roomNum  = (String) b[1];
        String checkIn  = (String) b[2];
        String checkOut = (String) b[3];
        String price    = (String) b[4];
        String status   = (String) b[5];

        // Row panel: GridBagLayout with same column weights as header
        JPanel row = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(alt ? C_ROW_ALT : C_ROW_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_ROW_BOR);
                g2.drawLine(16, getHeight()-1, getWidth()-16, getHeight()-1);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        row.setPreferredSize(new Dimension(0, 64));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        double[] weights = { 0.30, 0.16, 0.16, 0.12, 0.12 };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Col 0: Room name + number stacked
        gbc.gridx   = 0;
        gbc.weightx = weights[0];
        JPanel roomCell = new JPanel();
        roomCell.setLayout(new BoxLayout(roomCell, BoxLayout.Y_AXIS));
        roomCell.setOpaque(false);
        JLabel rName = lbl(roomName, F_RNAME, TXT_PRIMARY);
        JLabel rNum  = lbl(roomNum,  F_RNUM,  TXT_MUTED);
        roomCell.add(rName);
        roomCell.add(Box.createVerticalStrut(2));
        roomCell.add(rNum);
        row.add(roomCell, gbc);

        // Col 1: Check-in
        gbc.gridx   = 1;
        gbc.weightx = weights[1];
        row.add(lbl(checkIn, F_DATE, TXT_SECONDARY), gbc);

        // Col 2: Check-out
        gbc.gridx   = 2;
        gbc.weightx = weights[2];
        row.add(lbl(checkOut, F_DATE, TXT_SECONDARY), gbc);

        // Col 3: Price
        gbc.gridx   = 3;
        gbc.weightx = weights[3];
        row.add(lbl(price, F_PRICE, C_PRICE), gbc);

        // Col 4: Status badge
        gbc.gridx   = 4;
        gbc.weightx = weights[4];
        Color[] bc = getBadgeColors(status);
        JLabel badge = new JLabel(status, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bc[0]);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(F_BADGE);
        badge.setForeground(bc[1]);
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(96, 26));
        badge.setMaximumSize(new Dimension(110, 26));
        row.add(badge, gbc);

        // Spacer
        gbc.gridx   = 5;
        gbc.weightx = 1.0 - sumWeights(weights);
        row.add(new JLabel(), gbc);

        return row;
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private static double sumWeights(double[] w) {
        double s = 0; for (double d : w) s += d; return s;
    }

    private static Color[] getBadgeColors(String status) {
        switch (status) {
            case "Checked In":  return new Color[]{BADGE_IN_BG,      BADGE_IN_FG};
            case "Pending":     return new Color[]{BADGE_PENDING_BG,  BADGE_PENDING_FG};
            case "Approved":    return new Color[]{BADGE_APPR_BG,     BADGE_APPR_FG};
            case "Checked Out": return new Color[]{BADGE_OUT_BG,      BADGE_OUT_FG};
            case "Cancelled":   return new Color[]{BADGE_CANC_BG,     BADGE_CANC_FG};
            default:            return new Color[]{BADGE_OUT_BG,      BADGE_OUT_FG};
        }
    }

    private static JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Inner: FilterChip ─────────────────────────────────────────────
    static class FilterChip extends JButton {
        private boolean active;

        FilterChip(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(F_CHIP);
            setForeground(active ? NAVY : TXT_SECONDARY);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setActive(boolean a) {
            active = a;
            setForeground(a ? NAVY : TXT_SECONDARY);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(C_CHIP_ACT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(C_CHIP_ACT_BOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            } else {
                g2.setColor(C_CHIP_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(C_CHIP_BOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}