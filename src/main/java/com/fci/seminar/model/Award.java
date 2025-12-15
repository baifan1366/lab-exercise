package com.fci.seminar.model;

import com.fci.seminar.model.enums.AwardType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an award given to a presentation at the seminar.
 */
public class Award {
    private Long id;
    private AwardType type;
    private Long registrationId;
    private double score;
    private LocalDateTime awardedAt;

    public Award() {
    }

    public Award(Long id, AwardType type, Long registrationId, double score) {
        this.id = id;
        this.type = type;
        this.registrationId = registrationId;
        this.score = score;
        this.awardedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AwardType getType() {
        return type;
    }

    public void setType(AwardType type) {
        this.type = type;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Award award = (Award) o;
        return Double.compare(award.score, score) == 0 &&
               Objects.equals(id, award.id) &&
               type == award.type &&
               Objects.equals(registrationId, award.registrationId) &&
               Objects.equals(awardedAt, award.awardedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, registrationId, score, awardedAt);
    }

    @Override
    public String toString() {
        return "Award{" +
               "id=" + id +
               ", type=" + type +
               ", registrationId=" + registrationId +
               ", score=" + score +
               ", awardedAt=" + awardedAt +
               '}';
    }
}
