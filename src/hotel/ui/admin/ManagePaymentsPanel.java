package hotel.ui.admin;

import hotel.dao.PaymentDAO;
import hotel.model.Payment;
import hotel.ui.common.UITheme;
import hotel.util.ReportExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ManagePaymentsPanel extends JPanel {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Booking ID", "Amount", "Payment Date", "Method", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ManagePaymentsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = AdminUITheme.primaryButton("Add Payment");
        JButton editBtn = AdminUITheme.secondaryButton("Edit Payment");
        JButton deleteBtn = AdminUITheme.dangerButton("Delete Payment");
        JButton exportBtn = AdminUITheme.secondaryButton("Export CSV");
        JButton refreshBtn = AdminUITheme.secondaryButton("Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(exportBtn);
        actions.add(refreshBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(actions, BorderLayout.NORTH);
        add(AdminUITheme.scroll(table), BorderLayout.CENTER);

        addBtn.addActionListener(ignored -> openDialog(null));
        editBtn.addActionListener(ignored -> editSelected());
        deleteBtn.addActionListener(ignored -> deleteSelected());
        exportBtn.addActionListener(ignored -> exportCsv());
        refreshBtn.addActionListener(ignored -> reload());

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);

            List<Payment> payments = paymentDAO.getAll();

            for (Payment payment : payments) {
                tableModel.addRow(new Object[]{
                        payment.getId(),
                        payment.getBookingId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getMethod(),
                        payment.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Payment payment = paymentDAO.getById(id);

            if (payment == null) {
                JOptionPane.showMessageDialog(this, "Payment not found.");
                reload();
                return;
            }

            openDialog(payment);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete payment #" + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            paymentDAO.delete(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete payment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Payment editing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                editing == null ? "Add Payment" : "Edit Payment", true);

        JPanel root = UITheme.pagePanel();
        JPanel form = UITheme.cardPanel(new GridBagLayout());

        JTextField bookingIdField = UITheme.textField();
        JTextField amountField = UITheme.textField();
        JTextField paymentDateField = UITheme.textField();
        JComboBox<String> methodBox = UITheme.comboBox("cash", "card", "qr", "bank_transfer");
        JComboBox<String> statusBox = UITheme.comboBox("pending", "paid", "failed", "refunded");

        if (editing != null) {
            bookingIdField.setText(String.valueOf(editing.getBookingId()));
            amountField.setText(String.valueOf(editing.getAmount()));
            paymentDateField.setText(editing.getPaymentDate());
            methodBox.setSelectedItem(editing.getMethod());
            statusBox.setSelectedItem(editing.getStatus());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Booking ID", bookingIdField);
        addRow(form, gbc, y++, "Amount", amountField);
        addRow(form, gbc, y++, "Payment Date", paymentDateField);
        addRow(form, gbc, y++, "Method", methodBox);
        addRow(form, gbc, y++, "Status", statusBox);

        JButton saveBtn = UITheme.primaryButton("Save");
        JButton cancelBtn = UITheme.secondaryButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        dialog.add(root);

        saveBtn.addActionListener(ignored -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                double amount = Double.parseDouble(amountField.getText().trim());

                Payment payment = editing == null ? new Payment() : editing;
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setPaymentDate(paymentDateField.getText().trim());
                payment.setMethod(String.valueOf(methodBox.getSelectedItem()));
                payment.setStatus(String.valueOf(statusBox.getSelectedItem()));

                if (editing == null) {
                    paymentDAO.add(payment);
                } else {
                    paymentDAO.update(payment);
                }

                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Booking ID and Amount must be valid numbers.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save payment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ignored -> dialog.dispose());

        dialog.setSize(500, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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

    private void exportCsv() {
        try {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No payment records available to export.",
                        "Export CSV", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Payments CSV");
            chooser.setSelectedFile(new File("payments.csv"));
            int choice = chooser.showSaveDialog(this);
            if (choice != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();
            java.util.List<String[]> rows = new ArrayList<>();
            rows.add(new String[] {"ID", "Booking ID", "Amount", "Payment Date", "Method", "Status"});
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                rows.add(new String[] {
                        String.valueOf(tableModel.getValueAt(i, 0)),
                        String.valueOf(tableModel.getValueAt(i, 1)),
                        String.valueOf(tableModel.getValueAt(i, 2)),
                        String.valueOf(tableModel.getValueAt(i, 3)),
                        String.valueOf(tableModel.getValueAt(i, 4)),
                        String.valueOf(tableModel.getValueAt(i, 5))
                });
            }

            ReportExporter.exportToCsv(rows, file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Payments exported to " + file.getAbsolutePath(),
                    "Export CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(),
                    "Export CSV", JOptionPane.ERROR_MESSAGE);
        }
    }
}