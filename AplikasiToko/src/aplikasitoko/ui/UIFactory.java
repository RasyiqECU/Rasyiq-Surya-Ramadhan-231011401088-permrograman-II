package aplikasitoko.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public final class UIFactory {
    private UIFactory() {}

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Theme.CARD_BG);
        p.setBorder(new CompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));
        return p;
    }

    public static JButton primaryButton(String text) { return styledButton(text, Theme.BTN_PRIMARY, Color.WHITE); }
    public static JButton dangerButton(String text)  { return styledButton(text, Theme.BTN_DANGER, Color.WHITE); }
    public static JButton outlineButton(String text) { return styledButton(text, Theme.BTN_OUTLINE, Theme.TEXT_PRIMARY); }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    public static JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(Theme.FONT_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER, 1, true), new EmptyBorder(5, 9, 5, 9)));
        return tf;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(Theme.FONT_BODY);
        pf.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER, 1, true), new EmptyBorder(5, 9, 5, 9)));
        return pf;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }

    public static JLabel headerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADER);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }
}