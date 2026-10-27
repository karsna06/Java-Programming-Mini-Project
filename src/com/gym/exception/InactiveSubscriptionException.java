package com.gym.exception;

/**
 * Thrown when an operation requires an active subscription but none exists.
 */
public class InactiveSubscriptionException extends RuntimeException {
    public InactiveSubscriptionException(String message) {
        super(message);
    }
}
