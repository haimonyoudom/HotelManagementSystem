package hotel.main;

import hotel.config.DBConnection;
import hotel.config.DBInitializer;
import hotel.dao.UserDAO;
import hotel.model.User;
import hotel.ui.common.LoginFrame;
import hotel.util.DateUtil;
import hotel.util.PasswordHasher;

import javax.swing.*;

public class GUIMain {
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        DBInitializer.initialize();
        createDefaultAdmin();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeConnection));
    }

    private static void createDefaultAdmin() {
        try {
            UserDAO userDAO = new UserDAO();

            if (userDAO.getByUsername(DEFAULT_ADMIN_USERNAME) == null) {
                User admin = new User();
                admin.setUsername(DEFAULT_ADMIN_USERNAME);
                admin.setPasswordHash(PasswordHasher.hash(DEFAULT_ADMIN_PASSWORD));
                admin.setRole("ADMIN");
                admin.setCreatedAt(DateUtil.today());

                userDAO.add(admin);

                System.out.println("Default admin created -> "
                        + DEFAULT_ADMIN_USERNAME + " / " + DEFAULT_ADMIN_PASSWORD);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create default admin account", ex);
        }
    }
}