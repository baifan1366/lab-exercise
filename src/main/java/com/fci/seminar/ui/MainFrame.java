package com.fci.seminar.ui;

import com.fci.seminar.model.enums.Role;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.ui.components.HeaderPanel;
import com.fci.seminar.ui.components.SidebarPanel;
import com.fci.seminar.ui.components.StatusBar;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application frame with BorderLayout.
 * Integrates header, sidebar, content area, and status bar.
 * Handles panel switching and role changes.
 * Requirements: 10.1, 1.1
 */
public class MainFrame extends JFrame {
    
    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;
    private JPanel contentPanel;
    private StatusBar statusBar;
    
    private JPanel currentPanel;
    private Map<String, JPanel> panelCache;
    private RoleChangeListener roleChangeListener;
    private MenuActionHandler menuActionHandler;
    
    /**
     * Creates the main application frame.
     */
    public MainFrame() {
        this.panelCache = new HashMap<>();
        initFrame();
        initComponents();
        setupEventHandlers();
    }
    
    private void initFrame() {
        setTitle("Seminar Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        
        // Set application icon (if available)
        try {
            // Placeholder for icon loading
        } catch (Exception e) {
            // Ignore icon loading errors
        }
    }
    
    private void initComponents() {
        // Main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIConstants.BG_MAIN);
        
        // Header panel (NORTH)
        headerPanel = new HeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Sidebar panel (WEST)
        sidebarPanel = new SidebarPanel();
        mainContainer.add(sidebarPanel, BorderLayout.WEST);
        
        // Content panel (CENTER)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        // Default welcome panel
        showWelcomePanel();
        
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        // Status bar (SOUTH)
        statusBar = new StatusBar();
        mainContainer.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(mainContainer);
    }
    
    private void setupEventHandlers() {
        // Header login/logout handlers
        headerPanel.setLoginActionListener(e -> showLoginDialog());
        headerPanel.setLogoutActionListener(e -> handleLogout());
        
        // Sidebar menu click handler
        sidebarPanel.setMenuClickHandler(this::handleMenuClick);
    }
    
    private void showWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setOpaque(false);
        
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome to Seminar Management System");
        welcomeLabel.setFont(UIConstants.TITLE_LARGE);
        welcomeLabel.setForeground(UIConstants.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(welcomeLabel);
        
        centerContent.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        JLabel subtitleLabel = new JLabel("Select an option from the menu to get started");
        subtitleLabel.setFont(UIConstants.BODY);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(subtitleLabel);
        
        welcomePanel.add(centerContent);
        switchPanel(welcomePanel);
    }
    
    /**
     * Shows the login dialog.
     */
    public void showLoginDialog() {
        if (menuActionHandler != null) {
            menuActionHandler.onMenuAction("LOGIN");
        }
    }
    
    /**
     * Handles user logout.
     */
    public void handleLogout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            updateForRole(Role.GUEST);
            showWelcomePanel();
        }
    }
    
    private void handleMenuClick(String actionCommand) {
        if ("LOGIN".equals(actionCommand)) {
            showLoginDialog();
            return;
        }
        
        if ("LOGOUT".equals(actionCommand)) {
            handleLogout();
            return;
        }
        
        // Delegate to menu action handler
        if (menuActionHandler != null) {
            menuActionHandler.onMenuAction(actionCommand);
        }
    }
    
    /**
     * Switches the content panel to a new panel.
     * @param panel The panel to display
     */
    public void switchPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        currentPanel = panel;
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Switches to a cached panel by key, creating it if necessary.
     * @param key The panel key
     * @param panelSupplier Supplier to create the panel if not cached
     */
    public void switchToPanel(String key, java.util.function.Supplier<JPanel> panelSupplier) {
        JPanel panel = panelCache.computeIfAbsent(key, k -> panelSupplier.get());
        switchPanel(panel);
    }
    
    /**
     * Updates the UI for a role change.
     * @param role The new role
     */
    public void updateForRole(Role role) {
        // Update sidebar menu
        sidebarPanel.updateMenuForRole(role);
        
        // Update header display
        headerPanel.updateUserDisplay();
        
        // Update status bar
        statusBar.updateRoleDisplay(role);
        
        // Clear panel cache on role change
        panelCache.clear();
        
        // Notify listener
        if (roleChangeListener != null) {
            roleChangeListener.onRoleChanged(role);
        }
        
        // Select default menu item based on role
        String defaultMenuItem = getDefaultMenuItem(role);
        sidebarPanel.selectMenuItem(defaultMenuItem);
    }
    
    private String getDefaultMenuItem(Role role) {
        switch (role) {
            case COORDINATOR:
                return "DASHBOARD";
            case EVALUATOR:
                return "ASSIGNED_LIST";
            case STUDENT:
                return "REGISTRATION";
            case GUEST:
            default:
                return "SCHEDULE";
        }
    }
    
    /**
     * Gets the header panel.
     * @return The header panel
     */
    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }
    
    /**
     * Gets the sidebar panel.
     * @return The sidebar panel
     */
    public SidebarPanel getSidebarPanel() {
        return sidebarPanel;
    }
    
    /**
     * Gets the content panel.
     * @return The content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    /**
     * Gets the status bar.
     * @return The status bar
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }
    
    /**
     * Gets the currently displayed panel.
     * @return The current panel
     */
    public JPanel getCurrentPanel() {
        return currentPanel;
    }
    
    /**
     * Sets the role change listener.
     * @param listener The listener
     */
    public void setRoleChangeListener(RoleChangeListener listener) {
        this.roleChangeListener = listener;
    }
    
    /**
     * Sets the menu action handler.
     * @param handler The handler
     */
    public void setMenuActionHandler(MenuActionHandler handler) {
        this.menuActionHandler = handler;
    }
    
    /**
     * Refreshes the current panel if it implements Refreshable.
     */
    public void refreshCurrentPanel() {
        if (currentPanel instanceof Refreshable) {
            ((Refreshable) currentPanel).refresh();
        }
    }
    
    /**
     * Interface for role change notifications.
     */
    public interface RoleChangeListener {
        void onRoleChanged(Role newRole);
    }
    
    /**
     * Interface for menu action handling.
     */
    public interface MenuActionHandler {
        void onMenuAction(String actionCommand);
    }
    
    /**
     * Interface for refreshable panels.
     */
    public interface Refreshable {
        void refresh();
    }
}
