package hotel.ui.common;

import hotel.util.*;
import hotel.ui.staff.util.AuthLogic;
import hotel.ui.staff.util.LoginUIUtils;
import hotel.ui.staff.util.UIConstants;
import hotel.ui.staff.util.UIHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(Runnable onSignUpClick) {
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
                new EmptyBorder(30, 28, 28, 28)));

        // ── Components ─────────────────────────────────────────────
        JLabel title = UIHelper.styledLabel("Hotel Management System", Font.BOLD, UIConstants.TEXT_DARK);
        JLabel subtitle = UIHelper.styledLabel("Sign in to your dashboard", Font.PLAIN, UIConstants.TEXT_MID);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = UIHelper.styledTextField();
        JPasswordField passField = UIHelper.styledPasswordField();
        JButton signInBtn = LoginUIUtils.primaryButton("Sign in");
        JLabel statusLbl = UIHelper.statusLabel();

        // ── Assemble card ──────────────────────────────────────────
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(8));
        card.add(UIHelper.accentBar());
        card.add(Box.createVerticalStrut(18));
        card.add(UIHelper.fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(userField);
        card.add(Box.createVerticalStrut(10));
        card.add(UIHelper.fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(16));
        card.add(signInBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(LoginUIUtils.linkRow("Create new account? ", "Sign up", onSignUpClick));

        leftCol.add(card);
        add(leftCol);
        add(LoginUIUtils.illustrationPanel());

        // ── Actions ────────────────────────────────────────────────
        signInBtn.addActionListener(e -> AuthLogic.handleLogin(userField.getText().trim(),
                new String(passField.getPassword()),
                userField, passField, statusLbl));
        passField.addActionListener(e -> signInBtn.doClick());
    }
}