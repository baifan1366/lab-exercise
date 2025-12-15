package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import javax.swing.*;

/**
 * Custom label with colored background for displaying status information.
 * Provides factory methods for common status types (success, warning, danger, info).
 */
public class StatusLabel extends JLabel {
    
    private Color bgColor;
    private int radius;
    
    /**
     * Creates a status label with specified text and background color.
     * @param text The label text
     * @param bgColor The background color
     */
    public StatusLabel(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        this.radius = 12;
        initStyle();
    }
    
    private void initStyle() {
        setOpaque(false);
        setFont(UIConstants.SMALL);
        setForeground(Color.WHITE);
        setHorizontalAlignment(CENTER);
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Sets the background color.
     * @param bgColor The background color
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        repaint();
    }
    
    /**
     * Gets the background color.
     * @return The background color
     */
    public Color getBgColor() {
        return bgColor;
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
    
    // Factory methods for common status types
    
    /**
     * Creates a success status label (green).
     * @param text The label text
     * @return A success status label
     */
    public static StatusLabel success(String text) {
        return new StatusLabel(text, UIConstants.SUCCESS);
    }
    
    /**
     * Creates a warning status label (yellow).
     * @param text The label text
     * @return A warning status label
     */
    public static StatusLabel warning(String text) {
        StatusLabel label = new StatusLabel(text, UIConstants.WARNING);
        label.setForeground(UIConstants.TEXT_PRIMARY); // Dark text for yellow background
        return label;
    }
    
    /**
     * Creates a danger status label (red).
     * @param text The label text
     * @return A danger status label
     */
    public static StatusLabel danger(String text) {
        return new StatusLabel(text, UIConstants.DANGER);
    }
    
    /**
     * Creates an info status label (blue).
     * @param text The label text
     * @return An info status label
     */
    public static StatusLabel info(String text) {
        return new StatusLabel(text, UIConstants.INFO);
    }
    
    /**
     * Creates a status label for session status.
     * @param status The session status string
     * @return A status label with appropriate color
     */
    public static StatusLabel forSessionStatus(String status) {
        if (status == null) {
            return info("Unknown");
        }
        switch (status.toUpperCase()) {
            case "OPEN":
                return success("Open");
            case "FULL":
                return danger("Full");
            case "CLOSED":
                return new StatusLabel("Closed", UIConstants.TEXT_SECONDARY);
            case "REQUIRES_APPROVAL":
                return warning("Requires Approval");
            default:
                return info(status);
        }
    }
    
    /**
     * Creates a status label for registration status.
     * @param status The registration status string
     * @return A status label with appropriate color
     */
    public static StatusLabel forRegistrationStatus(String status) {
        if (status == null) {
            return info("Unknown");
        }
        switch (status.toUpperCase()) {
            case "PENDING":
                return warning("Pending");
            case "APPROVED":
                return success("Approved");
            case "REJECTED":
                return danger("Rejected");
            case "CANCELLED":
                return new StatusLabel("Cancelled", UIConstants.TEXT_SECONDARY);
            default:
                return info(status);
        }
    }
}
