package com.fci.seminar.ui.components;

import com.fci.seminar.model.enums.Role;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Status bar component for the main application window.
 * Displays current role and copyright information.
 * Requirements: 10.1
 */
public class StatusBar extends JPanel {
    
    private JLabel roleLabel;
    private JLabel copyrightLabel;
    
    /**
     * Creates a new status bar.
     */
    public StatusBar() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);
        setPreferredSize(new Dimension(0, UIConstants.STATUSBAR_HEIGHT));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER),
            BorderFactory.createEmptyBorder(0, UIConstants.SPACING_MD, 0, UIConstants.SPACING_MD)
        ));
        
        // Left side - Role display
        roleLabel = new JLabel();
        roleLabel.setFont(UIConstants.SMALL);
        roleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        add(roleLabel, BorderLayout.WEST);
        
        // Right side - Copyright
        copyrightLabel = new JLabel("\u00A9 2024 FCI Seminar Management System");
        copyrightLabel.setFont(UIConstants.SMALL);
        copyrightLabel.setForeground(UIConstants.TEXT_MUTED);
        add(copyrightLabel, BorderLayout.EAST);
        
        // Update role display
        updateRoleDisplay();
    }
    
    /**
     * Updates the role display based on current authentication state.
     */
    public void updateRoleDisplay() {
        AuthService authService = AuthService.getInstance();
        Role currentRole = authService.getCurrentRole();
        roleLabel.setText("Current Role: " + formatRole(currentRole));
    }
    
    /**
     * Updates the role display with a specific role.
     * @param role The role to display
     */
    public void updateRoleDisplay(Role role) {
        roleLabel.setText("Current Role: " + formatRole(role));
    }
    
    private String formatRole(Role role) {
        if (role == null) return "Guest";
        switch (role) {
            case COORDINATOR: return "Coordinator";
            case EVALUATOR: return "Evaluator";
            case STUDENT: return "Student";
            case GUEST:
            default: return "Guest";
        }
    }
    
    /**
     * Sets the copyright text.
     * @param text The copyright text
     */
    public void setCopyrightText(String text) {
        copyrightLabel.setText(text);
    }
    
    /**
     * Gets the copyright text.
     * @return The copyright text
     */
    public String getCopyrightText() {
        return copyrightLabel.getText();
    }
    
    /**
     * Sets a custom status message on the left side.
     * @param message The status message
     */
    public void setStatusMessage(String message) {
        roleLabel.setText(message);
    }
}
