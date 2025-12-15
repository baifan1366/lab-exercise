package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import javax.swing.*;

/**
 * Card container component extending RoundedPanel with title support.
 * Used for grouping related content with optional header.
 */
public class CardPanel extends RoundedPanel {
    
    private String title;
    private JPanel contentPanel;
    private JLabel titleLabel;
    
    /**
     * Creates a card panel without title.
     */
    public CardPanel() {
        this(null);
    }
    
    /**
     * Creates a card panel with specified title.
     * @param title The card title (null for no title)
     */
    public CardPanel(String title) {
        super(UIConstants.RADIUS_LG, true);
        this.title = title;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, 
            UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, 
            UIConstants.SPACING_LG
        ));
        
        // Add title if provided
        if (title != null && !title.isEmpty()) {
            titleLabel = new JLabel(title);
            titleLabel.setFont(UIConstants.TITLE_SMALL);
            titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(
                0, 0, UIConstants.SPACING_MD, 0
            ));
            add(titleLabel, BorderLayout.NORTH);
        }
        
        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Gets the content panel for adding child components.
     * @return The content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    /**
     * Sets the card title.
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
        if (titleLabel != null) {
            titleLabel.setText(title);
        } else if (title != null && !title.isEmpty()) {
            // Create title label if it doesn't exist
            titleLabel = new JLabel(title);
            titleLabel.setFont(UIConstants.TITLE_SMALL);
            titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(
                0, 0, UIConstants.SPACING_MD, 0
            ));
            add(titleLabel, BorderLayout.NORTH);
            revalidate();
        }
    }
    
    /**
     * Gets the card title.
     * @return The card title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the content panel layout.
     * @param layout The layout manager
     */
    public void setContentLayout(LayoutManager layout) {
        contentPanel.setLayout(layout);
    }
}
