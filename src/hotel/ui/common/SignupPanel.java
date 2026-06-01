package hotel.ui.common;

import hotel.ui.staff.util.AuthLogic;
import hotel.ui.staff.util.LoginUIUtils;
import hotel.ui.staff.util.UIConstants;
import hotel.ui.staff.util.UIHelper;
import hotel.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    public SignupPanel(Runnable onSignInClick) {
        setLayout(new GridLayout(1, 2, 8, 0));
        setBackground(UIConstants.BG_PAGE);

        // ── Left column — card container with padding ──────────────────
        JPanel leftCol = new JPanel(new GridBagLayout());
        leftCol.setOpaque(false);
        leftCol.setBorder(new EmptyBorder(0, 80, 0, 0)); // ← pushes card right

        // ── Card ───────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.BG_CARD_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT),
                new EmptyBorder(24, 28, 24, 28)));

        // ── Components ─────────────────────────────────────────────
        JLabel title = UIHelper.styledLabel("Create your account", Font.BOLD, UIConstants.TEXT_DARK);
        JLabel subtitle = UIHelper.styledLabel("Sign up to start managing bookings and customers.", Font.PLAIN,
                UIConstants.TEXT_MID);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = UIHelper.styledTextField();
        JTextField emailField = UIHelper.styledTextField();
        JTextField phoneField = UIHelper.styledTextField();
        JTextField addressField = UIHelper.styledTextField();
        JPasswordField passwordField = UIHelper.styledPasswordField();
        JPasswordField confirmField = UIHelper.styledPasswordField();
        JTextField[] fields = { nameField, emailField, phoneField,
                addressField, passwordField, confirmField };

        JButton signUpBtn = LoginUIUtils.primaryButton("Sign up");
        JLabel statusLbl = UIHelper.statusLabel();

        // ── Assemble card ──────────────────────────────────────────
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(8));
        card.add(UIHelper.accentBar());
        card.add(Box.createVerticalStrut(14));
        card.add(UIHelper.fieldLabel("Full Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Email"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Phone"));
        card.add(Box.createVerticalStrut(4));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Address"));
        card.add(Box.createVerticalStrut(4));
        card.add(addressField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));
        card.add(signUpBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(LoginUIUtils.linkRow("Already have an account? ", "Sign in", onSignInClick));

        leftCol.add(card);
        add(leftCol);
        add(LoginUIUtils.illustrationPanel());

        // ── Actions ────────────────────────────────────────────────
        signUpBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            AuthLogic.handleSignup(name, email, phone, address, password, confirm, fields, statusLbl);
        });
    }
}