package hotel.test;

import hotel.config.DBConnection;
import hotel.config.DBInitializer;
import hotel.dao.CustomerDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Payment;
import hotel.service.BookingService;
import hotel.service.PaymentService;
import hotel.service.ReportService;
import hotel.service.RoomService;
import hotel.dao.RoomDAO;
import hotel.model.Room;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestIntegration {

    public static void main(String[] args) throws Exception {
        Files.deleteIfExists(Paths.get("hotel.db"));
        DBInitializer.initialize();

        CustomerDAO customerDAO = new CustomerDAO();
        RoomDAO roomDAO = new RoomDAO();
        RoomService roomService = new RoomService();
        BookingService bookingService = new BookingService();
        PaymentService paymentService = new PaymentService();
        ReportService reportService = new ReportService();

        Customer customer = new Customer(0, "Integration Customer", "integration@example.com", "0102030405", "City Center");
        customerDAO.add(customer);

        boolean roomAdded = roomService.addRoom("INT-101", "double", 100.0);
        if (!roomAdded) {
            throw new AssertionError("Failed to add room");
        }

        Room room = roomDAO.getRoomsByType("double").stream()
                .filter(r -> "INT-101".equals(r.getRoomNumber()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Added room not found"));

        Booking booking = bookingService.createBooking(customer.getId(), room.getId(), "2026-06-01", "2026-06-03");
        if (booking == null) {
            throw new AssertionError("Booking creation failed");
        }

        Payment payment = paymentService.processPayment(booking.getId(), booking.getTotalPrice(), "cash");
        if (payment == null) {
            throw new AssertionError("Payment processing failed");
        }

        String revenueReport = reportService.generateRevenueReport();
        if (!revenueReport.contains("Total Revenue: 200.0")) {
            throw new AssertionError("Revenue report mismatch: " + revenueReport);
        }

        boolean cancelled = bookingService.cancelBooking(booking.getId());
        if (!cancelled) {
            throw new AssertionError("Booking cancellation failed");
        }

        Room updatedRoom = roomDAO.getById(room.getId());
        if (updatedRoom == null || !updatedRoom.isAvailable()) {
            throw new AssertionError("Room was not marked available after cancellation");
        }

        DBConnection.closeConnection();
        System.out.println("Integration test passed.");
    }
}