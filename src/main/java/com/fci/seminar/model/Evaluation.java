package com.fci.seminar.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an evaluation of a student's presentation by an evaluator.
 * Contains scoring criteria and feedback.
 */
public class Evaluation {
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 25;
    public static final int MAX_TOTAL_SCORE = 100;

    private Long id;
    private Long evaluatorId;
    private Long registrationId;
    private int problemClarity;      // 0-25
    private int methodology;         // 0-25
    private int results;             // 0-25
    private int presentationQuality; // 0-25
    private String comments;
    private boolean submitted;
    private LocalDateTime submittedAt;

    public Evaluation() {
        this.problemClarity = 0;
        this.methodology = 0;
        this.results = 0;
        this.presentationQuality = 0;
        this.submitted = false;
    }

    public Evaluation(Long id, Long evaluatorId, Long registrationId) {
        this.id = id;
        this.evaluatorId = evaluatorId;
        this.registrationId = registrationId;
        this.problemClarity = 0;
        this.methodology = 0;
        this.results = 0;
        this.presentationQuality = 0;
        this.submitted = false;
    }

    /**
     * Calculates the total score from all four criteria.
     * @return sum of all criteria scores (0-100)
     */
    public int getTotalScore() {
        return problemClarity + methodology + results + presentationQuality;
    }

    /**
     * Validates that a score is within the valid range [0, 25].
     */
    public static boolean isValidScore(int score) {
        return score >= MIN_SCORE && score <= MAX_SCORE;
    }

    /**
     * Validates that all scores are within valid ranges.
     */
    public boolean areScoresValid() {
        return isValidScore(problemClarity) &&
               isValidScore(methodology) &&
               isValidScore(results) &&
               isValidScore(presentationQuality);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public int getProblemClarity() {
        return problemClarity;
    }

    public void setProblemClarity(int problemClarity) {
        this.problemClarity = problemClarity;
    }

    public int getMethodology() {
        return methodology;
    }

    public void setMethodology(int methodology) {
        this.methodology = methodology;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public int getPresentationQuality() {
        return presentationQuality;
    }

    public void setPresentationQuality(int presentationQuality) {
        this.presentationQuality = presentationQuality;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return problemClarity == that.problemClarity &&
               methodology == that.methodology &&
               results == that.results &&
               presentationQuality == that.presentationQuality &&
               submitted == that.submitted &&
               Objects.equals(id, that.id) &&
               Objects.equals(evaluatorId, that.evaluatorId) &&
               Objects.equals(registrationId, that.registrationId) &&
               Objects.equals(comments, that.comments) &&
               Objects.equals(submittedAt, that.submittedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evaluatorId, registrationId, problemClarity, methodology,
                results, presentationQuality, comments, submitted, submittedAt);
    }

    @Override
    public String toString() {
        return "Evaluation{" +
               "id=" + id +
               ", evaluatorId=" + evaluatorId +
               ", registrationId=" + registrationId +
               ", totalScore=" + getTotalScore() +
               ", submitted=" + submitted +
               '}';
    }
}
