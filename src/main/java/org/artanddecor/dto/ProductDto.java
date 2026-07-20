package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product DTO for API requests and responses
 * Contains comprehensive information from PRODUCT, PRODUCT_CATEGORY, PRODUCT_TYPE, PRODUCT_STATE tables
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    // PRODUCT table fields
    private Long productId;
    
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    @Size(max = 100, message = "Tên sản phẩm không được vượt quá 100 ký tự")
    private String productName;
    
    @NotBlank(message = "Slug sản phẩm là bắt buộc")
    @Size(max = 64, message = "Slug sản phẩm không được vượt quá 64 ký tự")
    private String productSlug;
    
    @NotBlank(message = "Mã sản phẩm là bắt buộc")
    @Size(max = 64, message = "Mã sản phẩm không được vượt quá 64 ký tự")
    private String productCode;

    @DecimalMin(value = "0.0", message = "Giá sản phẩm không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Định dạng giá không hợp lệ")
    private BigDecimal productPrice;
    
    @NotNull(message = "Số lượng tồn kho là bắt buộc")
    @Min(value = 0, message = "Số lượng tồn kho không được là số âm")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Số lượng đã bán không được là số âm")
    private Integer soldQuantity;
    
    @Size(max = 65535, message = "Mô tả sản phẩm không được vượt quá 65535 ký tự")
    private String productDescription;
    
    private Boolean productEnabled;
    
    private Boolean productFeatured;
    
    private Boolean productHighlighted;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private ProductCategoryDto productCategory;
    private ProductStateDto productState;
    private SeoMetaDto seoMeta;
    
    // Related information
    private List<ProductImageDto> productImages;
    private List<ProductAttrWithVariantsDto> productAttributeGroups; // New grouped structure
    private List<ReviewDto> reviews;
    
    // Computed fields
    private boolean inStock;
    private BigDecimal totalStockValue;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalReviewLikes;

    /**
     * Calculate total stock value
     * @return Total value of stock
     */
    public BigDecimal calculateTotalStockValue() {
        if (stockQuantity == null || productPrice == null) {
            return BigDecimal.ZERO;
        }
        return productPrice.multiply(new BigDecimal(stockQuantity));
    }

    /**
     * Check if product is active
     * @return true if product is active
     */
    public boolean isActive() {
        return productState != null && "ACTIVE".equalsIgnoreCase(productState.getProductStateName());
    }

    /**
     * Check if product is out of stock
     * @return true if out of stock
     */
    public boolean isOutOfStock() {
        return (productState != null && "OUT_OF_STOCK".equalsIgnoreCase(productState.getProductStateName())) || 
               (stockQuantity != null && stockQuantity <= 0);
    }

    /**
     * Check if product is discontinued
     * @return true if discontinued
     */
    public boolean isDiscontinued() {
        return productState != null && "DISCONTINUED".equalsIgnoreCase(productState.getProductStateName());
    }

    /**
     * Get primary image URL if available
     * @return Primary image URL or null
     */
    public String getPrimaryImageUrl() {
        if (productImages != null) {
            return productImages.stream()
                .filter(ProductImageDto::getProductImagePrimary)
                .findFirst()
                .map(img -> img.getImage() != null ? img.getImage().getImageSlug() : null)
                .orElse(null);
        }
        return null;
    }
}