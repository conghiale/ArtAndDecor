package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for creating and updating ProductCategory
 * Used for both create and update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product category")
public class ProductCategoryRequestDto {

        @NotBlank(message = "Tên danh mục sản phẩm là bắt buộc")
        @Size(max = 64, message = "Tên danh mục sản phẩm không được vượt quá 64 ký tự")
    @Schema(description = "Name of the product category", 
            example = "Wall Paintings")
    private String productCategoryName;

        @NotBlank(message = "Slug danh mục sản phẩm là bắt buộc")
        @Size(max = 64, message = "Slug danh mục sản phẩm không được vượt quá 64 ký tự")
    @Schema(description = "URL-friendly slug for the product category", 
            example = "wall-paintings")
    private String productCategorySlug;

        @Size(max = 256, message = "Tên hiển thị danh mục sản phẩm không được vượt quá 256 ký tự")
    @Schema(description = "Display name for the product category", 
            example = "Beautiful Wall Paintings")
    private String productCategoryDisplayName;

    @Schema(description = "Detailed content/description for the product category", 
            example = "Detailed category content for landing page or storefront")
    private String productCategoryContent;

        @NotBlank(message = "Ghi chú danh mục sản phẩm là bắt buộc")
        @Size(max = 256, message = "Ghi chú không được vượt quá 256 ký tự")
    @Schema(description = "Remark or description about the product category", 
            example = "Category for wall decorative paintings")
    private String productCategoryRemark;

    @Builder.Default
    @Schema(description = "Whether the product category is enabled", 
            example = "true")
    private Boolean productCategoryEnabled = true;

    @Builder.Default
    @Schema(description = "Whether the product category is visible", 
            example = "true")
    private Boolean productCategoryVisible = true;

        @Min(value = 0, message = "Thứ tự hiển thị danh mục sản phẩm không được là số âm")
    @Schema(description = "Display order for product category in UI lists (optional)",
            example = "10")
    private Integer productCategoryDisplayOrder;

        @NotNull(message = "ID loại sản phẩm là bắt buộc")
        @Min(value = 1, message = "ID loại sản phẩm phải lớn hơn 0")
    @Schema(description = "Database ID of the product type", 
            example = "1")
    private Long productTypeId;

    @Schema(description = "Database ID of the parent category (for subcategories)", 
            example = "2")
    private Long productCategoryParentId;

    @Schema(description = "Image ID for the product category", 
            example = "3")
    private Long imageId;

    @Valid
    @Schema(description = "Optional SEO metadata for the product category")
    private SeoMetaRequestDto seoMeta;
}