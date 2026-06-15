package hotel.ui.customer;

import hotel.dao.BookingDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Room;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class BrowseRoomsPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final Customer customer;

    private final JPanel roomsGrid = new JPanel(new GridLayout(0, 4, 18, 18));
    private String selectedType = "All";

    public BrowseRoomsPanel(Customer customer) {
        this.customer = customer;

        setLayout(new BorderLayout(18, 18));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setBackground(Color.BLACK);
        filters.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        addFilterButton(filters, "All");
        addFilterButton(filters, "Standard");
        addFilterButton(filters, "Deluxe");
        addFilterButton(filters, "Suite");
        addFilterButton(filters, "Family");

        roomsGrid.setOpaque(false);

        JScrollPane scrollPane = UITheme.scroll(roomsGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(filters, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        reload();
    }

    private void addFilterButton(JPanel filters, String type) {
        JButton button = UITheme.secondaryButton(type);

        if ("All".equals(type)) {
            button.setBackground(UITheme.PRIMARY);
            button.setForeground(Color.WHITE);
        }

        button.addActionListener(ignored -> {
            selectedType = type;
            reload();
        });

        filters.add(button);
    }

    public void reload() {
        roomsGrid.removeAll();

        try {
            List<Room> rooms = roomDAO.getAll();

            for (Room room : rooms) {
                boolean matchesType = "All".equals(selectedType)
                        || selectedType.equalsIgnoreCase(room.getType());

                if (matchesType) {
                    roomsGrid.add(roomCard(room));
                }
            }

            if (roomsGrid.getComponentCount() == 0) {
                roomsGrid.add(emptyCard("No rooms found for this filter."));
            }
        } catch (Exception ex) {
            roomsGrid.add(emptyCard("Failed to load rooms."));
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        roomsGrid.revalidate();
        roomsGrid.repaint();
    }

    private JPanel emptyCard(String message) {
        JPanel card = UITheme.cardPanel(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setForeground(UITheme.MUTED);
        card.add(label, BorderLayout.CENTER);
        return card;
    }

    private JPanel roomCard(Room room) {
        JPanel card = UITheme.cardPanel(new BorderLayout(8, 8));

        JLabel image = new JLabel("Room " + room.getRoomNumber(), SwingConstants.CENTER);
        image.setOpaque(true);
        image.setBackground(new Color(215, 225, 245));
        image.setForeground(UITheme.TEXT);
        image.setFont(new Font("SansSerif", Font.BOLD, 18));
        image.setPreferredSize(new Dimension(180, 95));

        JLabel type = UITheme.heading(room.getType() + " Room");
        JLabel roomNumber = UITheme.muted("Room No: " + room.getRoomNumber());

        JLabel price = new JLabel(String.format("$%.2f/night", room.getPricePerNight()));
        price.setForeground(UITheme.SECONDARY);
        price.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel status = new JLabel(room.isAvailable() ? "Available" : "Unavailable");
        status.setForeground(room.isAvailable() ? UITheme.SUCCESS : UITheme.DANGER);

        JPanel info = new JPanel(new GridLayout(4, 1, 4, 4));
        info.setOpaque(false);
        info.add(type);
        info.add(roomNumber);
        info.add(price);
        info.add(status);

        JButton bookBtn;

        if (room.isAvailable()) {
            bookBtn = UITheme.primaryButton("Book Now");
            bookBtn.addActionListener(ignored -> openBookingDialog(room));
        } else {
            bookBtn = UITheme.secondaryButton("Unavailable");
            bookBtn.setEnabled(false);
        }

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(bookBtn, BorderLayout.SOUTH);

        return card;
    }

    private void openBookingDialog(Room room) {
        if (customer == null) {
            JOptionPane.showMessageDialog(this,
                    "Customer profile is required before booking.\n"
                            + "Please logout and login again, then create your customer profile when prompted.",
                    "Cannot Book", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Book Room " + room.getRoomNumber(), true);

        JPanel root = UITheme.pagePanel();
        JPanel form = UITheme.cardPanel(new GridBagLayout());

        JLabel titleLabel = UITheme.heading("Confirm Room Booking");

        JLabel roomLabel = new JLabel(room.getType() + " Room #" + room.getRoomNumber());
        roomLabel.setFont(UITheme.HEADER_FONT);

        JLabel customerLabel = new JLabel(customer.getName() + " (" + customer.getEmail() + ")");
        JLabel priceLabel = new JLabel(String.format("$%.2f per night", room.getPricePerNight()));
        priceLabel.setForeground(UITheme.SECONDARY);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JTextField checkInField = UITheme.textField();
        JTextField checkOutField = UITheme.textField();
        JTextField nightsField = UITheme.textField();

        checkInField.setText("2026-06-01");
        checkOutField.setText("2026-06-02");
        nightsField.setText("1");

        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", room.getPricePerNight()));
        totalLabel.setFont(UITheme.HEADER_FONT);
        totalLabel.setForeground(UITheme.PRIMARY);

        nightsField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                updateTotal();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                updateTotal();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                updateTotal();
            }

            private void updateTotal() {
                try {
                    int nights = Integer.parseInt(nightsField.getText().trim());

                    if (nights <= 0) {
                        totalLabel.setText("Total: $0.00");
                        return;
                    }

                    totalLabel.setText(String.format("Total: $%.2f", room.getPricePerNight() * nights));
                } catch (NumberFormatException ignored) {
                    totalLabel.setText("Total: $0.00");
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        addFullRow(form, gbc, y++, titleLabel);
        addFullRow(form, gbc, y++, roomLabel);
        addFullRow(form, gbc, y++, customerLabel);
        addFullRow(form, gbc, y++, priceLabel);

        addRow(form, gbc, y++, "Check In Date", checkInField);
        addRow(form, gbc, y++, "Check Out Date", checkOutField);
        addRow(form, gbc, y++, "Nights", nightsField);

        addFullRow(form, gbc, y++, totalLabel);

        JButton confirmBtn = UITheme.primaryButton("Confirm Booking");
        JButton cancelBtn = UITheme.secondaryButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancelBtn);
        buttons.add(confirmBtn);

        addFullRow(form, gbc, y, buttons);

        root.add(form, BorderLayout.CENTER);
        dialog.add(root);

        confirmBtn.addActionListener(ignored -> {
            try {
                int nights = Integer.parseInt(nightsField.getText().trim());

                if (nights <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Nights must be greater than zero.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String checkIn = checkInField.getText().trim();
                String checkOut = checkOutField.getText().trim();

                if (checkIn.isBlank() || checkOut.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "Check-in and check-out dates are required.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Room latestRoom = roomDAO.getById(room.getId());

                if (latestRoom == null) {
                    JOptionPane.showMessageDialog(dialog, "Room no longer exists.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    dialog.dispose();
                    reload();
                    return;
                }

                if (!latestRoom.isAvailable()) {
                    JOptionPane.showMessageDialog(dialog, "This room is no longer available.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    dialog.dispose();
                    reload();
                    return;
                }

                double totalPrice = latestRoom.getPricePerNight() * nights;

                Booking booking = new Booking();
                booking.setCustomerId(customer.getId());
                booking.setRoomId(latestRoom.getId());
                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkOut);
                booking.setTotalPrice(totalPrice);
                booking.setStatus("pending");

                bookingDAO.add(booking);

                latestRoom.setAvailable(false);
                roomDAO.update(latestRoom);

                JOptionPane.showMessageDialog(dialog,
                        "Booking created successfully.\n"
                                + "Booking ID: " + booking.getId() + "\n"
                                + "Status: pending\n"
                                + String.format("Total: $%.2f", booking.getTotalPrice()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Nights must be a valid number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to create booking: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ignored -> dialog.dispose());

        dialog.setSize(580, 540);
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

    private void addFullRow(JPanel form, GridBagConstraints gbc, int y, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        form.add(component, gbc);
    }
}