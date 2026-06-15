package hotel.ui.customer;

import hotel.dao.BookingDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingHistoryPanel extends JPanel {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final Customer customer;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Booking ID", "Room ID", "Check In", "Check Out", "Total", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final JLabel customerLabel = UITheme.muted("");

    public BookingHistoryPanel(Customer customer) {
        this.customer = customer;

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JButton refreshBtn = UITheme.primaryButton("Refresh History");

        if (customer == null) {
            customerLabel.setText("No customer profile linked. Booking history cannot be loaded.");
        } else {
            customerLabel.setText("Showing bookings for: " + customer.getName() + " (" + customer.getEmail() + ")");
        }

        top.add(customerLabel, BorderLayout.CENTER);
        top.add(refreshBtn, BorderLayout.EAST);

        table.setRowHeight(30);

        add(top, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(ignored -> reload());

        reload();
    }

    public void reload() {
        tableModel.setRowCount(0);

        if (customer == null) {
            return;
        }

        try {
            List<Booking> bookings = bookingDAO.getByCustomerId(customer.getId());

            for (Booking booking : bookings) {
                tableModel.addRow(new Object[]{
                        booking.getId(),
                        booking.getRoomId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getTotalPrice(),
                        booking.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load booking history: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}