package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ProductAttr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductAttr Repository for database operations
 */
@Repository
public interface ProductAttrRepository extends JpaRepository<ProductAttr, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product attribute by name
     */
    Optional<ProductAttr> findByProductAttrName(String productAttrName);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByProductAttrName(String productAttrName);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product attributes by multiple criteria with pagination
     */
    @Query("SELECT pa FROM ProductAttr pa " +
           "WHERE (:textSearch IS NULL OR (" +
           "     LOWER(pa.productAttrName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pa.productAttrDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pa.productAttrRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "AND (:enabled IS NULL OR pa.productAttrEnabled = :enabled) " +
           "ORDER BY pa.createdDt DESC")
    Page<ProductAttr> findProductAttrsByCriteriaPaginated(
        @Param("textSearch") String textSearch,
        @Param("enabled") Boolean enabled,
        Pageable pageable
    );

    /**
     * Find all product attribute names
     */
    @Query("SELECT DISTINCT pa.productAttrName FROM ProductAttr pa WHERE pa.productAttrEnabled = true ORDER BY pa.productAttrName ASC")
    List<String> findAllProductAttrNames();
}