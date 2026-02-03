package org.ArtAndDecor.exception;

/**
 * Custom exception for validation errors
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(400, message);
    }

    public ValidationException(String fieldName, Object fieldValue, String reason) {
        super(400, String.format("Validation failed for %s with value '%s': %s", fieldName, fieldValue, reason));
    }

    public ValidationException(String message, Throwable cause) {
        super(400, message, cause);
    }
}