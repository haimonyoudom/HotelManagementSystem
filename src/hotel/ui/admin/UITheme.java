package hotel.ui.admin;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public final class UITheme {
    public static final Color BG = new Color(245, 246, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SIDEBAR = new Color(35, 39, 47);
    public static final Color SIDEBAR_ACTIVE = new Color(68, 64, 140);
    public static final Color PRIMARY = new Color(91, 128, 225);
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color SECONDARY = new Color(218, 143, 72);
    public static final Color SUCCESS = new Color(117, 211, 130);
    public static final Color DANGER = new Color(207, 75, 86);
    public static final Color TEXT = new Color(40, 40, 45);
    public static final Color MUTED = new Color(120, 125, 135);
    public static final Color BORDER = new Color(220, 223, 230);
    public static final Color CARD = Color.WHITE;

    public static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    private UITheme() {
    }

    public static void applyGlobalFont() {
        FontUIResource font = new FontUIResource(UI_FONT);
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }

        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }

    public static JButton createPrimaryButton(String text) {
        return primaryButton(text);
    }

    public static JLabel headerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(PRIMARY_DARK);
        return label;
    }

    public static JPanel card() {
        return cardPanel();
    }

    public static JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        return panel;
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SURFACE);
        panel.setBorder(compoundBorder());
        return panel;
    }

    public static JPanel cardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(compoundBorder());
        return panel;
    }

    public static Border compoundBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SMALL_FONT);
        label.setForeground(MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(new Color(238, 241, 247));
        button.setForeground(TEXT);
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static JButton sidebarButton(String text) {
        JButton button = baseButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(SIDEBAR);
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return button;
    }

    public static JButton activeSidebarButton(String text) {
        JButton button = sidebarButton(text);
        button.setBackground(SIDEBAR_ACTIVE);
        return button;
    }

    private static JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(UI_FONT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return button;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(UI_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UI_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JComboBox<String> comboBox(String... values) {
        JComboBox<String> box = new JComboBox<>(values);
        box.setFont(UI_FONT);
        box.setBackground(Color.WHITE);
        return box;
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
}