package com.fci.seminar.model;

import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.model.enums.SessionType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a student's registration for a seminar presentation.
 * Contains research details and registration status.
 */
public class Registration {
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_ABSTRACT_LENGTH = 1000;

    private Long id;
    private Long studentId;
    private Long sessionId;
    private String researchTitle;
    private String abstractText;
    private String supervisorName;
    private SessionType presentationType;
    private RegistrationStatus status;
    private String filePath;
    private String boardId;  // For poster presentations - board assignment
    private LocalDateTime createdAt;

    public Registration() {
        this.status = RegistrationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Registration(Long id, Long studentId, Long sessionId, String researchTitle,
                        String abstractText, String supervisorName, SessionType presentationType) {
        this.id = id;
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.researchTitle = researchTitle;
        this.abstractText = abstractText;
        this.supervisorName = supervisorName;
        this.presentationType = presentationType;
        this.status = RegistrationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Validation methods
    public boolean isValid() {
        return studentId != null &&
               isValidTitle(researchTitle) &&
               isValidAbstract(abstractText) &&
               supervisorName != null && !supervisorName.trim().isEmpty() &&
               presentationType != null;
    }

    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= MAX_TITLE_LENGTH;
    }

    public static boolean isValidAbstract(String abstractText) {
        return abstractText != null && !abstractText.trim().isEmpty() && abstractText.length() <= MAX_ABSTRACT_LENGTH;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getResearchTitle() {
        return researchTitle;
    }

    public void setResearchTitle(String researchTitle) {
        this.researchTitle = researchTitle;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public SessionType getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(SessionType presentationType) {
        this.presentationType = presentationType;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(studentId, that.studentId) &&
               Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(researchTitle, that.researchTitle) &&
               Objects.equals(abstractText, that.abstractText) &&
               Objects.equals(supervisorName, that.supervisorName) &&
               presentationType == that.presentationType &&
               status == that.status &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(boardId, that.boardId) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, sessionId, researchTitle, abstractText,
                supervisorName, presentationType, status, filePath, boardId, createdAt);
    }

    @Override
    public String toString() {
        return "Registration{" +
               "id=" + id +
               ", studentId=" + studentId +
               ", sessionId=" + sessionId +
               ", researchTitle='" + researchTitle + '\'' +
               ", presentationType=" + presentationType +
               ", status=" + status +
               ", boardId='" + boardId + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
