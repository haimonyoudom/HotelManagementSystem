package hotel.ui.common;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/*
 SidebarPanel.java
 ------------------
 Reusable sidebar component for all dashboard screens.
 Handles navigation menu with light-mode styling.
*/

public class SidebarPanel extends JPanel {

    // ── Light Mode Palette ─────────────────────────────────────────────────
    static final Color BG_SIDEBAR    = new Color(245, 245, 245);
    static final Color NAV_ACTIVE_BG   = new Color(220, 230, 255);
    static final Color NAV_ACTIVE_TEXT = new Color(37, 99, 235);
    static final Color NAV_HOVER_BG    = new Color(245, 245, 245);
    static final Color TXT_PRIMARY   = new Color(20, 20, 20);
    static final Color BORDER        = new Color(220, 220, 220);

    static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 13);

    public static final int SIDEBAR_W = 180;
    public static final int TOPBAR_H  = 56;

    private String[] items;
    private String[] screens;
    private String activeScreen;
    private Runnable onNavigate;

    public SidebarPanel(String[] items, String[] screens, String activeScreen, Runnable onNavigate) {
        this.items = items;
        this.screens = screens;
        this.activeScreen = activeScreen;
        this.onNavigate = onNavigate;
        initUI();
    }

    private void initUI() {
        setLayout(null);
        setBackground(BG_SIDEBAR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        for (int i = 0; i < items.length; i++) {
            final String screen = screens[i];
            JButton btn = new JButton(items[i]);
            btn.setBounds(10, 20 + i * 50, SIDEBAR_W - 20, 40);
            btn.setFont(F_REG);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);

            if (screen.equals(activeScreen)) {
                btn.setBackground(NAV_ACTIVE_BG);
                btn.setForeground(NAV_ACTIVE_TEXT);
            } else {
                btn.setBackground(BG_SIDEBAR);
                btn.setForeground(TXT_PRIMARY);
            }

            btn.addActionListener(e -> {
                if (onNavigate != null) onNavigate.run();
            });

            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!screen.equals(activeScreen)) {
                        btn.setBackground(NAV_HOVER_BG);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (!screen.equals(activeScreen)) {
                        btn.setBackground(BG_SIDEBAR);
                    }
                }
            });

            add(btn);
        }
    }

    public void setActiveScreen(String screen) {
        this.activeScreen = screen;
        repaint();
    }
}
