package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductType DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDto {
    
    private Long productTypeId;
    
    @NotBlank(message = "Tên loại sản phẩm là bắt buộc")
    @Size(max = 64, message = "Tên loại sản phẩm không được vượt quá 64 ký tự")
    private String productTypeName;
    
    @NotBlank(message = "Slug loại sản phẩm là bắt buộc")
    @Size(max = 64, message = "Slug loại sản phẩm không được vượt quá 64 ký tự")
    private String productTypeSlug;
    
    @Size(max = 256, message = "Tên hiển thị loại sản phẩm không được vượt quá 256 ký tự")
    private String productTypeDisplayName;

    private String productTypeContent;

    @Size(max = 256, message = "Ghi chú loại sản phẩm không được vượt quá 256 ký tự")
    private String productTypeRemark;

    private Boolean productTypeEnabled;

    private Integer productTypeDisplayOrder;

    private SeoMetaDto seoMeta;

    // Nested related entity
    private ImageDto image;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Additional information for reporting
    private Long productCount;
    
    /**
     * Check if type has products
     */
    public boolean hasProducts() {
        return productCount != null && productCount > 0;
    }
}