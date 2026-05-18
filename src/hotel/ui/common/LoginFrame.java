package hotel.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame {

    static JPanel loginPanel;
    static JPanel signupPanel;
    static JLabel loginStatus;
    static JLabel signupStatus;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hotel Management System");
        frame.setSize(980, 760);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new JPanel();
        signupPanel = new JPanel();

        loginPanel.setLayout(null);
        loginPanel.setBounds(0, 0, 980, 760);
        loginPanel.setBackground(Color.WHITE);

        signupPanel.setLayout(null);
        signupPanel.setBounds(0, 0, 980, 760);
        signupPanel.setBackground(Color.WHITE);

        // Login right background
        JPanel loginRightBg = new JPanel();
        loginRightBg.setBounds(450, 0, 530, 760);
        loginRightBg.setBackground(Color.WHITE);
        loginPanel.add(loginRightBg);

        // Signup right background
        JPanel signupRightBg = new JPanel();
        signupRightBg.setBounds(450, 0, 530, 760);
        signupRightBg.setBackground(Color.WHITE);
        signupPanel.add(signupRightBg);

        frame.add(loginPanel);
        frame.add(signupPanel);

        buildLoginScreen();
        buildSignupScreen();

        loginPanel.setVisible(true);
        signupPanel.setVisible(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void buildLoginScreen() {
        JLabel title = new JLabel("Hotel Management System");
        title.setBounds(46, 30, 500, 48);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(20, 33, 61));
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

        JLabel emailLabel = new JLabel("Email");
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
        // Add this:
        signinButton.putClientProperty("JButton.buttonType", "roundRect");
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
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                if (email.isEmpty() || password.isEmpty()) {
                    loginStatus.setText("Please enter email and password.");
                    return;
                }
                loginStatus.setForeground(new Color(15, 84, 175));
                loginStatus.setText("Signed in successfully (demo mode). Welcome back!");
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

        JLabel emailLabel = new JLabel("Email");
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

        JComboBox<String> roleDropdown = new JComboBox<>(new String[] { "  ....", "  Customer", "  Staff" });
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
        createButton.putClientProperty("JButton.buttonType", "roundRect");
        signupPanel.add(createButton);

        signupStatus = new JLabel(" ");
        signupStatus.setBounds(60, 530, 360, 24);
        signupStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        signupStatus.setForeground(new Color(170, 34, 62));
        signupPanel.add(signupStatus);

        JPanel signupFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupFooter.setBounds(60, 548, 360, 24);
        signupFooter.setOpaque(false);
        signupStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        signupStatus.setForeground(new Color(170, 34, 62));
        signupPanel.add(signupStatus);

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
                String role = (String) roleDropdown.getSelectedItem();

                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || role.equals("...")) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Please fill in all fields.");
                    return;
                }
                if (!password.equals(confirm)) {
                    signupStatus.setForeground(new Color(170, 34, 62));
                    signupStatus.setText("Passwords do not match.");
                    return;
                }
                signupStatus.setForeground(new Color(15, 84, 175));
                signupStatus.setText("Account created successfully. You can sign in now.");
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
        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(610, 80, 320, 520);
        imagePanel.setBackground(new Color(245, 248, 252));
        imagePanel.setLayout(null);

        String[] imagePaths = {
                "src/hotel/images/resources/hotel1.jpg",
                "src/hotel/images/resources/hotel2.jpg",
                "src/hotel/images/resources/hotel3.jpg"
        };

        imagePanel.setBounds(460, 0, 520, 760);

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
                        // Get original image dimensions
                        int imgW = img.getWidth(this);
                        int imgH = img.getHeight(this);

                        // Scale to fill the box (cover, not stretch)
                        double scaleX = (double) w / imgW;
                        double scaleY = (double) h / imgH;
                        double scale = Math.max(scaleX, scaleY);

                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);

                        // Center the image
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
