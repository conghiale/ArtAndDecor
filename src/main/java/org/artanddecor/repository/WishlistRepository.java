package org.artanddecor.repository;

import org.artanddecor.model.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Wishlist Repository for database operations
 * Supports both authenticated and anonymous users
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Find wishlist items by multiple criteria with pagination
     * @param userId Filter by user ID (for authenticated users)
     * @param sessionId Filter by session ID (for anonymous users)  
     * @param productId Filter by specific product ID
     * @param pageable Pagination information
     * @return Page of matching wishlist items with full details
     */
    @Query("""
        SELECT w FROM Wishlist w 
        LEFT JOIN FETCH w.user u 
        LEFT JOIN FETCH w.product p 
        LEFT JOIN FETCH p.productCategory pc
        LEFT JOIN FETCH p.productState ps
        WHERE (:userId IS NULL OR w.user.userId = :userId) 
        AND (:sessionId IS NULL OR w.sessionId = :sessionId)
        AND (:productId IS NULL OR w.product.productId = :productId)
        ORDER BY w.createdDt DESC
        """)
    Page<Wishlist> findWishlistByCriteria(
        @Param("userId") Long userId,
        @Param("sessionId") String sessionId, 
        @Param("productId") Long productId,
        Pageable pageable
    );

    /**
     * Lightweight wishlist query for basic listing (no deep joins)
     * Use this for simple listing where only basic product info is needed
     */
    @Query("""
        SELECT w FROM Wishlist w 
        LEFT JOIN FETCH w.user u 
        LEFT JOIN FETCH w.product p
        WHERE (:userId IS NULL OR w.user.userId = :userId) 
        AND (:sessionId IS NULL OR w.sessionId = :sessionId)
        AND (:productId IS NULL OR w.product.productId = :productId)
        ORDER BY w.createdDt DESC
        """)
    Page<Wishlist> findWishlistByCriteriaLightweight(
        @Param("userId") Long userId,
        @Param("sessionId") String sessionId, 
        @Param("productId") Long productId,
        Pageable pageable
    );

    /**
     * Check if wishlist item exists for authenticated user
     * @param userId User ID
     * @param productId Product ID
     * @return true if exists
     */
    boolean existsByUserUserIdAndProductProductId(Long userId, Long productId);

    /**
     * Check if wishlist item exists for anonymous user
     * @param sessionId Session ID
     * @param productId Product ID 
     * @return true if exists
     */
    boolean existsBySessionIdAndProductProductId(String sessionId, Long productId);

    /**
     * Find specific wishlist item by user and product (for authenticated users)
     * @param userId User ID
     * @param productId Product ID
     * @return Optional wishlist item
     */
    Optional<Wishlist> findByUserUserIdAndProductProductId(Long userId, Long productId);

    /**
     * Find specific wishlist item by session and product (for anonymous users)
     * @param sessionId Session ID
     * @param productId Product ID
     * @return Optional wishlist item
     */
    Optional<Wishlist> findBySessionIdAndProductProductId(String sessionId, Long productId);
}