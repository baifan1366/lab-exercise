package com.fci.seminar.ui.dialogs;

import com.fci.seminar.model.enums.Role;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.ui.components.RoundedBorder;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTextField;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modal login dialog with role selection, username and password fields.
 * Handles authentication and displays error messages for invalid credentials.
 * Requirements: 1.2, 1.3, 1.4
 */
public class LoginDialog extends JDialog {
    
    private JComboBox<Role> roleComboBox;
    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private StyledButton loginButton;
    private StyledButton cancelButton;
    private JLabel errorLabel;
    
    private boolean loginSuccessful;
    
    /**
     * Creates a login dialog.
     * @param parent The parent frame
     */
    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        this.loginSuccessful = false;
        initDialog();
        initComponents();
        setupEventHandlers();
    }
    
    private void initDialog() {
        setSize(400, 380);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_XL,
            UIConstants.SPACING_LG, UIConstants.SPACING_XL
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_LG, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Role selection
        formPanel.add(createFieldPanel("Role", createRoleComboBox()));
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Username field
        usernameField = new StyledTextField("Enter username");
        formPanel.add(createFieldPanel("Username", usernameField));
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Password field
        passwordField = createPasswordField();
        formPanel.add(createFieldPanel("Password", passwordField));
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.SMALL);
        errorLabel.setForeground(UIConstants.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }

    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_XS, 0));
        panel.add(label);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        panel.add(field);
        
        return panel;
    }
    
    private JComboBox<Role> createRoleComboBox() {
        // Only allow login for non-guest roles
        Role[] loginRoles = {Role.STUDENT, Role.EVALUATOR, Role.COORDINATOR};
        roleComboBox = new JComboBox<>(loginRoles);
        roleComboBox.setFont(UIConstants.BODY);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        
        // Custom renderer for role display
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Role) {
                    setText(formatRoleName((Role) value));
                }
                return this;
            }
        });
        
        return roleComboBox;
    }
    
    private String formatRoleName(Role role) {
        String name = role.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UIConstants.BODY);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(UIConstants.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        
        // Add focus listener for border color change
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(UIConstants.PRIMARY_LIGHT, UIConstants.RADIUS_SM),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, 0, 0, 0));
        
        cancelButton = StyledButton.secondary("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        panel.add(cancelButton);
        
        loginButton = StyledButton.primary("Login");
        loginButton.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        panel.add(loginButton);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Login button click
        loginButton.addActionListener(this::handleLogin);
        
        // Cancel button click
        cancelButton.addActionListener(e -> {
            loginSuccessful = false;
            dispose();
        });
        
        // Enter key in password field triggers login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin(null);
                }
            }
        });
        
        // Enter key in username field moves to password
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        
        // Escape key closes dialog
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void handleLogin(ActionEvent e) {
        // Clear previous error
        errorLabel.setText(" ");
        
        // Get input values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem();
        
        // Validate input
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Attempt authentication
        boolean success = AuthService.getInstance().login(username, password, role);
        
        if (success) {
            loginSuccessful = true;
            dispose();
        } else {
            showError("Invalid username or password");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    /**
     * Checks if login was successful.
     * @return true if login succeeded
     */
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    /**
     * Gets the selected role.
     * @return the selected role
     */
    public Role getSelectedRole() {
        return (Role) roleComboBox.getSelectedItem();
    }
    
    /**
     * Shows the login dialog and returns whether login was successful.
     * @param parent The parent frame
     * @return true if login succeeded
     */
    public static boolean showDialog(Frame parent) {
        LoginDialog dialog = new LoginDialog(parent);
        dialog.setVisible(true);
        return dialog.isLoginSuccessful();
    }
}
