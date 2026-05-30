package hotel.ui.staff;

import hotel.dao.BookingDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Room;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CheckInOutPanel extends JPanel {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Customer ID", "Room ID", "Check In", "Check Out", "Total", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> statusFilter = UITheme.comboBox("confirmed", "checked_in", "checked_out", "pending", "cancelled");

    public CheckInOutPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        JButton checkInBtn = UITheme.primaryButton("Check In");
        JButton checkOutBtn = UITheme.dangerButton("Check Out");

        actions.add(new JLabel("Status:"));
        actions.add(statusFilter);
        actions.add(refreshBtn);
        actions.add(checkInBtn);
        actions.add(checkOutBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(actions, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(ignored -> reload());
        statusFilter.addActionListener(ignored -> reload());
        checkInBtn.addActionListener(ignored -> updateStatus("checked_in", false));
        checkOutBtn.addActionListener(ignored -> updateStatus("checked_out", true));

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);

            String status = String.valueOf(statusFilter.getSelectedItem());
            List<Booking> bookings = bookingDAO.getByStatus(status);

            for (Booking booking : bookings) {
                tableModel.addRow(new Object[]{
                        booking.getId(),
                        booking.getCustomerId(),
                        booking.getRoomId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getTotalPrice(),
                        booking.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String newStatus, boolean makeRoomAvailable) {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Booking booking = bookingDAO.getById(id);

            if (booking == null) {
                JOptionPane.showMessageDialog(this, "Booking not found.");
                reload();
                return;
            }

            booking.setStatus(newStatus);
            bookingDAO.update(booking);

            Room room = roomDAO.getById(booking.getRoomId());

            if (room != null) {
                room.setAvailable(makeRoomAvailable);
                roomDAO.update(room);
            }

            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update booking status: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}