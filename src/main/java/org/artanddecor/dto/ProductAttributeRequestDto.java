package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO for creating and updating product attribute definitions
 * Used for managing master attribute catalog with pricing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product attribute definitions")
public class ProductAttributeRequestDto {
    
    @NotNull(message = "ID thuộc tính sản phẩm là bắt buộc")
    @Min(value = 1, message = "ID thuộc tính sản phẩm phải lớn hơn 0")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTR table (e.g., Size, Color, Material)", 
            example = "1")
    private Long productAttrId;
    
    @NotBlank(message = "Giá trị thuộc tính là bắt buộc")
    @Size(max = 256, message = "Giá trị thuộc tính không được vượt quá 256 ký tự")
    @Schema(description = "The value of the product attribute (e.g., 'Red', 'Large', '32GB', 'Cotton')", 
            example = "40x60cm")
    private String productAttributeValue;
    
    @Size(max = 256, message = "Tên hiển thị thuộc tính không được vượt quá 256 ký tự")
    @Schema(description = "The display name for the product attribute value (e.g., 'Kích thước 40x60cm', 'Màu đỏ')", 
            example = "Kích thước 40x60cm")
    private String productAttributeDisplayName;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị giá thuộc tính không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Giá trị giá thuộc tính được phép tối đa 13 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Schema(description = "Price for this specific product attribute variant in VND. Null if no additional cost", 
            example = "1500000.00")
    private BigDecimal productAttributePrice;
    
    @Builder.Default
    @Schema(description = "Whether this product attribute is enabled. Defaults to true if not specified", 
            example = "true")
    private Boolean productAttributeEnabled = true;
}