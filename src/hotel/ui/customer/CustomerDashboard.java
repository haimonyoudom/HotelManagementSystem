package hotel.ui.customer;

import hotel.dao.CustomerDAO;
import hotel.model.Customer;
import hotel.model.User;
import hotel.ui.common.HeaderPanel;
import hotel.ui.common.LoginFrame;
import hotel.ui.common.SidebarPanel;
import hotel.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final User currentUser;
    private Customer currentCustomer;

    private BrowseRoomsPanel browseRoomsPanel;
    private BookingHistoryPanel bookingHistoryPanel;
    private PaymentQRPanel paymentQRPanel;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        this.currentCustomer = findCustomer(user);

        UITheme.applyGlobalFont();

        setTitle("Customer Dashboard - " + user.getUsername());
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (currentCustomer == null) {
            currentCustomer = askCreateCustomerProfile();
        }

        buildUI();
    }

    private Customer findCustomer(User user) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();

            Customer byEmail = customerDAO.getByEmail(user.getUsername());
            if (byEmail != null) {
                return byEmail;
            }

            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load customer profile: " + ex.getMessage(),
                    "Customer Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Customer askCreateCustomerProfile() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "No customer profile is linked to this login.\n"
                        + "Do you want to create your customer profile now?",
                "Customer Profile Required",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return null;
        }

        JTextField nameField = UITheme.textField();
        JTextField emailField = UITheme.textField();
        JTextField phoneField = UITheme.textField();
        JTextField addressField = UITheme.textField();

        if (currentUser.getUsername() != null && currentUser.getUsername().contains("@")) {
            emailField.setText(currentUser.getUsername());
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Full Name"));
        form.add(nameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Phone"));
        form.add(phoneField);
        form.add(new JLabel("Address"));
        form.add(addressField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Create Customer Profile",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "All customer profile fields are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return askCreateCustomerProfile();
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return askCreateCustomerProfile();
        }

        try {
            CustomerDAO customerDAO = new CustomerDAO();

            Customer existing = customerDAO.getByEmail(email);
            if (existing != null) {
                return existing;
            }

            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);

            customerDAO.add(customer);

            JOptionPane.showMessageDialog(this,
                    "Customer profile created successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            return customer;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to create customer profile: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        SidebarPanel sidebar = new SidebarPanel("HMS");

        browseRoomsPanel = new BrowseRoomsPanel(currentCustomer);
        bookingHistoryPanel = new BookingHistoryPanel(currentCustomer);
        paymentQRPanel = new PaymentQRPanel();

        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(wrapPage("Rooms", browseRoomsPanel), "ROOMS");
        contentPanel.add(wrapPage("Booking History", bookingHistoryPanel), "HISTORY");
        contentPanel.add(wrapPage("Payment", paymentQRPanel), "PAYMENT");

        sidebar.addSection("Customer");
        sidebar.addNavigationButton("ROOMS", "Rooms", () -> {
            browseRoomsPanel.reload();
            showPage("ROOMS");
        });
        sidebar.addNavigationButton("HISTORY", "Booking History", () -> {
            bookingHistoryPanel.reload();
            showPage("HISTORY");
        });
        sidebar.addNavigationButton("PAYMENT", "Payment", () -> showPage("PAYMENT"));

        sidebar.addBottomGlue();
        sidebar.addNavigationButton("LOGOUT", "Logout", this::logout);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPage("ROOMS");
    }

    private JPanel wrapPage(String title, JPanel page) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.add(new HeaderPanel(title, currentUser), BorderLayout.NORTH);
        wrapper.add(page, BorderLayout.CENTER);
        return wrapper;
    }

    private void showPage(String key) {
        cardLayout.show(contentPanel, key);
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}