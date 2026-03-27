package org.artanddecor.services;

import org.artanddecor.dto.ProductAttributeDto;
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
    // CRUD OPERATIONS  
    // =============================================
    
    /**
     * Create new product attribute association
     */
    ProductAttributeDto createProductAttribute(Long productId, Long productAttrId, String attributeValue, Integer quantity);
    
    /**
     * Update existing product attribute by ID
     */
    ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeDto productAttributeDto);
    
    /**
     * Update product attribute quantity
     */
    ProductAttributeDto updateProductAttributeQuantity(Long productAttributeId, Integer newQuantity);
    
    /**
     * Delete product attribute by ID
     */
    void deleteProductAttribute(Long productAttributeId);
    
    /**
     * Delete product attribute by combination
     */
    void deleteProductAttribute(Long productId, Long productAttrId, String attributeValue);
    
    // =============================================
    // FIND OPERATIONS
    // =============================================
    
    /**
     * Find product attribute by ID
     */
    Optional<ProductAttributeDto> findProductAttributeById(Long productAttributeId);
    
    /**
     * Find product attributes by product ID
     */
    List<ProductAttributeDto> findProductAttributesByProductId(Long productId);
    
    /**
     * Find product attributes by attribute ID  
     */
    List<ProductAttributeDto> findProductAttributesByAttrId(Long productAttrId);
    
    /**
     * Find specific product attribute by product, attribute and value
     */
    Optional<ProductAttributeDto> findProductAttributeByCombo(Long productId, Long productAttrId, String attributeValue);
    
    /**
     * Get product attributes with filtering and pagination
     */
    Page<ProductAttributeDto> getProductAttributesByCriteria(
            Long productId, Long productAttrId, Boolean enabled, String attributeValue, Pageable pageable);
    
    // =============================================
    // UTILITY OPERATIONS
    // =============================================
    
    /**
     * Check if product attribute combination exists
     */
    boolean existsByCombo(Long productId, Long productAttrId, String attributeValue);
    
    /**
     * Count product attributes by product ID
     */
    long countByProductId(Long productId);
    
    /**
     * Get total product attributes count
     */
    long getTotalCount();
}