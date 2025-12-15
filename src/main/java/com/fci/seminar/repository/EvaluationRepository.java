package com.fci.seminar.repository;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.util.DataManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Evaluation entity operations.
 * Handles CRUD operations and filtering for evaluations.
 * Requirements: 5.1, 5.4
 */
public class EvaluationRepository {
    private final DataManager dataManager;
    
    public EvaluationRepository() {
        this.dataManager = DataManager.getInstance();
    }
    
    /**
     * Constructor for testing with custom DataManager.
     */
    public EvaluationRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ==================== Find Operations ====================
    
    /**
     * Finds an evaluation by ID.
     */
    public Evaluation findById(Long id) {
        if (id == null) return null;
        
        for (Evaluation evaluation : dataManager.getEvaluations()) {
            if (id.equals(evaluation.getId())) {
                return evaluation;
            }
        }
        return null;
    }
    
    /**
     * Finds all evaluations.
     */
    public List<Evaluation> findAll() {
        return dataManager.getEvaluations();
    }
    
    /**
     * Finds evaluations by evaluator ID.
     * Requirements: 5.1
     */
    public List<Evaluation> findByEvaluatorId(Long evaluatorId) {
        if (evaluatorId == null) return new ArrayList<>();
        
        return dataManager.getEvaluations().stream()
                .filter(e -> evaluatorId.equals(e.getEvaluatorId()))
                .collect(Collectors.toList());
    }

    
    /**
     * Finds evaluations by registration ID.
     * Requirements: 5.4
     */
    public List<Evaluation> findByRegistrationId(Long registrationId) {
        if (registrationId == null) return new ArrayList<>();
        
        return dataManager.getEvaluations().stream()
                .filter(e -> registrationId.equals(e.getRegistrationId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds an evaluation by evaluator ID and registration ID.
     */
    public Evaluation findByEvaluatorIdAndRegistrationId(Long evaluatorId, Long registrationId) {
        if (evaluatorId == null || registrationId == null) return null;
        
        return dataManager.getEvaluations().stream()
                .filter(e -> evaluatorId.equals(e.getEvaluatorId()) && 
                            registrationId.equals(e.getRegistrationId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Finds submitted evaluations by evaluator ID.
     */
    public List<Evaluation> findSubmittedByEvaluatorId(Long evaluatorId) {
        if (evaluatorId == null) return new ArrayList<>();
        
        return dataManager.getEvaluations().stream()
                .filter(e -> evaluatorId.equals(e.getEvaluatorId()) && e.isSubmitted())
                .collect(Collectors.toList());
    }
    
    /**
     * Finds pending (not submitted) evaluations by evaluator ID.
     */
    public List<Evaluation> findPendingByEvaluatorId(Long evaluatorId) {
        if (evaluatorId == null) return new ArrayList<>();
        
        return dataManager.getEvaluations().stream()
                .filter(e -> evaluatorId.equals(e.getEvaluatorId()) && !e.isSubmitted())
                .collect(Collectors.toList());
    }
    
    /**
     * Finds submitted evaluations by registration ID.
     */
    public List<Evaluation> findSubmittedByRegistrationId(Long registrationId) {
        if (registrationId == null) return new ArrayList<>();
        
        return dataManager.getEvaluations().stream()
                .filter(e -> registrationId.equals(e.getRegistrationId()) && e.isSubmitted())
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all submitted evaluations.
     */
    public List<Evaluation> findAllSubmitted() {
        return dataManager.getEvaluations().stream()
                .filter(Evaluation::isSubmitted)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all pending (not submitted) evaluations.
     */
    public List<Evaluation> findAllPending() {
        return dataManager.getEvaluations().stream()
                .filter(e -> !e.isSubmitted())
                .collect(Collectors.toList());
    }

    
    // ==================== Save Operations ====================
    
    /**
     * Saves an evaluation (creates new or updates existing).
     * Requirements: 5.4
     */
    public Evaluation save(Evaluation evaluation) {
        if (evaluation == null) return null;
        
        List<Evaluation> evaluations = dataManager.getEvaluations();
        
        if (evaluation.getId() == null) {
            // New evaluation - assign ID
            evaluation.setId(dataManager.getNextId(Evaluation.class));
            evaluations.add(evaluation);
        } else {
            // Update existing evaluation
            boolean found = false;
            for (int i = 0; i < evaluations.size(); i++) {
                if (evaluation.getId().equals(evaluations.get(i).getId())) {
                    evaluations.set(i, evaluation);
                    found = true;
                    break;
                }
            }
            if (!found) {
                evaluations.add(evaluation);
            }
        }
        
        dataManager.setEvaluations(evaluations);
        dataManager.saveEvaluations();
        return evaluation;
    }
    
    // ==================== Delete Operations ====================
    
    /**
     * Deletes an evaluation by ID.
     */
    public void delete(Long id) {
        if (id == null) return;
        
        List<Evaluation> evaluations = dataManager.getEvaluations();
        if (evaluations.removeIf(e -> id.equals(e.getId()))) {
            dataManager.setEvaluations(evaluations);
            dataManager.saveEvaluations();
        }
    }
    
    /**
     * Deletes an evaluation.
     */
    public void delete(Evaluation evaluation) {
        if (evaluation == null || evaluation.getId() == null) return;
        delete(evaluation.getId());
    }
    
    /**
     * Deletes all evaluations for a registration.
     */
    public void deleteByRegistrationId(Long registrationId) {
        if (registrationId == null) return;
        
        List<Evaluation> evaluations = dataManager.getEvaluations();
        if (evaluations.removeIf(e -> registrationId.equals(e.getRegistrationId()))) {
            dataManager.setEvaluations(evaluations);
            dataManager.saveEvaluations();
        }
    }

    
    // ==================== Update Operations ====================
    
    /**
     * Submits an evaluation (marks as submitted with timestamp).
     */
    public Evaluation submit(Long id) {
        Evaluation evaluation = findById(id);
        if (evaluation != null && !evaluation.isSubmitted()) {
            evaluation.setSubmitted(true);
            evaluation.setSubmittedAt(LocalDateTime.now());
            return save(evaluation);
        }
        return evaluation;
    }
    
    /**
     * Updates scores for an evaluation.
     */
    public Evaluation updateScores(Long id, int problemClarity, int methodology, 
                                   int results, int presentationQuality) {
        Evaluation evaluation = findById(id);
        if (evaluation != null && !evaluation.isSubmitted()) {
            evaluation.setProblemClarity(problemClarity);
            evaluation.setMethodology(methodology);
            evaluation.setResults(results);
            evaluation.setPresentationQuality(presentationQuality);
            return save(evaluation);
        }
        return evaluation;
    }
    
    /**
     * Updates comments for an evaluation.
     */
    public Evaluation updateComments(Long id, String comments) {
        Evaluation evaluation = findById(id);
        if (evaluation != null && !evaluation.isSubmitted()) {
            evaluation.setComments(comments);
            return save(evaluation);
        }
        return evaluation;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Checks if an evaluation exists by ID.
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    
    /**
     * Checks if an evaluation assignment exists.
     */
    public boolean existsByEvaluatorIdAndRegistrationId(Long evaluatorId, Long registrationId) {
        return findByEvaluatorIdAndRegistrationId(evaluatorId, registrationId) != null;
    }
    
    /**
     * Counts total evaluations.
     */
    public long count() {
        return dataManager.getEvaluations().size();
    }
    
    /**
     * Counts submitted evaluations.
     */
    public long countSubmitted() {
        return dataManager.getEvaluations().stream()
                .filter(Evaluation::isSubmitted)
                .count();
    }
    
    /**
     * Counts pending evaluations.
     */
    public long countPending() {
        return dataManager.getEvaluations().stream()
                .filter(e -> !e.isSubmitted())
                .count();
    }
    
    /**
     * Counts evaluations by evaluator ID.
     */
    public long countByEvaluatorId(Long evaluatorId) {
        if (evaluatorId == null) return 0;
        return dataManager.getEvaluations().stream()
                .filter(e -> evaluatorId.equals(e.getEvaluatorId()))
                .count();
    }
    
    /**
     * Counts evaluations by registration ID.
     */
    public long countByRegistrationId(Long registrationId) {
        if (registrationId == null) return 0;
        return dataManager.getEvaluations().stream()
                .filter(e -> registrationId.equals(e.getRegistrationId()))
                .count();
    }
    
    /**
     * Calculates average score for a registration.
     */
    public double getAverageScoreByRegistrationId(Long registrationId) {
        List<Evaluation> submitted = findSubmittedByRegistrationId(registrationId);
        if (submitted.isEmpty()) return 0.0;
        
        return submitted.stream()
                .mapToInt(Evaluation::getTotalScore)
                .average()
                .orElse(0.0);
    }
}
