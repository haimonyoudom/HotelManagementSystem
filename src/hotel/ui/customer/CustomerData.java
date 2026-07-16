package hotel.ui.customer;

import hotel.dao.BookingDAO;
import hotel.dao.CustomerDAO;
import hotel.dao.PaymentDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Payment;
import hotel.model.Room;
import hotel.model.User;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class CustomerData {
    private static final DateTimeFormatter DB_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter UI_DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0");

    private static User currentUser;

    private CustomerData() {
    }

    static void setCurrentUser(User user) {
        currentUser = user;
    }

    static User getCurrentUser() {
        return currentUser;
    }

    static Customer getCurrentCustomer() throws SQLException {
        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers = dao.getAll();
        if (customers.isEmpty()) {
            return null;
        }
        if (currentUser == null) {
            return customers.get(0);
        }

        String username = clean(currentUser.getUsername());
        String email = clean(currentUser.getEmail());
        for (Customer customer : customers) {
            if (!email.isEmpty() && email.equalsIgnoreCase(clean(customer.getEmail()))) {
                return customer;
            }
            if (!username.isEmpty() && username.equalsIgnoreCase(clean(customer.getName()))) {
                return customer;
            }
        }
        return customers.get(0);
    }

    static List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new RoomDAO().getAvailableRooms();
        rooms.sort(Comparator.comparing(Room::getType, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER));
        return rooms;
    }

    static List<BookingRow> getBookingRows() throws SQLException {
        Customer customer = getCurrentCustomer();
        List<Booking> bookings = customer != null
                ? new BookingDAO().getByCustomerId(customer.getId())
                : new BookingDAO().getAll();
        RoomDAO roomDAO = new RoomDAO();
        List<BookingRow> rows = new ArrayList<>();
        for (Booking booking : bookings) {
            Room room = roomDAO.getById(booking.getRoomId());
            rows.add(new BookingRow(booking, room));
        }
        rows.sort(Comparator.comparingInt((BookingRow row) -> row.booking.getId()).reversed());
        return rows;
    }

    static Booking createBooking(Room room, LocalDate checkIn, LocalDate checkOut, double totalPrice) throws SQLException {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            throw new SQLException("No customer record found. Please create a customer account first.");
        }
        Booking booking = new Booking();
        booking.setCustomerId(customer.getId());
        booking.setRoomId(room.getId());
        booking.setCheckInDate(checkIn.format(DB_DATE));
        booking.setCheckOutDate(checkOut.format(DB_DATE));
        booking.setTotalPrice(totalPrice);
        booking.setStatus("Pending");
        new BookingDAO().add(booking);
        return booking;
    }

    static void markBookingPaid(int bookingId, double amount) throws SQLException {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now().format(DB_DATE));
        payment.setMethod("QR");
        payment.setStatus("paid");
        new PaymentDAO().add(payment);

        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getById(bookingId);
        if (booking != null) {
            booking.setStatus("Pending");
            bookingDAO.update(booking);
        }
    }

    static DashboardStats getDashboardStats() throws SQLException {
        List<BookingRow> rows = getBookingRows();
        int pending = 0;
        int checkedIn = 0;
        double total = 0;
        for (BookingRow row : rows) {
            
            String status = normalizeStatus(row.booking.getStatus());
            if("approved".equalsIgnoreCase(status)){
                total += row.booking.getTotalPrice();
            }
            if ("Pending".equalsIgnoreCase(status)) {
                pending++;
            } else if ("Checked In".equalsIgnoreCase(status)) {
                checkedIn++;
            }
        }
        return new DashboardStats(rows.size(), total, pending, checkedIn, rows);
    }

    static String roomTitle(Room room) {
        if (room == null) {
            return "Unknown Room";
        }
        return titleCase(room.getType()) + " Room";
    }

    static String roomDescription(Room room) {
        if (room == null) {
            return "Room details unavailable";
        }
        return "Room " + room.getRoomNumber() + " - " + normalizeStatus(room.getStatus());
    }

    static String pricePerNight(Room room) {
        return "$" + MONEY.format(room.getPricePerNight()) + "/night";
    }

    static String money(double value) {
        return "$" + MONEY.format(value);
    }

    static String formatDate(String dbDate) {
        try {
            return LocalDate.parse(dbDate, DB_DATE).format(UI_DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            return dbDate != null ? dbDate : "-";
        }
    }

    static String normalizeStatus(String status) {
        String s = clean(status);
        if (s.isEmpty()) {
            return "Pending";
        }
        if (s.equalsIgnoreCase("checked_in") || s.equalsIgnoreCase("checkin")) {
            return "Checked In";
        }
        if (s.equalsIgnoreCase("checked_out") || s.equalsIgnoreCase("checkout")) {
            return "Checked Out";
        }
        if (s.equalsIgnoreCase("approved") || s.equalsIgnoreCase("confirmed")) {
            return "Approved";
        }
        if (s.equalsIgnoreCase("cancelled") || s.equalsIgnoreCase("canceled")) {
            return "Cancelled";
        }
        if (s.equalsIgnoreCase("pending")) {
            return "Pending";
        }
        return titleCase(s);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static String titleCase(String value) {
        String text = clean(value);
        if (text.isEmpty()) {
            return "";
        }
        String[] parts = text.replace('_', ' ').split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    static final class BookingRow {
        final Booking booking;
        final Room room;

        BookingRow(Booking booking, Room room) {
            this.booking = booking;
            this.room = room;
        }
    }

    static final class DashboardStats {
        final int totalBookings;
        final double totalSpent;
        final int pendingApproval;
        final int checkedInNow;
        final List<BookingRow> recentBookings;

        DashboardStats(int totalBookings, double totalSpent, int pendingApproval,
                int checkedInNow, List<BookingRow> recentBookings) {
            this.totalBookings = totalBookings;
            this.totalSpent = totalSpent;
            this.pendingApproval = pendingApproval;
            this.checkedInNow = checkedInNow;
            this.recentBookings = recentBookings;
        }
    }
}
