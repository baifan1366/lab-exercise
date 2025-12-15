package com.fci.seminar.model.enums;

/**
 * Status of a seminar session.
 * Indicates the registration availability for each session.
 */
public enum SessionStatus {
    OPEN,              // Open for registration
    FULL,              // Capacity reached
    CLOSED,            // Registration closed
    REQUIRES_APPROVAL  // Registration requires coordinator approval
}
