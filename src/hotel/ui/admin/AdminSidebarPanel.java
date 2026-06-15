package hotel.ui.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Admin-only sidebar matching the Figma design:
 * - White background with indigo border
 * - Bold indigo "HMS" title
 * - Gray section labels
 * - Thin outlined icons + label per nav item
 * - Active = filled indigo rounded pill, white text/icon
 * - Hover = light indigo tint
 */
public class AdminSidebarPanel extends JPanel {

    // ── Palette ───────────────────────────────────────────────
    private static final Color BG         = Color.WHITE;
    private static final Color BORDER_C   = new Color(0x0A1F5C);         // navy blue outline
    private static final Color TITLE_C    = new Color(0x0A1F5C);         // navy blue title
    private static final Color SECTION_C  = new Color(140, 140, 160);    // muted gray
    private static final Color ITEM_FG    = new Color(50,  50,  70);     // dark text inactive
    private static final Color ACTIVE_BG  = new Color(0x0A1F5C);         // navy blue pill
    private static final Color ACTIVE_FG  = Color.WHITE;
    private static final Color HOVER_BG   = new Color(0xE3EAF8);         // navy blue tint

    private final Map<String, NavButton> buttons = new LinkedHashMap<>();
    private String activeKey;

    public AdminSidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setPreferredSize(new Dimension(220, 0));

        // Indigo rounded border around the whole sidebar
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedLineBorder(BORDER_C, 2, 14),
            BorderFactory.createEmptyBorder(24, 16, 24, 16)
        ));

        // HMS title
        JLabel title = new JLabel("HMS");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TITLE_C);
        title.setAlignmentX(LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(22));
    }

    /** Add a gray section header label */
    public void addSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(SECTION_C);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 2, 4, 0));
        add(lbl);
    }

    /** Add a nav item with a built-in icon type + label */
    public void addNavItem(String key, IconType icon, String label, Runnable action) {
        NavButton btn = new NavButton(icon, label);
        btn.addActionListener(e -> { setActive(key); action.run(); });
        buttons.put(key, btn);
        add(Box.createVerticalStrut(2));
        add(btn);
        if (activeKey == null) setActive(key);
    }

    public void addGlue() { add(Box.createVerticalGlue()); }

    public void setActive(String key) {
        activeKey = key;
        buttons.forEach((k, btn) -> btn.setActive(k.equals(key)));
    }

    // ── Icon types ────────────────────────────────────────────
    public enum IconType {
        OVERVIEW, STAFF, CUSTOMERS, ROOMS,
        REPORTS, REVIEWS, INCOME, TRANSACTIONS,
        BOOKINGS, PAYMENTS, LOGOUT
    }

    // ── Nav button ────────────────────────────────────────────
    private static class NavButton extends JButton {
        private final IconType iconType;
        private boolean active   = false;
        private boolean hovered  = false;

        NavButton(IconType icon, String label) {
            super(label);
            this.iconType = icon;
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(14);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(LEFT_ALIGNMENT);

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            });
        }

        void setActive(boolean a) { active = a; setForeground(a ? ACTIVE_FG : ITEM_FG); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background pill
            if (active) {
                g2.setColor(ACTIVE_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            } else if (hovered) {
                g2.setColor(HOVER_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            }

            // Draw icon
            int iconSize = 18;
            int ix = 10;
            int iy = (getHeight() - iconSize) / 2;
            Color iconColor = active ? ACTIVE_FG : ITEM_FG;
            drawIcon(g2, iconType, ix, iy, iconSize, iconColor);

            g2.dispose();

            // Draw text (offset past icon)
            FontMetrics fm = getFontMetrics(getFont());
            int tx = ix + iconSize + 12;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(getForeground());
            g.setFont(getFont());
            g.drawString(getText(), tx, ty);
        }

        /** Draw thin outlined icons using Java2D — matches Figma style */
        private void drawIcon(Graphics2D g, IconType type, int x, int y, int s, Color c) {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {

                case OVERVIEW -> {
                    // Three-line list with dots
                    int dotR = 2;
                    for (int i = 0; i < 3; i++) {
                        int cy = y + 3 + i * 6;
                        g.fillOval(x, cy, dotR, dotR);
                        g.drawLine(x + dotR + 3, cy + 1, x + s, cy + 1);
                    }
                }

                case STAFF -> {
                    // Single person: head circle + body arc
                    int headR = s / 5;
                    int hx = x + s / 2, hy = y + headR + 1;
                    g.drawOval(hx - headR, hy - headR, headR * 2, headR * 2);
                    // shoulders arc
                    g.drawArc(x + 1, y + s / 2, s - 2, s / 2 + 2, 0, 180);
                }

                case CUSTOMERS -> {
                    // Two people
                    int r = s / 6;
                    // back person (right)
                    g.drawOval(x + s/2, y + 1, r*2, r*2);
                    g.drawArc(x + s/2 - 1, y + s/2 - 1, s/2 + 2, s/2, 0, 180);
                    // front person (left)
                    g.drawOval(x + s/5, y + 2, r*2, r*2);
                    g.drawArc(x, y + s/2, s/2 + 4, s/2, 0, 180);
                }

                case ROOMS -> {
                    // Simple building/box icon
                    int pad = 2;
                    g.drawRect(x + pad, y + pad, s - pad*2, s - pad*2);
                    // door
                    int dw = s/4, dh = s/3;
                    int dx = x + (s - dw)/2, dy = y + s - dh - pad;
                    g.drawRect(dx, dy, dw, dh);
                    // window
                    int ww = s/5;
                    g.drawRect(x + pad + 2, y + pad + 3, ww, ww);
                    g.drawRect(x + s - pad - ww - 2, y + pad + 3, ww, ww);
                }

                case REPORTS -> {
                    // Chart with upward line
                    g.drawRect(x, y + s/3, s, s - s/3);
                    // trend line
                    int[] px = {x+2, x+s/3, x+s*2/3, x+s-2};
                    int[] py = {y+s-4, y+s/2, y+s*2/3, y+2};
                    g.drawPolyline(px, py, 4);
                }

                case REVIEWS -> {
                    // Star outline
                    int cx = x + s/2, cy = y + s/2, r1 = s/2, r2 = s/4;
                    int pts = 5;
                    int[] starX = new int[pts*2], starY = new int[pts*2];
                    for (int i = 0; i < pts*2; i++) {
                        double angle = Math.PI/pts * i - Math.PI/2;
                        int r = (i%2==0) ? r1 : r2;
                        starX[i] = cx + (int)(r * Math.cos(angle));
                        starY[i] = cy + (int)(r * Math.sin(angle));
                    }
                    g.drawPolygon(starX, starY, pts*2);
                }

                case INCOME -> {
                    // Dollar sign in rounded square
                    int pad2 = 1;
                    g.drawRoundRect(x+pad2, y+pad2, s-pad2*2, s-pad2*2, 6, 6);
                    // $ symbol
                    int mx = x + s/2;
                    g.drawLine(mx, y+3, mx, y+s-3);
                    g.drawArc(mx-3, y+3, 6, 5, 0, 180);
                    g.drawArc(mx-3, y+s/2-2, 6, 5, 180, 180);
                }

                case TRANSACTIONS -> {
                    // Line chart in a box
                    g.drawRect(x, y + s/3, s, s*2/3);
                    int[] tx2 = {x+2, x+s/3, x+s*2/3, x+s-2};
                    int[] ty2 = {y+s-2, y+s/2+2, y+s*2/3, y+s/3+4};
                    g.drawPolyline(tx2, ty2, 4);
                    // arrow up
                    g.drawLine(x+s/2, y, x+s/2, y+s/3-2);
                    g.drawLine(x+s/2-3, y+4, x+s/2, y);
                    g.drawLine(x+s/2+3, y+4, x+s/2, y);
                }

                case BOOKINGS -> {
                    // Calendar icon
                    g.drawRect(x, y+3, s, s-3);
                    g.drawLine(x, y+8, x+s, y+8);
                    g.drawLine(x+s/3, y, x+s/3, y+6);
                    g.drawLine(x+s*2/3, y, x+s*2/3, y+6);
                    // dots
                    for (int row=0;row<2;row++) for(int col=0;col<3;col++) {
                        g.fillOval(x+3+col*(s/3), y+11+row*5, 2, 2);
                    }
                }

                case PAYMENTS -> {
                    // Credit card
                    g.drawRoundRect(x, y+3, s, s-6, 4, 4);
                    g.drawLine(x, y+8, x+s, y+8);
                    g.fillRect(x+3, y+12, s/3, 3);
                }

                case LOGOUT -> {
                    // Arrow out of box
                    int mid = y + s/2;
                    g.drawLine(x+s/3, mid, x+s, mid);
                    g.drawLine(x+s-5, mid-4, x+s, mid);
                    g.drawLine(x+s-5, mid+4, x+s, mid);
                    // box left side
                    g.drawLine(x+s/3, y+2, x, y+2);
                    g.drawLine(x, y+2, x, y+s-2);
                    g.drawLine(x, y+s-2, x+s/3, y+s-2);
                }
            }
        }
    }

    // ── Rounded border ────────────────────────────────────────
    private static class RoundedLineBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int   thickness, radius;

        RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color; this.thickness = thickness; this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x+1, y+1, w-2, h-2, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(thickness, thickness, thickness, thickness); }
    }
}