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
     * Find product by name
     */
    Optional<Product> findByProductName(String productName);

    /**
     * Find product by code
     */
    Optional<Product> findByProductCode(String productCode);

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
     * Search products by multiple criteria with pagination
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
        Pageable pageable
    );

    /**
     * Find all enabled products
     */
    @Query("SELECT p FROM Product p WHERE p.productEnabled = true ORDER BY p.productName ASC")
    List<Product> findAllEnabledProducts();

    /**
     * Find products by category
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productCategory.productCategoryId = :categoryId " +
           "AND p.productEnabled = true " +
           "ORDER BY p.productName ASC")
    List<Product> findByProductCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Find enabled products by category slug
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productCategory.productCategorySlug = :categorySlug " +
           "AND p.productEnabled = true " +
           "ORDER BY p.productName ASC")
    List<Product> findEnabledProductsByCategorySlug(@Param("categorySlug") String categorySlug);

    /**
     * Find enabled products by category slug with pagination
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productCategory.productCategorySlug = :categorySlug " +
           "AND p.productEnabled = true " +
           "ORDER BY p.createdDt DESC, p.productName ASC")
    Page<Product> findEnabledProductsByCategorySlugPaginated(@Param("categorySlug") String categorySlug, Pageable pageable);

    /**
     * Find enabled products by type slug
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productCategory.productType.productTypeSlug = :typeSlug " +
           "AND p.productEnabled = true " +
           "ORDER BY p.productName ASC")
    List<Product> findEnabledProductsByTypeSlug(@Param("typeSlug") String typeSlug);

    /**
     * Find enabled products by type slug with pagination
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productCategory.productType.productTypeSlug = :typeSlug " +
           "AND p.productEnabled = true " +
           "ORDER BY p.createdDt DESC, p.productName ASC")
    Page<Product> findEnabledProductsByTypeSlugPaginated(@Param("typeSlug") String typeSlug, Pageable pageable);

    /**
     * Find products by state
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productState.productStateId = :stateId " +
           "AND p.productEnabled = true " +
           "ORDER BY p.productName ASC")
    List<Product> findByProductStateId(@Param("stateId") Long stateId);

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
     * Find featured products
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productEnabled = true " +
           "AND p.productFeatured = true " +
           "ORDER BY p.createdDt DESC")
    List<Product> findFeaturedProducts();

    /**
     * Find highlighted products
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productEnabled = true " +
           "AND p.productHighlighted = true " +
           "ORDER BY p.createdDt DESC")
    List<Product> findHighlightedProducts();

    /**
     * Find latest products (most recently created)
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productEnabled = true " +
           "ORDER BY p.createdDt DESC")
    Page<Product> findLatestProducts(Pageable pageable);

    /**
     * Get product count by category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.productCategory.productCategoryId = :categoryId AND p.productEnabled = true")
    Long countByProductCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Get product count by state
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.productState.productStateId = :stateId AND p.productEnabled = true")
    Long countByProductStateId(@Param("stateId") Long stateId);
}