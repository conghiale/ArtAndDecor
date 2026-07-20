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

    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Long productId;

    @NotNull(message = "ID người dùng là bắt buộc") 
    private Long userId;

    private Long parentReviewId; // Optional - null for top-level reviews

    @NotNull(message = "Điểm đánh giá là bắt buộc")
    @Min(value = 1, message = "Điểm đánh giá phải lớn hơn hoặc bằng 1")
    @Max(value = 5, message = "Điểm đánh giá không được vượt quá 5")
    private Integer rating;

    @NotBlank(message = "Nội dung đánh giá là bắt buộc")
    @Size(max = 65535, message = "Nội dung đánh giá không được vượt quá 65535 ký tự")
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