package com.fci.seminar.ui.components;

import java.awt.*;
import javax.swing.border.AbstractBorder;

/**
 * Custom border with rounded corners.
 * Supports configurable color and radius.
 */
public class RoundedBorder extends AbstractBorder {
    
    private final Color color;
    private final int radius;
    private final int thickness;
    
    /**
     * Creates a rounded border with default thickness of 1.
     * @param color The border color
     * @param radius The corner radius
     */
    public RoundedBorder(Color color, int radius) {
        this(color, radius, 1);
    }
    
    /**
     * Creates a rounded border with specified thickness.
     * @param color The border color
     * @param radius The corner radius
     * @param thickness The border thickness
     */
    public RoundedBorder(Color color, int radius, int thickness) {
        this.color = color;
        this.radius = radius;
        this.thickness = thickness;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = thickness;
        return insets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public int getThickness() {
        return thickness;
    }
}
