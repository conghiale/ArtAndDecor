package org.artanddecor.repository;

import org.artanddecor.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Repository for database operations
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product by slug for customer view
     */
    Optional<Product> findByProductSlug(String productSlug);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsByProductSlug(String productSlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByProductName(String productName);

    /**
     * Check if code exists (for validation)
     */
    boolean existsByProductCode(String productCode);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search products by multiple criteria with pagination including slug filtering
     */
    @Query("SELECT p FROM Product p " +
           "WHERE (:textSearch IS NULL OR (" +
           "     LOWER(p.productName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(p.productSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(p.productCode) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "AND (:enabled IS NULL OR p.productEnabled = :enabled) " +
           "AND (:categoryId IS NULL OR p.productCategory.productCategoryId = :categoryId) " +
           "AND (:typeId IS NULL OR p.productCategory.productType.productTypeId = :typeId) " +
           "AND (:stateId IS NULL OR p.productState.productStateId = :stateId) " +
           "AND (:minPrice IS NULL OR p.productPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.productPrice <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND p.stockQuantity = 0)) " +
           "AND (:productCode IS NULL OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :productCode, '%'))) " +
           "AND (:featured IS NULL OR p.productFeatured = :featured) " +
           "AND (:highlighted IS NULL OR p.productHighlighted = :highlighted) " +
           "AND (:productCategorySlug IS NULL OR p.productCategory.productCategorySlug = :productCategorySlug) " +
           "AND (:productTypeSlug IS NULL OR p.productCategory.productType.productTypeSlug = :productTypeSlug) " +
           "ORDER BY p.createdDt DESC")
    Page<Product> findProductsByCriteriaPaginated(
        @Param("textSearch") String textSearch,
        @Param("enabled") Boolean enabled,
        @Param("categoryId") Long categoryId,
        @Param("typeId") Long typeId,
        @Param("stateId") Long stateId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("inStock") Boolean inStock,
        @Param("productCode") String productCode,
        @Param("featured") Boolean featured,
        @Param("highlighted") Boolean highlighted,
        @Param("productCategorySlug") String productCategorySlug,
        @Param("productTypeSlug") String productTypeSlug,
        Pageable pageable
    );

    /**
     * Find products in stock
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.stockQuantity > 0 " +
           "AND p.productEnabled = true " +
           "ORDER BY p.productName ASC")
    List<Product> findProductsInStock();

    /**
     * Find top selling products with enhanced sorting logic
     * Prioritizes products that have actual sales (soldQuantity > 0)
     * Secondary sort by creation date for consistent ordering
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productEnabled = true " +
           "ORDER BY p.soldQuantity DESC, p.createdDt DESC")
    Page<Product> findTopSellingProducts(Pageable pageable);

    /**
     * Find products by image IDs with optional selling status filter
     * @param imageIds List of image IDs to search for
     * @param isSelling Filter for selling products (enabled, active state, in stock)
     * @param pageable Pagination information
     * @return Page of products matching criteria
     */
    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN p.productImages pi " +
           "WHERE pi.image.imageId IN :imageIds " +
           "AND (:isSelling IS NULL OR (" +
           "    :isSelling = true AND p.productEnabled = true AND p.productState.productStateId = 1 AND p.stockQuantity > 0" +
           ") OR (" +
           "    :isSelling = false AND (p.productEnabled = false OR p.productState.productStateId != 1 OR p.stockQuantity = 0)" +
           ")) " +
           "ORDER BY p.createdDt DESC")
    Page<Product> findProductsByImageIdsWithSellingFilter(
        @Param("imageIds") List<Long> imageIds,
        @Param("isSelling") Boolean isSelling,
        Pageable pageable
    );
}