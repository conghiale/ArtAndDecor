package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DiscountType DTO for API requests and responses
 * Auxiliary class containing only DISCOUNT_TYPE table data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountTypeDto {
    
    private Long discountTypeId;
    
    @NotBlank(message = "Tên loại giảm giá là bắt buộc")
    @Size(max = 64, message = "Tên loại giảm giá không được vượt quá 64 ký tự")
    private String discountTypeName;
    
    @Size(max = 256, message = "Tên hiển thị loại giảm giá không được vượt quá 256 ký tự")
    private String discountTypeDisplayName;
    
    @Size(max = 256, message = "Mô tả loại giảm giá không được vượt quá 256 ký tự")
    private String discountTypeDescription;
    
    @NotBlank(message = "Ghi chú loại giảm giá là bắt buộc")
    @Size(max = 256, message = "Ghi chú loại giảm giá không được vượt quá 256 ký tự")
    private String discountTypeRemark;
    
    @NotNull(message = "Cờ kích hoạt loại giảm giá là bắt buộc")
    private Boolean discountTypeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountTypeCreatedDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountTypeModifiedDate;
    
    // Additional information for reporting
    private Long discountCount;
    
    /**
     * Check if this discount type is for percentage discounts
     */
    public boolean isPercentageType() {
        return "PERCENTAGE".equalsIgnoreCase(discountTypeName);
    }
    
    /**
     * Check if this discount type is for fixed amount discounts
     */
    public boolean isFixedAmountType() {
        return "FIXED_AMOUNT".equalsIgnoreCase(discountTypeName);
    }
    
    /**
     * Check if this discount type is for free shipping
     */
    public boolean isFreeShippingType() {
        return "FREE_SHIPPING".equalsIgnoreCase(discountTypeName);
    }
}