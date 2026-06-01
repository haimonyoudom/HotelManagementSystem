package hotel.ui.staff.util;

import javax.swing.*;

import java.awt.*;

public class UIHelper {

    private UIHelper() {
    }

    public static JLabel styledLabel(String text, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, 20));
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel statusLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(UIConstants.FONT_SMALL);
        l.setForeground(UIConstants.ERR_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JTextField styledTextField() {
        JTextField f = new JTextField();
        LoginUIUtils.styleInput(f);
        return f;
    }

    public static JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        LoginUIUtils.styleInput(f);
        return f;
    }

    public static JPanel accentBar() {
        JPanel bar = new JPanel();
        bar.setBackground(UIConstants.ACCENT);
        bar.setMaximumSize(new Dimension(60, 3));
        bar.setPreferredSize(new Dimension(60, 3));
        bar.setMinimumSize(new Dimension(60, 3));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }
}
