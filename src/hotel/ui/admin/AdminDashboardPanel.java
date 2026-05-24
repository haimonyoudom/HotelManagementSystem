package hotel.ui.admin;

import hotel.ui.util.UIConstants;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel{

    public AdminDashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.BG_DARK);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BG_DARK);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(UIConstants.FONT_SMALL);
        dateLabel.setForeground(UIConstants.TEXT_MUTED);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(UIConstants.BG_DARK);

        statsPanel.add(createStatCard("Total Rooms", "24"));
        statsPanel.add(createStatCard("Available", "18"));
        statsPanel.add(createStatCard("Total Customers", "156"));
        statsPanel.add(createStatCard("Total Staff", "12"));
        statsPanel.add(createStatCard("Revenue", "$45,230"));
        statsPanel.add(createStatCard("Occupancy Rate", "75%"));

        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(UIConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_SMALL);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}
