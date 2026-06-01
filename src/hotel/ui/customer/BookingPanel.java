package hotel.ui.customer;

import hotel.model.Customer;
import hotel.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class BookingPanel extends JPanel {
    public BookingPanel(Customer customer) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = UITheme.cardPanel(new BorderLayout(12, 12));

        JLabel title = UITheme.heading("Book from the Rooms page");
        JLabel message = new JLabel(
                "<html>"
                        + "Please open the <b>Rooms</b> page, choose an available room, "
                        + "and click <b>Book Now</b>.<br><br>"
                        + "The system will automatically use your customer profile and the selected room."
                        + "</html>"
        );

        card.add(title, BorderLayout.NORTH);
        card.add(message, BorderLayout.CENTER);

        add(card, BorderLayout.NORTH);
    }
}