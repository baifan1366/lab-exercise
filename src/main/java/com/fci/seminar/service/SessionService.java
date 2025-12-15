package com.fci.seminar.service;

import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.SessionStatus;
import com.fci.seminar.model.enums.SessionType;
import com.fci.seminar.repository.RegistrationRepository;
import com.fci.seminar.repository.SessionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing seminar sessions.
 * Handles session CRUD operations and status management.
 * Requirements: 2.1, 6.2, 6.3, 6.4, 6.5, 6.6
 */
public class SessionService {
    private static SessionService instance;
    
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    
    /**
     * Private constructor for singleton pattern.
     */
    private SessionService() {
        this.sessionRepository = new SessionRepository();
        this.registrationRepository = new RegistrationRepository();
    }
    
    /**
     * Constructor for testing with custom repositories.
     */
    public SessionService(SessionRepository sessionRepository, 
                          RegistrationRepository registrationRepository) {
        this.sessionRepository = sessionRepository;
        this.registrationRepository = registrationRepository;
    }
    
    /**
     * Gets the singleton instance of SessionService.
     */
    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
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
     * Gets all sessions.
     * Requirements: 2.1, 6.1
     * @return list of all sessions
     */
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }
    
    /**
     * Gets a session by ID.
     * @param id the session ID
     * @return the session, or null if not found
     */
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id);
    }
    
    /**
     * Gets available sessions (OPEN status with available slots).
     * Requirements: 3.3
     * @return list of available sessions
     */
    public List<Session> getAvailableSessions() {
        return sessionRepository.findAvailable();
    }
    
    /**
     * Gets available sessions by presentation type.
     * Requirements: 3.3
     * @param type the session type (ORAL or POSTER)
     * @return list of available sessions of the specified type
     */
    public List<Session> getAvailableSessions(SessionType type) {
        if (type == null) {
            return getAvailableSessions();
        }
        return sessionRepository.findAvailableByType(type);
    }
    
    /**
     * Gets sessions by date.
     * Requirements: 2.3
     * @param date the date to filter by
     * @return list of sessions on the specified date
     */
    public List<Session> getSessionsByDate(LocalDate date) {
        return sessionRepository.findByDate(date);
    }
    
    /**
     * Gets sessions by type.
     * Requirements: 2.3
     * @param type the session type
     * @return list of sessions of the specified type
     */
    public List<Session> getSessionsByType(SessionType type) {
        return sessionRepository.findByType(type);
    }
    
    /**
     * Gets sessions by status.
     * @param status the session status
     * @return list of sessions with the specified status
     */
    public List<Session> getSessionsByStatus(SessionStatus status) {
        return sessionRepository.findByStatus(status);
    }
    
    /**
     * Filters sessions by date and/or type.
     * Requirements: 2.3
     * @param date optional date filter
     * @param type optional type filter
     * @return filtered list of sessions
     */
    public List<Session> filterSessions(LocalDate date, SessionType type) {
        List<Session> sessions = getAllSessions();
        
        if (date != null) {
            sessions = sessions.stream()
                    .filter(s -> date.equals(s.getDate()))
                    .collect(Collectors.toList());
        }
        
        if (type != null) {
            sessions = sessions.stream()
                    .filter(s -> type.equals(s.getType()))
                    .collect(Collectors.toList());
        }
        
        return sessions;
    }

    
    // ==================== Create/Update Operations ====================
    
    /**
     * Creates a new session with validation.
     * Requirements: 6.2
     * @param session the session to create
     * @return the created session, or null if validation fails
     * @throws IllegalArgumentException if session is invalid
     */
    public Session createSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        // Validate required fields
        validateSession(session);
        
        // Set default values
        session.setId(null); // Ensure new ID is assigned
        session.setRegistered(0);
        if (session.getStatus() == null) {
            session.setStatus(SessionStatus.OPEN);
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Updates an existing session.
     * Requirements: 6.3
     * @param session the session to update
     * @return the updated session
     * @throws IllegalArgumentException if session is invalid or not found
     */
    public Session updateSession(Session session) {
        if (session == null || session.getId() == null) {
            throw new IllegalArgumentException("Session and ID cannot be null");
        }
        
        // Check if session exists
        Session existing = sessionRepository.findById(session.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Session not found: " + session.getId());
        }
        
        // Validate required fields
        validateSession(session);
        
        return sessionRepository.save(session);
    }
    
    /**
     * Validates session required fields.
     * Requirements: 6.2
     * @param session the session to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSession(Session session) {
        if (session.getDate() == null) {
            throw new IllegalArgumentException("Session date is required");
        }
        if (session.getStartTime() == null) {
            throw new IllegalArgumentException("Session start time is required");
        }
        if (session.getEndTime() == null) {
            throw new IllegalArgumentException("Session end time is required");
        }
        if (session.getVenue() == null || session.getVenue().trim().isEmpty()) {
            throw new IllegalArgumentException("Session venue is required");
        }
        if (session.getType() == null) {
            throw new IllegalArgumentException("Session type is required");
        }
        if (session.getCapacity() <= 0) {
            throw new IllegalArgumentException("Session capacity must be positive");
        }
        if (!session.getStartTime().isBefore(session.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
    
    /**
     * Updates the status of a session.
     * Requirements: 6.6
     * @param sessionId the session ID
     * @param status the new status
     * @return the updated session
     */
    public Session updateSessionStatus(Long sessionId, SessionStatus status) {
        if (sessionId == null || status == null) {
            throw new IllegalArgumentException("Session ID and status cannot be null");
        }
        
        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        session.setStatus(status);
        return sessionRepository.save(session);
    }

    
    // ==================== Delete Operations ====================
    
    /**
     * Deletes a session if it has no registrations.
     * Requirements: 6.4, 6.5
     * @param sessionId the session ID
     * @return true if deleted, false if session has registrations
     * @throws IllegalArgumentException if session not found
     */
    public boolean deleteSession(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        
        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        // Check if session has registrations
        if (hasRegistrations(sessionId)) {
            return false; // Cannot delete - has registrations
        }
        
        sessionRepository.delete(sessionId);
        return true;
    }
    
    /**
     * Deletes a session with confirmation (for sessions with registrations).
     * Requirements: 6.5
     * @param sessionId the session ID
     * @param confirmed whether deletion is confirmed
     * @return true if deleted
     * @throws IllegalArgumentException if session not found
     */
    public boolean deleteSessionWithConfirmation(Long sessionId, boolean confirmed) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        
        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        // If session has registrations, require confirmation
        if (hasRegistrations(sessionId) && !confirmed) {
            return false;
        }
        
        sessionRepository.delete(sessionId);
        return true;
    }
    
    /**
     * Checks if a session has any registrations.
     * @param sessionId the session ID
     * @return true if session has registrations
     */
    public boolean hasRegistrations(Long sessionId) {
        return registrationRepository.countBySessionId(sessionId) > 0;
    }
    
    /**
     * Gets the registration count for a session.
     * @param sessionId the session ID
     * @return the number of registrations
     */
    public long getRegistrationCount(Long sessionId) {
        return registrationRepository.countBySessionId(sessionId);
    }
    
    // ==================== Capacity Management ====================
    
    /**
     * Increments the registered count for a session.
     * Automatically updates status to FULL if capacity is reached.
     * Requirements: 7.4
     * @param sessionId the session ID
     * @return the updated session
     */
    public Session incrementRegistered(Long sessionId) {
        return sessionRepository.incrementRegistered(sessionId);
    }
    
    /**
     * Decrements the registered count for a session.
     * Automatically updates status to OPEN if was FULL.
     * @param sessionId the session ID
     * @return the updated session
     */
    public Session decrementRegistered(Long sessionId) {
        return sessionRepository.decrementRegistered(sessionId);
    }
    
    /**
     * Checks if a session has available slots.
     * @param sessionId the session ID
     * @return true if session has available slots
     */
    public boolean hasAvailableSlots(Long sessionId) {
        Session session = sessionRepository.findById(sessionId);
        return session != null && session.hasAvailableSlots();
    }
    
    // ==================== Statistics ====================
    
    /**
     * Counts total sessions.
     * @return total session count
     */
    public long countSessions() {
        return sessionRepository.count();
    }
    
    /**
     * Counts sessions by status.
     * @param status the status to count
     * @return count of sessions with the specified status
     */
    public long countByStatus(SessionStatus status) {
        return sessionRepository.countByStatus(status);
    }
    
    /**
     * Counts sessions by type.
     * @param type the type to count
     * @return count of sessions of the specified type
     */
    public long countByType(SessionType type) {
        return sessionRepository.countByType(type);
    }
}
