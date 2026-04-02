package org.artanddecor.services;

import org.artanddecor.dto.ProductAttrDto;
import org.artanddecor.dto.ProductAttrRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * ProductAttr Service Interface
 * Defines business operations for product attribute management
 */
public interface ProductAttrService {

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    /**
     * Find product attribute by ID for admin management
     */
    Optional<ProductAttrDto> findProductAttrById(Long productAttrId);

    /**
     * Get product attributes by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param pageable Pagination and sorting information
     * @return Page of ProductAttrDto matching criteria
     */
    Page<ProductAttrDto> getProductAttrsByCriteria(String textSearch, Boolean enabled, Pageable pageable);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product attribute using request DTO
     */
    ProductAttrDto createProductAttr(ProductAttrRequestDto requestDto);

    /**
     * Update existing product attribute using request DTO
     */
    ProductAttrDto updateProductAttr(Long productAttrId, ProductAttrRequestDto requestDto);
}