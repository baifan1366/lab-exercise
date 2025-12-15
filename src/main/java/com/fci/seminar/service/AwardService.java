package com.fci.seminar.service;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.Registration;
import com.fci.seminar.model.enums.AwardType;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.repository.EvaluationRepository;
import com.fci.seminar.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing awards.
 * Handles award calculation and winner determination.
 * Requirements: 8.2, 8.3, 8.4
 */
public class AwardService {
    private static AwardService instance;
    
    private final RegistrationRepository registrationRepository;
    private final EvaluationRepository evaluationRepository;
    
    // Cached awards
    private List<Award> calculatedAwards;
    
    /**
     * Private constructor for singleton pattern.
     */
    private AwardService() {
        this.registrationRepository = new RegistrationRepository();
        this.evaluationRepository = new EvaluationRepository();
        this.calculatedAwards = new ArrayList<>();
    }
    
    /**
     * Constructor for testing with custom repositories.
     */
    public AwardService(RegistrationRepository registrationRepository,
                        EvaluationRepository evaluationRepository) {
        this.registrationRepository = registrationRepository;
        this.evaluationRepository = evaluationRepository;
        this.calculatedAwards = new ArrayList<>();
    }
    
    /**
     * Gets the singleton instance of AwardService.
     */
    public static synchronized AwardService getInstance() {
        if (instance == null) {
            instance = new AwardService();
        }
        return instance;
    }

    
    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    // ==================== Award Calculation ====================
    
    /**
     * Calculates all awards based on evaluation scores.
     * Requirements: 8.2
     * @return list of calculated awards (may include ties)
     */
    public List<Award> calculateAwards() {
        calculatedAwards = new ArrayList<>();
        
        // Get all approved registrations
        List<Registration> approvedRegistrations = registrationRepository
                .findByStatus(RegistrationStatus.APPROVED);
        
        // Calculate Best Oral awards
        List<Award> oralAwards = calculateAwardsByType(
                approvedRegistrations, SessionType.ORAL, AwardType.BEST_ORAL);
        calculatedAwards.addAll(oralAwards);
        
        // Calculate Best Poster awards
        List<Award> posterAwards = calculateAwardsByType(
                approvedRegistrations, SessionType.POSTER, AwardType.BEST_POSTER);
        calculatedAwards.addAll(posterAwards);
        
        // Calculate People's Choice (based on all registrations)
        // For now, People's Choice uses the same scoring as others
        // In a full implementation, this would use a separate voting system
        List<Award> peoplesChoice = calculatePeoplesChoice(approvedRegistrations);
        calculatedAwards.addAll(peoplesChoice);
        
        return calculatedAwards;
    }
    
    /**
     * Calculates awards for a specific presentation type.
     * Requirements: 8.2, 8.4
     * @param registrations all approved registrations
     * @param type the presentation type
     * @param awardType the award type
     * @return list of awards (multiple if tied)
     */
    private List<Award> calculateAwardsByType(List<Registration> registrations,
                                               SessionType type, AwardType awardType) {
        List<Award> awards = new ArrayList<>();
        
        // Filter by presentation type
        List<Registration> typeRegistrations = registrations.stream()
                .filter(r -> type.equals(r.getPresentationType()))
                .collect(Collectors.toList());
        
        if (typeRegistrations.isEmpty()) {
            return awards;
        }
        
        // Calculate scores for each registration
        List<RegistrationScore> scores = typeRegistrations.stream()
                .map(r -> new RegistrationScore(r.getId(), 
                        evaluationRepository.getAverageScoreByRegistrationId(r.getId())))
                .filter(rs -> rs.score > 0) // Only include those with evaluations
                .sorted(Comparator.comparingDouble(RegistrationScore::getScore).reversed())
                .collect(Collectors.toList());
        
        if (scores.isEmpty()) {
            return awards;
        }
        
        // Find highest score and all tied winners
        double highestScore = scores.get(0).score;
        
        for (RegistrationScore rs : scores) {
            if (rs.score == highestScore) {
                Award award = new Award();
                award.setType(awardType);
                award.setRegistrationId(rs.registrationId);
                award.setScore(rs.score);
                award.setAwardedAt(LocalDateTime.now());
                awards.add(award);
            } else {
                break; // No more ties
            }
        }
        
        return awards;
    }

    
    /**
     * Calculates People's Choice award.
     * In a full implementation, this would use a voting system.
     * Currently uses the highest overall score.
     * @param registrations all approved registrations
     * @return list of People's Choice awards (multiple if tied)
     */
    private List<Award> calculatePeoplesChoice(List<Registration> registrations) {
        List<Award> awards = new ArrayList<>();
        
        if (registrations.isEmpty()) {
            return awards;
        }
        
        // Calculate scores for all registrations
        List<RegistrationScore> scores = registrations.stream()
                .map(r -> new RegistrationScore(r.getId(),
                        evaluationRepository.getAverageScoreByRegistrationId(r.getId())))
                .filter(rs -> rs.score > 0)
                .sorted(Comparator.comparingDouble(RegistrationScore::getScore).reversed())
                .collect(Collectors.toList());
        
        if (scores.isEmpty()) {
            return awards;
        }
        
        // Find highest score and all tied winners
        double highestScore = scores.get(0).score;
        
        for (RegistrationScore rs : scores) {
            if (rs.score == highestScore) {
                Award award = new Award();
                award.setType(AwardType.PEOPLES_CHOICE);
                award.setRegistrationId(rs.registrationId);
                award.setScore(rs.score);
                award.setAwardedAt(LocalDateTime.now());
                awards.add(award);
            } else {
                break;
            }
        }
        
        return awards;
    }
    
    // ==================== Award Retrieval ====================
    
    /**
     * Gets the Best Oral Presentation award(s).
     * Requirements: 8.3
     * @return list of Best Oral awards (multiple if tied)
     */
    public List<Award> getBestOral() {
        return calculatedAwards.stream()
                .filter(a -> a.getType() == AwardType.BEST_ORAL)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the Best Poster Presentation award(s).
     * Requirements: 8.3
     * @return list of Best Poster awards (multiple if tied)
     */
    public List<Award> getBestPoster() {
        return calculatedAwards.stream()
                .filter(a -> a.getType() == AwardType.BEST_POSTER)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the People's Choice award(s).
     * Requirements: 8.3
     * @return list of People's Choice awards (multiple if tied)
     */
    public List<Award> getPeoplesChoice() {
        return calculatedAwards.stream()
                .filter(a -> a.getType() == AwardType.PEOPLES_CHOICE)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all calculated awards.
     * @return list of all awards
     */
    public List<Award> getAllAwards() {
        return new ArrayList<>(calculatedAwards);
    }
    
    /**
     * Gets awards by type.
     * @param type the award type
     * @return list of awards of the specified type
     */
    public List<Award> getAwardsByType(AwardType type) {
        return calculatedAwards.stream()
                .filter(a -> a.getType() == type)
                .collect(Collectors.toList());
    }
    
    /**
     * Clears calculated awards.
     */
    public void clearAwards() {
        calculatedAwards.clear();
    }
    
    // ==================== Helper Class ====================
    
    /**
     * Helper class to hold registration ID and score for sorting.
     */
    private static class RegistrationScore {
        final Long registrationId;
        final double score;
        
        RegistrationScore(Long registrationId, double score) {
            this.registrationId = registrationId;
            this.score = score;
        }
        
        double getScore() {
            return score;
        }
    }
}
