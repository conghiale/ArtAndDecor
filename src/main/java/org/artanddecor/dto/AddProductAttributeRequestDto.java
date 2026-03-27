package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating product attribute association
 * Used for creating new product attribute links between products and attributes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating product attribute association")
public class AddProductAttributeRequestDto {

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be positive")
    @Schema(description = "Database product identifier", 
            example = "1")
    private Long productId;

    @NotNull(message = "Product attribute ID is required")
    @Min(value = 1, message = "Product attribute ID must be positive")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTR table", 
            example = "2")
    private Long productAttrId;

    @NotBlank(message = "Attribute value is required")
    @Size(min = 1, max = 500, message = "Attribute value must be between 1 and 500 characters")
    @Schema(description = "The value of the product attribute (e.g., 'Red', 'Large', '32GB')", 
            example = "Red")
    private String attrValue;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be 0 or positive")
    @Schema(description = "Initial quantity/stock for this attribute variant. Use 0 for out-of-stock items.", 
            example = "10")
    private Integer quantity;

    @Schema(description = "Optional remark or note about this attribute variant", 
            example = "Special limited edition color")
    @Size(max = 1000, message = "Remark must be less than 1000 characters")
    private String remark;
}