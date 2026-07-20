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

        @Min(value = 1, message = "ID sản phẩm phải lớn hơn 0")
    @Schema(description = "Database product identifier. Optional when creating product with variants", 
            example = "1")
    private Long productId;
    
        @NotNull(message = "ID thuộc tính sản phẩm là bắt buộc")
        @Min(value = 1, message = "ID thuộc tính sản phẩm phải lớn hơn 0")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTRIBUTE table", 
            example = "5")
    private Long productAttributeId;
    
        @NotNull(message = "Tồn kho biến thể sản phẩm là bắt buộc")
        @Min(value = 0, message = "Tồn kho biến thể sản phẩm không được là số âm")
    @Schema(description = "Available stock quantity for this product-attribute combination", 
            example = "25")
    private Integer productVariantStock;
    
    @Builder.Default
    @Schema(description = "Whether this product variant is enabled. Defaults to true if not specified", 
            example = "true")
    private Boolean productVariantEnabled = true;
}