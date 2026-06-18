package hotel.ui.admin;

import hotel.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin sidebar — Staff/Customer flat style with original Java2D drawn icons.
 *   • White background, 1px gray right matte border
 *   • Bold navy "HMS" brand label
 *   • Gray section headers
 *   • Flat nav buttons: Java2D icon + text
 *   • Active = light-blue bg + bold navy text + navy icon
 *   • User chip at bottom
 *   • Red logout button with top separator
 */
public class AdminSidebarPanel extends JPanel {

    // ── Palette ───────────────────────────────────────────────────────
    private static final Color WHITE_BG   = new Color(0xFF, 0xFF, 0xFF);
    private static final Color NAVY       = new Color(0x0A, 0x1F, 0x5C);
    private static final Color BORDER_C   = new Color(220, 220, 220);
    private static final Color HOVER_BG   = new Color(240, 240, 240);
    private static final Color ACTIVE_BG  = new Color(230, 240, 255);
    private static final Color TEXT_IDLE  = new Color(80, 80, 80);
    private static final Color TEXT_MUTED = new Color(130, 130, 130);
    private static final Color LOGOUT_FG  = new Color(180, 60, 60);
    private static final Color LOGOUT_HOV = new Color(255, 240, 240);

    public static final int SIDEBAR_W = 240;

    public enum IconType {
        OVERVIEW, STAFF, CUSTOMERS, ROOMS,
        REPORTS, REVIEWS, INCOME, TRANSACTIONS,
        BOOKINGS, PAYMENTS, LOGOUT
    }

    // ── State ─────────────────────────────────────────────────────────
    private final List<NavEntry> navEntries = new ArrayList<>();
    private NavEntry activeEntry = null;
    private User currentUser;

    // ── Constructor ───────────────────────────────────────────────────
    public AdminSidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(WHITE_BG);
        setPreferredSize(new Dimension(SIDEBAR_W, 0));
        setMinimumSize(new Dimension(SIDEBAR_W, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));

        add(Box.createVerticalStrut(24));
        JLabel hmsLabel = new JLabel("HMS");
        hmsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hmsLabel.setForeground(NAVY);
        hmsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hmsLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        add(hmsLabel);
        add(Box.createVerticalStrut(22));
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void addSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 18, 8, 0));
        add(lbl);
    }

    public void addNavItem(String key, IconType iconType, String label, Runnable action) {
        NavButton btn = new NavButton(iconType, label);
        NavEntry entry = new NavEntry(key, btn, action);
        navEntries.add(entry);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (entry != activeEntry) { btn.setHovered(true); }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (entry != activeEntry) { btn.setHovered(false); }
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                setActive(key);
                action.run();
            }
        });

        add(btn);
        if (activeEntry == null) setActive(key);
    }

    public void addGlue() { add(Box.createVerticalGlue()); }

    public void addLogout(Runnable logoutAction) {
        add(buildUserChip());
        add(buildLogoutButton(logoutAction));
    }

    public void setActive(String key) {
        for (NavEntry e : navEntries) {
            if (e.key.equals(key)) {
                if (activeEntry != null) activeEntry.btn.setActive(false);
                activeEntry = e;
                e.btn.setActive(true);
            }
        }
    }

    // ── NavButton (custom painted with Java2D icons) ──────────────────
    private static class NavButton extends JButton {
        private final IconType iconType;
        private boolean active  = false;
        private boolean hovered = false;

        NavButton(IconType iconType, String label) {
            super(label);
            this.iconType = iconType;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(80, 80, 80));
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        void setActive(boolean a) {
            active = a;
            hovered = false;
            setFont(new Font("Segoe UI", a ? Font.BOLD : Font.PLAIN, 13));
            setForeground(a ? NAVY : new Color(80, 80, 80));
            repaint();
        }

        void setHovered(boolean h) {
            hovered = h;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            if (active) {
                g2.setColor(ACTIVE_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            } else if (hovered) {
                g2.setColor(HOVER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g2.setColor(WHITE_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Icon
            int iconSize = 18;
            int ix = 18;
            int iy = (getHeight() - iconSize) / 2;
            Color iconColor = active ? NAVY : new Color(80, 80, 80);
            drawIcon(g2, iconType, ix, iy, iconSize, iconColor);

            g2.dispose();

            // Text (drawn manually to control position)
            FontMetrics fm = getFontMetrics(getFont());
            int tx = ix + iconSize + 12;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(getForeground());
            g.setFont(getFont());
            g.drawString(getText(), tx, ty);
        }

        private void drawIcon(Graphics2D g, IconType type, int x, int y, int s, Color c) {
            g.setColor(c);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case OVERVIEW -> {
                    int dotR = 2;
                    for (int i = 0; i < 3; i++) {
                        int cy = y + 3 + i * 6;
                        g.fillOval(x, cy, dotR, dotR);
                        g.drawLine(x + dotR + 3, cy + 1, x + s, cy + 1);
                    }
                }
                case STAFF -> {
                    int headR = s / 5;
                    int hx = x + s / 2, hy = y + headR + 1;
                    g.drawOval(hx - headR, hy - headR, headR * 2, headR * 2);
                    g.drawArc(x + 1, y + s / 2, s - 2, s / 2 + 2, 0, 180);
                }
                case CUSTOMERS -> {
                    int r = s / 6;
                    g.drawOval(x + s/2, y + 1, r*2, r*2);
                    g.drawArc(x + s/2 - 1, y + s/2 - 1, s/2 + 2, s/2, 0, 180);
                    g.drawOval(x + s/5, y + 2, r*2, r*2);
                    g.drawArc(x, y + s/2, s/2 + 4, s/2, 0, 180);
                }
                case ROOMS -> {
                    int pad = 2;
                    g.drawRect(x + pad, y + pad, s - pad*2, s - pad*2);
                    int dw = s/4, dh = s/3;
                    int dx = x + (s - dw)/2, dy = y + s - dh - pad;
                    g.drawRect(dx, dy, dw, dh);
                    int ww = s/5;
                    g.drawRect(x + pad + 2, y + pad + 3, ww, ww);
                    g.drawRect(x + s - pad - ww - 2, y + pad + 3, ww, ww);
                }
                case REPORTS -> {
                    g.drawRect(x, y + s/3, s, s - s/3);
                    int[] px = {x+2, x+s/3, x+s*2/3, x+s-2};
                    int[] py = {y+s-4, y+s/2, y+s*2/3, y+2};
                    g.drawPolyline(px, py, 4);
                }
                case REVIEWS -> {
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
                    int pad2 = 1;
                    g.drawRoundRect(x+pad2, y+pad2, s-pad2*2, s-pad2*2, 6, 6);
                    int mx = x + s/2;
                    g.drawLine(mx, y+3, mx, y+s-3);
                    g.drawArc(mx-3, y+3, 6, 5, 0, 180);
                    g.drawArc(mx-3, y+s/2-2, 6, 5, 180, 180);
                }
                case TRANSACTIONS -> {
                    g.drawRect(x, y + s/3, s, s*2/3);
                    int[] tx2 = {x+2, x+s/3, x+s*2/3, x+s-2};
                    int[] ty2 = {y+s-2, y+s/2+2, y+s*2/3, y+s/3+4};
                    g.drawPolyline(tx2, ty2, 4);
                    g.drawLine(x+s/2, y, x+s/2, y+s/3-2);
                    g.drawLine(x+s/2-3, y+4, x+s/2, y);
                    g.drawLine(x+s/2+3, y+4, x+s/2, y);
                }
                case BOOKINGS -> {
                    g.drawRect(x, y+3, s, s-3);
                    g.drawLine(x, y+8, x+s, y+8);
                    g.drawLine(x+s/3, y, x+s/3, y+6);
                    g.drawLine(x+s*2/3, y, x+s*2/3, y+6);
                    for (int row=0; row<2; row++)
                        for (int col=0; col<3; col++)
                            g.fillOval(x+3+col*(s/3), y+11+row*5, 2, 2);
                }
                case PAYMENTS -> {
                    g.drawRoundRect(x, y+3, s, s-6, 4, 4);
                    g.drawLine(x, y+8, x+s, y+8);
                    g.fillRect(x+3, y+12, s/3, 3);
                }
                case LOGOUT -> {
                    int mid = y + s/2;
                    g.drawLine(x+s/3, mid, x+s, mid);
                    g.drawLine(x+s-5, mid-4, x+s, mid);
                    g.drawLine(x+s-5, mid+4, x+s, mid);
                    g.drawLine(x+s/3, y+2, x, y+2);
                    g.drawLine(x, y+2, x, y+s-2);
                    g.drawLine(x, y+s-2, x+s/3, y+s-2);
                }
            }
        }
    }

    // ── User chip ─────────────────────────────────────────────────────
    private JPanel buildUserChip() {
        JPanel chip = new JPanel();
        chip.setBackground(new Color(245, 245, 245));
        chip.setLayout(new BoxLayout(chip, BoxLayout.X_AXIS));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        String initials = "AD";
        if (currentUser != null && currentUser.getUsername() != null && !currentUser.getUsername().isEmpty()) {
            String u = currentUser.getUsername().trim();
            initials = u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
        }
        final String finalInitials = initials;

        JLabel avatar = new JLabel(finalInitials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMinimumSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setOpaque(false);

        JPanel nameBox = new JPanel();
        nameBox.setOpaque(false);
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        String displayName = (currentUser != null && currentUser.getUsername() != null)
            ? currentUser.getUsername() : "Admin";
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(new Color(30, 30, 30));

        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(TEXT_MUTED);

        nameBox.add(nameLabel);
        nameBox.add(roleLabel);
        chip.add(avatar);
        chip.add(nameBox);
        return chip;
    }

    // ── Logout button ─────────────────────────────────────────────────
    private JButton buildLogoutButton(Runnable logoutAction) {
        // Custom painted logout button with Java2D icon
        JButton logoutBtn = new JButton("  Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();

                // Draw logout icon
                Graphics2D ig = (Graphics2D) g.create();
                ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ig.setColor(LOGOUT_FG);
                ig.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int s = 16, x = 18, y = (getHeight() - s) / 2;
                int mid = y + s/2;
                ig.drawLine(x+s/3, mid, x+s, mid);
                ig.drawLine(x+s-5, mid-4, x+s, mid);
                ig.drawLine(x+s-5, mid+4, x+s, mid);
                ig.drawLine(x+s/3, y+2, x, y+2);
                ig.drawLine(x, y+2, x, y+s-2);
                ig.drawLine(x, y+s-2, x+s/3, y+s-2);
                ig.dispose();

                FontMetrics fm = getFontMetrics(getFont());
                int tx = 18 + s + 12;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.setColor(LOGOUT_FG);
                g.setFont(getFont());
                g.drawString("Logout", tx, ty);
            }
        };

        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setForeground(LOGOUT_FG);
        logoutBtn.setBackground(WHITE_BG);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_C),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JButton finalLogoutBtn = logoutBtn;
        finalLogoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { finalLogoutBtn.setBackground(LOGOUT_HOV); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { finalLogoutBtn.setBackground(WHITE_BG);  }
        });

        finalLogoutBtn.addActionListener(e -> logoutAction.run());
        return finalLogoutBtn;
    }

    // ── NavEntry record ───────────────────────────────────────────────
    private static class NavEntry {
        final String key;
        final NavButton btn;
        final Runnable action;
        NavEntry(String key, NavButton btn, Runnable action) {
            this.key = key; this.btn = btn; this.action = action;
        }
    }
}