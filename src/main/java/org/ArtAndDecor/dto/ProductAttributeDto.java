package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductAttribute DTO for API requests and responses
 * Contains PRODUCT_ATTRIBUTE table data with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDto {
    
    private Long productAttributeId;
    
    @NotBlank(message = "Attribute value is required")
    @Size(max = 255, message = "Attribute value must not exceed 255 characters")
    private String attributeValue;
    
    private Boolean productAttributeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entities following clean architecture
    private ProductDto product;
    private ProductAttrDto productAttr;
}