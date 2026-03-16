package org.artanddecor.services;

import org.artanddecor.dto.ProductCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * ProductCategory Service Interface
 * Defines business operations for product category management
 */
public interface ProductCategoryService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (slug > name > ID priority)
    // =============================================

    /**
     * Find product category by slug for customer view
     */
    Optional<ProductCategoryDto> findProductCategoryBySlug(String productCategorySlug);

    /**
     * Get product categories by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, slug, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param visible Filter by visible status
     * @param productTypeId Filter by product type ID
     * @param pageable Pagination and sorting information
     * @return Page of ProductCategoryDto matching criteria
     */
    Page<ProductCategoryDto> getProductCategoriesByCriteria(String textSearch, Boolean enabled, Boolean visible, Long productTypeId, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name > slug priority)
    // =============================================

    /**
     * Find product category by ID for admin management
     */
    Optional<ProductCategoryDto> findProductCategoryById(Long productCategoryId);

    /**
     * Find product category by name
     */
    Optional<ProductCategoryDto> findProductCategoryByName(String productCategoryName);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product category
     */
    ProductCategoryDto createProductCategory(ProductCategoryDto productCategoryDto);

    /**
     * Update existing product category
     */
    ProductCategoryDto updateProductCategory(Long productCategoryId, ProductCategoryDto productCategoryDto);

    /**
     * Delete product category by ID
     */
    void deleteProductCategoryById(Long productCategoryId);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of product categories
     */
    long getTotalProductCategoryCount();

    /**
     * Get categories by product type ID
     */
    List<ProductCategoryDto> getCategoriesByProductTypeId(Long productTypeId);

    /**
     * Get root categories (no parent)
     */
    List<ProductCategoryDto> getRootCategories();

    /**
     * Get child categories by parent ID
     */
    List<ProductCategoryDto> getChildCategoriesByParentId(Long parentId);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsBySlug(String productCategorySlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String productCategoryName);
}