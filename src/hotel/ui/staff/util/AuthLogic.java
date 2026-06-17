package hotel.ui.staff.util;

import hotel.service.AuthService;
import hotel.config.DBConnection;
import hotel.dao.UserDAO;
import hotel.dao.CustomerDAO;
import hotel.model.User;
import hotel.model.Customer;
import hotel.ui.staff.StaffDashboard;
import hotel.ui.admin.AdminDashboard;
import hotel.util.PasswordHasher;
import hotel.ui.customer.CustomerDashboard;

import javax.swing.*;
import java.awt.Window;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthLogic {

    private AuthLogic() {
    }

    // ── LOGIN LOGIC ─────────────────────────────────────────────────
    public static void handleLogin(String username, String password, JTextField userField,
            JPasswordField passField, JLabel statusLbl) {
        if (username.isEmpty() || password.isEmpty()) {
            setError(statusLbl, "Please enter username and password.");
            return;
        }
        if (username.length() < 3) {
            setError(statusLbl, "Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 8) {
            setError(statusLbl, "Password must be at least 8 characters.");
            return;
        }

        try {
            DBConnection.getConnection();
            AuthService authService = new AuthService();
            if (authService.login(username, password)) {
                User currentUser = authService.getCurrentUser();
                if (currentUser == null) {
                    setError(statusLbl, "Login failed: user not found.");
                    return;
                }

                String role = currentUser.getRole() != null ? currentUser.getRole().toLowerCase() : "";

                setSuccess(statusLbl, "Login successful! Welcome back!");
                userField.setText("");
                passField.setText("");

                // Close the login window first
                Window loginWindow = SwingUtilities.getWindowAncestor(statusLbl);
                if (loginWindow != null)
                    loginWindow.dispose();

                switch (role) {
                    case "admin":
                        SwingUtilities.invokeLater(() -> new AdminDashboard(currentUser).setVisible(true));
                        break;
                    case "staff":
                        SwingUtilities.invokeLater(() -> new StaffDashboard(currentUser).setVisible(true));
                        break;
                    case "customer":
                        SwingUtilities.invokeLater(() -> new CustomerDashboard(currentUser).setVisible(true));
                        break;
                    default:
                        setError(statusLbl, "Unknown role: " + role + ". Access denied.");
                        break;
                }

            } else {
                setError(statusLbl, "Invalid username or password.");
            }
        } catch (Exception ex) {
            setError(statusLbl, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── SIGNUP LOGIC ────────────────────────────────────────────────
    public static void handleSignup(String name, String email, String phone, String address,
            String password, String confirm, JTextField[] fields, JLabel statusLbl) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()
                || confirm.isEmpty()) {
            setError(statusLbl, "Please fill in all fields.");
            return;
        }
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            setError(statusLbl, "Please enter a valid email address.");
            return;
        }
        if (!phone.matches("\\d{9,11}")) {
            setError(statusLbl, "Phone must be 9-11 digits only.");
            return;
        }
        if (password.length() < 8) {
            setError(statusLbl, "Password must be at least 8 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            setError(statusLbl, "Passwords do not match.");
            return;
        }
        try {
            DBConnection.getConnection();
            Customer c = new Customer();
            c.setName(name);
            c.setEmail(email);
            c.setPhone(phone);
            c.setAddress(address);
            new CustomerDAO().add(c);
            User u = new User();
            u.setUsername(name);
            u.setPasswordHash(PasswordHasher.hash(password));
            u.setRole("customer");
            u.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            new UserDAO().add(u);
            setSuccess(statusLbl, "Account created successfully. You can sign in now.");
            for (JTextField f : fields)
                f.setText("");
        } catch (Exception ex) {
            setError(statusLbl, ex.getMessage() != null && ex.getMessage().contains("UNIQUE")
                    ? "Email already exists. Please use a different email."
                    : "Error creating account: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── STATUS MESSAGE HELPERS ──────────────────────────────────────
    public static void setError(JLabel statusLbl, String msg) {
        statusLbl.setForeground(UIConstants.ERR_COLOR);
        statusLbl.setText(msg);
    }

    public static void setSuccess(JLabel statusLbl, String msg) {
        statusLbl.setForeground(UIConstants.OK_COLOR);
        statusLbl.setText(msg);
    }
}