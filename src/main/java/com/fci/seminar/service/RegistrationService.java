package com.fci.seminar.service;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.repository.RegistrationRepository;
import com.fci.seminar.repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing student registrations.
 * Handles registration CRUD operations and status management.
 * Requirements: 3.4, 3.5, 3.6, 7.2
 */
public class RegistrationService {
    private static RegistrationService instance;
    
    private final RegistrationRepository registrationRepository;
    private final SessionRepository sessionRepository;
    
    /**
     * Private constructor for singleton pattern.
     */
    private RegistrationService() {
        this.registrationRepository = new RegistrationRepository();
        this.sessionRepository = new SessionRepository();
    }
    
    /**
     * Constructor for testing with custom repositories.
     */
    public RegistrationService(RegistrationRepository registrationRepository,
                               SessionRepository sessionRepository) {
        this.registrationRepository = registrationRepository;
        this.sessionRepository = sessionRepository;
    }
    
    /**
     * Gets the singleton instance of RegistrationService.
     */
    public static synchronized RegistrationService getInstance() {
        if (instance == null) {
            instance = new RegistrationService();
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
     * Gets all registrations.
     * @return list of all registrations
     */
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
    
    /**
     * Gets a registration by ID.
     * @param id the registration ID
     * @return the registration, or null if not found
     */
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }
    
    /**
     * Gets registrations by student ID.
     * Requirements: 3.6
     * @param studentId the student ID
     * @return list of registrations for the student
     */
    public List<Registration> getRegistrationsByStudent(Long studentId) {
        return registrationRepository.findByStudentId(studentId);
    }
    
    /**
     * Gets registrations by session ID.
     * @param sessionId the session ID
     * @return list of registrations for the session
     */
    public List<Registration> getRegistrationsBySession(Long sessionId) {
        return registrationRepository.findBySessionId(sessionId);
    }
    
    /**
     * Gets registrations by status.
     * @param status the registration status
     * @return list of registrations with the specified status
     */
    public List<Registration> getRegistrationsByStatus(RegistrationStatus status) {
        return registrationRepository.findByStatus(status);
    }
    
    /**
     * Gets pending registrations.
     * @return list of pending registrations
     */
    public List<Registration> getPendingRegistrations() {
        return registrationRepository.findPending();
    }
    
    /**
     * Gets approved registrations.
     * @return list of approved registrations
     */
    public List<Registration> getApprovedRegistrations() {
        return registrationRepository.findApproved();
    }
    
    /**
     * Gets unassigned registrations (approved but no session assigned).
     * @return list of unassigned registrations
     */
    public List<Registration> getUnassignedRegistrations() {
        return registrationRepository.findUnassigned();
    }

    
    // ==================== Registration Operations ====================
    
    /**
     * Registers a student for a seminar presentation.
     * Requirements: 3.4, 3.5
     * @param registration the registration to create
     * @return the created registration with PENDING status
     * @throws IllegalArgumentException if validation fails
     */
    public Registration register(Registration registration) {
        if (registration == null) {
            throw new IllegalArgumentException("Registration cannot be null");
        }
        
        // Validate required fields
        validateRegistration(registration);
        
        // Set default values
        registration.setId(null); // Ensure new ID is assigned
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setCreatedAt(LocalDateTime.now());
        
        return registrationRepository.save(registration);
    }
    
    /**
     * Validates registration required fields.
     * Requirements: 3.5
     * @param registration the registration to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistration(Registration registration) {
        if (registration.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        
        // Validate title
        if (registration.getResearchTitle() == null || 
            registration.getResearchTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Research title is required");
        }
        if (registration.getResearchTitle().length() > Registration.MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                "Research title exceeds maximum length of " + Registration.MAX_TITLE_LENGTH);
        }
        
        // Validate abstract
        if (registration.getAbstractText() == null || 
            registration.getAbstractText().trim().isEmpty()) {
            throw new IllegalArgumentException("Abstract is required");
        }
        if (registration.getAbstractText().length() > Registration.MAX_ABSTRACT_LENGTH) {
            throw new IllegalArgumentException(
                "Abstract exceeds maximum length of " + Registration.MAX_ABSTRACT_LENGTH);
        }
        
        // Validate supervisor name
        if (registration.getSupervisorName() == null || 
            registration.getSupervisorName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supervisor name is required");
        }
        
        // Validate presentation type
        if (registration.getPresentationType() == null) {
            throw new IllegalArgumentException("Presentation type is required");
        }
    }
    
    /**
     * Updates an existing registration.
     * @param registration the registration to update
     * @return the updated registration
     * @throws IllegalArgumentException if validation fails or not found
     */
    public Registration updateRegistration(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration and ID cannot be null");
        }
        
        Registration existing = registrationRepository.findById(registration.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Registration not found: " + registration.getId());
        }
        
        validateRegistration(registration);
        return registrationRepository.save(registration);
    }

    
    // ==================== Status Management ====================
    
    /**
     * Approves a registration.
     * Requirements: 3.6
     * @param registrationId the registration ID
     * @return the updated registration
     * @throws IllegalArgumentException if registration not found
     */
    public Registration approveRegistration(Long registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID cannot be null");
        }
        
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }
        
        registration.setStatus(RegistrationStatus.APPROVED);
        return registrationRepository.save(registration);
    }
    
    /**
     * Rejects a registration.
     * Requirements: 3.6
     * @param registrationId the registration ID
     * @return the updated registration
     * @throws IllegalArgumentException if registration not found
     */
    public Registration rejectRegistration(Long registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID cannot be null");
        }
        
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }
        
        registration.setStatus(RegistrationStatus.REJECTED);
        return registrationRepository.save(registration);
    }
    
    /**
     * Cancels a registration.
     * @param registrationId the registration ID
     * @return the updated registration
     * @throws IllegalArgumentException if registration not found
     */
    public Registration cancelRegistration(Long registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID cannot be null");
        }
        
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }
        
        // If was assigned to a session, decrement the session's registered count
        if (registration.getSessionId() != null) {
            sessionRepository.decrementRegistered(registration.getSessionId());
        }
        
        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setSessionId(null);
        return registrationRepository.save(registration);
    }

    
    // ==================== Session Assignment ====================
    
    /**
     * Assigns a registration to a session.
     * Requirements: 7.2
     * @param registrationId the registration ID
     * @param sessionId the session ID
     * @return the updated registration
     * @throws IllegalArgumentException if registration or session not found, or session is full
     */
    public Registration assignToSession(Long registrationId, Long sessionId) {
        if (registrationId == null || sessionId == null) {
            throw new IllegalArgumentException("Registration ID and Session ID cannot be null");
        }
        
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }
        
        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        // Check if session has available slots
        if (!session.hasAvailableSlots()) {
            throw new IllegalArgumentException("Session is full or not open for registration");
        }
        
        // Check if presentation type matches session type
        if (registration.getPresentationType() != session.getType()) {
            throw new IllegalArgumentException(
                "Presentation type does not match session type");
        }
        
        // If previously assigned to another session, decrement that session's count
        if (registration.getSessionId() != null && 
            !registration.getSessionId().equals(sessionId)) {
            sessionRepository.decrementRegistered(registration.getSessionId());
        }
        
        // Assign to new session and increment count
        registration.setSessionId(sessionId);
        sessionRepository.incrementRegistered(sessionId);
        
        // Auto-approve if pending
        if (registration.getStatus() == RegistrationStatus.PENDING) {
            registration.setStatus(RegistrationStatus.APPROVED);
        }
        
        return registrationRepository.save(registration);
    }
    
    /**
     * Unassigns a registration from its session.
     * @param registrationId the registration ID
     * @return the updated registration
     * @throws IllegalArgumentException if registration not found
     */
    public Registration unassignFromSession(Long registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID cannot be null");
        }
        
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }
        
        // Decrement session's registered count if assigned
        if (registration.getSessionId() != null) {
            sessionRepository.decrementRegistered(registration.getSessionId());
            registration.setSessionId(null);
            return registrationRepository.save(registration);
        }
        
        return registration;
    }
    
    // ==================== File Management ====================
    
    /**
     * Updates the file path for a registration.
     * @param registrationId the registration ID
     * @param filePath the file path
     * @return the updated registration
     */
    public Registration updateFilePath(Long registrationId, String filePath) {
        return registrationRepository.updateFilePath(registrationId, filePath);
    }
    
    // ==================== Statistics ====================
    
    /**
     * Counts total registrations.
     * @return total registration count
     */
    public long countRegistrations() {
        return registrationRepository.count();
    }
    
    /**
     * Counts registrations by status.
     * @param status the status to count
     * @return count of registrations with the specified status
     */
    public long countByStatus(RegistrationStatus status) {
        return registrationRepository.countByStatus(status);
    }
    
    /**
     * Checks if a student has already registered.
     * @param studentId the student ID
     * @return true if student has a registration
     */
    public boolean hasStudentRegistered(Long studentId) {
        return registrationRepository.existsByStudentId(studentId);
    }
}
