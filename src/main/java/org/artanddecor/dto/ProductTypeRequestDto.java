package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating and updating ProductType
 * Used for both create and update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product type")
public class ProductTypeRequestDto {

    @NotBlank(message = "Product type name is required")
    @Size(max = 64, message = "Product type name must not exceed 64 characters")
    @Schema(description = "Name of the product type", 
            example = "Art Decor")
    private String productTypeName;

    @NotBlank(message = "Product type slug is required")
    @Size(max = 64, message = "Product type slug must not exceed 64 characters")
    @Schema(description = "URL-friendly slug for the product type", 
            example = "art-decor")
    private String productTypeSlug;

    @Size(max = 256, message = "Product type display name must not exceed 256 characters")
    @Schema(description = "Display name for the product type", 
            example = "Art & Decor Items")
    private String productTypeDisplayName;

    @NotBlank(message = "Product type remark is required")
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    @Schema(description = "Remark or description about the product type", 
            example = "Category for artistic and decorative items")
    private String productTypeRemark;

    @Builder.Default
    @Schema(description = "Whether the product type is enabled", 
            example = "true")
    private Boolean productTypeEnabled = true;

    @Schema(description = "Image ID for the product type", 
            example = "1")
    private Long imageId;

    @Valid
    @Schema(description = "Optional SEO metadata for the product type")
    private SeoMetaRequestDto seoMeta;
}