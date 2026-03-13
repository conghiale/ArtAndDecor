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
    @Size(max = 64, message = "Attribute name must not exceed 64 characters")
    private String productAttrName;
    
    private Boolean productAttrEnabled;

    @Size(max = 256, message = "Attribute display name must not exceed 256 characters")
    private String productAttrDisplayName;

    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String productAttrRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    /**
     * Check if attribute is enabled
     */
    public boolean isEnabled() {
        return productAttrEnabled == null || productAttrEnabled;
    }
}