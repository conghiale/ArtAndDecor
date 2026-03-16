package org.artanddecor.services;

import org.artanddecor.dto.ProductTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * ProductType Service Interface
 * Defines business operations for product type management
 */
public interface ProductTypeService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (slug > name > ID priority)
    // =============================================

    /**
     * Find product type by slug for customer view
     */
    Optional<ProductTypeDto> findProductTypeBySlug(String productTypeSlug);

    /**
     * Get product types by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, slug, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param pageable Pagination and sorting information
     * @return Page of ProductTypeDto matching criteria
     */
    Page<ProductTypeDto> getProductTypesByCriteria(String textSearch, Boolean enabled, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name > slug priority)
    // =============================================

    /**
     * Find product type by ID for admin management
     */
    Optional<ProductTypeDto> findProductTypeById(Long productTypeId);

    /**
     * Find product type by name
     */
    Optional<ProductTypeDto> findProductTypeByName(String productTypeName);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product type
     */
    ProductTypeDto createProductType(ProductTypeDto productTypeDto);

    /**
     * Update existing product type
     */
    ProductTypeDto updateProductType(Long productTypeId, ProductTypeDto productTypeDto);

    /**
     * Delete product type by ID
     */
    void deleteProductTypeById(Long productTypeId);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of product types
     */
    long getTotalProductTypeCount();

    /**
     * Check if slug exists (for validation)
     */
    boolean existsBySlug(String productTypeSlug);

    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String productTypeName);
}