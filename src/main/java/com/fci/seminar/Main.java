package com.fci.seminar;

import com.fci.seminar.model.enums.Role;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.dialogs.LoginDialog;
import com.fci.seminar.ui.panels.coordinator.*;
import com.fci.seminar.ui.panels.evaluator.*;
import com.fci.seminar.ui.panels.guest.*;
import com.fci.seminar.ui.panels.student.*;
import com.fci.seminar.util.DataManager;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Seminar Management System.
 * FCI Postgraduate Academic Research Seminar Management System.
 * 
 * Initializes the application by:
 * - Setting up the look and feel
 * - Loading data from JSON files
 * - Creating and displaying the main frame
 * - Handling application shutdown
 * 
 * Requirements: 1.1, 11.2
 */
public class Main {
    
    private static MainFrame mainFrame;
    private static DataManager dataManager;
    
    public static void main(String[] args) {
        // Set system look and feel with custom UI defaults
        setupLookAndFeel();
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize DataManager and load data
                initializeData();
                
                // Create and display MainFrame
                createAndShowMainFrame();
                
                // Setup shutdown hook for data persistence
                setupShutdownHook();
                
                System.out.println("Seminar Management System started successfully.");
            } catch (Exception e) {
                handleStartupError(e);
            }
        });
    }
    
    /**
     * Sets up the look and feel with custom UI defaults.
     */
    private static void setupLookAndFeel() {
        try {
            // Use system look and feel as base
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Apply custom UI defaults for consistent styling
            UIManager.put("Button.font", UIConstants.BODY);
            UIManager.put("Label.font", UIConstants.BODY);
            UIManager.put("TextField.font", UIConstants.BODY);
            UIManager.put("TextArea.font", UIConstants.BODY);
            UIManager.put("ComboBox.font", UIConstants.BODY);
            UIManager.put("Table.font", UIConstants.BODY);
            UIManager.put("TableHeader.font", UIConstants.BODY_BOLD);
            UIManager.put("TitledBorder.font", UIConstants.BODY_BOLD);
            
            // Set default colors
            UIManager.put("Panel.background", UIConstants.BG_MAIN);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageFont", UIConstants.BODY);
            UIManager.put("OptionPane.buttonFont", UIConstants.BODY);
            
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
    }
    
    /**
     * Initializes the DataManager and loads all data from JSON files.
     * Requirements: 11.2
     */
    private static void initializeData() {
        System.out.println("Loading data from JSON files...");
        dataManager = DataManager.getInstance();
        dataManager.loadAllData();
        System.out.println("Data loaded successfully.");
    }
    
    /**
     * Creates and displays the main application frame.
     * Requirements: 1.1
     */
    private static void createAndShowMainFrame() {
        mainFrame = new MainFrame();
        
        // Set up menu action handler for panel navigation
        mainFrame.setMenuActionHandler(Main::handleMenuAction);
        
        // Set up role change listener
        mainFrame.setRoleChangeListener(Main::handleRoleChange);
        
        // Display the frame
        mainFrame.setVisible(true);
        
        // Start in Guest mode with schedule view
        mainFrame.updateForRole(Role.GUEST);
    }
    
    /**
     * Handles menu action commands from the sidebar.
     * @param actionCommand The action command string
     */
    private static void handleMenuAction(String actionCommand) {
        switch (actionCommand) {
            // Login/Logout
            case "LOGIN":
                showLoginDialog();
                break;
            case "LOGOUT":
                mainFrame.handleLogout();
                break;
                
            // Guest panels
            case "SCHEDULE":
                mainFrame.switchToPanel("SCHEDULE", ScheduleViewPanel::new);
                break;
                
            // Student panels
            case "REGISTRATION":
                mainFrame.switchToPanel("REGISTRATION", RegistrationPanel::new);
                break;
            case "UPLOAD":
                mainFrame.switchToPanel("UPLOAD", UploadPanel::new);
                break;
            case "STATUS":
                mainFrame.switchToPanel("STATUS", StatusPanel::new);
                break;
                
            // Evaluator panels
            case "ASSIGNED_LIST":
                mainFrame.switchToPanel("ASSIGNED_LIST", () -> {
                    AssignedListPanel panel = new AssignedListPanel();
                    panel.setNavigationHandler(evaluationId -> {
                        // Navigate to evaluation panel with the specific evaluation
                        mainFrame.switchToPanel("EVALUATION", () -> {
                            EvaluationPanel evalPanel = new EvaluationPanel();
                            evalPanel.setBackHandler(() -> handleMenuAction("ASSIGNED_LIST"));
                            evalPanel.loadEvaluation(evaluationId);
                            return evalPanel;
                        });
                    });
                    return panel;
                });
                break;
            case "EVALUATION":
                mainFrame.switchToPanel("EVALUATION", () -> {
                    EvaluationPanel panel = new EvaluationPanel();
                    panel.setBackHandler(() -> handleMenuAction("ASSIGNED_LIST"));
                    return panel;
                });
                break;
                
            // Coordinator panels
            case "DASHBOARD":
                mainFrame.switchToPanel("DASHBOARD", () -> {
                    DashboardPanel panel = new DashboardPanel();
                    panel.setQuickActionHandler(Main::handleQuickAction);
                    return panel;
                });
                break;
            case "USER_MANAGEMENT":
                mainFrame.switchToPanel("USER_MANAGEMENT", UserManagementPanel::new);
                break;
            case "SESSION_MANAGEMENT":
                mainFrame.switchToPanel("SESSION_MANAGEMENT", SessionManagementPanel::new);
                break;
            case "REGISTRATION_MANAGEMENT":
                // Registration management is handled through Assignment panel
                mainFrame.switchToPanel("ASSIGNMENT", AssignmentPanel::new);
                break;
            case "ASSIGNMENTS":
                mainFrame.switchToPanel("ASSIGNMENT", AssignmentPanel::new);
                break;
            case "ASSIGNMENT":
                mainFrame.switchToPanel("ASSIGNMENT", AssignmentPanel::new);
                break;
            case "EVALUATIONS_VIEW":
                // Evaluations view - show evaluation management panel
                mainFrame.switchToPanel("EVALUATIONS_VIEW", () -> {
                    // Create a panel to view all evaluations
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setOpaque(false);
                    JLabel label = new JLabel("Evaluation Management - Coming Soon");
                    label.setFont(UIConstants.TITLE_MEDIUM);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(label, BorderLayout.CENTER);
                    return panel;
                });
                break;
            case "AWARDS":
                mainFrame.switchToPanel("AWARDS", AwardPanel::new);
                break;
            case "REPORTS":
                mainFrame.switchToPanel("REPORTS", ReportPanel::new);
                break;
            case "SETTINGS":
                // Settings panel - placeholder
                mainFrame.switchToPanel("SETTINGS", () -> {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setOpaque(false);
                    JLabel label = new JLabel("Settings - Coming Soon");
                    label.setFont(UIConstants.TITLE_MEDIUM);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(label, BorderLayout.CENTER);
                    return panel;
                });
                break;
            case "PROFILE":
                // Profile panel for all logged-in users
                mainFrame.switchToPanel("PROFILE", () -> {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setOpaque(false);
                    JLabel label = new JLabel("User Profile - Coming Soon");
                    label.setFont(UIConstants.TITLE_MEDIUM);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(label, BorderLayout.CENTER);
                    return panel;
                });
                break;
                
            default:
                System.out.println("Unknown action: " + actionCommand);
        }
    }
    
    /**
     * Handles quick action commands from the dashboard.
     * @param action The quick action string
     */
    private static void handleQuickAction(String action) {
        switch (action) {
            case "CREATE_SESSION":
                handleMenuAction("SESSION_MANAGEMENT");
                break;
            case "MANAGE_REGISTRATIONS":
                handleMenuAction("ASSIGNMENT");
                break;
            case "ASSIGN_EVALUATORS":
                handleMenuAction("ASSIGNMENT");
                break;
            case "CALCULATE_AWARDS":
                handleMenuAction("AWARDS");
                break;
            case "GENERATE_REPORTS":
                handleMenuAction("REPORTS");
                break;
            default:
                System.out.println("Unknown quick action: " + action);
        }
    }
    
    /**
     * Shows the login dialog and handles authentication.
     */
    private static void showLoginDialog() {
        boolean success = LoginDialog.showDialog(mainFrame);
        if (success) {
            Role currentRole = AuthService.getInstance().getCurrentRole();
            mainFrame.updateForRole(currentRole);
        }
    }
    
    /**
     * Handles role change events.
     * @param newRole The new role after change
     */
    private static void handleRoleChange(Role newRole) {
        // Navigate to default panel for the new role
        switch (newRole) {
            case COORDINATOR:
                handleMenuAction("DASHBOARD");
                break;
            case EVALUATOR:
                handleMenuAction("ASSIGNED_LIST");
                break;
            case STUDENT:
                handleMenuAction("REGISTRATION");
                break;
            case GUEST:
            default:
                handleMenuAction("SCHEDULE");
                break;
        }
    }
    
    /**
     * Sets up a shutdown hook to save data when the application closes.
     */
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving data before shutdown...");
            if (dataManager != null) {
                dataManager.saveAllData();
            }
            System.out.println("Data saved. Goodbye!");
        }));
        
        // Also save data when window is closed
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleApplicationExit();
            }
        });
    }
    
    /**
     * Handles application exit with confirmation and data saving.
     */
    private static void handleApplicationExit() {
        int result = JOptionPane.showConfirmDialog(
            mainFrame,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Save all data before exit
            if (dataManager != null) {
                dataManager.saveAllData();
            }
            mainFrame.dispose();
            System.exit(0);
        }
    }
    
    /**
     * Handles startup errors by showing an error dialog.
     * @param e The exception that occurred
     */
    private static void handleStartupError(Exception e) {
        System.err.println("Failed to start application: " + e.getMessage());
        e.printStackTrace();
        
        JOptionPane.showMessageDialog(
            null,
            "Failed to start the application:\n" + e.getMessage() + 
            "\n\nPlease check the data files and try again.",
            "Startup Error",
            JOptionPane.ERROR_MESSAGE
        );
        
        System.exit(1);
    }
    
    /**
     * Gets the main frame instance.
     * @return The main frame
     */
    public static MainFrame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * Gets the data manager instance.
     * @return The data manager
     */
    public static DataManager getDataManager() {
        return dataManager;
    }
}
