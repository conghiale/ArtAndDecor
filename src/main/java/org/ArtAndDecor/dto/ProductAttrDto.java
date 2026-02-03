package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductAttr DTO - Auxiliary class containing only PRODUCT_ATTR table data
 * Represents attribute definitions/templates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttrDto {
    
    private Long productAttrId;
    
    @NotBlank(message = "Attribute name is required")
    @Size(max = 100, message = "Attribute name must not exceed 100 characters")
    private String productAttrName;
    
    @Size(max = 100, message = "Attribute name (English) must not exceed 100 characters")
    private String productAttrNameEn;
    
    @Size(max = 50, message = "Attribute slug must not exceed 50 characters")
    private String productAttrSlug;
    
    @Size(max = 255, message = "Attribute description must not exceed 255 characters")
    private String productAttrDesc;
    
    @Size(max = 255, message = "Attribute description (English) must not exceed 255 characters")
    private String productAttrDescEn;
    
    @Size(max = 50, message = "Data type must not exceed 50 characters")
    private String dataType; // e.g., "STRING", "NUMBER", "BOOLEAN", "DATE"
    
    private Boolean productAttrRequired;
    
    private Boolean productAttrEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    /**
     * Check if attribute is required
     */
    public boolean isRequired() {
        return productAttrRequired != null && productAttrRequired;
    }
    
    /**
     * Check if attribute is enabled
     */
    public boolean isEnabled() {
        return productAttrEnabled == null || productAttrEnabled;
    }
}