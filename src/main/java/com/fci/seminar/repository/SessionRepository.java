package com.fci.seminar.repository;

import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.util.DataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Session entity operations.
 * Handles CRUD operations and filtering for seminar sessions.
 * Requirements: 2.1, 2.3, 6.1
 */
public class SessionRepository {
    private final DataManager dataManager;
    
    public SessionRepository() {
        this.dataManager = DataManager.getInstance();
    }
    
    /**
     * Constructor for testing with custom DataManager.
     */
    public SessionRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ==================== Find Operations ====================
    
    /**
     * Finds a session by ID.
     */
    public Session findById(Long id) {
        if (id == null) return null;
        
        for (Session session : dataManager.getSessions()) {
            if (id.equals(session.getId())) {
                return session;
            }
        }
        return null;
    }
    
    /**
     * Finds all sessions.
     * Requirements: 2.1, 6.1
     */
    public List<Session> findAll() {
        return dataManager.getSessions();
    }
    
    /**
     * Finds sessions by date.
     * Requirements: 2.3
     */
    public List<Session> findByDate(LocalDate date) {
        if (date == null) return new ArrayList<>();
        
        return dataManager.getSessions().stream()
                .filter(s -> date.equals(s.getDate()))
                .collect(Collectors.toList());
    }

    
    /**
     * Finds sessions by type (ORAL or POSTER).
     * Requirements: 2.3
     */
    public List<Session> findByType(SessionType type) {
        if (type == null) return new ArrayList<>();
        
        return dataManager.getSessions().stream()
                .filter(s -> type.equals(s.getType()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds sessions by status.
     * Requirements: 2.3
     */
    public List<Session> findByStatus(SessionStatus status) {
        if (status == null) return new ArrayList<>();
        
        return dataManager.getSessions().stream()
                .filter(s -> status.equals(s.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds sessions by date and type.
     */
    public List<Session> findByDateAndType(LocalDate date, SessionType type) {
        if (date == null && type == null) return findAll();
        if (date == null) return findByType(type);
        if (type == null) return findByDate(date);
        
        return dataManager.getSessions().stream()
                .filter(s -> date.equals(s.getDate()) && type.equals(s.getType()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds sessions that are open for registration.
     */
    public List<Session> findAvailable() {
        return dataManager.getSessions().stream()
                .filter(Session::hasAvailableSlots)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds available sessions by type.
     */
    public List<Session> findAvailableByType(SessionType type) {
        if (type == null) return findAvailable();
        
        return dataManager.getSessions().stream()
                .filter(s -> s.hasAvailableSlots() && type.equals(s.getType()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds sessions within a date range.
     */
    public List<Session> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return new ArrayList<>();
        
        return dataManager.getSessions().stream()
                .filter(s -> s.getDate() != null && 
                        !s.getDate().isBefore(startDate) && 
                        !s.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    
    // ==================== Save Operations ====================
    
    /**
     * Saves a session (creates new or updates existing).
     * Requirements: 6.1
     */
    public Session save(Session session) {
        if (session == null) return null;
        
        List<Session> sessions = dataManager.getSessions();
        
        if (session.getId() == null) {
            // New session - assign ID
            session.setId(dataManager.getNextId(Session.class));
            sessions.add(session);
        } else {
            // Update existing session
            boolean found = false;
            for (int i = 0; i < sessions.size(); i++) {
                if (session.getId().equals(sessions.get(i).getId())) {
                    sessions.set(i, session);
                    found = true;
                    break;
                }
            }
            if (!found) {
                sessions.add(session);
            }
        }
        
        dataManager.setSessions(sessions);
        dataManager.saveSessions();
        return session;
    }
    
    // ==================== Delete Operations ====================
    
    /**
     * Deletes a session by ID.
     * Requirements: 6.1
     */
    public void delete(Long id) {
        if (id == null) return;
        
        List<Session> sessions = dataManager.getSessions();
        if (sessions.removeIf(s -> id.equals(s.getId()))) {
            dataManager.setSessions(sessions);
            dataManager.saveSessions();
        }
    }
    
    /**
     * Deletes a session.
     */
    public void delete(Session session) {
        if (session == null || session.getId() == null) return;
        delete(session.getId());
    }
    
    // ==================== Update Operations ====================
    
    /**
     * Updates the status of a session.
     */
    public Session updateStatus(Long id, SessionStatus status) {
        Session session = findById(id);
        if (session != null) {
            session.setStatus(status);
            return save(session);
        }
        return null;
    }
    
    /**
     * Increments the registered count for a session.
     */
    public Session incrementRegistered(Long id) {
        Session session = findById(id);
        if (session != null) {
            session.setRegistered(session.getRegistered() + 1);
            // Auto-update status to FULL if capacity reached
            if (session.getRegistered() >= session.getCapacity()) {
                session.setStatus(SessionStatus.FULL);
            }
            return save(session);
        }
        return null;
    }
    
    /**
     * Decrements the registered count for a session.
     */
    public Session decrementRegistered(Long id) {
        Session session = findById(id);
        if (session != null && session.getRegistered() > 0) {
            session.setRegistered(session.getRegistered() - 1);
            // Auto-update status to OPEN if was FULL and now has space
            if (session.getStatus() == SessionStatus.FULL && 
                session.getRegistered() < session.getCapacity()) {
                session.setStatus(SessionStatus.OPEN);
            }
            return save(session);
        }
        return null;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Checks if a session exists by ID.
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    
    /**
     * Counts total sessions.
     */
    public long count() {
        return dataManager.getSessions().size();
    }
    
    /**
     * Counts sessions by status.
     */
    public long countByStatus(SessionStatus status) {
        if (status == null) return 0;
        return dataManager.getSessions().stream()
                .filter(s -> status.equals(s.getStatus()))
                .count();
    }
    
    /**
     * Counts sessions by type.
     */
    public long countByType(SessionType type) {
        if (type == null) return 0;
        return dataManager.getSessions().stream()
                .filter(s -> type.equals(s.getType()))
                .count();
    }
}
