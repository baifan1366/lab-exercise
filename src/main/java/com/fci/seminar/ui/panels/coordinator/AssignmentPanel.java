package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.repository.UserRepository;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTable;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing student-session and evaluator-presentation assignments.
 * Requirements: 7.1, 7.2, 7.3, 7.4, 7.5
 */
public class AssignmentPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final SessionService sessionService;
    private final RegistrationService registrationService;
    private final EvaluationService evaluationService;
    private final UserRepository userRepository;
    
    // Tab pane
    private JTabbedPane tabbedPane;
    
    // Student-Session assignment components
    private StyledTable unassignedStudentsTable;
    private DefaultTableModel unassignedStudentsModel;
    private JComboBox<SessionItem> sessionCombo;
    
    // Evaluator-Presentation assignment components
    private StyledTable registrationsTable;
    private DefaultTableModel registrationsModel;
    private StyledTable evaluatorsTable;
    private DefaultTableModel evaluatorsModel;
    private StyledTable assignmentMatrixTable;
    private DefaultTableModel assignmentMatrixModel;
    
    /**
     * Creates the assignment panel.
     */
    public AssignmentPanel() {
        this.sessionService = SessionService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        this.evaluationService = EvaluationService.getInstance();
        this.userRepository = new UserRepository();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_BOLD);
        tabbedPane.addTab("Student-Session Assignment", createStudentSessionPanel());
        tabbedPane.addTab("Evaluator-Presentation Assignment", createEvaluatorPresentationPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Assignment Management");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        StyledButton refreshBtn = StyledButton.secondary("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        return headerPanel;
    }

    
    private JPanel createStudentSessionPanel() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Top panel with session selector and assign button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        topPanel.setOpaque(false);
        
        JLabel sessionLabel = new JLabel("Assign to Session:");
        sessionLabel.setFont(UIConstants.BODY);
        sessionLabel.setForeground(UIConstants.TEXT_SECONDARY);
        topPanel.add(sessionLabel);
        
        sessionCombo = new JComboBox<>();
        sessionCombo.setFont(UIConstants.BODY);
        sessionCombo.setPreferredSize(new Dimension(300, UIConstants.INPUT_HEIGHT));
        topPanel.add(sessionCombo);
        
        topPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        StyledButton assignBtn = StyledButton.primary("Assign Selected");
        assignBtn.addActionListener(e -> assignSelectedStudentToSession());
        topPanel.add(assignBtn);
        
        content.add(topPanel, BorderLayout.NORTH);
        
        // Unassigned students table
        String[] columns = {"ID", "Student Name", "Research Title", "Type", "Status"};
        unassignedStudentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        unassignedStudentsTable = new StyledTable(unassignedStudentsModel);
        unassignedStudentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(unassignedStudentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Unassigned/Pending Registrations"));
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createEvaluatorPresentationPanel() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Split pane for registrations and evaluators
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        
        // Left: Approved registrations
        JPanel registrationsPanel = new JPanel(new BorderLayout());
        registrationsPanel.setOpaque(false);
        
        String[] regColumns = {"ID", "Student", "Research Title", "Type"};
        registrationsModel = new DefaultTableModel(regColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registrationsTable = new StyledTable(registrationsModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane regScrollPane = new JScrollPane(registrationsTable);
        regScrollPane.setBorder(BorderFactory.createTitledBorder("Approved Registrations"));
        registrationsPanel.add(regScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(registrationsPanel);
        
        // Right: Evaluators
        JPanel evaluatorsPanel = new JPanel(new BorderLayout());
        evaluatorsPanel.setOpaque(false);
        
        String[] evalColumns = {"ID", "Name", "Department", "Expertise"};
        evaluatorsModel = new DefaultTableModel(evalColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluatorsTable = new StyledTable(evaluatorsModel);
        evaluatorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane evalScrollPane = new JScrollPane(evaluatorsTable);
        evalScrollPane.setBorder(BorderFactory.createTitledBorder("Evaluators"));
        evaluatorsPanel.add(evalScrollPane, BorderLayout.CENTER);
        
        splitPane.setRightComponent(evaluatorsPanel);
        
        content.add(splitPane, BorderLayout.CENTER);
        
        // Bottom: Assignment buttons and matrix
        JPanel bottomPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        bottomPanel.setOpaque(false);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton assignEvalBtn = StyledButton.primary("Assign Evaluator");
        assignEvalBtn.addActionListener(e -> assignEvaluatorToPresentation());
        buttonPanel.add(assignEvalBtn);
        
        StyledButton removeAssignBtn = StyledButton.danger("Remove Assignment");
        removeAssignBtn.addActionListener(e -> removeEvaluatorAssignment());
        buttonPanel.add(removeAssignBtn);
        
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Assignment matrix
        assignmentMatrixModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentMatrixTable = new StyledTable(assignmentMatrixModel);
        
        JScrollPane matrixScrollPane = new JScrollPane(assignmentMatrixTable);
        matrixScrollPane.setBorder(BorderFactory.createTitledBorder("Assignment Matrix"));
        matrixScrollPane.setPreferredSize(new Dimension(0, 200));
        
        bottomPanel.add(matrixScrollPane, BorderLayout.CENTER);
        
        content.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }

    
    private void loadData() {
        loadSessions();
        loadUnassignedStudents();
        loadApprovedRegistrations();
        loadEvaluators();
        loadAssignmentMatrix();
    }
    
    private void loadSessions() {
        sessionCombo.removeAllItems();
        List<Session> sessions = sessionService.getAvailableSessions();
        for (Session session : sessions) {
            sessionCombo.addItem(new SessionItem(session));
        }
    }
    
    private void loadUnassignedStudents() {
        unassignedStudentsModel.setRowCount(0);
        
        // Get pending and unassigned registrations
        List<Registration> registrations = registrationService.getAllRegistrations();
        for (Registration reg : registrations) {
            if (reg.getStatus() == RegistrationStatus.PENDING || 
                (reg.getStatus() == RegistrationStatus.APPROVED && reg.getSessionId() == null)) {
                
                String studentName = getStudentName(reg.getStudentId());
                unassignedStudentsModel.addRow(new Object[]{
                    reg.getId(),
                    studentName,
                    truncateText(reg.getResearchTitle(), 40),
                    reg.getPresentationType().name(),
                    reg.getStatus().name()
                });
            }
        }
    }
    
    private void loadApprovedRegistrations() {
        registrationsModel.setRowCount(0);
        
        List<Registration> registrations = registrationService.getApprovedRegistrations();
        for (Registration reg : registrations) {
            String studentName = getStudentName(reg.getStudentId());
            registrationsModel.addRow(new Object[]{
                reg.getId(),
                studentName,
                truncateText(reg.getResearchTitle(), 30),
                reg.getPresentationType().name()
            });
        }
    }
    
    private void loadEvaluators() {
        evaluatorsModel.setRowCount(0);
        
        List<Evaluator> evaluators = userRepository.findAllEvaluators();
        for (Evaluator evaluator : evaluators) {
            evaluatorsModel.addRow(new Object[]{
                evaluator.getId(),
                evaluator.getName(),
                evaluator.getDepartment(),
                evaluator.getExpertise()
            });
        }
    }
    
    private void loadAssignmentMatrix() {
        // Get all evaluators and approved registrations
        List<Evaluator> evaluators = userRepository.findAllEvaluators();
        List<Registration> registrations = registrationService.getApprovedRegistrations();
        
        // Build column names: first column is "Registration", then evaluator names
        String[] columns = new String[evaluators.size() + 1];
        columns[0] = "Registration";
        for (int i = 0; i < evaluators.size(); i++) {
            columns[i + 1] = evaluators.get(i).getName();
        }
        
        assignmentMatrixModel.setColumnIdentifiers(columns);
        assignmentMatrixModel.setRowCount(0);
        
        // Build rows: each row is a registration
        for (Registration reg : registrations) {
            Object[] row = new Object[evaluators.size() + 1];
            row[0] = truncateText(reg.getResearchTitle(), 25);
            
            for (int i = 0; i < evaluators.size(); i++) {
                boolean assigned = evaluationService.isAssigned(evaluators.get(i).getId(), reg.getId());
                row[i + 1] = assigned ? "✓" : "";
            }
            
            assignmentMatrixModel.addRow(row);
        }
    }
    
    private void assignSelectedStudentToSession() {
        int selectedRow = unassignedStudentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration to assign.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SessionItem selectedSession = (SessionItem) sessionCombo.getSelectedItem();
        if (selectedSession == null) {
            JOptionPane.showMessageDialog(this, "Please select a session.", 
                "No Session", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long registrationId = (Long) unassignedStudentsModel.getValueAt(selectedRow, 0);
        
        try {
            registrationService.assignToSession(registrationId, selectedSession.session.getId());
            JOptionPane.showMessageDialog(this, "Registration assigned successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void assignEvaluatorToPresentation() {
        int regRow = registrationsTable.getSelectedRow();
        int evalRow = evaluatorsTable.getSelectedRow();
        
        if (regRow < 0 || evalRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select both a registration and an evaluator.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long registrationId = (Long) registrationsModel.getValueAt(regRow, 0);
        Long evaluatorId = (Long) evaluatorsModel.getValueAt(evalRow, 0);
        
        try {
            evaluationService.assignEvaluator(evaluatorId, registrationId);
            JOptionPane.showMessageDialog(this, "Evaluator assigned successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssignmentMatrix();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeEvaluatorAssignment() {
        int regRow = registrationsTable.getSelectedRow();
        int evalRow = evaluatorsTable.getSelectedRow();
        
        if (regRow < 0 || evalRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select both a registration and an evaluator.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long registrationId = (Long) registrationsModel.getValueAt(regRow, 0);
        Long evaluatorId = (Long) evaluatorsModel.getValueAt(evalRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Remove this evaluator assignment?", 
            "Confirm Remove", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            evaluationService.removeAssignment(evaluatorId, registrationId);
            loadAssignmentMatrix();
        }
    }
    
    private String getStudentName(Long studentId) {
        if (studentId == null) return "Unknown";
        User user = userRepository.findById(studentId);
        if (user instanceof Student) {
            return ((Student) user).getName();
        }
        return "Unknown";
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    @Override
    public void refresh() {
        loadData();
    }
    
    /**
     * Helper class for session combo box items.
     */
    private static class SessionItem {
        final Session session;
        
        SessionItem(Session session) {
            this.session = session;
        }
        
        @Override
        public String toString() {
            return session.getDate().format(DATE_FORMATTER) + " - " + 
                   session.getVenue() + " (" + session.getType() + ") - " +
                   "Available: " + (session.getCapacity() - session.getRegistered());
        }
    }
}
