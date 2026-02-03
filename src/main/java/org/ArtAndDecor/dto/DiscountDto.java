package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Discount DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    
    private Long discountId;
    
    @NotBlank(message = "Discount code is required")
    @Size(max = 50, message = "Discount code must not exceed 50 characters")
    private String discountCode;
    
    @NotBlank(message = "Discount name is required")
    @Size(max = 100, message = "Discount name must not exceed 100 characters")
    private String discountName;
    
    private String remark;
    
    @NotNull(message = "Discount type is required")
    @Pattern(regexp = "^(FIXED|PERCENTAGE)$", message = "Discount type must be FIXED or PERCENTAGE")
    private String discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.0", message = "Minimum order amount must not be negative")
    private BigDecimal minimumOrderAmount;
    
    @DecimalMin(value = "0.0", message = "Maximum discount amount must not be negative")
    private BigDecimal maximumDiscountAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;
    
    @Min(value = 0, message = "Usage limit must not be negative")
    private Integer usageLimit;
    
    @Min(value = 0, message = "Used count must not be negative")
    private Integer usedCount;
    
    @NotNull(message = "Discount enabled flag is required")
    private Boolean discountEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    /**
     * Check if discount is valid now
     */
    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return discountEnabled != null && discountEnabled &&
               (validFrom == null || !now.isBefore(validFrom)) &&
               (validTo == null || !now.isAfter(validTo)) &&
               (usageLimit == null || usedCount < usageLimit);
    }
    
    /**
     * Check if discount is expired
     */
    public boolean isExpired() {
        return validTo != null && LocalDateTime.now().isAfter(validTo);
    }
    
    /**
     * Check if discount usage is exhausted
     */
    public boolean isUsageExhausted() {
        return usageLimit != null && usedCount >= usageLimit;
    }
    
    /**
     * Calculate discount amount for given order amount
     */
    public BigDecimal calculateDiscountAmount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (minimumOrderAmount != null && orderAmount.compareTo(minimumOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountAmount;
        if ("FIXED".equals(discountType)) {
            discountAmount = discountValue;
        } else if ("PERCENTAGE".equals(discountType)) {
            discountAmount = orderAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            return BigDecimal.ZERO;
        }
        
        if (maximumDiscountAmount != null && discountAmount.compareTo(maximumDiscountAmount) > 0) {
            discountAmount = maximumDiscountAmount;
        }
        
        return discountAmount.min(orderAmount);
    }
}