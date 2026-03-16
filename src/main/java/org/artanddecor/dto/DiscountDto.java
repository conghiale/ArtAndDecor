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
 * Discount DTO for API requests and responses
 * Contains comprehensive information from DISCOUNT table with related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    
    private Long discountId;
    
    @NotBlank(message = "Discount code is required")
    @Size(max = 100, message = "Discount code must not exceed 100 characters")
    private String discountCode;
    
    @NotBlank(message = "Discount name is required")
    @Size(max = 64, message = "Discount name must not exceed 64 characters")
    private String discountName;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
    
    @NotNull(message = "Max discount amount is required")
    @DecimalMin(value = "0.0", message = "Max discount amount must not be negative")
    private BigDecimal maxDiscountAmount;
    
    @NotNull(message = "Min order amount is required")
    @DecimalMin(value = "0.0", message = "Min order amount must not be negative")
    private BigDecimal minOrderAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Start date is required")
    private LocalDateTime startAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "End date is required")
    private LocalDateTime endAt;
    
    @NotNull(message = "Usage limit is required")
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer totalUsageLimit;
    
    @Min(value = 0, message = "Used count must not be negative")
    private Integer usedCount;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    @Size(max = 256, message = "Display name must not exceed 256 characters")
    private String discountDisplayName;
    
    @NotBlank(message = "Discount remark is required")
    @Size(max = 256, message = "Discount remark must not exceed 256 characters")
    private String discountRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested DTO for related entity
    private DiscountTypeDto discountType;
    
    // Computed fields
    private Boolean isExpired;
    private Boolean isExhausted;
    private Integer remainingUsage;
    
    /**
     * Check if discount is valid now
     */
    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return isActive != null && isActive &&
               (startAt == null || !now.isBefore(startAt)) &&
               (endAt == null || !now.isAfter(endAt)) &&
               (totalUsageLimit == null || usedCount < totalUsageLimit);
    }
    
    /**
     * Check if discount is expired
     */
    public boolean isExpired() {
        return endAt != null && LocalDateTime.now().isAfter(endAt);
    }
    
    /**
     * Check if discount usage is exhausted
     */
    public boolean isUsageExhausted() {
        return totalUsageLimit != null && usedCount >= totalUsageLimit;
    }
    
    /**
     * Get remaining usage count
     */
    public int getRemainingUsageCount() {
        if (totalUsageLimit == null) return Integer.MAX_VALUE;
        return Math.max(0, totalUsageLimit - (usedCount != null ? usedCount : 0));
    }
    
    /**
     * Calculate discount amount for given order amount
     */
    public BigDecimal calculateDiscountAmount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (minOrderAmount != null && orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountAmount;
        if (discountType != null && discountType.isFixedAmountType()) {
            discountAmount = discountValue;
        } else if (discountType != null && discountType.isPercentageType()) {
            discountAmount = orderAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            return BigDecimal.ZERO;
        }
        
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }
        
        return discountAmount.min(orderAmount);
    }
}