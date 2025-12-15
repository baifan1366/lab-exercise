package com.fci.seminar.model.enums;

/**
 * User roles in the Seminar Management System.
 * Defines the access levels and permissions for different user types.
 */
public enum Role {
    GUEST,       // Read-only access - view schedule and availability
    STUDENT,     // Limited write - register and submit materials
    EVALUATOR,   // Controlled write - evaluate assigned presentations
    COORDINATOR  // Full control - manage entire system
}
