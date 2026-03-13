package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;
    
    @NotBlank(message = "Product slug is required")
    @Size(max = 64, message = "Product slug must not exceed 64 characters")
    private String productSlug;
    
    @NotBlank(message = "Product code is required")
    @Size(max = 64, message = "Product code must not exceed 64 characters")
    private String productCode;

    @DecimalMin(value = "0.0", message = "Product price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid price format")
    private BigDecimal productPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Sold quantity must not be negative")
    private Integer soldQuantity;
    
    @NotBlank(message = "Product description is required")
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
    private List<ProductAttributeDto> productAttributes;
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