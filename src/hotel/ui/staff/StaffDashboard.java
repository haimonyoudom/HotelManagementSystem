package hotel.ui.staff;

import hotel.ui.common.LoginFrame;
import hotel.ui.staff.util.UIConstants;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import hotel.model.User;
import hotel.service.BookingService;
import hotel.service.RoomService;
import hotel.model.Booking;
import hotel.model.Room;

import java.util.List;

public class StaffDashboard extends JFrame {

    // ── Panel references ──────────────────────────────────────────────
    static JPanel sidebarPanel;
    static JPanel mainPanel;

    private String staffName;
    private String staffEmail;
    private BookingService bookingService;
    private RoomService roomService;
    private JButton activeSidebarBtn = null;

    // ─────────────────────────────────────────────────────────────────
    public StaffDashboard(User user) {
        this.staffName = user.getName() != null ? user.getName() : user.getUsername();
        this.staffEmail = user.getEmail() != null ? user.getEmail() : user.getUsername() + "@hms.com";
        this.bookingService = new BookingService();
        this.roomService = new RoomService();

        setupFrame();
        createSidebar();
        createMainContent();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    // ── Frame setup ───────────────────────────────────────────────────
    private void setupFrame() {
        setTitle("Staff Dashboard – Hotel Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setSize(1200, 750);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.THEME_WHITE_BG);
    }

    // ── Sidebar ───────────────────────────────────────────────────────
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebarPanel.setMinimumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebarPanel.setBackground(UIConstants.THEME_WHITE_BG);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // ── Brand label ──────────────────────────────────────────────
        sidebarPanel.add(Box.createVerticalStrut(24));
        JLabel hmsLabel = new JLabel("HMS");
        hmsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hmsLabel.setForeground(UIConstants.THEME_NAVY);
        hmsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Left padding for the brand label only
        hmsLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        sidebarPanel.add(hmsLabel);
        sidebarPanel.add(Box.createVerticalStrut(22));

        // ── Section label ────────────────────────────────────────────
        JLabel sectionLabel = new JLabel("Staff");
        sectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sectionLabel.setForeground(new Color(130, 130, 130));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 8, 0));
        sidebarPanel.add(sectionLabel);

        String[] iconPaths = {
                "src/hotel/images/resources/dashboardicon.png",
                "src/hotel/images/resources/bookingicon.png",
                "src/hotel/images/resources/check-inicon.png",
                "src/hotel/images/resources/roomicon.png"
        };
        String[] labels = { "Dashboard", "Bookings", "Check-in/Out", "Rooms" };

        for (int i = 0; i < labels.length; i++) {
            JButton btn = buildNavButton(iconPaths[i], labels[i]);
            sidebarPanel.add(btn);
            if (i == 0) {
                markActive(btn);
                activeSidebarBtn = btn;
            }
        }

        sidebarPanel.add(Box.createVerticalGlue());

        // ── User chip ────────────────────────────────────────────────
        sidebarPanel.add(buildUserChip());

        // ── Logout button ─────────────────────────────────────────────
        ImageIcon rawLogout = new ImageIcon("src/hotel/images/resources/logouticon.png");
        BufferedImage scaledLogout = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledLogout.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(rawLogout.getImage(), 0, 0, 20, 20, null);
        g2d.dispose();

        JButton logoutBtn = new JButton("  Logout", new ImageIcon(scaledLogout));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setForeground(new Color(180, 60, 60));
        logoutBtn.setBackground(UIConstants.THEME_WHITE_BG);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setIconTextGap(10);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(255, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(UIConstants.THEME_WHITE_BG);
            }
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new hotel.ui.common.LoginFrame().setVisible(true));
        });

        sidebarPanel.add(logoutBtn);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton buildNavButton(String icon, String label) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Active: fill entire row; otherwise transparent fill
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // ── Layout: icon box + label ──────────────────────────────────
        btn.setLayout(new BorderLayout());
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setMinimumSize(new Dimension(0, 46));

        // No border, no focus ring – we paint everything ourselves
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); // we paint manually above
        btn.setOpaque(false);
        btn.setBackground(UIConstants.THEME_WHITE_BG);

        // Inner content panel (transparent, sits inside the button)
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        // Icon box
        JLabel iconBox = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Load and scale the image
        ImageIcon rawIcon = new ImageIcon(icon); // 'icon' is the path string
        Image scaled = rawIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        iconBox.setIcon(new ImageIcon(scaled));
        iconBox.setOpaque(false);
        iconBox.setHorizontalAlignment(SwingConstants.CENTER);
        iconBox.setVerticalAlignment(SwingConstants.CENTER);
        Dimension iconSize = new Dimension(32, 32);
        iconBox.setPreferredSize(iconSize);
        iconBox.setMinimumSize(iconSize);
        iconBox.setMaximumSize(iconSize);

        // Label
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(new Color(80, 80, 80));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        inner.add(iconBox);
        inner.add(textLabel);
        btn.add(inner, BorderLayout.CENTER);

        // Store references so we can update colours on state change
        btn.putClientProperty("iconBox", iconBox);
        btn.putClientProperty("textLabel", textLabel);
        btn.putClientProperty("innerPanel", inner);

        // ── Mouse interactions ────────────────────────────────────────
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeSidebarBtn) {
                    btn.setBackground(new Color(240, 240, 240));
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeSidebarBtn) {
                    btn.setBackground(UIConstants.THEME_WHITE_BG);
                    btn.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (activeSidebarBtn != null)
                    deactivate(activeSidebarBtn);
                markActive(btn);
                activeSidebarBtn = btn;
                showPanel(label);
            }
        });

        return btn;
    }

    private void showPanel(String label) {
        Container cp = getContentPane();
        BorderLayout bl = (BorderLayout) cp.getLayout();
        Component current = bl.getLayoutComponent(BorderLayout.CENTER);
        if (current != null) {
            cp.remove(current);
        }

        JPanel panel;
        switch (label) {
            case "Bookings":
                panel = new PendingBookingsPanel();
                break;
            case "Check-in/Out":
                panel = new CheckInOutPanel();
                break;
            case "Rooms":
                panel = new RoomStatusPanel(() -> refreshDashboard());
                break;
            default:
                refreshDashboard();
                panel = mainPanel;
                break;
        }

        panel.setOpaque(true);
        cp.add(panel, BorderLayout.CENTER);
        cp.revalidate();
        cp.repaint();
    }

    /** Turn a button visually active (orange row + white text). */
    private void markActive(JButton btn) {
        btn.setBackground(new Color(230, 240, 255));
        btn.setContentAreaFilled(false); // keep custom paint
        JLabel iconBox = (JLabel) btn.getClientProperty("iconBox");
        JLabel textLabel = (JLabel) btn.getClientProperty("textLabel");
        if (iconBox != null)
            iconBox.setForeground(UIConstants.THEME_NAVY);
        if (textLabel != null) {
            textLabel.setForeground(UIConstants.THEME_NAVY);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }
        btn.repaint();
    }

    /** Restore a button to its inactive appearance. */
    private void deactivate(JButton btn) {
        btn.setBackground(UIConstants.THEME_WHITE_BG);
        JLabel iconBox = (JLabel) btn.getClientProperty("iconBox");
        JLabel textLabel = (JLabel) btn.getClientProperty("textLabel");
        if (iconBox != null)
            iconBox.setForeground(new Color(150, 150, 150));
        if (textLabel != null) {
            textLabel.setForeground(new Color(80, 80, 80));
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        btn.repaint();
    }

    // ── User chip ─────────────────────────────────
    private JPanel buildUserChip() {
        JPanel chip = new JPanel();
        chip.setBackground(new Color(245, 245, 245));
        chip.setLayout(new BoxLayout(chip, BoxLayout.X_AXIS));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel avatar = new JLabel("ST") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.THEME_NAVY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setOpaque(false);
        Dimension av = new Dimension(36, 36);
        avatar.setPreferredSize(av);
        avatar.setMinimumSize(av);
        avatar.setMaximumSize(av);

        JPanel info = new JPanel();
        info.setBackground(new Color(245, 245, 245));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameL = new JLabel(staffName);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameL.setForeground(UIConstants.THEME_NAVY);

        JLabel mailL = new JLabel(staffEmail);
        mailL.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        mailL.setForeground(new Color(130, 130, 130));

        info.add(nameL);
        info.add(mailL);

        chip.add(avatar);
        chip.add(Box.createHorizontalStrut(10));
        chip.add(info);

        return chip;
    }

    // ── Main content ──────────────────────────────────────────────────
    private void createMainContent() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.THEME_WHITE_BG);

        mainPanel.add(buildHeaderBar(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 20));
        contentWrapper.setBackground(UIConstants.THEME_WHITE_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 30, 30, 30));

        contentWrapper.add(buildGreetingCard(), BorderLayout.NORTH);

        JPanel innerBody = new JPanel(new BorderLayout(0, 24));
        innerBody.setBackground(UIConstants.THEME_WHITE_BG);
        innerBody.add(buildStatCardsRow(), BorderLayout.NORTH);
        innerBody.add(buildBottomRow(), BorderLayout.CENTER);

        contentWrapper.add(innerBody, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    // ── Header bar ────────────────────────────────────────────────────
    private JPanel buildHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.THEME_WHITE_BG);
        bar.setPreferredSize(new Dimension(0, 68));
        bar.setMinimumSize(new Dimension(0, 68));
        bar.setBorder(BorderFactory.createEmptyBorder(18, 30, 10, 30));

        JLabel title = new JLabel("DASHBOARD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UIConstants.THEME_NAVY);

        JPanel right = new JPanel();
        right.setBackground(UIConstants.THEME_WHITE_BG);
        right.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JLabel dateLbl = new JLabel(getCurrentDate());
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLbl.setForeground(UIConstants.THEME_DARK_FONT);

        // load notification icon
        ImageIcon rawBell = new ImageIcon("src/hotel/images/resources/notificationicon.png");
        BufferedImage scaledBell = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledBell.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(rawBell.getImage(), 0, 0, 20, 20, null);
        g2d.dispose();

        JLabel bell = new JLabel(new ImageIcon(scaledBell));
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        right.add(dateLbl);
        right.add(bell);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Greeting card ─────────────────────────────────────────────────
    private JPanel buildGreetingCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(245, 245, 245),
                        getWidth(), getHeight(), new Color(250, 250, 250));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 110));
        card.setMinimumSize(new Dimension(0, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 28, 20, 28)));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel greet = new JLabel("Good morning, " + staffName);
        greet.setFont(new Font("Segoe UI", Font.BOLD, 22));
        greet.setForeground(UIConstants.THEME_DARK_FONT);

        JLabel sub = new JLabel("Here is your overview for today, " + getCurrentDateShort());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 120, 120));
        sub.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        text.add(greet);
        text.add(sub);
        card.add(text, BorderLayout.WEST);

        return card;
    }

    // ── Stat cards row ────────────────────────────────────────────────
    private JPanel buildStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(UIConstants.THEME_WHITE_BG);
        row.setPreferredSize(new Dimension(0, 110));
        row.setMinimumSize(new Dimension(0, 80));

        int[] stats = loadStatisticsData();

        int confirmedCheckins = stats[1];
        int checkedIn = stats[2];
        int totalExpected = confirmedCheckins + checkedIn;

        row.add(createStatCard("PENDING BOOKINGS",
                String.valueOf(stats[0]),
                "Awaiting approval",
                UIConstants.STAT_PURPLE));

        row.add(createStatCard("CHECK-INS",
                String.valueOf(checkedIn),
                "checked in of " + totalExpected,
                UIConstants.STAT_BLUE));

        row.add(createStatCard("CHECK-OUTS",
                String.valueOf(checkedIn),
                checkedIn + " pending departure",
                UIConstants.STAT_RED));

        row.add(createStatCard("OCCUPIED ROOMS",
                String.valueOf(stats[3]),
                "of " + stats[4] + " total",
                UIConstants.STAT_YELLOW));

        return row;
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.THEME_WHITE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(new Color(120, 120, 120));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(accent);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(120, 120, 120));

        JPanel stack = new JPanel(new GridLayout(3, 1, 0, 2));
        stack.setBackground(UIConstants.THEME_WHITE_BG);
        stack.add(titleLbl);
        stack.add(valueLbl);
        stack.add(subLbl);
        card.add(stack, BorderLayout.CENTER);

        return card;
    }

    // ── Bottom split row ──────────────────────────────────────────────
    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(UIConstants.THEME_WHITE_BG);

        row.add(createSchedulePanel());
        row.add(createRoomStatusPanel());
        return row;
    }

    // ── Schedule panel ────────────────────────────────────────────────
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.THEME_WHITE_BG);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel titleLbl = new JLabel("Upcoming schedule");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(UIConstants.THEME_DARK_FONT);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        panel.add(titleLbl, BorderLayout.NORTH);

        String[] cols = { "GUEST", "ROOM", "TIME", "STATUS" };
        Object[][] data = loadBookingData();

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setBackground(UIConstants.THEME_WHITE_BG);
        table.setForeground(UIConstants.THEME_DARK_FONT);
        table.setGridColor(new Color(200, 200, 200));
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 220, 220));
        table.setSelectionForeground(UIConstants.THEME_DARK_FONT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.THEME_WHITE_BG);
        header.setForeground(UIConstants.THEME_NAVY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(row % 2 == 0 ? UIConstants.THEME_WHITE_BG : new Color(248, 248, 248));
                setForeground(UIConstants.THEME_DARK_FONT);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, rowRenderer);

        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                String status = val == null ? "" : val.toString();
                Color rowBg = row % 2 == 0 ? UIConstants.THEME_WHITE_BG : new Color(248, 248, 248);

                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
                wrapper.setBackground(rowBg);

                JLabel badge = new JLabel(status) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                badge.setOpaque(false);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setForeground(Color.WHITE);
                badge.setHorizontalAlignment(SwingConstants.CENTER);
                badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

                Color bg = UIConstants.BADGE_PENDING;
                if (status.equalsIgnoreCase("approved"))
                    bg = UIConstants.BADGE_APPROVED;
                else if (status.equalsIgnoreCase("checked in"))
                    bg = UIConstants.BADGE_CHECKIN;
                badge.setBackground(bg);

                wrapper.add(badge);
                return wrapper;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(UIConstants.THEME_WHITE_BG);
        sp.getViewport().setBackground(UIConstants.THEME_WHITE_BG);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Room Status panel ─────────────────────────────────────────────
    private JPanel createRoomStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.THEME_WHITE_BG);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel titleLbl = new JLabel("Room Status");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(UIConstants.THEME_NAVY);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setBackground(UIConstants.THEME_WHITE_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 20, 20, 20));

        int[] counts = loadRoomStatusCounts();
        grid.add(createRoomBox(String.valueOf(counts[0]), "Occupied", UIConstants.ROOM_OCCUPIED));
        grid.add(createRoomBox(String.valueOf(counts[1]), "Available", UIConstants.ROOM_AVAILABLE));
        grid.add(createRoomBox(String.valueOf(counts[2]), "Cleaning", UIConstants.ROOM_CLEANING));
        grid.add(createRoomBox(String.valueOf(counts[3]), "Maintenance", UIConstants.ROOM_MAINTENANCE));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoomBox(String number, String label, Color bg) {
        JPanel box = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setOpaque(false);

        // Inner panel holds the two labels, centered
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel numLbl = new JLabel(number);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 34));
        numLbl.setForeground(Color.BLACK);
        numLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labLbl = new JLabel(label);
        labLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labLbl.setForeground(Color.BLACK);
        labLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        labLbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        inner.add(numLbl);
        inner.add(labLbl);

        box.add(inner); // GridBagLayout centers inner by default
        return box;
    }

    // ── Reload ───────────────────────────────────────────
    private void refreshDashboard() {
        mainPanel.removeAll();
        mainPanel.add(buildHeaderBar(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 20));
        contentWrapper.setBackground(UIConstants.THEME_WHITE_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 30, 30, 30));
        contentWrapper.add(buildGreetingCard(), BorderLayout.NORTH);

        JPanel innerBody = new JPanel(new BorderLayout(0, 24));
        innerBody.setBackground(UIConstants.THEME_WHITE_BG);
        innerBody.add(buildStatCardsRow(), BorderLayout.NORTH);
        innerBody.add(buildBottomRow(), BorderLayout.CENTER);

        contentWrapper.add(innerBody, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // ── Data loaders ──────────────────────────────────────────────────
    private Object[][] loadBookingData() {
        List<Booking> bookings = bookingService.getAllBookings();
        int count = 0;
        for (Booking b : bookings) {
            String s = b.getStatus();
            if (s.equalsIgnoreCase("pending") || s.equalsIgnoreCase("approved")
                    || s.equalsIgnoreCase("checked in")) {
                if (++count >= 6)
                    break;
            }
        }
        Object[][] data = new Object[count][4];
        int i = 0;
        for (Booking b : bookings) {
            if (i >= count)
                break;
            String s = b.getStatus();
            if (s.equalsIgnoreCase("pending") || s.equalsIgnoreCase("approved")
                    || s.equalsIgnoreCase("checked in")) {
                data[i][0] = "Guest #" + b.getCustomerId();
                data[i][1] = "Room #" + b.getRoomId();
                data[i][2] = b.getCheckInDate();
                data[i][3] = b.getStatus();
                i++;
            }
        }
        return data;
    }

    private int[] loadRoomStatusCounts() {
        int[] c = { 0, 0, 0, 0 }; // occupied, available, cleaning, maintenance
        for (Room r : roomService.getAllRooms()) {
            String status = r.getStatus() != null ? r.getStatus() : (r.isAvailable() ? "available" : "booked");
            switch (status.toLowerCase()) {
                case "available":
                    c[1]++;
                    break;
                case "cleaning":
                    c[2]++;
                    break;
                case "maintenance":
                    c[3]++;
                    break;
                default:
                    c[0]++;
                    break; // booked = occupied
            }
        }
        return c;
    }

    private int[] loadStatisticsData() {
        // [0]pending, [1]confirmed(ready checkin), [2]checked_in(ready checkout),
        // [3]occupied, [4]total rooms
        int[] s = { 0, 0, 0, 0, 0 };

        for (Booking b : bookingService.getAllBookings()) {
            String status = b.getStatus() != null ? b.getStatus() : "";
            if ("pending".equalsIgnoreCase(status))
                s[0]++;
            if ("confirmed".equalsIgnoreCase(status))
                s[1]++; // awaiting check-in
            if ("checked_in".equalsIgnoreCase(status))
                s[2]++; // awaiting check-out
        }

        List<Room> allRooms = roomService.getAllRooms();
        s[4] = allRooms.size();
        for (Room r : allRooms) {
            String status = r.getStatus() != null ? r.getStatus()
                    : (r.isAvailable() ? "available" : "booked");
            if (!"available".equalsIgnoreCase(status))
                s[3]++;
        }
        return s;
    }

    // ── Date helpers ──────────────────────────────────────────────────
    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
    }

    private String getCurrentDateShort() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d"));
    }
}