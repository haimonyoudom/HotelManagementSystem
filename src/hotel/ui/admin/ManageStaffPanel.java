package hotel.ui.admin;

import hotel.dao.StaffDAO;
import hotel.dao.UserDAO;
import hotel.model.Staff;
import hotel.model.User;
import hotel.ui.common.UITheme;
import hotel.util.DateUtil;
import hotel.util.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStaffPanel extends JPanel {
    private final StaffDAO staffDAO = new StaffDAO();
    private final UserDAO userDAO = new UserDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Position", "Salary", "User ID"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ManageStaffPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = UITheme.primaryButton("Add Staff");
        JButton editBtn = UITheme.secondaryButton("Edit Staff");
        JButton deleteBtn = UITheme.dangerButton("Delete Staff");
        JButton refreshBtn = UITheme.secondaryButton("Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(actions, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        addBtn.addActionListener(ignored -> openDialog(null));
        editBtn.addActionListener(ignored -> editSelected());
        deleteBtn.addActionListener(ignored -> deleteSelected());
        refreshBtn.addActionListener(ignored -> reload());

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);

            List<Staff> staffList = staffDAO.getAll();

            for (Staff staff : staffList) {
                tableModel.addRow(new Object[]{
                        staff.getId(),
                        staff.getName(),
                        staff.getPosition(),
                        staff.getSalary(),
                        staff.getUserId()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load staff: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Staff staff = staffDAO.getById(id);

            if (staff == null) {
                JOptionPane.showMessageDialog(this, "Staff member not found.");
                reload();
                return;
            }

            openDialog(staff);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load staff member: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int userId = (int) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete staff #" + id + " and its login user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            staffDAO.delete(id);

            if (userId > 0) {
                userDAO.delete(userId);
            }

            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete staff: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Staff editing) {
        boolean addMode = editing == null;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                addMode ? "Add Staff" : "Edit Staff", true);

        JPanel root = UITheme.pagePanel();
        JPanel form = UITheme.cardPanel(new GridBagLayout());

        JTextField nameField = UITheme.textField();
        JTextField positionField = UITheme.textField();
        JTextField salaryField = UITheme.textField();
        JTextField usernameField = UITheme.textField();
        JPasswordField passwordField = UITheme.passwordField();

        if (editing != null) {
            nameField.setText(editing.getName());
            positionField.setText(editing.getPosition());
            salaryField.setText(String.valueOf(editing.getSalary()));

            usernameField.setEnabled(false);
            passwordField.setEnabled(false);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Name", nameField);
        addRow(form, gbc, y++, "Position", positionField);
        addRow(form, gbc, y++, "Salary", salaryField);

        if (addMode) {
            addRow(form, gbc, y++, "Login Username", usernameField);
            addRow(form, gbc, y++, "Login Password", passwordField);
        }

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
            String position = positionField.getText().trim();
            String salaryText = salaryField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isBlank() || position.isBlank() || salaryText.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Name, position and salary are required.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (addMode && (username.isBlank() || password.isBlank())) {
                JOptionPane.showMessageDialog(dialog, "Username and password are required for staff login.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double salary = Double.parseDouble(salaryText);

                Staff staff = addMode ? new Staff() : editing;
                staff.setName(name);
                staff.setPosition(position);
                staff.setSalary(salary);

                if (addMode) {
                    if (userDAO.getByUsername(username) != null) {
                        JOptionPane.showMessageDialog(dialog, "Username already exists.",
                                "Validation", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(dialog, "Salary must be a valid number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save staff: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ignored -> dialog.dispose());

        dialog.setSize(540, addMode ? 500 : 390);
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