package com.gym;

/**
 * Thrown when a payment is attempted without a valid unpaid invoice.
 */
public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
