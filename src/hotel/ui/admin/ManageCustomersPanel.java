package hotel.ui.admin;

import hotel.dao.CustomerDAO;
import hotel.model.Customer;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCustomersPanel extends JPanel {
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Phone", "Address"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = UITheme.textField();

    public ManageCustomersPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = UITheme.primaryButton("Add Customer");
        JButton editBtn = UITheme.secondaryButton("Edit Customer");
        JButton deleteBtn = UITheme.dangerButton("Delete Customer");
        JButton refreshBtn = UITheme.secondaryButton("Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchBtn = UITheme.secondaryButton("Search");
        searchPanel.add(searchBtn, BorderLayout.EAST);

        top.add(actions, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(top, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        addBtn.addActionListener(ignored -> openDialog(null));
        editBtn.addActionListener(ignored -> editSelected());
        deleteBtn.addActionListener(ignored -> deleteSelected());
        refreshBtn.addActionListener(ignored -> reload());
        searchBtn.addActionListener(ignored -> search());

        reload();
    }

    public void reload() {
        try {
            searchField.setText("");
            tableModel.setRowCount(0);

            List<Customer> customers = customerDAO.getAll();

            for (Customer customer : customers) {
                tableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();

        if (keyword.isBlank()) {
            reload();
            return;
        }

        try {
            tableModel.setRowCount(0);

            List<Customer> customers = customerDAO.searchByName(keyword);

            for (Customer customer : customers) {
                tableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Customer customer = customerDAO.getById(id);

            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found.");
                reload();
                return;
            }

            openDialog(customer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer #" + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.delete(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete customer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Customer editing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                editing == null ? "Add Customer" : "Edit Customer", true);

        JPanel root = UITheme.pagePanel();
        JPanel form = UITheme.cardPanel(new GridBagLayout());

        JTextField nameField = UITheme.textField();
        JTextField emailField = UITheme.textField();
        JTextField phoneField = UITheme.textField();
        JTextField addressField = UITheme.textField();

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
        addRow(form, gbc, y++, "Name", nameField);
        addRow(form, gbc, y++, "Email", emailField);
        addRow(form, gbc, y++, "Phone", phoneField);
        addRow(form, gbc, y++, "Address", addressField);

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
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Customer customer = editing == null ? new Customer() : editing;
                customer.setName(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setAddress(address);

                if (editing == null) {
                    customerDAO.add(customer);
                } else {
                    customerDAO.update(customer);
                }

                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save customer: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ignored -> dialog.dispose());

        dialog.setSize(520, 420);
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
}