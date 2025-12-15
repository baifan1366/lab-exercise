package com.fci.seminar.repository;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.util.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Registration entity operations.
 * Handles CRUD operations and filtering for student registrations.
 * Requirements: 3.4, 3.6
 */
public class RegistrationRepository {
    private final DataManager dataManager;
    
    public RegistrationRepository() {
        this.dataManager = DataManager.getInstance();
    }
    
    /**
     * Constructor for testing with custom DataManager.
     */
    public RegistrationRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ==================== Find Operations ====================
    
    /**
     * Finds a registration by ID.
     */
    public Registration findById(Long id) {
        if (id == null) return null;
        
        for (Registration registration : dataManager.getRegistrations()) {
            if (id.equals(registration.getId())) {
                return registration;
            }
        }
        return null;
    }
    
    /**
     * Finds all registrations.
     */
    public List<Registration> findAll() {
        return dataManager.getRegistrations();
    }
    
    /**
     * Finds registrations by student ID.
     * Requirements: 3.6
     */
    public List<Registration> findByStudentId(Long studentId) {
        if (studentId == null) return new ArrayList<>();
        
        return dataManager.getRegistrations().stream()
                .filter(r -> studentId.equals(r.getStudentId()))
                .collect(Collectors.toList());
    }

    
    /**
     * Finds registrations by session ID.
     */
    public List<Registration> findBySessionId(Long sessionId) {
        if (sessionId == null) return new ArrayList<>();
        
        return dataManager.getRegistrations().stream()
                .filter(r -> sessionId.equals(r.getSessionId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations by status.
     * Requirements: 3.6
     */
    public List<Registration> findByStatus(RegistrationStatus status) {
        if (status == null) return new ArrayList<>();
        
        return dataManager.getRegistrations().stream()
                .filter(r -> status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds pending registrations.
     */
    public List<Registration> findPending() {
        return findByStatus(RegistrationStatus.PENDING);
    }
    
    /**
     * Finds approved registrations.
     */
    public List<Registration> findApproved() {
        return findByStatus(RegistrationStatus.APPROVED);
    }
    
    /**
     * Finds registrations by student ID and status.
     */
    public List<Registration> findByStudentIdAndStatus(Long studentId, RegistrationStatus status) {
        if (studentId == null || status == null) return new ArrayList<>();
        
        return dataManager.getRegistrations().stream()
                .filter(r -> studentId.equals(r.getStudentId()) && status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations by session ID and status.
     */
    public List<Registration> findBySessionIdAndStatus(Long sessionId, RegistrationStatus status) {
        if (sessionId == null || status == null) return new ArrayList<>();
        
        return dataManager.getRegistrations().stream()
                .filter(r -> sessionId.equals(r.getSessionId()) && status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations without an assigned session.
     */
    public List<Registration> findUnassigned() {
        return dataManager.getRegistrations().stream()
                .filter(r -> r.getSessionId() == null && r.getStatus() == RegistrationStatus.APPROVED)
                .collect(Collectors.toList());
    }

    
    // ==================== Save Operations ====================
    
    /**
     * Saves a registration (creates new or updates existing).
     * Requirements: 3.4
     */
    public Registration save(Registration registration) {
        if (registration == null) return null;
        
        List<Registration> registrations = dataManager.getRegistrations();
        
        if (registration.getId() == null) {
            // New registration - assign ID
            registration.setId(dataManager.getNextId(Registration.class));
            registrations.add(registration);
        } else {
            // Update existing registration
            boolean found = false;
            for (int i = 0; i < registrations.size(); i++) {
                if (registration.getId().equals(registrations.get(i).getId())) {
                    registrations.set(i, registration);
                    found = true;
                    break;
                }
            }
            if (!found) {
                registrations.add(registration);
            }
        }
        
        dataManager.setRegistrations(registrations);
        dataManager.saveRegistrations();
        return registration;
    }
    
    // ==================== Delete Operations ====================
    
    /**
     * Deletes a registration by ID.
     */
    public void delete(Long id) {
        if (id == null) return;
        
        List<Registration> registrations = dataManager.getRegistrations();
        if (registrations.removeIf(r -> id.equals(r.getId()))) {
            dataManager.setRegistrations(registrations);
            dataManager.saveRegistrations();
        }
    }
    
    /**
     * Deletes a registration.
     */
    public void delete(Registration registration) {
        if (registration == null || registration.getId() == null) return;
        delete(registration.getId());
    }
    
    // ==================== Update Operations ====================
    
    /**
     * Updates the status of a registration.
     * Requirements: 3.6
     */
    public Registration updateStatus(Long id, RegistrationStatus status) {
        Registration registration = findById(id);
        if (registration != null) {
            registration.setStatus(status);
            return save(registration);
        }
        return null;
    }
    
    /**
     * Assigns a registration to a session.
     */
    public Registration assignToSession(Long registrationId, Long sessionId) {
        Registration registration = findById(registrationId);
        if (registration != null) {
            registration.setSessionId(sessionId);
            return save(registration);
        }
        return null;
    }
    
    /**
     * Updates the file path for a registration.
     */
    public Registration updateFilePath(Long id, String filePath) {
        Registration registration = findById(id);
        if (registration != null) {
            registration.setFilePath(filePath);
            return save(registration);
        }
        return null;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Checks if a registration exists by ID.
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    
    /**
     * Counts total registrations.
     */
    public long count() {
        return dataManager.getRegistrations().size();
    }
    
    /**
     * Counts registrations by status.
     */
    public long countByStatus(RegistrationStatus status) {
        if (status == null) return 0;
        return dataManager.getRegistrations().stream()
                .filter(r -> status.equals(r.getStatus()))
                .count();
    }
    
    /**
     * Counts registrations by session ID.
     */
    public long countBySessionId(Long sessionId) {
        if (sessionId == null) return 0;
        return dataManager.getRegistrations().stream()
                .filter(r -> sessionId.equals(r.getSessionId()))
                .count();
    }
    
    /**
     * Checks if a student has already registered.
     */
    public boolean existsByStudentId(Long studentId) {
        if (studentId == null) return false;
        return dataManager.getRegistrations().stream()
                .anyMatch(r -> studentId.equals(r.getStudentId()));
    }
}
