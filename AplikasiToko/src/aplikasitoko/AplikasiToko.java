package aplikasitoko; 

import aplikasitoko.ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class AplikasiToko { 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                UIManager.put("nimbusBase",        new Color(0x1e3a5f));
                UIManager.put("nimbusBlueGrey",    new Color(0x64748b));
                UIManager.put("control",           new Color(0xf1f5f9));
                UIManager.put("text",              new Color(0x1e293b));
                UIManager.put("nimbusSelectionBackground", new Color(0x3b82f6));
                UIManager.put("Button.arc",        8);
                UIManager.put("Component.arc",     8);
                UIManager.put("TextComponent.arc", 8);
                UIManager.put("defaultFont",       new Font("Segoe UI", Font.PLAIN, 13));
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}