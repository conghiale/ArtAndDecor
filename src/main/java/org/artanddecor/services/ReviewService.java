package org.artanddecor.services;

import org.artanddecor.dto.ReviewDto;
import org.artanddecor.dto.ReviewRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Review Service Interface
 * Business logic for Review management
 */
public interface ReviewService {

    /**
     * Get all reviews with pagination
     */
    Page<ReviewDto> getAllReviews(int page, int size, String sortBy, String sortDir);

    /**
     * Get review by ID
     */
    Optional<ReviewDto> getReviewById(Long reviewId);

    /**
     * Get reviews with filters
     */
    Page<ReviewDto> getReviewsWithFilters(
            Long userId, Long productId, Long parentReviewId, Long rootReviewId,
            Byte rating, Integer minCountLike, Boolean isVisible, Boolean isDeleted,
            String searchText, int page, int size, String sortBy, String sortDir);

    /**
     * Get reviews by product ID
     */
    Page<ReviewDto> getReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir);

    /**
     * Get reviews by user ID
     */
    Page<ReviewDto> getReviewsByUserId(Long userId, int page, int size, String sortBy, String sortDir);

    /**
     * Get top-level reviews for a product (no parent)
     */
    Page<ReviewDto> getTopLevelReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir);

    /**
     * Get reply reviews for a parent review
     */
    Page<ReviewDto> getReplyReviews(Long parentReviewId, int page, int size);

    /**
     * Get recent reviews for a product
     */
    List<ReviewDto> getRecentReviewsByProductId(Long productId);

    /**
     * Get review statistics for a product
     */
    ReviewStatisticsDto getReviewStatistics(Long productId);

    /**
     * Create new review (Customer & Admin access)
     */
    ReviewDto createReview(ReviewRequestDto requestDto);

    /**
     * Update existing review (Admin only)
     */
    ReviewDto updateReview(Long reviewId, ReviewRequestDto requestDto);

    /**
     * Update review visibility status (Admin only)
     */
    ReviewDto updateVisibilityStatus(Long reviewId, boolean isVisible);

    /**
     * Soft delete review (Admin only)
     */
    ReviewDto softDeleteReview(Long reviewId);

    /**
     * Inner class for review statistics
     */
    class ReviewStatisticsDto {
        private Long productId;
        private Long totalReviews;
        private Double averageRating;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long productId;
            private Long totalReviews;
            private Double averageRating;

            public Builder productId(Long productId) {
                this.productId = productId;
                return this;
            }

            public Builder totalReviews(Long totalReviews) {
                this.totalReviews = totalReviews;
                return this;
            }

            public Builder averageRating(Double averageRating) {
                this.averageRating = averageRating;
                return this;
            }

            public ReviewStatisticsDto build() {
                ReviewStatisticsDto dto = new ReviewStatisticsDto();
                dto.productId = this.productId;
                dto.totalReviews = this.totalReviews;
                dto.averageRating = this.averageRating;
                return dto;
            }
        }

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }
}