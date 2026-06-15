package hotel.ui.common;

import hotel.dao.CustomerDAO;
import hotel.dao.UserDAO;
import hotel.model.Customer;
import hotel.model.User;
import hotel.util.DateUtil;
import hotel.util.PasswordHasher;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RegisterDialog extends JDialog {
    private final JTextField fullNameField = UITheme.textField();
    private final JTextField emailField = UITheme.textField();
    private final JTextField phoneField = UITheme.textField();
    private final JTextField addressField = UITheme.textField();
    private final JPasswordField passwordField = UITheme.passwordField();
    private final JPasswordField confirmPasswordField = UITheme.passwordField();

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public RegisterDialog(JFrame owner) {
        super(owner, "Register Customer", true);

        setMinimumSize(new Dimension(420, 480));
        setPreferredSize(new Dimension(540, 600));

        buildUI();

        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        JPanel content = UITheme.pagePanel();
        JPanel card = UITheme.cardPanel(new BorderLayout(0, 18));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = UITheme.title("Create Customer Account");
        JLabel subtitle = UITheme.muted("Use your email as your login username.");

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        int y = 0;
        addField(form, gbc, y++, "Full Name", fullNameField);
        addField(form, gbc, y++, "Email / Login Username", emailField);
        addField(form, gbc, y++, "Phone", phoneField);
        addField(form, gbc, y++, "Address", addressField);
        addField(form, gbc, y++, "Password", passwordField);
        addField(form, gbc, y, "Confirm Password", confirmPasswordField);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setOpaque(false);

        JButton registerBtn = UITheme.primaryButton("Register");
        JButton cancelBtn = UITheme.secondaryButton("Cancel");

        buttons.add(cancelBtn);
        buttons.add(registerBtn);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(UITheme.BG);

        root.add(scrollPane, BorderLayout.CENTER);
        setContentPane(root);

        getRootPane().setDefaultButton(registerBtn);

        registerBtn.addActionListener(ignored -> registerCustomer());
        cancelBtn.addActionListener(ignored -> dispose());
    }

    private void addField(JPanel form, GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridy = y * 2;
        form.add(new JLabel(label), gbc);

        gbc.gridy = y * 2 + 1;
        form.add(field, gbc);
    }

    private void registerCustomer() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        char[] passwordChars = passwordField.getPassword();
        char[] confirmChars = confirmPasswordField.getPassword();

        String password = new String(passwordChars);
        String confirmPassword = new String(confirmChars);

        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank()
                || password.isBlank() || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.getByUsername(email) != null) {
                JOptionPane.showMessageDialog(this, "This email is already used for login.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (customerDAO.getByEmail(email) != null) {
                JOptionPane.showMessageDialog(this, "Customer email already exists.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = new User();
            user.setUsername(email);
            user.setPasswordHash(PasswordHasher.hash(password));
            user.setRole("CUSTOMER");
            user.setCreatedAt(DateUtil.today());
            userDAO.add(user);

            Customer customer = new Customer();
            customer.setName(fullName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            customerDAO.add(customer);

            JOptionPane.showMessageDialog(this,
                    "Registration successful.\nYou can now login using your email.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(passwordChars, '\0');
            Arrays.fill(confirmChars, '\0');
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }
}