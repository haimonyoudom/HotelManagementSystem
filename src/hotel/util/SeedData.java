package hotel.util;

import hotel.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeedData {

    public static void main(String[] args) throws Exception {
        Connection conn = DBConnection.getConnection();
        String hashedPassword = PasswordHasher.hash("admin123");

        // ── USERS ────────────────────────────────────────────────────
        String insertUser = "INSERT OR IGNORE INTO users (username, password, role, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
            Object[][] users = {
                {"admin",  "admin"},
                {"staff1", "staff"},
                {"staff2", "staff"},
            };
            for (Object[] u : users) {
                ps.setString(1, (String) u[0]);
                ps.setString(2, hashedPassword);
                ps.setString(3, (String) u[1]);
                ps.setString(4, "2024-01-01 00:00:00");
                ps.executeUpdate();
            }
        }
        System.out.println("✓ Users inserted");

        // ── STAFF (look up user_id dynamically) ──────────────────────
        String insertStaff = "INSERT OR IGNORE INTO staff (name, position, salary, user_id) VALUES (?, ?, ?, ?)";
        String getUserId   = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(insertStaff);
             PreparedStatement idPs = conn.prepareStatement(getUserId)) {

            Object[][] staffList = {
                {"staff1", "Alice Johnson", "Receptionist", 800.00},
                {"staff2", "Bob Smith",     "Housekeeping",  700.00},
            };

            for (Object[] s : staffList) {
                idPs.setString(1, (String) s[0]);
                ResultSet rs = idPs.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    ps.setString(1, (String) s[1]);
                    ps.setString(2, (String) s[2]);
                    ps.setDouble(3, (Double)  s[3]);
                    ps.setInt(4, userId);
                    ps.executeUpdate();
                    System.out.println("✓ Staff inserted: " + s[1] + " (user_id=" + userId + ")");
                }
            }
        }

        // ── ROOMS ────────────────────────────────────────────────────
        String insertRoom = "INSERT OR IGNORE INTO rooms (room_number, type, price_per_night, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertRoom)) {
            Object[][] rooms = {
                {"101", "Single", 50.00,  "available"},
                {"102", "Double", 80.00,  "available"},
                {"103", "Suite",  150.00, "available"},
                {"104", "Single", 50.00,  "occupied"},
                {"105", "Double", 80.00,  "available"},
            };
            for (Object[] r : rooms) {
                ps.setString(1, (String) r[0]);
                ps.setString(2, (String) r[1]);
                ps.setDouble(3, (Double)  r[2]);
                ps.setString(4, (String)  r[3]);
                ps.executeUpdate();
            }
        }
        System.out.println("✓ Rooms inserted");
        System.out.println("\nDone! Login with admin / staff1 / staff2, password: admin123");
    }
}