package hotel.ui.common;

import java.awt.*;
import javax.swing.*;

/*
 HeaderPanel.java
 -----------------
 Reusable header component for all dashboard screens.
 Uses light-mode color palette for consistency.
*/

public class HeaderPanel extends JPanel {

    // ── Light Mode Palette ─────────────────────────────────────────────────
    static final Color BG_TOPBAR = new Color(245, 245, 245);
    static final Color TXT_PRIMARY = new Color(20, 20, 20);
    static final Color TXT_SECONDARY = new Color(90, 90, 90);
    static final Color BORDER = new Color(220, 220, 220);

    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private String title;
    private String subtitle;

    public HeaderPanel(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        initUI();
    }

    private void initUI() {
        setLayout(null);
        setBackground(BG_TOPBAR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(F_TITLE);
        titleLabel.setForeground(TXT_PRIMARY);
        titleLabel.setBounds(20, 8, 300, 20);
        add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(F_SMALL);
        subtitleLabel.setForeground(TXT_SECONDARY);
        subtitleLabel.setBounds(20, 28, 300, 16);
        add(subtitleLabel);
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
        repaint();
    }

    public void setSubtitle(String newSubtitle) {
        this.subtitle = newSubtitle;
        repaint();
    }
}
