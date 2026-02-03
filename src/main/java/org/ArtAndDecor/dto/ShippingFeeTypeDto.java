package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ShippingFeeType DTO for API requests and responses
 * Contains information from SHIPPING_FEE_TYPE table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeTypeDto {
    
    private Long shippingFeeTypeId;
    
    @NotBlank(message = "Shipping fee type name is required")
    @Size(max = 64, message = "Shipping fee type name must not exceed 64 characters")
    private String shippingFeeTypeName;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String shippingFeeTypeRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String shippingFeeTypeRemark;
    
    @NotNull(message = "Shipping fee type enabled flag is required")
    private Boolean shippingFeeTypeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
