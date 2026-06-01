package hotel.ui.admin;

import hotel.dao.BookingDAO;
import hotel.dao.CustomerDAO;
import hotel.dao.PaymentDAO;
import hotel.dao.RoomDAO;
import hotel.dao.StaffDAO;
import hotel.model.Staff;
import hotel.model.Booking;
import hotel.model.Room;
import hotel.ui.admin.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminHomePanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final StaffDAO staffDAO = new StaffDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private final JLabel roomsValue = valueLabel("0");
    private final JLabel bookingsValue = valueLabel("0");
    private final JLabel customersValue = valueLabel("0");
    private final JLabel staffValue = valueLabel("0");
    private final JLabel revenueValue = valueLabel("$0.00");
    
    private ChartPlaceholder chartPlaceholder;

    public AdminHomePanel() {
        setLayout(new BorderLayout(24, 24));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel cards = new JPanel(new GridLayout(1, 5, 18, 0));
        cards.setOpaque(false);

        cards.add(statCard("Available Rooms", roomsValue, UITheme.PRIMARY));
        cards.add(statCard("Bookings", bookingsValue, UITheme.SECONDARY));
        cards.add(statCard("Customers", customersValue, UITheme.SUCCESS));
        cards.add(statCard("Staff", staffValue, new Color(145, 117, 211)));
        cards.add(statCard("Revenue", revenueValue, UITheme.DANGER));

        JPanel reportCard = UITheme.cardPanel(new BorderLayout());
        reportCard.setPreferredSize(new Dimension(0, 360));

        JLabel reportTitle = UITheme.heading("Hotel Activity Overview");
        reportCard.add(reportTitle, BorderLayout.NORTH);
        chartPlaceholder = new ChartPlaceholder();
        reportCard.add(chartPlaceholder, BorderLayout.CENTER);

        add(cards, BorderLayout.NORTH);
        add(reportCard, BorderLayout.CENTER);

        reload();
    }

    public void reload() {
        try {
            List<Room> rooms = roomDAO.getAll();
            List<Booking> bookings = bookingDAO.getAll();
            List<hotel.model.Customer> customers = customerDAO.getAll();
            List<Staff> staff = staffDAO.getAll();

            long availableRooms = rooms.stream().filter(Room::isAvailable).count();

            roomsValue.setText(String.valueOf(availableRooms));
            bookingsValue.setText(String.valueOf(bookings.size()));
            customersValue.setText(String.valueOf(customers.size()));
            staffValue.setText(String.valueOf(staff.size()));
            revenueValue.setText(String.format("$%.2f", paymentDAO.getTotalRevenue()));
            
            // Update chart with actual data
            int[] data = {
                (int) availableRooms,
                bookings.size(),
                staff.size(),
                customers.size(),
                (int) paymentDAO.getTotalRevenue() / 100  // Scale down for visual consistency
            };
            chartPlaceholder.updateData(data);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel panel = UITheme.cardPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(150, 100));

        JLabel icon = new JLabel("■");
        icon.setForeground(accent);
        icon.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel titleLabel = UITheme.muted(title);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(icon, BorderLayout.WEST);
        top.add(titleLabel, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    private JLabel valueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(UITheme.TEXT);
        return label;
    }

    private static class ChartPlaceholder extends JPanel {
        private final Color[] colors = {
                UITheme.PRIMARY,
                UITheme.SECONDARY,
                new Color(145, 117, 211),
                UITheme.SUCCESS,
                new Color(205, 100, 154)
        };
        
        private int[] data = {0, 0, 0, 0, 0};

        ChartPlaceholder() {
            setOpaque(false);
        }
        
        public void updateData(int[] newData) {
            this.data = newData;
            repaint();  // Refresh the chart
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) / 2;
            int x = getWidth() / 2 - size / 2 - 80;
            int y = getHeight() / 2 - size / 2;

            // Calculate arcs based on actual data
            int[] arcs = calculateArcs(data);

            int start = 90;

            for (int i = 0; i < arcs.length; i++) {
                g.setColor(colors[i]);
                g.setStroke(new BasicStroke(28, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                g.drawArc(x, y, size, size, start, -arcs[i]);
                start -= arcs[i];
            }

            int legendX = x + size + 90;
            int legendY = y + 30;

            String[] labels = {"Rooms", "Bookings", "Staff", "Customers", "Revenue"};

            for (int i = 0; i < labels.length; i++) {
                g.setColor(colors[i]);
                g.fillOval(legendX, legendY + i * 34, 16, 16);

                g.setColor(UITheme.TEXT);
                g.setFont(UITheme.UI_FONT);
                g.drawString(labels[i] + " (" + data[i] + ")", legendX + 28, legendY + 13 + i * 34);
            }

            g.dispose();
        }
        
        private int[] calculateArcs(int[] values) {
            int[] arcs = new int[values.length];
            int total = 0;
            
            // Calculate total
            for (int value : values) {
                total += value;
            }
            
            if (total == 0) {
                // If no data, show equal distribution
                for (int i = 0; i < arcs.length; i++) {
                    arcs[i] = 72;  // 360 / 5 = 72 degrees each
                }
                return arcs;
            }
            
            // Calculate arc percentage based on data
            for (int i = 0; i < values.length; i++) {
                arcs[i] = (int) ((values[i] / (double) total) * 360);
            }
            
            return arcs;
        }
    }
}