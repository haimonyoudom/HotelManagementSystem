package hotel.ui.customer;

import java.awt.*;
import javax.swing.*;
import static hotel.ui.customer.CustomerDashboard.*;

public class BrowseRoomsPanel extends JPanel {

    private JPanel cardsPanel;
    private JLabel summaryLabel;
    private FilterButton[] filterButtons;
    private String activeFilter = "All Types";
    private final String[] roomFilters = {"All Types", "Standard", "Deluxe", "Suite", "Family"};
    private final Object[][] roomData = {
        {"Deluxe King", "Deluxe", "Ocean view · 2 guests", "$120/night", "Popular"},
        {"Premier Suite", "Suite", "City view · 3 guests", "$160/night", "Luxury"},
        {"Family Room", "Family", "2 bedrooms · 4 guests", "$210/night", "Best Seller"},
        {"Standard Twin", "Standard", "City view · 2 guests", "$85/night", "Budget"}
    };

    public BrowseRoomsPanel() {
        setLayout(null);
        setBounds(0, 0, W, H);
        setBackground(BG_MAIN);
        buildRoomsScreen();
    }

    private void buildRoomsScreen() {
        addTopbar(this, "Rooms", "Browse available rooms");
        addSidebar(this, "rooms");

        JPanel content = makeRoundPanel(BG_CONTENT);
        content.setLayout(null);
        content.setBounds(CONTENT_X + 10, CONTENT_Y + 10, CONTENT_W - 14, CONTENT_H - 14);
        content.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 1));
        add(content);

        int cx = 14;
        int cy = 14;
        int cw = content.getWidth() - cx * 2;

        JPanel header = makeRoundPanel(BG_ELEVATED);
        header.setLayout(null);
        header.setBounds(cx, cy, cw, 94);
        content.add(header);

        JLabel title = new JLabel("Rooms");
        title.setBounds(16, 14, 240, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TXT_PRIMARY);
        header.add(title);

        JLabel subtitle = new JLabel("Choose the perfect room for your stay.");
        subtitle.setBounds(16, 44, 360, 18);
        subtitle.setFont(F_SMALL);
        subtitle.setForeground(TXT_SECONDARY);
        header.add(subtitle);

        summaryLabel = new JLabel("4 available rooms");
        summaryLabel.setBounds(16, 64, 240, 16);
        summaryLabel.setFont(F_TINY);
        summaryLabel.setForeground(TXT_MUTED);
        header.add(summaryLabel);

        cy += 94 + 16;

        int fx = cx;
        filterButtons = new FilterButton[roomFilters.length];
        for (int i = 0; i < roomFilters.length; i++) {
            String filter = roomFilters[i];
            boolean active = filter.equals(activeFilter);
            FilterButton chip = new FilterButton(filter, active);
            chip.addActionListener(e -> setActiveFilter(filter));
            filterButtons[i] = chip;
            int chipW = 90 + (active ? 8 : 0);
            chip.setBounds(fx, cy, chipW, 32);
            content.add(chip);
            fx += chipW + 10;
        }

        cy += 32 + 18;
        int cardsY = cy;
        int cardsH = content.getHeight() - cardsY - 16;

        cardsPanel = new JPanel(null);
        cardsPanel.setOpaque(false);
        cardsPanel.setBounds(cx, cardsY, cw, cardsH);
        content.add(cardsPanel);

        renderRoomCards();
    }

    private void renderRoomCards() {
        cardsPanel.removeAll();
        int visibleCount = 0;
        for (Object[] room : roomData) {
            String type = (String) room[1];
            if (activeFilter.equals("All Types") || type.equals(activeFilter)) {
                visibleCount++;
            }
        }

        int columns = Math.min(3, Math.max(1, visibleCount));
        int gap = 20;
        int cardW = (cardsPanel.getWidth() - gap * (columns + 1)) / columns;
        int cardH = 260;
        int x = gap;
        int y = 0;
        int shown = 0;

        for (Object[] room : roomData) {
            String name = (String) room[0];
            String type = (String) room[1];
            String desc = (String) room[2];
            String price = (String) room[3];
            String badge = (String) room[4];

            if (!activeFilter.equals("All Types") && !type.equals(activeFilter)) {
                continue;
            }

            addRoomCard(cardsPanel, x, y, cardW, cardH, name, desc, price, badge,
                new Color(0xF97316), TXT_PRIMARY);

            shown++;
            x += cardW + gap;

            if (shown % columns == 0) {
                x = gap;
                y += cardH + 24;
            }
        }

        if (shown == 0) {
            JLabel empty = new JLabel("No rooms found for " + activeFilter, SwingConstants.CENTER);
            empty.setBounds(0, 0, cardsPanel.getWidth(), cardsPanel.getHeight());
            empty.setFont(F_MED);
            empty.setForeground(TXT_SECONDARY);
            cardsPanel.add(empty);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void setActiveFilter(String filter) {
        activeFilter = filter;
        if (filterButtons != null) {
            for (FilterButton button : filterButtons) {
                button.setActive(button.getText().equals(filter));
            }
        }
        if (summaryLabel != null) {
            summaryLabel.setText(getMatchingRoomCount() + " available rooms");
        }
        renderRoomCards();
    }

    private int getMatchingRoomCount() {
        if (activeFilter.equals("All Types")) {
            return roomData.length;
        }
        int count = 0;
        for (Object[] room : roomData) {
            if (((String) room[1]).equals(activeFilter)) {
                count++;
            }
        }
        return count;
    }

    private static class FilterButton extends JButton {
        private boolean activeState;

        public FilterButton(String text, boolean activeState) {
            super(text);
            this.activeState = activeState;
            setFont(F_MED);
            setForeground(activeState ? TXT_PRIMARY : TXT_SECONDARY);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setActive(boolean active) {
            activeState = active;
            setForeground(active ? TXT_PRIMARY : TXT_SECONDARY);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (activeState) {
                g2.setColor(new Color(0xF97316));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            } else {
                g2.setColor(BG_ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
