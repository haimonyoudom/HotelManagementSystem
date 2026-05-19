package hotel.ui.admin;

import hotel.model.Room;
import hotel.service.RoomService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ManageRoomsPanel extends JPanel {

    static final Color BG_DARK = new Color(18, 18, 18);
    static final Color BG_CARD = new Color(28, 28, 28);
    static final Color ACCENT_RED = new Color(200, 50, 50);
    static final Color TEXT_WHITE = new Color(240, 240, 240);
    static final Color TEXT_GRAY = new Color(150, 150, 150);
    static final Color TEXT_GREEN = new Color(80, 200, 120);
    static final Color TEXT_ORANGE = new Color(220, 160, 60);
    static final Color BORDER_COLOR = new Color(50, 50, 50);

    static final int CONTENT_WIDTH = 1180;
    static final int FRAME_HEIGHT = 900;

    private RoomService roomService;
    private JPanel roomGridContainer;
    private List<Room> roomList;

    public ManageRoomsPanel() {
        this.roomService = new RoomService();
        setLayout(null);
        setBackground(BG_DARK);

        // ═══════════════════════════════════════════════════════════
        // HEADER
        // ═══════════════════════════════════════════════════════════
        JLabel title = new JLabel("Rooms");
        title.setBounds(30, 25, 200, 35);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_WHITE);
        add(title);

        JLabel subtitle = new JLabel("Find your perfect room");
        subtitle.setBounds(30, 60, 300, 20);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_GRAY);
        add(subtitle);

        // Date display
        JLabel dateLabel = new JLabel("Apr 19, 2026");
        dateLabel.setBounds(CONTENT_WIDTH - 150, 30, 120, 25);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_GRAY);
        add(dateLabel);

        // ═══════════════════════════════════════════════════════════
        // FILTER BUTTONS
        // ═══════════════════════════════════════════════════════════
        String[] filters = {"All", "Standard", "Deluxe", "Suite", "Presidential", "Family"};
        int filterX = 30;
        for (String filter : filters) {
            final String filterType = filter;
            JButton btn = createStyledButton(filter, filterX, 100, 90, 36, 
                filter.equals("All") ? ACCENT_RED : BG_CARD, TEXT_WHITE);
            btn.addActionListener(e -> filterRooms(filterType));
            add(btn);
            filterX += 100;
        }

        // ═══════════════════════════════════════════════════════════
        // ROOM CARDS GRID CONTAINER
        // ═══════════════════════════════════════════════════════════
        roomGridContainer = new JPanel();
        roomGridContainer.setLayout(null);
        roomGridContainer.setBounds(0, 160, CONTENT_WIDTH, FRAME_HEIGHT - 160);
        roomGridContainer.setBackground(BG_DARK);
        add(roomGridContainer);

        // Load data using YOUR RoomService
        loadRoomsFromDatabase();
    }

    // ── Load rooms using YOUR RoomService.getAllRooms() ─────────
    private void loadRoomsFromDatabase() {
        roomList = roomService.getAllRooms();
        System.out.println("Loaded " + roomList.size() + " rooms via RoomService");

        if (roomList == null || roomList.isEmpty()) {
            roomList = getDemoRooms();
        }

        renderRoomCards(roomList);
    }

    // ── Filter rooms using YOUR RoomService.searchRooms() ──────
    private void filterRooms(String type) {
        if (type.equals("All")) {
            roomList = roomService.getAllRooms();
            renderRoomCards(roomList);
            return;
        }

        List<Room> filtered = roomService.searchRooms(type);
        if (filtered == null || filtered.isEmpty()) {
            // Fallback to manual filter
            filtered = new java.util.ArrayList<>();
            for (Room r : roomList) {
                if (r.getType().equalsIgnoreCase(type)) {
                    filtered.add(r);
                }
            }
        }
        renderRoomCards(filtered);
    }

    // ── Render room cards ──────────────────────────────────────
    private void renderRoomCards(List<Room> rooms) {
        roomGridContainer.removeAll();

        int cardX = 30, cardY = 0;
        int cardWidth = 360, cardHeight = 220;
        int cardsPerRow = 3;
        int count = 0;

        for (Room room : rooms) {
            if (count > 0 && count % cardsPerRow == 0) {
                cardX = 30;
                cardY += cardHeight + 20;
            }

            JPanel card = createRoomCard(room, cardX, cardY, cardWidth, cardHeight);
            roomGridContainer.add(card);

            cardX += cardWidth + 20;
            count++;
        }

        roomGridContainer.revalidate();
        roomGridContainer.repaint();
    }

    // ── Create room card from YOUR Room model ──────────────────
    JPanel createRoomCard(Room room, int x, int y, int w, int h) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, w, h);
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Room image placeholder
        JPanel imgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 60, 60));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(TEXT_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.drawString("[Room Image]", getWidth()/2 - 35, getHeight()/2);
            }
        };
        imgPanel.setBounds(15, 15, w - 30, 100);
        card.add(imgPanel);

        // Room number (String from YOUR model)
        JLabel numLabel = new JLabel("Room " + room.getRoomNumber());
        numLabel.setBounds(15, 125, 120, 22);
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        numLabel.setForeground(TEXT_WHITE);
        card.add(numLabel);

        // Type
        JLabel typeLabel = new JLabel(room.getType());
        typeLabel.setBounds(w - 110, 125, 95, 20);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(TEXT_GRAY);
        typeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(typeLabel);

        // Price (from YOUR model: pricePerNight)
        JLabel priceLabel = new JLabel("$" + (int)room.getPricePerNight() + "/night");
        priceLabel.setBounds(15, 152, 120, 22);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLabel.setForeground(TEXT_GREEN);
        card.add(priceLabel);

        // Status (from YOUR model: isAvailable boolean)
        String status = room.isAvailable() ? "Available" : "Occupied";
        JLabel statusLabel = new JLabel(status);
        statusLabel.setBounds(w - 105, 152, 90, 24);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (room.isAvailable()) {
            statusLabel.setForeground(TEXT_GREEN);
            statusLabel.setBackground(new Color(30, 60, 40));
        } else {
            statusLabel.setForeground(ACCENT_RED);
            statusLabel.setBackground(new Color(60, 30, 30));
        }
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        card.add(statusLabel);

        // Details
        JLabel detailLabel = new JLabel("ID: " + room.getId() + " • " + room.getType() + " Room");
        detailLabel.setBounds(15, 185, w - 30, 18);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailLabel.setForeground(TEXT_GRAY);
        card.add(detailLabel);

        return card;
    }

    // ── Demo data matching YOUR Room model ─────────────────────
    private List<Room> getDemoRooms() {
        List<Room> rooms = new java.util.ArrayList<>();
        rooms.add(new Room(1, "101", "Standard", 120.0, true));
        rooms.add(new Room(2, "102", "Deluxe", 180.0, false));
        rooms.add(new Room(3, "103", "Suite", 350.0, true));
        rooms.add(new Room(4, "104", "Presidential", 800.0, false));
        rooms.add(new Room(5, "105", "Family", 250.0, true));
        rooms.add(new Room(6, "106", "Standard", 120.0, false));
        rooms.add(new Room(7, "201", "Deluxe", 200.0, true));
        rooms.add(new Room(8, "202", "Suite", 380.0, false));
        rooms.add(new Room(9, "301", "Presidential", 900.0, true));
        return rooms;
    }

    JButton createStyledButton(String text, int x, int y, int w, int h, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }
}