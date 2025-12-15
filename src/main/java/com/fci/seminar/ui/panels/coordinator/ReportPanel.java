package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.service.AwardService;
import com.fci.seminar.service.ReportService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTable;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Panel for generating reports and exporting data.
 * Requirements: 9.1, 9.2, 9.3, 9.4
 */
public class ReportPanel extends JPanel implements MainFrame.Refreshable {
    
    private final ReportService reportService;
    private final AwardService awardService;
    
    // Report type selector
    private JComboBox<String> reportTypeCombo;
    
    // Report display area
    private JPanel reportDisplayPanel;
    private StyledTable reportTable;
    private DefaultTableModel tableModel;
    private JTextArea summaryArea;
    
    // Current report type
    private String currentReportType;
    
    /**
     * Creates the report panel.
     */
    public ReportPanel() {
        this.reportService = ReportService.getInstance();
        this.awardService = AwardService.getInstance();
        initComponents();
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
        
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        mainPanel.setOpaque(false);
        
        // Top: Report type selection and actions
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);
        
        // Center: Report display
        mainPanel.add(createReportDisplayPanel(), BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createControlPanel() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, 0));
        
        // Report type selector
        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(UIConstants.BODY);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        content.add(typeLabel);
        
        reportTypeCombo = new JComboBox<>(new String[]{
            "Registration Statistics",
            "Evaluation Summary",
            "Session Attendance",
            "Award Results"
        });
        reportTypeCombo.setFont(UIConstants.BODY);
        reportTypeCombo.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        content.add(reportTypeCombo);
        
        content.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        // Generate button
        StyledButton generateBtn = StyledButton.primary("Generate Report");
        generateBtn.addActionListener(e -> generateReport());
        content.add(generateBtn);
        
        content.add(Box.createHorizontalStrut(UIConstants.SPACING_MD));
        
        // Export button
        StyledButton exportBtn = StyledButton.secondary("Export to CSV");
        exportBtn.addActionListener(e -> exportToCsv());
        content.add(exportBtn);
        
        // Print button
        StyledButton printBtn = StyledButton.secondary("Print Preview");
        printBtn.addActionListener(e -> printPreview());
        content.add(printBtn);
        
        return card;
    }

    
    private JPanel createReportDisplayPanel() {
        CardPanel card = new CardPanel("Report Data");
        reportDisplayPanel = card.getContentPanel();
        reportDisplayPanel.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Summary area at top
        summaryArea = new JTextArea(5, 40);
        summaryArea.setFont(UIConstants.BODY);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText("Select a report type and click 'Generate Report' to view data.");
        
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setBorder(BorderFactory.createTitledBorder("Summary"));
        reportDisplayPanel.add(summaryScroll, BorderLayout.NORTH);
        
        // Table for detailed data
        tableModel = new DefaultTableModel();
        reportTable = new StyledTable(tableModel);
        
        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Details"));
        reportDisplayPanel.add(tableScroll, BorderLayout.CENTER);
        
        return card;
    }
    
    private void generateReport() {
        currentReportType = (String) reportTypeCombo.getSelectedItem();
        
        if (currentReportType == null) {
            return;
        }
        
        switch (currentReportType) {
            case "Registration Statistics":
                generateRegistrationStatistics();
                break;
            case "Evaluation Summary":
                generateEvaluationSummary();
                break;
            case "Session Attendance":
                generateSessionAttendance();
                break;
            case "Award Results":
                generateAwardResults();
                break;
        }
    }
    
    private void generateRegistrationStatistics() {
        Map<String, Object> stats = reportService.getRegistrationStatistics();
        
        // Build summary
        StringBuilder summary = new StringBuilder();
        summary.append("=== Registration Statistics Report ===\n\n");
        summary.append("Total Registrations: ").append(stats.get("totalRegistrations")).append("\n\n");
        summary.append("By Status:\n");
        summary.append("  - Pending: ").append(stats.get("pendingCount")).append("\n");
        summary.append("  - Approved: ").append(stats.get("approvedCount")).append("\n");
        summary.append("  - Rejected: ").append(stats.get("rejectedCount")).append("\n");
        summary.append("  - Cancelled: ").append(stats.get("cancelledCount")).append("\n\n");
        summary.append("By Presentation Type:\n");
        summary.append("  - Oral: ").append(stats.get("oralCount")).append("\n");
        summary.append("  - Poster: ").append(stats.get("posterCount")).append("\n\n");
        summary.append("Assignment Status:\n");
        summary.append("  - Assigned: ").append(stats.get("assignedCount")).append("\n");
        summary.append("  - Unassigned: ").append(stats.get("unassignedCount")).append("\n");
        
        summaryArea.setText(summary.toString());
        
        // Build table
        String[] columns = {"Metric", "Value"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        tableModel.addRow(new Object[]{"Total Registrations", stats.get("totalRegistrations")});
        tableModel.addRow(new Object[]{"Pending", stats.get("pendingCount")});
        tableModel.addRow(new Object[]{"Approved", stats.get("approvedCount")});
        tableModel.addRow(new Object[]{"Rejected", stats.get("rejectedCount")});
        tableModel.addRow(new Object[]{"Cancelled", stats.get("cancelledCount")});
        tableModel.addRow(new Object[]{"Oral Presentations", stats.get("oralCount")});
        tableModel.addRow(new Object[]{"Poster Presentations", stats.get("posterCount")});
        tableModel.addRow(new Object[]{"Assigned to Sessions", stats.get("assignedCount")});
        tableModel.addRow(new Object[]{"Unassigned", stats.get("unassignedCount")});
    }
    
    private void generateEvaluationSummary() {
        Map<String, Object> summary = reportService.getEvaluationSummary();
        
        // Build summary text
        StringBuilder sb = new StringBuilder();
        sb.append("=== Evaluation Summary Report ===\n\n");
        sb.append("Total Evaluations: ").append(summary.get("totalEvaluations")).append("\n");
        sb.append("Submitted: ").append(summary.get("submittedCount")).append("\n");
        sb.append("Pending: ").append(summary.get("pendingCount")).append("\n\n");
        sb.append("Average Scores:\n");
        sb.append(String.format("  - Total Score: %.2f / 100\n", summary.get("averageTotalScore")));
        sb.append(String.format("  - Problem Clarity: %.2f / 25\n", summary.get("averageProblemClarity")));
        sb.append(String.format("  - Methodology: %.2f / 25\n", summary.get("averageMethodology")));
        sb.append(String.format("  - Results: %.2f / 25\n", summary.get("averageResults")));
        sb.append(String.format("  - Presentation Quality: %.2f / 25\n", summary.get("averagePresentationQuality")));
        
        // Score distribution
        int[] distribution = (int[]) summary.get("scoreDistribution");
        if (distribution != null) {
            sb.append("\nScore Distribution:\n");
            sb.append("  - 0-20: ").append(distribution[0]).append("\n");
            sb.append("  - 21-40: ").append(distribution[1]).append("\n");
            sb.append("  - 41-60: ").append(distribution[2]).append("\n");
            sb.append("  - 61-80: ").append(distribution[3]).append("\n");
            sb.append("  - 81-100: ").append(distribution[4]).append("\n");
        }
        
        summaryArea.setText(sb.toString());
        
        // Build table
        String[] columns = {"Metric", "Value"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        tableModel.addRow(new Object[]{"Total Evaluations", summary.get("totalEvaluations")});
        tableModel.addRow(new Object[]{"Submitted", summary.get("submittedCount")});
        tableModel.addRow(new Object[]{"Pending", summary.get("pendingCount")});
        tableModel.addRow(new Object[]{"Avg Total Score", String.format("%.2f", summary.get("averageTotalScore"))});
        tableModel.addRow(new Object[]{"Avg Problem Clarity", String.format("%.2f", summary.get("averageProblemClarity"))});
        tableModel.addRow(new Object[]{"Avg Methodology", String.format("%.2f", summary.get("averageMethodology"))});
        tableModel.addRow(new Object[]{"Avg Results", String.format("%.2f", summary.get("averageResults"))});
        tableModel.addRow(new Object[]{"Avg Presentation Quality", String.format("%.2f", summary.get("averagePresentationQuality"))});
    }
    
    private void generateSessionAttendance() {
        Map<String, Object> attendance = reportService.getSessionAttendance();
        
        // Build summary text
        StringBuilder sb = new StringBuilder();
        sb.append("=== Session Attendance Report ===\n\n");
        sb.append("Total Sessions: ").append(attendance.get("totalSessions")).append("\n\n");
        sb.append("By Status:\n");
        sb.append("  - Open: ").append(attendance.get("openSessions")).append("\n");
        sb.append("  - Full: ").append(attendance.get("fullSessions")).append("\n");
        sb.append("  - Closed: ").append(attendance.get("closedSessions")).append("\n\n");
        sb.append("By Type:\n");
        sb.append("  - Oral: ").append(attendance.get("oralSessions")).append("\n");
        sb.append("  - Poster: ").append(attendance.get("posterSessions")).append("\n\n");
        sb.append("Capacity Utilization:\n");
        sb.append("  - Total Capacity: ").append(attendance.get("totalCapacity")).append("\n");
        sb.append("  - Total Registered: ").append(attendance.get("totalRegistered")).append("\n");
        sb.append(String.format("  - Utilization Rate: %.1f%%\n", attendance.get("utilizationRate")));
        
        summaryArea.setText(sb.toString());
        
        // Build table
        String[] columns = {"Metric", "Value"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        tableModel.addRow(new Object[]{"Total Sessions", attendance.get("totalSessions")});
        tableModel.addRow(new Object[]{"Open Sessions", attendance.get("openSessions")});
        tableModel.addRow(new Object[]{"Full Sessions", attendance.get("fullSessions")});
        tableModel.addRow(new Object[]{"Closed Sessions", attendance.get("closedSessions")});
        tableModel.addRow(new Object[]{"Oral Sessions", attendance.get("oralSessions")});
        tableModel.addRow(new Object[]{"Poster Sessions", attendance.get("posterSessions")});
        tableModel.addRow(new Object[]{"Total Capacity", attendance.get("totalCapacity")});
        tableModel.addRow(new Object[]{"Total Registered", attendance.get("totalRegistered")});
        tableModel.addRow(new Object[]{"Utilization Rate", String.format("%.1f%%", attendance.get("utilizationRate"))});
    }
    
    private void generateAwardResults() {
        // Calculate awards first
        awardService.calculateAwards();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Award Results Report ===\n\n");
        
        // Best Oral
        sb.append("Best Oral Presentation:\n");
        var bestOral = awardService.getBestOral();
        if (bestOral.isEmpty()) {
            sb.append("  No winner determined\n");
        } else {
            for (var award : bestOral) {
                sb.append(String.format("  - Registration ID: %d, Score: %.1f\n", 
                    award.getRegistrationId(), award.getScore()));
            }
        }
        
        sb.append("\nBest Poster Presentation:\n");
        var bestPoster = awardService.getBestPoster();
        if (bestPoster.isEmpty()) {
            sb.append("  No winner determined\n");
        } else {
            for (var award : bestPoster) {
                sb.append(String.format("  - Registration ID: %d, Score: %.1f\n", 
                    award.getRegistrationId(), award.getScore()));
            }
        }
        
        sb.append("\nPeople's Choice Award:\n");
        var peoplesChoice = awardService.getPeoplesChoice();
        if (peoplesChoice.isEmpty()) {
            sb.append("  No winner determined\n");
        } else {
            for (var award : peoplesChoice) {
                sb.append(String.format("  - Registration ID: %d, Score: %.1f\n", 
                    award.getRegistrationId(), award.getScore()));
            }
        }
        
        summaryArea.setText(sb.toString());
        
        // Build table
        String[] columns = {"Award Type", "Registration ID", "Score"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        for (var award : bestOral) {
            tableModel.addRow(new Object[]{"Best Oral", award.getRegistrationId(), 
                String.format("%.1f", award.getScore())});
        }
        for (var award : bestPoster) {
            tableModel.addRow(new Object[]{"Best Poster", award.getRegistrationId(), 
                String.format("%.1f", award.getScore())});
        }
        for (var award : peoplesChoice) {
            tableModel.addRow(new Object[]{"People's Choice", award.getRegistrationId(), 
                String.format("%.1f", award.getScore())});
        }
    }

    
    private void exportToCsv() {
        if (currentReportType == null) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.", 
                "No Report", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setSelectedFile(new File(getDefaultFileName() + ".csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            try {
                String reportType = getReportTypeForExport();
                reportService.exportToCsv(reportType, filePath);
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + filePath, 
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Cannot export this report type to CSV.", 
                    "Export Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private String getReportTypeForExport() {
        switch (currentReportType) {
            case "Registration Statistics":
                return "registrations";
            case "Evaluation Summary":
                return "evaluations";
            case "Session Attendance":
                return "sessions";
            default:
                throw new IllegalArgumentException("Cannot export this report type");
        }
    }
    
    private String getDefaultFileName() {
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return currentReportType.replace(" ", "_").toLowerCase() + "_" + timestamp;
    }
    
    private void printPreview() {
        if (currentReportType == null) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.", 
                "No Report", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a printable text area with the summary
        JTextArea printArea = new JTextArea(summaryArea.getText());
        printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        try {
            boolean complete = printArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Print job completed.", 
                    "Print", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Print job cancelled.", 
                    "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage(), 
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void refresh() {
        // Clear current report
        summaryArea.setText("Select a report type and click 'Generate Report' to view data.");
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        currentReportType = null;
    }
}
