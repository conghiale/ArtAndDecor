package org.artanddecor.services;

import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.ProductRequestDto;
import org.artanddecor.dto.ProductImageDto;
import org.artanddecor.dto.ProductAttributeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
     * @param typeId Filter by product type ID
     * @param stateId Filter by state ID
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param inStock Filter by stock availability
     * @param productCode Filter by product code (partial match, case-insensitive)
     * @param featured Filter by featured status
     * @param highlighted Filter by highlighted status
     * @param productCategorySlug Filter by product category slug (URL-friendly identifier)
     * @param productTypeSlug Filter by product type slug (URL-friendly identifier)
     * @param pageable Pagination and sorting information
     * @return Page of ProductDto matching criteria
     */
    Page<ProductDto> getProductsByCriteria(String textSearch, Boolean enabled, Long categoryId, Long typeId, Long stateId, 
                                         BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, String productCode, 
                                         Boolean featured, Boolean highlighted, String productCategorySlug, String productTypeSlug, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name > slug priority)
    // =============================================

    /**
     * Find product by ID for admin management
     */
    Optional<ProductDto> findProductById(Long productId);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product using simplified DTO with IDs and image/attribute support  
     * @param productRequestDto Product request data with image IDs and product attributes
     * @return Created ProductDto with associated images and attributes
     */
    ProductDto createProduct(ProductRequestDto productRequestDto);

    /**
     * Update existing product using simplified DTO with IDs and image/attribute support
     * @param productId Product ID to update
     * @param productRequestDto Product request data with image IDs and product attributes
     * @return Updated ProductDto with associated images and attributes
     */
    ProductDto updateProduct(Long productId, ProductRequestDto productRequestDto);

    // =============================================
    // PRODUCT IMAGE OPERATIONS
    // =============================================

    /**
     * Remove image from product
     */
    void removeImageFromProduct(Long productId, Long imageId);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of products
     */
    long getTotalProductCount();

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
     * Get products in stock
     */
    List<ProductDto> getProductsInStock();

    /**
     * Get top selling products
     */
    Page<ProductDto> getTopSellingProducts(Pageable pageable);

    /**
     * Search products by similar images using AI service
     * @param imageFile The image file to search for similar images
     * @param isSelling Filter for selling products (null = no filter, true = selling only, false = non-selling only)
     * @param pageable Pagination information  
     * @return Page of products with similar images
     * @throws Exception if AI service call fails or configuration is invalid
     */
    Page<ProductDto> searchProductsBySimilarImage(
        MultipartFile imageFile, 
        Boolean isSelling, 
        Pageable pageable
    ) throws Exception;

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