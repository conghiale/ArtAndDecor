package org.artanddecor.repository;

import org.artanddecor.model.Cart;
import org.artanddecor.model.CartState;
import org.artanddecor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cart entity
 * Handles CRUD operations for shopping carts
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find cart by slug
     * @param cartSlug Cart slug
     * @return Optional Cart
     */
    Optional<Cart> findByCartSlug(String cartSlug);

    /**
     * Check if cart exists by slug
     * @param cartSlug Cart slug
     * @return true if exists
     */
    boolean existsByCartSlug(String cartSlug);

    /**
     * Find carts by user
     * @param user User entity
     * @return List of carts
     */
    List<Cart> findByUser(User user);

    /**
     * Find carts by user ID
     * @param userId User ID
     * @return List of carts
     */
    List<Cart> findByUser_UserId(Long userId);

    /**
     * Find carts by session ID
     * @param sessionId Session ID
     * @return List of carts
     */
    List<Cart> findBySessionId(String sessionId);

    /**
     * Find cart by user and cart state
     * @param user User entity
     * @param cartState Cart state entity
     * @return Optional Cart
     */
    Optional<Cart> findByUserAndCartState(User user, CartState cartState);

    /**
     * Find cart by user ID and cart state name
     * @param userId User ID
     * @param cartStateName Cart state name
     * @return Optional Cart
     */
    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.cartState.cartStateName = :cartStateName")
    Optional<Cart> findByUserIdAndCartStateName(@Param("userId") Long userId, @Param("cartStateName") String cartStateName);

    /**
     * Find active cart by user
     * @param userId User ID
     * @return Optional Cart
     */
    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId AND c.cartState.cartStateName = 'ACTIVE' AND c.cartEnabled = true")
    Optional<Cart> findActiveCartByUser(@Param("userId") Long userId);

    /**
     * Find active cart by session
     * @param sessionId Session ID
     * @return Optional Cart
     */
    @Query("SELECT c FROM Cart c WHERE c.sessionId = :sessionId AND c.cartState.cartStateName = 'ACTIVE' AND c.cartEnabled = true")
    Optional<Cart> findActiveCartBySession(@Param("sessionId") String sessionId);

    /**
     * Find carts by cart state with pagination
     * @param cartState Cart state entity
     * @param pageable Pagination parameters
     * @return Page of carts
     */
    Page<Cart> findByCartState(CartState cartState, Pageable pageable);

    /**
     * Find enabled carts with pagination
     * @param enabled Enabled status
     * @param pageable Pagination parameters
     * @return Page of carts
     */
    Page<Cart> findByCartEnabled(Boolean enabled, Pageable pageable);

    /**
     * Find carts by date range
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of carts
     */
    @Query("SELECT c FROM Cart c WHERE c.createdDt >= :startDate AND c.createdDt <= :endDate")
    Page<Cart> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate, 
                              Pageable pageable);

    /**
     * Find abandoned carts (older than specified days)
     * @param cutoffDate Cutoff date for abandoned carts
     * @param pageable Pagination parameters
     * @return Page of abandoned carts
     */
    @Query("SELECT c FROM Cart c WHERE c.cartState.cartStateName = 'ACTIVE' AND c.modifiedDt < :cutoffDate")
    Page<Cart> findAbandonedCarts(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);

    /**
     * Search carts by user name or cart slug
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of carts
     */
    @Query("SELECT c FROM Cart c LEFT JOIN c.user u " +
           "WHERE c.cartSlug LIKE %:keyword% " +
           "OR CONCAT(u.firstName, ' ', u.lastName) LIKE %:keyword% " +
           "OR u.email LIKE %:keyword%")
    Page<Cart> searchCarts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Get cart statistics by state
     * @return Object array with cart state and count
     */
    @Query("SELECT c.cartState.cartStateName, COUNT(c) FROM Cart c GROUP BY c.cartState.cartStateName")
    List<Object[]> getCartStatistics();

    /**
     * Count carts by user
     * @param userId User ID
     * @return Cart count
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user.userId = :userId")
    Long countCartsByUser(@Param("userId") Long userId);

    /**
     * Find carts with items count
     * @param pageable Pagination parameters
     * @return Page of carts with total items
     */
    @Query("SELECT c, COALESCE(SUM(ci.cartItemQuantity), 0) as totalItems " +
           "FROM Cart c LEFT JOIN CartItem ci ON c.cartId = ci.cart.cartId " +
           "GROUP BY c.cartId")
    Page<Object[]> findCartsWithItemCount(Pageable pageable);

    /**
     * Find carts by various criteria with flexible filtering
     * @param cartId Filter by cart ID (optional)
     * @param userId Filter by user ID (optional)  
     * @param sessionId Filter by session ID (optional)
     * @param cartStateId Filter by cart state ID (optional)
     * @param cartSlug Filter by cart slug (optional)
     * @param cartEnabled Filter by enabled status (optional)
     * @param pageable Pagination parameters
     * @return Page of carts matching criteria
     */
    @Query("SELECT c FROM Cart c LEFT JOIN c.user u LEFT JOIN c.cartState cs " +
           "WHERE (:cartId IS NULL OR c.cartId = :cartId) " +
           "AND (:userId IS NULL OR u.userId = :userId) " +
           "AND (:sessionId IS NULL OR c.sessionId = :sessionId) " +
           "AND (:cartStateId IS NULL OR cs.cartStateId = :cartStateId) " +
           "AND (:cartSlug IS NULL OR c.cartSlug = :cartSlug) " +
           "AND (:cartEnabled IS NULL OR c.cartEnabled = :cartEnabled)")
    Page<Cart> findCartsByCriteria(
        @Param("cartId") Long cartId,
        @Param("userId") Long userId,
        @Param("sessionId") String sessionId,
        @Param("cartStateId") Long cartStateId,
        @Param("cartSlug") String cartSlug,
        @Param("cartEnabled") Boolean cartEnabled,
        Pageable pageable);
}