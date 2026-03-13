package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ProductReviewLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ProductReviewLike Repository  
 * Handles database operations for ProductReviewLike entity
 */
@Repository
public interface ProductReviewLikeRepository extends JpaRepository<ProductReviewLike, Long> {

    /**
     * Find likes by user ID
     */
    Page<ProductReviewLike> findByUser_UserId(Long userId, Pageable pageable);

    /**
     * Find likes by review ID  
     */
    Page<ProductReviewLike> findByReview_ReviewId(Long reviewId, Pageable pageable);

    /**
     * Find specific like by user and review (due to unique constraint)
     */
    Optional<ProductReviewLike> findByUser_UserIdAndReview_ReviewId(Long userId, Long reviewId);

    /**
     * Check if user has liked a specific review
     */
    boolean existsByUser_UserIdAndReview_ReviewId(Long userId, Long reviewId);

    /**
     * Count total likes for a review
     */
    Long countByReview_ReviewId(Long reviewId);

    /**
     * Count total likes by a user
     */
    Long countByUser_UserId(Long userId);

    /**
     * Complex query with multiple optional filters
     */
    @Query("SELECT prl FROM ProductReviewLike prl WHERE " +
           "(:userId IS NULL OR prl.user.userId = :userId) AND " +
           "(:reviewId IS NULL OR prl.review.reviewId = :reviewId)")
    Page<ProductReviewLike> findLikesWithFilters(
        @Param("userId") Long userId,
        @Param("reviewId") Long reviewId,
        Pageable pageable
    );

    /**
     * Delete like by user and review
     */
    void deleteByUser_UserIdAndReview_ReviewId(Long userId, Long reviewId);

    /**
     * Find likes for reviews of a specific product
     */
    @Query("SELECT prl FROM ProductReviewLike prl WHERE prl.review.product.productId = :productId")
    Page<ProductReviewLike> findByProductId(@Param("productId") Long productId, Pageable pageable);

    /**
     * Find likes by a user for a specific product's reviews
     */
    @Query("SELECT prl FROM ProductReviewLike prl WHERE prl.user.userId = :userId AND prl.review.product.productId = :productId")  
    Page<ProductReviewLike> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId, Pageable pageable);
}