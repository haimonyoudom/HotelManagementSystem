package hotel.ui.admin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import hotel.ui.util.UIConstants;

public class test {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        UIManager.put("Panel.background", UIConstants.BG_DARK);
        UIManager.put("OptionPane.background", UIConstants.BG_DARK);
        UIManager.put("OptionPane.messageForeground", UIConstants.TEXT_PRIMARY);
        
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}
