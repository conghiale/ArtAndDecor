package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating and updating product attribute association
 * Used for both create and update operations of PRODUCT_ATTRIBUTE table
 * Contains all fields needed for creating/updating product attribute associations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product attribute association")
public class ProductAttributeRequestDto {

    @Min(value = 1, message = "Product ID must be positive")
    @Schema(description = "Database product identifier", 
            example = "1")
    private Long productId;
    
    @NotNull(message = "Product attribute ID is required")
    @Min(value = 1, message = "Product attribute ID must be positive")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTR table (e.g., Size, Color, Material)", 
            example = "2")
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