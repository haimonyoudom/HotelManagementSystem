package hotel.ui.customer;

import static hotel.ui.customer.CustomerDashboard.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BrowseRoomsPanel extends JPanel {

    // ── Data ──────────────────────────────────────────────────────────────────
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

    private static final String[] ROOM_IMAGES = {
        "/hotel/images/resources/hotel2.jpg",
        "/hotel/images/resources/hotel3.jpg",
        "/hotel/images/resources/hotel2.jpg",
        "/hotel/images/resources/hotel1.jpg",
    };

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_PAGE_BG  = new Color(250, 250, 250);
    private static final Color C_BOX_BG   = new Color(255, 255, 255);
    private static final Color C_CARD_BG  = new Color(255, 255, 255);
    private static final Color C_CARD_HOV = new Color(245, 245, 255);
    private static final Color C_BORDER   = new Color(230, 230, 230);
    private static final Color C_ORANGE   = new Color(249, 115,  22);
    private static final Color C_ORANGE_H = new Color(255, 240, 230);
    private static final Color C_BADGE_BG = new Color(255, 244, 230);
    private static final Color C_IMG_TOP  = new Color(245, 245, 255);
    private static final Color C_IMG_BOT  = new Color(250, 250, 255);
    private static final Color C_TEXT     = new Color( 20,  20,  20);
    private static final Color C_GRAY     = new Color(100, 100, 100);
    private static final Color C_MUTED    = new Color(140, 140, 140);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_COUNT = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_CHIP  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_CNAME = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_CDESC = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_PRICE = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_BADGE = new Font("Segoe UI", Font.BOLD,   9);
    private static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  12);

    // ── Card dimensions ───────────────────────────────────────────────────────
    private static final int COLS       = 4;
    private static final int CARD_H     = 238;
    private static final int MIN_CARD_W = 260;
    private static final int IMG_H      = 118;
    private static final int GAP        = 12;

    // ── State ─────────────────────────────────────────────────────────────────
    private String       activeFilter = "All Types";
    private JPanel       cardsPanel;
    private JLabel       countLbl;
    private FilterChip[] chips;

    // =========================================================================
    public BrowseRoomsPanel() {
        // This panel IS the card — BorderLayout with topbar NORTH, content CENTER
        setLayout(new BorderLayout());
        setBackground(C_PAGE_BG);
        build();
    }

    // ── Build the panel ───────────────────────────────────────────────────────
    private void build() {
        // NORTH: topbar (reuses CustomerDashboard.buildTopbar)
        add(buildTopbar("ROOMS"), BorderLayout.NORTH);

        // CENTER: scrollable content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(C_PAGE_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        add(contentWrapper, BorderLayout.CENTER);

        // Inner box with rounded border paint
        JPanel box = new JPanel(new BorderLayout(0, 14)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BOX_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        contentWrapper.add(box, BorderLayout.CENTER);

        // ── Header section (title + subtitle + count + filter chips) ──────────
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = makeLabel("Rooms", F_TITLE, C_ORANGE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));

        JLabel sub = makeLabel("Find your perfect room", F_SUB, C_GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(sub);
        header.add(Box.createVerticalStrut(4));

        countLbl = makeLabel(countText(), F_COUNT, C_MUTED);
        countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(countLbl);
        header.add(Box.createVerticalStrut(12));

        // Filter chips row (FlowLayout so they wrap naturally)
        JPanel chipsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chipsRow.setOpaque(false);
        chipsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chips = new FilterChip[FILTERS.length];
        FontMetrics fm = getFontMetrics(F_CHIP);
        for (int i = 0; i < FILTERS.length; i++) {
            final String f = FILTERS[i];
            FilterChip chip = new FilterChip(f, f.equals(activeFilter));
            chip.setPreferredSize(new Dimension(fm.stringWidth(f) + 36, 26));
            chip.addActionListener(e -> onFilter(f));
            chips[i] = chip;
            chipsRow.add(chip);
        }
        header.add(chipsRow);

        box.add(header, BorderLayout.NORTH);

        // ── Cards area (GridLayout, inside a scroll pane) ─────────────────────
        cardsPanel = new JPanel(new GridLayout(0, COLS, GAP, GAP));
        cardsPanel.setOpaque(false);

        // Wrap in a fixed-height panel so cards don't stretch vertically
        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(cardsWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        box.add(scroll, BorderLayout.CENTER);

        renderCards();
    }

    // ── Render room cards ─────────────────────────────────────────────────────
    private void renderCards() {
        cardsPanel.removeAll();

        List<Integer> visible = new ArrayList<>();
        for (int i = 0; i < ROOMS.length; i++) {
            if (activeFilter.equals("All Types") || ((String) ROOMS[i][1]).equals(activeFilter))
                visible.add(i);
        }

        if (visible.isEmpty()) {
            cardsPanel.setLayout(new BorderLayout());
            JLabel empty = makeLabel("No rooms match \"" + activeFilter + "\"", F_SUB, C_MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            cardsPanel.add(empty, BorderLayout.CENTER);
        } else {
            cardsPanel.setLayout(new GridLayout(0, COLS, GAP, GAP));
            for (int idx : visible) {
                Object[] r = ROOMS[idx];
                cardsPanel.add(buildCard(
                    (String) r[0], (String) r[2],
                    (String) r[3], (String) r[4],
                    ROOM_IMAGES[idx]
                ));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // ── Single room card ──────────────────────────────────────────────────────
    private JPanel buildCard(String name, String desc, String price, String badge, String imagePath) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            private boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? C_CARD_HOV : C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hov ? C_ORANGE : C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(MIN_CARD_W, CARD_H));

        // ── Image area ────────────────────────────────────────────────────────
        JPanel img = new JPanel(new BorderLayout()) {
            private final Image photo = loadRoomImage(imagePath);
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (photo != null) {
                    g2.drawImage(photo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, C_IMG_TOP, 0, getHeight(), C_IMG_BOT);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() + 10, 10, 10);
                }
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setPreferredSize(new Dimension(0, IMG_H));
        img.setMinimumSize(new Dimension(0, IMG_H));
        img.setMaximumSize(new Dimension(Integer.MAX_VALUE, IMG_H));

        // Badge in image top-right
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        badgeWrap.setOpaque(false);
        img.add(badgeWrap, BorderLayout.NORTH);

        FontMetrics bfm = getFontMetrics(F_BADGE);
        JLabel bdg = new JLabel(badge, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_ORANGE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bdg.setFont(F_BADGE);
        bdg.setForeground(C_ORANGE);
        bdg.setOpaque(false);
        bdg.setPreferredSize(new Dimension(bfm.stringWidth(badge) + 14, 16));
        badgeWrap.add(bdg);

        card.add(img, BorderLayout.NORTH);

        // ── Info area (BoxLayout Y_AXIS with padding) ─────────────────────────
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel nameLbl = makeLabel(name,  F_CNAME, C_TEXT);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(nameLbl);
        info.add(Box.createVerticalStrut(4));

        JLabel descLbl = makeLabel(desc,  F_CDESC, C_GRAY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(descLbl);
        info.add(Box.createVerticalStrut(8));

        JLabel priceLbl = makeLabel(price, F_PRICE, C_ORANGE);
        priceLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(priceLbl);
        info.add(Box.createVerticalGlue());
        info.add(Box.createVerticalStrut(10));

        BookingBtn btn = new BookingBtn("Book Now");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.addActionListener(e -> {
            BookingPanel.selectBookingRoom(name, price);
            CustomerDashboard.switchTo("booking");
        });
        info.add(btn);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // ── Filter handler ────────────────────────────────────────────────────────
    private void onFilter(String f) {
        activeFilter = f;
        for (FilterChip c : chips)
            c.setActive(c.getText().equals(f));
        countLbl.setText(countText());
        renderCards();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static Image loadRoomImage(String path) {
        try {
            java.net.URL url = BrowseRoomsPanel.class.getResource(path);
            return url == null ? null : new ImageIcon(url).getImage();
        } catch (Exception e) { return null; }
    }

    private String countText() {
        int n = 0;
        for (Object[] r : ROOMS)
            if (activeFilter.equals("All Types") || ((String) r[1]).equals(activeFilter)) n++;
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

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(active ? C_ORANGE : C_CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            if (!active) {
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
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

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? C_ORANGE_H : C_ORANGE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}