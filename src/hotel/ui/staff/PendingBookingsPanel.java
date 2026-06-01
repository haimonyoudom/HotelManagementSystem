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

public class PendingBookingsPanel extends JPanel {

    private BookingService bookingService;
    private CustomerDAO customerDAO;
    private RoomDAO roomDAO;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JLabel pageInfoLabel;
    private int currentPage = 0;
    private int pageSize = 10;
    private JTextField filterField;

    public PendingBookingsPanel() {
        this.bookingService = new BookingService();
        this.customerDAO = new CustomerDAO();
        this.roomDAO = new RoomDAO();
        setLayout(new BorderLayout());
        setBackground(UIConstants.THEME_WHITE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadBookingsData();
    }

    // ── Header section ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.THEME_WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLbl = new JLabel("Pending Bookings");
        titleLbl.setFont(UIConstants.FONT_TITLE);
        titleLbl.setForeground(UIConstants.THEME_NAVY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setBackground(UIConstants.THEME_WHITE_BG);

        // Filter field
        filterField = new JTextField(15);
        filterField.setText("Filter bookings...");
        filterField.setFont(UIConstants.FONT_BODY);
        filterField.setBackground(new Color(250, 250, 250));
        filterField.setForeground(new Color(130, 130, 130));
        filterField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        filterField.setPreferredSize(new Dimension(200, 36));

        filterField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (filterField.getText().equals("Filter bookings...")) {
                    filterField.setText("");
                    filterField.setForeground(UIConstants.THEME_DARK_FONT);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (filterField.getText().isEmpty()) {
                    filterField.setText("Filter bookings...");
                    filterField.setForeground(new Color(130, 130, 130));
                }
            }
        });

        controls.add(filterField);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    // ── Content section (table) ───────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UIConstants.THEME_WHITE_BG);

        // Create table
        String[] columns = { "Booking ID", "Guest Name", "Room Type", "Check-in", "Check-out", "Status", "Actions" };
        tableModel = new DefaultTableModel(new Object[][] {}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 6) { // Actions column
                    return c; // Custom renderer handled below
                }
                c.setBackground(UIConstants.THEME_WHITE_BG);
                c.setForeground(UIConstants.THEME_DARK_FONT);
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(220, 230, 255));
                }
                return c;
            }
        };

        // Style table
        bookingsTable.setFont(UIConstants.FONT_BODY);
        bookingsTable.setRowHeight(44);
        bookingsTable.setBackground(UIConstants.THEME_WHITE_BG);
        bookingsTable.setForeground(UIConstants.THEME_DARK_FONT);
        bookingsTable.setGridColor(new Color(200, 200, 200));
        bookingsTable.setSelectionBackground(new Color(220, 230, 255));
        bookingsTable.setSelectionForeground(UIConstants.THEME_DARK_FONT);

        // Header styling
        JTableHeader header = bookingsTable.getTableHeader();
        header.setBackground(UIConstants.THEME_WHITE_BG);
        header.setForeground(UIConstants.THEME_NAVY);
        header.setFont(UIConstants.FONT_SUBHEADER);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        // Actions column renderer
        bookingsTable.getColumn("Actions").setCellRenderer(new ActionCellRenderer());
        bookingsTable.getColumn("Actions").setWidth(150);
        bookingsTable.getColumn("Actions").setPreferredWidth(150);

        // Set column widths
        bookingsTable.getColumn("Booking ID").setPreferredWidth(80);
        bookingsTable.getColumn("Guest Name").setPreferredWidth(120);
        bookingsTable.getColumn("Room Type").setPreferredWidth(100);
        bookingsTable.getColumn("Check-in").setPreferredWidth(90);
        bookingsTable.getColumn("Check-out").setPreferredWidth(90);
        bookingsTable.getColumn("Status").setPreferredWidth(80);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBackground(UIConstants.THEME_WHITE_BG);
        scrollPane.getViewport().setBackground(UIConstants.THEME_WHITE_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        content.add(scrollPane, BorderLayout.CENTER);
        return content;
    }

    // ── Footer section (pagination) ───────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UIConstants.THEME_WHITE_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        pageInfoLabel = new JLabel("Showing 1-10 of pending bookings");
        pageInfoLabel.setFont(UIConstants.FONT_SMALL);
        pageInfoLabel.setForeground(new Color(130, 130, 130));

        JPanel paginationButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        paginationButtons.setBackground(UIConstants.THEME_WHITE_BG);

        JButton prevBtn = createPaginationButton("◀", e -> previousPage());
        JButton pageBtn1 = createPaginationButton("1", e -> goToPage(0));
        JButton pageBtn2 = createPaginationButton("2", e -> goToPage(1));
        JButton pageBtn3 = createPaginationButton("3", e -> goToPage(2));
        JButton nextBtn = createPaginationButton("▶", e -> nextPage());

        paginationButtons.add(prevBtn);
        paginationButtons.add(pageBtn1);
        paginationButtons.add(pageBtn2);
        paginationButtons.add(pageBtn3);
        paginationButtons.add(nextBtn);

        footer.add(pageInfoLabel, BorderLayout.WEST);
        footer.add(paginationButtons, BorderLayout.EAST);
        return footer;
    }

    private JButton createPaginationButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_SMALL);
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(new Color(80, 80, 80));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(36, 30));
        btn.addActionListener(action);
        return btn;
    }

    // ── Load data ─────────────────────────────────────────────────────
    private void loadBookingsData() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();

            tableModel.setRowCount(0);

            for (Booking booking : bookings) {
                String customerName = "N/A";
                String roomType = "N/A";

                try {
                    Customer customer = customerDAO.getById(booking.getCustomerId());
                    if (customer != null) {
                        customerName = customer.getName();
                    }
                } catch (SQLException e) {
                    // Use default N/A
                }

                try {
                    Room room = roomDAO.getById(booking.getRoomId());
                    if (room != null) {
                        roomType = room.getType();
                    }
                } catch (SQLException e) {
                    // Use default N/A
                }

                Object[] row = {
                        booking.getId(),
                        customerName,
                        roomType,
                        booking.getCheckInDate() != null ? booking.getCheckInDate() : "N/A",
                        booking.getCheckOutDate() != null ? booking.getCheckOutDate() : "N/A",
                        getStatusBadge(booking.getStatus()),
                        "ACTIONS"
                };
                tableModel.addRow(row);
            }

            updatePageInfo();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }

    private String getStatusBadge(String status) {
        return status != null ? status : "PENDING";
    }

    private void updatePageInfo() {
        int total = tableModel.getRowCount();
        int start = currentPage * pageSize + 1;
        int end = Math.min((currentPage + 1) * pageSize, total);
        pageInfoLabel.setText(String.format("Showing %d-%d of %d pending bookings", start, end, total));
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePageInfo();
        }
    }

    private void nextPage() {
        int totalPages = (tableModel.getRowCount() + pageSize - 1) / pageSize;
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePageInfo();
        }
    }

    private void goToPage(int page) {
        currentPage = page;
        updatePageInfo();
    }

    // ── Custom cell renderer for actions ──────────────────────────────
    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private JButton approveBtn;
        private JButton rejectBtn;

        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
            setOpaque(true);

            approveBtn = new JButton("Approve");
            approveBtn.setFont(UIConstants.FONT_SMALL);
            approveBtn.setBackground(UIConstants.ACCENT_GREEN);
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            approveBtn.setFocusPainted(false);

            rejectBtn = new JButton("Reject");
            rejectBtn.setFont(UIConstants.FONT_SMALL);
            rejectBtn.setBackground(UIConstants.ACCENT_RED);
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            rejectBtn.setFocusPainted(false);

            add(approveBtn);
            add(rejectBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setBackground(UIConstants.THEME_WHITE_BG);
            if (isSelected) {
                setBackground(new Color(220, 230, 255));
            }
            return this;
        }
    }
}
