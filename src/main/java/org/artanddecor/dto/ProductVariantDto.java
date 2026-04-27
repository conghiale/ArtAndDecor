package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductVariant DTO for API requests and responses
 * Represents the mapping between products and attributes with stock management
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {
    
    private Long productVariantId;
    
    private Long productId;
    
    private Long productAttributeId;
    
    @NotNull(message = "Product variant stock is required")
    @Min(value = 0, message = "Product variant stock must not be negative")
    private Integer productVariantStock;
    
    @Builder.Default
    private Boolean productVariantEnabled = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entities for complete information
    private ProductDto product;
    private ProductAttributeDto productAttribute;
    
    // Computed fields for convenience
    private Boolean isAvailable;
    private Boolean isOutOfStock;
    private String attributeName;
    private String attributeValue;
    private BigDecimal attributePrice;
}