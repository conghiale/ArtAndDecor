package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * Product Request DTO for API create and update requests
 * Contains minimal required information for creating/updating a product with IDs instead of nested objects
 * This provides a cleaner and simpler interface for client integration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    @Size(max = 100, message = "Tên sản phẩm không được vượt quá 100 ký tự")
    private String productName;
    
    @NotBlank(message = "Slug sản phẩm là bắt buộc")
    @Size(max = 64, message = "Slug sản phẩm không được vượt quá 64 ký tự")
    private String productSlug;
    
    @NotBlank(message = "Mã sản phẩm là bắt buộc")
    @Size(max = 64, message = "Mã sản phẩm không được vượt quá 64 ký tự")
    private String productCode;

    @NotNull(message = "ID danh mục sản phẩm là bắt buộc")
    private Long productCategoryId;

    @NotNull(message = "ID trạng thái sản phẩm là bắt buộc")
    private Long productStateId;

    @DecimalMin(value = "0.0", message = "Giá sản phẩm không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Định dạng giá không hợp lệ")
    private BigDecimal productPrice;
    
    @NotNull(message = "Số lượng tồn kho là bắt buộc")
    @Min(value = 0, message = "Số lượng tồn kho không được là số âm")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Số lượng đã bán không được là số âm")
    private Integer soldQuantity = 0;
    
    @Size(max = 65535, message = "Mô tả sản phẩm không được vượt quá 65535 ký tự")
    private String productDescription;
    
    private Boolean productEnabled = true;
    
    private Boolean productFeatured = false;
    
    private Boolean productHighlighted = false;
    
    /**
     * SEO metadata for this product (optional)
     * When provided, SEO meta entry will be created/updated in SEO_META table
     * This enables comprehensive search engine optimization for the product
     */
    @Valid
    private SeoMetaRequestDto seoMeta;
    
    /**
     * List of image IDs to associate with this product
     * Client should upload images first and get image IDs, then include them here
     * These will be saved to PRODUCT_IMAGE table with appropriate primary/secondary flags
     */
    private List<Long> imageIds;
    
    /**
     * ID of the image to set as primary (must be included in imageIds list)
     * If not specified, the first image in imageIds will be set as primary
     */
    private Long primaryImageId;
    
    /**
     * List of product variants to associate with this product
     * Each variant contains productAttributeId (from PRODUCT_ATTRIBUTE table), quantity, and enabled status
     * These will be saved to PRODUCT_VARIANT table
     * Optional - product can be created without variants
     */
    @Valid
    private List<ProductVariantRequestDto> productVariants;
}