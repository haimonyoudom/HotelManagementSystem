package hotel.ui.common;

import hotel.model.User;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    public HeaderPanel(String title, User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)
        ));

        JLabel titleLabel = UITheme.title(title);
        add(titleLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel bell = new JLabel("🔔");
        bell.setFont(UITheme.HEADER_FONT);

        String initials = user == null || user.getUsername() == null || user.getUsername().isBlank()
                ? "U"
                : user.getUsername().substring(0, 1).toUpperCase();

        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(UITheme.PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(UITheme.HEADER_FONT);
        avatar.setPreferredSize(new Dimension(38, 38));

        JLabel username = new JLabel(user == null ? "Guest" : user.getUsername());
        username.setForeground(UITheme.TEXT);

        right.add(bell);
        right.add(avatar);
        right.add(username);

        add(right, BorderLayout.EAST);
    }
}