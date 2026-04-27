package org.artanddecor.services;

import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for ProductAttribute operations
 * Manages master attribute definitions with pricing
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
     * Find product attributes by attribute type ID
     */
    List<ProductAttributeDto> findAttributesByAttrId(Long attrId);
    
    /**
     * Find product attributes by value
     */
    List<ProductAttributeDto> findAttributesByValue(String attributeValue);
    
    /**
     * Find specific attribute by attr ID, value and price
     */
    Optional<ProductAttributeDto> findByAttrIdValueAndPrice(Long attrId, String value, BigDecimal price);
    
    /**
     * Get product attributes with filtering and pagination
     */
    Page<ProductAttributeDto> getProductAttributesByCriteria(
            Long attrId, Boolean enabled, String attributeValue, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Get distinct attribute values for an attribute type
     */
    List<String> getDistinctValuesByAttrId(Long attrId);
    
    /**
     * Get price range for an attribute type [minPrice, maxPrice]
     */
    BigDecimal[] getPriceRangeByAttrId(Long attrId);
    
    // =============================================
    // CRUD OPERATIONS  
    // =============================================
    
    /**
     * Create new product attribute definition
     */
    ProductAttributeDto createProductAttribute(ProductAttributeRequestDto requestDto);
    
    /**
     * Update existing product attribute by ID
     */
    ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeRequestDto requestDto);
    
    /**
     * Delete product attribute by ID
     */
    void deleteProductAttribute(Long productAttributeId);
    
    // =============================================
    // BATCH OPERATIONS
    // =============================================
    
    /**
     * Update product attribute prices by attribute values
     * @param productAttributeValues List of attribute values to update
     * @param productAttributePrice New price to set for all matching values
     * @return Number of records updated
     */
    int updatePricesByAttributeValues(List<String> productAttributeValues, BigDecimal productAttributePrice);
    
    /**
     * Update product attributes by attribute values (batch update)
     * @param productAttributeValues List of attribute values to update
     * @param requestDto Request DTO containing update data (fields can be null to skip update)
     * @return Number of records updated
     */
    int updateByAttributeValues(List<String> productAttributeValues, ProductAttributeRequestDto requestDto);
    
    /**
     * Delete product attributes by attribute values
     * @param productAttributeValues List of attribute values to delete
     * @return Number of records deleted
     */
    int deleteByAttributeValues(List<String> productAttributeValues);
    
    /**
     * Delete product attributes by product attribute ID
     * @param productAttrId Product attribute ID to delete all associations
     * @return Number of records deleted
     */
    int deleteByProductAttrId(Long productAttrId);
    
    // =============================================
    // UTILITY OPERATIONS
    // =============================================
    
    /**
     * Check if attribute combination exists (for validation)
     */
    boolean existsAttributeCombination(Long attrId, String value, BigDecimal price);
    
    /**
     * Count attributes by attribute type
     */
    Long countByProductAttrId(Long attrId);
    
    /**
     * Find attributes by multiple values
     */
    List<ProductAttributeDto> findByAttributeValues(List<String> values);
}