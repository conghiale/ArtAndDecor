package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductType DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDto {
    
    private Long productTypeId;
    
    @NotBlank(message = "Product type name is required")
    @Size(max = 64, message = "Product type name must not exceed 64 characters")
    private String productTypeName;
    
    @NotBlank(message = "Product type slug is required")
    @Size(max = 64, message = "Product type slug must not exceed 64 characters")
    private String productTypeSlug;
    
    @Size(max = 256, message = "Product type display name must not exceed 256 characters")
    private String productTypeDisplayName;

    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String productTypeRemark;

    private Boolean productTypeEnabled;

    private Long seoMetaId;

    // Nested related entity
    private ImageDto image;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Additional information for reporting
    private Long productCount;
    
    /**
     * Check if type has products
     */
    public boolean hasProducts() {
        return productCount != null && productCount > 0;
    }
}