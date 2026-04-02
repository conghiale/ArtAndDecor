package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating and updating ProductAttr
 * Used for both create and update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product attribute definition")
public class ProductAttrRequestDto {

    @NotBlank(message = "Product attribute name is required")
    @Size(max = 64, message = "Product attribute name must not exceed 64 characters")
    @Schema(description = "Name of the product attribute", 
            example = "Color")
    private String productAttrName;

    @Size(max = 256, message = "Product attribute display name must not exceed 256 characters")
    @Schema(description = "Display name for the product attribute", 
            example = "Product Color")
    private String productAttrDisplayName;

    @NotBlank(message = "Product attribute remark is required")
    @Size(max = 256, message = "Product attribute remark must not exceed 256 characters")
    @Schema(description = "Remark or description about the product attribute", 
            example = "Color variation of the product")
    private String productAttrRemark;

    @Builder.Default
    @Schema(description = "Whether the product attribute is enabled", 
            example = "true")
    private Boolean productAttrEnabled = true;
}