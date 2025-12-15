package com.fci.seminar.ui.panels.evaluator;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Registration;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StatusLabel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.dialogs.ConfirmDialog;
import com.fci.seminar.ui.dialogs.MessageDialog;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel for evaluating presentations with scoring sliders.
 * Displays real-time total score calculation, comments area, and save/submit buttons.
 * Requirements: 5.2, 5.3, 5.4, 5.5, 5.6
 */
@SuppressWarnings("unused")
public class EvaluationPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private final AuthService authService; // Reserved for future permission checks
    private final EvaluationService evaluationService;
    private final RegistrationService registrationService;
    
    // Current evaluation
    private Evaluation currentEvaluation;
    private Registration currentRegistration;
    
    // UI Components - Scoring sliders
    private JSlider problemClaritySlider;
    private JSlider methodologySlider;
    private JSlider resultsSlider;
    private JSlider presentationQualitySlider;
    
    // Score labels
    private JLabel problemClarityScoreLabel;
    private JLabel methodologyScoreLabel;
    private JLabel resultsScoreLabel;
    private JLabel presentationQualityScoreLabel;
    private JLabel totalScoreLabel;
    
    // Comments
    private JTextArea commentsArea;
    
    // Buttons
    private StyledButton saveDraftButton;
    private StyledButton submitButton;
    private StyledButton backButton;
    
    // Status display
    private JPanel statusPanel;
    
    // Navigation callback
    private BackNavigationHandler backHandler;
    
    /**
     * Creates the evaluation panel.
     */
    public EvaluationPanel() {
        this.authService = AuthService.getInstance();
        this.evaluationService = EvaluationService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header with back button
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content - scrollable
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Presentation info card
        contentPanel.add(createPresentationInfoCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Scoring card
        contentPanel.add(createScoringCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Comments card
        contentPanel.add(createCommentsCard());
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Action buttons
        contentPanel.add(createActionPanel());
        
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
        
        // Back button and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, 0));
        leftPanel.setOpaque(false);
        
        backButton = StyledButton.secondary("\u2190 Back");
        backButton.addActionListener(e -> navigateBack());
        leftPanel.add(backButton);
        
        JLabel titleLabel = new JLabel("Evaluation Form");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        leftPanel.add(titleLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Status panel
        statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        statusPanel.setOpaque(false);
        headerPanel.add(statusPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private CardPanel createPresentationInfoCard() {
        CardPanel card = new CardPanel("Presentation Details");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Placeholder - will be populated when evaluation is loaded
        JLabel placeholderLabel = new JLabel("Select an evaluation to view details");
        placeholderLabel.setFont(UIConstants.BODY);
        placeholderLabel.setForeground(UIConstants.TEXT_SECONDARY);
        placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(placeholderLabel);
        
        return card;
    }
    
    private CardPanel createScoringCard() {
        CardPanel card = new CardPanel("Scoring Rubric");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Create change listener for real-time score update
        ChangeListener scoreChangeListener = e -> updateTotalScore();
        
        // Problem Clarity (0-25)
        problemClaritySlider = createScoreSlider();
        problemClaritySlider.addChangeListener(scoreChangeListener);
        problemClarityScoreLabel = new JLabel("0/25");
        content.add(createScoringRow("Problem Clarity", 
                "Clarity of research problem and objectives", 
                problemClaritySlider, problemClarityScoreLabel));
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Methodology (0-25)
        methodologySlider = createScoreSlider();
        methodologySlider.addChangeListener(scoreChangeListener);
        methodologyScoreLabel = new JLabel("0/25");
        content.add(createScoringRow("Methodology", 
                "Appropriateness and rigor of research methods", 
                methodologySlider, methodologyScoreLabel));
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Results (0-25)
        resultsSlider = createScoreSlider();
        resultsSlider.addChangeListener(scoreChangeListener);
        resultsScoreLabel = new JLabel("0/25");
        content.add(createScoringRow("Results", 
                "Quality and significance of research findings", 
                resultsSlider, resultsScoreLabel));
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Presentation Quality (0-25)
        presentationQualitySlider = createScoreSlider();
        presentationQualitySlider.addChangeListener(scoreChangeListener);
        presentationQualityScoreLabel = new JLabel("0/25");
        content.add(createScoringRow("Presentation Quality", 
                "Clarity, organization, and delivery of presentation", 
                presentationQualitySlider, presentationQualityScoreLabel));
        
        // Separator
        content.add(Box.createVerticalStrut(UIConstants.SPACING_LG));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        content.add(separator);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Total score
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel totalLabel = new JLabel("Total Score");
        totalLabel.setFont(UIConstants.TITLE_SMALL);
        totalLabel.setForeground(UIConstants.TEXT_PRIMARY);
        totalPanel.add(totalLabel, BorderLayout.WEST);
        
        totalScoreLabel = new JLabel("0/100");
        totalScoreLabel.setFont(UIConstants.TITLE_MEDIUM);
        totalScoreLabel.setForeground(UIConstants.PRIMARY);
        totalPanel.add(totalScoreLabel, BorderLayout.EAST);
        
        content.add(totalPanel);
        
        return card;
    }
    
    private JSlider createScoreSlider() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 25, 0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setOpaque(false);
        return slider;
    }
    
    private JPanel createScoringRow(String title, String description, JSlider slider, JLabel scoreLabel) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Title and score
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.BODY_BOLD);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleRow.add(titleLabel, BorderLayout.WEST);
        
        scoreLabel.setFont(UIConstants.BODY_BOLD);
        scoreLabel.setForeground(UIConstants.PRIMARY);
        titleRow.add(scoreLabel, BorderLayout.EAST);
        
        row.add(titleRow);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(UIConstants.SMALL);
        descLabel.setForeground(UIConstants.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(descLabel);
        row.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Slider
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.add(slider);
        
        return row;
    }

    
    private CardPanel createCommentsCard() {
        CardPanel card = new CardPanel("Comments");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout());
        
        commentsArea = new JTextArea(5, 40);
        commentsArea.setFont(UIConstants.BODY);
        commentsArea.setForeground(UIConstants.TEXT_PRIMARY);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER),
                new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_SM, 
                        UIConstants.SPACING_SM, UIConstants.SPACING_SM)
        ));
        
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        scrollPane.setBorder(null);
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        // Character count hint
        JLabel hintLabel = new JLabel("Provide qualitative feedback for the presenter (optional)");
        hintLabel.setFont(UIConstants.SMALL);
        hintLabel.setForeground(UIConstants.TEXT_SECONDARY);
        hintLabel.setBorder(new EmptyBorder(UIConstants.SPACING_SM, 0, 0, 0));
        content.add(hintLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_MD, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        saveDraftButton = StyledButton.secondary("Save Draft");
        saveDraftButton.addActionListener(e -> saveDraft());
        panel.add(saveDraftButton);
        
        submitButton = StyledButton.primary("Submit Evaluation");
        submitButton.addActionListener(e -> submitEvaluation());
        panel.add(submitButton);
        
        return panel;
    }
    
    /**
     * Loads an evaluation for editing/viewing.
     * @param evaluationId The evaluation ID to load
     */
    public void loadEvaluation(Long evaluationId) {
        if (evaluationId == null) {
            showError("Invalid evaluation ID");
            return;
        }
        
        currentEvaluation = evaluationService.getEvaluationById(evaluationId);
        if (currentEvaluation == null) {
            showError("Evaluation not found");
            return;
        }
        
        currentRegistration = registrationService.getRegistrationById(currentEvaluation.getRegistrationId());
        
        // Update UI with evaluation data
        updatePresentationInfo();
        updateScoringSliders();
        updateCommentsArea();
        updateStatusDisplay();
        updateButtonStates();
    }
    
    private void updatePresentationInfo() {
        // Find and update the presentation info card
        JScrollPane scrollPane = (JScrollPane) getComponent(1);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        Component[] components = contentPanel.getComponents();
        
        // Rebuild the presentation info card content
        if (components.length > 0 && components[0] instanceof CardPanel) {
            CardPanel card = (CardPanel) components[0];
            JPanel content = card.getContentPanel();
            content.removeAll();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            
            if (currentRegistration != null) {
                // Research title
                addDetailRow(content, "Research Title", currentRegistration.getResearchTitle());
                content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
                
                // Presentation type
                String typeText = currentRegistration.getPresentationType() != null ?
                        currentRegistration.getPresentationType().name() : "N/A";
                addDetailRow(content, "Presentation Type", typeText);
                content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
                
                // Supervisor
                addDetailRow(content, "Supervisor", currentRegistration.getSupervisorName());
                content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
                
                // Abstract (truncated)
                JLabel abstractLabel = new JLabel("Abstract:");
                abstractLabel.setFont(UIConstants.BODY_BOLD);
                abstractLabel.setForeground(UIConstants.TEXT_PRIMARY);
                abstractLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(abstractLabel);
                content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                
                String abstractText = currentRegistration.getAbstractText();
                if (abstractText != null && abstractText.length() > 300) {
                    abstractText = abstractText.substring(0, 300) + "...";
                }
                JTextArea abstractArea = new JTextArea(abstractText);
                abstractArea.setFont(UIConstants.BODY);
                abstractArea.setForeground(UIConstants.TEXT_SECONDARY);
                abstractArea.setLineWrap(true);
                abstractArea.setWrapStyleWord(true);
                abstractArea.setEditable(false);
                abstractArea.setOpaque(false);
                abstractArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                abstractArea.setBorder(new EmptyBorder(0, 0, 0, 0));
                content.add(abstractArea);
            } else {
                JLabel errorLabel = new JLabel("Registration information not available");
                errorLabel.setFont(UIConstants.BODY);
                errorLabel.setForeground(UIConstants.DANGER);
                errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(errorLabel);
            }
            
            content.revalidate();
            content.repaint();
        }
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
    
    private void updateScoringSliders() {
        if (currentEvaluation != null) {
            problemClaritySlider.setValue(currentEvaluation.getProblemClarity());
            methodologySlider.setValue(currentEvaluation.getMethodology());
            resultsSlider.setValue(currentEvaluation.getResults());
            presentationQualitySlider.setValue(currentEvaluation.getPresentationQuality());
        } else {
            problemClaritySlider.setValue(0);
            methodologySlider.setValue(0);
            resultsSlider.setValue(0);
            presentationQualitySlider.setValue(0);
        }
        updateTotalScore();
    }
    
    private void updateCommentsArea() {
        if (currentEvaluation != null && currentEvaluation.getComments() != null) {
            commentsArea.setText(currentEvaluation.getComments());
        } else {
            commentsArea.setText("");
        }
    }
    
    private void updateStatusDisplay() {
        statusPanel.removeAll();
        
        if (currentEvaluation != null) {
            if (currentEvaluation.isSubmitted()) {
                StatusLabel statusLabel = StatusLabel.success("Submitted");
                statusPanel.add(statusLabel);
                
                if (currentEvaluation.getSubmittedAt() != null) {
                    JLabel dateLabel = new JLabel(currentEvaluation.getSubmittedAt().format(DATETIME_FORMATTER));
                    dateLabel.setFont(UIConstants.SMALL);
                    dateLabel.setForeground(UIConstants.TEXT_SECONDARY);
                    statusPanel.add(dateLabel);
                }
            } else if (currentEvaluation.getTotalScore() > 0) {
                StatusLabel statusLabel = StatusLabel.warning("Draft");
                statusPanel.add(statusLabel);
            } else {
                StatusLabel statusLabel = StatusLabel.info("Pending");
                statusPanel.add(statusLabel);
            }
        }
        
        statusPanel.revalidate();
        statusPanel.repaint();
    }
    
    private void updateButtonStates() {
        boolean isSubmitted = currentEvaluation != null && currentEvaluation.isSubmitted();
        
        // Disable editing if already submitted
        problemClaritySlider.setEnabled(!isSubmitted);
        methodologySlider.setEnabled(!isSubmitted);
        resultsSlider.setEnabled(!isSubmitted);
        presentationQualitySlider.setEnabled(!isSubmitted);
        commentsArea.setEditable(!isSubmitted);
        
        saveDraftButton.setEnabled(!isSubmitted);
        submitButton.setEnabled(!isSubmitted);
        
        if (isSubmitted) {
            submitButton.setText("Already Submitted");
        } else {
            submitButton.setText("Submit Evaluation");
        }
    }

    
    /**
     * Updates the total score display based on slider values.
     * Requirements: 5.3
     */
    private void updateTotalScore() {
        int problemClarity = problemClaritySlider.getValue();
        int methodology = methodologySlider.getValue();
        int results = resultsSlider.getValue();
        int presentationQuality = presentationQualitySlider.getValue();
        
        // Update individual score labels
        problemClarityScoreLabel.setText(problemClarity + "/25");
        methodologyScoreLabel.setText(methodology + "/25");
        resultsScoreLabel.setText(results + "/25");
        presentationQualityScoreLabel.setText(presentationQuality + "/25");
        
        // Calculate and update total
        int total = problemClarity + methodology + results + presentationQuality;
        totalScoreLabel.setText(total + "/100");
        
        // Color code the total score
        if (total >= 80) {
            totalScoreLabel.setForeground(UIConstants.SUCCESS);
        } else if (total >= 60) {
            totalScoreLabel.setForeground(UIConstants.PRIMARY);
        } else if (total >= 40) {
            totalScoreLabel.setForeground(UIConstants.WARNING);
        } else {
            totalScoreLabel.setForeground(UIConstants.DANGER);
        }
    }
    
    /**
     * Saves the evaluation as a draft.
     * Requirements: 5.5
     */
    private void saveDraft() {
        if (currentEvaluation == null) {
            showError("No evaluation loaded");
            return;
        }
        
        if (currentEvaluation.isSubmitted()) {
            showError("Cannot modify submitted evaluation");
            return;
        }
        
        try {
            // Update evaluation with current values
            currentEvaluation.setProblemClarity(problemClaritySlider.getValue());
            currentEvaluation.setMethodology(methodologySlider.getValue());
            currentEvaluation.setResults(resultsSlider.getValue());
            currentEvaluation.setPresentationQuality(presentationQualitySlider.getValue());
            currentEvaluation.setComments(commentsArea.getText());
            currentEvaluation.setSubmitted(false);
            
            // Save to service
            currentEvaluation = evaluationService.saveEvaluation(currentEvaluation);
            
            updateStatusDisplay();
            showSuccess("Draft saved successfully");
            
        } catch (IllegalArgumentException e) {
            showError("Failed to save draft: " + e.getMessage());
        }
    }
    
    /**
     * Submits the evaluation as final.
     * Requirements: 5.6
     */
    private void submitEvaluation() {
        if (currentEvaluation == null) {
            showError("No evaluation loaded");
            return;
        }
        
        if (currentEvaluation.isSubmitted()) {
            showError("Evaluation already submitted");
            return;
        }
        
        // Confirm submission
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmDialog.showDialog(
                parentFrame,
                "Submit Evaluation",
                "Are you sure you want to submit this evaluation? " +
                "Once submitted, you cannot modify the scores."
        );
        
        if (!confirmed) {
            return;
        }
        
        try {
            // First save current values
            currentEvaluation.setProblemClarity(problemClaritySlider.getValue());
            currentEvaluation.setMethodology(methodologySlider.getValue());
            currentEvaluation.setResults(resultsSlider.getValue());
            currentEvaluation.setPresentationQuality(presentationQualitySlider.getValue());
            currentEvaluation.setComments(commentsArea.getText());
            
            // Save first
            evaluationService.saveEvaluation(currentEvaluation);
            
            // Then submit
            currentEvaluation = evaluationService.submitEvaluation(currentEvaluation.getId());
            
            updateStatusDisplay();
            updateButtonStates();
            showSuccess("Evaluation submitted successfully");
            
        } catch (IllegalArgumentException e) {
            showError("Failed to submit evaluation: " + e.getMessage());
        }
    }
    
    /**
     * Navigates back to the assigned list.
     */
    private void navigateBack() {
        // Check for unsaved changes
        if (currentEvaluation != null && !currentEvaluation.isSubmitted() && hasUnsavedChanges()) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            boolean saveChanges = ConfirmDialog.showWarning(
                    parentFrame,
                    "Unsaved Changes",
                    "You have unsaved changes. Do you want to save before leaving?"
            );
            
            if (saveChanges) {
                saveDraft();
            }
        }
        
        if (backHandler != null) {
            backHandler.navigateBack();
        }
    }
    
    private boolean hasUnsavedChanges() {
        if (currentEvaluation == null) return false;
        
        return currentEvaluation.getProblemClarity() != problemClaritySlider.getValue() ||
               currentEvaluation.getMethodology() != methodologySlider.getValue() ||
               currentEvaluation.getResults() != resultsSlider.getValue() ||
               currentEvaluation.getPresentationQuality() != presentationQualitySlider.getValue() ||
               !java.util.Objects.equals(currentEvaluation.getComments(), commentsArea.getText());
    }
    
    private void showError(String message) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showError(parentFrame, "Error", message);
    }
    
    private void showSuccess(String message) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showSuccess(parentFrame, "Success", message);
    }
    
    /**
     * Sets the back navigation handler.
     * @param handler The navigation handler
     */
    public void setBackHandler(BackNavigationHandler handler) {
        this.backHandler = handler;
    }
    
    /**
     * Gets the current evaluation.
     * @return The current evaluation
     */
    public Evaluation getCurrentEvaluation() {
        return currentEvaluation;
    }
    
    @Override
    public void refresh() {
        if (currentEvaluation != null) {
            loadEvaluation(currentEvaluation.getId());
        }
    }
    
    /**
     * Interface for handling back navigation.
     */
    public interface BackNavigationHandler {
        void navigateBack();
    }
}
