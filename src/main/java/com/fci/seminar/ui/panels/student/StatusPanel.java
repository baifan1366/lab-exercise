package com.fci.seminar.ui.panels.student;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StatusLabel;
import com.fci.seminar.util.FileUtils;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for displaying student registration status.
 * Shows registration details, assigned session, and uploaded files.
 * Requirements: 3.6
 */
public class StatusPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private final AuthService authService;
    private final RegistrationService registrationService;
    private final SessionService sessionService;
    
    // Current registration
    private Registration currentRegistration;
    
    // UI Components
    private JPanel contentPanel;
    
    /**
     * Creates the status panel.
     */
    public StatusPanel() {
        this.authService = AuthService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        this.sessionService = SessionService.getInstance();
        initComponents();
        loadRegistration();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Registration Status");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private void loadRegistration() {
        contentPanel.removeAll();
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            showNoRegistration("Please log in to view your registration status");
            return;
        }
        
        List<Registration> registrations = registrationService.getRegistrationsByStudent(currentUser.getId());
        
        if (registrations.isEmpty()) {
            showNoRegistration("No registration found. Please register for the seminar first.");
            return;
        }
        
        // Get the most recent registration
        currentRegistration = registrations.get(registrations.size() - 1);
        
        // Display registration details
        displayRegistrationDetails();
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showNoRegistration(String message) {
        contentPanel.removeAll();
        
        CardPanel card = new CardPanel("Status");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.BODY);
        messageLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        card.getContentPanel().setLayout(new BorderLayout());
        card.getContentPanel().add(messageLabel, BorderLayout.CENTER);
        
        contentPanel.add(card);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void displayRegistrationDetails() {
        // Registration Status Card
        contentPanel.add(createStatusCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Research Details Card
        contentPanel.add(createResearchCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Session Details Card
        contentPanel.add(createSessionCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Uploaded Files Card
        contentPanel.add(createFilesCard());
    }

    
    private CardPanel createStatusCard() {
        CardPanel card = new CardPanel("Registration Status");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Status badge
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        StatusLabel statusLabel = StatusLabel.forRegistrationStatus(currentRegistration.getStatus().name());
        statusRow.add(statusLabel);
        content.add(statusRow);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Status description
        String statusDescription = getStatusDescription(currentRegistration.getStatus());
        JLabel descLabel = new JLabel(statusDescription);
        descLabel.setFont(UIConstants.BODY);
        descLabel.setForeground(UIConstants.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(descLabel);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Registration date
        if (currentRegistration.getCreatedAt() != null) {
            addDetailRow(content, "Registered On", 
                    currentRegistration.getCreatedAt().format(DATETIME_FORMATTER));
        }
        
        return card;
    }
    
    private String getStatusDescription(RegistrationStatus status) {
        switch (status) {
            case PENDING:
                return "Your registration is pending approval by the coordinator.";
            case APPROVED:
                return "Your registration has been approved. You can now upload your presentation materials.";
            case REJECTED:
                return "Your registration has been rejected. Please contact the coordinator for more information.";
            case CANCELLED:
                return "Your registration has been cancelled.";
            default:
                return "Unknown status.";
        }
    }
    
    private CardPanel createResearchCard() {
        CardPanel card = new CardPanel("Research Details");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Research title
        addDetailRow(content, "Title", currentRegistration.getResearchTitle());
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Presentation type
        String typeText = currentRegistration.getPresentationType() != null ?
                currentRegistration.getPresentationType().name() : "Not specified";
        addDetailRow(content, "Presentation Type", typeText);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Supervisor
        addDetailRow(content, "Supervisor", currentRegistration.getSupervisorName());
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Abstract
        JLabel abstractLabel = new JLabel("Abstract:");
        abstractLabel.setFont(UIConstants.BODY_BOLD);
        abstractLabel.setForeground(UIConstants.TEXT_PRIMARY);
        abstractLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(abstractLabel);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        JTextArea abstractArea = new JTextArea(currentRegistration.getAbstractText());
        abstractArea.setFont(UIConstants.BODY);
        abstractArea.setForeground(UIConstants.TEXT_SECONDARY);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        abstractArea.setEditable(false);
        abstractArea.setOpaque(false);
        abstractArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        abstractArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        content.add(abstractArea);
        
        return card;
    }
    
    private CardPanel createSessionCard() {
        CardPanel card = new CardPanel("Assigned Session");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        if (currentRegistration.getSessionId() == null) {
            JLabel noSessionLabel = new JLabel("No session assigned yet");
            noSessionLabel.setFont(UIConstants.BODY);
            noSessionLabel.setForeground(UIConstants.TEXT_SECONDARY);
            noSessionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(noSessionLabel);
        } else {
            Session session = sessionService.getSessionById(currentRegistration.getSessionId());
            if (session != null) {
                // Date
                addDetailRow(content, "Date", session.getDate().format(DATE_FORMATTER));
                content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                
                // Time
                String timeText = session.getStartTime().format(TIME_FORMATTER) + " - " +
                        session.getEndTime().format(TIME_FORMATTER);
                addDetailRow(content, "Time", timeText);
                content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                
                // Venue
                addDetailRow(content, "Venue", session.getVenue());
                content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                
                // Type
                String typeText = session.getType() != null ? session.getType().name() : "Not specified";
                addDetailRow(content, "Session Type", typeText);
                content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                
                // Board ID (for poster presentations only)
                if (currentRegistration.getPresentationType() == com.fci.seminar.model.enums.SessionType.POSTER) {
                    String boardId = currentRegistration.getBoardId();
                    if (boardId != null && !boardId.isEmpty()) {
                        addDetailRow(content, "Board ID", boardId);
                        content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                    } else {
                        addDetailRow(content, "Board ID", "Not assigned yet");
                        content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                    }
                }
                
                // Description
                if (session.getDescription() != null && !session.getDescription().isEmpty()) {
                    addDetailRow(content, "Description", session.getDescription());
                }
            } else {
                JLabel errorLabel = new JLabel("Session information not available");
                errorLabel.setFont(UIConstants.BODY);
                errorLabel.setForeground(UIConstants.DANGER);
                errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(errorLabel);
            }
        }
        
        return card;
    }
    
    private CardPanel createFilesCard() {
        CardPanel card = new CardPanel("Uploaded Files");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        String filePath = currentRegistration.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            JLabel noFileLabel = new JLabel("No files uploaded yet");
            noFileLabel.setFont(UIConstants.BODY);
            noFileLabel.setForeground(UIConstants.TEXT_SECONDARY);
            noFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(noFileLabel);
            
            // Show hint if approved
            if (currentRegistration.getStatus() == RegistrationStatus.APPROVED) {
                content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
                JLabel hintLabel = new JLabel("Go to 'Upload Materials' to upload your presentation files.");
                hintLabel.setFont(UIConstants.SMALL);
                hintLabel.setForeground(UIConstants.INFO);
                hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(hintLabel);
            }
        } else {
            // Show file info
            JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
            fileRow.setOpaque(false);
            fileRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel iconLabel = new JLabel("\uD83D\uDCC4"); // Document emoji
            iconLabel.setFont(UIConstants.BODY);
            fileRow.add(iconLabel);
            
            String fileName = new File(filePath).getName();
            JLabel nameLabel = new JLabel(fileName);
            nameLabel.setFont(UIConstants.BODY);
            nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
            fileRow.add(nameLabel);
            
            // File size if exists
            File file = new File(filePath);
            if (file.exists()) {
                JLabel sizeLabel = new JLabel("(" + FileUtils.formatFileSize(file.length()) + ")");
                sizeLabel.setFont(UIConstants.SMALL);
                sizeLabel.setForeground(UIConstants.TEXT_SECONDARY);
                fileRow.add(sizeLabel);
            }
            
            content.add(fileRow);
        }
        
        return card;
    }
    
    private void addDetailRow(JPanel container, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_SM, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(UIConstants.BODY_BOLD);
        labelComponent.setForeground(UIConstants.TEXT_PRIMARY);
        labelComponent.setPreferredSize(new Dimension(130, 20));
        row.add(labelComponent, BorderLayout.WEST);
        
        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(UIConstants.BODY);
        valueComponent.setForeground(UIConstants.TEXT_SECONDARY);
        row.add(valueComponent, BorderLayout.CENTER);
        
        container.add(row);
    }
    
    @Override
    public void refresh() {
        loadRegistration();
    }
}
