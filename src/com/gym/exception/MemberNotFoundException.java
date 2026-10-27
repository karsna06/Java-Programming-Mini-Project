package com.gym.exception;

/**
 * Thrown when a member is not found in the system.
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
