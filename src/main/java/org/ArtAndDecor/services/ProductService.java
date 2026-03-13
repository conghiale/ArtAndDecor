package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ProductDto;
import org.ArtAndDecor.dto.ProductImageDto;
import org.ArtAndDecor.dto.ProductAttributeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Service Interface
 * Defines business operations for product management
 */
public interface ProductService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (slug > name > ID priority)
    // =============================================

    /**
     * Find product by slug for customer view
     */
    Optional<ProductDto> findProductBySlug(String productSlug);

    /**
     * Get products by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, slug, code, description, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param categoryId Filter by category ID
     * @param stateId Filter by state ID
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param inStock Filter by stock availability
     * @param productCode Filter by product code (partial match, case-insensitive)
     * @param featured Filter by featured status
     * @param highlighted Filter by highlighted status
     * @param pageable Pagination and sorting information
     * @return Page of ProductDto matching criteria
     */
    Page<ProductDto> getProductsByCriteria(String textSearch, Boolean enabled, Long categoryId, Long stateId, 
                                         BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, String productCode, 
                                         Boolean featured, Boolean highlighted, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name > slug priority)
    // =============================================

    /**
     * Find product by ID for admin management
     */
    Optional<ProductDto> findProductById(Long productId);

    /**
     * Find product by name
     */
    Optional<ProductDto> findProductByName(String productName);

    /**
     * Find product by code
     */
    Optional<ProductDto> findProductByCode(String productCode);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product
     */
    ProductDto createProduct(ProductDto productDto);

    /**
     * Update existing product
     */
    ProductDto updateProduct(Long productId, ProductDto productDto);

    // =============================================
    // PRODUCT IMAGE OPERATIONS
    // =============================================

    /**
     * Add image to product
     */
    ProductImageDto addImageToProduct(Long productId, Long imageId, Boolean isPrimary);

    /**
     * Remove image from product
     */
    void removeImageFromProduct(Long productId, Long imageId);

    /**
     * Get product images
     */
    List<ProductImageDto> getProductImages(Long productId);

    /**
     * Set primary image for product
     */
    ProductImageDto setPrimaryImage(Long productId, Long imageId);

    // =============================================
    // PRODUCT ATTRIBUTE OPERATIONS
    // =============================================

    /**
     * Add attribute to product
     */
    ProductAttributeDto addAttributeToProduct(Long productId, Long attrId, String attrValue);

    /**
     * Remove attribute from product
     */
    void removeAttributeFromProduct(Long productId, Long attrId);

    /**
     * Get product attributes
     */
    List<ProductAttributeDto> getProductAttributes(Long productId);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of products
     */
    long getTotalProductCount();

    /**
     * Get all enabled products (for dropdown/combobox)
     */
    List<ProductDto> getAllEnabledProducts();

    /**
     * Get enabled products by category slug
     */
    List<ProductDto> getEnabledProductsByCategorySlug(String categorySlug);

    /**
     * Get enabled products by type slug
     */
    List<ProductDto> getEnabledProductsByTypeSlug(String typeSlug);

    /**
     * Get featured products (enabled and featured = true) with pagination
     */
    Page<ProductDto> getFeaturedProducts(Pageable pageable);

    /**
     * Get highlighted products (enabled and highlighted = true) with pagination
     */
    Page<ProductDto> getHighlightedProducts(Pageable pageable);

    /**
     * Get latest products (enabled products ordered by creation date)
     */
    Page<ProductDto> getLatestProducts(Pageable pageable);

    /**
     * Get products by category ID
     */
    List<ProductDto> getProductsByCategoryId(Long categoryId);

    /**
     * Get products by state ID
     */
    List<ProductDto> getProductsByStateId(Long stateId);

    /**
     * Get products in stock
     */
    List<ProductDto> getProductsInStock();

    /**
     * Get top selling products
     */
    Page<ProductDto> getTopSellingProducts(Pageable pageable);

    /**
     * Get product count by category
     */
    Long getProductCountByCategoryId(Long categoryId);

    /**
     * Get product count by state
     */
    Long getProductCountByStateId(Long stateId);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsBySlug(String productSlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String productName);

    /**
     * Check if code exists (for validation)
     */
    boolean existsByCode(String productCode);
}