package com.fci.seminar.ui.dialogs;

import com.fci.seminar.model.Registration;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTextField;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for assigning board IDs to poster presentations.
 * Allows coordinators to assign exhibition board numbers.
 */
public class BoardAssignmentDialog extends JDialog {
    
    private StyledTextField boardIdField;
    private boolean confirmed = false;
    private String assignedBoardId;
    
    /**
     * Creates a board assignment dialog.
     * @param parent The parent frame
     * @param registration The registration to assign board to
     */
    public BoardAssignmentDialog(JFrame parent, Registration registration) {
        super(parent, "Assign Board ID", true);
        initComponents(registration);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents(Registration registration) {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        contentPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Assign Exhibition Board");
        titleLabel.setFont(UIConstants.TITLE_SMALL);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Registration info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, UIConstants.SPACING_SM));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(400, 80));
        
        JLabel studentLabel = new JLabel("Student ID: " + registration.getStudentId());
        studentLabel.setFont(UIConstants.BODY);
        studentLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(studentLabel);
        
        JLabel titleInfoLabel = new JLabel("Title: " + truncate(registration.getResearchTitle(), 50));
        titleInfoLabel.setFont(UIConstants.BODY);
        titleInfoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(titleInfoLabel);
        
        JLabel typeLabel = new JLabel("Type: " + registration.getPresentationType());
        typeLabel.setFont(UIConstants.BODY);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(typeLabel);
        
        contentPanel.add(infoPanel);
        
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_LG));
        
        // Board ID input
        JLabel boardLabel = new JLabel("Board ID:");
        boardLabel.setFont(UIConstants.BODY_BOLD);
        boardLabel.setForeground(UIConstants.TEXT_PRIMARY);
        boardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(boardLabel);
        
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        boardIdField = new StyledTextField("e.g., B01, B02, B03...");
        boardIdField.setAlignmentX(Component.LEFT_ALIGNMENT);
        boardIdField.setMaximumSize(new Dimension(400, UIConstants.INPUT_HEIGHT));
        
        // Pre-fill if already assigned
        if (registration.getBoardId() != null && !registration.getBoardId().isEmpty()) {
            boardIdField.setText(registration.getBoardId());
        }
        
        contentPanel.add(boardIdField);
        
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Help text
        JLabel helpLabel = new JLabel("Format: B01-B99 (Board number for poster exhibition)");
        helpLabel.setFont(UIConstants.SMALL);
        helpLabel.setForeground(UIConstants.TEXT_MUTED);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(helpLabel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD, UIConstants.SPACING_LG,
            UIConstants.SPACING_MD, UIConstants.SPACING_LG
        ));
        
        StyledButton cancelBtn = StyledButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        buttonPanel.add(cancelBtn);
        
        StyledButton assignBtn = StyledButton.primary("Assign");
        assignBtn.addActionListener(e -> handleAssign());
        buttonPanel.add(assignBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Enter key to assign
        getRootPane().setDefaultButton(assignBtn);
    }
    
    private void handleAssign() {
        String boardId = boardIdField.getText().trim().toUpperCase();
        
        // Validate board ID
        if (boardId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a board ID.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate format (B01-B99)
        if (!boardId.matches("^B\\d{2}$")) {
            JOptionPane.showMessageDialog(this,
                "Invalid board ID format. Please use format: B01, B02, etc.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        assignedBoardId = boardId;
        confirmed = true;
        dispose();
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Shows the dialog and returns the assigned board ID.
     * @param parent The parent frame
     * @param registration The registration to assign board to
     * @return The assigned board ID, or null if cancelled
     */
    public static String showDialog(JFrame parent, Registration registration) {
        BoardAssignmentDialog dialog = new BoardAssignmentDialog(parent, registration);
        dialog.setVisible(true);
        return dialog.confirmed ? dialog.assignedBoardId : null;
    }
}
