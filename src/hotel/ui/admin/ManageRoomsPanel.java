package hotel.ui.admin;

import hotel.model.Room;
import hotel.service.RoomService;
import hotel.ui.util.UIConstants;
import hotel.ui.util.UIUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageRoomsPanel extends JPanel {
    private RoomService roomService;
    private JPanel roomsGrid;
    private JPanel filterPanel;

    public ManageRoomsPanel() {
        roomService = new RoomService();
        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.BG_DARK);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BG_DARK);

        JLabel titleLabel = new JLabel("Rooms");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        filterPanel.setBackground(UIConstants.BG_DARK);

        String[] filters = {"All", "Single", "Double", "Suite", "Deluxe"};
        for (String filter : filters) {
            filterPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            JButton btn = UIUtils.createSecondaryButton(filter);
            btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 32));
            attachFilterButtonBehavior(btn);
            filterPanel.add(btn);
        }
        headerPanel.add(filterPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        roomsGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        roomsGrid.setBackground(UIConstants.BG_DARK);

        JScrollPane scrollPane = new JScrollPane(roomsGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(UIConstants.BG_DARK);

        JButton addButton = UIUtils.createPrimaryButton("+ Add Room");
        addButton.setPreferredSize(new Dimension(120, 36));
        bottomPanel.add(addButton);

        addButton.addActionListener(e -> {
            JDialog dialogue = new JDialog((Frame) null, "Add Room", true);
            dialogue.setSize(400, 320);
            dialogue.setLocationRelativeTo(null);
            dialogue.getContentPane().setBackground(UIConstants.BG_DARK);
            dialogue.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBackground(UIConstants.BG_DARK);
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            formPanel.add(new JLabel("Room Label:"));
            JTextField roomNumberField = UIUtils.createStyledTextField();
            formPanel.add(roomNumberField);

            formPanel.add(new JLabel("Type:"));
            JComboBox<String> typeCombo = UIUtils.createStyledComboBox(new String[]{"Single", "Double", "Continued", "Deluxed"});
            formPanel.add(typeCombo);

            formPanel.add(new JLabel("Price/Night:"));
            JTextField priceField = UIUtils.createStyledTextField();
            formPanel.add(priceField);

            formPanel.add(new JLabel("Status:"));
            JComboBox<String> statusCombo = UIUtils.createStyledComboBox(new String[]{"Available", "Occupied"});
            formPanel.add(statusCombo);

            JButton saveBtn = UIUtils.createPrimaryButton("Save");
            saveBtn.addActionListener(ev -> {
            String number = roomNumberField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String priceText = priceField.getText().trim();

            if (number.isEmpty() || priceText.isEmpty()) {
                UIUtils.showError(dialogue, "Please fill in all fields.");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialogue, "Please enter a valid price.");
                return;
            }

            boolean success = roomService.addRoom(number, type, price);
            if (success) {
                UIUtils.showSuccess(dialogue, "Room added successfully.");
                dialogue.dispose();
                loadRooms();
            } else {
                UIUtils.showError(dialogue, "Failed to add room. Number may already exist.");
            }
        });

            dialogue.add(formPanel, BorderLayout.CENTER);
            dialogue.add(saveBtn, BorderLayout.SOUTH);
            dialogue.setVisible(true);
        });

        add(bottomPanel, BorderLayout.SOUTH);

        loadRooms();
    }

    private void loadRooms() {
        roomsGrid.removeAll();
        try {
            List<Room> rooms = roomService.getAllRooms();
            for (Room room : rooms) {
                roomsGrid.add(createRoomCard(room));
            }
        } catch (Exception e) {
            String[][] demoRooms = {
                {"101", "Single", "89.00", "Available"},
                {"102", "Double", "129.00", "Occupied"},
                {"103", "Suite", "249.00", "Available"},
                {"104", "Deluxe", "189.00", "Available"},
                {"105", "Single", "89.00", "Occupied"},
                {"201", "Double", "139.00", "Available"},
                {"202", "Suite", "259.00", "Occupied"},
                {"203", "Deluxe", "199.00", "Available"},
            };
            for (String[] r : demoRooms) {
                Room room = new Room(0, r[0], r[1], Double.parseDouble(r[2]), "Available".equals(r[3]));
                roomsGrid.add(createRoomCard(room));
            }
        }
        roomsGrid.revalidate();
        roomsGrid.repaint();
    }

    private void attachFilterButtonBehavior(JButton button) {
        button.putClientProperty("selected", false);
        button.addActionListener(e -> {
            for (Component component : filterPanel.getComponents()) {
                if (component instanceof JButton) {
                    setFilterButtonSelected((JButton) component, false);
                }
            }
            setFilterButtonSelected(button, true);
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("selected"))) {
                    button.setBackground(UIConstants.BG_INPUT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("selected"))) {
                    button.setBackground(UIConstants.BG_CARD);
                }
            }
        });
    }

    private void setFilterButtonSelected(JButton button, boolean selected) {
        button.putClientProperty("selected", selected);
        if (selected) {
            button.setBackground(UIConstants.ACCENT_RED);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(UIConstants.BG_CARD);
            button.setForeground(UIConstants.TEXT_PRIMARY);
        }
    }

    private JPanel createRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.BG_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(250, 180));

        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(UIConstants.BG_INPUT);
        imagePanel.setPreferredSize(new Dimension(0, 80));
        imagePanel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        imagePanel.setLayout(new BorderLayout());

        JLabel imgLabel = new JLabel("🖼️", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        imagePanel.add(imgLabel, BorderLayout.CENTER);

        card.add(imagePanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel numberLabel = new JLabel("Room " + room.getRoomNumber());
        numberLabel.setFont(UIConstants.FONT_SUBHEADER);
        numberLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel typeLabel = new JLabel(room.getType());
        typeLabel.setFont(UIConstants.FONT_SMALL);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", room.getPricePerNight()) + "/night");
        priceLabel.setFont(UIConstants.FONT_SMALL);
        priceLabel.setForeground(UIConstants.ACCENT_RED);

        JLabel statusLabel = new JLabel(room.isAvailable() ? "Available" : "Occupied");
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(room.isAvailable() ? UIConstants.ACCENT_GREEN : UIConstants.ACCENT_RED);

        statusPanel.add(priceLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        infoPanel.add(numberLabel);
        infoPanel.add(typeLabel);
        infoPanel.add(statusPanel);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JButton editBtn = new JButton("✏️");
        editBtn.setFont(UIConstants.FONT_SMALL);
        editBtn.setBackground(UIConstants.BG_INPUT);
        editBtn.setForeground(UIConstants.TEXT_PRIMARY);
        editBtn.setFocusPainted(false);
        editBtn.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        editBtn.setPreferredSize(new Dimension(32, 28));

        JButton deleteBtn = new JButton("🗑️");
        deleteBtn.setFont(UIConstants.FONT_SMALL);
        deleteBtn.setBackground(UIConstants.BG_INPUT);
        
        deleteBtn.setForeground(UIConstants.ACCENT_RED);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        deleteBtn.setPreferredSize(new Dimension(32, 28));

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }
}