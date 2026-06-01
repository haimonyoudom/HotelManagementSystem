package hotel.ui.admin;

import hotel.dao.RoomDAO;
import hotel.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageRoomsPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Room Number", "Type", "Price/Night", "Available"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    public ManageRoomsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AdminUITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn     = AdminUITheme.primaryButton("➕  Add Room");
        JButton editBtn    = AdminUITheme.secondaryButton("✏  Edit Room");
        JButton deleteBtn  = AdminUITheme.dangerButton("🗑  Delete Room");
        JButton refreshBtn = AdminUITheme.secondaryButton("↻  Refresh");

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(actions,                    BorderLayout.NORTH);
        add(AdminUITheme.scroll(table), BorderLayout.CENTER);

        addBtn.addActionListener(e     -> openDialog(null));
        editBtn.addActionListener(e    -> editSelected());
        deleteBtn.addActionListener(e  -> deleteSelected());
        refreshBtn.addActionListener(e -> reload());

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);
            for (Room room : roomDAO.getAll()) {
                tableModel.addRow(new Object[]{
                    room.getId(), room.getRoomNumber(), room.getType(),
                    String.format("$%.2f", room.getPricePerNight()),
                    room.isAvailable() ? "Available" : "Occupied"
                });
            }
            // Status badge on Available column
            table.getColumnModel().getColumn(4).setCellRenderer(AdminUITheme.statusRenderer());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a room first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Room room = roomDAO.getById(id);
            if (room == null) { JOptionPane.showMessageDialog(this, "Room not found."); reload(); return; }
            openDialog(room);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a room first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete room #" + id + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            roomDAO.delete(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Room editing) {
        boolean addMode = editing == null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            addMode ? "Add Room" : "Edit Room", true);

        JPanel root = AdminUITheme.pagePanel();
        JPanel form = AdminUITheme.cardPanel(new GridBagLayout());

        JTextField        roomNumberField = AdminUITheme.textField();
        JComboBox<String> typeBox         = AdminUITheme.comboBox("Single", "Double", "Suite", "Deluxe");
        JTextField        priceField      = AdminUITheme.textField();
        JCheckBox         availableBox    = new JCheckBox("Available");
        availableBox.setOpaque(false);
        availableBox.setSelected(true);

        if (editing != null) {
            roomNumberField.setText(editing.getRoomNumber());
            typeBox.setSelectedItem(editing.getType());
            priceField.setText(String.valueOf(editing.getPricePerNight()));
            availableBox.setSelected(editing.isAvailable());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(7, 7, 7, 7);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        addRow(form, gbc, y++, "Room Number",    roomNumberField);
        addRow(form, gbc, y++, "Type",           typeBox);
        addRow(form, gbc, y++, "Price Per Night",priceField);
        addRow(form, gbc, y++, "Status",         availableBox);

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
            String number   = roomNumberField.getText().trim();
            String priceStr = priceField.getText().trim();
            if (number.isBlank() || priceStr.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Room number and price are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                Room room    = addMode ? new Room() : editing;
                room.setRoomNumber(number);
                room.setType(String.valueOf(typeBox.getSelectedItem()));
                room.setPricePerNight(price);
                room.setAvailable(availableBox.isSelected());
                if (addMode) roomDAO.add(room); else roomDAO.update(room);
                dialog.dispose();
                reload();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
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