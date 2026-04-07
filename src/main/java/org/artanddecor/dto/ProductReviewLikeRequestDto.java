package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * ProductReviewLike Request DTO for Create operations
 * Simple request structure for liking/unliking reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewLikeRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Review ID is required") 
    private Long reviewId;
}