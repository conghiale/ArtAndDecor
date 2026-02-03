package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductCategory DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    
    private Long productCategoryId;
    
    @NotBlank(message = "Product category name is required")
    @Size(max = 64, message = "Product category name must not exceed 64 characters")
    private String productCategoryName;
    
    @NotBlank(message = "Product category slug is required")
    @Size(max = 64, message = "Product category slug must not exceed 64 characters")
    private String productCategorySlug;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String productCategoryRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String productCategoryRemark;
    
    private Boolean productCategoryEnabled;
    
    private Boolean productCategoryDisplay;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Additional information for reporting
    private Long productCount;
    
    /**
     * Constructor for essential category fields
     * @param productCategoryName Category name
     * @param productCategorySlug Category slug
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