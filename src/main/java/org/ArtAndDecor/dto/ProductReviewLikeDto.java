package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductReviewLike DTO for API requests and responses
 * Contains information from PRODUCT_REVIEW_LIKE table
 * Represents users liking product reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewLikeDto {
    
    private Long productReviewLikeId;
    
    @NotNull(message = "Review ID is required")
    private Long reviewId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (for response)
    // Note: Only include review and user, avoid circular reference by not including ProductReviewLike in ReviewDto
    // =============================================
    private ReviewDto review;
    private UserDto user;
    
    /**
     * Check if this like is active (not deleted)
     */
    public boolean isActive() {
        return productReviewLikeId != null;
    }
}
