package hotel.ui.admin;

import hotel.dao.StaffDAO;
import hotel.dao.UserDAO;
import hotel.model.Staff;
import hotel.model.User;
import hotel.util.DateUtil;
import hotel.util.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStaffPanel extends JPanel {
    private final StaffDAO staffDAO = new StaffDAO();
    private final UserDAO  userDAO  = new UserDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Position", "Salary", "User ID"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    public ManageStaffPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn     = AdminUITheme.primaryButton(" Add Staff");
        JButton editBtn    = AdminUITheme.secondaryButton(" Edit Staff");
        JButton deleteBtn  = AdminUITheme.dangerButton(" Delete Staff");
        JButton refreshBtn = AdminUITheme.secondaryButton(" Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(actions,                      BorderLayout.NORTH);
        add(AdminUITheme.scroll(table),   BorderLayout.CENTER);

        addBtn.addActionListener(e     -> openDialog(null));
        editBtn.addActionListener(e    -> editSelected());
        deleteBtn.addActionListener(e  -> deleteSelected());
        refreshBtn.addActionListener(e -> reload());

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);
            for (Staff s : staffDAO.getAll()) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getPosition(),
                    String.format("$%.2f", s.getSalary()), s.getUserId()
                });
            }
            // Apply status-style renderer to Position column
            table.getColumnModel().getColumn(2).setCellRenderer(AdminUITheme.statusRenderer());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load staff: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a staff member first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Staff staff = staffDAO.getById(id);
            if (staff == null) { JOptionPane.showMessageDialog(this, "Staff not found."); reload(); return; }
            openDialog(staff);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load staff: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a staff member first."); return; }
        int id     = (int) tableModel.getValueAt(row, 0);
        int userId = (int) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete staff #" + id + " and their login account?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            staffDAO.delete(id);
            if (userId > 0) userDAO.delete(userId);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Staff editing) {
        boolean addMode = editing == null;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            addMode ? "Add Staff" : "Edit Staff", true);

        JPanel root = AdminUITheme.pagePanel();
        JPanel form = AdminUITheme.cardPanel(new GridBagLayout());

        JTextField        nameField     = AdminUITheme.textField();
        // ── Position is a combobox instead of free text ──
        JComboBox<String> positionBox   = AdminUITheme.comboBox(
            "Manager", "Receptionist", "Housekeeping", "Security", "Accountant", "Chef"
        );
        JTextField        salaryField   = AdminUITheme.textField();
        JTextField        usernameField = AdminUITheme.textField();
        JPasswordField    passwordField = AdminUITheme.passwordField();

        if (editing != null) {
            nameField.setText(editing.getName());
            positionBox.setSelectedItem(editing.getPosition());
            salaryField.setText(String.valueOf(editing.getSalary()));
            usernameField.setEnabled(false);
            passwordField.setEnabled(false);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(7, 7, 7, 7);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Name",            nameField);
        addRow(form, gbc, y++, "Position",         positionBox);
        addRow(form, gbc, y++, "Salary ($)",       salaryField);
        if (addMode) {
            addRow(form, gbc, y++, "Login Username", usernameField);
            addRow(form, gbc, y++, "Login Password", passwordField);
        }

        JButton saveBtn   = AdminUITheme.primaryButton("Save");
        JButton cancelBtn = AdminUITheme.secondaryButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        dialog.add(root);

        saveBtn.addActionListener(e -> {
            String name     = nameField.getText().trim();
            String position = (String) positionBox.getSelectedItem();
            String salaryTx = salaryField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isBlank() || salaryTx.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Name and salary are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (addMode && (username.isBlank() || password.isBlank())) {
                JOptionPane.showMessageDialog(dialog, "Username and password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double salary = Double.parseDouble(salaryTx);
                Staff staff   = addMode ? new Staff() : editing;
                staff.setName(name);
                staff.setPosition(position);
                staff.setSalary(salary);

                if (addMode) {
                    if (userDAO.getByUsername(username) != null) {
                        JOptionPane.showMessageDialog(dialog, "Username already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    User user = new User();
                    user.setUsername(username);
                    user.setPasswordHash(PasswordHasher.hash(password));
                    user.setRole("STAFF");
                    user.setCreatedAt(DateUtil.today());
                    userDAO.add(user);
                    staff.setUserId(user.getId());
                    staffDAO.add(staff);
                } else {
                    staffDAO.update(staff);
                }
                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Salary must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(540, addMode ? 500 : 380);
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