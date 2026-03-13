package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductType Repository for database operations
 */
@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product type by slug for customer view
     */
    Optional<ProductType> findByProductTypeSlug(String productTypeSlug);

    /**
     * Find product type by name
     */
    Optional<ProductType> findByProductTypeName(String productTypeName);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsByProductTypeSlug(String productTypeSlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByProductTypeName(String productTypeName);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product types by multiple criteria with pagination
     */
    @Query("SELECT pt FROM ProductType pt " +
           "WHERE (:textSearch IS NULL OR (" +
           "     LOWER(pt.productTypeName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pt.productTypeSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pt.productTypeDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pt.productTypeRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "AND (:enabled IS NULL OR pt.productTypeEnabled = :enabled) " +
           "ORDER BY pt.createdDt DESC")
    Page<ProductType> findProductTypesByCriteriaPaginated(
        @Param("textSearch") String textSearch,
        @Param("enabled") Boolean enabled,
        Pageable pageable
    );
}