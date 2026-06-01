package hotel.ui.common;

import hotel.model.User;
import hotel.service.AuthService;
import hotel.ui.admin.AdminDashboard;
import hotel.ui.customer.CustomerDashboard;
import hotel.ui.staff.StaffDashboard;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = UITheme.textField();
    private final JPasswordField passwordField = UITheme.passwordField();
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        UITheme.applyGlobalFont();

        setTitle("Hotel Management System - Login");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 560));

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(UITheme.BG);

        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.SIDEBAR);

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("HMS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Hotel Management System");
        subtitle.setFont(UITheme.HEADER_FONT);
        subtitle.setForeground(new Color(220, 224, 235));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("Manage rooms, bookings, customers and payments.");
        description.setForeground(new Color(190, 196, 210));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(12));
        brand.add(subtitle);
        brand.add(Box.createVerticalStrut(8));
        brand.add(description);

        left.add(brand);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(380, 420));
        card.setLayout(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = UITheme.title("Welcome Back");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel muted = UITheme.muted("Login to continue to your dashboard");
        muted.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(muted);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);

        gbc.gridy++;
        form.add(usernameField, gbc);

        gbc.gridy++;
        form.add(new JLabel("Password"), gbc);

        gbc.gridy++;
        form.add(passwordField, gbc);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        buttons.setOpaque(false);

        JButton loginBtn = UITheme.primaryButton("Login");
        JButton registerBtn = UITheme.secondaryButton("Register as Customer");

        buttons.add(loginBtn);
        buttons.add(registerBtn);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        right.add(card);

        root.add(left);
        root.add(right);

        add(root);

        getRootPane().setDefaultButton(loginBtn);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> {
            RegisterDialog dialog = new RegisterDialog(this);
            dialog.setVisible(true);
        });
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean loggedIn = authService.login(username, password);

            if (!loggedIn) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = authService.getCurrentUser();

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Unable to load user account.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFrame dashboard = createDashboard(user);

            if (dashboard == null) {
                JOptionPane.showMessageDialog(this, "Unknown role: " + user.getRole(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dashboard.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(passwordChars, '\0');
            passwordField.setText("");
        }
    }

    private JFrame createDashboard(User user) {
        String role = user.getRole();

        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AdminDashboard(user);
        }

        if ("STAFF".equalsIgnoreCase(role)) {
            return new StaffDashboard(user);
        }

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            return new CustomerDashboard(user);
        }

        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}