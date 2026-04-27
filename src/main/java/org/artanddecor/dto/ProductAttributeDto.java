package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductAttribute DTO for API requests and responses
 * Represents master attribute definitions with pricing
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
    
    @Size(max = 256, message = "Product attribute display name must not exceed 256 characters")
    private String productAttributeDisplayName;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Product attribute price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Product attribute price must have at most 13 integer digits and 2 decimal places")
    private BigDecimal productAttributePrice;
    
    @Builder.Default
    private Boolean productAttributeEnabled = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entity (ProductAttr info only)
    private ProductAttrDto productAttr;
}