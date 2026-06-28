package hotel.ui.admin;

import hotel.dao.PaymentDAO;
import hotel.model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IncomeReportPanel extends JPanel {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private final JLabel totalRevenueLabel = new JLabel("$0.00");
    private final JLabel paidCountLabel    = new JLabel("0");
    private final JLabel pendingCountLabel = new JLabel("0");
    private final JLabel failedCountLabel  = new JLabel("0");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Booking ID", "Amount", "Date", "Method", "Status"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    public IncomeReportPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new GridLayout(1, 4, 16, 0));
        top.setOpaque(false);

        top.add(reportCard("Total Revenue",  totalRevenueLabel, AdminUITheme.SUCCESS));
        top.add(reportCard("Paid Payments",  paidCountLabel,    AdminUITheme.PRIMARY));
        // top.add(reportCard("Pending",        pendingCountLabel, AdminUITheme.WARNING));
        // top.add(reportCard("Failed",         failedCountLabel,  AdminUITheme.DANGER));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton refreshBtn = AdminUITheme.secondaryButton("  Refresh");
        actions.add(refreshBtn);

        JPanel north = new JPanel(new BorderLayout(0, 16));
        north.setOpaque(false);
        north.add(top,     BorderLayout.CENTER);
        north.add(actions, BorderLayout.SOUTH);

        add(north,                      BorderLayout.NORTH);
        add(AdminUITheme.scroll(table), BorderLayout.CENTER);

        // Status badge renderer on Status column
        table.getColumnModel().getColumn(5).setCellRenderer(AdminUITheme.statusRenderer());

        refreshBtn.addActionListener(e -> reload());
        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);
            double totalRevenue = 0;
            int paidCount = 0, pendingCount = 0, failedCount = 0;

            for (Payment p : paymentDAO.getAll()) {
                String status = p.getStatus() == null ? "" : p.getStatus().trim();
                if ("paid".equalsIgnoreCase(status))        { totalRevenue += p.getAmount(); paidCount++;    }
                else if ("pending".equalsIgnoreCase(status)) pendingCount++;
                else if ("failed".equalsIgnoreCase(status))  failedCount++;

                tableModel.addRow(new Object[]{
                    p.getId(), p.getBookingId(),
                    String.format("$%.2f", p.getAmount()),
                    p.getPaymentDate(), p.getMethod(), p.getStatus()
                });
            }

            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
            paidCountLabel.setText(String.valueOf(paidCount));
            // pendingCountLabel.setText(String.valueOf(pendingCount));
            // failedCountLabel.setText(String.valueOf(failedCount));

            // Re-apply renderer after reload
            table.getColumnModel().getColumn(5).setCellRenderer(AdminUITheme.statusRenderer());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load income report: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel reportCard(String title, JLabel valueLabel, Color accent) {
        JPanel panel = AdminUITheme.cardPanel(new BorderLayout(8, 8));

        JLabel titleLabel = AdminUITheme.muted(title);
        JLabel marker     = new JLabel("■");
        marker.setForeground(accent);
        marker.setFont(new Font("SansSerif", Font.BOLD, 24));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accent);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(marker,     BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        panel.add(header,     BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
}