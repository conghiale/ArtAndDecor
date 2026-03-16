package org.artanddecor.services;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ReviewDto;
import org.artanddecor.model.Review;
import org.artanddecor.repository.ReviewRepository;
import org.artanddecor.utils.ReviewMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Review Service
 * Business logic for Review management
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    private final ReviewMapperUtil reviewMapperUtil;

    /**
     * Get all reviews with pagination
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getAllReviews(int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting all reviews - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get review by ID
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDto> getReviewById(Long reviewId) {
        logger.debug("Getting review by ID: {}", reviewId);
        
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews with filters
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsWithFilters(
            Long userId, Long productId, Long parentReviewId, Long rootReviewId,
            Byte rating, Integer minCountLike, Boolean isVisible, Boolean isDeleted,
            String searchText, int page, int size, String sortBy, String sortDir) {
        
        logger.debug("Getting reviews with filters - userId: {}, productId: {}, rating: {}, searchText: {}", 
                    userId, productId, rating, searchText);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findReviewsWithFilters(
            userId, productId, parentReviewId, rootReviewId, 
            rating, minCountLike, isVisible, isDeleted, searchText, pageable);
            
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews by product ID
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting reviews for product ID: {}", productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByProduct_ProductId(productId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews by user ID
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByUserId(Long userId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting reviews for user ID: {}", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByUser_UserId(userId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get top-level reviews for a product (no parent)
     */
    @Transactional(readOnly = true) 
    public Page<ReviewDto> getTopLevelReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting top-level reviews for product ID: {}", productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByProduct_ProductIdAndParentReviewIsNull(productId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reply reviews for a parent review
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReplyReviews(Long parentReviewId, int page, int size) {
        logger.debug("Getting reply reviews for parent review ID: {}", parentReviewId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByParentReview_ReviewIdOrderByCreatedDtAsc(parentReviewId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get recent reviews for a product
     */
    @Transactional(readOnly = true)
    public List<ReviewDto> getRecentReviewsByProductId(Long productId) {
        logger.debug("Getting recent reviews for product ID: {}", productId);
        
        List<Review> reviews = reviewRepository.findTop10ByProduct_ProductIdAndIsDeletedFalseAndIsVisibleTrueOrderByCreatedDtDesc(productId);
        return reviews.stream()
                     .map(reviewMapperUtil::toDto)
                     .collect(Collectors.toList());
    }

    /**
     * Get review statistics for a product
     */
    @Transactional(readOnly = true)
    public ReviewStatisticsDto getReviewStatistics(Long productId) {
        logger.debug("Getting review statistics for product ID: {}", productId);
        
        Long totalReviews = reviewRepository.countByProduct_ProductId(productId);
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        
        return ReviewStatisticsDto.builder()
                .productId(productId)
                .totalReviews(totalReviews)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .build();
    }

    /**
     * Inner class for review statistics
     */
    public static class ReviewStatisticsDto {
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