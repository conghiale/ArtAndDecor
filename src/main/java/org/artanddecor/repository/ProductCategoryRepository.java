package org.artanddecor.repository;

import org.artanddecor.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductCategory Repository for database operations
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product category by slug for customer view
     */
    Optional<ProductCategory> findByProductCategorySlug(String productCategorySlug);

    /**
     * Find product category by name
     */
    Optional<ProductCategory> findByProductCategoryName(String productCategoryName);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsByProductCategorySlug(String productCategorySlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByProductCategoryName(String productCategoryName);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product categories by multiple criteria with pagination (Enhanced with parent filters)
     */
    @Query("SELECT pc FROM ProductCategory pc " +
           "WHERE (:textSearch IS NULL OR (" +
           "     LOWER(pc.productCategoryName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pc.productCategorySlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pc.productCategoryDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(pc.productCategoryRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "AND (:enabled IS NULL OR pc.productCategoryEnabled = :enabled) " +
           "AND (:visible IS NULL OR pc.productCategoryVisible = :visible) " +
           "AND (:productTypeId IS NULL OR pc.productType.productTypeId = :productTypeId) " +
           "AND (:parentCategoryId IS NULL OR (:parentCategoryId = -1 AND pc.parentCategory IS NULL) OR pc.parentCategory.productCategoryId = :parentCategoryId) " +
           "ORDER BY pc.createdDt DESC")
    Page<ProductCategory> findProductCategoriesByCriteriaPaginated(
        @Param("textSearch") String textSearch,
        @Param("enabled") Boolean enabled,
        @Param("visible") Boolean visible,
        @Param("productTypeId") Long productTypeId,
        @Param("parentCategoryId") Long parentCategoryId, // Use -1 for root categories, specific ID for children
        Pageable pageable
    );

    /**
     * Find categories by product type
     */
    @Query("SELECT pc FROM ProductCategory pc " +
           "WHERE pc.productType.productTypeId = :productTypeId " +
           "AND pc.productCategoryEnabled = true " +
           "ORDER BY pc.productCategoryName ASC")
    List<ProductCategory> findByProductTypeId(@Param("productTypeId") Long productTypeId);



    /**
     * Find child categories by parent ID
     */
    @Query("SELECT pc FROM ProductCategory pc " +
           "WHERE pc.parentCategory.productCategoryId = :parentId " +
           "AND pc.productCategoryEnabled = true " +
           "ORDER BY pc.productCategoryName ASC")
    List<ProductCategory> findByParentCategoryId(@Param("parentId") Long parentId);
}