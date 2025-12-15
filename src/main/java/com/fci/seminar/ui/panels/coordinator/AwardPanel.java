package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.repository.UserRepository;
import com.fci.seminar.service.AwardService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.RoundedPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing and displaying awards.
 * Requirements: 8.1, 8.2, 8.3, 8.4
 */
public class AwardPanel extends JPanel implements MainFrame.Refreshable {
    
    private final AwardService awardService;
    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    
    // Award display panels
    private JPanel bestOralPanel;
    private JPanel bestPosterPanel;
    private JPanel peoplesChoicePanel;
    
    // Status label
    private JLabel statusLabel;
    
    /**
     * Creates the award panel.
     */
    public AwardPanel() {
        this.awardService = AwardService.getInstance();
        this.registrationService = RegistrationService.getInstance();
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
        
        // Main content with award cards
        add(createMainContent(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Award Management");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton calculateBtn = StyledButton.primary("Calculate Awards");
        calculateBtn.addActionListener(e -> calculateAwards());
        buttonPanel.add(calculateBtn);
        
        StyledButton refreshBtn = StyledButton.secondary("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        mainPanel.setOpaque(false);
        
        // Status label
        statusLabel = new JLabel("Click 'Calculate Awards' to compute winners based on evaluation scores.");
        statusLabel.setFont(UIConstants.BODY);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(0, 0, UIConstants.SPACING_MD, 0));
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        
        // Award cards grid
        JPanel awardsGrid = new JPanel(new GridLayout(1, 3, UIConstants.SPACING_MD, 0));
        awardsGrid.setOpaque(false);
        
        // Best Oral Award
        bestOralPanel = createAwardCard("Best Oral Presentation", "\uD83E\uDD47", UIConstants.SUCCESS);
        awardsGrid.add(bestOralPanel);
        
        // Best Poster Award
        bestPosterPanel = createAwardCard("Best Poster Presentation", "\uD83E\uDD48", UIConstants.INFO);
        awardsGrid.add(bestPosterPanel);
        
        // People's Choice Award
        peoplesChoicePanel = createAwardCard("People's Choice Award", "\uD83C\uDFC6", UIConstants.WARNING);
        awardsGrid.add(peoplesChoicePanel);
        
        mainPanel.add(awardsGrid, BorderLayout.CENTER);
        
        return mainPanel;
    }

    
    private JPanel createAwardCard(String title, String emoji, Color accentColor) {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Header with emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_SM, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        headerPanel.add(emojiLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.TITLE_SMALL);
        titleLabel.setForeground(accentColor);
        headerPanel.add(titleLabel);
        
        content.add(headerPanel);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(separator);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Winner info placeholder
        JLabel noWinnerLabel = new JLabel("No winner calculated yet");
        noWinnerLabel.setFont(UIConstants.BODY);
        noWinnerLabel.setForeground(UIConstants.TEXT_MUTED);
        noWinnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noWinnerLabel);
        
        content.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private void loadData() {
        // Display existing awards if any
        displayAwards();
    }
    
    private void calculateAwards() {
        try {
            List<Award> awards = awardService.calculateAwards();
            
            if (awards.isEmpty()) {
                statusLabel.setText("No awards could be calculated. Ensure there are approved registrations with evaluations.");
                statusLabel.setForeground(UIConstants.WARNING);
            } else {
                statusLabel.setText("Awards calculated successfully! " + awards.size() + " award(s) determined.");
                statusLabel.setForeground(UIConstants.SUCCESS);
            }
            
            displayAwards();
            
        } catch (Exception e) {
            statusLabel.setText("Error calculating awards: " + e.getMessage());
            statusLabel.setForeground(UIConstants.DANGER);
        }
    }
    
    private void displayAwards() {
        // Display Best Oral
        List<Award> bestOral = awardService.getBestOral();
        updateAwardCard(bestOralPanel, bestOral, "Best Oral Presentation", "\uD83E\uDD47", UIConstants.SUCCESS);
        
        // Display Best Poster
        List<Award> bestPoster = awardService.getBestPoster();
        updateAwardCard(bestPosterPanel, bestPoster, "Best Poster Presentation", "\uD83E\uDD48", UIConstants.INFO);
        
        // Display People's Choice
        List<Award> peoplesChoice = awardService.getPeoplesChoice();
        updateAwardCard(peoplesChoicePanel, peoplesChoice, "People's Choice Award", "\uD83C\uDFC6", UIConstants.WARNING);
    }
    
    private void updateAwardCard(JPanel cardPanel, List<Award> awards, String title, String emoji, Color accentColor) {
        if (!(cardPanel instanceof CardPanel)) return;
        
        CardPanel card = (CardPanel) cardPanel;
        JPanel content = card.getContentPanel();
        content.removeAll();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Header with emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_SM, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        headerPanel.add(emojiLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.TITLE_SMALL);
        titleLabel.setForeground(accentColor);
        headerPanel.add(titleLabel);
        
        content.add(headerPanel);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(separator);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        if (awards == null || awards.isEmpty()) {
            JLabel noWinnerLabel = new JLabel("No winner calculated yet");
            noWinnerLabel.setFont(UIConstants.BODY);
            noWinnerLabel.setForeground(UIConstants.TEXT_MUTED);
            noWinnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(noWinnerLabel);
        } else {
            // Check for ties
            if (awards.size() > 1) {
                JLabel tieLabel = new JLabel("TIE - " + awards.size() + " Co-Winners!");
                tieLabel.setFont(UIConstants.BODY_BOLD);
                tieLabel.setForeground(UIConstants.WARNING);
                tieLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(tieLabel);
                content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
            }
            
            // Display each winner
            for (Award award : awards) {
                content.add(createWinnerPanel(award));
                content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
            }
        }
        
        content.add(Box.createVerticalGlue());
        content.revalidate();
        content.repaint();
    }
    
    private JPanel createWinnerPanel(Award award) {
        RoundedPanel panel = new RoundedPanel(UIConstants.RADIUS_SM, false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_SM, 
                                        UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        panel.setBackground(UIConstants.TABLE_ROW_ALT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Get registration and student info
        Registration registration = registrationService.getRegistrationById(award.getRegistrationId());
        String studentName = "Unknown";
        String researchTitle = "Unknown";
        
        if (registration != null) {
            researchTitle = registration.getResearchTitle();
            User user = userRepository.findById(registration.getStudentId());
            if (user instanceof Student) {
                studentName = ((Student) user).getName();
            }
        }
        
        // Winner name
        JLabel nameLabel = new JLabel("\uD83D\uDC64 " + studentName);
        nameLabel.setFont(UIConstants.BODY_BOLD);
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        
        // Research title
        JLabel titleLabel = new JLabel(truncateText(researchTitle, 35));
        titleLabel.setFont(UIConstants.BODY);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        // Score
        JLabel scoreLabel = new JLabel(String.format("Score: %.1f / 100", award.getScore()));
        scoreLabel.setFont(UIConstants.SMALL);
        scoreLabel.setForeground(UIConstants.SUCCESS);
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scoreLabel);
        
        return panel;
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
}
