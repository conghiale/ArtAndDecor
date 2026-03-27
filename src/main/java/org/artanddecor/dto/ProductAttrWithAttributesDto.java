package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO to represent ProductAttr with its corresponding ProductAttributes
 * Used for better client-side mapping to show product variants grouped by attribute type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttrWithAttributesDto {
    
    // ProductAttr information
    private ProductAttrDto productAttr;
    
    // List of ProductAttributes for this ProductAttr (only quantity > 0)
    private List<ProductAttributeDto> attributeValues;
    
    // Computed fields for convenience
    private Integer totalQuantity;
    private Integer variantCount;
    private Boolean hasStock;
    
    /**
     * Calculate total quantity across all attribute values
     */
    public Integer calculateTotalQuantity() {
        if (attributeValues == null) return 0;
        return attributeValues.stream()
                .mapToInt(attr -> attr.getProductAttributeQuantity() != null ? attr.getProductAttributeQuantity() : 0)
                .sum();
    }
    
    /**
     * Get variant count (number of different values for this attribute type)
     */
    public Integer calculateVariantCount() {
        return attributeValues != null ? attributeValues.size() : 0;
    }
    
    /**
     * Check if this attribute type has any stock available
     */
    public Boolean calculateHasStock() {
        if (attributeValues == null) return false;
        return attributeValues.stream()
                .anyMatch(attr -> attr.getProductAttributeQuantity() != null && attr.getProductAttributeQuantity() > 0);
    }
}