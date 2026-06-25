package aplikasitoko.ui;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

public final class Theme {
    private Theme() {}

    public static final Color SIDEBAR_BG      = new Color(0x1e293b);
    public static final Color SIDEBAR_TEXT    = new Color(0x94a3b8);
    public static final Color TOPBAR_BG       = Color.WHITE;
    public static final Color CONTENT_BG      = new Color(0xf0f4f8);
    public static final Color CARD_BG         = Color.WHITE;
    public static final Color BORDER          = new Color(0xe2e8f0);
    public static final Color TABLE_HEADER    = new Color(0xf8fafc);
    public static final Color TABLE_SELECT    = new Color(0xdbeafe);
    public static final Color TABLE_SEL_TEXT  = new Color(0x1e40af);

    public static final Color TEXT_PRIMARY    = new Color(0x1e293b);
    public static final Color TEXT_SECONDARY  = new Color(0x64748b);
    public static final Color TEXT_MUTED      = new Color(0x94a3b8);

    public static final Color BTN_PRIMARY     = new Color(0x1e40af);
    public static final Color BTN_DANGER      = new Color(0xdc2626);
    public static final Color BTN_OUTLINE     = new Color(0xf1f5f9);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_NAV    = new Font("Segoe UI", Font.PLAIN, 13);

    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    public static String formatRupiah(double amount) { return RUPIAH.format(amount); }

    public static final int SIDEBAR_WIDTH   = 210;
    public static final int TOPBAR_HEIGHT   = 50;
    public static final int ROW_HEIGHT      = 38;
}