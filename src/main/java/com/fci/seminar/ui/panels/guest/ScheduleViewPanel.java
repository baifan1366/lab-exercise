package com.fci.seminar.ui.panels.guest;

import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.RoundedPanel;
import com.fci.seminar.ui.components.StatusLabel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing seminar schedule.
 * Displays sessions in a list/card view with filtering capabilities.
 * Available to all roles including Guest.
 * Requirements: 2.1, 2.2, 2.3, 2.4
 */
public class ScheduleViewPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private final SessionService sessionService;
    
    // Filter components
    private JComboBox<String> dateFilterCombo;
    private JComboBox<String> typeFilterCombo;
    private StyledButton refreshButton;
    
    // Session list panel
    private JPanel sessionListPanel;
    private JScrollPane scrollPane;
    
    // Detail panel
    private CardPanel detailPanel;
    private Session selectedSession;
    
    /**
     * Creates the schedule view panel.
     */
    public ScheduleViewPanel() {
        this.sessionService = SessionService.getInstance();
        initComponents();
        loadData();
    }

    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header with title and filters
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content area with session list and detail panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        
        // Left: Session list
        splitPane.setLeftComponent(createSessionListPanel());
        
        // Right: Session detail
        splitPane.setRightComponent(createDetailPanel());
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Seminar Schedule");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        filterPanel.setOpaque(false);
        
        // Date filter
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(UIConstants.BODY);
        dateLabel.setForeground(UIConstants.TEXT_SECONDARY);
        filterPanel.add(dateLabel);
        
        dateFilterCombo = new JComboBox<>();
        dateFilterCombo.setFont(UIConstants.BODY);
        dateFilterCombo.setPreferredSize(new Dimension(140, UIConstants.INPUT_HEIGHT));
        dateFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(dateFilterCombo);
        
        filterPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        // Type filter
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(UIConstants.BODY);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        filterPanel.add(typeLabel);
        
        typeFilterCombo = new JComboBox<>(new String[]{"All Types", "Oral", "Poster"});
        typeFilterCombo.setFont(UIConstants.BODY);
        typeFilterCombo.setPreferredSize(new Dimension(120, UIConstants.INPUT_HEIGHT));
        typeFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(typeFilterCombo);
        
        filterPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        // Refresh button
        refreshButton = StyledButton.secondary("Refresh");
        refreshButton.addActionListener(e -> refresh());
        filterPanel.add(refreshButton);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSessionListPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        
        sessionListPanel = new JPanel();
        sessionListPanel.setLayout(new BoxLayout(sessionListPanel, BoxLayout.Y_AXIS));
        sessionListPanel.setOpaque(false);
        
        scrollPane = new JScrollPane(sessionListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    private JPanel createDetailPanel() {
        detailPanel = new CardPanel("Session Details");
        detailPanel.setPreferredSize(new Dimension(350, 0));
        showNoSelectionMessage();
        return detailPanel;
    }

    
    private void loadData() {
        // Populate date filter with available dates
        populateDateFilter();
        
        // Load and display sessions
        displaySessions(sessionService.getAllSessions());
    }
    
    private void populateDateFilter() {
        dateFilterCombo.removeAllItems();
        dateFilterCombo.addItem("All Dates");
        
        List<Session> allSessions = sessionService.getAllSessions();
        allSessions.stream()
                .map(Session::getDate)
                .distinct()
                .sorted()
                .forEach(date -> dateFilterCombo.addItem(date.format(DATE_FORMATTER)));
    }
    
    private void applyFilters() {
        LocalDate dateFilter = null;
        SessionType typeFilter = null;
        
        // Parse date filter
        String selectedDate = (String) dateFilterCombo.getSelectedItem();
        if (selectedDate != null && !"All Dates".equals(selectedDate)) {
            try {
                dateFilter = LocalDate.parse(selectedDate, DATE_FORMATTER);
            } catch (Exception e) {
                // Invalid date format, ignore filter
            }
        }
        
        // Parse type filter
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        if (selectedType != null && !"All Types".equals(selectedType)) {
            try {
                typeFilter = SessionType.valueOf(selectedType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid type, ignore filter
            }
        }
        
        // Get filtered sessions
        List<Session> filteredSessions = sessionService.filterSessions(dateFilter, typeFilter);
        displaySessions(filteredSessions);
        
        // Revalidate and repaint to ensure UI updates
        sessionListPanel.revalidate();
        sessionListPanel.repaint();
    }
    
    private void displaySessions(List<Session> sessions) {
        sessionListPanel.removeAll();
        
        if (sessions.isEmpty()) {
            JLabel emptyLabel = new JLabel("No sessions found");
            emptyLabel.setFont(UIConstants.BODY);
            emptyLabel.setForeground(UIConstants.TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(UIConstants.SPACING_XL, 0, 0, 0));
            sessionListPanel.add(emptyLabel);
        } else {
            for (Session session : sessions) {
                sessionListPanel.add(createSessionCard(session));
                sessionListPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
            }
        }
        
        sessionListPanel.revalidate();
        sessionListPanel.repaint();
    }
    
    private JPanel createSessionCard(Session session) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, true);
        card.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_SM));
        card.setBorder(new EmptyBorder(
            UIConstants.SPACING_MD, UIConstants.SPACING_MD,
            UIConstants.SPACING_MD, UIConstants.SPACING_MD
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Highlight selected card
        if (selectedSession != null && selectedSession.getId().equals(session.getId())) {
            card.setBackground(new Color(240, 245, 255));
        }
        
        // Click handler
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectSession(session);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedSession == null || !selectedSession.getId().equals(session.getId())) {
                    card.setBackground(new Color(248, 250, 252));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedSession == null || !selectedSession.getId().equals(session.getId())) {
                    card.setBackground(Color.WHITE);
                }
            }
        });
        
        // Left: Session info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        // Date and time row
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        dateTimePanel.setOpaque(false);
        
        JLabel dateIcon = new JLabel("\uD83D\uDCC5"); // Calendar emoji
        dateIcon.setFont(UIConstants.BODY);
        dateTimePanel.add(dateIcon);
        
        String dateTimeText = session.getDate().format(DATE_FORMATTER) + "  " +
                session.getStartTime().format(TIME_FORMATTER) + " - " +
                session.getEndTime().format(TIME_FORMATTER);
        JLabel dateTimeLabel = new JLabel(dateTimeText);
        dateTimeLabel.setFont(UIConstants.BODY_BOLD);
        dateTimeLabel.setForeground(UIConstants.TEXT_PRIMARY);
        dateTimePanel.add(dateTimeLabel);
        
        infoPanel.add(dateTimePanel);
        
        // Venue row
        JPanel venuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        venuePanel.setOpaque(false);
        
        JLabel venueIcon = new JLabel("\uD83D\uDCCD"); // Pin emoji
        venueIcon.setFont(UIConstants.BODY);
        venuePanel.add(venueIcon);
        
        JLabel venueLabel = new JLabel(session.getVenue());
        venueLabel.setFont(UIConstants.BODY);
        venueLabel.setForeground(UIConstants.TEXT_SECONDARY);
        venuePanel.add(venueLabel);
        
        infoPanel.add(venuePanel);
        
        // Type and capacity row
        JPanel typeCapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        typeCapPanel.setOpaque(false);
        
        JLabel typeIcon = new JLabel(session.getType() == SessionType.ORAL ? "\uD83C\uDFA4" : "\uD83D\uDDBC"); // Mic or frame emoji
        typeIcon.setFont(UIConstants.BODY);
        typeCapPanel.add(typeIcon);
        
        JLabel typeLabel = new JLabel(session.getType() == SessionType.ORAL ? "Oral Presentation" : "Poster Presentation");
        typeLabel.setFont(UIConstants.BODY);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        typeCapPanel.add(typeLabel);
        
        typeCapPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        JLabel capacityIcon = new JLabel("\uD83D\uDC65"); // People emoji
        capacityIcon.setFont(UIConstants.BODY);
        typeCapPanel.add(capacityIcon);
        
        int remaining = session.getCapacity() - session.getRegistered();
        JLabel capacityLabel = new JLabel("Remaining: " + remaining + "/" + session.getCapacity());
        capacityLabel.setFont(UIConstants.BODY);
        capacityLabel.setForeground(remaining > 0 ? UIConstants.TEXT_SECONDARY : UIConstants.DANGER);
        typeCapPanel.add(capacityLabel);
        
        infoPanel.add(typeCapPanel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Right: Status label
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        statusPanel.setOpaque(false);
        
        StatusLabel statusLabel = StatusLabel.forSessionStatus(session.getStatus().name());
        statusPanel.add(statusLabel);
        
        card.add(statusPanel, BorderLayout.EAST);
        
        return card;
    }

    
    private void selectSession(Session session) {
        this.selectedSession = session;
        showSessionDetails(session);
        
        // Refresh list to update selection highlight
        applyFilters();
    }
    
    private void showNoSelectionMessage() {
        JPanel content = detailPanel.getContentPanel();
        content.removeAll();
        content.setLayout(new GridBagLayout());
        
        JLabel messageLabel = new JLabel("Select a session to view details");
        messageLabel.setFont(UIConstants.BODY);
        messageLabel.setForeground(UIConstants.TEXT_SECONDARY);
        content.add(messageLabel);
        
        content.revalidate();
        content.repaint();
    }
    
    private void showSessionDetails(Session session) {
        JPanel content = detailPanel.getContentPanel();
        content.removeAll();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Status badge
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        StatusLabel statusLabel = StatusLabel.forSessionStatus(session.getStatus().name());
        statusRow.add(statusLabel);
        content.add(statusRow);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Date and Time
        addDetailRow(content, "Date", session.getDate().format(DATE_FORMATTER));
        addDetailRow(content, "Time", session.getStartTime().format(TIME_FORMATTER) + 
                " - " + session.getEndTime().format(TIME_FORMATTER));
        
        // Venue
        addDetailRow(content, "Venue", session.getVenue());
        
        // Type
        String typeText = session.getType() == SessionType.ORAL ? "Oral Presentation" : "Poster Presentation";
        addDetailRow(content, "Type", typeText);
        
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(separator);
        
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Capacity info
        addDetailRow(content, "Capacity", String.valueOf(session.getCapacity()));
        addDetailRow(content, "Registered", String.valueOf(session.getRegistered()));
        
        int remaining = session.getCapacity() - session.getRegistered();
        JPanel remainingRow = createDetailRow("Available Slots", String.valueOf(remaining));
        JLabel valueLabel = (JLabel) ((JPanel) remainingRow.getComponent(1)).getComponent(0);
        valueLabel.setForeground(remaining > 0 ? UIConstants.SUCCESS : UIConstants.DANGER);
        valueLabel.setFont(UIConstants.BODY_BOLD);
        content.add(remainingRow);
        
        // Description if available
        if (session.getDescription() != null && !session.getDescription().isEmpty()) {
            content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
            
            JSeparator separator2 = new JSeparator();
            separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator2.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(separator2);
            
            content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
            
            JLabel descTitle = new JLabel("Description");
            descTitle.setFont(UIConstants.BODY_BOLD);
            descTitle.setForeground(UIConstants.TEXT_PRIMARY);
            descTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(descTitle);
            
            content.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
            
            JTextArea descText = new JTextArea(session.getDescription());
            descText.setFont(UIConstants.BODY);
            descText.setForeground(UIConstants.TEXT_SECONDARY);
            descText.setLineWrap(true);
            descText.setWrapStyleWord(true);
            descText.setEditable(false);
            descText.setOpaque(false);
            descText.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(descText);
        }
        
        // Add glue to push content to top
        content.add(Box.createVerticalGlue());
        
        content.revalidate();
        content.repaint();
    }
    
    private void addDetailRow(JPanel container, String label, String value) {
        container.add(createDetailRow(label, value));
        container.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_SM, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(UIConstants.BODY);
        labelComponent.setForeground(UIConstants.TEXT_SECONDARY);
        labelComponent.setPreferredSize(new Dimension(100, 20));
        row.add(labelComponent, BorderLayout.WEST);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(UIConstants.BODY);
        valueComponent.setForeground(UIConstants.TEXT_PRIMARY);
        valuePanel.add(valueComponent);
        
        row.add(valuePanel, BorderLayout.CENTER);
        
        return row;
    }
    
    /**
     * Refreshes the panel data.
     * Implementation of MainFrame.Refreshable interface.
     */
    @Override
    public void refresh() {
        selectedSession = null;
        populateDateFilter();
        applyFilters();
        showNoSelectionMessage();
    }
    
    /**
     * Gets the currently selected session.
     * @return The selected session, or null if none selected
     */
    public Session getSelectedSession() {
        return selectedSession;
    }
}
