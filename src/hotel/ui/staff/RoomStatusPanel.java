package hotel.ui.staff;

import hotel.ui.staff.util.UIConstants;
import hotel.model.Room;
import hotel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomStatusPanel extends JPanel {

    private RoomDAO roomDAO;
    private List<Room> allRooms = new ArrayList<>();
    private JPanel roomGrid;
    private JLabel availableCount, bookedCount, cleaningCount, maintenanceCount;

    // Filter buttons
    private JButton activeFilter;

    public RoomStatusPanel() {
        this.roomDAO = new RoomDAO();
        setLayout(new BorderLayout());
        setBackground(UIConstants.THEME_WHITE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        loadRooms(null);
    }

    // ── Header ────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.THEME_WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Rooms");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.THEME_NAVY);

        header.add(title, BorderLayout.WEST);
        header.add(buildStatBadges(), BorderLayout.EAST);
        return header;
    }

    // ── Stat badges row (Available / Booked / Cleaning / Maintenance) ─
    private JPanel buildStatBadges() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        p.setBackground(UIConstants.THEME_WHITE_BG);

        availableCount = new JLabel("0");
        bookedCount = new JLabel("0");
        cleaningCount = new JLabel("0");
        maintenanceCount = new JLabel("0");

        p.add(statBadge(availableCount, "Available", new Color(30, 160, 80)));
        p.add(statBadge(bookedCount, "Booked", new Color(60, 100, 220)));
        p.add(statBadge(cleaningCount, "Cleaning", new Color(180, 110, 20)));
        p.add(statBadge(maintenanceCount, "Maintenance", new Color(180, 40, 40)));
        return p;
    }

    private JPanel statBadge(JLabel countLbl, String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(UIConstants.THEME_WHITE_BG);

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("Dialog", Font.PLAIN, 10));

        countLbl.setFont(UIConstants.FONT_SMALL);
        countLbl.setForeground(new Color(60, 60, 70));

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(UIConstants.FONT_SMALL);
        nameLbl.setForeground(new Color(100, 100, 110));

        p.add(dot);
        p.add(countLbl);
        p.add(nameLbl);
        return p;
    }

    // ── Content: filter tabs + grid ───────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(UIConstants.THEME_WHITE_BG);

        content.add(buildFilterBar(), BorderLayout.NORTH);

        roomGrid = new JPanel(new GridLayout(0, 4, 14, 14));
        roomGrid.setBackground(UIConstants.THEME_WHITE_BG);

        JScrollPane scroll = new JScrollPane(roomGrid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIConstants.THEME_WHITE_BG);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Dynamically adjust columns based on available width
        scroll.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = scroll.getViewport().getWidth();
                int cardWidth = 184; // card width + gap
                int cols = Math.max(1, width / cardWidth);
                GridLayout gl = (GridLayout) roomGrid.getLayout();
                if (gl.getColumns() != cols) {
                    gl.setColumns(cols);
                    roomGrid.revalidate();
                    roomGrid.repaint();
                }
            }
        });

        content.add(scroll, BorderLayout.CENTER);

        // FAB — Add Room button
        JPanel fab = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        fab.setBackground(UIConstants.THEME_WHITE_BG);
        JButton addBtn = createFab();
        addBtn.addActionListener(e -> showAddRoomDialog());
        fab.add(addBtn);
        content.add(fab, BorderLayout.SOUTH);

        return content;
    }

    // ── Filter tab bar ────────────────────────────────────────────────
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(UIConstants.THEME_WHITE_BG);

        String[] labels = { "All Rooms", "Floor 1", "Floor 2", "Suites" };
        String[] filters = { null, "1", "2", "Suite" };

        for (int i = 0; i < labels.length; i++) {
            final String filter = filters[i];
            JButton btn = createFilterButton(labels[i]);
            if (i == 0) {
                setActiveFilter(btn);
                activeFilter = btn;
            }
            btn.addActionListener(e -> {
                setActiveFilter(btn);
                loadRooms(filter);
            });
            bar.add(btn);
        }
        return bar;
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30));
        styleFilterInactive(btn);
        return btn;
    }

    private void setActiveFilter(JButton btn) {
        if (activeFilter != null)
            styleFilterInactive(activeFilter);
        activeFilter = btn;
        btn.setBackground(UIConstants.THEME_NAVY);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
    }

    private void styleFilterInactive(JButton btn) {
        btn.setBackground(new Color(240, 242, 248));
        btn.setForeground(new Color(80, 80, 100));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
    }

    // ── Load & render rooms ───────────────────────────────────────────
    private void loadRooms(String filter) {
        try {
            allRooms = roomDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            allRooms = new ArrayList<>();
        }

        // Update stat counts
        long avail = allRooms.stream().filter(r -> "available".equalsIgnoreCase(resolveStatus(r))).count();
        long booked = allRooms.stream().filter(r -> "booked".equalsIgnoreCase(resolveStatus(r))).count();
        long cleaning = allRooms.stream().filter(r -> "cleaning".equalsIgnoreCase(resolveStatus(r))).count();
        long maint = allRooms.stream().filter(r -> "maintenance".equalsIgnoreCase(resolveStatus(r))).count();

        availableCount.setText(avail + " ");
        bookedCount.setText(booked + " ");
        cleaningCount.setText(cleaning + " ");
        maintenanceCount.setText(maint + " ");

        // Filter rooms
        List<Room> filtered = new ArrayList<>();
        for (Room r : allRooms) {
            if (filter == null) {
                filtered.add(r);
            } else if (filter.equals("1") || filter.equals("2")) {
                if (r.getRoomNumber() != null && r.getRoomNumber().startsWith(filter)) {
                    filtered.add(r);
                }
            } else if (filter.equals("Suite")) {
                if (r.getType() != null && r.getType().toLowerCase().contains("suite")) {
                    filtered.add(r);
                }
            }
        }

        // Rebuild grid
        roomGrid.removeAll();
        for (Room room : filtered) {
            roomGrid.add(buildRoomCard(room));
        }
        roomGrid.revalidate();
        roomGrid.repaint();
    }

    // ── Single room card ──────────────────────────────────────────────
    private JPanel buildRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 222, 230), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.setPreferredSize(new Dimension(170, 110));

        // Top row: room number + status badge
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel roomNo = new JLabel(room.getRoomNumber() != null ? room.getRoomNumber() : "—");
        roomNo.setFont(new Font("Dialog", Font.BOLD, 13));
        roomNo.setForeground(UIConstants.THEME_NAVY);

        String status = resolveStatus(room);
        JLabel badge = makeStatusBadge(status);

        top.add(roomNo, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Room type
        JLabel type = new JLabel(room.getType() != null ? room.getType() : "");
        type.setFont(UIConstants.FONT_SMALL);
        type.setForeground(new Color(100, 100, 115));
        card.add(type, BorderLayout.CENTER);

        // Bottom: sub-info
        JLabel info = new JLabel(getCardSubInfo(room, status));
        info.setFont(new Font("Dialog", Font.PLAIN, 10));
        info.setForeground(new Color(140, 140, 155));
        card.add(info, BorderLayout.SOUTH);

        // Click to manage
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRoomDetail(room);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 253));
                top.setBackground(new Color(248, 249, 253));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                top.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JLabel makeStatusBadge(String status) {
        JLabel lbl = new JLabel(capitalize(status));
        lbl.setFont(new Font("Dialog", Font.BOLD, 10));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        switch (status.toLowerCase()) {
            case "available":
                lbl.setBackground(new Color(220, 245, 225));
                lbl.setForeground(new Color(30, 130, 60));
                break;
            case "booked":
                lbl.setBackground(new Color(220, 232, 255));
                lbl.setForeground(new Color(40, 80, 200));
                break;
            case "cleaning":
                lbl.setBackground(new Color(255, 243, 220));
                lbl.setForeground(new Color(180, 110, 20));
                break;
            case "maintenance":
                lbl.setBackground(new Color(255, 225, 225));
                lbl.setForeground(new Color(180, 40, 40));
                break;
            default:
                lbl.setBackground(new Color(235, 235, 235));
                lbl.setForeground(new Color(80, 80, 80));
                break;
        }
        return lbl;
    }

    // ── Room detail dialog ────────────────────────────────────────────
    private void showRoomDetail(Room room) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Room " + room.getRoomNumber(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(340, 260);
        dialog.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridLayout(0, 2, 10, 10));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        addDetailRow(body, "Room No.", room.getRoomNumber());
        addDetailRow(body, "Type", room.getType());
        addDetailRow(body, "Price/Night", "$ " + room.getPricePerNight());
        addDetailRow(body, "Status", resolveStatus(room));

        // Status change dropdown
        String[] statuses = { "available", "booked", "cleaning", "maintenance" };
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setSelectedItem(resolveStatus(room));
        statusBox.setFont(UIConstants.FONT_SMALL);

        JPanel changeRow = new JPanel(new BorderLayout(8, 0));
        changeRow.setBackground(Color.WHITE);
        changeRow.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        JLabel changeLbl = new JLabel("Change Status:");
        changeLbl.setFont(UIConstants.FONT_SMALL);
        changeLbl.setForeground(new Color(100, 100, 110));
        changeRow.add(changeLbl, BorderLayout.WEST);
        changeRow.add(statusBox, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Close");
        cancelBtn.setFont(UIConstants.FONT_SMALL);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(UIConstants.FONT_SMALL);
        saveBtn.setBackground(UIConstants.THEME_NAVY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            String newStatus = (String) statusBox.getSelectedItem();
            try {
                boolean avail = "available".equalsIgnoreCase(newStatus);
                room.setAvailable(avail);
                roomDAO.update(room);
                loadRooms(null);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to update: " + ex.getMessage());
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(body, BorderLayout.NORTH);
        dialog.add(changeRow, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setVisible(true);
    }

    // ── Add Room dialog ───────────────────────────────────────────────
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Room", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(320, 280);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField roomNoField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField priceField = new JTextField();
        String[] statuses = { "available", "cleaning", "maintenance" };
        JComboBox<String> statusBox = new JComboBox<>(statuses);

        form.add(fieldLabel("Room Number"));
        form.add(roomNoField);
        form.add(fieldLabel("Type"));
        form.add(typeField);
        form.add(fieldLabel("Price/Night"));
        form.add(priceField);
        form.add(fieldLabel("Status"));
        form.add(statusBox);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton addBtn = new JButton("Add Room");
        addBtn.setBackground(UIConstants.THEME_NAVY);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            try {
                String no = roomNoField.getText().trim();
                String type = typeField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String st = (String) statusBox.getSelectedItem();

                if (no.isEmpty() || type.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Room number and type are required.");
                    return;
                }

                Room newRoom = new Room(0, no, type, price, "available".equalsIgnoreCase(st));
                roomDAO.add(newRoom);
                loadRooms(null);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price format.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to add room: " + ex.getMessage());
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(addBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setVisible(true);
    }

    // ── FAB button ────────────────────────────────────────────────────
    private JButton createFab() {
        JButton btn = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? UIConstants.THEME_NAVY.brighter() : UIConstants.THEME_NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 22));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(48, 48));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private String resolveStatus(Room room) {
        return room.isAvailable() ? "available" : "booked";
    }

    private String getCardSubInfo(Room room, String status) {
        switch (status.toLowerCase()) {
            case "booked":
                return "Guest assigned";
            case "cleaning":
                return "In progress";
            case "maintenance":
                return "Under repair";
            default:
                return "Ready";
        }
    }

    private void addDetailRow(JPanel panel, String label, Object value) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(new Color(100, 100, 110));

        JLabel val = new JLabel(value != null ? value.toString() : "—");
        val.setFont(UIConstants.FONT_BODY);
        val.setForeground(UIConstants.THEME_DARK_FONT);

        panel.add(lbl);
        panel.add(val);
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text + ":");
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(new Color(100, 100, 110));
        return lbl;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}