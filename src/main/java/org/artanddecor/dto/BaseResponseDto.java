package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base response DTO for all API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDto<T> {
    
    private int code;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Constructor for response with data
     * @param code Response code
     * @param message Response message
     * @param data Response data
     */
    public BaseResponseDto(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor for response without data
     * @param code Response code
     * @param message Response message
     */
    public BaseResponseDto(int code, String message) {
        this(code, message, null);
    }

    /**
     * Create success response with data
     * @param data Response data
     * @param <T> Data type
     * @return Success response
     */
    public static <T> BaseResponseDto<T> success(T data) {
        return new BaseResponseDto<>(200, "Success", data);
    }

    /**
     * Create success response with custom message and data
     * @param message Custom message
     * @param data Response data
     * @param <T> Data type
     * @return Success response
     */
    public static <T> BaseResponseDto<T> success(String message, T data) {
        return new BaseResponseDto<>(200, message, data);
    }

    /**
     * Create success response without data
     * @param message Success message
     * @param <T> Data type
     * @return Success response
     */
    public static <T> BaseResponseDto<T> success(String message) {
        return new BaseResponseDto<>(200, message, null);
    }

    /**
     * Create error response
     * @param code Error code
     * @param message Error message
     * @param <T> Data type
     * @return Error response
     */
    public static <T> BaseResponseDto<T> error(int code, String message) {
        return new BaseResponseDto<>(code, message, null);
    }

    /**
     * Create bad request response
     * @param message Error message
     * @param <T> Data type
     * @return Bad request response
     */
    public static <T> BaseResponseDto<T> badRequest(String message) {
        return new BaseResponseDto<>(400, message, null);
    }

    /**
     * Create unauthorized response
     * @param message Error message
     * @param <T> Data type
     * @return Unauthorized response
     */
    public static <T> BaseResponseDto<T> unauthorized(String message) {
        return new BaseResponseDto<>(401, message, null);
    }

    /**
     * Create forbidden response
     * @param message Error message
     * @param <T> Data type
     * @return Forbidden response
     */
    public static <T> BaseResponseDto<T> forbidden(String message) {
        return new BaseResponseDto<>(403, message, null);
    }

    /**
     * Create not found response
     * @param message Error message
     * @param <T> Data type
     * @return Not found response
     */
    public static <T> BaseResponseDto<T> notFound(String message) {
        return new BaseResponseDto<>(404, message, null);
    }

    /**
     * Create internal server error response
     * @param message Error message
     * @param <T> Data type
     * @return Internal server error response
     */
    public static <T> BaseResponseDto<T> serverError(String message) {
        return new BaseResponseDto<>(500, message, null);
    }
}