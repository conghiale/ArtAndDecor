package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ProductState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductState Repository for database operations
 */
@Repository
public interface ProductStateRepository extends JpaRepository<ProductState, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product state by name
     */
    Optional<ProductState> findByProductStateName(String productStateName);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByProductStateName(String productStateName);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product states by multiple criteria with pagination
     */
    @Query("SELECT ps FROM ProductState ps " +
           "WHERE (:textSearch IS NULL OR (" +
           "     LOWER(ps.productStateName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(ps.productStateDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(ps.productStateRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "AND (:enabled IS NULL OR ps.productStateEnabled = :enabled) " +
           "ORDER BY ps.createdDt DESC")
    Page<ProductState> findProductStatesByCriteriaPaginated(
        @Param("textSearch") String textSearch,
        @Param("enabled") Boolean enabled,
        Pageable pageable
    );

    /**
     * Find all product state names
     */
    @Query("SELECT DISTINCT ps.productStateName FROM ProductState ps WHERE ps.productStateEnabled = true ORDER BY ps.productStateName ASC")
    List<String> findAllProductStateNames();
}