package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ProductAttrDto;
import org.ArtAndDecor.dto.ProductAttributeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * ProductAttr Service Interface
 * Defines business operations for product attribute management
 */
public interface ProductAttrService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (name > ID priority)
    // =============================================

    /**
     * Find product attribute by name for customer view
     */
    Optional<ProductAttrDto> findProductAttrByName(String productAttrName);

    /**
     * Get product attributes by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param pageable Pagination and sorting information
     * @return Page of ProductAttrDto matching criteria
     */
    Page<ProductAttrDto> getProductAttrsByCriteria(String textSearch, Boolean enabled, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name priority)
    // =============================================

    /**
     * Find product attribute by ID for admin management
     */
    Optional<ProductAttrDto> findProductAttrById(Long productAttrId);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product attribute
     */
    ProductAttrDto createProductAttr(ProductAttrDto productAttrDto);

    /**
     * Update existing product attribute
     */
    ProductAttrDto updateProductAttr(Long productAttrId, ProductAttrDto productAttrDto);

    /**
     * Delete product attribute by ID
     */
    void deleteProductAttrById(Long productAttrId);

    // =============================================
    // PRODUCT ATTRIBUTE ASSOCIATION OPERATIONS
    // =============================================

    /**
     * Update product attribute association by PRODUCT_ATTRIBUTE_ID
     */
    ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeDto productAttributeDto);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of product attributes
     */
    long getTotalProductAttrCount();

    /**
     * Get all product attribute names (for dropdown/combobox)
     */
    List<String> getAllProductAttrNames();

    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String productAttrName);
}