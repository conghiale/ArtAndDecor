package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Cart Item Update Request DTO for API requests
 * Contains only the fields needed for updating a cart item without nested objects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateRequestDto {
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;
    
    // Foreign key IDs only - no nested objects
    private Long cartId;
    private Long productId;
    private Long cartItemStateId;
    
    /**
     * Validate that either unitPrice or totalPrice is provided
     */
    public boolean isValidPriceData() {
        return unitPrice != null || totalPrice != null;
    }
    
    /**
     * Calculate total price from unit price and quantity if not provided
     * @return Calculated or provided total price
     */
    public BigDecimal calculateTotalPrice() {
        if (totalPrice != null) {
            return totalPrice;
        } else if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(new BigDecimal(quantity));
        }
        return null;
    }
}