package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên thuộc tính là bắt buộc")
    @Size(max = 64, message = "Tên thuộc tính không được vượt quá 64 ký tự")
    private String productAttrName;
    
    private Boolean productAttrEnabled;

    @Size(max = 256, message = "Tên hiển thị thuộc tính không được vượt quá 256 ký tự")
    private String productAttrDisplayName;

    @Size(max = 256, message = "Ghi chú thuộc tính không được vượt quá 256 ký tự")
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