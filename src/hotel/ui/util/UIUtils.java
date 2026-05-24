package hotel.ui.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIUtils {
    
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY);
        btn.setBackground(UIConstants.ACCENT_RED);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.ACCENT_RED_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.ACCENT_RED);
            }
        });
        return btn;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY);
        btn.setBackground(UIConstants.BG_CARD);
        btn.setForeground(UIConstants.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.BG_INPUT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.BG_CARD);
            }
        });
        return btn;
    }
    
    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY);
        btn.setBackground(UIConstants.ACCENT_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.ACCENT_GREEN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.ACCENT_GREEN);
            }
        });
        return btn;
    }
    
    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_BODY);
        field.setBackground(UIConstants.BG_INPUT);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setCaretColor(UIConstants.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        return field;
    }
    
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UIConstants.FONT_BODY);
        combo.setBackground(UIConstants.BG_INPUT);
        combo.setForeground(UIConstants.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        combo.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        return combo;
    }
    
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}