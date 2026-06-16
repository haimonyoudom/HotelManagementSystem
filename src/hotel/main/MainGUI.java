package hotel.main;

import javax.swing.SwingUtilities;
import hotel.ui.common.LoginFrame;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}