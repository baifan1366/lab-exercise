package com.fci.seminar.ui.panels.evaluator;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Registration;
import com.fci.seminar.model.User;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StatusLabel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTable;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying list of presentations assigned to an evaluator.
 * Shows evaluation status for each presentation and enables navigation to evaluation form.
 * Requirements: 5.1
 */
public class AssignedListPanel extends JPanel implements MainFrame.Refreshable {
    
    private final AuthService authService;
    private final EvaluationService evaluationService;
    private final RegistrationService registrationService;
    
    // UI Components
    private StyledTable assignedTable;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    
    // Navigation callback
    private EvaluationNavigationHandler navigationHandler;
    
    /**
     * Creates the assigned list panel.
     */
    public AssignedListPanel() {
        this.authService = AuthService.getInstance();
        this.evaluationService = EvaluationService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        initComponents();
        loadAssignedPresentations();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content - table card
        add(createTableCard(), BorderLayout.CENTER);
    }

    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Assigned Presentations");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Summary and refresh button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_MD, 0));
        rightPanel.setOpaque(false);
        
        summaryLabel = new JLabel();
        summaryLabel.setFont(UIConstants.BODY);
        summaryLabel.setForeground(UIConstants.TEXT_SECONDARY);
        rightPanel.add(summaryLabel);
        
        StyledButton refreshButton = StyledButton.secondary("Refresh");
        refreshButton.addActionListener(e -> refresh());
        rightPanel.add(refreshButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private CardPanel createTableCard() {
        CardPanel card = new CardPanel();
        card.setContentLayout(new BorderLayout());
        
        // Table columns
        String[] columns = {"ID", "Research Title", "Presentation Type", "Status", "Score", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is "editable" (for button)
            }
        };
        
        assignedTable = new StyledTable(tableModel);
        assignedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        assignedTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        assignedTable.getColumnModel().getColumn(0).setMaxWidth(60);
        assignedTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        assignedTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        assignedTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        assignedTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        assignedTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Custom renderer for status column
        assignedTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        // Custom renderer for action column
        assignedTable.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        assignedTable.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor());
        
        JScrollPane scrollPane = new JScrollPane(assignedTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        card.getContentPanel().add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadAssignedPresentations() {
        tableModel.setRowCount(0);
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            updateSummary(0, 0);
            return;
        }
        
        // Get evaluations assigned to this evaluator
        List<Evaluation> evaluations = evaluationService.getEvaluationsByEvaluator(currentUser.getId());
        
        int completed = 0;
        int pending = 0;
        
        for (Evaluation evaluation : evaluations) {
            Registration registration = registrationService.getRegistrationById(evaluation.getRegistrationId());
            if (registration == null) continue;
            
            String status;
            String score;
            if (evaluation.isSubmitted()) {
                status = "Submitted";
                score = String.valueOf(evaluation.getTotalScore());
                completed++;
            } else if (evaluation.getTotalScore() > 0) {
                status = "Draft";
                score = String.valueOf(evaluation.getTotalScore()) + " (draft)";
                pending++;
            } else {
                status = "Pending";
                score = "-";
                pending++;
            }
            
            String presentationType = registration.getPresentationType() != null ?
                    registration.getPresentationType().name() : "N/A";
            
            Object[] row = {
                evaluation.getId(),
                registration.getResearchTitle(),
                presentationType,
                status,
                score,
                evaluation.isSubmitted() ? "View" : "Evaluate"
            };
            tableModel.addRow(row);
        }
        
        updateSummary(completed, pending);
    }
    
    private void updateSummary(int completed, int pending) {
        summaryLabel.setText(String.format("Completed: %d | Pending: %d", completed, pending));
    }
    
    /**
     * Sets the navigation handler for opening evaluation form.
     * @param handler The navigation handler
     */
    public void setNavigationHandler(EvaluationNavigationHandler handler) {
        this.navigationHandler = handler;
    }
    
    /**
     * Opens the evaluation form for the specified evaluation ID.
     * @param evaluationId The evaluation ID
     */
    private void openEvaluationForm(Long evaluationId) {
        if (navigationHandler != null) {
            navigationHandler.navigateToEvaluation(evaluationId);
        }
    }
    
    @Override
    public void refresh() {
        loadAssignedPresentations();
    }
    
    /**
     * Interface for handling navigation to evaluation form.
     */
    public interface EvaluationNavigationHandler {
        void navigateToEvaluation(Long evaluationId);
    }

    
    /**
     * Custom cell renderer for status column.
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String status = value != null ? value.toString() : "";
            StatusLabel label;
            
            switch (status) {
                case "Submitted":
                    label = StatusLabel.success(status);
                    break;
                case "Draft":
                    label = StatusLabel.warning(status);
                    break;
                case "Pending":
                    label = StatusLabel.info(status);
                    break;
                default:
                    label = StatusLabel.info(status);
            }
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            panel.setOpaque(true);
            
            if (isSelected) {
                panel.setBackground(UIConstants.TABLE_HOVER);
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.TABLE_ROW_ALT);
            }
            
            panel.add(label);
            return panel;
        }
    }
    
    /**
     * Custom cell renderer for action button column.
     */
    private class ActionButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String buttonText = value != null ? value.toString() : "Evaluate";
            StyledButton button;
            
            if ("View".equals(buttonText)) {
                button = StyledButton.secondary(buttonText);
            } else {
                button = StyledButton.primary(buttonText);
            }
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
            panel.setOpaque(true);
            
            if (isSelected) {
                panel.setBackground(UIConstants.TABLE_HOVER);
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.TABLE_ROW_ALT);
            }
            
            panel.add(button);
            return panel;
        }
    }
    
    /**
     * Custom cell editor for action button column.
     */
    private class ActionButtonEditor extends DefaultCellEditor {
        private StyledButton button;
        private Long evaluationId;
        
        public ActionButtonEditor() {
            super(new JCheckBox());
            button = StyledButton.primary("Evaluate");
            button.addActionListener(e -> {
                fireEditingStopped();
                openEvaluationForm(evaluationId);
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            // Get evaluation ID from first column
            evaluationId = (Long) table.getValueAt(row, 0);
            
            String buttonText = value != null ? value.toString() : "Evaluate";
            if ("View".equals(buttonText)) {
                button = StyledButton.secondary(buttonText);
            } else {
                button = StyledButton.primary(buttonText);
            }
            
            button.addActionListener(e -> {
                fireEditingStopped();
                openEvaluationForm(evaluationId);
            });
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
            panel.setOpaque(true);
            panel.setBackground(UIConstants.TABLE_HOVER);
            panel.add(button);
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
