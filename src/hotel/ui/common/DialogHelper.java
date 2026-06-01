package hotel.ui.common;

import java.awt.*;
import javax.swing.*;

/*
 DialogHelper.java
 ------------------
 Utility class for displaying dialog boxes with consistent light-mode styling.
 Provides methods for alerts, confirmations, and input dialogs.
*/

public class DialogHelper {

    // ── Light Mode Palette ─────────────────────────────────────────────────
    static final Color BG_DIALOG     = new Color(255, 255, 255);
    static final Color TXT_PRIMARY   = new Color(20, 20, 20);
    static final Color TXT_SECONDARY = new Color(90, 90, 90);
    static final Color BUTTON_BG     = new Color(59, 130, 246);
    static final Color BUTTON_FG     = new Color(255, 255, 255);
    static final Color BORDER        = new Color(220, 220, 220);

    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  14);
    static final Font F_REG   = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  12);

    /**
     * Show an informational message dialog with light-mode styling
     */
    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show an error message dialog with light-mode styling
     */
    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a warning message dialog with light-mode styling
     */
    public static void showWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show a confirmation dialog and return true if user clicks Yes
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Show an input dialog and return the user's input
     */
    public static String showInput(Component parent, String title, String message) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Show a custom dialog with buttons
     */
    public static int showCustom(Component parent, String title, String message, String[] options) {
        return JOptionPane.showOptionDialog(parent, message, title, JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }
}
