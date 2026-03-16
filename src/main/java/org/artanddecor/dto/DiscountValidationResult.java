package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Result DTO for discount validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountValidationResult {
    
    private boolean valid;
    private String message;
    private String discountCode;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal minOrderAmount;
    private Integer remainingUsage;
}