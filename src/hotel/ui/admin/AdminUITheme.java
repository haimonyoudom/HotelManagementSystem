package hotel.ui.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Admin-only UI theme — blue & light mode.
 * Drop-in replacement for hotel.ui.common.UITheme inside admin panels.
 */
public class AdminUITheme {

    // ── Palette ──────────────────────────────────────────────
    public static final Color PRIMARY    = new Color(0x0A1F5C); // navy blue
    public static final Color SECONDARY  = new Color(0x1A3A8A); // navy blue light
    public static final Color SUCCESS    = new Color(0x2E7D32);
    public static final Color DANGER     = new Color(0xC62828);
    public static final Color WARNING    = new Color(0xF57F17);

    public static final Color BG         = new Color(0xF0F4FB);
    public static final Color CARD_BG    = Color.WHITE;
    public static final Color DIVIDER    = new Color(0xDCE4F5);
    public static final Color INPUT_BG   = new Color(0xF8FAFF);
    public static final Color INPUT_BORDER = new Color(0xBBDEFB);

    public static final Color TEXT       = new Color(0x1A237E);
    public static final Color TEXT_MUTED = new Color(0x546E7A);
    public static final Color TEXT_WHITE = Color.WHITE;

    public static final Color TABLE_HDR  = new Color(0x0A1F5C); // navy blue
    public static final Color TABLE_ALT  = new Color(0xE8F0FE);
    public static final Color TABLE_SEL  = new Color(0xBBDEFB);

    // ── Fonts ─────────────────────────────────────────────────
    public static final Font UI_FONT     = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD,   16);
    public static final Font SMALL_FONT  = new Font("Segoe UI", Font.PLAIN,  11);

    public static void applyGlobalFont() {
        // no-op; kept for compatibility if called
    }

    // ── Factories matching sample's UITheme API ───────────────

    /** White rounded card panel */
    public static JPanel cardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }

    /** Page wrapper with BG colour + padding */
    public static JPanel pagePanel() {
        JPanel p = new JPanel(new BorderLayout(16, 16));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        return p;
    }

    /** Scroll pane with themed border */
    public static JScrollPane scroll(JTable table) {
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        sp.getViewport().setBackground(CARD_BG);
        return sp;
    }

    /** Styled text field */
    public static JTextField textField() {
        JTextField f = new JTextField();
        f.setFont(UI_FONT);
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    /** Styled password field */
    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(UI_FONT);
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    /** Styled combo box */
    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(UI_FONT);
        c.setBackground(INPUT_BG);
        return c;
    }

    /** Heading label */
    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(HEADER_FONT);
        l.setForeground(TEXT);
        return l;
    }

    /** Muted secondary label */
    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL_FONT);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    /** Primary (blue) button */
    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY, TEXT_WHITE);
    }

    /** Secondary (ghost) button */
    public static JButton secondaryButton(String text) {
        return styledButton(text, new Color(0xE3EAF8), PRIMARY);
    }

    /** Danger (red) button */
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, TEXT_WHITE);
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(130, 34));
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Apply blue theme styling to a JTable */
    public static void styleTable(JTable table) {
        table.setFont(UI_FONT);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SEL);
        table.setSelectionForeground(TEXT);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(TABLE_HDR);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (sel) { setBackground(TABLE_SEL); setForeground(TEXT); }
                else { setBackground(row % 2 == 0 ? CARD_BG : TABLE_ALT); setForeground(TEXT); }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    /** Coloured status badge renderer for table cells */
    public static javax.swing.table.TableCellRenderer statusRenderer() {
        return new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                String s = v == null ? "" : v.toString().toLowerCase();
                Color bg, fg;
                switch (s) {
                    case "confirmed","paid","available" -> { bg = new Color(0xE8F5E9); fg = SUCCESS; }
                    case "pending"                      -> { bg = new Color(0xFFFDE7); fg = WARNING; }
                    case "cancelled","refunded","failed" -> { bg = new Color(0xFFEBEE); fg = DANGER; }
                    default -> { bg = TABLE_ALT; fg = TEXT; }
                }
                JLabel lbl = new JLabel(v == null ? "" : v.toString(), SwingConstants.CENTER) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(sel ? TABLE_SEL : bg);
                        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(fg);
                lbl.setOpaque(false);
                return lbl;
            }
        };
    }
}