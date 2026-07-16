package hotel.ui.admin;

import hotel.dao.RoomDAO;
import hotel.model.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();

    private final JPanel roomGrid = new JPanel(new GridLayout(0, 3, 16, 16));
    private final JTextField searchField = AdminUITheme.textField();
    private final JButton allFilter = createFilterButton("All");
    private final JButton singleFilter = createFilterButton("Single");
    private final JButton doubleFilter = createFilterButton("Double");
    private final JButton suiteFilter = createFilterButton("Suite");
    private final JLabel filterLabel = new JLabel("Show:");
    private String activeTypeFilter = null;
    private Room selectedRoom;
    private JPanel selectedCard;

    public ManageRoomsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton addBtn = AdminUITheme.primaryButton(" Add Room");
        JButton editBtn = AdminUITheme.secondaryButton(" Edit Room");
        JButton deleteBtn = AdminUITheme.dangerButton(" Delete Room");
        JButton refreshBtn = AdminUITheme.secondaryButton(" Refresh");
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(AdminUITheme.SMALL_FONT);
        searchLbl.setForeground(AdminUITheme.TEXT_MUTED);
        JButton searchBtn = AdminUITheme.secondaryButton("Search");
        searchPanel.add(searchLbl, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        top.add(actions, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filterLabel.setFont(AdminUITheme.SMALL_FONT);
        filterLabel.setForeground(AdminUITheme.TEXT_MUTED);
        filters.add(filterLabel);
        filters.add(allFilter);
        filters.add(singleFilter);
        filters.add(doubleFilter);
        filters.add(suiteFilter);

        roomGrid.setBackground(AdminUITheme.BG);
        JScrollPane scroll = new JScrollPane(roomGrid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AdminUITheme.BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        header.add(top, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> reload());
        searchBtn.addActionListener(e -> reload());

        allFilter.addActionListener(e -> { activeTypeFilter = null; updateFilterStates(allFilter); reload(); });
        singleFilter.addActionListener(e -> { activeTypeFilter = "Single"; updateFilterStates(singleFilter); reload(); });
        doubleFilter.addActionListener(e -> { activeTypeFilter = "Double"; updateFilterStates(doubleFilter); reload(); });
        suiteFilter.addActionListener(e -> { activeTypeFilter = "Suite"; updateFilterStates(suiteFilter); reload(); });

        updateFilterStates(allFilter);
        reload();
    }

    public void reload() {
        try {
            String keyword = searchField.getText().trim().toLowerCase();
            List<Room> rooms = roomDAO.getAll();
            List<Room> filtered = new ArrayList<>();
            for (Room room : rooms) {
                if (activeTypeFilter != null && room.getType() != null && !room.getType().equalsIgnoreCase(activeTypeFilter)) {
                    continue;
                }
                if (!keyword.isBlank() && !matchesFilter(room, keyword)) {
                    continue;
                }
                filtered.add(room);
            }
            populateGrid(filtered);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean matchesFilter(Room room, String keyword) {
        String text = String.valueOf(room.getId()) + " " + safe(room.getRoomNumber()) + " " + safe(room.getType()) + " "
                + room.getPricePerNight() + " " + safe(room.getStatus());
        return text.toLowerCase().contains(keyword);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void populateGrid(List<Room> rooms) {
        roomGrid.removeAll();
        selectedRoom = null;
        selectedCard = null;
        for (Room room : rooms) {
            roomGrid.add(createRoomCard(room));
        }
        roomGrid.revalidate();
        roomGrid.repaint();
    }

    private JPanel createRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(AdminUITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = loadTypeImage(room.getType());
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(280, 140, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setPreferredSize(new Dimension(280, 140));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(240, 244, 255));
            imageLabel.setText("No image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            imageLabel.setForeground(AdminUITheme.TEXT_MUTED);
        }

        JLabel title = new JLabel("Room " + safe(room.getRoomNumber()));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(AdminUITheme.PRIMARY);

        JLabel type = new JLabel(safe(room.getType()));
        type.setFont(AdminUITheme.SMALL_FONT);
        type.setForeground(AdminUITheme.TEXT_MUTED);

        JLabel price = new JLabel(String.format("$%.2f / night", room.getPricePerNight()));
        price.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        price.setForeground(AdminUITheme.TEXT);

        JLabel status = new JLabel(capitalize(safe(room.getStatus())));
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setOpaque(true);
        status.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        switch (safe(room.getStatus()).toLowerCase()) {
            case "available" -> {
                status.setBackground(new Color(220, 245, 225));
                status.setForeground(new Color(30, 130, 60));
            }
            case "booked" -> {
                status.setBackground(new Color(220, 232, 255));
                status.setForeground(new Color(40, 80, 200));
            }
            case "cleaning" -> {
                status.setBackground(new Color(255, 243, 220));
                status.setForeground(new Color(180, 110, 20));
            }
            case "maintenance" -> {
                status.setBackground(new Color(255, 225, 225));
                status.setForeground(new Color(180, 40, 40));
            }
            default -> {
                status.setBackground(new Color(235, 235, 235));
                status.setForeground(AdminUITheme.TEXT);
            }
        }

        JPanel info = new JPanel(new GridLayout(0, 1, 4, 4));
        info.setOpaque(false);
        info.add(title);
        info.add(type);
        info.add(price);
        info.add(status);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (selectedCard != null) {
                    selectedCard.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
                }
                selectedRoom = room;
                selectedCard = card;
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AdminUITheme.PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(11, 11, 11, 11)));
            }

            @Override public void mouseEntered(MouseEvent e) {
                if (card != selectedCard) card.setBackground(new Color(245, 248, 255));
            }

            @Override public void mouseExited(MouseEvent e) {
                if (card != selectedCard) card.setBackground(AdminUITheme.CARD_BG);
            }
        });

        return card;
    }

    private ImageIcon loadTypeImage(String type) {
        if (type == null) return null;
        String resource = switch (type.trim().toLowerCase()) {
            case "single" -> "/hotel/images/resources/hotel1.jpg";
            case "double" -> "/hotel/images/resources/hotel2.jpg";
            case "suite" -> "/hotel/images/resources/hotel3.jpg";
            default -> null;
        };
        if (resource == null) return null;
        try {
            java.net.URL url = getClass().getResource(resource);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            return icon.getIconWidth() > 0 ? icon : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void editSelected() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }
        openDialog(selectedRoom);
    }

    private void deleteSelected() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete room #" + selectedRoom.getId() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            roomDAO.delete(selectedRoom.getId());
            selectedRoom = null;
            selectedCard = null;
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Room editing) {
        boolean addMode = editing == null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                addMode ? "Add Room" : "Edit Room", true);

        JPanel root = AdminUITheme.pagePanel();
        JPanel form = AdminUITheme.cardPanel(new GridBagLayout());

        JTextField roomNumberField = AdminUITheme.textField();
        JComboBox<String> typeBox = AdminUITheme.comboBox("Single", "Double", "Suite");
        JTextField priceField = AdminUITheme.textField();
        // JCheckBox availableBox = new JCheckBox("Available");
        JComboBox<String> availableBox = AdminUITheme.comboBox("booked", "available","cleaning","maintenance");

        if (editing != null) {
            roomNumberField.setText(editing.getRoomNumber());
            typeBox.setSelectedItem(editing.getType());
            priceField.setText(String.valueOf(editing.getPricePerNight()));
            availableBox.setSelectedItem(editing.getStatus());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Room Number", roomNumberField);
        addRow(form, gbc, y++, "Type", typeBox);
        addRow(form, gbc, y++, "Price Per Night", priceField);
        addRow(form, gbc, y++, "Status", availableBox);

        JButton saveBtn = AdminUITheme.primaryButton("Save");
        JButton cancelBtn = AdminUITheme.secondaryButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        dialog.add(root);

        saveBtn.addActionListener(e -> {
            String number = roomNumberField.getText().trim();
            String priceStr = priceField.getText().trim();
            if (number.isBlank() || priceStr.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Room number and price are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(Float.parseFloat(priceStr) < 45 || Float.parseFloat(priceStr) > 400 ){
                JOptionPane.showMessageDialog(dialog, "Price should be positive(45-400). ", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(Float.parseFloat(number) < 0 ){
                JOptionPane.showMessageDialog(dialog, "No Room should be positive. ", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                Room room = addMode ? new Room() : editing;
                room.setRoomNumber(number);
                room.setType(String.valueOf(typeBox.getSelectedItem()));
                room.setPricePerNight(price);
                room.setStatus(String.valueOf(availableBox.getSelectedItem()));
                if (addMode) roomDAO.add(room); else roomDAO.update(room);
                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminUITheme.SMALL_FONT);
        lbl.setForeground(AdminUITheme.TEXT_MUTED);
        form.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
    }

    private void updateFilterStates(JButton activeBtn) {
        for (JButton btn : new JButton[]{allFilter, singleFilter, doubleFilter, suiteFilter}) {
            if (btn == activeBtn) {
                btn.setBackground(AdminUITheme.PRIMARY);
                btn.setForeground(AdminUITheme.TEXT_WHITE);
            } else {
                btn.setBackground(new Color(227, 234, 248));
                btn.setForeground(AdminUITheme.PRIMARY);
            }
        }
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AdminUITheme.SMALL_FONT);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setBackground(new Color(227, 234, 248));
        btn.setForeground(AdminUITheme.PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        return btn;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }
}
