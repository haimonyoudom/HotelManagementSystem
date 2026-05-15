package hotel.main;

import hotel.config.DBConnection;
import hotel.config.DBInitializer;
import hotel.dao.CustomerDAO;
import hotel.dao.StaffDAO;
import hotel.dao.UserDAO;
import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Room;
import hotel.model.Staff;
import hotel.model.User;
import hotel.service.AuthService;
import hotel.service.BookingService;
import hotel.service.PaymentService;
import hotel.service.ReportService;
import hotel.service.RoomService;
import hotel.util.DateUtil;
import hotel.util.PasswordHasher;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final UserDAO userDAO = new UserDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final StaffDAO staffDAO = new StaffDAO();

    private static final AuthService authService = new AuthService();
    private static final RoomService roomService = new RoomService();
    private static final BookingService bookingService = new BookingService();
    private static final PaymentService paymentService = new PaymentService();
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        DBInitializer.initialize();
        ensureDefaultAdmin();

        printBanner();

        boolean running = true;
        while (running) {
            if (authService.getCurrentUser() == null) {
                printAuthMenu();
                int choice = readInt("Choose: ");
                try {
                    switch (choice) {
                        case 1: handleLogin(); break;
                        case 2: handleRegister(); break;
                        case 0:
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                printMainMenu();
                int choice = readInt("Choose: ");
                try {
                    switch (choice) {
                        case 1: roomMenu(); break;
                        case 2: bookingMenu(); break;
                        case 3: paymentMenu(); break;
                        case 4: customerMenu(); break;
                        case 5:
                            if (isAdmin()) staffMenu();
                            break;
                        case 6:
                            if (isAdmin()) userMenu();
                            break;
                        case 7:
                            if (isAdmin()) reportMenu();
                            break;
                        case 8:
                            authService.logout();
                            System.out.println("\nLogged out successfully.\n");
                            break;
                        case 0:
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        DBConnection.closeConnection();
        SCANNER.close();
        System.out.println("Goodbye.");
    }

    // ─── Banner ──────────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║       HOTEL MANAGEMENT SYSTEM  v2.0     ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    // ─── Auth Menu (before login) ─────────────────────────────────────────────

    private static void printAuthMenu() {
        System.out.println("=== Welcome ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
    }

    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        boolean success = authService.login(username, password);
        if (success) {
            User user = authService.getCurrentUser();
            System.out.println("\nWelcome back, " + user.getUsername() + "! [" + user.getRole() + "]");
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
        System.out.println();
    }

    private static void handleRegister() throws SQLException {
        System.out.println("\n--- Register ---");
        System.out.println("Register as:");
        System.out.println("1. Customer");
        System.out.println("2. Staff");
        int type = readInt("Choose: ");

        if (type != 1 && type != 2) {
            System.out.println("Invalid option.");
            return;
        }

        System.out.println();
        String username = readLine("Username: ");

        if (userDAO.getByUsername(username) != null) {
            System.out.println("Username already taken. Please choose another.");
            return;
        }

        String password = readLine("Password: ");
        String confirmPassword = readLine("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration cancelled.");
            return;
        }

        String role = (type == 2) ? "staff" : "customer";

        // Create the user account (role is always customer or staff — admin assigns admin manually)
        User newUser = new User(0, username, PasswordHasher.hash(password), role, DateUtil.today());
        userDAO.add(newUser);

        // Collect profile info based on role
        if (type == 1) {
            // Customer profile
            System.out.println("\nFill in your customer profile:");
            String name    = readLine("Full Name: ");
            String email   = readLine("Email: ");
            String phone   = readLine("Phone: ");
            String address = readLine("Address: ");

            Customer customer = new Customer(0, name, email, phone, address);
            customerDAO.add(customer);
            System.out.println("\nCustomer account created! You can now login.");

        } else {
            // Staff profile
            System.out.println("\nFill in your staff profile:");
            String name     = readLine("Full Name: ");
            String position = readLine("Position: ");
            double salary   = readDouble("Salary: ");

            Staff staff = new Staff(0, name, position, salary, newUser.getId());
            staffDAO.add(staff);
            System.out.println("\nStaff account created! You can now login.");
        }

        System.out.println();
    }

    // ─── Main Menu (after login) ──────────────────────────────────────────────

    private static void printMainMenu() {
        User user = authService.getCurrentUser();
        System.out.println("\n=== Main Menu  [" + user.getUsername() + " / " + user.getRole() + "] ===");
        System.out.println("1. Rooms");
        System.out.println("2. Bookings");
        System.out.println("3. Payments");
        System.out.println("4. Customers");
        if (isAdmin()) {
            System.out.println("5. Staff        (admin)");
            System.out.println("6. Users        (admin)");
            System.out.println("7. Reports      (admin)");
        }
        System.out.println("8. Logout");
        System.out.println("0. Exit");
    }

    // ─── Rooms ───────────────────────────────────────────────────────────────

    private static void roomMenu() {
        System.out.println("\n--- Room Menu ---");
        System.out.println("1. List all rooms");
        System.out.println("2. List available rooms");
        System.out.println("3. Search by type");
        System.out.println("4. Add room        (admin)");
        System.out.println("5. Update room     (admin)");
        System.out.println("6. Delete room     (admin)");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                roomService.getAllRooms().forEach(System.out::println);
                break;
            case 2:
                roomService.getAvailableRooms().forEach(System.out::println);
                break;
            case 3:
                roomService.searchRooms(readLine("Type: ")).forEach(System.out::println);
                break;
            case 4:
                if (!isAdmin()) break;
                boolean added = roomService.addRoom(
                        readLine("Room number: "),
                        readLine("Type: "),
                        readDouble("Price per night: ")
                );
                System.out.println(added ? "Room added." : "Failed to add room.");
                break;
            case 5:
                if (!isAdmin()) break;
                Room existing = findRoomById(readInt("Room ID: "));
                if (existing == null) { System.out.println("Room not found."); break; }
                existing.setRoomNumber(readLine("New room number: "));
                existing.setType(readLine("New type: "));
                existing.setPricePerNight(readDouble("New price: "));
                existing.setAvailable(readBoolean("Is available (true/false): "));
                System.out.println(roomService.updateRoom(existing) ? "Room updated." : "Update failed.");
                break;
            case 6:
                if (!isAdmin()) break;
                System.out.println(roomService.deleteRoom(readInt("Room ID: ")) ? "Room deleted." : "Delete failed.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Bookings ─────────────────────────────────────────────────────────────

    private static void bookingMenu() {
        System.out.println("\n--- Booking Menu ---");
        System.out.println("1. Create booking");
        System.out.println("2. List all bookings");
        System.out.println("3. My bookings");
        System.out.println("4. Confirm booking  (admin/staff)");
        System.out.println("5. Cancel booking");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                Booking booking = bookingService.createBooking(
                        readInt("Customer ID: "),
                        readInt("Room ID: "),
                        readLine("Check-in  (YYYY-MM-DD): "),
                        readLine("Check-out (YYYY-MM-DD): ")
                );
                System.out.println(booking == null ? "Could not create booking." : "Booking created:\n" + booking);
                break;
            case 2:
                bookingService.getAllBookings().forEach(System.out::println);
                break;
            case 3:
                bookingService.getBookingsForCustomer(readInt("Customer ID: ")).forEach(System.out::println);
                break;
            case 4:
                if (!isAdminOrStaff()) { System.out.println("Access denied."); break; }
                System.out.println(bookingService.confirmBooking(readInt("Booking ID: ")) ? "Booking confirmed." : "Booking not found.");
                break;
            case 5:
                System.out.println(bookingService.cancelBooking(readInt("Booking ID: ")) ? "Booking cancelled." : "Booking not found.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Payments ─────────────────────────────────────────────────────────────

    private static void paymentMenu() {
        System.out.println("\n--- Payment Menu ---");
        System.out.println("1. Process payment");
        System.out.println("2. Refund payment   (admin)");
        System.out.println("3. Payments by booking");
        System.out.println("4. Total revenue    (admin)");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                System.out.println(paymentService.processPayment(
                        readInt("Booking ID: "),
                        readDouble("Amount: "),
                        readLine("Method (cash/card/online): ")
                ));
                break;
            case 2:
                if (!isAdmin()) { System.out.println("Access denied."); break; }
                System.out.println(paymentService.refundPayment(readInt("Payment ID: ")) ? "Refunded." : "Payment not found.");
                break;
            case 3:
                paymentService.getPaymentsForBooking(readInt("Booking ID: ")).forEach(System.out::println);
                break;
            case 4:
                if (!isAdmin()) { System.out.println("Access denied."); break; }
                System.out.println("Total revenue: " + paymentService.getTotalRevenue());
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Customers ────────────────────────────────────────────────────────────

    private static void customerMenu() throws SQLException {
        System.out.println("\n--- Customer Menu ---");
        System.out.println("1. List customers");
        System.out.println("2. Search by name");
        System.out.println("3. Add customer     (admin/staff)");
        System.out.println("4. Delete customer  (admin)");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                customerDAO.getAll().forEach(System.out::println);
                break;
            case 2:
                customerDAO.searchByName(readLine("Keyword: ")).forEach(System.out::println);
                break;
            case 3:
                if (!isAdminOrStaff()) { System.out.println("Access denied."); break; }
                Customer customer = new Customer(
                        0,
                        readLine("Name: "),
                        readLine("Email: "),
                        readLine("Phone: "),
                        readLine("Address: ")
                );
                customerDAO.add(customer);
                System.out.println("Customer added with id=" + customer.getId());
                break;
            case 4:
                if (!isAdmin()) { System.out.println("Access denied."); break; }
                customerDAO.delete(readInt("Customer ID: "));
                System.out.println("Customer deleted.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Staff (admin only) ───────────────────────────────────────────────────

    private static void staffMenu() throws SQLException {
        System.out.println("\n--- Staff Menu --- (admin)");
        System.out.println("1. List staff");
        System.out.println("2. Filter by position");
        System.out.println("3. Delete staff");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                staffDAO.getAll().forEach(System.out::println);
                break;
            case 2:
                staffDAO.getByPosition(readLine("Position: ")).forEach(System.out::println);
                break;
            case 3:
                staffDAO.delete(readInt("Staff ID: "));
                System.out.println("Staff deleted.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Users (admin only) ───────────────────────────────────────────────────

    private static void userMenu() throws SQLException {
        System.out.println("\n--- User Menu --- (admin)");
        System.out.println("1. List users");
        System.out.println("2. Promote to admin");
        System.out.println("3. Delete user");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1:
                userDAO.getAll().forEach(System.out::println);
                break;
            case 2:
                String target = readLine("Username to promote: ");
                User u = userDAO.getByUsername(target);
                if (u == null) { System.out.println("User not found."); break; }
                u.setRole("admin");
                userDAO.update(u);
                System.out.println(target + " is now an admin.");
                break;
            case 3:
                userDAO.delete(readInt("User ID: "));
                System.out.println("User deleted.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Reports (admin only) ─────────────────────────────────────────────────

    private static void reportMenu() {
        System.out.println("\n--- Report Menu --- (admin)");
        System.out.println("1. Occupancy report");
        System.out.println("2. Revenue report");
        System.out.println("3. Booking summary");
        System.out.println("4. Export report to .txt");
        int choice = readInt("Choose: ");

        String content;
        switch (choice) {
            case 1:
                System.out.println(reportService.generateOccupancyReport());
                break;
            case 2:
                System.out.println(reportService.generateRevenueReport());
                break;
            case 3:
                System.out.println(reportService.generateBookingSummary());
                break;
            case 4:
                System.out.println("1. Occupancy  2. Revenue  3. Booking summary");
                int rt = readInt("Report type: ");
                if (rt == 1)      content = reportService.generateOccupancyReport();
                else if (rt == 2) content = reportService.generateRevenueReport();
                else if (rt == 3) content = reportService.generateBookingSummary();
                else { System.out.println("Invalid."); return; }
                String filename = readLine("Filename: ");
                reportService.exportReport(content, filename);
                System.out.println("Report exported.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void ensureDefaultAdmin() {

    createAdmin("admin", "admin123");
    createAdmin("admin2", "admin123");
    createAdmin("admin3", "admin123");
}

private static void createAdmin(String username, String password) {

    try {

        if (userDAO.getByUsername(username) == null) {

            User admin = new User(
                    0,
                    username,
                    PasswordHasher.hash(password),
                    "admin",
                    DateUtil.today()
            );

            userDAO.add(admin);

            System.out.println(
                    "Default admin created -> " +
                    username + " / " + password
            );
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Failed to create admin: " + username,
                e
        );
    }
}

    private static boolean isAdmin() {

    User u = authService.getCurrentUser();

    System.out.println("ROLE = [" + u.getRole() + "]");

    return u != null &&
           "ADMIN".equalsIgnoreCase(u.getRole().trim());
}

    private static boolean isAdminOrStaff() {
        User u = authService.getCurrentUser();
        return u != null && (u.getRole().equals("admin") || u.getRole().equals("staff"));
    }

    private static Room findRoomById(int id) {
        return roomService.getAllRooms().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid integer."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    private static boolean readBoolean(String prompt) {
        while (true) {
            String v = readLine(prompt).trim().toLowerCase();
            if ("true".equals(v) || "yes".equals(v) || "y".equals(v)) return true;
            if ("false".equals(v) || "no".equals(v) || "n".equals(v)) return false;
            System.out.println("Enter true/false.");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine();
    }
}