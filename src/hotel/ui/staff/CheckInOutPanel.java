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
import java.awt.event.*;
import java.util.List;
import java.sql.SQLException;

public class CheckInOutPanel extends JPanel {

    private BookingService bookingService;
    private CustomerDAO customerDAO;
    private RoomDAO roomDAO;

    // Check-in fields
    private JTextField ciBookingIdField;
    private JTextField ciRoomField;
    private JTextField ciGuestField;

    // Check-out fields
    private JTextField coBookingIdField;
    private JTextField coRoomField;
    private JTextField coGuestField;

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

        // Card title
        JLabel title = new JLabel("  Check-in");
        title.setFont(UIConstants.FONT_SUBHEADER);
        title.setForeground(UIConstants.THEME_NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        card.add(title, BorderLayout.NORTH);

        // Fields panel
        JPanel fields = new JPanel(new GridLayout(3, 1, 0, 10));
        fields.setBackground(Color.WHITE);

        // Row 1: Booking ID + Room No
        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        ciBookingIdField = createField("e.g. BK-9821");
        ciRoomField = createField("e.g. 402");
        JPanel bkCol = labeledField("Booking ID", ciBookingIdField);
        JPanel rmCol = labeledField("Room No.", ciRoomField);
        row1.add(bkCol);
        row1.add(rmCol);

        // Row 2: Guest Name
        ciGuestField = createField("Enter guest's full name");
        JPanel row2 = labeledField("Guest Name", ciGuestField);

        // Row 3: Button
        JButton confirmBtn = createPrimaryButton("Confirm Arrival", UIConstants.ACCENT_GREEN);
        confirmBtn.addActionListener(e -> handleCheckIn());

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

        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        coBookingIdField = createField("e.g. BK-7742");
        coRoomField = createField("e.g. 108");
        row1.add(labeledField("Booking ID", coBookingIdField));
        row1.add(labeledField("Room No.", coRoomField));

        coGuestField = createField("Enter guest's full name");

        JButton departBtn = createPrimaryButton("Process Departure", new Color(100, 110, 140));
        departBtn.addActionListener(e -> handleCheckOut());

        fields.add(row1);
        fields.add(labeledField("Guest Name", coGuestField));
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
        String[] cols = { "Booking ID", "Guest Name", "Room", "Type", "Status" };
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
        scheduleTable.getColumn("Type")
                .setCellRenderer(new BadgeCellRenderer(new Color(220, 235, 255), new Color(40, 80, 180)));
        scheduleTable.getColumn("Status").setCellRenderer(new StatusCellRenderer());

        scheduleTable.getColumn("Booking ID").setPreferredWidth(80);
        scheduleTable.getColumn("Guest Name").setPreferredWidth(160);
        scheduleTable.getColumn("Room").setPreferredWidth(60);
        scheduleTable.getColumn("Type").setPreferredWidth(90);
        scheduleTable.getColumn("Status").setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(scheduleTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        panel.add(hdr, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadSchedule();
        return panel;
    }

    // ── Actions ───────────────────────────────────────────────────────
    private void handleCheckIn() {
        String bookingIdStr = ciBookingIdField.getText().trim();
        String guestName = ciGuestField.getText().trim();

        if (bookingIdStr.isEmpty() || bookingIdStr.equals("e.g. BK-9821")) {
            showError("Please enter a Booking ID.");
            return;
        }

        try {
            int bookingId = parseBookingId(bookingIdStr);
            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                showError("Booking #" + bookingId + " not found.");
                return;
            }
            if (!"pending".equalsIgnoreCase(booking.getStatus()) &&
                    !"confirmed".equalsIgnoreCase(booking.getStatus())) {
                showError("Booking #" + bookingId + " is not eligible for check-in (status: " + booking.getStatus()
                        + ").");
                return;
            }

            bookingService.confirmBooking(bookingId);

            JOptionPane.showMessageDialog(this,
                    "Check-in successful for Booking #" + bookingId + ".",
                    "Check-In Confirmed", JOptionPane.INFORMATION_MESSAGE);

            clearFields(ciBookingIdField, ciRoomField, ciGuestField);
            loadSchedule();

        } catch (NumberFormatException ex) {
            showError("Invalid Booking ID format. Use a number or BK-XXXX.");
        } catch (Exception ex) {
            showError("Check-in failed: " + ex.getMessage());
        }
    }

    private void handleCheckOut() {
        String bookingIdStr = coBookingIdField.getText().trim();

        if (bookingIdStr.isEmpty() || bookingIdStr.equals("e.g. BK-7742")) {
            showError("Please enter a Booking ID.");
            return;
        }

        try {
            int bookingId = parseBookingId(bookingIdStr);
            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                showError("Booking #" + bookingId + " not found.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Process departure for Booking #" + bookingId + "?",
                    "Confirm Check-Out", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                bookingService.cancelBooking(bookingId); // marks completed / frees room

                JOptionPane.showMessageDialog(this,
                        "Check-out processed for Booking #" + bookingId + ".",
                        "Departure Processed", JOptionPane.INFORMATION_MESSAGE);

                clearFields(coBookingIdField, coRoomField, coGuestField);
                loadSchedule();
            }

        } catch (NumberFormatException ex) {
            showError("Invalid Booking ID format. Use a number or BK-XXXX.");
        } catch (Exception ex) {
            showError("Check-out failed: " + ex.getMessage());
        }
    }

    // ── Load schedule (confirmed only) ───────────────────────────────
    private void loadSchedule() {
        scheduleModel.setRowCount(0);
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking b : bookings) {
                String status = b.getStatus() != null ? b.getStatus() : "";

                // Only show confirmed bookings
                if (!"confirmed".equalsIgnoreCase(status))
                    continue;

                String guestName = "N/A";
                String roomNo = "N/A";
                String type = "Check-out";

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
                } catch (SQLException ignored) {
                }

                scheduleModel.addRow(new Object[] {
                        "BK-" + b.getId(),
                        guestName,
                        roomNo,
                        type,
                        status
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private int parseBookingId(String input) {
        // Accept "BK-1234" or plain "1234"
        return Integer.parseInt(input.replaceAll("(?i)BK-", "").trim());
    }

    private Booking getBookingById(int id) {
        try {
            return bookingService.getAllBookings().stream()
                    .filter(b -> b.getId() == id)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields)
            f.setText("");
    }

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

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_BODY);
        field.setForeground(new Color(130, 130, 130));
        field.setBackground(new Color(248, 249, 251));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(UIConstants.THEME_DARK_FONT);
                    field.setBackground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(130, 130, 130));
                    field.setBackground(new Color(248, 249, 251));
                }
            }
        });
        return field;
    }

    private JPanel labeledField(String label, JTextField field) {
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

    // ── Cell Renderers ────────────────────────────────────────────────
    private static class BadgeCellRenderer extends DefaultTableCellRenderer {
        private final Color bg, fg;

        BadgeCellRenderer(Color bg, Color fg) {
            this.bg = bg;
            this.fg = fg;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            JLabel lbl = new JLabel(v != null ? v.toString() : "");
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