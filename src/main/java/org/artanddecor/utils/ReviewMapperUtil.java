package org.artanddecor.utils;

import org.artanddecor.dto.ReviewDto;
import org.artanddecor.model.Review;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping Review entities to ReviewDto objects
 * Supports clean architecture pattern with nested DTOs
 */
@Component
public class ReviewMapperUtil {

    // Note: Using static references to other mappers for better performance
    // No need for @Autowired dependencies
    
    // =============================================
    // REVIEW MAPPING METHODS
    // =============================================
    
    /**
     * Convert Review entity to ReviewDto with foreign keys only (for basic operations)
     * @param review Review entity
     * @return ReviewDto with only basic fields and foreign key IDs
     */
    public ReviewDto toBasicDto(Review review) {
        if (review == null) return null;
        
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setParentReviewId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null);
        dto.setRootReviewId(review.getRootReview() != null ? review.getRootReview().getReviewId() : null);
        dto.setReviewLevel(review.getReviewLevel());
        dto.setRating(review.getRating());
        dto.setReviewContent(review.getReviewContent());
        dto.setCountLike(review.getCountLike());
        dto.setIsVisible(review.getIsVisible());
        dto.setIsDeleted(review.getIsDeleted());
        dto.setCreatedDt(review.getCreatedDt());
        dto.setModifiedDt(review.getModifiedDt());
        
        return dto;
    }
    
    /**
     * Convert Review entity to ReviewDto with nested DTOs
     * @param review Review entity
     * @return ReviewDto with foreign keys and nested DTO objects
     */
    public ReviewDto toDto(Review review) {
        if (review == null) return null;
        
        ReviewDto dto = toBasicDto(review);
        
        // Add nested DTOs for related entities
        if (review.getUser() != null) {
            dto.setUser(UserMapperUtil.toBasicDto(review.getUser()));
        }
        
        if (review.getProduct() != null) {
            dto.setProduct(ProductMapperUtil.toProductDto(review.getProduct()));
        }
        
        if (review.getParentReview() != null) {
            dto.setParentReview(toBasicDto(review.getParentReview()));
        }
        
        return dto;
    }

    /**
     * Convert Review entity to ReviewDto with full nested DTOs (including circular references handled)
     * @param review Review entity
     * @return ReviewDto with comprehensive nested data
     */
    public ReviewDto toFullDto(Review review) {
        if (review == null) return null;
        
        ReviewDto dto = toBasicDto(review);
        
        // Add nested DTOs with full details
        if (review.getUser() != null) {
            // Use static method to avoid deep nesting
            dto.setUser(UserMapperUtil.toBasicDto(review.getUser()));
        }
        
        if (review.getProduct() != null) {
            // Use static method to avoid deep nesting
            dto.setProduct(ProductMapperUtil.toProductDto(review.getProduct()));
        }
        
        if (review.getParentReview() != null) {
            // For parent review, use basic DTO to avoid circular reference
            dto.setParentReview(toBasicDto(review.getParentReview()));
        }
        
        return dto;
    }

    /**
     * Convert ReviewDto to Review entity (for save operations)
     * @param reviewDto ReviewDto object
     * @return Review entity
     */
    public Review toEntity(ReviewDto reviewDto) {
        if (reviewDto == null) return null;
        
        Review review = new Review();
        review.setReviewId(reviewDto.getReviewId());
        review.setReviewLevel(reviewDto.getReviewLevel());
        review.setRating((byte) reviewDto.getRating());
        review.setReviewContent(reviewDto.getReviewContent());
        review.setCountLike(reviewDto.getCountLike());
        review.setIsVisible(reviewDto.getIsVisible());
        review.setIsDeleted(reviewDto.getIsDeleted());
        review.setCreatedDt(reviewDto.getCreatedDt());
        review.setModifiedDt(reviewDto.getModifiedDt());
        
        // Note: Related entities (User, Product, ParentReview, etc.) should be set separately
        // in the service layer to avoid potential issues with entity management
        
        return review;
    }

    /**
     * Update Review entity from ReviewDto (for update operations)
     * @param review Existing Review entity
     * @param reviewDto ReviewDto with updated data
     */
    public void updateEntityFromDto(Review review, ReviewDto reviewDto) {
        if (review == null || reviewDto == null) return;
        
        // Update only modifiable fields
        if (reviewDto.getReviewContent() != null) {
            review.setReviewContent(reviewDto.getReviewContent());
        }
        if (reviewDto.getRating() != 0) {
            review.setRating((byte) reviewDto.getRating());
        }
        if (reviewDto.getIsVisible() != null) {
            review.setIsVisible(reviewDto.getIsVisible());
        }
        if (reviewDto.getIsDeleted() != null) {
            review.setIsDeleted(reviewDto.getIsDeleted());
        }
        
        // Don't update: reviewId, userId, productId, parentReviewId, rootReviewId, 
        // reviewLevel, countLike, createdDt (these should be managed by business logic)
    }
}