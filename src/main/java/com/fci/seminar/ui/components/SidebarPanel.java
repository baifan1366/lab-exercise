package com.fci.seminar.ui.components;

import com.fci.seminar.model.enums.Role;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sidebar panel with role-based menu items.
 * Displays navigation menu filtered by user role.
 * Requirements: 10.2
 */
public class SidebarPanel extends JPanel {
    
    private JPanel menuContainer;
    private Consumer<String> menuClickHandler;
    private String selectedMenuItem;
    private List<MenuItemPanel> menuItems;
    
    /**
     * Creates a new sidebar panel.
     */
    public SidebarPanel() {
        this.menuItems = new ArrayList<>();
        initComponents();
        updateMenuForRole(Role.GUEST);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_SIDEBAR);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        
        // Menu container with vertical layout
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD, 0, UIConstants.SPACING_MD, 0));
        
        // Wrap in scroll pane for overflow
        JScrollPane scrollPane = new JScrollPane(menuContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Updates the menu items based on the user's role.
     * @param role The current user role
     */
    public void updateMenuForRole(Role role) {
        menuContainer.removeAll();
        menuItems.clear();
        
        // Add menu items based on role
        switch (role) {
            case COORDINATOR:
                addCoordinatorMenu();
                break;
            case EVALUATOR:
                addEvaluatorMenu();
                break;
            case STUDENT:
                addStudentMenu();
                break;
            case GUEST:
            default:
                addGuestMenu();
                break;
        }
        
        // Add glue to push items to top
        menuContainer.add(Box.createVerticalGlue());
        
        menuContainer.revalidate();
        menuContainer.repaint();
    }
    
    private void addGuestMenu() {
        addMenuItem("\uD83D\uDCC5", "Schedule", "SCHEDULE");
        addMenuSeparator();
        addMenuItem("\uD83D\uDD10", "Login", "LOGIN");
    }
    
    private void addStudentMenu() {
        addMenuItem("\uD83D\uDCC5", "Schedule", "SCHEDULE");
        addMenuSeparator();
        addMenuItem("\uD83D\uDCDD", "My Registration", "REGISTRATION");
        addMenuItem("\uD83D\uDCE4", "Upload Materials", "UPLOAD");
        addMenuItem("\uD83D\uDCCB", "My Status", "STATUS");
        addMenuSeparator();
        addMenuItem("\uD83D\uDC64", "Profile", "PROFILE");
        addMenuItem("\uD83D\uDEAA", "Logout", "LOGOUT");
    }
    
    private void addEvaluatorMenu() {
        addMenuItem("\uD83D\uDCC5", "Schedule", "SCHEDULE");
        addMenuSeparator();
        addMenuItem("\uD83D\uDCCB", "Assigned Presentations", "ASSIGNED_LIST");
        addMenuItem("✍\uFE0F", "Evaluation", "EVALUATION");
        addMenuSeparator();
        addMenuItem("\uD83D\uDC64", "Profile", "PROFILE");
        addMenuItem("\uD83D\uDEAA", "Logout", "LOGOUT");
    }
    
    private void addCoordinatorMenu() {
        addMenuItem("\uD83D\uDCCA", "Dashboard", "DASHBOARD");
        addMenuSeparator();
        addMenuItem("\uD83D\uDC65", "User Management", "USER_MANAGEMENT");
        addMenuItem("\uD83D\uDCC5", "Session Management", "SESSION_MANAGEMENT");
        addMenuItem("\uD83D\uDCDD", "Registration Management", "REGISTRATION_MANAGEMENT");
        addMenuSeparator();
        addMenuItem("\uD83C\uDFAF", "Assignments", "ASSIGNMENTS");
        addMenuItem("\uD83D\uDCCB", "Evaluations", "EVALUATIONS_VIEW");
        addMenuSeparator();
        addMenuItem("\uD83C\uDFC6", "Awards", "AWARDS");
        addMenuItem("\uD83D\uDCC8", "Reports", "REPORTS");
        addMenuSeparator();
        addMenuItem("⚙\uFE0F", "Settings", "SETTINGS");
        addMenuItem("\uD83D\uDEAA", "Logout", "LOGOUT");
    }
    
    private void addMenuItem(String icon, String text, String actionCommand) {
        MenuItemPanel item = new MenuItemPanel(icon, text, actionCommand);
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMenuItem(actionCommand);
                if (menuClickHandler != null) {
                    menuClickHandler.accept(actionCommand);
                }
            }
        });
        menuItems.add(item);
        menuContainer.add(item);
    }
    
    private void addMenuSeparator() {
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(255, 255, 255, 30));
        JPanel separatorPanel = new JPanel();
        separatorPanel.setOpaque(false);
        separatorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.SPACING_MD));
        separatorPanel.setPreferredSize(new Dimension(0, UIConstants.SPACING_MD));
        menuContainer.add(separatorPanel);
    }
    
    /**
     * Selects a menu item by its action command.
     * @param actionCommand The action command of the item to select
     */
    public void selectMenuItem(String actionCommand) {
        this.selectedMenuItem = actionCommand;
        for (MenuItemPanel item : menuItems) {
            item.setSelected(item.getActionCommand().equals(actionCommand));
        }
    }
    
    /**
     * Sets the handler for menu item clicks.
     * @param handler The click handler
     */
    public void setMenuClickHandler(Consumer<String> handler) {
        this.menuClickHandler = handler;
    }
    
    /**
     * Gets the currently selected menu item.
     * @return The action command of the selected item
     */
    public String getSelectedMenuItem() {
        return selectedMenuItem;
    }
    
    /**
     * Inner class for menu item panel.
     */
    private static class MenuItemPanel extends JPanel {
        private final String actionCommand;
        private boolean selected;
        private boolean hovered;
        
        public MenuItemPanel(String icon, String text, String actionCommand) {
            this.actionCommand = actionCommand;
            this.selected = false;
            this.hovered = false;
            
            setLayout(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 44));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, UIConstants.SPACING_MD, 0, UIConstants.SPACING_MD));
            
            // Icon label
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            iconLabel.setForeground(UIConstants.TEXT_LIGHT);
            add(iconLabel);
            
            // Text label
            JLabel textLabel = new JLabel(text);
            textLabel.setFont(UIConstants.MENU);
            textLabel.setForeground(UIConstants.TEXT_LIGHT);
            add(textLabel);
            
            // Hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (selected) {
                g2.setColor(UIConstants.PRIMARY_LIGHT);
                g2.fillRoundRect(UIConstants.SPACING_SM, 2, 
                    getWidth() - UIConstants.SPACING_MD, getHeight() - 4, 
                    UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(UIConstants.SPACING_SM, 2, 
                    getWidth() - UIConstants.SPACING_MD, getHeight() - 4, 
                    UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
        
        public String getActionCommand() {
            return actionCommand;
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }
        
        /**
         * Returns whether this menu item is currently selected.
         * @return true if selected, false otherwise
         */
        @SuppressWarnings("unused")
        public boolean isSelected() {
            return selected;
        }
    }
}
