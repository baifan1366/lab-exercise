package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StatusLabel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTable;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for managing seminar sessions.
 * Provides CRUD operations and status management.
 * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6
 */
public class SessionManagementPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private final SessionService sessionService;
    
    // Table components
    private StyledTable sessionsTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> typeFilterCombo;
    
    /**
     * Creates the session management panel.
     */
    public SessionManagementPanel() {
        this.sessionService = SessionService.getInstance();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content
        add(createMainContent(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Session Management");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add session button
        StyledButton addBtn = StyledButton.primary("Create Session");
        addBtn.addActionListener(e -> showSessionDialog(null));
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        return headerPanel;
    }

    
    private JPanel createMainContent() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Toolbar with filters and actions
        JPanel toolbar = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        toolbar.setOpaque(false);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        filterPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.BODY);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        filterPanel.add(statusLabel);
        
        statusFilterCombo = new JComboBox<>(new String[]{"All Status", "OPEN", "FULL", "CLOSED", "REQUIRES_APPROVAL"});
        statusFilterCombo.setFont(UIConstants.BODY);
        statusFilterCombo.setPreferredSize(new Dimension(140, UIConstants.INPUT_HEIGHT));
        statusFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(statusFilterCombo);
        
        filterPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(UIConstants.BODY);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        filterPanel.add(typeLabel);
        
        typeFilterCombo = new JComboBox<>(new String[]{"All Types", "ORAL", "POSTER"});
        typeFilterCombo.setFont(UIConstants.BODY);
        typeFilterCombo.setPreferredSize(new Dimension(120, UIConstants.INPUT_HEIGHT));
        typeFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(typeFilterCombo);
        
        toolbar.add(filterPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton editBtn = StyledButton.secondary("Edit");
        editBtn.addActionListener(e -> editSelectedSession());
        buttonPanel.add(editBtn);
        
        StyledButton statusBtn = StyledButton.secondary("Change Status");
        statusBtn.addActionListener(e -> changeSelectedSessionStatus());
        buttonPanel.add(statusBtn);
        
        StyledButton deleteBtn = StyledButton.danger("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedSession());
        buttonPanel.add(deleteBtn);
        
        StyledButton refreshBtn = StyledButton.secondary("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);
        
        toolbar.add(buttonPanel, BorderLayout.EAST);
        
        content.add(toolbar, BorderLayout.NORTH);
        
        // Sessions table
        String[] columns = {"ID", "Date", "Time", "Venue", "Type", "Capacity", "Registered", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sessionsTable = new StyledTable(tableModel);
        sessionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom renderer for status column
        sessionsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        
        // Set column widths
        sessionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        sessionsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        sessionsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        sessionsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        sessionsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        sessionsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        sessionsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        sessionsTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(sessionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Session> sessions = sessionService.getAllSessions();
        for (Session session : sessions) {
            addSessionToTable(session);
        }
    }
    
    private void applyFilters() {
        tableModel.setRowCount(0);
        
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        
        SessionStatus status = null;
        SessionType type = null;
        
        if (statusFilter != null && !"All Status".equals(statusFilter)) {
            status = SessionStatus.valueOf(statusFilter);
        }
        if (typeFilter != null && !"All Types".equals(typeFilter)) {
            type = SessionType.valueOf(typeFilter);
        }
        
        List<Session> sessions = sessionService.getAllSessions();
        for (Session session : sessions) {
            boolean matchesStatus = status == null || session.getStatus() == status;
            boolean matchesType = type == null || session.getType() == type;
            
            if (matchesStatus && matchesType) {
                addSessionToTable(session);
            }
        }
    }
    
    private void addSessionToTable(Session session) {
        String timeRange = session.getStartTime().format(TIME_FORMATTER) + " - " + 
                          session.getEndTime().format(TIME_FORMATTER);
        tableModel.addRow(new Object[]{
            session.getId(),
            session.getDate().format(DATE_FORMATTER),
            timeRange,
            session.getVenue(),
            session.getType().name(),
            session.getCapacity(),
            session.getRegistered(),
            session.getStatus().name()
        });
    }

    
    private void showSessionDialog(Session session) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            session == null ? "Create Session" : "Edit Session", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_LG, UIConstants.SPACING_LG, 
                                                        UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_XS, UIConstants.SPACING_XS, 
                                UIConstants.SPACING_XS, UIConstants.SPACING_XS);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        JTextField dateField = new JTextField(session != null ? session.getDate().format(DATE_FORMATTER) : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        // Start Time
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Start Time (HH:mm):"), gbc);
        JTextField startTimeField = new JTextField(session != null ? session.getStartTime().format(TIME_FORMATTER) : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(startTimeField, gbc);
        
        // End Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("End Time (HH:mm):"), gbc);
        JTextField endTimeField = new JTextField(session != null ? session.getEndTime().format(TIME_FORMATTER) : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(endTimeField, gbc);
        
        // Venue
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Venue:"), gbc);
        JTextField venueField = new JTextField(session != null ? session.getVenue() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(venueField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Type:"), gbc);
        JComboBox<SessionType> typeCombo = new JComboBox<>(SessionType.values());
        if (session != null) {
            typeCombo.setSelectedItem(session.getType());
        }
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(typeCombo, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Capacity:"), gbc);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(
            session != null ? session.getCapacity() : 20, 1, 1000, 1));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(capacitySpinner, gbc);
        
        // Status (only for edit)
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Status:"), gbc);
        JComboBox<SessionStatus> statusCombo = new JComboBox<>(SessionStatus.values());
        if (session != null) {
            statusCombo.setSelectedItem(session.getStatus());
        }
        statusCombo.setEnabled(session != null);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(statusCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        JTextArea descriptionArea = new JTextArea(session != null ? session.getDescription() : "", 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(descScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton cancelBtn = StyledButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        
        StyledButton saveBtn = StyledButton.primary("Save");
        saveBtn.addActionListener(e -> {
            try {
                Session s = session != null ? session : new Session();
                s.setDate(LocalDate.parse(dateField.getText().trim(), DATE_FORMATTER));
                s.setStartTime(LocalTime.parse(startTimeField.getText().trim(), TIME_FORMATTER));
                s.setEndTime(LocalTime.parse(endTimeField.getText().trim(), TIME_FORMATTER));
                s.setVenue(venueField.getText().trim());
                s.setType((SessionType) typeCombo.getSelectedItem());
                s.setCapacity((Integer) capacitySpinner.getValue());
                s.setStatus((SessionStatus) statusCombo.getSelectedItem());
                s.setDescription(descriptionArea.getText().trim());
                
                if (session == null) {
                    sessionService.createSession(s);
                } else {
                    sessionService.updateSession(s);
                }
                
                loadData();
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void editSelectedSession() {
        int selectedRow = sessionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Session session = sessionService.getSessionById(id);
        if (session != null) {
            showSessionDialog(session);
        }
    }
    
    private void changeSelectedSessionStatus() {
        int selectedRow = sessionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Session session = sessionService.getSessionById(id);
        if (session == null) return;
        
        SessionStatus newStatus = (SessionStatus) JOptionPane.showInputDialog(
            this, "Select new status:", "Change Status",
            JOptionPane.QUESTION_MESSAGE, null, SessionStatus.values(), session.getStatus());
        
        if (newStatus != null && newStatus != session.getStatus()) {
            sessionService.updateSessionStatus(id, newStatus);
            loadData();
        }
    }
    
    private void deleteSelectedSession() {
        int selectedRow = sessionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        
        // Check if session has registrations
        if (sessionService.hasRegistrations(id)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "This session has existing registrations. Are you sure you want to delete it?", 
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                sessionService.deleteSessionWithConfirmation(id, true);
                loadData();
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this session?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                sessionService.deleteSession(id);
                loadData();
            }
        }
    }
    
    @Override
    public void refresh() {
        loadData();
    }
    
    /**
     * Custom cell renderer for status column.
     */
    private static class StatusCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String status = value != null ? value.toString() : "";
            StatusLabel label = StatusLabel.forSessionStatus(status);
            
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        }
    }
}
