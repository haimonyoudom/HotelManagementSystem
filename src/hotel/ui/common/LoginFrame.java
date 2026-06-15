package hotel.ui.common;

import hotel.service.AuthService;
import hotel.dao.UserDAO;
import hotel.model.User;
import hotel.util.PasswordHasher;
import hotel.ui.customer.CustomerDashboard;
import hotel.ui.admin.AdminDashboard;
import hotel.ui.staff.StaffDashboard;
import hotel.config.DBInitializer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginFrame extends JFrame {

    static JPanel loginPanel;
    static JPanel signupPanel;
    static JLabel loginStatus;
    static JLabel signupStatus;

    // ── Light Mode Palette ─────────────────────────────────────────────────
    static final Color BG_MAIN       = new Color(250, 250, 250);
    static final Color BG_CARD       = new Color(255, 255, 255);
    static final Color BG_ELEVATED   = new Color(235, 241, 255);
    static final Color TXT_PRIMARY   = new Color(20, 20, 20);
    static final Color TXT_SECONDARY = new Color(90, 90, 90);
    static final Color BLUE          = new Color(59, 130, 246);
    static final Color BORDER        = new Color(220, 220, 220);

    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  18);
    static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public LoginFrame() {
        initializeFrame();
    }

    public static void main(String[] args) {
        try {
            DBInitializer.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database initialization error", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private void initializeFrame() {
        setTitle("Hotel Management System - Login");
        setSize(980, 760);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_MAIN);

        loginPanel = new JPanel();
        signupPanel = new JPanel();

        loginPanel.setLayout(null);
        loginPanel.setBounds(0, 0, 980, 760);
        loginPanel.setBackground(BG_MAIN);

        signupPanel.setLayout(null);
        signupPanel.setBounds(0, 0, 980, 760);
        signupPanel.setBackground(BG_MAIN);

        // Login right background
        JPanel loginRightBg = new JPanel();
        loginRightBg.setBounds(450, 0, 530, 760);
        loginRightBg.setBackground(BG_CARD);
        loginRightBg.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));
        loginPanel.add(loginRightBg);

        // Signup right background
        JPanel signupRightBg = new JPanel();
        signupRightBg.setBounds(450, 0, 530, 760);
        signupRightBg.setBackground(BG_CARD);
        signupRightBg.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));
        signupPanel.add(signupRightBg);

        add(loginPanel);
        add(signupPanel);

        buildLoginScreen();
        buildSignupScreen();

        loginPanel.setVisible(true);
        signupPanel.setVisible(false);

        setLocationRelativeTo(null);
    }

    private static void buildLoginScreen() {
        JLabel title = new JLabel("Hotel Management System");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(20, 33, 61));
        title.setBounds(46, 30, 500, 48);
        loginPanel.add(title);

        JLabel subtitle = new JLabel("Sign in to your dashboard");
        subtitle.setBounds(46, 80, 500, 26);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 110, 130));
        loginPanel.add(subtitle);

        JPanel accent = new JPanel();
        accent.setBounds(46, 115, 60, 3);
        accent.setBackground(new Color(15, 84, 175));
        loginPanel.add(accent);

        JLabel emailLabel = new JLabel("Username");
        emailLabel.setBounds(60, 160, 180, 24);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(60, 190, 360, 45);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(new Color(250, 251, 253));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        loginPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(60, 260, 180, 24);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(60, 288, 360, 45);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(250, 251, 253));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        loginPanel.add(passwordField);

        JButton signinButton = new JButton("Sign in");
        signinButton.setBounds(60, 360, 360, 52);
        signinButton.setFont(new Font("Arial", Font.BOLD, 16));
        signinButton.setForeground(Color.WHITE);
        signinButton.setBackground(new Color(0x16, 0x2D, 0x3A));
        signinButton.setBorder(null);
        signinButton.setBorder(BorderFactory.createEmptyBorder());
        signinButton.setFocusPainted(false);
        loginPanel.add(signinButton);

        loginStatus = new JLabel(" ");
        loginStatus.setBounds(60, 410, 360, 24);
        loginStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        loginStatus.setForeground(new Color(170, 34, 62));
        loginPanel.add(loginStatus);

        JPanel loginFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginFooter.setBounds(60, 430, 360, 24);
        loginFooter.setOpaque(false);

        JLabel createAccountText = new JLabel("Create new account? ");
        createAccountText.setFont(new Font("Arial", Font.PLAIN, 14));
        createAccountText.setForeground(new Color(90, 100, 120));
        loginFooter.add(createAccountText);

        JLabel signUpLink = new JLabel("Sign up");
        signUpLink.setFont(new Font("Arial", Font.BOLD, 14));
        signUpLink.setForeground(new Color(15, 84, 175));
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginFooter.add(signUpLink);

        loginPanel.add(loginFooter);

        signinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Please enter username and password.");
                    return;
                }
                try {
                    AuthService authService = new AuthService();
                    boolean success = authService.login(username, password);
                    if (success) {
                        User user = authService.getCurrentUser();
                        loginStatus.setForeground(new Color(15, 84, 175));
                        loginStatus.setText("Signed in successfully. Welcome back!");
                        new Thread(() -> {
                            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                            openDashboard(user);
                        }).start();
                    } else {
                        loginStatus.setForeground(new Color(170, 34, 62));
                        loginStatus.setText("Invalid username or password.");
                    }
                } catch (Exception ex) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Login error: " + ex.getMessage());
                }
            }
        });

        signUpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loginPanel.setVisible(false);
                signupPanel.setVisible(true);
            }
        });

        buildIllustrationPanel(loginPanel);
    }

    private static void buildSignupScreen() {
        JLabel title = new JLabel("Create your account");
        title.setBounds(46, 30, 400, 48);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(20, 33, 61));
        signupPanel.add(title);

        JLabel subtitle = new JLabel("Sign up to start managing bookings and customers.");
        subtitle.setBounds(46, 80, 400, 26);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 110, 130));
        signupPanel.add(subtitle);

        JPanel accent = new JPanel();
        accent.setBounds(46, 115, 60, 3);
        accent.setBackground(new Color(15, 84, 175));
        signupPanel.add(accent);

        JLabel emailLabel = new JLabel("Username");
        emailLabel.setBounds(60, 130, 180, 24);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signupPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(60, 158, 360, 45);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(new Color(250, 251, 253));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        signupPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(60, 220, 180, 24);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signupPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(60, 248, 360, 45);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(250, 251, 253));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        signupPanel.add(passwordField);

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setBounds(60, 308, 180, 24);
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signupPanel.add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(60, 336, 360, 45);
        confirmField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmField.setBackground(new Color(250, 251, 253));
        confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        signupPanel.add(confirmField);

        JLabel roleLabel = new JLabel("Select your Role");
        roleLabel.setBounds(60, 396, 200, 24);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signupPanel.add(roleLabel);

        JComboBox<String> roleDropdown = new JComboBox<>(new String[] { "----", "Customer", "Staff" });
        roleDropdown.setBounds(60, 424, 200, 35);
        roleDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        roleDropdown.setBackground(new Color(250, 251, 253));
        signupPanel.add(roleDropdown);

        JButton createButton = new JButton("Sign up");
        createButton.setBounds(60, 480, 360, 52);
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(0x16, 0x2D, 0x3A));
        createButton.setBorder(null);
        createButton.setBorder(BorderFactory.createEmptyBorder());
        createButton.setFocusPainted(false);
        signupPanel.add(createButton);

        signupStatus = new JLabel(" ");
        signupStatus.setBounds(60, 530, 360, 24);
        signupStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        signupStatus.setForeground(new Color(170, 34, 62));
        signupPanel.add(signupStatus);

        JPanel signupFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupFooter.setBounds(60, 548, 360, 24);
        signupFooter.setOpaque(false);

        JLabel alreadyText = new JLabel("Already have an account? ");
        alreadyText.setFont(new Font("Arial", Font.PLAIN, 14));
        alreadyText.setForeground(new Color(90, 100, 120));
        signupFooter.add(alreadyText);

        JLabel signInLink = new JLabel("Sign in");
        signInLink.setFont(new Font("Arial", Font.BOLD, 14));
        signInLink.setForeground(new Color(15, 84, 175));
        signInLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupFooter.add(signInLink);

        signupPanel.add(signupFooter);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirm = new String(confirmField.getPassword());
                String role = ((String) roleDropdown.getSelectedItem()).trim();

                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || role.equals("----")) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Please fill in all fields.");
                    return;
                }
                if (!password.equals(confirm)) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Passwords do not match.");
                    return;
                }
                try {
                    String hashedPassword = PasswordHasher.hash(password);
                    String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    User newUser = new User(0, email, hashedPassword, role, createdAt);
                    UserDAO userDAO = new UserDAO();
                    userDAO.add(newUser);
                    signupStatus.setForeground(new Color(15, 84, 175));
                    signupStatus.setText("Account created! You can sign in now.");
                    emailField.setText("");
                    passwordField.setText("");
                    confirmField.setText("");
                    roleDropdown.setSelectedIndex(0);
                } catch (SQLException ex) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    if (ex.getMessage().contains("UNIQUE")) {
                        signupStatus.setText("Username already exists.");
                    } else {
                        signupStatus.setText("Error creating account: " + ex.getMessage());
                    }
                }
            }
        });

        signInLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                signupPanel.setVisible(false);
                loginPanel.setVisible(true);
            }
        });

        buildIllustrationPanel(signupPanel);
    }

    private static void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            String role = user.getRole() != null ? user.getRole().trim().toLowerCase() : "";
            switch (role) {
                case "admin":
                    new AdminDashboard(user).setVisible(true);
                    break;
                case "staff":
                    new StaffDashboard(user).setVisible(true);
                    break;
                case "customer":
                    new CustomerDashboard(user).setVisible(true);
                    break;
                default:
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Unknown role: " + user.getRole());
                    return;
            }

            Window loginWindow = SwingUtilities.getWindowAncestor(loginPanel);
            if (loginWindow != null) {
                loginWindow.dispose();
            }
        });
    }

    private static void buildIllustrationPanel(JPanel parent) {
        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(460, 0, 520, 760);
        imagePanel.setBackground(new Color(245, 248, 252));
        imagePanel.setLayout(null);

        String[] imagePaths = {
                "src/hotel/images/resources/hotel1.jpg",
                "src/hotel/images/resources/hotel2.jpg",
                "src/hotel/images/resources/hotel3.jpg"
        };

        int[] xPositions = { 50, 200, 350 };
        int[] yPositions = { 120, 80, 40 };
        int[] widths = { 140, 140, 140 };
        int[] heights = { 480, 560, 640 };

        for (int i = 0; i < 3; i++) {
            final String path = imagePaths[i];
            final int w = widths[i];
            final int h = heights[i];

            Image[] holder = new Image[1];
            try {
                holder[0] = new ImageIcon(path).getImage();
            } catch (Exception e) {
                holder[0] = null;
            }

            final Image img = holder[0];

            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    if (img != null) {
                        int imgW = img.getWidth(this);
                        int imgH = img.getHeight(this);
                        double scaleX = (double) w / imgW;
                        double scaleY = (double) h / imgH;
                        double scale = Math.max(scaleX, scaleY);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int offsetX = (w - drawW) / 2;
                        int offsetY = (h - drawH) / 2;
                        g2.drawImage(img, offsetX, offsetY, drawW, drawH, this);
                    } else {
                        g2.setColor(new Color(37, 114, 198));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    }
                    g2.dispose();
                }
            };

            card.setBounds(xPositions[i], yPositions[i], w, h);
            card.setOpaque(false);
            imagePanel.add(card);
        }

        parent.add(imagePanel, 0);
    }
}
