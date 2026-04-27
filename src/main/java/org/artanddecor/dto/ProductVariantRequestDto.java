package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating and updating product variants
 * Used for managing the mapping between products and attributes with stock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product variants")
public class ProductVariantRequestDto {

    @Min(value = 1, message = "Product ID must be positive")
    @Schema(description = "Database product identifier. Optional when creating product with variants", 
            example = "1")
    private Long productId;
    
    @NotNull(message = "Product attribute ID is required")
    @Min(value = 1, message = "Product attribute ID must be positive")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTRIBUTE table", 
            example = "5")
    private Long productAttributeId;
    
    @NotNull(message = "Product variant stock is required")
    @Min(value = 0, message = "Product variant stock must not be negative")
    @Schema(description = "Available stock quantity for this product-attribute combination", 
            example = "25")
    private Integer productVariantStock;
    
    @Builder.Default
    @Schema(description = "Whether this product variant is enabled. Defaults to true if not specified", 
            example = "true")
    private Boolean productVariantEnabled = true;
}