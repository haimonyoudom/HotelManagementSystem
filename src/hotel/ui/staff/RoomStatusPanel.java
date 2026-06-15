package hotel.ui.staff;

import hotel.dao.RoomDAO;
import hotel.model.Room;
import hotel.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomStatusPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Room Number", "Type", "Price/Night", "Available"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public RoomStatusPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        JButton availableBtn = UITheme.primaryButton("Mark Available");
        JButton unavailableBtn = UITheme.dangerButton("Mark Unavailable");

        actions.add(refreshBtn);
        actions.add(availableBtn);
        actions.add(unavailableBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        add(actions, BorderLayout.NORTH);
        add(UITheme.scroll(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(ignored -> reload());
        availableBtn.addActionListener(ignored -> updateAvailability(true));
        unavailableBtn.addActionListener(ignored -> updateAvailability(false));

        reload();
    }

    public void reload() {
        try {
            tableModel.setRowCount(0);

            List<Room> rooms = roomDAO.getAll();

            for (Room room : rooms) {
                tableModel.addRow(new Object[]{
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        room.getPricePerNight(),
                        room.isAvailable()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAvailability(boolean available) {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Room room = roomDAO.getById(id);

            if (room == null) {
                JOptionPane.showMessageDialog(this, "Room not found.");
                reload();
                return;
            }

            room.setAvailable(available);
            roomDAO.update(room);

            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update room status: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}