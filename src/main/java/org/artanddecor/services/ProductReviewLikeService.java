package org.artanddecor.services;

import org.artanddecor.dto.ProductReviewLikeDto;
import org.artanddecor.dto.ProductReviewLikeRequestDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * ProductReviewLike Service Interface
 * Business logic for ProductReviewLike management
 */
public interface ProductReviewLikeService {

    /**
     * Get all product review likes with pagination
     */
    Page<ProductReviewLikeDto> getAllProductReviewLikes(int page, int size, String sortBy, String sortDir);

    /**
     * Get product review like by ID
     */
    Optional<ProductReviewLikeDto> getProductReviewLikeById(Long likeId);

    /**
     * Get product review likes with filters
     */
    Page<ProductReviewLikeDto> getProductReviewLikesWithFilters(
            Long userId, Long reviewId, int page, int size, String sortBy, String sortDir);

    /**
     * Get product review likes by user ID
     */
    Page<ProductReviewLikeDto> getProductReviewLikesByUserId(Long userId, int page, int size, String sortBy, String sortDir);

    /**
     * Get product review likes by review ID
     */
    Page<ProductReviewLikeDto> getProductReviewLikesByReviewId(Long reviewId, int page, int size, String sortBy, String sortDir);

    /**
     * Get specific product review like by user and review
     */
    Optional<ProductReviewLikeDto> getProductReviewLikeByUserAndReview(Long userId, Long reviewId);

    /**
     * Check if user has liked a review
     */
    boolean hasUserLikedReview(Long userId, Long reviewId);

    /**
     * Get likes count for a review
     */
    Long getLikesCountByReviewId(Long reviewId);

    /**
     * Get total likes count by a user
     */
    Long getLikesCountByUserId(Long userId);

    /**
     * Get likes by product ID
     */
    Page<ProductReviewLikeDto> getProductReviewLikesByProductId(Long productId, int page, int size, String sortBy, String sortDir);

    /**
     * Get likes by user for a specific product
     */
    Page<ProductReviewLikeDto> getProductReviewLikesByUserIdAndProductId(
            Long userId, Long productId, int page, int size, String sortBy, String sortDir);

    /**
     * Create new product review like (permitAll - accessible to all users)
     */
    ProductReviewLikeDto createProductReviewLike(ProductReviewLikeRequestDto requestDto);

    /**
     * Delete product review like (permitAll - accessible to all users)
     */
    void deleteProductReviewLike(Long userId, Long reviewId);
}