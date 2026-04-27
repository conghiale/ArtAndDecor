package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Combined DTO for ProductAttr with its associated ProductVariants
 * Used for displaying complete attribute information with stock for products
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttrWithVariantsDto {
    
    // ProductAttr information
    private ProductAttrDto productAttr;
    
    // List of product variants for this attribute
    private List<ProductVariantDto> variants;
    
    // Computed fields
    private Integer totalQuantity;
    private Integer variantCount;
    private Boolean hasStock;
    
    /**
     * Calculate total quantity across all variants
     */
    public Integer calculateTotalQuantity() {
        if (variants == null || variants.isEmpty()) {
            return 0;
        }
        return variants.stream()
                .mapToInt(variant -> variant.getProductVariantStock() != null ? variant.getProductVariantStock() : 0)
                .sum();
    }
    
    /**
     * Calculate number of variants
     */
    public Integer calculateVariantCount() {
        return variants != null ? variants.size() : 0;
    }
    
    /**
     * Check if any variant has stock
     */
    public Boolean calculateHasStock() {
        if (variants == null || variants.isEmpty()) {
            return false;
        }
        return variants.stream()
                .anyMatch(variant -> variant.getProductVariantStock() != null && variant.getProductVariantStock() > 0);
    }
}