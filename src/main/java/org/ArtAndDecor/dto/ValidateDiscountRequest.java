package org.ArtAndDecor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for validating discount code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateDiscountRequest {
    
    @NotBlank(message = "Discount code is required")
    private String code;
    
    @NotNull(message = "Cart amount is required")
    @DecimalMin(value = "0.0", message = "Cart amount must be positive")
    private BigDecimal cartAmount;
    
    @NotEmpty(message = "Product IDs are required")
    private List<Long> productIds;
}