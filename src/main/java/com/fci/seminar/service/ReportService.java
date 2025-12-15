package com.fci.seminar.service;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.repository.EvaluationRepository;
import com.fci.seminar.repository.RegistrationRepository;
import com.fci.seminar.repository.SessionRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating reports and analytics.
 * Handles report generation and data export.
 * Requirements: 9.1, 9.2, 9.3
 */
public class ReportService {
    private static ReportService instance;
    
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final EvaluationRepository evaluationRepository;
    
    /**
     * Private constructor for singleton pattern.
     */
    private ReportService() {
        this.sessionRepository = new SessionRepository();
        this.registrationRepository = new RegistrationRepository();
        this.evaluationRepository = new EvaluationRepository();
    }
    
    /**
     * Constructor for testing with custom repositories.
     */
    public ReportService(SessionRepository sessionRepository,
                         RegistrationRepository registrationRepository,
                         EvaluationRepository evaluationRepository) {
        this.sessionRepository = sessionRepository;
        this.registrationRepository = registrationRepository;
        this.evaluationRepository = evaluationRepository;
    }
    
    /**
     * Gets the singleton instance of ReportService.
     */
    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    
    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    // ==================== Registration Statistics ====================
    
    /**
     * Gets registration statistics.
     * Requirements: 9.1
     * @return map containing registration statistics
     */
    public Map<String, Object> getRegistrationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Registration> allRegistrations = registrationRepository.findAll();
        
        // Total registrations
        stats.put("totalRegistrations", allRegistrations.size());
        
        // By status
        stats.put("pendingCount", registrationRepository.countByStatus(RegistrationStatus.PENDING));
        stats.put("approvedCount", registrationRepository.countByStatus(RegistrationStatus.APPROVED));
        stats.put("rejectedCount", registrationRepository.countByStatus(RegistrationStatus.REJECTED));
        stats.put("cancelledCount", registrationRepository.countByStatus(RegistrationStatus.CANCELLED));
        
        // By presentation type
        long oralCount = allRegistrations.stream()
                .filter(r -> r.getPresentationType() == SessionType.ORAL)
                .count();
        long posterCount = allRegistrations.stream()
                .filter(r -> r.getPresentationType() == SessionType.POSTER)
                .count();
        stats.put("oralCount", oralCount);
        stats.put("posterCount", posterCount);
        
        // Assigned vs unassigned
        long assignedCount = allRegistrations.stream()
                .filter(r -> r.getSessionId() != null)
                .count();
        stats.put("assignedCount", assignedCount);
        stats.put("unassignedCount", allRegistrations.size() - assignedCount);
        
        return stats;
    }
    
    // ==================== Evaluation Summary ====================
    
    /**
     * Gets evaluation summary.
     * Requirements: 9.2
     * @return map containing evaluation summary
     */
    public Map<String, Object> getEvaluationSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        List<Evaluation> allEvaluations = evaluationRepository.findAll();
        
        // Total evaluations
        summary.put("totalEvaluations", allEvaluations.size());
        
        // Submitted vs pending
        long submittedCount = evaluationRepository.countSubmitted();
        long pendingCount = evaluationRepository.countPending();
        summary.put("submittedCount", submittedCount);
        summary.put("pendingCount", pendingCount);
        
        // Average scores (from submitted evaluations only)
        List<Evaluation> submitted = evaluationRepository.findAllSubmitted();
        if (!submitted.isEmpty()) {
            double avgTotal = submitted.stream()
                    .mapToInt(Evaluation::getTotalScore)
                    .average()
                    .orElse(0.0);
            double avgClarity = submitted.stream()
                    .mapToInt(Evaluation::getProblemClarity)
                    .average()
                    .orElse(0.0);
            double avgMethodology = submitted.stream()
                    .mapToInt(Evaluation::getMethodology)
                    .average()
                    .orElse(0.0);
            double avgResults = submitted.stream()
                    .mapToInt(Evaluation::getResults)
                    .average()
                    .orElse(0.0);
            double avgPresentation = submitted.stream()
                    .mapToInt(Evaluation::getPresentationQuality)
                    .average()
                    .orElse(0.0);
            
            summary.put("averageTotalScore", avgTotal);
            summary.put("averageProblemClarity", avgClarity);
            summary.put("averageMethodology", avgMethodology);
            summary.put("averageResults", avgResults);
            summary.put("averagePresentationQuality", avgPresentation);
        } else {
            summary.put("averageTotalScore", 0.0);
            summary.put("averageProblemClarity", 0.0);
            summary.put("averageMethodology", 0.0);
            summary.put("averageResults", 0.0);
            summary.put("averagePresentationQuality", 0.0);
        }
        
        // Score distribution
        int[] distribution = new int[5]; // 0-20, 21-40, 41-60, 61-80, 81-100
        for (Evaluation eval : submitted) {
            int score = eval.getTotalScore();
            if (score <= 20) distribution[0]++;
            else if (score <= 40) distribution[1]++;
            else if (score <= 60) distribution[2]++;
            else if (score <= 80) distribution[3]++;
            else distribution[4]++;
        }
        summary.put("scoreDistribution", distribution);
        
        return summary;
    }

    
    // ==================== Session Attendance ====================
    
    /**
     * Gets session attendance report.
     * Requirements: 9.2
     * @return map containing session attendance data
     */
    public Map<String, Object> getSessionAttendance() {
        Map<String, Object> attendance = new HashMap<>();
        
        List<Session> allSessions = sessionRepository.findAll();
        
        // Total sessions
        attendance.put("totalSessions", allSessions.size());
        
        // By status
        attendance.put("openSessions", sessionRepository.countByStatus(SessionStatus.OPEN));
        attendance.put("fullSessions", sessionRepository.countByStatus(SessionStatus.FULL));
        attendance.put("closedSessions", sessionRepository.countByStatus(SessionStatus.CLOSED));
        
        // By type
        attendance.put("oralSessions", sessionRepository.countByType(SessionType.ORAL));
        attendance.put("posterSessions", sessionRepository.countByType(SessionType.POSTER));
        
        // Capacity utilization
        int totalCapacity = allSessions.stream()
                .mapToInt(Session::getCapacity)
                .sum();
        int totalRegistered = allSessions.stream()
                .mapToInt(Session::getRegistered)
                .sum();
        
        attendance.put("totalCapacity", totalCapacity);
        attendance.put("totalRegistered", totalRegistered);
        
        if (totalCapacity > 0) {
            double utilizationRate = (double) totalRegistered / totalCapacity * 100;
            attendance.put("utilizationRate", utilizationRate);
        } else {
            attendance.put("utilizationRate", 0.0);
        }
        
        // Per-session details
        Map<Long, Map<String, Object>> sessionDetails = new HashMap<>();
        for (Session session : allSessions) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("date", session.getDate());
            detail.put("venue", session.getVenue());
            detail.put("type", session.getType());
            detail.put("capacity", session.getCapacity());
            detail.put("registered", session.getRegistered());
            detail.put("status", session.getStatus());
            detail.put("availableSlots", session.getCapacity() - session.getRegistered());
            sessionDetails.put(session.getId(), detail);
        }
        attendance.put("sessionDetails", sessionDetails);
        
        return attendance;
    }
    
    // ==================== CSV Export ====================
    
    /**
     * Exports data to CSV file.
     * Requirements: 9.3
     * @param reportType the type of report to export
     * @param filePath the output file path
     * @throws IOException if file writing fails
     */
    public void exportToCsv(String reportType, String filePath) throws IOException {
        if (reportType == null || filePath == null) {
            throw new IllegalArgumentException("Report type and file path cannot be null");
        }
        
        switch (reportType.toLowerCase()) {
            case "registrations":
                exportRegistrationsToCsv(filePath);
                break;
            case "evaluations":
                exportEvaluationsToCsv(filePath);
                break;
            case "sessions":
                exportSessionsToCsv(filePath);
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }
    
    /**
     * Exports registrations to CSV.
     */
    private void exportRegistrationsToCsv(String filePath) throws IOException {
        List<Registration> registrations = registrationRepository.findAll();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("ID,Student ID,Session ID,Research Title,Presentation Type,Status,Created At");
            
            // Data rows
            for (Registration r : registrations) {
                writer.printf("%d,%d,%s,\"%s\",%s,%s,%s%n",
                        r.getId(),
                        r.getStudentId(),
                        r.getSessionId() != null ? r.getSessionId() : "",
                        escapeCsv(r.getResearchTitle()),
                        r.getPresentationType(),
                        r.getStatus(),
                        r.getCreatedAt());
            }
        }
    }
    
    /**
     * Exports evaluations to CSV.
     */
    private void exportEvaluationsToCsv(String filePath) throws IOException {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("ID,Evaluator ID,Registration ID,Problem Clarity,Methodology,Results,Presentation Quality,Total Score,Submitted,Submitted At");
            
            // Data rows
            for (Evaluation e : evaluations) {
                writer.printf("%d,%d,%d,%d,%d,%d,%d,%d,%s,%s%n",
                        e.getId(),
                        e.getEvaluatorId(),
                        e.getRegistrationId(),
                        e.getProblemClarity(),
                        e.getMethodology(),
                        e.getResults(),
                        e.getPresentationQuality(),
                        e.getTotalScore(),
                        e.isSubmitted(),
                        e.getSubmittedAt() != null ? e.getSubmittedAt() : "");
            }
        }
    }
    
    /**
     * Exports sessions to CSV.
     */
    private void exportSessionsToCsv(String filePath) throws IOException {
        List<Session> sessions = sessionRepository.findAll();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("ID,Date,Start Time,End Time,Venue,Type,Capacity,Registered,Status");
            
            // Data rows
            for (Session s : sessions) {
                writer.printf("%d,%s,%s,%s,\"%s\",%s,%d,%d,%s%n",
                        s.getId(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        escapeCsv(s.getVenue()),
                        s.getType(),
                        s.getCapacity(),
                        s.getRegistered(),
                        s.getStatus());
            }
        }
    }
    
    /**
     * Escapes special characters for CSV.
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
