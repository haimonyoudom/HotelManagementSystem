package hotel.ui.staff.util;

import java.awt.*;

public class UIConstants {

    // ── Dark theme ────────────────────────
    public static final Color BG_DARK = new Color(18, 18, 18);
    public static final Color BG_CARD = new Color(28, 28, 28);
    public static final Color BG_INPUT = new Color(35, 35, 35);
    public static final Color BG_SIDEBAR = new Color(22, 22, 22);
    public static final Color BG_SIDEBAR_HOVER = new Color(35, 35, 35);
    public static final Color BG_SIDEBAR_ACTIVE = new Color(40, 40, 40);

    public static final Color ACCENT_RED = new Color(200, 50, 50);
    public static final Color ACCENT_RED_HOVER = new Color(220, 70, 70);
    public static final Color ACCENT_GREEN = new Color(50, 150, 80);
    public static final Color ACCENT_GREEN_HOVER = new Color(60, 170, 90);

    public static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    public static final Color TEXT_SECONDARY = new Color(150, 150, 150);
    public static final Color TEXT_MUTED = new Color(100, 100, 100);

    public static final Color BORDER = new Color(45, 45, 45);

    // ── Light theme (Login / Signup screens) ─────────────────────────
    public static final Color BG_PAGE = new Color(245, 248, 252);
    public static final Color BG_CARD_LIGHT = Color.WHITE;
    public static final Color BG_INPUT_LIGHT = new Color(250, 251, 253);
    public static final Color BORDER_LIGHT = new Color(220, 225, 235);
    public static final Color BORDER_INPUT = new Color(210, 215, 223);

    public static final Color ACCENT = new Color(15, 84, 175);
    public static final Color DARK_BTN = new Color(0x16, 0x2D, 0x3A);
    public static final Color DARK_BTN_HOVER = new Color(0x22, 0x44, 0x55);

    public static final Color TEXT_DARK = new Color(20, 33, 61);
    public static final Color TEXT_MID = new Color(100, 110, 130);
    public static final Color TEXT_LIGHT = new Color(90, 100, 120);

    public static final Color ERR_COLOR = new Color(170, 34, 62);
    public static final Color OK_COLOR = new Color(34, 139, 34);

    // ── Staff Dashboard specific colors ───────────────────────────────
    public static final Color SIDEBAR_ACTIVE = new Color(224, 90, 43);

    // ── Stat card accent colors ──────────────────────────────────────
    public static final Color STAT_PURPLE = new Color(120, 100, 180);
    public static final Color STAT_BLUE = new Color(80, 150, 200);
    public static final Color STAT_RED = new Color(200, 100, 100);
    public static final Color STAT_YELLOW = new Color(220, 180, 80);

    // ── Status badge colors ──────────────────────────────────────────
    public static final Color BADGE_PENDING = new Color(210, 130, 50);
    public static final Color BADGE_APPROVED = new Color(60, 120, 200);
    public static final Color BADGE_CHECKIN = new Color(50, 160, 90);

    // ── Room status tile colors ──────────────────────────────────────
    public static final Color ROOM_OCCUPIED = new Color(220, 180, 80);
    public static final Color ROOM_AVAILABLE = new Color(100, 180, 100);
    public static final Color ROOM_CLEANING = new Color(220, 150, 100);
    public static final Color ROOM_MAINTENANCE = new Color(220, 100, 100);

    // ── Custom theme colors ──────────────────────────────────────────
    public static final Color THEME_NAVY = new Color(0x00, 0x00, 0x80);
    public static final Color THEME_WHITE_BG = new Color(0xFF, 0xFF, 0xFF);
    public static final Color THEME_DARK_FONT = new Color(0x28, 0x27, 0x28);

    // ── Shared fonts ─────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Shared dimensions ────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH = 210;
    public static final int BUTTON_HEIGHT = 42;
    public static final int INPUT_HEIGHT = 38;

    private UIConstants() {
    }
}