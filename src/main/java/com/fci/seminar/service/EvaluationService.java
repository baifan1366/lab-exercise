package com.fci.seminar.service;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.repository.EvaluationRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing evaluations.
 * Handles evaluation CRUD operations and scoring.
 * Requirements: 5.1, 5.4, 5.5, 5.6
 */
public class EvaluationService {
    private static EvaluationService instance;
    
    private final EvaluationRepository evaluationRepository;
    
    /**
     * Private constructor for singleton pattern.
     */
    private EvaluationService() {
        this.evaluationRepository = new EvaluationRepository();
    }
    
    /**
     * Constructor for testing with custom repository.
     */
    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }
    
    /**
     * Gets the singleton instance of EvaluationService.
     */
    public static synchronized EvaluationService getInstance() {
        if (instance == null) {
            instance = new EvaluationService();
        }
        return instance;
    }
    
    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    // ==================== Query Operations ====================
    
    /**
     * Gets all evaluations.
     * @return list of all evaluations
     */
    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    
    /**
     * Gets an evaluation by ID.
     * @param id the evaluation ID
     * @return the evaluation, or null if not found
     */
    public Evaluation getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }
    
    /**
     * Gets evaluations assigned to an evaluator.
     * Requirements: 5.1
     * @param evaluatorId the evaluator ID
     * @return list of evaluations for the evaluator
     */
    public List<Evaluation> getEvaluationsByEvaluator(Long evaluatorId) {
        return evaluationRepository.findByEvaluatorId(evaluatorId);
    }
    
    /**
     * Gets evaluations for a registration.
     * @param registrationId the registration ID
     * @return list of evaluations for the registration
     */
    public List<Evaluation> getEvaluationsByRegistration(Long registrationId) {
        return evaluationRepository.findByRegistrationId(registrationId);
    }
    
    /**
     * Gets submitted evaluations for an evaluator.
     * @param evaluatorId the evaluator ID
     * @return list of submitted evaluations
     */
    public List<Evaluation> getSubmittedByEvaluator(Long evaluatorId) {
        return evaluationRepository.findSubmittedByEvaluatorId(evaluatorId);
    }
    
    /**
     * Gets pending (not submitted) evaluations for an evaluator.
     * @param evaluatorId the evaluator ID
     * @return list of pending evaluations
     */
    public List<Evaluation> getPendingByEvaluator(Long evaluatorId) {
        return evaluationRepository.findPendingByEvaluatorId(evaluatorId);
    }
    
    /**
     * Gets submitted evaluations for a registration.
     * @param registrationId the registration ID
     * @return list of submitted evaluations
     */
    public List<Evaluation> getSubmittedByRegistration(Long registrationId) {
        return evaluationRepository.findSubmittedByRegistrationId(registrationId);
    }
    
    // ==================== Save Operations ====================
    
    /**
     * Saves an evaluation (creates new or updates existing).
     * Requirements: 5.4, 5.5
     * @param evaluation the evaluation to save
     * @return the saved evaluation
     * @throws IllegalArgumentException if validation fails
     */
    public Evaluation saveEvaluation(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation cannot be null");
        }
        
        // Validate scores
        validateScores(evaluation);
        
        // Cannot modify submitted evaluations
        if (evaluation.getId() != null) {
            Evaluation existing = evaluationRepository.findById(evaluation.getId());
            if (existing != null && existing.isSubmitted()) {
                throw new IllegalArgumentException("Cannot modify submitted evaluation");
            }
        }
        
        // Ensure not marked as submitted when saving draft
        if (!evaluation.isSubmitted()) {
            evaluation.setSubmittedAt(null);
        }
        
        return evaluationRepository.save(evaluation);
    }

    
    /**
     * Validates evaluation scores.
     * @param evaluation the evaluation to validate
     * @throws IllegalArgumentException if scores are invalid
     */
    private void validateScores(Evaluation evaluation) {
        if (!Evaluation.isValidScore(evaluation.getProblemClarity())) {
            throw new IllegalArgumentException(
                "Problem clarity score must be between " + 
                Evaluation.MIN_SCORE + " and " + Evaluation.MAX_SCORE);
        }
        if (!Evaluation.isValidScore(evaluation.getMethodology())) {
            throw new IllegalArgumentException(
                "Methodology score must be between " + 
                Evaluation.MIN_SCORE + " and " + Evaluation.MAX_SCORE);
        }
        if (!Evaluation.isValidScore(evaluation.getResults())) {
            throw new IllegalArgumentException(
                "Results score must be between " + 
                Evaluation.MIN_SCORE + " and " + Evaluation.MAX_SCORE);
        }
        if (!Evaluation.isValidScore(evaluation.getPresentationQuality())) {
            throw new IllegalArgumentException(
                "Presentation quality score must be between " + 
                Evaluation.MIN_SCORE + " and " + Evaluation.MAX_SCORE);
        }
    }
    
    /**
     * Submits an evaluation (marks as final).
     * Requirements: 5.6
     * @param evaluationId the evaluation ID
     * @return the submitted evaluation
     * @throws IllegalArgumentException if evaluation not found or already submitted
     */
    public Evaluation submitEvaluation(Long evaluationId) {
        if (evaluationId == null) {
            throw new IllegalArgumentException("Evaluation ID cannot be null");
        }
        
        Evaluation evaluation = evaluationRepository.findById(evaluationId);
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation not found: " + evaluationId);
        }
        
        if (evaluation.isSubmitted()) {
            throw new IllegalArgumentException("Evaluation already submitted");
        }
        
        // Validate scores before submission
        validateScores(evaluation);
        
        evaluation.setSubmitted(true);
        evaluation.setSubmittedAt(LocalDateTime.now());
        
        return evaluationRepository.save(evaluation);
    }
    
    // ==================== Assignment Operations ====================
    
    /**
     * Assigns an evaluator to a presentation (creates evaluation record).
     * Requirements: 5.1
     * @param evaluatorId the evaluator ID
     * @param registrationId the registration ID
     * @return the created evaluation assignment
     * @throws IllegalArgumentException if assignment already exists
     */
    public Evaluation assignEvaluator(Long evaluatorId, Long registrationId) {
        if (evaluatorId == null || registrationId == null) {
            throw new IllegalArgumentException("Evaluator ID and Registration ID cannot be null");
        }
        
        // Check if assignment already exists
        if (evaluationRepository.existsByEvaluatorIdAndRegistrationId(evaluatorId, registrationId)) {
            throw new IllegalArgumentException("Evaluator already assigned to this registration");
        }
        
        Evaluation evaluation = new Evaluation();
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setRegistrationId(registrationId);
        
        return evaluationRepository.save(evaluation);
    }
    
    /**
     * Removes an evaluator assignment.
     * @param evaluatorId the evaluator ID
     * @param registrationId the registration ID
     */
    public void removeAssignment(Long evaluatorId, Long registrationId) {
        Evaluation evaluation = evaluationRepository
                .findByEvaluatorIdAndRegistrationId(evaluatorId, registrationId);
        if (evaluation != null) {
            evaluationRepository.delete(evaluation.getId());
        }
    }
    
    /**
     * Checks if an evaluator is assigned to a registration.
     * @param evaluatorId the evaluator ID
     * @param registrationId the registration ID
     * @return true if assigned
     */
    public boolean isAssigned(Long evaluatorId, Long registrationId) {
        return evaluationRepository.existsByEvaluatorIdAndRegistrationId(evaluatorId, registrationId);
    }

    
    // ==================== Score Calculations ====================
    
    /**
     * Gets the average score for a registration.
     * @param registrationId the registration ID
     * @return the average score from all submitted evaluations
     */
    public double getAverageScore(Long registrationId) {
        return evaluationRepository.getAverageScoreByRegistrationId(registrationId);
    }
    
    /**
     * Gets the evaluation by evaluator and registration.
     * @param evaluatorId the evaluator ID
     * @param registrationId the registration ID
     * @return the evaluation, or null if not found
     */
    public Evaluation getEvaluation(Long evaluatorId, Long registrationId) {
        return evaluationRepository.findByEvaluatorIdAndRegistrationId(evaluatorId, registrationId);
    }
    
    // ==================== Statistics ====================
    
    /**
     * Counts total evaluations.
     * @return total evaluation count
     */
    public long countEvaluations() {
        return evaluationRepository.count();
    }
    
    /**
     * Counts submitted evaluations.
     * @return count of submitted evaluations
     */
    public long countSubmitted() {
        return evaluationRepository.countSubmitted();
    }
    
    /**
     * Counts pending evaluations.
     * @return count of pending evaluations
     */
    public long countPending() {
        return evaluationRepository.countPending();
    }
    
    /**
     * Counts evaluations by evaluator.
     * @param evaluatorId the evaluator ID
     * @return count of evaluations for the evaluator
     */
    public long countByEvaluator(Long evaluatorId) {
        return evaluationRepository.countByEvaluatorId(evaluatorId);
    }
    
    /**
     * Counts evaluations by registration.
     * @param registrationId the registration ID
     * @return count of evaluations for the registration
     */
    public long countByRegistration(Long registrationId) {
        return evaluationRepository.countByRegistrationId(registrationId);
    }
}
