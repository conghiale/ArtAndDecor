package org.artanddecor.repository;

import org.artanddecor.model.CartItemState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItemState entity
 * Handles CRUD operations for cart item states
 */
@Repository
public interface CartItemStateRepository extends JpaRepository<CartItemState, Long> {

    /**
     * Find cart item state by name
     * @param cartItemStateName Cart item state name
     * @return Optional CartItemState
     */
    Optional<CartItemState> findByCartItemStateName(String cartItemStateName);

    /**
     * Check if cart item state exists by name
     * @param cartItemStateName Cart item state name
     * @return true if exists
     */
    boolean existsByCartItemStateName(String cartItemStateName);

    /**
     * Find all enabled cart item states
     * @return List of enabled cart item states
     */
    List<CartItemState> findByCartItemStateEnabledTrue();

    /**
     * Find cart item states by enabled status with pagination
     * @param enabled Enabled status
     * @param pageable Pagination parameters
     * @return Page of cart item states
     */
    Page<CartItemState> findByCartItemStateEnabled(Boolean enabled, Pageable pageable);

    /**
     * Find cart item states by name containing keyword (case-insensitive)
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of cart item states
     */
    Page<CartItemState> findByCartItemStateNameContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Find cart item states with cart item count
     * @param pageable Pagination parameters
     * @return Page of cart item states with cart item count
     */
    @Query("SELECT cis, COUNT(ci.cartItemId) as cartItemCount " +
           "FROM CartItemState cis LEFT JOIN CartItem ci ON cis.cartItemStateId = ci.cartItemState.cartItemStateId " +
           "GROUP BY cis.cartItemStateId " +
           "ORDER BY cis.cartItemStateName")
    Page<Object[]> findCartItemStatesWithCartItemCount(Pageable pageable);

    /**
     * Find active cart item state (ACTIVE)
     * @return Optional CartItemState
     */
    @Query("SELECT cis FROM CartItemState cis WHERE cis.cartItemStateName = 'ACTIVE' AND cis.cartItemStateEnabled = true")
    Optional<CartItemState> findActiveCartItemState();

    /**
     * Find ordered cart item state (ORDERED)
     * @return Optional CartItemState
     */
    @Query("SELECT cis FROM CartItemState cis WHERE cis.cartItemStateName = 'ORDERED' AND cis.cartItemStateEnabled = true")
    Optional<CartItemState> findOrderedCartItemState();

    /**
     * Get cart item count by state
     * @param cartItemStateId Cart item state ID
     * @return Cart item count
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cartItemState.cartItemStateId = :cartItemStateId")
    Long getCartItemCountByState(@Param("cartItemStateId") Long cartItemStateId);

    /**
     * Get cart item statistics by state
     * @return Object array with cart item state and count
     */
    @Query("SELECT ci.cartItemState.cartItemStateName, COUNT(ci) FROM CartItem ci GROUP BY ci.cartItemState.cartItemStateName")
    List<Object[]> getCartItemStatistics();

    /**
     * Find cart item states by various criteria with flexible filtering
     * @param cartItemStateId Filter by cart item state ID (optional)
     * @param cartItemStateName Filter by cart item state name (optional)
     * @param cartItemStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of cart item states matching criteria (no pagination)
     */
    @Query("SELECT cis FROM CartItemState cis " +
           "WHERE (:cartItemStateId IS NULL OR cis.cartItemStateId = :cartItemStateId) " +
           "AND (:cartItemStateName IS NULL OR cis.cartItemStateName = :cartItemStateName) " +
           "AND (:cartItemStateEnabled IS NULL OR cis.cartItemStateEnabled = :cartItemStateEnabled) " +
           "AND (:textSearch IS NULL OR " +
           "     cis.cartItemStateName LIKE %:textSearch% OR " +
           "     cis.cartItemStateDisplayName LIKE %:textSearch% OR " +
           "     cis.cartItemStateRemark LIKE %:textSearch%) " +
           "ORDER BY cis.cartItemStateName")
    List<CartItemState> findCartItemStatesByCriteria(
        @Param("cartItemStateId") Long cartItemStateId,
        @Param("cartItemStateName") String cartItemStateName,
        @Param("cartItemStateEnabled") Boolean cartItemStateEnabled,
        @Param("textSearch") String textSearch);
}