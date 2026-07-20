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
    
    @NotBlank(message = "Giá trị thuộc tính là bắt buộc")
    @Size(max = 256, message = "Giá trị thuộc tính không được vượt quá 256 ký tự")
    private String productAttributeValue;
    
    @Size(max = 256, message = "Tên hiển thị thuộc tính không được vượt quá 256 ký tự")
    private String productAttributeDisplayName;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị giá thuộc tính không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Giá trị giá thuộc tính được phép tối đa 13 chữ số phần nguyên và 2 chữ số phần thập phân")
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