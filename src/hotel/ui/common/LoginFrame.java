package hotel.ui.common;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import hotel.service.AuthService;
import hotel.config.DBConnection;
import hotel.dao.UserDAO;
import hotel.dao.CustomerDAO;
import hotel.model.User;
import hotel.model.Customer;
import hotel.util.PasswordHasher;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginFrame {

    // ── Shared panels & status labels ──────────────────────────────
    public static JPanel loginPanel;
    public static JPanel signupPanel;
    public static JLabel loginStatus;
    public static JLabel signupStatus;

    // ── Brand colors (kept identical to original) ──────────────────
    private static final Color BG_PAGE = new Color(245, 248, 252);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_CLR = new Color(220, 225, 235);
    private static final Color ACCENT = new Color(15, 84, 175);
    private static final Color DARK_BTN = new Color(0x16, 0x2D, 0x3A);
    private static final Color TEXT_DARK = new Color(20, 33, 61);
    private static final Color TEXT_MID = new Color(100, 110, 130);
    private static final Color TEXT_LIGHT = new Color(90, 100, 120);
    private static final Color ERR_COLOR = new Color(170, 34, 62);
    private static final Color OK_COLOR = new Color(34, 139, 34);
    private static final Color FIELD_BG = new Color(250, 251, 253);
    private static final Color FIELD_BDR = new Color(210, 215, 223);

    // ──────────────────────────────────────────────────────────────
    // LOGIN SCREEN
    // ──────────────────────────────────────────────────────────────
    public static void buildLoginScreen() {
        loginPanel.setBackground(BG_PAGE);
        loginPanel.setLayout(new BorderLayout());

        // ── Left column: card ──────────────────────────────────────
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new GridBagLayout()); // centers the card vertically
        leftColumn.setPreferredSize(new Dimension(680, 0));

        JPanel card = buildCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                BorderFactory.createEmptyBorder(30, 28, 28, 28)));

        // Title
        JLabel title = styledLabel("Hotel Management System", Font.BOLD, 26, TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        // Subtitle
        JLabel subtitle = styledLabel("Sign in to your dashboard", Font.PLAIN, 14, TEXT_MID);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(10));

        // Accent bar
        card.add(accentBar());
        card.add(Box.createVerticalStrut(24));

        // Username
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        JTextField usernameField = styledTextField();
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));

        // Password
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        JPasswordField passwordField = styledPasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));

        // Sign-in button
        JButton signinButton = primaryButton("Sign in");
        card.add(signinButton);
        card.add(Box.createVerticalStrut(10));

        // Status label
        loginStatus = statusLabel();
        card.add(loginStatus);
        card.add(Box.createVerticalStrut(6));

        // Footer link
        card.add(buildLinkRow(
                "Create new account? ", "Sign up",
                () -> {
                    loginPanel.setVisible(false);
                    signupPanel.setVisible(true);
                }));

        leftColumn.add(card);
        loginPanel.add(leftColumn, BorderLayout.WEST);

        // ── Right column: illustration ─────────────────────────────
        loginPanel.add(buildIllustrationPanel(), BorderLayout.CENTER);

        // ── Wire up sign-in action ─────────────────────────────────
        signinButton.addActionListener(e -> handleLogin(usernameField, passwordField, loginStatus));
        // Allow Enter key in password field
        passwordField.addActionListener(e -> signinButton.doClick());
    }

    // ──────────────────────────────────────────────────────────────
    // SIGN-UP SCREEN
    // ──────────────────────────────────────────────────────────────
    public static void buildSignupScreen() {
        signupPanel.setBackground(BG_PAGE);
        signupPanel.setLayout(new BorderLayout());

        // ── Left column: card ──────────────────────────────────────
        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setOpaque(false);
        leftColumn.setPreferredSize(new Dimension(680, 0));

        JPanel card = buildCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)));

        // Title
        JLabel title = styledLabel("Create your account", Font.BOLD, 26, TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel subtitle = styledLabel("Sign up to start managing bookings and customers.", Font.PLAIN, 13, TEXT_MID);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(10));

        card.add(accentBar());
        card.add(Box.createVerticalStrut(18));

        // Fields
        card.add(fieldLabel("Full Name"));
        card.add(Box.createVerticalStrut(4));
        JTextField nameField = styledTextField();
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("Email"));
        card.add(Box.createVerticalStrut(4));
        JTextField emailField = styledTextField();
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("Phone"));
        card.add(Box.createVerticalStrut(4));
        JTextField phoneField = styledTextField();
        card.add(phoneField);
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("Address"));
        card.add(Box.createVerticalStrut(4));
        JTextField addressField = styledTextField();
        card.add(addressField);
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        JPasswordField passwordField = styledPasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        card.add(fieldLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        JPasswordField confirmField = styledPasswordField();
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));

        // Sign-up button
        JButton createButton = primaryButton("Sign up");
        card.add(createButton);
        card.add(Box.createVerticalStrut(8));

        // Status label
        signupStatus = statusLabel();
        card.add(signupStatus);
        card.add(Box.createVerticalStrut(6));

        // Footer link
        card.add(buildLinkRow(
                "Already have an account? ", "Sign in",
                () -> {
                    signupPanel.setVisible(false);
                    loginPanel.setVisible(true);
                }));

        leftColumn.add(card);
        signupPanel.add(leftColumn, BorderLayout.WEST);
        signupPanel.add(buildIllustrationPanel(), BorderLayout.CENTER);

        // ── Wire up sign-up action ─────────────────────────────────
        createButton.addActionListener(e -> handleSignup(
                nameField, emailField, phoneField, addressField,
                passwordField, confirmField, signupStatus));
    }

    // ──────────────────────────────────────────────────────────────
    // ACTION HANDLERS (logic unchanged from original)
    // ──────────────────────────────────────────────────────────────
    private static void handleLogin(JTextField usernameField,
            JPasswordField passwordField,
            JLabel status) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            err(status, "Please enter username and password.");
            return;
        }
        if (username.length() < 3) {
            err(status, "Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 8) {
            err(status, "Password must be at least 8 characters.");
            return;
        }
        try {
            DBConnection.getConnection();
            AuthService authService = new AuthService();
            if (authService.login(username, password)) {
                ok(status, "Login successful! Welcome back!");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                err(status, "Invalid username or password.");
            }
        } catch (Exception ex) {
            err(status, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void handleSignup(JTextField nameField, JTextField emailField,
            JTextField phoneField, JTextField addressField,
            JPasswordField passwordField, JPasswordField confirmField,
            JLabel status) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            err(status, "Please fill in all fields.");
            return;
        }
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            err(status, "Please enter a valid email address.");
            return;
        }
        if (!phone.matches("\\d{9,11}")) {
            err(status, "Phone must be 9-11 digits only.");
            return;
        }
        if (password.length() < 8) {
            err(status, "Password must be at least 8 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            err(status, "Passwords do not match.");
            return;
        }
        try {
            DBConnection.getConnection();
            String passwordHash = PasswordHasher.hash(password);

            User newUser = new User();
            newUser.setUsername(name);
            newUser.setPasswordHash(passwordHash);
            newUser.setRole("customer");
            newUser.setCreatedAt(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setAddress(address);

            CustomerDAO customerDAO = new CustomerDAO();
            Customer newCustomer = new Customer();
            newCustomer.setName(name);
            newCustomer.setEmail(email);
            newCustomer.setPhone(phone);
            newCustomer.setAddress(address);
            customerDAO.add(newCustomer);

            new UserDAO().add(newUser);

            ok(status, "Account created successfully. You can sign in now.");
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            addressField.setText("");
            passwordField.setText("");
            confirmField.setText("");

        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("UNIQUE"))
                err(status, "Email already exists. Please use a different email.");
            else
                err(status, "Error creating account: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // ILLUSTRATION PANEL (right side, unchanged visually)
    // ──────────────────────────────────────────────────────────────
    private static JPanel buildIllustrationPanel() {
        String[] imagePaths = {
                "src/hotel/images/resources/hotel1.jpg",
                "src/hotel/images/resources/hotel2.jpg",
                "src/hotel/images/resources/hotel3.jpg"
        };
        double[] heightRatios = { 0.65, 0.75, 0.80 };
        int[] stagger = { -40, -40, -40 };

        // Use GridLayout so each image column is equal width and stretches with the
        // window
        JPanel imagePanel = new JPanel(new GridLayout(1, 3, 8, 0)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(520, super.getPreferredSize().height);
            }
        };
        imagePanel.setBackground(BG_PAGE);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 20));

        for (int i = 0; i < 3; i++) {
            final String path = imagePaths[i];
            final double hRatio = heightRatios[i];
            final int stag = stagger[i];

            Image[] holder = { null };
            try {
                holder[0] = new ImageIcon(path).getImage();
            } catch (Exception ignored) {
            }
            final Image img = holder[0];

            // Wrap in a panel that centres a fixed-height image card
            JPanel slot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int w = getWidth();
                    int h = getHeight();
                    int cardH = (int) (h * hRatio);
                    int cardY = (h - cardH) / 2 + stag;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Shape clip = new java.awt.geom.RoundRectangle2D.Float(0, cardY, w, cardH, 20, 20);
                    g2.setClip(clip);
                    if (img != null) {
                        int imgW = img.getWidth(this);
                        int imgH = img.getHeight(this);
                        double sc = Math.max((double) w / imgW, (double) cardH / imgH);
                        int drawW = (int) (imgW * sc);
                        int drawH = (int) (imgH * sc);
                        g2.drawImage(img, (w - drawW) / 2, cardY + (cardH - drawH) / 2, drawW, drawH, this);
                    } else {
                        g2.setColor(new Color(37, 114, 198));
                        g2.fill(clip);
                    }
                    g2.dispose();
                }
            };
            slot.setOpaque(false);
            imagePanel.add(slot);
        }
        return imagePanel;
    }

    // ──────────────────────────────────────────────────────────────
    // COMPONENT HELPERS
    // ──────────────────────────────────────────────────────────────

    /** White rounded card panel */
    private static JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        return card;
    }

    /** Blue accent bar under the subtitle */
    private static JPanel accentBar() {
        JPanel bar = new JPanel();
        bar.setBackground(ACCENT);
        bar.setMaximumSize(new Dimension(60, 3));
        bar.setPreferredSize(new Dimension(60, 3));
        bar.setMinimumSize(new Dimension(60, 3));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }

    /** Small bold field label */
    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** Styled text field (full width via AlignmentX + max size) */
    private static JTextField styledTextField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    /** Styled password field */
    private static JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    private static void styleInput(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BDR),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /** Dark full-width primary action button */
    private static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(DARK_BTN);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.putClientProperty("JButton.buttonType", "roundRect");

        // Hover effect
        Color normal = DARK_BTN;
        Color hover = new Color(0x22, 0x44, 0x55);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        });
        return btn;
    }

    /** Status label (error / success) */
    private static JLabel statusLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(ERR_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JPanel buildLinkRow(String plainText, String linkText, Runnable onClick) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel plain = new JLabel(plainText);
        plain.setFont(new Font("Arial", Font.PLAIN, 14));
        plain.setForeground(TEXT_LIGHT);

        JLabel link = new JLabel(linkText);
        link.setFont(new Font("Arial", Font.BOLD, 14));
        link.setForeground(ACCENT);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });

        row.add(plain);
        row.add(link);
        return row;
    }

    private static JLabel styledLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(color);
        return label;
    }

    // ── Status helpers ─────────────────────────────────────────────
    private static void err(JLabel label, String msg) {
        label.setForeground(ERR_COLOR);
        label.setText(msg);
    }

    private static void ok(JLabel label, String msg) {
        label.setForeground(OK_COLOR);
        label.setText(msg);
    }
}