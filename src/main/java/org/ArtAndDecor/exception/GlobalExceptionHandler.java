package org.ArtAndDecor.exception;

import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.utils.ResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Global exception handler for all API endpoints
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleBusinessException(BusinessException ex, WebRequest request) {
        logger.error("Business exception occurred: {}", ex.getErrorMessage(), ex);
        
        BaseResponseDto<Object> response = BaseResponseDto.error(ex.getErrorCode(), ex.getErrorMessage());
        ResponseUtils.logResponse(ex.getErrorCode(), ex.getErrorMessage(), null);
        
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode()));
    }

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getErrorMessage());
        
        BaseResponseDto<Object> response = BaseResponseDto.notFound(ex.getErrorMessage());
        ResponseUtils.logResponse(ex.getErrorCode(), ex.getErrorMessage(), null);
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation error: {}", ex.getErrorMessage());
        
        BaseResponseDto<Object> response = BaseResponseDto.badRequest(ex.getErrorMessage());
        ResponseUtils.logResponse(ex.getErrorCode(), ex.getErrorMessage(), null);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Bean Validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.error("Method argument validation failed");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            logger.debug("Validation error - Field: {}, Error: {}", fieldName, errorMessage);
        });

        String message = "Validation failed for " + errors.size() + " field(s)";
        BaseResponseDto<Object> response = new BaseResponseDto<>(
            400, 
            message, 
            errors
        );
        
        ResponseUtils.logResponse(400, message, errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Constraint Validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        logger.error("Constraint validation failed");
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
            logger.debug("Constraint violation - Field: {}, Error: {}", fieldName, errorMessage);
        }

        String message = "Constraint validation failed for " + errors.size() + " field(s)";
        BaseResponseDto<Object> response = new BaseResponseDto<>(
            400,
            message,
            errors
        );
        
        ResponseUtils.logResponse(400, message, errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal argument: {}", ex.getMessage());
        
        BaseResponseDto<Object> response = BaseResponseDto.badRequest("Invalid argument: " + ex.getMessage());
        ResponseUtils.logResponse(400, ex.getMessage(), null);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto<Object>> handleGeneralException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        BaseResponseDto<Object> response = BaseResponseDto.serverError("Internal server error occurred");
        ResponseUtils.logResponse(500, "Internal server error", null);
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}