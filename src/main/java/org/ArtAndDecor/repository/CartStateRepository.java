package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.CartState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartState entity
 * Handles CRUD operations for cart states
 */
@Repository
public interface CartStateRepository extends JpaRepository<CartState, Long> {

    /**
     * Find cart state by name
     * @param cartStateName Cart state name
     * @return Optional CartState
     */
    Optional<CartState> findByCartStateName(String cartStateName);

    /**
     * Check if cart state exists by name
     * @param cartStateName Cart state name
     * @return true if exists
     */
    boolean existsByCartStateName(String cartStateName);

    /**
     * Find all enabled cart states
     * @return List of enabled cart states
     */
    List<CartState> findByCartStateEnabledTrue();

    /**
     * Find cart states by enabled status with pagination
     * @param enabled Enabled status
     * @param pageable Pagination parameters
     * @return Page of cart states
     */
    Page<CartState> findByCartStateEnabled(Boolean enabled, Pageable pageable);

    /**
     * Find cart states by name containing keyword (case-insensitive)
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of cart states
     */
    Page<CartState> findByCartStateNameContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Find cart states with cart count
     * @param pageable Pagination parameters
     * @return Page of cart states with cart count
     */
    @Query("SELECT cs, COUNT(c.cartId) as cartCount " +
           "FROM CartState cs LEFT JOIN Cart c ON cs.cartStateId = c.cartState.cartStateId " +
           "GROUP BY cs.cartStateId " +
           "ORDER BY cs.cartStateName")
    Page<Object[]> findCartStatesWithCartCount(Pageable pageable);

    /**
     * Find active cart state (ACTIVE)
     * @return Optional CartState
     */
    @Query("SELECT cs FROM CartState cs WHERE cs.cartStateName = 'ACTIVE' AND cs.cartStateEnabled = true")
    Optional<CartState> findActiveCartState();

    /**
     * Get cart count by state
     * @param cartStateId Cart state ID
     * @return Cart count
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.cartState.cartStateId = :cartStateId")
    Long getCartCountByState(@Param("cartStateId") Long cartStateId);

    /**
     * Find cart states by various criteria with flexible filtering
     * @param cartStateId Filter by cart state ID (optional)
     * @param cartStateName Filter by cart state name (optional)
     * @param cartStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of cart states matching criteria (no pagination)
     */
    @Query("SELECT cs FROM CartState cs " +
           "WHERE (:cartStateId IS NULL OR cs.cartStateId = :cartStateId) " +
           "AND (:cartStateName IS NULL OR cs.cartStateName = :cartStateName) " +
           "AND (:cartStateEnabled IS NULL OR cs.cartStateEnabled = :cartStateEnabled) " +
           "AND (:textSearch IS NULL OR " +
           "     cs.cartStateName LIKE %:textSearch% OR " +
           "     cs.cartStateDisplayName LIKE %:textSearch% OR " +
           "     cs.cartStateRemark LIKE %:textSearch%) " +
           "ORDER BY cs.cartStateName")
    List<CartState> findCartStatesByCriteria(
        @Param("cartStateId") Long cartStateId,
        @Param("cartStateName") String cartStateName,
        @Param("cartStateEnabled") Boolean cartStateEnabled,
        @Param("textSearch") String textSearch);
}