package com.fci.seminar.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Utility class for common UI operations.
 * Provides helper methods for color manipulation, component styling, and layout.
 */
public final class UIUtils {
    
    private UIUtils() {
        // Prevent instantiation
    }
    
    // ===== Color Manipulation Methods =====
    
    /**
     * Darkens a color by the specified factor.
     * @param color The original color
     * @param factor The darkening factor (0.0 to 1.0, where 0.0 is black)
     * @return The darkened color
     */
    public static Color darken(Color color, double factor) {
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException("Factor must be between 0 and 1");
        }
        int r = (int) (color.getRed() * factor);
        int g = (int) (color.getGreen() * factor);
        int b = (int) (color.getBlue() * factor);
        return new Color(r, g, b, color.getAlpha());
    }
    
    /**
     * Lightens a color by the specified factor.
     * @param color The original color
     * @param factor The lightening factor (0.0 to 1.0, where 1.0 is white)
     * @return The lightened color
     */
    public static Color lighten(Color color, double factor) {
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException("Factor must be between 0 and 1");
        }
        int r = (int) (color.getRed() + (255 - color.getRed()) * factor);
        int g = (int) (color.getGreen() + (255 - color.getGreen()) * factor);
        int b = (int) (color.getBlue() + (255 - color.getBlue()) * factor);
        return new Color(r, g, b, color.getAlpha());
    }

    /**
     * Creates a color with the specified alpha transparency.
     * @param color The original color
     * @param alpha The alpha value (0-255)
     * @return The color with new alpha
     */
    public static Color withAlpha(Color color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha must be between 0 and 255");
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    /**
     * Blends two colors together.
     * @param color1 The first color
     * @param color2 The second color
     * @param ratio The blend ratio (0.0 = color1, 1.0 = color2)
     * @return The blended color
     */
    public static Color blend(Color color1, Color color2, double ratio) {
        if (ratio < 0 || ratio > 1) {
            throw new IllegalArgumentException("Ratio must be between 0 and 1");
        }
        int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        int a = (int) (color1.getAlpha() * (1 - ratio) + color2.getAlpha() * ratio);
        return new Color(r, g, b, a);
    }
    
    // ===== Component Styling Methods =====
    
    /**
     * Creates a compound border with padding.
     * @param outerBorder The outer border
     * @param padding The padding in pixels
     * @return The compound border
     */
    public static Border createPaddedBorder(Border outerBorder, int padding) {
        return BorderFactory.createCompoundBorder(
            outerBorder,
            BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }
    
    /**
     * Creates an empty border with uniform padding.
     * @param padding The padding in pixels
     * @return The empty border
     */
    public static Border createEmptyBorder(int padding) {
        return BorderFactory.createEmptyBorder(padding, padding, padding, padding);
    }
    
    /**
     * Creates an empty border with specified padding.
     * @param top Top padding
     * @param left Left padding
     * @param bottom Bottom padding
     * @param right Right padding
     * @return The empty border
     */
    public static Border createEmptyBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }

    // ===== Layout Helper Methods =====
    
    /**
     * Creates a horizontal box with components.
     * @param components The components to add
     * @return The horizontal box
     */
    public static Box createHorizontalBox(Component... components) {
        Box box = Box.createHorizontalBox();
        for (Component component : components) {
            box.add(component);
        }
        return box;
    }
    
    /**
     * Creates a vertical box with components.
     * @param components The components to add
     * @return The vertical box
     */
    public static Box createVerticalBox(Component... components) {
        Box box = Box.createVerticalBox();
        for (Component component : components) {
            box.add(component);
        }
        return box;
    }
    
    /**
     * Creates a horizontal strut (rigid spacing).
     * @param width The width in pixels
     * @return The strut component
     */
    public static Component createHorizontalStrut(int width) {
        return Box.createHorizontalStrut(width);
    }
    
    /**
     * Creates a vertical strut (rigid spacing).
     * @param height The height in pixels
     * @return The strut component
     */
    public static Component createVerticalStrut(int height) {
        return Box.createVerticalStrut(height);
    }
    
    /**
     * Creates horizontal glue (flexible spacing).
     * @return The glue component
     */
    public static Component createHorizontalGlue() {
        return Box.createHorizontalGlue();
    }
    
    /**
     * Creates vertical glue (flexible spacing).
     * @return The glue component
     */
    public static Component createVerticalGlue() {
        return Box.createVerticalGlue();
    }
    
    // ===== Graphics Helper Methods =====
    
    /**
     * Enables anti-aliasing for the given Graphics2D context.
     * @param g2 The Graphics2D context
     */
    public static void enableAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    /**
     * Draws a rounded rectangle with the specified parameters.
     * @param g2 The Graphics2D context
     * @param x The x coordinate
     * @param y The y coordinate
     * @param width The width
     * @param height The height
     * @param radius The corner radius
     * @param fill Whether to fill the rectangle
     */
    public static void drawRoundedRect(Graphics2D g2, int x, int y, int width, int height, 
                                        int radius, boolean fill) {
        if (fill) {
            g2.fillRoundRect(x, y, width, height, radius, radius);
        } else {
            g2.drawRoundRect(x, y, width, height, radius, radius);
        }
    }
    
    // ===== Dialog Helper Methods =====
    
    /**
     * Shows an information message dialog.
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a warning message dialog.
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     */
    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Shows an error message dialog.
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog.
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     * @return true if user confirmed, false otherwise
     */
    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
