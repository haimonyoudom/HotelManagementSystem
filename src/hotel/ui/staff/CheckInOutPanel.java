package hotel.ui.staff;

import hotel.ui.staff.util.UIConstants;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Room;
import hotel.service.BookingService;
import hotel.dao.CustomerDAO;
import hotel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class CheckInOutPanel extends JPanel {

    private BookingService bookingService;
    private CustomerDAO customerDAO;
    private RoomDAO roomDAO;
    private Map<Integer, String> guestNameCache = new HashMap<>();

    // Check-in fields
    private JComboBox<Booking> ciBookingBox;
    private JLabel ciRoomValue;
    private JLabel ciGuestValue;
    private JLabel ciRoomTypeValue;

    // Check-out fields
    private JComboBox<Booking> coBookingBox;
    private JLabel coRoomValue;
    private JLabel coGuestValue;
    private JLabel coRoomTypeValue;

    // Schedule table
    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;

    public CheckInOutPanel() {
        this.bookingService = new BookingService();
        this.customerDAO = new CustomerDAO();
        this.roomDAO = new RoomDAO();

        setLayout(new BorderLayout());
        setBackground(UIConstants.THEME_WHITE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refreshDropdowns();
        loadSchedule();
    }

    // ── Header ────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.THEME_WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Check-In / Check-Out");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.THEME_NAVY);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ── Main content ─────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(UIConstants.THEME_WHITE_BG);

        // Top row: Check-in card | Check-out card
        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsRow.setBackground(UIConstants.THEME_WHITE_BG);
        cardsRow.add(buildCheckInCard());
        cardsRow.add(buildCheckOutCard());

        content.add(cardsRow, BorderLayout.NORTH);
        content.add(buildSchedule(), BorderLayout.CENTER);
        return content;
    }

    // ── Check-In card ─────────────────────────────────────────────────
    private JPanel buildCheckInCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("  Check-in");
        title.setFont(UIConstants.FONT_SUBHEADER);
        title.setForeground(UIConstants.THEME_NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(3, 1, 0, 10));
        fields.setBackground(Color.WHITE);

        ciBookingBox = new JComboBox<>();
        ciBookingBox.setFont(UIConstants.FONT_BODY);
        ciBookingBox.setRenderer(new BookingComboRenderer());

        ciRoomValue = new JLabel("—");
        ciGuestValue = new JLabel("—");
        ciRoomTypeValue = new JLabel("—");
        styleValueLabel(ciRoomValue);
        styleValueLabel(ciGuestValue);
        styleValueLabel(ciRoomTypeValue);

        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.add(labeledField("Booking", ciBookingBox));
        row1.add(labeledValue("Room No.", ciRoomValue));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setBackground(Color.WHITE);
        row2.add(labeledValue("Guest Name", ciGuestValue));
        row2.add(labeledValue("Room Type", ciRoomTypeValue));

        JButton confirmBtn = createPrimaryButton("Confirm Arrival", UIConstants.ACCENT_GREEN);
        confirmBtn.addActionListener(e -> handleCheckIn());

        ciBookingBox.addActionListener(e -> updateCheckInPreview());

        fields.add(row1);
        fields.add(row2);
        fields.add(confirmBtn);

        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    // ── Check-Out card ────────────────────────────────────────────────
    private JPanel buildCheckOutCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("  Check-out");
        title.setFont(UIConstants.FONT_SUBHEADER);
        title.setForeground(UIConstants.THEME_NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(3, 1, 0, 10));
        fields.setBackground(Color.WHITE);

        coBookingBox = new JComboBox<>();
        coBookingBox.setFont(UIConstants.FONT_BODY);
        coBookingBox.setRenderer(new BookingComboRenderer());

        coRoomValue = new JLabel("—");
        coGuestValue = new JLabel("—");
        coRoomTypeValue = new JLabel("—");
        styleValueLabel(coRoomValue);
        styleValueLabel(coGuestValue);
        styleValueLabel(coRoomTypeValue);

        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.add(labeledField("Booking", coBookingBox));
        row1.add(labeledValue("Room No.", coRoomValue));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setBackground(Color.WHITE);
        row2.add(labeledValue("Guest Name", coGuestValue));
        row2.add(labeledValue("Room Type", coRoomTypeValue));

        JButton departBtn = createPrimaryButton("Process Departure", new Color(100, 110, 140));
        departBtn.addActionListener(e -> handleCheckOut());

        coBookingBox.addActionListener(e -> updateCheckOutPreview());

        fields.add(row1);
        fields.add(row2);
        fields.add(departBtn);

        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    // ── Today's Schedule table ────────────────────────────────────────
    private JPanel buildSchedule() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(0, 12));

        // Header row
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(Color.WHITE);

        JLabel title = new JLabel("Today's Schedule");
        title.setFont(UIConstants.FONT_SUBHEADER);
        title.setForeground(UIConstants.THEME_NAVY);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setBackground(Color.WHITE);
        badges.add(makeDot(UIConstants.ACCENT_GREEN, "Arrivals"));
        badges.add(makeDot(new Color(230, 140, 60), "Departures"));

        hdr.add(title, BorderLayout.WEST);
        hdr.add(badges, BorderLayout.EAST);

        // Table
        String[] cols = { "Booking ID", "Guest Name", "Room", "Room Type", "Check-in", "Check-out", "Status" };
        scheduleModel = new DefaultTableModel(new Object[][] {}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        scheduleTable = new JTable(scheduleModel);
        scheduleTable.setFont(UIConstants.FONT_BODY);
        scheduleTable.setRowHeight(40);
        scheduleTable.setBackground(Color.WHITE);
        scheduleTable.setForeground(UIConstants.THEME_DARK_FONT);
        scheduleTable.setGridColor(new Color(235, 235, 235));
        scheduleTable.setSelectionBackground(new Color(220, 230, 255));
        scheduleTable.setShowVerticalLines(false);

        JTableHeader th = scheduleTable.getTableHeader();
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(130, 130, 130));
        th.setFont(UIConstants.FONT_SMALL);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Status badge renderer
        scheduleTable.getColumn("Status").setCellRenderer(new StatusCellRenderer());

        scheduleTable.getColumn("Booking ID").setPreferredWidth(80);
        scheduleTable.getColumn("Guest Name").setPreferredWidth(140);
        scheduleTable.getColumn("Room").setPreferredWidth(60);
        scheduleTable.getColumn("Room Type").setPreferredWidth(90);
        scheduleTable.getColumn("Check-in").setPreferredWidth(100);
        scheduleTable.getColumn("Check-out").setPreferredWidth(100);
        scheduleTable.getColumn("Status").setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(scheduleTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(hdr, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadSchedule();
        return panel;
    }

    private JPanel labeledValue(String label, JLabel valueLbl) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(new Color(100, 100, 110));
        p.add(lbl, BorderLayout.NORTH);
        p.add(valueLbl, BorderLayout.CENTER);
        return p;
    }

    private void styleValueLabel(JLabel lbl) {
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.THEME_DARK_FONT);
        lbl.setBackground(new Color(248, 249, 251));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    private class BookingComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Booking b) {
                String guest = guestNameCache.getOrDefault(b.getId(), "Unknown");
                setText(b.getId() + " — " + guest);
            } else {
                setText("Select booking...");
            }
            return this;
        }
    }

    private void refreshDropdowns() {
        ciBookingBox.removeAllItems();
        coBookingBox.removeAllItems();
        guestNameCache.clear();

        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking b : bookings) {
                String status = b.getStatus() != null ? b.getStatus() : "";
                String guest = "N/A";
                try {
                    Customer c = customerDAO.getById(b.getCustomerId());
                    if (c != null)
                        guest = c.getName();
                } catch (SQLException ignored) {
                }
                guestNameCache.put(b.getId(), guest);

                if ("confirmed".equalsIgnoreCase(status)) {
                    ciBookingBox.addItem(b);
                } else if ("checked_in".equalsIgnoreCase(status)) {
                    coBookingBox.addItem(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateCheckInPreview();
        updateCheckOutPreview();
    }

    private void updateCheckInPreview() {
        Booking b = (Booking) ciBookingBox.getSelectedItem();
        if (b == null) {
            ciRoomValue.setText("—");
            ciGuestValue.setText("—");
            ciRoomTypeValue.setText("—");
            return;
        }
        ciGuestValue.setText(guestNameCache.getOrDefault(b.getId(), "N/A"));
        try {
            Room r = roomDAO.getById(b.getRoomId());
            ciRoomValue.setText(r != null ? r.getRoomNumber() : "N/A");
            ciRoomTypeValue.setText(r != null ? r.getType() : "N/A");
        } catch (SQLException e) {
            ciRoomValue.setText("N/A");
            ciRoomTypeValue.setText("N/A");
        }
    }

    private void updateCheckOutPreview() {
        Booking b = (Booking) coBookingBox.getSelectedItem();
        if (b == null) {
            coRoomValue.setText("—");
            coGuestValue.setText("—");
            coRoomTypeValue.setText("—");
            return;
        }
        coGuestValue.setText(guestNameCache.getOrDefault(b.getId(), "N/A"));
        try {
            Room r = roomDAO.getById(b.getRoomId());
            coRoomValue.setText(r != null ? r.getRoomNumber() : "N/A");
            coRoomTypeValue.setText(r != null ? r.getType() : "N/A");
        } catch (SQLException e) {
            coRoomValue.setText("N/A");
            coRoomTypeValue.setText("N/A");
        }
    }

    // ── Actions ───────────────────────────────────────────────────────
    private void handleCheckIn() {
        Booking booking = (Booking) ciBookingBox.getSelectedItem();
        if (booking == null) {
            showError("Please select a booking to check in.");
            return;
        }
        try {
            bookingService.checkInBooking(booking.getId());
            JOptionPane.showMessageDialog(this,
                    "Check-in successful for Booking #" + booking.getId() + ".",
                    "Check-In Confirmed", JOptionPane.INFORMATION_MESSAGE);
            refreshDropdowns();
            loadSchedule();
        } catch (Exception ex) {
            showError("Check-in failed: " + ex.getMessage());
        }
    }

    private void handleCheckOut() {
        Booking booking = (Booking) coBookingBox.getSelectedItem();
        if (booking == null) {
            showError("Please select a booking to check out.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Process departure for Booking #" + booking.getId() + "?",
                "Confirm Check-Out", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            bookingService.cancelBooking(booking.getId()); // marks completed / frees room
            JOptionPane.showMessageDialog(this,
                    "Check-out processed for Booking #" + booking.getId() + ".",
                    "Departure Processed", JOptionPane.INFORMATION_MESSAGE);
            refreshDropdowns();
            loadSchedule();
        } catch (Exception ex) {
            showError("Check-out failed: " + ex.getMessage());
        }
    }

    // ── Load schedule (confirmed + checked_in) ──────────────────────
    private void loadSchedule() {
        scheduleModel.setRowCount(0);
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking b : bookings) {
                String status = b.getStatus() != null ? b.getStatus() : "";

                // confirmed = waiting to check in, checked_in = waiting to check out
                if (!"confirmed".equalsIgnoreCase(status) && !"checked_in".equalsIgnoreCase(status))
                    continue;

                String guestName = "N/A";
                String roomNo = "N/A";
                String roomType = "N/A";

                try {
                    Customer c = customerDAO.getById(b.getCustomerId());
                    if (c != null)
                        guestName = c.getName();
                } catch (SQLException ignored) {
                }

                try {
                    Room r = roomDAO.getById(b.getRoomId());
                    if (r != null)
                        roomNo = r.getRoomNumber();
                    roomType = r.getType();
                } catch (SQLException ignored) {
                }

                scheduleModel.addRow(new Object[] {
                        b.getId(),
                        guestName,
                        roomNo,
                        roomType,
                        b.getCheckInDate() != null ? b.getCheckInDate() : "N/A",
                        b.getCheckOutDate() != null ? b.getCheckOutDate() : "N/A",
                        status
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── UI builders ───────────────────────────────────────────────────
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return card;
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(new Color(100, 100, 110));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton createPrimaryButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_SUBHEADER);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    private JPanel makeDot(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(Color.WHITE);
        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("Dialog", Font.PLAIN, 10));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(new Color(100, 100, 110));
        p.add(dot);
        p.add(lbl);
        return p;
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v != null ? v.toString() : "";
            Color bg, fg;
            switch (status.toLowerCase()) {
                case "confirmed":
                    bg = new Color(220, 245, 225);
                    fg = new Color(30, 130, 60);
                    break;
                case "pending":
                    bg = new Color(255, 243, 220);
                    fg = new Color(180, 110, 20);
                    break;
                case "checked_in":
                    bg = new Color(220, 235, 255);
                    fg = new Color(40, 80, 200);
                    break;
                case "cancelled":
                    bg = new Color(255, 225, 225);
                    fg = new Color(180, 40, 40);
                    break;
                default:
                    bg = new Color(235, 235, 235);
                    fg = new Color(80, 80, 80);
                    break;
            }

            JLabel lbl = new JLabel(status.toUpperCase());
            lbl.setFont(new Font("Dialog", Font.BOLD, 11));
            lbl.setForeground(fg);
            lbl.setBackground(bg);
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrap.setBackground(sel ? new Color(220, 230, 255) : Color.WHITE);
            wrap.add(lbl);
            return wrap;
        }
    }
}