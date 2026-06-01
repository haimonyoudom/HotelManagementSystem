package hotel.ui.common;

/*
 MainFrame.java
 ---------------
 Main application entry point that displays the login screen.
 Initializes the application with light-mode styling.
*/

import java.awt.*;
import javax.swing.*;

public class MainFrame {

    // ── Light Mode Palette ─────────────────────────────────────────────────
    static final Color BG_MAIN       = new Color(250, 250, 250);
    static final Color BG_CARD       = new Color(255, 255, 255);
    static final Color TXT_PRIMARY   = new Color(20, 20, 20);
    static final Color BORDER        = new Color(220, 220, 220);

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // Start with login screen
            LoginFrame.main(args);
        });
    }
}
