package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên loại phí vận chuyển là bắt buộc")
    @Size(max = 64, message = "Tên loại phí vận chuyển không được vượt quá 64 ký tự")
    private String shippingFeeTypeName;
    
    @Size(max = 256, message = "Tên hiển thị loại phí vận chuyển không được vượt quá 256 ký tự")
    private String shippingFeeTypeDisplayName;
    
    @NotBlank(message = "Ghi chú loại phí vận chuyển là bắt buộc")
    @Size(max = 256, message = "Ghi chú loại phí vận chuyển không được vượt quá 256 ký tự")
    private String shippingFeeTypeRemark;
    
    @NotNull(message = "Cờ trạng thái loại phí vận chuyển là bắt buộc")
    private Boolean shippingFeeTypeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
