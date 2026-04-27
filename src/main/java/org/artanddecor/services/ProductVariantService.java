package org.artanddecor.services;

import org.artanddecor.dto.ProductVariantDto;
import org.artanddecor.dto.ProductVariantRequestDto;
import org.artanddecor.dto.ProductAttrWithVariantsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for ProductVariant operations
 * Manages product-attribute mappings with stock information
 */
public interface ProductVariantService {
    
    // =============================================
    // FIND OPERATIONS
    // =============================================
    
    /**
     * Find product variant by ID
     */
    Optional<ProductVariantDto> findProductVariantById(Long productVariantId);
    
    /**
     * Get product variants by product ID
     */
    List<ProductVariantDto> findVariantsByProductId(Long productId);
    
    /**
     * Get enabled product variants by product ID
     */
    List<ProductVariantDto> findEnabledVariantsByProductId(Long productId);
    
    /**
     * Get product variants by product attribute ID
     */
    List<ProductVariantDto> findVariantsByProductAttributeId(Long productAttributeId);
    
    /**
     * Find specific product variant by product and attribute
     */
    Optional<ProductVariantDto> findVariantByProductAndAttribute(Long productId, Long productAttributeId);
    
    /**
     * Get product variants with filtering and pagination
     */
    Page<ProductVariantDto> getProductVariantsByCriteria(
            Long productId, Long productAttributeId, Boolean enabled, Boolean hasStock, Pageable pageable);
    
    /**
     * Get variants with stock > 0
     */
    List<ProductVariantDto> findVariantsWithStock();
    
    /**
     * Get product attributes with variants for a specific product (grouped by attribute type)
     */
    List<ProductAttrWithVariantsDto> getProductAttributesWithVariants(Long productId);
    
    // =============================================
    // CRUD OPERATIONS  
    // =============================================
    
    /**
     * Create new product variant
     */
    ProductVariantDto createProductVariant(ProductVariantRequestDto requestDto);
    
    /**
     * Update existing product variant
     */
    ProductVariantDto updateProductVariant(Long productVariantId, ProductVariantRequestDto requestDto);
    
    /**
     * Delete product variant by ID
     */
    void deleteProductVariant(Long productVariantId);
    
    // =============================================
    // STOCK OPERATIONS
    // =============================================
    
    /**
     * Update product variant stock
     */
    ProductVariantDto updateProductVariantStock(Long productVariantId, Integer stock);
    
    /**
     * Decrease product variant stock (for orders)
     */
    boolean decreaseProductVariantStock(Long productVariantId, Integer quantity);
    
    /**
     * Increase product variant stock (for returns/restocking)
     */
    ProductVariantDto increaseProductVariantStock(Long productVariantId, Integer quantity);
    
    /**
     * Get total stock for a product (sum of all variants)
     */
    Long getTotalStockByProductId(Long productId);
    
    // =============================================
    // UTILITY OPERATIONS
    // =============================================
    
    /**
     * Check if product-attribute variant exists
     */
    boolean existsProductVariant(Long productId, Long productAttributeId);
    
    /**
     * Count variants by product ID
     */
    Long countVariantsByProductId(Long productId);
    
    /**
     * Count enabled variants by product ID
     */
    Long countEnabledVariantsByProductId(Long productId);
}