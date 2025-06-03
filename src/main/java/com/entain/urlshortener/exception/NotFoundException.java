package com.entain.urlshortener.exception;

/**
 * Exception to indicate that the requested resource was not found.
 * This exception is a custom runtime exception typically used in the context
 * of the application when a resource, such as a URL short code, is not found.
 * It is caught and handled by a global exception handler to provide
 * appropriate error responses.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}