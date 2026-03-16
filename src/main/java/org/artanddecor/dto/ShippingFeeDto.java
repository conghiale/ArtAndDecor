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
    
    @NotNull(message = "Shipping fee type ID is required")
    private Long shippingFeeTypeId;
    
    @NotNull(message = "Min order price is required")
    @DecimalMin(value = "0.0", message = "Min order price must not be negative")
    private BigDecimal minOrderPrice;
    
    @NotNull(message = "Max order price is required")
    @DecimalMin(value = "0.0", message = "Max order price must not be negative")
    private BigDecimal maxOrderPrice;
    
    @NotNull(message = "Shipping fee value is required")
    @DecimalMin(value = "0.0", message = "Shipping fee value must not be negative")
    private BigDecimal shippingFeeValue;
    
    @Size(max = 256, message = "Shipping fee display name must not exceed 256 characters")
    private String shippingFeeDisplayName;
    
    @NotBlank(message = "Shipping fee remark is required")
    @Size(max = 256, message = "Shipping fee remark must not exceed 256 characters")
    private String shippingFeeRemark;
    
    @NotNull(message = "Shipping fee enabled flag is required")
    private Boolean shippingFeeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Related data for response
    private String shippingFeeTypeName;
    private String shippingFeeTypeRemark;
}
