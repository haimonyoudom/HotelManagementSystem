package hotel.test;

import javax.swing.SwingUtilities;
import hotel.ui.common.LoginFrame;

public class staffmain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}