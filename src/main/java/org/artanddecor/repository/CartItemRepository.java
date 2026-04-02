package org.artanddecor.repository;

import org.artanddecor.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity
 * Minimal interface for cart item operations
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find cart items by cart ID
     * @param cartId Cart ID
     * @return List of cart items
     */
    List<CartItem> findByCart_CartId(Long cartId);

    /**
     * Find all cart items by user ID (for entire cart checkout)
     * @param userId User ID
     * @return List of cart items for the user's cart
     */
    List<CartItem> findByCart_User_UserId(Long userId);

    /**
     * Find all cart items by session ID (for guest user entire cart checkout)
     * @param sessionId Session ID
     * @return List of cart items for the session's cart
     */
    List<CartItem> findByCart_SessionId(String sessionId);

    /**
     * Find cart item by cart ID and product ID
     * @param cartId Cart ID
     * @param productId Product ID
     * @return Optional CartItem
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.product.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    /**
     * Find cart items by cart ID and cart item state ID
     * @param cartId Cart ID
     * @param cartItemStateId Cart item state ID
     * @return List of cart items
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.cartItemState.cartItemStateId = :cartItemStateId")
    List<CartItem> findByCartIdAndCartItemStateId(@Param("cartId") Long cartId, @Param("cartItemStateId") Long cartItemStateId);

    /**
     * Count cart items by cart ID and state ID
     * @param cartId Cart ID
     * @param cartItemStateId Cart item state ID
     * @return Cart items count
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.cartItemState.cartItemStateId = :cartItemStateId")
    Long countCartItems(@Param("cartId") Long cartId, @Param("cartItemStateId") Long cartItemStateId);

    /**
     * Delete cart items by cart ID
     * @param cartId Cart ID
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    /**
     * Get total quantity of active cart items
     * @param cartId Cart ID
     * @return Total quantity
     */
    @Query("SELECT COALESCE(SUM(ci.cartItemQuantity), 0) FROM CartItem ci " +
           "WHERE ci.cart.cartId = :cartId AND ci.cartItemState.cartItemStateName = 'ACTIVE'")
    Integer getActiveCartItemsTotalQuantity(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.createdDt >= :startDate AND ci.createdDt <= :endDate")
    Page<CartItem> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    /**
     * Search cart items by product name or category
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of cart items
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.product.productName LIKE %:keyword% " +
           "OR ci.product.productCategory.productCategoryName LIKE %:keyword%")
    Page<CartItem> searchCartItems(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Get cart total value by cart ID
     * @param cartId Cart ID
     * @return Total cart value
     */
    @Query("SELECT COALESCE(SUM(ci.cartItemTotalPrice), 0) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    BigDecimal getCartTotalValue(@Param("cartId") Long cartId);

    /**
     * Get cart total quantity by cart ID
     * @param cartId Cart ID
     * @return Total cart quantity
     */
    @Query("SELECT COALESCE(SUM(ci.cartItemQuantity), 0) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    Integer getCartTotalQuantity(@Param("cartId") Long cartId);

    /**
     * Count cart items by cart ID
     * @param cartId Cart ID
     * @return Cart item count
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    Long countCartItemsByCart(@Param("cartId") Long cartId);

    /**
     * Get cart item statistics by product category
     * @return Object array with category and statistics
     */
    @Query("SELECT ci.product.productCategory.productCategoryName, COUNT(ci), SUM(ci.cartItemQuantity), SUM(ci.cartItemTotalPrice) " +
           "FROM CartItem ci " +
           "GROUP BY ci.product.productCategory.productCategoryId")
    List<Object[]> getCartItemStatisticsByCategory();

    /**
     * Find cart items with product and category details
     * @param pageable Pagination parameters
     * @return Page of cart items with full details
     */
    @Query("SELECT ci FROM CartItem ci " +
           "LEFT JOIN FETCH ci.cart c " +
           "LEFT JOIN FETCH ci.product p " +
           "LEFT JOIN FETCH p.productCategory " +
           "LEFT JOIN FETCH ci.cartItemState")
    Page<CartItem> findCartItemsWithDetails(Pageable pageable);

    /**
     * Find cart items by various criteria with flexible filtering
     * @param cartItemId Filter by cart item ID (optional)
     * @param cartId Filter by cart ID (optional)
     * @param productId Filter by product ID (optional)
     * @param cartItemStateId Filter by cart item state ID (optional)
     * @param pageable Pagination parameters
     * @return Page of cart items matching criteria
     */
    /**
     * Find cart items by multiple criteria with pagination
     * @param cartItemId Filter by cart item ID
     * @param cartId Filter by cart ID 
     * @param productId Filter by product ID
     * @param userId Filter by user ID
     * @param minPrice Filter by minimum price
     * @param maxPrice Filter by maximum price
     * @param minQuantity Filter by minimum quantity
     * @param maxQuantity Filter by maximum quantity
     * @param cartItemStateId Filter by cart item state ID
     * @param pageable Pagination information
     * @return Page of cart items matching criteria
     */
    @Query("SELECT ci FROM CartItem ci " +
           "LEFT JOIN ci.cart c " +
           "LEFT JOIN ci.product p " +
           "LEFT JOIN ci.cartItemState cis " +
           "WHERE (:cartItemId IS NULL OR ci.cartItemId = :cartItemId) " +
           "AND (:cartId IS NULL OR c.cartId = :cartId) " +
           "AND (:productId IS NULL OR p.productId = :productId) " +
           "AND (:userId IS NULL OR c.user.userId = :userId) " +
           "AND (:minPrice IS NULL OR ci.cartItemTotalPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR ci.cartItemTotalPrice <= :maxPrice) " +
           "AND (:minQuantity IS NULL OR ci.cartItemQuantity >= :minQuantity) " +
           "AND (:maxQuantity IS NULL OR ci.cartItemQuantity <= :maxQuantity) " +
           "AND (:cartItemStateId IS NULL OR cis.cartItemStateId = :cartItemStateId)")
    Page<CartItem> findCartItemsByCriteria(
        @Param("cartItemId") Long cartItemId,
        @Param("cartId") Long cartId,
        @Param("productId") Long productId,
        @Param("userId") Long userId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minQuantity") Integer minQuantity,
        @Param("maxQuantity") Integer maxQuantity,
        @Param("cartItemStateId") Long cartItemStateId,
        Pageable pageable);

    /**
     * Find active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active cart items
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.cartId = :cartId " +
           "AND ci.cartItemState.cartItemStateName = 'ACTIVE'")
    List<CartItem> findActiveCartItemsByCartId(@Param("cartId") Long cartId);

    /**
     * Find cart items by cart ID and product ID with attributes loaded
     * Used to find existing items with same product to check for attribute matches
     * @param cartId Cart ID
     * @param productId Product ID
     * @return List of cart items with attributes
     */
    @Query("SELECT DISTINCT ci FROM CartItem ci " +
           "LEFT JOIN FETCH ci.cartItemAttributes cia " +
           "LEFT JOIN FETCH cia.productAttribute pa " +
           "WHERE ci.cart.cartId = :cartId AND ci.product.productId = :productId")
    List<CartItem> findByCartIdAndProductIdWithAttributes(@Param("cartId") Long cartId, @Param("productId") Long productId);
}