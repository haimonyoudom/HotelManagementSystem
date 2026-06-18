package hotel.ui.admin;

import hotel.dao.BookingDAO;
import hotel.dao.CustomerDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.ui.common.UITheme;
import hotel.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageBookingsPanel extends JPanel {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Customer ID", "Customer Name", "Room ID", "Check In", "Check Out", "Total Price", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = UITheme.textField();

    public ManageBookingsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton editBtn = UITheme.secondaryButton("Edit Booking");
        JButton deleteBtn = UITheme.dangerButton("Delete Booking");
        JButton refreshBtn = UITheme.secondaryButton("Refresh");

        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.SMALL_FONT);
        searchLbl.setForeground(UITheme.MUTED);
        JButton searchBtn = UITheme.secondaryButton("Search");
        searchPanel.add(searchLbl, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        top.add(actions, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(top, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        editBtn.addActionListener(ignored -> editSelected());
        deleteBtn.addActionListener(ignored -> deleteSelected());
        refreshBtn.addActionListener(ignored -> reload());
        searchBtn.addActionListener(ignored -> search());

        reload();
    }

    public void reload() {
        searchField.setText("");
        loadBookings(null);
    }

    private void search() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isBlank()) {
            loadBookings(null);
            return;
        }
        loadBookings(keyword);
    }

    private void loadBookings(String keyword) {
        try {
            tableModel.setRowCount(0);

            List<Booking> bookings = bookingDAO.getAll();
            for (Booking booking : bookings) {
                if (keyword != null && !keyword.isBlank() && !matchesFilter(booking, keyword)) {
                    continue;
                }
                Customer customer = customerDAO.getById(booking.getCustomerId());
                String customerName = customer == null ? "Unknown" : customer.getName();
                tableModel.addRow(new Object[]{
                        booking.getId(),
                        booking.getCustomerId(),
                        customerName,
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

    private boolean matchesFilter(Booking booking, String keyword) {
        String customerName = "";
        try {
            Customer customer = customerDAO.getById(booking.getCustomerId());
            customerName = customer == null ? "" : customer.getName();
        } catch (Exception ignored) {
        }

        String text = String.valueOf(booking.getId()) + " "
                + booking.getCustomerId() + " "
                + customerName + " "
                + booking.getRoomId() + " "
                + safeString(booking.getCheckInDate()) + " "
                + safeString(booking.getCheckOutDate()) + " "
                + booking.getTotalPrice() + " "
                + safeString(booking.getStatus());
        return text.toLowerCase().contains(keyword);
    }

    private String safeString(String value) {
        return value == null ? "" : value;
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
        JSpinner checkInSpinner = makeDateSpinner();
        JSpinner checkOutSpinner = makeDateSpinner();
        JPanel checkInField = makeDateField(checkInSpinner);
        JPanel checkOutField = makeDateField(checkOutSpinner);
        JTextField totalPriceField = UITheme.textField();
        JComboBox<String> statusBox = UITheme.comboBox("pending", "confirmed", "checked_in", "checked_out", "cancelled");

        if (editing != null) {
            customerIdField.setText(String.valueOf(editing.getCustomerId()));
            roomIdField.setText(String.valueOf(editing.getRoomId()));
            setSpinnerDate(checkInSpinner, editing.getCheckInDate());
            setSpinnerDate(checkOutSpinner, editing.getCheckOutDate());
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
                String checkIn = toIsoDateString(checkInSpinner);
                String checkOut = toIsoDateString(checkOutSpinner);

                if (!DateUtil.isValidDate(checkIn) || !DateUtil.isValidDate(checkOut)) {
                    JOptionPane.showMessageDialog(dialog, "Please choose valid check-in and check-out dates.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!DateUtil.isBefore(checkIn, checkOut)) {
                    JOptionPane.showMessageDialog(dialog, "Check-out date must be after check-in date.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (totalPrice < 0) {
                    JOptionPane.showMessageDialog(dialog, "Total price cannot be negative.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Booking booking = editing == null ? new Booking() : editing;
                booking.setCustomerId(customerId);
                booking.setRoomId(roomId);
                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkOut);
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

    private JSpinner makeDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(UITheme.UI_FONT);
        return spinner;
    }

    private String toIsoDateString(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private void setSpinnerDate(JSpinner spinner, String isoDate) {
        try {
            if (isoDate != null && !isoDate.isBlank()) {
                spinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse(isoDate));
            }
        } catch (Exception ignored) {
            // Keep current value if parsing fails.
        }
    }

    private JPanel makeDateField(JSpinner spinner) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF8FAFF));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0xBBDEFB));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.add(spinner, BorderLayout.CENTER);

        JButton calIcon = new JButton("📅") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xE3EAF8));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0x0A1F5C));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        calIcon.setPreferredSize(new Dimension(40, 34));
        calIcon.setOpaque(false);
        calIcon.setContentAreaFilled(false);
        calIcon.setBorderPainted(false);
        calIcon.setFocusPainted(false);
        calIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calIcon.addActionListener(e -> showCalendarPopup(spinner, calIcon));

        wrapper.add(calIcon, BorderLayout.EAST);
        return wrapper;
    }

    private void showCalendarPopup(JSpinner spinner, Component invoker) {
        LocalDate selectedDate = toLocalDate((Date) spinner.getValue());
        int[] year = { selectedDate.getYear() };
        int[] month = { selectedDate.getMonthValue() };

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xBBDEFB)));

        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(new Color(0x0A1F5C));
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton prev = calendarNavButton("<", year, month, title, popup, spinner);
        JButton next = calendarNavButton(">", year, month, title, popup, spinner);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(prev, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 4, 4));
        daysPanel.setBackground(Color.WHITE);
        daysPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        panel.add(daysPanel, BorderLayout.CENTER);

        Runnable refresh = () -> {
            YearMonth ym = YearMonth.of(year[0], month[0]);
            title.setText(ym.getMonth().name().substring(0, 1) + ym.getMonth().name().substring(1).toLowerCase() + " " + ym.getYear());
            daysPanel.removeAll();
            for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
                JLabel label = new JLabel(d, SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                label.setForeground(new Color(0x546E7A));
                daysPanel.add(label);
            }
            int offset = ym.atDay(1).getDayOfWeek().getValue() % 7;
            for (int i = 0; i < offset; i++) {
                daysPanel.add(new JLabel(""));
            }
            for (int day = 1; day <= ym.lengthOfMonth(); day++) {
                LocalDate date = ym.atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                dayButton.setOpaque(false);
                dayButton.setContentAreaFilled(false);
                dayButton.setFocusPainted(false);
                dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (date.equals(selectedDate)) {
                    dayButton.setBorder(BorderFactory.createLineBorder(new Color(0x0A1F5C)));
                } else {
                    dayButton.setBorderPainted(false);
                }
                dayButton.addActionListener(ae -> {
                    spinner.setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    popup.setVisible(false);
                });
                daysPanel.add(dayButton);
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        };

        prev.addActionListener(e -> {
            month[0] = month[0] == 1 ? 12 : month[0] - 1;
            if (month[0] == 12) year[0]--;
            refresh.run();
        });
        next.addActionListener(e -> {
            month[0] = month[0] == 12 ? 1 : month[0] + 1;
            if (month[0] == 1) year[0]++;
            refresh.run();
        });

        refresh.run();
        popup.add(panel);
        popup.show(invoker, 0, invoker.getHeight());
    }

    private JButton calendarNavButton(String text, int[] year, int[] month, JLabel title, JPopupMenu popup, JSpinner spinner) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(0x0A1F5C));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}