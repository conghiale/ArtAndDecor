package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
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

    @NotBlank(message = "Tên loại sản phẩm là bắt buộc")
    @Size(max = 64, message = "Tên loại sản phẩm không được vượt quá 64 ký tự")
    @Schema(description = "Name of the product type", 
            example = "Art Decor")
    private String productTypeName;

    @NotBlank(message = "Slug loại sản phẩm là bắt buộc")
    @Size(max = 64, message = "Slug loại sản phẩm không được vượt quá 64 ký tự")
    @Schema(description = "URL-friendly slug for the product type", 
            example = "art-decor")
    private String productTypeSlug;

    @Size(max = 256, message = "Tên hiển thị loại sản phẩm không được vượt quá 256 ký tự")
    @Schema(description = "Display name for the product type", 
            example = "Art & Decor Items")
    private String productTypeDisplayName;

    @Schema(description = "Detailed content/description for the product type", 
            example = "Detailed content about this product type for storefront pages")
    private String productTypeContent;

    @NotBlank(message = "Ghi chú loại sản phẩm là bắt buộc")
    @Size(max = 256, message = "Ghi chú loại sản phẩm không được vượt quá 256 ký tự")
    @Schema(description = "Remark or description about the product type", 
            example = "Category for artistic and decorative items")
    private String productTypeRemark;

    @Builder.Default
    @Schema(description = "Whether the product type is enabled", 
            example = "true")
    private Boolean productTypeEnabled = true;

    @Min(value = 0, message = "Thứ tự hiển thị loại sản phẩm không được là số âm")
    @Schema(description = "Display order for product type in UI lists (optional)",
            example = "1")
    private Integer productTypeDisplayOrder;

    @Schema(description = "Image ID for the product type", 
            example = "1")
    private Long imageId;

    @Valid
    @Schema(description = "Optional SEO metadata for the product type")
    private SeoMetaRequestDto seoMeta;
}