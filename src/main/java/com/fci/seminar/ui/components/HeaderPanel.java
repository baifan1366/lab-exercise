package com.fci.seminar.ui.components;

import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.Role;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Header panel component for the main application window.
 * Displays logo, title, user info, and login/logout button.
 * Requirements: 10.1
 */
public class HeaderPanel extends JPanel {
    
    private JLabel logoLabel;
    private JLabel titleLabel;
    private JLabel userInfoLabel;
    private StyledButton loginLogoutButton;
    
    private ActionListener loginActionListener;
    private ActionListener logoutActionListener;
    
    /**
     * Creates a new header panel.
     */
    public HeaderPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_HEADER);
        setPreferredSize(new Dimension(0, UIConstants.HEADER_HEIGHT));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        
        // Left section - Logo and Title
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Right section - User info and Login/Logout button
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, 0));
        panel.setOpaque(false);
        
        // Logo icon (using text as placeholder)
        logoLabel = new JLabel("\uD83C\uDF93"); // Graduation cap emoji
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, UIConstants.SPACING_MD, 0, 0));
        panel.add(logoLabel);
        
        // Title
        titleLabel = new JLabel("Seminar Management System");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.PRIMARY);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_MD, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIConstants.SPACING_MD));
        
        // User info label
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(UIConstants.BODY);
        userInfoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(userInfoLabel);
        
        // Login/Logout button
        loginLogoutButton = new StyledButton("Login", StyledButton.ButtonType.PRIMARY);
        loginLogoutButton.setPreferredSize(new Dimension(100, 36));
        loginLogoutButton.addActionListener(e -> handleLoginLogoutClick());
        panel.add(loginLogoutButton);
        
        // Update display based on current auth state
        updateUserDisplay();
        
        return panel;
    }
    
    private void handleLoginLogoutClick() {
        AuthService authService = AuthService.getInstance();
        if (authService.isAuthenticated()) {
            // Logout action
            if (logoutActionListener != null) {
                logoutActionListener.actionPerformed(null);
            }
        } else {
            // Login action
            if (loginActionListener != null) {
                loginActionListener.actionPerformed(null);
            }
        }
    }
    
    /**
     * Updates the user display based on current authentication state.
     */
    public void updateUserDisplay() {
        AuthService authService = AuthService.getInstance();
        User currentUser = authService.getCurrentUser();
        Role currentRole = authService.getCurrentRole();
        
        if (authService.isAuthenticated() && currentUser != null) {
            userInfoLabel.setText(currentUser.getName() + " (" + formatRole(currentRole) + ")");
            loginLogoutButton.setText("Logout");
            loginLogoutButton.setButtonType(StyledButton.ButtonType.SECONDARY);
        } else {
            userInfoLabel.setText("Welcome, Guest");
            loginLogoutButton.setText("Login");
            loginLogoutButton.setButtonType(StyledButton.ButtonType.PRIMARY);
        }
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
     * Sets the action listener for login button click.
     * @param listener The action listener
     */
    public void setLoginActionListener(ActionListener listener) {
        this.loginActionListener = listener;
    }
    
    /**
     * Sets the action listener for logout button click.
     * @param listener The action listener
     */
    public void setLogoutActionListener(ActionListener listener) {
        this.logoutActionListener = listener;
    }
    
    /**
     * Sets the application title.
     * @param title The title text
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    /**
     * Gets the application title.
     * @return The title text
     */
    public String getTitle() {
        return titleLabel.getText();
    }
}
