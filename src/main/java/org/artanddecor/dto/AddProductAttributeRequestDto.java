package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating product attribute association
 * Used for creating new product attribute links between products and attributes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating product attribute association")
public class AddProductAttributeRequestDto {

        @NotNull(message = "ID sản phẩm là bắt buộc")
        @Min(value = 1, message = "ID sản phẩm phải lớn hơn 0")
    @Schema(description = "Database product identifier", 
            example = "1")
    private Long productId;

        @NotNull(message = "ID thuộc tính sản phẩm là bắt buộc")
        @Min(value = 1, message = "ID thuộc tính sản phẩm phải lớn hơn 0")
    @Schema(description = "Database product attribute identifier from PRODUCT_ATTR table", 
            example = "2")
    private Long productAttrId;

        @NotBlank(message = "Giá trị thuộc tính là bắt buộc")
        @Size(min = 1, max = 500, message = "Giá trị thuộc tính phải có từ 1 đến 500 ký tự")
    @Schema(description = "The value of the product attribute (e.g., 'Red', 'Large', '32GB')", 
            example = "Red")
    private String attrValue;

        @NotNull(message = "Số lượng là bắt buộc")
        @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    @Schema(description = "Initial quantity/stock for this attribute variant. Use 0 for out-of-stock items.", 
            example = "10")
    private Integer quantity;

    @Schema(description = "Optional remark or note about this attribute variant", 
            example = "Special limited edition color")
        @Size(max = 1000, message = "Ghi chú phải ít hơn 1000 ký tự")
    private String remark;
}