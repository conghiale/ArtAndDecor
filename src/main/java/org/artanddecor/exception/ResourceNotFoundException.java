package org.artanddecor.exception;

/**
 * Custom exception for resource not found errors
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(404, String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}