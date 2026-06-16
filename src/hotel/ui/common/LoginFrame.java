package hotel.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;

    public LoginFrame() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(400, 300));

        // ── Build screens ──────────────────────────────────────────
        loginPanel = new LoginPanel(() -> cardLayout.show(cardContainer, "signup"));
        signupPanel = new SignupPanel(() -> cardLayout.show(cardContainer, "login"));

        cardContainer.add(loginPanel, "login");
        cardContainer.add(signupPanel, "signup");
        cardLayout.show(cardContainer, "login");

        setContentPane(cardContainer);
        setLocationRelativeTo(null);
        setVisible(true);

        // ── Frame-level resize listener — updates BOTH panels ──────
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int W = getContentPane().getWidth();
                int H = getContentPane().getHeight();
            }
        });

        // ── Fire once after frame is fully shown ───────────────────
        SwingUtilities.invokeLater(() -> {
            int W = getContentPane().getWidth();
            int H = getContentPane().getHeight();
        });
    }
}