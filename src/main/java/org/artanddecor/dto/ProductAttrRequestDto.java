package org.artanddecor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating and updating ProductAttr
 * Used for both create and update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating/updating product attribute definition")
public class ProductAttrRequestDto {

    @NotBlank(message = "Tên thuộc tính là bắt buộc")
    @Size(max = 64, message = "Tên thuộc tính không được vượt quá 64 ký tự")
    @Schema(description = "Name of the product attribute", 
            example = "Color")
    private String productAttrName;

    @Size(max = 256, message = "Tên hiển thị thuộc tính không được vượt quá 256 ký tự")
    @Schema(description = "Display name for the product attribute", 
            example = "Product Color")
    private String productAttrDisplayName;

    @NotBlank(message = "Ghi chú thuộc tính là bắt buộc")
    @Size(max = 256, message = "Ghi chú thuộc tính không được vượt quá 256 ký tự")
    @Schema(description = "Remark or description about the product attribute", 
            example = "Color variation of the product")
    private String productAttrRemark;

    @Builder.Default
    @Schema(description = "Whether the product attribute is enabled", 
            example = "true")
    private Boolean productAttrEnabled = true;
}