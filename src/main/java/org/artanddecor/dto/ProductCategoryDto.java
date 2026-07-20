package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductCategory DTO for API requests and responses with hierarchical support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    
    private Long productCategoryId;
    
    @NotBlank(message = "Tên danh mục sản phẩm là bắt buộc")
    @Size(max = 64, message = "Tên danh mục sản phẩm không được vượt quá 64 ký tự")
    private String productCategoryName;
    
    @NotBlank(message = "Slug danh mục sản phẩm là bắt buộc")
    @Size(max = 64, message = "Slug danh mục sản phẩm không được vượt quá 64 ký tự")
    private String productCategorySlug;
    
    @Size(max = 256, message = "Tên hiển thị danh mục sản phẩm không được vượt quá 256 ký tự")
    private String productCategoryDisplayName;

    private String productCategoryContent;

    @Size(max = 256, message = "Ghi chú danh mục sản phẩm không được vượt quá 256 ký tự")
    private String productCategoryRemark;

    private Boolean productCategoryEnabled;

    private Boolean productCategoryVisible;

    private Integer productCategoryDisplayOrder;

    private SeoMetaDto seoMeta;

    private Long productTypeId;

    private Long parentCategoryId;

    // Nested related entities
    private ProductTypeDto productType;
    private ImageDto image;
    
    // Hierarchical structure support - list of child categories
    private List<ProductCategoryDto> children;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Additional information for reporting
    private Long productCount;

    /**
     * Constructor with essential fields
     * @param productCategoryName Name of the product category
     * @param productCategorySlug URL-friendly slug for the product category
     * @param productCategoryEnabled Enable status
     */
    public ProductCategoryDto(String productCategoryName, String productCategorySlug, Boolean productCategoryEnabled) {
        this.productCategoryName = productCategoryName;
        this.productCategorySlug = productCategorySlug;
        this.productCategoryEnabled = productCategoryEnabled;
    }
    
    /**
     * Check if category has products
     */
    public boolean hasProducts() {
        return productCount != null && productCount > 0;
    }
}