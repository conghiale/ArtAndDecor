package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Review Request DTO for Create and Update operations
 * Separated from ReviewDto to have clear request/response distinction
 * Contains only fields that can be modified by user input
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "User ID is required") 
    private Long userId;

    private Long parentReviewId; // Optional - null for top-level reviews

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Review content is required")
    @Size(max = 65535, message = "Review content must not exceed 65535 characters")
    private String reviewContent;

    /**
     * For admin operations - visibility control
     * Default will be true if not specified
     */
    private Boolean isVisible;

    /**
     * Get effective visibility value (default true if not set)
     */
    public Boolean getEffectiveIsVisible() {
        return isVisible != null ? isVisible : true;
    }
}