package hotel.ui.admin;

import hotel.dao.BookingDAO;
import hotel.model.Booking;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageBookingsPanel extends JPanel {
    private final BookingDAO bookingDAO = new BookingDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Customer ID", "Room ID", "Check In", "Check Out", "Total Price", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ManageBookingsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = UITheme.primaryButton("Add Booking");
        JButton editBtn = UITheme.secondaryButton("Edit Booking");
        JButton deleteBtn = UITheme.dangerButton("Delete Booking");
        JButton refreshBtn = UITheme.secondaryButton("Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(actions, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        addBtn.addActionListener(ignored -> openDialog(null));
        editBtn.addActionListener(ignored -> editSelected());
        deleteBtn.addActionListener(ignored -> deleteSelected());
        refreshBtn.addActionListener(ignored -> reload());

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);

            List<Booking> bookings = bookingDAO.getAll();

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

    private void editSelected() {
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

            openDialog(booking);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load booking: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete booking #" + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            bookingDAO.delete(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete booking: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Booking editing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                editing == null ? "Add Booking" : "Edit Booking", true);

        JPanel root = UITheme.pagePanel();
        JPanel form = UITheme.cardPanel(new GridBagLayout());

        JTextField customerIdField = UITheme.textField();
        JTextField roomIdField = UITheme.textField();
        JTextField checkInField = UITheme.textField();
        JTextField checkOutField = UITheme.textField();
        JTextField totalPriceField = UITheme.textField();
        JComboBox<String> statusBox = UITheme.comboBox("pending", "confirmed", "checked_in", "checked_out", "cancelled");

        if (editing != null) {
            customerIdField.setText(String.valueOf(editing.getCustomerId()));
            roomIdField.setText(String.valueOf(editing.getRoomId()));
            checkInField.setText(editing.getCheckInDate());
            checkOutField.setText(editing.getCheckOutDate());
            totalPriceField.setText(String.valueOf(editing.getTotalPrice()));
            statusBox.setSelectedItem(editing.getStatus());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Customer ID", customerIdField);
        addRow(form, gbc, y++, "Room ID", roomIdField);
        addRow(form, gbc, y++, "Check In Date", checkInField);
        addRow(form, gbc, y++, "Check Out Date", checkOutField);
        addRow(form, gbc, y++, "Total Price", totalPriceField);
        addRow(form, gbc, y++, "Status", statusBox);

        JButton saveBtn = UITheme.primaryButton("Save");
        JButton cancelBtn = UITheme.secondaryButton("Cancel");

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

        saveBtn.addActionListener(ignored -> {
            try {
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                int roomId = Integer.parseInt(roomIdField.getText().trim());
                double totalPrice = Double.parseDouble(totalPriceField.getText().trim());

                Booking booking = editing == null ? new Booking() : editing;
                booking.setCustomerId(customerId);
                booking.setRoomId(roomId);
                booking.setCheckInDate(checkInField.getText().trim());
                booking.setCheckOutDate(checkOutField.getText().trim());
                booking.setTotalPrice(totalPrice);
                booking.setStatus(String.valueOf(statusBox.getSelectedItem()));

                if (editing == null) {
                    bookingDAO.add(booking);
                } else {
                    bookingDAO.update(booking);
                }

                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Customer ID, Room ID and Total Price must be valid numbers.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save booking: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ignored -> dialog.dispose());

        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(input, gbc);
    }
}