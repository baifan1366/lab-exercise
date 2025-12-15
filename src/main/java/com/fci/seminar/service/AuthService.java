package com.fci.seminar.service;

import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.Role;
import com.fci.seminar.repository.UserRepository;

/**
 * Singleton service for user authentication and session management.
 * Handles login, logout, and role-based access control.
 * Requirements: 1.3, 1.4, 1.5
 */
public class AuthService {
    private static AuthService instance;
    
    private final UserRepository userRepository;
    private User currentUser;
    private Role currentRole;
    
    /**
     * Private constructor for singleton pattern.
     */
    private AuthService() {
        this.userRepository = new UserRepository();
        this.currentUser = null;
        this.currentRole = Role.GUEST;
    }
    
    /**
     * Constructor for testing with custom UserRepository.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
        this.currentRole = Role.GUEST;
    }
    
    /**
     * Gets the singleton instance of AuthService.
     * @return the AuthService instance
     */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    
    /**
     * Authenticates a user with the given credentials.
     * Requirements: 1.3, 1.4
     * 
     * @param username the username
     * @param password the password
     * @param role the role to authenticate as
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(String username, String password, Role role) {
        // Guest role doesn't require authentication
        if (role == Role.GUEST) {
            logout();
            return true;
        }
        
        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            role == null) {
            return false;
        }
        
        // Find user by username and role
        User user = userRepository.findByUsernameAndRole(username, role);
        
        // Validate credentials
        if (user != null && password.equals(user.getPassword())) {
            this.currentUser = user;
            this.currentRole = user.getRole();
            return true;
        }
        
        // Authentication failed - maintain current session state
        return false;
    }
    
    /**
     * Logs out the current user and resets to Guest mode.
     * Requirements: 1.5
     */
    public void logout() {
        this.currentUser = null;
        this.currentRole = Role.GUEST;
    }
    
    /**
     * Gets the currently authenticated user.
     * @return the current user, or null if in Guest mode
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the current role.
     * @return the current role (GUEST if not authenticated)
     */
    public Role getCurrentRole() {
        return currentRole;
    }
    
    /**
     * Checks if a user is currently authenticated.
     * @return true if authenticated, false if in Guest mode
     */
    public boolean isAuthenticated() {
        return currentUser != null && currentRole != Role.GUEST;
    }
    
    /**
     * Checks if the current user has a specific permission.
     * @param permission the permission to check
     * @return true if the user has the permission
     */
    public boolean hasPermission(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            return false;
        }
        
        switch (currentRole) {
            case COORDINATOR:
                // Coordinator has all permissions
                return true;
            case EVALUATOR:
                return isEvaluatorPermission(permission);
            case STUDENT:
                return isStudentPermission(permission);
            case GUEST:
            default:
                return isGuestPermission(permission);
        }
    }
    
    /**
     * Checks if the permission is allowed for Guest role.
     */
    private boolean isGuestPermission(String permission) {
        return "VIEW_SCHEDULE".equals(permission) ||
               "VIEW_SESSIONS".equals(permission);
    }
    
    /**
     * Checks if the permission is allowed for Student role.
     */
    private boolean isStudentPermission(String permission) {
        return isGuestPermission(permission) ||
               "REGISTER".equals(permission) ||
               "UPLOAD_FILE".equals(permission) ||
               "VIEW_OWN_REGISTRATION".equals(permission);
    }
    
    /**
     * Checks if the permission is allowed for Evaluator role.
     */
    private boolean isEvaluatorPermission(String permission) {
        return isGuestPermission(permission) ||
               "VIEW_ASSIGNED_PRESENTATIONS".equals(permission) ||
               "EVALUATE".equals(permission) ||
               "SAVE_DRAFT".equals(permission) ||
               "SUBMIT_EVALUATION".equals(permission);
    }
    
    /**
     * Checks if the current user has the specified role.
     * @param role the role to check
     * @return true if the current role matches
     */
    public boolean hasRole(Role role) {
        return currentRole == role;
    }
    
    /**
     * Checks if the current user has at least the specified role level.
     * Role hierarchy: GUEST < STUDENT < EVALUATOR < COORDINATOR
     * @param minimumRole the minimum required role
     * @return true if the current role is at or above the minimum
     */
    public boolean hasMinimumRole(Role minimumRole) {
        if (minimumRole == null) return true;
        
        int currentLevel = getRoleLevel(currentRole);
        int requiredLevel = getRoleLevel(minimumRole);
        
        return currentLevel >= requiredLevel;
    }
    
    /**
     * Gets the numeric level of a role for comparison.
     */
    private int getRoleLevel(Role role) {
        switch (role) {
            case COORDINATOR: return 4;
            case EVALUATOR: return 3;
            case STUDENT: return 2;
            case GUEST:
            default: return 1;
        }
    }
}
