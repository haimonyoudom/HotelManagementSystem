package hotel.ui.customer;

import hotel.dao.PaymentDAO;
import hotel.model.Payment;
import hotel.ui.common.UITheme;
import hotel.util.DateUtil;

import javax.swing.*;
import java.awt.*;

public class PaymentQRPanel extends JPanel {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private final JTextField bookingIdField = UITheme.textField();
    private final JTextField amountField = UITheme.textField();
    private final JComboBox<String> methodBox = UITheme.comboBox("qr", "cash", "card", "bank_transfer");

    public PaymentQRPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel form = UITheme.cardPanel(new GridBagLayout());
        JPanel qrCard = UITheme.cardPanel(new BorderLayout());

        JLabel qr = new JLabel("<html><div style='text-align:center;font-size:24px;'>▦ ▦ ▦<br/>▦ QR ▦<br/>▦ ▦ ▦</div></html>",
                SwingConstants.CENTER);
        qr.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel note = UITheme.muted("Demo QR payment area. Add booking ID and amount, then confirm payment.");

        qrCard.add(qr, BorderLayout.CENTER);
        qrCard.add(note, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Booking ID", bookingIdField);
        addRow(form, gbc, y++, "Amount", amountField);
        addRow(form, gbc, y++, "Method", methodBox);

        JButton payBtn = UITheme.primaryButton("Confirm Payment");

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(payBtn, gbc);

        add(form, BorderLayout.NORTH);
        add(qrCard, BorderLayout.CENTER);

        payBtn.addActionListener(ignored -> createPayment());
    }

    private void createPayment() {
        try {
            int bookingId = Integer.parseInt(bookingIdField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim());

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setPaymentDate(DateUtil.today());
            payment.setMethod(String.valueOf(methodBox.getSelectedItem()));
            payment.setStatus("paid");

            paymentDAO.add(payment);

            JOptionPane.showMessageDialog(this,
                    "Payment saved successfully.\nPayment ID: " + payment.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            bookingIdField.setText("");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Booking ID and amount must be valid numbers.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save payment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
    }
}