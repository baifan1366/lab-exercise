package com.fci.seminar.ui.dialogs;

import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Dialog for displaying info, warning, and error messages.
 * Supports different icon types based on message severity.
 * Requirements: 1.4, 4.4
 */
public class MessageDialog extends JDialog {
    
    /**
     * Message type enumeration for different visual styles.
     */
    public enum MessageType {
        INFO, SUCCESS, WARNING, ERROR
    }
    
    private MessageType messageType;
    
    /**
     * Creates a message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @param type The message type
     */
    public MessageDialog(Frame parent, String title, String message, MessageType type) {
        super(parent, title, true);
        this.messageType = type;
        initDialog();
        initComponents(message);
    }
    
    /**
     * Creates an info message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public MessageDialog(Frame parent, String title, String message) {
        this(parent, title, message, MessageType.INFO);
    }
    
    private void initDialog() {
        setSize(400, 180);
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
        
        // Enter or Escape key closes dialog
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
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
        
        switch (messageType) {
            case SUCCESS:
                iconLabel.setText("\u2714"); // Check mark
                iconLabel.setForeground(UIConstants.SUCCESS);
                break;
            case WARNING:
                iconLabel.setText("\u26A0"); // Warning symbol
                iconLabel.setForeground(UIConstants.WARNING);
                break;
            case ERROR:
                iconLabel.setText("\u2716"); // X mark
                iconLabel.setForeground(UIConstants.DANGER);
                break;
            case INFO:
            default:
                iconLabel.setText("\u2139"); // Information symbol
                iconLabel.setForeground(UIConstants.INFO);
                break;
        }
        
        return iconLabel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_LG, 0, 0, 0));
        
        StyledButton okButton = StyledButton.primary("OK");
        okButton.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        okButton.addActionListener(e -> dispose());
        panel.add(okButton);
        
        // Set default button
        getRootPane().setDefaultButton(okButton);
        
        return panel;
    }
    
    /**
     * Shows an info message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public static void showInfo(Frame parent, String title, String message) {
        MessageDialog dialog = new MessageDialog(parent, title, message, MessageType.INFO);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a success message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public static void showSuccess(Frame parent, String title, String message) {
        MessageDialog dialog = new MessageDialog(parent, title, message, MessageType.SUCCESS);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a warning message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public static void showWarning(Frame parent, String title, String message) {
        MessageDialog dialog = new MessageDialog(parent, title, message, MessageType.WARNING);
        dialog.setVisible(true);
    }
    
    /**
     * Shows an error message dialog.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     */
    public static void showError(Frame parent, String title, String message) {
        MessageDialog dialog = new MessageDialog(parent, title, message, MessageType.ERROR);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a message dialog with specified type.
     * @param parent The parent frame
     * @param title The dialog title
     * @param message The message to display
     * @param type The message type
     */
    public static void show(Frame parent, String title, String message, MessageType type) {
        MessageDialog dialog = new MessageDialog(parent, title, message, type);
        dialog.setVisible(true);
    }
    
    /**
     * Shows an info message dialog with default title.
     * @param parent The parent frame
     * @param message The message to display
     */
    public static void showInfo(Frame parent, String message) {
        showInfo(parent, "Information", message);
    }
    
    /**
     * Shows a success message dialog with default title.
     * @param parent The parent frame
     * @param message The message to display
     */
    public static void showSuccess(Frame parent, String message) {
        showSuccess(parent, "Success", message);
    }
    
    /**
     * Shows a warning message dialog with default title.
     * @param parent The parent frame
     * @param message The message to display
     */
    public static void showWarning(Frame parent, String message) {
        showWarning(parent, "Warning", message);
    }
    
    /**
     * Shows an error message dialog with default title.
     * @param parent The parent frame
     * @param message The message to display
     */
    public static void showError(Frame parent, String message) {
        showError(parent, "Error", message);
    }
}
