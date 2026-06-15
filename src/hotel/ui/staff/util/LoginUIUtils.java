package hotel.ui.staff.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUIUtils {

    private LoginUIUtils() {
    }

    // ── Primary dark button ─────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UIConstants.DARK_BTN);
        btn.setBorder(new EmptyBorder(12, 0, 12, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(UIConstants.DARK_BTN_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(UIConstants.DARK_BTN);
            }
        });
        return btn;
    }

    // ── Styled text/password field ──────────────────────────────────
    public static void styleInput(JTextField field) {
        field.setFont(UIConstants.FONT_BODY);
        field.setBackground(UIConstants.BG_INPUT_LIGHT);
        field.setForeground(UIConstants.TEXT_DARK);
        field.setCaretColor(UIConstants.TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT),
                new EmptyBorder(6, 10, 6, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ── "Some text? Link" footer row ────────────────────────────────
    public static JPanel linkRow(String plainText, String linkText, Runnable onClick) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel plain = new JLabel(plainText);
        plain.setFont(UIConstants.FONT_SMALL);
        plain.setForeground(UIConstants.TEXT_LIGHT);

        JLabel link = new JLabel(linkText);
        link.setFont(new Font("Segoe UI", Font.BOLD, 13));
        link.setForeground(UIConstants.ACCENT);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });

        row.add(plain);
        row.add(link);
        return row;
    }

    // ── Three hotel image panels (right side illustration) ──────────
    public static JPanel illustrationPanel() {
        String[] paths = {
                "src/hotel/images/resources/hotel1.jpg",
                "src/hotel/images/resources/hotel2.jpg",
                "src/hotel/images/resources/hotel3.jpg"
        };
        double[] hRatios = { 0.65, 0.75, 0.80 };
        int[] stagger = { -40, -40, -40 };

        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));
        panel.setBackground(UIConstants.BG_PAGE);
        panel.setBorder(new EmptyBorder(20, 8, 20, 20));

        for (int i = 0; i < 3; i++) {
            final double hRatio = hRatios[i];
            final int stag = stagger[i];
            Image[] holder = { null };
            try {
                holder[0] = new ImageIcon(paths[i]).getImage();
            } catch (Exception ignored) {
            }
            final Image img = holder[0];

            JPanel slot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int w = getWidth(), h = getHeight();
                    int cardH = (int) (h * hRatio);
                    int cardY = (h - cardH) / 2 + stag;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Shape clip = new java.awt.geom.RoundRectangle2D.Float(0, cardY, w, cardH, 20, 20);
                    g2.setClip(clip);
                    if (img != null) {
                        int imgW = img.getWidth(this), imgH = img.getHeight(this);
                        double sc = Math.max((double) w / imgW, (double) cardH / imgH);
                        g2.drawImage(img,
                                (w - (int) (imgW * sc)) / 2,
                                cardY + (cardH - (int) (imgH * sc)) / 2,
                                (int) (imgW * sc), (int) (imgH * sc), this);
                    } else {
                        g2.setColor(new Color(37, 114, 198));
                        g2.fill(clip);
                    }
                    g2.dispose();
                }
            };
            slot.setOpaque(false);
            panel.add(slot);
        }
        return panel;
    }
}