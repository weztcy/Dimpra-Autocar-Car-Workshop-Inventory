package com.workshop.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    private static boolean isDarkMode = false;

    public static void setup() {
        try {
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("Table.rowHeight", 32);
            UIManager.put("TableHeader.height", 36);

            FlatLightLaf.setup();
            isDarkMode = false;
        } catch (Exception e) {
            System.err.println("Could not initialize FlatLaf theme: " + e.getMessage());
        }
    }

    public static void toggleTheme(JFrame frame) {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                isDarkMode = false;
            } else {
                FlatDarkLaf.setup();
                isDarkMode = true;
            }
            FlatLaf.updateUI();
            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}
