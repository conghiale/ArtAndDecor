package org.artanddecor.services;

import org.artanddecor.dto.ProductCategoryDto;
import org.artanddecor.dto.ProductCategoryRequestDto;
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
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    /**
     * Find product category by ID for admin management
     */
    Optional<ProductCategoryDto> findProductCategoryById(Long productCategoryId);

    /**
     * Get root categories (categories with no parent)
     * Returns enabled root categories only, ordered by name
     */
    List<ProductCategoryDto> getRootCategories();

    /**
     * Get product categories by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, slug, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param visible Filter by visible status
     * @param productTypeId Filter by product type ID
     * @param parentCategoryId Filter by parent category ID (null for all)
     * @param rootOnly Filter only root categories (cannot be used with parentCategoryId)
     * @param pageable Pagination and sorting information
     * @return Page of ProductCategoryDto matching criteria
     */
    Page<ProductCategoryDto> getProductCategoriesByCriteria(String textSearch, Boolean enabled, Boolean visible, 
                                                           Long productTypeId, Long parentCategoryId, Boolean rootOnly, 
                                                           Pageable pageable);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product category using request DTO
     */
    ProductCategoryDto createProductCategory(ProductCategoryRequestDto requestDto);

    /**
     * Update existing product category using request DTO
     */
    ProductCategoryDto updateProductCategory(Long productCategoryId, ProductCategoryRequestDto requestDto);
}