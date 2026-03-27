package org.artanddecor.dto;

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
    
    @NotBlank(message = "Product attribute value is required")
    @Size(max = 256, message = "Product attribute value must not exceed 256 characters")
    private String productAttributeValue;
    
    @NotNull(message = "Product attribute quantity is required")
    @Min(value = 0, message = "Product attribute quantity must not be negative")
    private Integer productAttributeQuantity;
    
    @Builder.Default
    private Boolean productAttributeEnabled = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entity (only ProductAttr, not Product to avoid circular reference)
    private ProductAttrDto productAttr;
}