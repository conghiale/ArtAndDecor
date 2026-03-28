package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

/**
 * Product Attribute Request DTO for API create and update operations
 * Used within ProductRequestDto to handle product attributes during product creation/update
 * Contains only essential fields needed for creating/updating product attributes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product attribute data for creating/updating product attributes during product operations")
public class ProductAttributeRequestDto {
    
    @NotNull(message = "Product attribute ID is required")
    @Min(value = 1, message = "Product attribute ID must be positive")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTR table (e.g., Size, Color, Material)", 
            example = "1")
    private Long productAttrId;
    
    @NotBlank(message = "Product attribute value is required")
    @Size(max = 256, message = "Product attribute value must not exceed 256 characters")
    @Schema(description = "The value of the product attribute (e.g., 'Red', 'Large', '32GB', 'Cotton')", 
            example = "Red")
    private String productAttributeValue;
    
    @NotNull(message = "Product attribute quantity is required")
    @Min(value = 0, message = "Product attribute quantity must not be negative")
    @Schema(description = "Available quantity/stock for this specific attribute variant", 
            example = "10")
    private Integer productAttributeQuantity;
    
    @Builder.Default
    @Schema(description = "Whether this product attribute is enabled. Defaults to true if not specified", 
            example = "true")
    private Boolean productAttributeEnabled = true;
}