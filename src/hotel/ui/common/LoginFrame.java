package hotel.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import hotel.service.AuthService;
import hotel.service.BookingService;
import hotel.config.DBConnection;
import hotel.dao.UserDAO;
import hotel.dao.CustomerDAO;
import hotel.model.User;
import hotel.model.Customer;
import hotel.util.PasswordHasher;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class LoginFrame {

    // ── Scaling fields ──────────────────────────────────────
    public static JPanel loginPanel;
    public static JPanel signupPanel;
    public static JLabel loginStatus;
    public static JLabel signupStatus;
    public static double scaleX = 1.0;
    public static double scaleY = 1.0;
    public static int screenWidth = 980;
    public static int screenHeight = 760;
    public static int leftPanelWidth;

    // ── Frame dimensions ──────
    static final int FRAME_WIDTH = 1400;
    static final int FRAME_HEIGHT = 900;
    static final int SIDEBAR_WIDTH = 220;
    static final int CONTENT_X = SIDEBAR_WIDTH;
    static final int CONTENT_WIDTH = FRAME_WIDTH - SIDEBAR_WIDTH;

    public static void buildLoginScreen() {
        int cardW = sx(420); // ← card width, adjust to taste
        int cardH = sy(480); // ← card height
        int cardX = sx(40); // ← distance from left edge
        int cardY = (screenHeight - cardH) / 3; // vertically centered

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(cardX, cardY, cardW, cardH);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        loginPanel.add(card);

        int marginX = sx(20);
        int fieldW = cardW - sx(40);
        loginPanel.setBackground(new Color(245, 248, 252));

        JLabel title = new JLabel("Hotel Management System");
        title.setBounds(marginX, sy(22), cardW - sx(40), sy(44));
        title.setFont(new Font("Arial", Font.BOLD, sy(28)));
        title.setForeground(new Color(20, 33, 61));
        card.add(title);

        JLabel subtitle = new JLabel("Sign in to your dashboard");
        subtitle.setBounds(marginX, sy(68), cardW - sx(40), sy(24));
        subtitle.setFont(new Font("Arial", Font.PLAIN, sy(15)));
        subtitle.setForeground(new Color(100, 110, 130));
        card.add(subtitle);

        JPanel accent = new JPanel();
        accent.setBounds(marginX, sy(100), sx(60), sy(3));
        accent.setBackground(new Color(15, 84, 175));
        card.add(accent);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(marginX, sy(155), fieldW, sy(22));
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(marginX, sy(177), fieldW, sy(32));
        usernameField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        usernameField.setBackground(new Color(250, 251, 253));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(marginX, sy(219), fieldW, sy(22));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(marginX, sy(241), fieldW, sy(32));
        passwordField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        passwordField.setBackground(new Color(250, 251, 253));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(passwordField);

        JButton signinButton = new JButton("Sign in");
        signinButton.setBounds(marginX, sy(293), fieldW, sy(48));
        signinButton.setFont(new Font("Arial", Font.BOLD, sy(15)));
        signinButton.setForeground(Color.WHITE);
        signinButton.setBackground(new Color(0x16, 0x2D, 0x3A));
        signinButton.setBorder(BorderFactory.createEmptyBorder());
        signinButton.setFocusPainted(false);
        signinButton.putClientProperty("JButton.buttonType", "roundRect");
        card.add(signinButton);

        loginStatus = new JLabel(" ");
        loginStatus.setBounds(marginX, sy(349), fieldW, sy(22));
        loginStatus.setFont(new Font("Arial", Font.PLAIN, sy(13)));
        loginStatus.setForeground(new Color(170, 34, 62));
        card.add(loginStatus);

        JPanel loginFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginFooter.setBounds(marginX, sy(371), fieldW, sy(24));
        loginFooter.setOpaque(false);

        JLabel createAccountText = new JLabel("Create new account? ");
        createAccountText.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        createAccountText.setForeground(new Color(90, 100, 120));
        loginFooter.add(createAccountText);

        JLabel signUpLink = new JLabel("Sign up");
        signUpLink.setFont(new Font("Arial", Font.BOLD, sy(14)));
        signUpLink.setForeground(new Color(15, 84, 175));
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginFooter.add(signUpLink);
        card.add(loginFooter);

        signinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Please enter username and password.");
                    return;
                }
                if (username.length() < 3) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Username must be at least 3 characters.");
                    return;
                }
                if (password.length() < 8) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Password must be at least 8 characters.");
                    return;
                }

                // Connect to database and validate credentials
                try {
                    DBConnection.getConnection();
                    AuthService authService = new AuthService();

                    if (authService.login(username, password)) {
                        loginStatus.setForeground(new Color(34, 139, 34));
                        loginStatus.setText("Login successful! Welcome back!");
                        usernameField.setText("");
                        passwordField.setText("");
                    } else {
                        loginStatus.setForeground(new Color(170, 34, 62));
                        loginStatus.setText("Invalid username or password.");
                    }
                } catch (Exception ex) {
                    loginStatus.setForeground(new Color(170, 34, 62));
                    loginStatus.setText("Database error: " + ex.getMessage());
                    ex.printStackTrace();
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

    public static void buildSignupScreen() {
        int cardW = sx(420);
        int cardH = sy(650); // Adjusted height for compact fields
        int cardX = sx(40);
        int cardY = (screenHeight - cardH) / 3;

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(cardX, cardY, cardW, cardH);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        signupPanel.add(card);

        int marginX = sx(20);
        int fieldW = cardW - sx(40);
        signupPanel.setBackground(new Color(245, 248, 252));

        JLabel title = new JLabel("Create your account");
        title.setBounds(marginX, sy(22), fieldW, sy(44));
        title.setFont(new Font("Arial", Font.BOLD, sy(28)));
        title.setForeground(new Color(20, 33, 61));
        card.add(title);

        JLabel subtitle = new JLabel("Sign up to start managing bookings and customers.");
        subtitle.setBounds(marginX, sy(68), fieldW, sy(24));
        subtitle.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        subtitle.setForeground(new Color(100, 110, 130));
        card.add(subtitle);

        JPanel accent = new JPanel();
        accent.setBounds(marginX, sy(100), sx(60), sy(3));
        accent.setBackground(new Color(15, 84, 175));
        card.add(accent);

        // Name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setBounds(marginX, sy(116), fieldW, sy(20));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(marginX, sy(136), fieldW, sy(30));
        nameField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        nameField.setBackground(new Color(250, 251, 253));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(nameField);

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(marginX, sy(174), fieldW, sy(20));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(marginX, sy(194), fieldW, sy(30));
        emailField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        emailField.setBackground(new Color(250, 251, 253));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(emailField);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setBounds(marginX, sy(232), fieldW, sy(20));
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(marginX, sy(252), fieldW, sy(30));
        phoneField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        phoneField.setBackground(new Color(250, 251, 253));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(phoneField);

        // Address field
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setBounds(marginX, sy(290), fieldW, sy(20));
        addressLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(addressLabel);

        JTextField addressField = new JTextField();
        addressField.setBounds(marginX, sy(310), fieldW, sy(30));
        addressField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        addressField.setBackground(new Color(250, 251, 253));
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(addressField);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(marginX, sy(348), fieldW, sy(20));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(marginX, sy(368), fieldW, sy(30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        passwordField.setBackground(new Color(250, 251, 253));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(passwordField);

        // Confirm Password field
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setBounds(marginX, sy(406), fieldW, sy(20));
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        card.add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(marginX, sy(426), fieldW, sy(30));
        confirmField.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        confirmField.setBackground(new Color(250, 251, 253));
        confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        card.add(confirmField);

        JButton createButton = new JButton("Sign up");
        createButton.setBounds(marginX, sy(474), fieldW, sy(48));
        createButton.setFont(new Font("Arial", Font.BOLD, sy(15)));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(0x16, 0x2D, 0x3A));
        createButton.setBorder(BorderFactory.createEmptyBorder());
        createButton.setFocusPainted(false);
        createButton.putClientProperty("JButton.buttonType", "roundRect");
        card.add(createButton);

        signupStatus = new JLabel(" ");
        signupStatus.setBounds(marginX, sy(530), fieldW, sy(20));
        signupStatus.setFont(new Font("Arial", Font.PLAIN, sy(13)));
        signupStatus.setForeground(new Color(170, 34, 62));
        card.add(signupStatus);

        JPanel signupFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupFooter.setBounds(marginX, sy(550), fieldW, sy(24));
        signupFooter.setOpaque(false);

        JLabel alreadyText = new JLabel("Already have an account? ");
        alreadyText.setFont(new Font("Arial", Font.PLAIN, sy(14)));
        alreadyText.setForeground(new Color(90, 100, 120));
        signupFooter.add(alreadyText);

        JLabel signInLink = new JLabel("Sign in");
        signInLink.setFont(new Font("Arial", Font.BOLD, sy(14)));
        signInLink.setForeground(new Color(15, 84, 175));
        signInLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupFooter.add(signInLink);
        card.add(signupFooter);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirm = new String(confirmField.getPassword());

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()
                        || password.isEmpty() || confirm.isEmpty()) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Please fill in all fields.");
                    return;
                }
                if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Please enter a valid email address.");
                    return;
                }
                if (!phone.matches("\\d{9,11}")) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Phone must be 9-11 digits only.");
                    return;
                }
                if (password.length() < 8) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Password must be at least 8 characters.");
                    return;
                }
                if (!password.equals(confirm)) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Passwords do not match.");
                    return;
                }

                // Save new user to database
                try {
                    DBConnection.getConnection();
                    UserDAO userDAO = new UserDAO();

                    // Hash the password
                    String passwordHash = PasswordHasher.hash(password);

                    // Create new user object (using email as username for this signup form)
                    User newUser = new User();
                    newUser.setUsername(name);
                    newUser.setPasswordHash(passwordHash);
                    newUser.setRole("customer");
                    newUser.setCreatedAt(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer newCustomer = new Customer();
                    newCustomer.setName(name);
                    newCustomer.setEmail(email);
                    newCustomer.setPhone(phone);
                    newCustomer.setAddress(address);
                    customerDAO.add(newCustomer);
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setAddress(address);

                    // Add user to database
                    userDAO.add(newUser);

                    signupStatus.setForeground(new Color(34, 139, 34));
                    signupStatus.setText("Account created successfully. You can sign in now.");

                    // Clear fields
                    nameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    addressField.setText("");
                    passwordField.setText("");
                    confirmField.setText("");

                } catch (Exception ex) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    if (ex.getMessage() != null && ex.getMessage().contains("UNIQUE")) {
                        signupStatus.setText("Email already exists. Please use a different email.");
                    } else {
                        signupStatus.setText("Error creating account: " + ex.getMessage());
                    }
                    ex.printStackTrace();
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

    private static void buildIllustrationPanel(JPanel parent) {
        // Right panel starts right after the left panel
        int rightX = leftPanelWidth + 210;
        int rightW = screenWidth - rightX - 40;

        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(245, 248, 252));
        imagePanel.setLayout(null);
        imagePanel.setBounds(rightX, 0, rightW, screenHeight);

        String[] imagePaths = {
                "src/hotel/images/resources/hotel1.jpg",
                "src/hotel/images/resources/hotel2.jpg",
                "src/hotel/images/resources/hotel3.jpg"
        };

        // Three equal-width columns inside the right panel
        int colW = rightW / 3;

        int[] cardHeights = {
                (int) (screenHeight * 0.65), // card 1
                (int) (screenHeight * 0.75), // card 2
                (int) (screenHeight * 0.80) // card 3
        };
        ;
        int[] stagger = { -40, -40, -40 }; // card 1 lower, card 2 center, card 3 higher

        for (int i = 0; i < 3; i++) {
            final String path = imagePaths[i];
            final int x = i * colW + 4;
            final int w = colW - 8;
            final int h = cardHeights[i];
            final int y = (screenHeight - h) / 2 + stagger[i];

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
                        double sc = Math.max((double) w / imgW, (double) h / imgH);
                        int drawW = (int) (imgW * sc);
                        int drawH = (int) (imgH * sc);
                        g2.drawImage(img, (w - drawW) / 2, (h - drawH) / 2, drawW, drawH, this);
                    } else {
                        g2.setColor(new Color(37, 114, 198));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    }
                    g2.dispose();
                }
            };

            card.setBounds(x, y, w, h);
            card.setOpaque(false);
            imagePanel.add(card);
        }

        parent.add(imagePanel, 0);
    }

    /** Scale a horizontal (X-axis) value */
    private static int sx(int value) {
        return (int) (value * scaleX);
    }

    /** Scale a vertical (Y-axis) value */
    private static int sy(int value) {
        return (int) (value * scaleY);
    }
}