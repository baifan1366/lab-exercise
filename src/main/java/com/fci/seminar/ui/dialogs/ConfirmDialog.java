package com.fci.seminar.ui.dialogs;

import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Reusable confirmation dialog with custom title and message.
 * Returns user choice (confirm or cancel).
 * Requirements: 6.5
 */
public class ConfirmDialog extends JDialog {
    
    /**
     * Dialog type enumeration for different visual styles.
     */
    public enum DialogType {
        INFO, WARNING, DANGER, QUESTION
    }
    
    private boolean confirmed;
    private DialogType dialogType;
    
    /**
     * Creates a confirmation dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @param type The dialog type
     */
    public ConfirmDialog(Frame parent, String title, String message, DialogType type) {
        super(parent, title, true);
        this.confirmed = false;
        this.dialogType = type;
        initDialog();
        initComponents(message);
    }
    
    /**
     * Creates a confirmation dialog with default QUESTION type.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public ConfirmDialog(Frame parent, String title, String message) {
        this(parent, title, message, DialogType.QUESTION);
    }
    
    private void initDialog() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents(String message) {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_XL,
            UIConstants.SPACING_LG, UIConstants.SPACING_XL
        ));
        
        // Content panel with icon and message
        JPanel contentPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        contentPanel.setOpaque(false);
        
        // Icon
        JLabel iconLabel = createIconLabel();
        contentPanel.add(iconLabel, BorderLayout.WEST);
        
        // Message
        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setFont(UIConstants.BODY);
        messageLabel.setForeground(UIConstants.TEXT_PRIMARY);
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Escape key closes dialog
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private JLabel createIconLabel() {
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(48, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Use Unicode symbols as icons
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        
        switch (dialogType) {
            case INFO:
                iconLabel.setText("\u2139"); // Information symbol
                iconLabel.setForeground(UIConstants.INFO);
                break;
            case WARNING:
                iconLabel.setText("\u26A0"); // Warning symbol
                iconLabel.setForeground(UIConstants.WARNING);
                break;
            case DANGER:
                iconLabel.setText("\u26D4"); // No entry symbol
                iconLabel.setForeground(UIConstants.DANGER);
                break;
            case QUESTION:
            default:
                iconLabel.setText("?");
                iconLabel.setForeground(UIConstants.PRIMARY);
                break;
        }
        
        return iconLabel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_LG, 0, 0, 0));
        
        StyledButton cancelButton = StyledButton.secondary("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        panel.add(cancelButton);
        
        // Confirm button style based on dialog type
        StyledButton confirmButton;
        if (dialogType == DialogType.DANGER) {
            confirmButton = StyledButton.danger("Confirm");
        } else {
            confirmButton = StyledButton.primary("Confirm");
        }
        confirmButton.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        panel.add(confirmButton);
        
        // Set default button
        getRootPane().setDefaultButton(confirmButton);
        
        return panel;
    }
    
    /**
     * Checks if the user confirmed the action.
     * @return true if confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * Shows a confirmation dialog and returns the user's choice.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @return true if confirmed
     */
    public static boolean showDialog(Frame parent, String title, String message) {
        return showDialog(parent, title, message, DialogType.QUESTION);
    }
    
    /**
     * Shows a confirmation dialog with specified type and returns the user's choice.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @param type The dialog type
     * @return true if confirmed
     */
    public static boolean showDialog(Frame parent, String title, String message, DialogType type) {
        ConfirmDialog dialog = new ConfirmDialog(parent, title, message, type);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
    
    /**
     * Shows a warning confirmation dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @return true if confirmed
     */
    public static boolean showWarning(Frame parent, String title, String message) {
        return showDialog(parent, title, message, DialogType.WARNING);
    }
    
    /**
     * Shows a danger confirmation dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @return true if confirmed
     */
    public static boolean showDanger(Frame parent, String title, String message) {
        return showDialog(parent, title, message, DialogType.DANGER);
    }
}
