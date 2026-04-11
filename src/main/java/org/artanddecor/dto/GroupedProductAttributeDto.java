package org.artanddecor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for grouped Product Attribute responses
 * Used for API responses that group attributes by value and price
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupedProductAttributeDto {
    
    /**
     * Product attribute value (e.g., "30x40", "Red", "Medium")
     */
    private String productAttributeValue;
    
    /**
     * Product attribute price
     */
    private BigDecimal productAttributePrice;
    
    /**
     * Whether this attribute value is enabled
     */
    private Boolean productAttributeEnabled;
    
    /**
     * Sample product ID from the group
     */
    private Long productId;
    
    /**
     * Sample product attribute ID from the group
     */
    private Long productAttrId;
    
    /**
     * Sample product attribute association ID from the group
     */
    private Long productAttributeId;
    
    /**
     * Sample quantity from the group
     */
    private Integer productAttributeQuantity;
    
    /**
     * Modification date from the first record in the group
     */
    private LocalDateTime modifiedDt;
}