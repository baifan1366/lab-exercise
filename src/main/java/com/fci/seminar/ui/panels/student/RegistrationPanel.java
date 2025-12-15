package com.fci.seminar.ui.panels.student;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTextField;
import com.fci.seminar.ui.dialogs.MessageDialog;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for student registration to seminar presentations.
 * Provides form for research details and session selection.
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5
 */
public class RegistrationPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private final AuthService authService;
    private final RegistrationService registrationService;
    private final SessionService sessionService;
    
    // Form fields
    private StyledTextField titleField;
    private JTextArea abstractArea;
    private StyledTextField supervisorField;
    private JComboBox<String> typeCombo;
    private JComboBox<SessionItem> sessionCombo;
    
    // Character counters
    private JLabel titleCountLabel;
    private JLabel abstractCountLabel;
    
    // Buttons
    private StyledButton submitButton;
    private StyledButton clearButton;
    
    // Error labels
    private JLabel titleErrorLabel;
    private JLabel abstractErrorLabel;
    private JLabel supervisorErrorLabel;
    private JLabel sessionErrorLabel;
    
    /**
     * Creates the registration panel.
     */
    public RegistrationPanel() {
        this.authService = AuthService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        this.sessionService = SessionService.getInstance();
        initComponents();
        loadAvailableSessions();
    }

    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main form card
        add(createFormCard(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Register for Seminar");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private CardPanel createFormCard() {
        CardPanel card = new CardPanel("Registration Form");
        card.setContentLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, UIConstants.SPACING_MD, 0));
        
        // Research Title field
        formPanel.add(createTitleSection());
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Abstract field
        formPanel.add(createAbstractSection());
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Supervisor field
        formPanel.add(createSupervisorSection());
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Presentation type selection
        formPanel.add(createTypeSection());
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Session selection
        formPanel.add(createSessionSection());
        formPanel.add(Box.createVerticalStrut(UIConstants.SPACING_LG));
        
        // Buttons
        formPanel.add(createButtonPanel());
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        card.getContentPanel().add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTitleSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Label row with counter
        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel label = new JLabel("Research Title *");
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        labelRow.add(label, BorderLayout.WEST);
        
        titleCountLabel = new JLabel("0/" + Registration.MAX_TITLE_LENGTH);
        titleCountLabel.setFont(UIConstants.SMALL);
        titleCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        labelRow.add(titleCountLabel, BorderLayout.EAST);
        
        section.add(labelRow);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        // Text field
        titleField = new StyledTextField("Enter your research title");
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTitleCount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTitleCount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTitleCount(); }
        });
        section.add(titleField);
        
        // Error label
        titleErrorLabel = createErrorLabel();
        section.add(titleErrorLabel);
        
        return section;
    }
    
    private JPanel createAbstractSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Label row with counter
        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel label = new JLabel("Abstract *");
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        labelRow.add(label, BorderLayout.WEST);
        
        abstractCountLabel = new JLabel("0/" + Registration.MAX_ABSTRACT_LENGTH);
        abstractCountLabel.setFont(UIConstants.SMALL);
        abstractCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        labelRow.add(abstractCountLabel, BorderLayout.EAST);
        
        section.add(labelRow);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        // Text area
        abstractArea = new JTextArea(6, 40);
        abstractArea.setFont(UIConstants.BODY);
        abstractArea.setForeground(UIConstants.TEXT_PRIMARY);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        abstractArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        abstractArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAbstractCount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAbstractCount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAbstractCount(); }
        });
        
        JScrollPane scrollPane = new JScrollPane(abstractArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scrollPane.setBorder(null);
        section.add(scrollPane);
        
        // Error label
        abstractErrorLabel = createErrorLabel();
        section.add(abstractErrorLabel);
        
        return section;
    }

    
    private JPanel createSupervisorSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Supervisor Name *");
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(label);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        // Pre-fill with student's supervisor if available
        String defaultSupervisor = "";
        User currentUser = authService.getCurrentUser();
        if (currentUser instanceof Student) {
            String supervisor = ((Student) currentUser).getSupervisor();
            if (supervisor != null && !supervisor.isEmpty()) {
                defaultSupervisor = supervisor;
            }
        }
        
        supervisorField = new StyledTextField("Enter supervisor name");
        supervisorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        if (!defaultSupervisor.isEmpty()) {
            supervisorField.setText(defaultSupervisor);
        }
        section.add(supervisorField);
        
        // Error label
        supervisorErrorLabel = createErrorLabel();
        section.add(supervisorErrorLabel);
        
        return section;
    }
    
    private JPanel createTypeSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Presentation Type *");
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(label);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        typeCombo = new JComboBox<>(new String[]{"Select Type", "Oral", "Poster"});
        typeCombo.setFont(UIConstants.BODY);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        typeCombo.addActionListener(e -> {
            loadAvailableSessions();
        });
        section.add(typeCombo);
        
        return section;
    }
    
    private JPanel createSessionSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Available Session *");
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(label);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        
        sessionCombo = new JComboBox<>();
        sessionCombo.setFont(UIConstants.BODY);
        sessionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        sessionCombo.setRenderer(new SessionComboRenderer());
        section.add(sessionCombo);
        
        // Error label
        sessionErrorLabel = createErrorLabel();
        section.add(sessionErrorLabel);
        
        // Help text
        JLabel helpLabel = new JLabel("Only sessions with Open status and matching type are shown");
        helpLabel.setFont(UIConstants.SMALL);
        helpLabel.setForeground(UIConstants.TEXT_SECONDARY);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        section.add(helpLabel);
        
        return section;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT + 20));
        
        clearButton = StyledButton.secondary("Clear Form");
        clearButton.addActionListener(e -> clearForm());
        panel.add(clearButton);
        
        submitButton = StyledButton.primary("Submit Registration");
        submitButton.addActionListener(e -> submitRegistration());
        panel.add(submitButton);
        
        return panel;
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(UIConstants.SMALL);
        label.setForeground(UIConstants.DANGER);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void updateTitleCount() {
        int count = titleField.getText().length();
        titleCountLabel.setText(count + "/" + Registration.MAX_TITLE_LENGTH);
        if (count > Registration.MAX_TITLE_LENGTH) {
            titleCountLabel.setForeground(UIConstants.DANGER);
        } else {
            titleCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        }
    }
    
    private void updateAbstractCount() {
        int count = abstractArea.getText().length();
        abstractCountLabel.setText(count + "/" + Registration.MAX_ABSTRACT_LENGTH);
        if (count > Registration.MAX_ABSTRACT_LENGTH) {
            abstractCountLabel.setForeground(UIConstants.DANGER);
        } else {
            abstractCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        }
    }
    
    private void loadAvailableSessions() {
        sessionCombo.removeAllItems();
        sessionCombo.addItem(new SessionItem(null, "Select a session"));
        
        String selectedType = (String) typeCombo.getSelectedItem();
        if (selectedType == null || "Select Type".equals(selectedType)) {
            return;
        }
        
        SessionType type = SessionType.valueOf(selectedType.toUpperCase());
        List<Session> sessions = sessionService.getAvailableSessions(type);
        
        for (Session session : sessions) {
            String displayText = formatSessionDisplay(session);
            sessionCombo.addItem(new SessionItem(session, displayText));
        }
    }
    
    private String formatSessionDisplay(Session session) {
        int remaining = session.getCapacity() - session.getRegistered();
        return String.format("%s  %s-%s  %s  (Slots: %d/%d)",
                session.getDate().format(DATE_FORMATTER),
                session.getStartTime().format(TIME_FORMATTER),
                session.getEndTime().format(TIME_FORMATTER),
                session.getVenue(),
                remaining,
                session.getCapacity());
    }

    
    private boolean validateForm() {
        boolean valid = true;
        clearErrors();
        
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            titleErrorLabel.setText("Research title is required");
            valid = false;
        } else if (title.length() > Registration.MAX_TITLE_LENGTH) {
            titleErrorLabel.setText("Title exceeds maximum length of " + Registration.MAX_TITLE_LENGTH + " characters");
            valid = false;
        }
        
        // Validate abstract
        String abstractText = abstractArea.getText().trim();
        if (abstractText.isEmpty()) {
            abstractErrorLabel.setText("Abstract is required");
            valid = false;
        } else if (abstractText.length() > Registration.MAX_ABSTRACT_LENGTH) {
            abstractErrorLabel.setText("Abstract exceeds maximum length of " + Registration.MAX_ABSTRACT_LENGTH + " characters");
            valid = false;
        }
        
        // Validate supervisor
        String supervisor = supervisorField.getText().trim();
        if (supervisor.isEmpty()) {
            supervisorErrorLabel.setText("Supervisor name is required");
            valid = false;
        }
        
        // Validate session selection
        SessionItem selectedItem = (SessionItem) sessionCombo.getSelectedItem();
        if (selectedItem == null || selectedItem.getSession() == null) {
            sessionErrorLabel.setText("Please select a session");
            valid = false;
        }
        
        return valid;
    }
    
    private void clearErrors() {
        titleErrorLabel.setText(" ");
        abstractErrorLabel.setText(" ");
        supervisorErrorLabel.setText(" ");
        sessionErrorLabel.setText(" ");
    }
    
    private void submitRegistration() {
        if (!validateForm()) {
            return;
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            showError("You must be logged in to register");
            return;
        }
        
        // Check if student already has a registration
        if (registrationService.hasStudentRegistered(currentUser.getId())) {
            showWarning("You have already registered for this seminar. Please check your registration status.");
            return;
        }
        
        try {
            // Create registration
            Registration registration = new Registration();
            registration.setStudentId(currentUser.getId());
            registration.setResearchTitle(titleField.getText().trim());
            registration.setAbstractText(abstractArea.getText().trim());
            registration.setSupervisorName(supervisorField.getText().trim());
            
            String selectedType = (String) typeCombo.getSelectedItem();
            registration.setPresentationType(SessionType.valueOf(selectedType.toUpperCase()));
            
            SessionItem selectedSession = (SessionItem) sessionCombo.getSelectedItem();
            if (selectedSession != null && selectedSession.getSession() != null) {
                registration.setSessionId(selectedSession.getSession().getId());
            }
            
            // Submit registration
            Registration saved = registrationService.register(registration);
            
            if (saved != null) {
                showSuccess("Registration submitted successfully! Your registration is pending approval.");
                clearForm();
            } else {
                showError("Failed to submit registration. Please try again.");
            }
            
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        titleField.setText("");
        abstractArea.setText("");
        
        // Reset supervisor to default if available
        User currentUser = authService.getCurrentUser();
        if (currentUser instanceof Student) {
            String supervisor = ((Student) currentUser).getSupervisor();
            if (supervisor != null && !supervisor.isEmpty()) {
                supervisorField.setText(supervisor);
            } else {
                supervisorField.setText("");
            }
        } else {
            supervisorField.setText("");
        }
        
        typeCombo.setSelectedIndex(0);
        sessionCombo.removeAllItems();
        sessionCombo.addItem(new SessionItem(null, "Select a session"));
        
        clearErrors();
        updateTitleCount();
        updateAbstractCount();
    }
    
    private void showSuccess(String message) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showSuccess(parent, "Success", message);
    }
    
    private void showWarning(String message) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showWarning(parent, "Warning", message);
    }
    
    private void showError(String message) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showError(parent, "Error", message);
    }
    
    @Override
    public void refresh() {
        loadAvailableSessions();
    }
    
    /**
     * Helper class for session combo box items.
     */
    private static class SessionItem {
        private final Session session;
        private final String displayText;
        
        public SessionItem(Session session, String displayText) {
            this.session = session;
            this.displayText = displayText;
        }
        
        public Session getSession() {
            return session;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
    
    /**
     * Custom renderer for session combo box.
     */
    private static class SessionComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof SessionItem) {
                SessionItem item = (SessionItem) value;
                setText(item.toString());
                
                if (item.getSession() == null) {
                    setForeground(UIConstants.TEXT_MUTED);
                }
            }
            
            return this;
        }
    }
}
