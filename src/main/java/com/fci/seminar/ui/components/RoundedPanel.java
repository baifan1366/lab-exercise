package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import javax.swing.*;

/**
 * Custom JPanel with rounded corners and optional shadow effect.
 * Used as base component for cards and containers.
 */
public class RoundedPanel extends JPanel {
    
    private int radius;
    private Color shadowColor;
    private boolean hasShadow;
    private int shadowOffset;
    
    /**
     * Creates a rounded panel with default radius and no shadow.
     */
    public RoundedPanel() {
        this(UIConstants.RADIUS_LG, false);
    }
    
    /**
     * Creates a rounded panel with specified radius and shadow option.
     * @param radius The corner radius
     * @param hasShadow Whether to display shadow effect
     */
    public RoundedPanel(int radius, boolean hasShadow) {
        this.radius = radius;
        this.hasShadow = hasShadow;
        this.shadowColor = new Color(0, 0, 0, 20);
        this.shadowOffset = 3;
        
        setOpaque(false);
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int offset = hasShadow ? shadowOffset : 0;
        int width = getWidth() - offset;
        int height = getHeight() - offset;
        
        // Draw shadow
        if (hasShadow) {
            g2.setColor(shadowColor);
            g2.fillRoundRect(offset, offset, width, height, radius, radius);
        }
        
        // Draw background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Sets the corner radius.
     * @param radius The corner radius
     */
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }
    
    /**
     * Gets the corner radius.
     * @return The corner radius
     */
    public int getRadius() {
        return radius;
    }
    
    /**
     * Sets whether shadow is displayed.
     * @param hasShadow True to show shadow
     */
    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        repaint();
    }
    
    /**
     * Gets whether shadow is displayed.
     * @return True if shadow is shown
     */
    public boolean isHasShadow() {
        return hasShadow;
    }
    
    /**
     * Sets the shadow color.
     * @param shadowColor The shadow color
     */
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        repaint();
    }
    
    /**
     * Gets the shadow color.
     * @return The shadow color
     */
    public Color getShadowColor() {
        return shadowColor;
    }
    
    /**
     * Sets the shadow offset.
     * @param shadowOffset The shadow offset in pixels
     */
    public void setShadowOffset(int shadowOffset) {
        this.shadowOffset = shadowOffset;
        repaint();
    }
    
    /**
     * Gets the shadow offset.
     * @return The shadow offset in pixels
     */
    public int getShadowOffset() {
        return shadowOffset;
    }
}
