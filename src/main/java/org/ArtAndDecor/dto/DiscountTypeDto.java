package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Discount type name is required")
    @Size(max = 64, message = "Discount type name must not exceed 64 characters")
    private String discountTypeName;
    
    @Size(max = 256, message = "Discount type display name must not exceed 256 characters")
    private String discountTypeDisplayName;
    
    @Size(max = 256, message = "Discount type description must not exceed 256 characters")
    private String discountTypeDescription;
    
    @NotBlank(message = "Discount type remark is required")
    @Size(max = 256, message = "Discount type remark must not exceed 256 characters")
    private String discountTypeRemark;
    
    @NotNull(message = "Discount type enabled flag is required")
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