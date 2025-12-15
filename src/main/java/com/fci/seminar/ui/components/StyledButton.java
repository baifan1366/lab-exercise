package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Custom styled button with hover and press state effects.
 * Supports PRIMARY, SECONDARY, DANGER, and SUCCESS button types.
 */
public class StyledButton extends JButton {
    
    /**
     * Button type enumeration.
     */
    public enum ButtonType {
        PRIMARY, SECONDARY, DANGER, SUCCESS
    }
    
    private ButtonType type;
    private Color bgColor;
    private Color hoverColor;
    private Color pressColor;
    private int radius;
    
    /**
     * Creates a styled button with specified text and type.
     * @param text The button text
     * @param type The button type
     */
    public StyledButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        this.radius = UIConstants.RADIUS_MD;
        initStyle();
    }
    
    /**
     * Creates a primary styled button.
     * @param text The button text
     */
    public StyledButton(String text) {
        this(text, ButtonType.PRIMARY);
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set preferred size with padding
        Dimension size = getPreferredSize();
        setPreferredSize(new Dimension(size.width + 32, UIConstants.BUTTON_HEIGHT));
        
        // Configure colors based on type
        configureColors();
        
        // Add mouse listener for hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }
    
    private void configureColors() {
        switch (type) {
            case PRIMARY:
                bgColor = UIConstants.PRIMARY;
                hoverColor = UIConstants.PRIMARY_LIGHT;
                pressColor = UIConstants.PRIMARY_DARK;
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                bgColor = Color.WHITE;
                hoverColor = new Color(248, 249, 252);
                pressColor = new Color(233, 236, 239);
                setForeground(UIConstants.PRIMARY);
                break;
            case DANGER:
                bgColor = UIConstants.DANGER;
                hoverColor = new Color(200, 35, 51);
                pressColor = new Color(180, 25, 41);
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                bgColor = UIConstants.SUCCESS;
                hoverColor = new Color(33, 136, 56);
                pressColor = new Color(25, 105, 43);
                setForeground(Color.WHITE);
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine background color based on state
        Color bg = bgColor;
        if (getModel().isPressed()) {
            bg = pressColor;
        } else if (getModel().isRollover()) {
            bg = hoverColor;
        }
        
        // Draw background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Draw border for SECONDARY type
        if (type == ButtonType.SECONDARY) {
            g2.setColor(UIConstants.PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Sets the button type and updates colors.
     * @param type The button type
     */
    public void setButtonType(ButtonType type) {
        this.type = type;
        configureColors();
        repaint();
    }
    
    /**
     * Gets the button type.
     * @return The button type
     */
    public ButtonType getButtonType() {
        return type;
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
    
    // Factory methods for convenience
    
    /**
     * Creates a primary button.
     * @param text The button text
     * @return A primary styled button
     */
    public static StyledButton primary(String text) {
        return new StyledButton(text, ButtonType.PRIMARY);
    }
    
    /**
     * Creates a secondary button.
     * @param text The button text
     * @return A secondary styled button
     */
    public static StyledButton secondary(String text) {
        return new StyledButton(text, ButtonType.SECONDARY);
    }
    
    /**
     * Creates a danger button.
     * @param text The button text
     * @return A danger styled button
     */
    public static StyledButton danger(String text) {
        return new StyledButton(text, ButtonType.DANGER);
    }
    
    /**
     * Creates a success button.
     * @param text The button text
     * @return A success styled button
     */
    public static StyledButton success(String text) {
        return new StyledButton(text, ButtonType.SUCCESS);
    }
}
