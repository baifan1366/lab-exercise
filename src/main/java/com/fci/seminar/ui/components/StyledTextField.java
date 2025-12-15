package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Custom styled text field with placeholder support and focus state styling.
 * Displays placeholder text when empty and not focused.
 */
public class StyledTextField extends JTextField {
    
    private String placeholder;
    private boolean showingPlaceholder;
    private Color normalBorderColor;
    private Color focusBorderColor;
    
    /**
     * Creates a styled text field with placeholder.
     * @param placeholder The placeholder text
     */
    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        this.normalBorderColor = UIConstants.BORDER;
        this.focusBorderColor = UIConstants.PRIMARY_LIGHT;
        initStyle();
    }
    
    /**
     * Creates a styled text field without placeholder.
     */
    public StyledTextField() {
        this("");
        this.showingPlaceholder = false;
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setCaretColor(UIConstants.TEXT_PRIMARY);
        
        // Set border with padding
        updateBorder(normalBorderColor);
        
        // Set preferred size
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        
        // Show placeholder initially
        if (placeholder != null && !placeholder.isEmpty()) {
            setText(placeholder);
            setForeground(UIConstants.TEXT_MUTED);
        }
        
        // Add focus listener for placeholder and border effects
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    StyledTextField.super.setText("");
                    setForeground(UIConstants.TEXT_PRIMARY);
                    showingPlaceholder = false;
                }
                updateBorder(focusBorderColor);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (StyledTextField.super.getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                    StyledTextField.super.setText(placeholder);
                    setForeground(UIConstants.TEXT_MUTED);
                    showingPlaceholder = true;
                }
                updateBorder(normalBorderColor);
            }
        });
    }
    
    private void updateBorder(Color borderColor) {
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(borderColor, UIConstants.RADIUS_SM),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }
    
    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            if (placeholder != null && !placeholder.isEmpty() && !hasFocus()) {
                super.setText(placeholder);
                setForeground(UIConstants.TEXT_MUTED);
                showingPlaceholder = true;
            } else {
                super.setText("");
                showingPlaceholder = false;
            }
        } else {
            super.setText(text);
            setForeground(UIConstants.TEXT_PRIMARY);
            showingPlaceholder = false;
        }
    }
    
    /**
     * Sets the placeholder text.
     * @param placeholder The placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        if (showingPlaceholder || super.getText().isEmpty()) {
            super.setText(placeholder);
            setForeground(UIConstants.TEXT_MUTED);
            showingPlaceholder = true;
        }
    }
    
    /**
     * Gets the placeholder text.
     * @return The placeholder text
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Checks if the field is showing placeholder.
     * @return True if showing placeholder
     */
    public boolean isShowingPlaceholder() {
        return showingPlaceholder;
    }
    
    /**
     * Sets the normal border color.
     * @param color The border color
     */
    public void setNormalBorderColor(Color color) {
        this.normalBorderColor = color;
        if (!hasFocus()) {
            updateBorder(normalBorderColor);
        }
    }
    
    /**
     * Sets the focus border color.
     * @param color The border color
     */
    public void setFocusBorderColor(Color color) {
        this.focusBorderColor = color;
        if (hasFocus()) {
            updateBorder(focusBorderColor);
        }
    }
}
