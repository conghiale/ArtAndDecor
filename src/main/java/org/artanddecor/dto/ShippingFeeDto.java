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
 * ShippingFee DTO for API requests and responses
 * Contains information from SHIPPING_FEE table with related type data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeDto {
    
    private Long shippingFeeId;
    
    @NotNull(message = "ID loại phí vận chuyển là bắt buộc")
    private Long shippingFeeTypeId;
    
    @NotNull(message = "Giá trị đơn hàng tối thiểu là bắt buộc")
    @DecimalMin(value = "0.0", message = "Giá trị đơn hàng tối thiểu không được là số âm")
    private BigDecimal minOrderPrice;
    
    @NotNull(message = "Giá trị đơn hàng tối đa là bắt buộc")
    @DecimalMin(value = "0.0", message = "Giá trị đơn hàng tối đa không được là số âm")
    private BigDecimal maxOrderPrice;
    
    @NotNull(message = "Giá trị phí vận chuyển là bắt buộc")
    @DecimalMin(value = "0.0", message = "Giá trị phí vận chuyển không được là số âm")
    private BigDecimal shippingFeeValue;
    
    @Size(max = 256, message = "Tên hiển thị phí vận chuyển không được vượt quá 256 ký tự")
    private String shippingFeeDisplayName;
    
    @NotBlank(message = "Ghi chú phí vận chuyển là bắt buộc")
    @Size(max = 256, message = "Ghi chú phí vận chuyển không được vượt quá 256 ký tự")
    private String shippingFeeRemark;
    
    @NotNull(message = "Cờ trạng thái phí vận chuyển là bắt buộc")
    private Boolean shippingFeeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Related data for response
    private String shippingFeeTypeName;
    private String shippingFeeTypeRemark;
}
