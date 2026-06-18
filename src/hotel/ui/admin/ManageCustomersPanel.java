package hotel.ui.admin;

import hotel.dao.CustomerDAO;
import hotel.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCustomersPanel extends JPanel {
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Phone", "Address"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable     table       = new JTable(tableModel);
    private final JTextField searchField = AdminUITheme.textField();

    public ManageCustomersPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton editBtn    = AdminUITheme.secondaryButton("✏  Edit Customer");
        JButton deleteBtn  = AdminUITheme.dangerButton("🗑  Delete Customer");
        JButton refreshBtn = AdminUITheme.secondaryButton("↻  Refresh");

        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(AdminUITheme.SMALL_FONT);
        searchLbl.setForeground(AdminUITheme.TEXT_MUTED);
        searchPanel.add(searchLbl, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchBtn = AdminUITheme.secondaryButton("Search");
        searchPanel.add(searchBtn, BorderLayout.EAST);

        top.add(actions,     BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(top,                        BorderLayout.NORTH);
        add(AdminUITheme.scroll(table), BorderLayout.CENTER);

        editBtn.addActionListener(e    -> editSelected());
        deleteBtn.addActionListener(e  -> deleteSelected());
        refreshBtn.addActionListener(e -> reload());
        searchBtn.addActionListener(e  -> search());

        reload();
    }

    public void reload() {
        try {
            searchField.setText("");
            tableModel.setRowCount(0);
            for (Customer c : customerDAO.getAll()) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isBlank()) { reload(); return; }
        try {
            tableModel.setRowCount(0);
            for (Customer c : customerDAO.searchByName(keyword)) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a customer first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Customer c = customerDAO.getById(id);
            if (c == null) { JOptionPane.showMessageDialog(this, "Customer not found."); reload(); return; }
            openDialog(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a customer first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete customer #" + id + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            customerDAO.delete(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Customer editing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            editing == null ? "Add Customer" : "Edit Customer", true);

        JPanel root = AdminUITheme.pagePanel();
        JPanel form = AdminUITheme.cardPanel(new GridBagLayout());

        JTextField nameField    = AdminUITheme.textField();
        JTextField emailField   = AdminUITheme.textField();
        JTextField phoneField   = AdminUITheme.textField();
        JTextField addressField = AdminUITheme.textField();

        if (editing != null) {
            nameField.setText(editing.getName());
            emailField.setText(editing.getEmail());
            phoneField.setText(editing.getPhone());
            addressField.setText(editing.getAddress());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Name",    nameField);
        addRow(form, gbc, y++, "Email",   emailField);
        addRow(form, gbc, y++, "Phone",   phoneField);
        addRow(form, gbc, y++, "Address", addressField);

        JButton saveBtn   = AdminUITheme.primaryButton("Save");
        JButton cancelBtn = AdminUITheme.secondaryButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancelBtn); buttons.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        dialog.add(root);

        saveBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String addr  = addressField.getText().trim();
            if (name.isBlank() || email.isBlank() || phone.isBlank() || addr.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Customer c = editing == null ? new Customer() : editing;
                c.setName(name); c.setEmail(email); c.setPhone(phone); c.setAddress(addr);
                if (editing == null) customerDAO.add(c); else customerDAO.update(c);
                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent input) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AdminUITheme.SMALL_FONT);
        lbl.setForeground(AdminUITheme.TEXT_MUTED);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(input, gbc);
    }
}