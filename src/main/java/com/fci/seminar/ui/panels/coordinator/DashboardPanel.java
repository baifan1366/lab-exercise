package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.RegistrationService;
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
 * Dashboard panel for coordinators.
 * Displays summary statistics, recent activities, and quick access to common actions.
 * Requirements: 6.1
 */
public class DashboardPanel extends JPanel implements MainFrame.Refreshable {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final SessionService sessionService;
    private final RegistrationService registrationService;
    private final EvaluationService evaluationService;
    
    // Statistics cards
    private JLabel totalSessionsLabel;
    private JLabel openSessionsLabel;
    private JLabel totalRegistrationsLabel;
    private JLabel pendingRegistrationsLabel;
    private JLabel totalEvaluationsLabel;
    private JLabel pendingEvaluationsLabel;
    
    // Recent activities panel
    private JPanel recentActivitiesPanel;
    
    // Quick action handler
    private QuickActionHandler quickActionHandler;
    
    /**
     * Creates the dashboard panel.
     */
    public DashboardPanel() {
        this.sessionService = SessionService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        this.evaluationService = EvaluationService.getInstance();
        initComponents();
        loadData();
    }

    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header with title
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content with statistics and activities
        JPanel mainContent = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        mainContent.setOpaque(false);
        
        // Statistics cards at top
        mainContent.add(createStatisticsPanel(), BorderLayout.NORTH);
        
        // Center area with recent activities and quick actions
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, UIConstants.SPACING_MD, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(createRecentActivitiesPanel());
        centerPanel.add(createQuickActionsPanel());
        
        mainContent.add(centerPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        StyledButton refreshButton = StyledButton.secondary("Refresh");
        refreshButton.addActionListener(e -> refresh());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, UIConstants.SPACING_MD, 0));
        statsPanel.setOpaque(false);
        
        // Sessions card
        CardPanel sessionsCard = createStatCard("Sessions", UIConstants.PRIMARY);
        JPanel sessionsContent = sessionsCard.getContentPanel();
        sessionsContent.setLayout(new BoxLayout(sessionsContent, BoxLayout.Y_AXIS));
        
        totalSessionsLabel = createStatValue("0");
        JLabel totalSessionsDesc = createStatDescription("Total Sessions");
        openSessionsLabel = createStatValue("0");
        JLabel openSessionsDesc = createStatDescription("Open Sessions");
        
        sessionsContent.add(totalSessionsLabel);
        sessionsContent.add(totalSessionsDesc);
        sessionsContent.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        sessionsContent.add(openSessionsLabel);
        sessionsContent.add(openSessionsDesc);
        
        statsPanel.add(sessionsCard);
        
        // Registrations card
        CardPanel registrationsCard = createStatCard("Registrations", UIConstants.SUCCESS);
        JPanel registrationsContent = registrationsCard.getContentPanel();
        registrationsContent.setLayout(new BoxLayout(registrationsContent, BoxLayout.Y_AXIS));
        
        totalRegistrationsLabel = createStatValue("0");
        JLabel totalRegistrationsDesc = createStatDescription("Total Registrations");
        pendingRegistrationsLabel = createStatValue("0");
        JLabel pendingRegistrationsDesc = createStatDescription("Pending Approval");
        
        registrationsContent.add(totalRegistrationsLabel);
        registrationsContent.add(totalRegistrationsDesc);
        registrationsContent.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        registrationsContent.add(pendingRegistrationsLabel);
        registrationsContent.add(pendingRegistrationsDesc);
        
        statsPanel.add(registrationsCard);
        
        // Evaluations card
        CardPanel evaluationsCard = createStatCard("Evaluations", UIConstants.INFO);
        JPanel evaluationsContent = evaluationsCard.getContentPanel();
        evaluationsContent.setLayout(new BoxLayout(evaluationsContent, BoxLayout.Y_AXIS));
        
        totalEvaluationsLabel = createStatValue("0");
        JLabel totalEvaluationsDesc = createStatDescription("Total Evaluations");
        pendingEvaluationsLabel = createStatValue("0");
        JLabel pendingEvaluationsDesc = createStatDescription("Pending Submission");
        
        evaluationsContent.add(totalEvaluationsLabel);
        evaluationsContent.add(totalEvaluationsDesc);
        evaluationsContent.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        evaluationsContent.add(pendingEvaluationsLabel);
        evaluationsContent.add(pendingEvaluationsDesc);
        
        statsPanel.add(evaluationsCard);
        
        return statsPanel;
    }
    
    private CardPanel createStatCard(String title, Color accentColor) {
        CardPanel card = new CardPanel(title);
        card.setPreferredSize(new Dimension(0, 180));
        return card;
    }
    
    private JLabel createStatValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(UIConstants.TITLE_LARGE);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JLabel createStatDescription(String description) {
        JLabel label = new JLabel(description);
        label.setFont(UIConstants.BODY);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    
    private JPanel createRecentActivitiesPanel() {
        CardPanel card = new CardPanel("Recent Activities");
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout());
        
        recentActivitiesPanel = new JPanel();
        recentActivitiesPanel.setLayout(new BoxLayout(recentActivitiesPanel, BoxLayout.Y_AXIS));
        recentActivitiesPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(recentActivitiesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createQuickActionsPanel() {
        CardPanel card = new CardPanel("Quick Actions");
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Create session button
        StyledButton createSessionBtn = StyledButton.primary("Create New Session");
        createSessionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        createSessionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createSessionBtn.addActionListener(e -> {
            if (quickActionHandler != null) {
                quickActionHandler.onQuickAction("CREATE_SESSION");
            }
        });
        content.add(createSessionBtn);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Manage registrations button
        StyledButton manageRegistrationsBtn = StyledButton.secondary("Manage Registrations");
        manageRegistrationsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        manageRegistrationsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        manageRegistrationsBtn.addActionListener(e -> {
            if (quickActionHandler != null) {
                quickActionHandler.onQuickAction("MANAGE_REGISTRATIONS");
            }
        });
        content.add(manageRegistrationsBtn);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Assign evaluators button
        StyledButton assignEvaluatorsBtn = StyledButton.secondary("Assign Evaluators");
        assignEvaluatorsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        assignEvaluatorsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        assignEvaluatorsBtn.addActionListener(e -> {
            if (quickActionHandler != null) {
                quickActionHandler.onQuickAction("ASSIGN_EVALUATORS");
            }
        });
        content.add(assignEvaluatorsBtn);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Calculate awards button
        StyledButton calculateAwardsBtn = StyledButton.success("Calculate Awards");
        calculateAwardsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        calculateAwardsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateAwardsBtn.addActionListener(e -> {
            if (quickActionHandler != null) {
                quickActionHandler.onQuickAction("CALCULATE_AWARDS");
            }
        });
        content.add(calculateAwardsBtn);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        // Generate reports button
        StyledButton generateReportsBtn = StyledButton.secondary("Generate Reports");
        generateReportsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        generateReportsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateReportsBtn.addActionListener(e -> {
            if (quickActionHandler != null) {
                quickActionHandler.onQuickAction("GENERATE_REPORTS");
            }
        });
        content.add(generateReportsBtn);
        
        content.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private void loadData() {
        // Load statistics
        loadStatistics();
        
        // Load recent activities
        loadRecentActivities();
    }
    
    private void loadStatistics() {
        // Sessions statistics
        long totalSessions = sessionService.countSessions();
        long openSessions = sessionService.countByStatus(SessionStatus.OPEN);
        totalSessionsLabel.setText(String.valueOf(totalSessions));
        openSessionsLabel.setText(String.valueOf(openSessions));
        
        // Registrations statistics
        long totalRegistrations = registrationService.countRegistrations();
        long pendingRegistrations = registrationService.countByStatus(RegistrationStatus.PENDING);
        totalRegistrationsLabel.setText(String.valueOf(totalRegistrations));
        pendingRegistrationsLabel.setText(String.valueOf(pendingRegistrations));
        
        // Evaluations statistics
        long totalEvaluations = evaluationService.countEvaluations();
        long pendingEvaluations = evaluationService.countPending();
        totalEvaluationsLabel.setText(String.valueOf(totalEvaluations));
        pendingEvaluationsLabel.setText(String.valueOf(pendingEvaluations));
    }
    
    private void loadRecentActivities() {
        recentActivitiesPanel.removeAll();
        
        // Get recent registrations
        List<Registration> recentRegistrations = registrationService.getAllRegistrations();
        
        // Get upcoming sessions
        List<Session> upcomingSessions = sessionService.getAllSessions();
        
        // Show recent registrations (last 5)
        int count = 0;
        for (Registration reg : recentRegistrations) {
            if (count >= 5) break;
            recentActivitiesPanel.add(createActivityItem(
                "New registration: " + truncateText(reg.getResearchTitle(), 30),
                reg.getStatus().name(),
                reg.getCreatedAt() != null ? reg.getCreatedAt().toLocalDate().format(DATE_FORMATTER) : "N/A"
            ));
            recentActivitiesPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
            count++;
        }
        
        // Show upcoming sessions (next 3)
        LocalDate today = LocalDate.now();
        count = 0;
        for (Session session : upcomingSessions) {
            if (count >= 3) break;
            if (session.getDate() != null && !session.getDate().isBefore(today)) {
                recentActivitiesPanel.add(createActivityItem(
                    "Session: " + session.getVenue() + " (" + session.getType() + ")",
                    session.getStatus().name(),
                    session.getDate().format(DATE_FORMATTER)
                ));
                recentActivitiesPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
                count++;
            }
        }
        
        if (recentActivitiesPanel.getComponentCount() == 0) {
            JLabel emptyLabel = new JLabel("No recent activities");
            emptyLabel.setFont(UIConstants.BODY);
            emptyLabel.setForeground(UIConstants.TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            recentActivitiesPanel.add(emptyLabel);
        }
        
        recentActivitiesPanel.add(Box.createVerticalGlue());
        recentActivitiesPanel.revalidate();
        recentActivitiesPanel.repaint();
    }
    
    private JPanel createActivityItem(String title, String status, String date) {
        RoundedPanel item = new RoundedPanel(UIConstants.RADIUS_SM, false);
        item.setLayout(new BorderLayout(UIConstants.SPACING_SM, 0));
        item.setBorder(new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_SM, 
                                       UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        item.setBackground(UIConstants.TABLE_ROW_ALT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Title and date
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.BODY);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        textPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(UIConstants.SMALL);
        dateLabel.setForeground(UIConstants.TEXT_MUTED);
        textPanel.add(dateLabel, BorderLayout.SOUTH);
        
        item.add(textPanel, BorderLayout.CENTER);
        
        // Status label
        StatusLabel statusLabel = StatusLabel.forRegistrationStatus(status);
        item.add(statusLabel, BorderLayout.EAST);
        
        return item;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Sets the quick action handler.
     * @param handler The handler for quick actions
     */
    public void setQuickActionHandler(QuickActionHandler handler) {
        this.quickActionHandler = handler;
    }
    
    @Override
    public void refresh() {
        loadData();
    }
    
    /**
     * Interface for handling quick action button clicks.
     */
    public interface QuickActionHandler {
        void onQuickAction(String action);
    }
}
