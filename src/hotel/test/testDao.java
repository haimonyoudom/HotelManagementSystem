package hotel.test;

import hotel.config.DBConnection;
import hotel.config.DBInitializer;
import hotel.dao.BookingDAO;
import hotel.dao.CustomerDAO;
import hotel.dao.PaymentDAO;
import hotel.dao.RoomDAO;
import hotel.dao.StaffDAO;
import hotel.dao.UserDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Payment;
import hotel.model.Room;
import hotel.model.Staff;
import hotel.model.User;
import hotel.util.PasswordHasher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class testDao {

    private static final String RUN_ID = String.valueOf(System.currentTimeMillis());

    public static void main(String[] args) throws Exception {
        resetDatabase();
        DBInitializer.initialize();

        demoUserDao();
        demoRoomDao();
        demoCustomerDao();
        demoStaffDao();
        demoBookingDao();
        demoPaymentDao();

        DBConnection.closeConnection();
        System.out.println("Finished DAO demo.");
    }

    private static void resetDatabase() throws Exception {
        Files.deleteIfExists(Paths.get("hotel.db"));
    }

    private static void demoUserDao() throws Exception {
        UserDAO userDAO = new UserDAO();
        String username = "dao_user_" + RUN_ID;
        User user = new User(0, username, PasswordHasher.hash("secret123"), "staff", LocalDate.now().toString());

        System.out.println("\n--- User DAO Demo ---");
        System.out.println("Adding user: " + user.getUsername());
        userDAO.add(user);
        System.out.println("Saved user id: " + user.getId());

        User byId = userDAO.getById(user.getId());
        System.out.println("Get by id: " + byId);

        User byUsername = userDAO.getByUsername(username);
        System.out.println("Get by username: " + byUsername);

        user.setRole("admin");
        System.out.println("Updating role to admin");
        userDAO.update(user);

        User updated = userDAO.getById(user.getId());
        System.out.println("After update: " + updated);

        List<User> users = userDAO.getAll();
        System.out.println("All users:");
        for (User item : users) {
            System.out.println("  " + item);
        }

        System.out.println("Deleting user");
        userDAO.delete(user.getId());
        System.out.println("After delete: " + userDAO.getById(user.getId()));
    }

    private static void demoRoomDao() throws Exception {
        RoomDAO roomDAO = new RoomDAO();
        String roomNumber = "R-" + RUN_ID;
        Room room = new Room(0, roomNumber, "single", 125.50, true);

        System.out.println("\n--- Room DAO Demo ---");
        System.out.println("Adding room: " + room.getRoomNumber());
        roomDAO.add(room);
        System.out.println("Saved room id: " + room.getId());

        Room byId = roomDAO.getById(room.getId());
        System.out.println("Get by id: " + byId);

        room.setPricePerNight(150.0);
        room.setAvailable(false);
        System.out.println("Updating room price and availability");
        roomDAO.update(room);

        Room updated = roomDAO.getById(room.getId());
        System.out.println("After update: " + updated);

        List<Room> availableRooms = roomDAO.getAvailableRooms();
        System.out.println("Available rooms:");
        for (Room item : availableRooms) {
            System.out.println("  " + item);
        }

        List<Room> sameTypeRooms = roomDAO.getRoomsByType("single");
        System.out.println("Rooms by type 'single':");
        for (Room item : sameTypeRooms) {
            System.out.println("  " + item);
        }

        System.out.println("Deleting room");
        roomDAO.delete(room.getId());
        System.out.println("After delete: " + roomDAO.getById(room.getId()));
    }

    private static void demoCustomerDao() throws Exception {
        CustomerDAO customerDAO = new CustomerDAO();
        String email = "dao_customer_" + RUN_ID + "@example.com";
        Customer customer = new Customer(0, "DAO Customer " + RUN_ID, email, "0123456789", "Test Address");

        System.out.println("\n--- Customer DAO Demo ---");
        System.out.println("Adding customer: " + customer.getName());
        customerDAO.add(customer);
        System.out.println("Saved customer id: " + customer.getId());

        Customer byId = customerDAO.getById(customer.getId());
        System.out.println("Get by id: " + byId);

        Customer byEmail = customerDAO.getByEmail(email);
        System.out.println("Get by email: " + byEmail);

        customer.setPhone("0987654321");
        System.out.println("Updating customer phone");
        customerDAO.update(customer);

        Customer updated = customerDAO.getById(customer.getId());
        System.out.println("After update: " + updated);

        List<Customer> matches = customerDAO.searchByName("DAO Customer");
        System.out.println("Search by name:");
        for (Customer item : matches) {
            System.out.println("  " + item);
        }

        System.out.println("Deleting customer");
        customerDAO.delete(customer.getId());
        System.out.println("After delete: " + customerDAO.getById(customer.getId()));
    }

    private static void demoStaffDao() throws Exception {
        UserDAO userDAO = new UserDAO();
        StaffDAO staffDAO = new StaffDAO();
        String username = "dao_staff_user_" + RUN_ID;
        User staffUser = new User(0, username, PasswordHasher.hash("secret123"), "staff", LocalDate.now().toString());

        System.out.println("\n--- Staff DAO Demo ---");
        System.out.println("Adding staff login user: " + staffUser.getUsername());
        userDAO.add(staffUser);

        Staff staff = new Staff(0, "DAO Staff " + RUN_ID, "receptionist", 4500.0, staffUser.getId());
        System.out.println("Adding staff profile: " + staff.getName());
        staffDAO.add(staff);
        System.out.println("Saved staff id: " + staff.getId());

        Staff byId = staffDAO.getById(staff.getId());
        System.out.println("Get by id: " + byId);

        staff.setPosition("manager");
        System.out.println("Updating staff position");
        staffDAO.update(staff);

        Staff updated = staffDAO.getById(staff.getId());
        System.out.println("After update: " + updated);

        List<Staff> byPosition = staffDAO.getByPosition("manager");
        System.out.println("Staff by position 'manager':");
        for (Staff item : byPosition) {
            System.out.println("  " + item);
        }

        System.out.println("Deleting staff and login user");
        staffDAO.delete(staff.getId());
        userDAO.delete(staffUser.getId());
        System.out.println("After delete: " + staffDAO.getById(staff.getId()));
    }

    private static void demoBookingDao() throws Exception {
        CustomerDAO customerDAO = new CustomerDAO();
        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();

        Customer customer = new Customer(0, "Booking Customer " + RUN_ID, "booking_customer_" + RUN_ID + "@example.com", "0111111111", "Booking Address");
        Room room = new Room(0, "B-" + RUN_ID, "double", 220.0, true);
        customerDAO.add(customer);
        roomDAO.add(room);

        Booking booking = new Booking(0, customer.getId(), room.getId(), "2026-05-12", "2026-05-15", 660.0, "pending");
        System.out.println("\n--- Booking DAO Demo ---");
        System.out.println("Adding booking for customer id " + customer.getId() + " and room id " + room.getId());
        bookingDAO.add(booking);
        System.out.println("Saved booking id: " + booking.getId());

        Booking byId = bookingDAO.getById(booking.getId());
        System.out.println("Get by id: " + byId);

        booking.setStatus("confirmed");
        System.out.println("Updating booking status to confirmed");
        bookingDAO.update(booking);

        Booking updated = bookingDAO.getById(booking.getId());
        System.out.println("After update: " + updated);

        List<Booking> byCustomer = bookingDAO.getByCustomerId(customer.getId());
        System.out.println("Bookings by customer:");
        for (Booking item : byCustomer) {
            System.out.println("  " + item);
        }

        List<Booking> byStatus = bookingDAO.getByStatus("confirmed");
        System.out.println("Bookings by status 'confirmed':");
        for (Booking item : byStatus) {
            System.out.println("  " + item);
        }

        System.out.println("Deleting booking, room, and customer");
        bookingDAO.delete(booking.getId());
        roomDAO.delete(room.getId());
        customerDAO.delete(customer.getId());
        System.out.println("After delete: " + bookingDAO.getById(booking.getId()));
    }

    private static void demoPaymentDao() throws Exception {
        CustomerDAO customerDAO = new CustomerDAO();
        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
        PaymentDAO paymentDAO = new PaymentDAO();

        Customer customer = new Customer(0, "Payment Customer " + RUN_ID, "payment_customer_" + RUN_ID + "@example.com", "0222222222", "Payment Address");
        Room room = new Room(0, "P-" + RUN_ID, "suite", 300.0, true);
        customerDAO.add(customer);
        roomDAO.add(room);

        Booking booking = new Booking(0, customer.getId(), room.getId(), "2026-05-20", "2026-05-22", 600.0, "pending");
        bookingDAO.add(booking);

        Payment payment = new Payment(0, booking.getId(), 600.0, LocalDate.now().toString(), "cash", "paid");
        System.out.println("\n--- Payment DAO Demo ---");
        System.out.println("Adding payment for booking id " + booking.getId());
        paymentDAO.add(payment);
        System.out.println("Saved payment id: " + payment.getId());

        Payment byId = paymentDAO.getById(payment.getId());
        System.out.println("Get by id: " + byId);

        payment.setStatus("refunded");
        System.out.println("Updating payment status to refunded");
        paymentDAO.update(payment);

        Payment updated = paymentDAO.getById(payment.getId());
        System.out.println("After update: " + updated);

        List<Payment> byBooking = paymentDAO.getByBookingId(booking.getId());
        System.out.println("Payments by booking:");
        for (Payment item : byBooking) {
            System.out.println("  " + item);
        }

        double revenue = paymentDAO.getTotalRevenue();
        System.out.println("Total revenue: " + revenue);

        System.out.println("Deleting payment, booking, room, and customer");
        paymentDAO.delete(payment.getId());
        bookingDAO.delete(booking.getId());
        roomDAO.delete(room.getId());
        customerDAO.delete(customer.getId());
        System.out.println("After delete: " + paymentDAO.getById(payment.getId()));
    }
}