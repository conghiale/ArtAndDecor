package org.artanddecor.services;

import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for ProductAttribute operations
 * Provides CRUD operations for product attribute associations
 */
public interface ProductAttributeService {
    
    // =============================================
    // FIND OPERATIONS
    // =============================================
    
    /**
     * Find product attribute by ID
     */
    Optional<ProductAttributeDto> findProductAttributeById(Long productAttributeId);
    
    /**
     * Get product attributes with filtering and pagination
     */
    Page<ProductAttributeDto> getProductAttributesByCriteria(
            Long productId, Long productAttrId, Boolean enabled, String attributeValue, Pageable pageable);
    
    // =============================================
    // CRUD OPERATIONS  
    // =============================================
    
    /**
     * Create new product attribute association using DTO
     */
    ProductAttributeDto createProductAttribute(ProductAttributeRequestDto requestDto);
    
    /**
     * Update existing product attribute by ID using DTO
     */
    ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeRequestDto requestDto);
    
    /**
     * Update product attribute quantity only
     */
    ProductAttributeDto updateProductAttributeQuantity(Long productAttributeId, Integer quantity);
    
    /**
     * Delete product attribute by ID
     */
    void deleteProductAttribute(Long productAttributeId);
}