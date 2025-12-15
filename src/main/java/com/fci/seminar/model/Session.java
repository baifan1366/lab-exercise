package com.fci.seminar.model;

import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.model.enums.SessionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a seminar session with date, time, venue, and capacity information.
 */
public class Session {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String venue;
    private SessionType type;
    private int capacity;
    private int registered;
    private SessionStatus status;
    private String description;

    public Session() {
        this.registered = 0;
        this.status = SessionStatus.OPEN;
    }

    public Session(Long id, LocalDate date, LocalTime startTime, LocalTime endTime,
                   String venue, SessionType type, int capacity, String description) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.type = type;
        this.capacity = capacity;
        this.registered = 0;
        this.status = SessionStatus.OPEN;
        this.description = description;
    }

    // Validation methods
    public boolean isValid() {
        return date != null &&
               startTime != null &&
               endTime != null &&
               venue != null && !venue.trim().isEmpty() &&
               type != null &&
               capacity > 0 &&
               startTime.isBefore(endTime);
    }

    public boolean hasAvailableSlots() {
        return registered < capacity && status == SessionStatus.OPEN;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return capacity == session.capacity &&
               registered == session.registered &&
               Objects.equals(id, session.id) &&
               Objects.equals(date, session.date) &&
               Objects.equals(startTime, session.startTime) &&
               Objects.equals(endTime, session.endTime) &&
               Objects.equals(venue, session.venue) &&
               type == session.type &&
               status == session.status &&
               Objects.equals(description, session.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, startTime, endTime, venue, type, capacity, registered, status, description);
    }

    @Override
    public String toString() {
        return "Session{" +
               "id=" + id +
               ", date=" + date +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", venue='" + venue + '\'' +
               ", type=" + type +
               ", capacity=" + capacity +
               ", registered=" + registered +
               ", status=" + status +
               '}';
    }
}
