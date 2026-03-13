package org.ArtAndDecor.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Response utility class for API responses
 * Provides standardized response codes and messages
 */
@Component
public class ResponseUtils {
    
    private static final Logger logger = LogManager.getLogger(ResponseUtils.class);

    // Standard HTTP Response codes
    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;
    
    // Custom business response codes
    public static final int INVALID_PARAM = 1001;
    public static final int MISSING_PARAM = 1002;
    public static final int DUPLICATE_DATA = 1003;
    public static final int DATA_NOT_FOUND = 1004;
    public static final int UPDATED = 1005;
    public static final int DELETED = 1006;

    /**
     * Get response message by code (Vietnamese default)
     * @param code Response code (HTTP code or custom code)
     * @return Response message
     */
    public static String getResponseMessage(int code) {
        return getDefaultMessage(code);
    }

    /**
     * Get English response message by code
     * @param code Response code (HTTP code or custom code)
     * @return English response message
     */
    public static String getEnglishResponseMessage(int code) {
        return getDefaultEnglishMessage(code);
    }

    /**
     * Fallback method for default Vietnamese messages
     * @param code Response code
     * @return Default Vietnamese message
     */
    private static String getDefaultMessage(int code) {
        switch (code) {
            case SUCCESS:
                return "Thành công";
            case CREATED:
                return "Tạo thành công";
            case NO_CONTENT:
                return "Không có nội dung";
            case BAD_REQUEST:
                return "Yêu cầu không hợp lệ";
            case UNAUTHORIZED:
                return "Không có quyền truy cập";
            case FORBIDDEN:
                return "Truy cập bị cấm";
            case NOT_FOUND:
                return "Không tìm thấy";
            case CONFLICT:
                return "Xung đột dữ liệu";
            case INTERNAL_SERVER_ERROR:
                return "Lỗi máy chủ nội bộ";
            case SERVICE_UNAVAILABLE:
                return "Dịch vụ không khả dụng";
            case INVALID_PARAM:
                return "Tham số không hợp lệ";
            case MISSING_PARAM:
                return "Thiếu tham số bắt buộc";
            case DUPLICATE_DATA:
                return "Dữ liệu trùng lặp";
            case DATA_NOT_FOUND:
                return "Không tìm thấy dữ liệu";
            case UPDATED:
                return "Cập nhật thành công";
            case DELETED:
                return "Xóa thành công";
            default:
                return "Mã phản hồi không xác định";
        }
    }

    /**
     * Fallback method for default English messages
     * @param code Response code
     * @return Default English message
     */
    private static String getDefaultEnglishMessage(int code) {
        switch (code) {
            case SUCCESS:
                return "Success";
            case CREATED:
                return "Created successfully";
            case NO_CONTENT:
                return "No content";
            case BAD_REQUEST:
                return "Bad request";
            case UNAUTHORIZED:
                return "Unauthorized";
            case FORBIDDEN:
                return "Access forbidden";
            case NOT_FOUND:
                return "Not found";
            case CONFLICT:
                return "Conflict occurred";
            case INTERNAL_SERVER_ERROR:
                return "Internal server error";
            case SERVICE_UNAVAILABLE:
                return "Service unavailable";
            case INVALID_PARAM:
                return "Invalid parameters";
            case MISSING_PARAM:
                return "Missing required parameters";
            case DUPLICATE_DATA:
                return "Duplicate data";
            case DATA_NOT_FOUND:
                return "Data not found";
            case UPDATED:
                return "Updated successfully";
            case DELETED:
                return "Deleted successfully";
            default:
                return "Unknown response code";
        }
    }

    /**
     * Check if response code indicates success
     * @param code Response code
     * @return true if success code (2xx or successful custom codes)
     */
    public static boolean isSuccessCode(int code) {
        return (code >= 200 && code < 300) || code == UPDATED || code == DELETED;
    }

    /**
     * Check if response code indicates client error
     * @param code Response code
     * @return true if client error code (4xx or client error custom codes)
     */
    public static boolean isClientError(int code) {
        return (code >= 400 && code < 500) || 
               code == INVALID_PARAM || code == MISSING_PARAM || 
               code == DUPLICATE_DATA || code == DATA_NOT_FOUND;
    }

    /**
     * Check if response code indicates server error
     * @param code Response code
     * @return true if server error code (5xx)
     */
    public static boolean isServerError(int code) {
        return code >= 500 && code < 600;
    }

    /**
     * Log response information with message lookup from database
     * @param code Response code
     * @param data Response data (optional)
     */
    public static void logResponse(int code, Object data) {
        String message = getResponseMessage(code);
        logResponse(code, message, data);
    }

    /**
     * Log response information with custom message
     * @param code Response code
     * @param message Custom message
     * @param data Response data (optional)
     */
    public static void logResponse(int code, String message, Object data) {
        if (isSuccessCode(code)) {
            if (data != null) {
                logger.info("Response [{}]: {} - Data: {}", code, message, data.getClass().getSimpleName());
            } else {
                logger.info("Response [{}]: {}", code, message);
            }
        } else if (isClientError(code)) {
            logger.warn("Client Error [{}]: {}", code, message);
        } else if (isServerError(code)) {
            logger.error("Server Error [{}]: {}", code, message);
        } else {
            logger.debug("Response [{}]: {}", code, message);
        }
    }
}