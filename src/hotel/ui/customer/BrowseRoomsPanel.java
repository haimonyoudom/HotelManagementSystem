package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

// BrowseRoomsPanel: UI for browsing/filtering room cards
// - Shows filter chips, room count, and a grid of room cards with booking actions.
// - Uses shared theme tokens from CustomerDashboard for consistent look.

public class BrowseRoomsPanel extends JPanel {

    // ── Data ─────────────────────────────────────────────────────────────────
    private static final String[] FILTERS = {
        "All Types", "Standard", "Deluxe", "Suite", "Family"
    };

    // { name, type, description, price, badge }
    private static final Object[][] ROOMS = {
        {"Deluxe King",   "Deluxe",   "Ocean view · 2 guests",  "$120/night", "Popular"},
        {"Premier Suite", "Suite",    "City view · 3 guests",   "$160/night", "Luxury"},
        {"Family Room",   "Family",   "2 bedrooms · 4 guests",  "$210/night", "Best Seller"},
        {"Standard Twin", "Standard", "City view · 2 guests",   "$85/night",  "Budget"},
    };

    // ── Palette ──────────────────────────────────────────────────────────────
private static final Color C_PAGE_BG  = new Color(18, 18, 18);   // BG_DARK
private static final Color C_BOX_BG   = new Color(20, 20, 40);   // dark navy box
private static final Color C_CARD_BG  = new Color(22, 22, 45);   // card bg
private static final Color C_CARD_HOV = new Color(30, 30, 60);   // BG_HOVER navy
private static final Color C_BORDER   = new Color(50, 50, 50);   // BORDER_COLOR
private static final Color C_ORANGE   = new Color(249, 115, 22); // orange accent
private static final Color C_ORANGE_H = new Color(220, 100, 15); // orange hover
private static final Color C_BADGE_BG = new Color(60, 25, 8);    // orange dim
private static final Color C_IMG_TOP  = new Color(30, 30, 60);   // card img top
private static final Color C_IMG_BOT  = new Color(18, 18, 35);   // card img bottom
private static final Color C_WHITE    = new Color(240, 240, 240); // TEXT_WHITE
private static final Color C_GRAY     = new Color(150, 150, 150); // TEXT_GRAY
private static final Color C_MUTED    = new Color(80, 80, 80);   // muted

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_COUNT = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_CHIP  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_CNAME = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_CDESC = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_PRICE = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_BADGE = new Font("Segoe UI", Font.BOLD,   9);
    private static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  12);

    // ── Layout constants ─────────────────────────────────────────────────────
    private static final int COLS   = 4;
    private static final int CARD_H = 238;
    private static final int IMG_H  = 118;
    private static final int GAP    = 12;
    private static final int PAD    = 20;

    // ── Mutable state ────────────────────────────────────────────────────────
    private String       activeFilter = "All Types";
    private JPanel       cardsPanel;
    private JLabel       countLbl;
    private FilterChip[] chips;

    // =========================================================================
    public BrowseRoomsPanel() {
        setLayout(null);
        setBounds(0, 0, W, H);
        setBackground(C_PAGE_BG);
        build();
    }

    // ── Build UI ─────────────────────────────────────────────────────────────
    private void build() {
        addTopbar(this, "Rooms", "Apr 29, 2026");
        addSidebar(this, "rooms");

        int cx = CONTENT_X + PAD;
        int cy = CONTENT_Y + PAD;
        int cw = CONTENT_W - PAD * 2;
        int ch = CONTENT_H - PAD * 2;

        // Outer rounded box
        JPanel content = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BOX_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setBounds(cx, cy, cw, ch);
        add(content);

        int iy = PAD;

        // Title
        JLabel title = makeLabel("Rooms", F_TITLE, C_ORANGE);
        title.setBounds(PAD, iy, 260, 24);
        content.add(title);

        // Subtitle
        JLabel sub = makeLabel("Find your perfect room", F_SUB, C_GRAY);
        sub.setBounds(PAD, iy + 27, 300, 16);
        content.add(sub);

        // Count
        countLbl = makeLabel(countText(), F_COUNT, C_MUTED);
        countLbl.setBounds(PAD, iy + 46, 220, 14);
        content.add(countLbl);

        iy += 72;

        // Filter chips
        chips = new FilterChip[FILTERS.length];
        int fx = PAD;
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(F_CHIP);
        for (int i = 0; i < FILTERS.length; i++) {
            final String f = FILTERS[i];
            FilterChip chip = new FilterChip(f, f.equals(activeFilter));
            int chipW = fm.stringWidth(f) + 36;
            chip.setBounds(fx, iy, chipW, 26);
            chip.addActionListener(e -> onFilter(f));
            chips[i] = chip;
            content.add(chip);
            fx += chipW + 8;
        }

        iy += 26 + 16;

        // Cards area
        int cardsW = cw - PAD * 2;
        int cardsH = ch - iy - PAD;
        cardsPanel = new JPanel(null);
        cardsPanel.setOpaque(false);
        cardsPanel.setBounds(PAD, iy, cardsW, cardsH);
        content.add(cardsPanel);

        renderCards();
    }

    // ── Render cards ─────────────────────────────────────────────────────────
    private void renderCards() {
        cardsPanel.removeAll();

        List<Object[]> visible = new ArrayList<>();
        for (Object[] r : ROOMS) {
            if (activeFilter.equals("All Types") || ((String) r[1]).equals(activeFilter)) {
                visible.add(r);
            }
        }

        if (visible.isEmpty()) {
            JLabel empty = makeLabel("No rooms match \"" + activeFilter + "\"", F_SUB, C_MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setBounds(0, 0, cardsPanel.getWidth(), cardsPanel.getHeight());
            cardsPanel.add(empty);
        } else {
            int totalGaps = GAP * (COLS - 1);
            int cardW = (cardsPanel.getWidth() - totalGaps) / COLS;

            int col = 0, row = 0;
            for (Object[] r : visible) {
                int x = col * (cardW + GAP);
                int y = row * (CARD_H + GAP);
                JPanel card = buildCard(
                    (String) r[0],
                    (String) r[2],
                    (String) r[3],
                    (String) r[4],
                    cardW
                );
                card.setBounds(x, y, cardW, CARD_H);
                cardsPanel.add(card);
                if (++col >= COLS) { col = 0; row++; }
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // ── Single card ───────────────────────────────────────────────────────────
    private JPanel buildCard(String name, String desc, String price, String badge, int cw) {
        JPanel card = new JPanel(null) {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? C_CARD_HOV : C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hov ? new Color(0x6B3A1A) : C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        // Image
        JPanel img = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_IMG_TOP, 0, getHeight(), C_IMG_BOT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 10, 10, 10);
                int iw = 30, ih = 22;
                int ix = (getWidth() - iw) / 2, iy2 = (getHeight() - ih) / 2;
                g2.setColor(new Color(0x505052));
                g2.fillRoundRect(ix, iy2, iw, ih, 4, 4);
                g2.setColor(new Color(0x636366));
                g2.fillOval(ix + 9, iy2 + 5, 12, 12);
                g2.setColor(C_IMG_BOT);
                g2.fillOval(ix + 12, iy2 + 8, 6, 6);
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setBounds(0, 0, cw, IMG_H);
        card.add(img);

        // Badge
        FontMetrics bfm = getFontMetrics(F_BADGE);
        int bw = bfm.stringWidth(badge) + 14;
        JLabel bdg = new JLabel(badge, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_ORANGE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bdg.setFont(F_BADGE);
        bdg.setForeground(C_ORANGE);
        bdg.setOpaque(false);
        bdg.setBounds(cw - bw - 7, 7, bw, 16);
        card.add(bdg);

        // Text info
        int ix = 10, iw = cw - 20;
        int infoY = IMG_H + 8;

        JLabel nameLbl = makeLabel(name,  F_CNAME, C_WHITE);
        nameLbl.setBounds(ix, infoY, iw, 17);
        card.add(nameLbl);

        JLabel descLbl = makeLabel(desc,  F_CDESC, C_GRAY);
        descLbl.setBounds(ix, infoY + 19, iw, 14);
        card.add(descLbl);

        JLabel priceLbl = makeLabel(price, F_PRICE, C_ORANGE);
        priceLbl.setBounds(ix, infoY + 36, iw, 18);
        card.add(priceLbl);

        // Booking button
        BookingBtn btn = new BookingBtn("Booking");
        btn.setBounds(ix, CARD_H - 36, iw, 28);
        card.add(btn);

        return card;
    }

    // ── Filter handler ────────────────────────────────────────────────────────
    private void onFilter(String f) {
        activeFilter = f;
        for (FilterChip c : chips) {
            c.setActive(c.getText().equals(f));
        }
        countLbl.setText(countText());
        renderCards();
    }

    private String countText() {
        int n = 0;
        for (Object[] r : ROOMS) {
            if (activeFilter.equals("All Types") || ((String) r[1]).equals(activeFilter)) n++;
        }
        return n + (n == 1 ? " available room" : " available rooms");
    }

    private static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Inner: FilterChip ─────────────────────────────────────────────────────
    private static class FilterChip extends JButton {
        private boolean active;

        FilterChip(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(F_CHIP);
            setForeground(active ? Color.WHITE : C_GRAY);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setActive(boolean a) {
            active = a;
            setForeground(a ? Color.WHITE : C_GRAY);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(active ? C_ORANGE : new Color(0x3A3A3C));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            if (!active) {
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Inner: BookingBtn ─────────────────────────────────────────────────────
    private static class BookingBtn extends JButton {
        BookingBtn(String text) {
            super(text);
            setFont(F_BTN);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? C_ORANGE_H : C_ORANGE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}