package org.artanddecor.services;

import org.artanddecor.dto.ProductTypeDto;
import org.artanddecor.dto.ProductTypeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * ProductType Service Interface  
 * Defines business operations for product type management
 */
public interface ProductTypeService {

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    /**
     * Find product type by ID for admin management
     */
    Optional<ProductTypeDto> findProductTypeById(Long productTypeId);

    /**
     * Get product types by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, slug, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param productTypeSlug Filter by product type slug
     * @param pageable Pagination and sorting information
     * @return Page of ProductTypeDto matching criteria
     */
    Page<ProductTypeDto> getProductTypesByCriteria(String textSearch, Boolean enabled, String productTypeSlug, Pageable pageable);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product type using request DTO
     */
    ProductTypeDto createProductType(ProductTypeRequestDto requestDto);

    /**
     * Update existing product type using request DTO
     */
    ProductTypeDto updateProductType(Long productTypeId, ProductTypeRequestDto requestDto);
}