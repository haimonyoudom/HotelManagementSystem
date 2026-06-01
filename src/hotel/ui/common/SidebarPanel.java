package hotel.ui.common;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SidebarPanel extends JPanel {
    private final Map<String, JButton> buttons = new LinkedHashMap<>();
    private String activeKey;

    public SidebarPanel(String appTitle) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UITheme.SIDEBAR);
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel title = new JLabel(appTitle);
        title.setFont(UITheme.HEADER_FONT);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(24));
    }

    public void addSection(String sectionTitle) {
        JLabel label = new JLabel(sectionTitle);
        label.setFont(UITheme.SMALL_FONT);
        label.setForeground(new Color(185, 190, 205));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(Box.createVerticalStrut(12));
        add(label);
        add(Box.createVerticalStrut(8));
    }

    public void addNavigationButton(String key, String text, Runnable action) {
        JButton button = UITheme.sidebarButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {
            setActive(key);
            action.run();
        });

        buttons.put(key, button);
        add(button);
        add(Box.createVerticalStrut(8));

        if (activeKey == null) {
            setActive(key);
        }
    }

    public void addBottomGlue() {
        add(Box.createVerticalGlue());
    }

    public void setActive(String key) {
        activeKey = key;

        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            JButton button = entry.getValue();

            if (entry.getKey().equals(key)) {
                button.setBackground(UITheme.SIDEBAR_ACTIVE);
            } else {
                button.setBackground(UITheme.SIDEBAR);
            }
        }
    }
}