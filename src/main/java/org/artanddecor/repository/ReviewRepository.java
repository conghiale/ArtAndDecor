package org.artanddecor.repository;

import org.artanddecor.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Review Repository
 * Handles database operations for Review entity
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find reviews by product ID
     */
    Page<Review> findByProduct_ProductId(Long productId, Pageable pageable);

    /**
     * Find reviews by user ID  
     */
    Page<Review> findByUser_UserId(Long userId, Pageable pageable);

    /**
     * Find reviews by parent review ID
     */
    Page<Review> findByParentReview_ReviewId(Long parentReviewId, Pageable pageable);

    /**
     * Find reviews by root review ID
     */
    Page<Review> findByRootReview_ReviewId(Long rootReviewId, Pageable pageable);

    /**
     * Find reviews by rating
     */
    Page<Review> findByRating(Byte rating, Pageable pageable);

    /**
     * Find reviews with count like greater than or equal to specified value
     */
    Page<Review> findByCountLikeGreaterThanEqual(Integer countLike, Pageable pageable);

    /**
     * Find reviews by visibility status
     */
    Page<Review> findByIsVisible(Boolean isVisible, Pageable pageable);

    /**
     * Find reviews by deleted status
     */
    Page<Review> findByIsDeleted(Boolean isDeleted, Pageable pageable);

    /**
     * Search reviews by content containing text (case insensitive)
     */
    Page<Review> findByReviewContentContainingIgnoreCase(String searchText, Pageable pageable);

    /**
     * Complex query with multiple optional filters
     */
    @Query("SELECT r FROM Review r WHERE " +
           "(:userId IS NULL OR r.user.userId = :userId) AND " +
           "(:productId IS NULL OR r.product.productId = :productId) AND " +
           "(:parentReviewId IS NULL OR r.parentReview.reviewId = :parentReviewId) AND " +
           "(:rootReviewId IS NULL OR r.rootReview.reviewId = :rootReviewId) AND " +
           "(:rating IS NULL OR r.rating = :rating) AND " +
           "(:minCountLike IS NULL OR r.countLike >= :minCountLike) AND " +
           "(:isVisible IS NULL OR r.isVisible = :isVisible) AND " +
           "(:isDeleted IS NULL OR r.isDeleted = :isDeleted) AND " +
           "(:searchText IS NULL OR LOWER(r.reviewContent) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Review> findReviewsWithFilters(
        @Param("userId") Long userId,
        @Param("productId") Long productId,
        @Param("parentReviewId") Long parentReviewId,
        @Param("rootReviewId") Long rootReviewId,
        @Param("rating") Byte rating,
        @Param("minCountLike") Integer minCountLike,
        @Param("isVisible") Boolean isVisible,
        @Param("isDeleted") Boolean isDeleted,
        @Param("searchText") String searchText,
        Pageable pageable
    );

    /**
     * Find top-level reviews for a product (no parent review)
     */
    Page<Review> findByProduct_ProductIdAndParentReviewIsNull(Long productId, Pageable pageable);

    /**
     * Find reply reviews for a parent review
     */
    Page<Review> findByParentReview_ReviewIdOrderByCreatedDtAsc(Long parentReviewId, Pageable pageable);

    /**
     * Count reviews by product ID
     */
    Long countByProduct_ProductId(Long productId);

    /**
     * Calculate average rating for a product
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId AND r.isDeleted = false AND r.isVisible = true")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * Find recent reviews for a product
     */
    List<Review> findTop10ByProduct_ProductIdAndIsDeletedFalseAndIsVisibleTrueOrderByCreatedDtDesc(Long productId);
}