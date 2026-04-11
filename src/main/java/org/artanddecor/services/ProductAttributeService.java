package org.artanddecor.services;

import org.artanddecor.dto.GroupedProductAttributeDto;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
    
    // =============================================
    // CUSTOM OPERATIONS
    // =============================================
    
    /**
     * Get grouped product attributes by value and price
     * @param productId Optional filter by product ID
     * @param productAttrId Optional filter by product attribute ID
     * @param enabled Optional filter by enabled status
     * @return List of grouped attribute values with pricing and sample data
     */
    List<GroupedProductAttributeDto> getGroupedProductAttributes(Long productId, Long productAttrId, Boolean enabled);
    
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
}