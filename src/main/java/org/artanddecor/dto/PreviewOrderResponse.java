package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for order preview (checkout calculation)
 * Contains all calculated information without creating actual order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewOrderResponse {
    
    // Selected cart items for the order
    private List<CartItemDto> selectedCartItems;
    
    // Financial breakdown
    private BigDecimal subtotalAmount;          // Total of selected cart items
    private BigDecimal discountAmount;          // Applied discount amount
    private BigDecimal shippingFeeAmount;       // Calculated shipping fee
    private BigDecimal totalAmount;             // Final amount customer pays
    
    // Applied discount information (if any)
    private DiscountDto appliedDiscount;
    private String discountMessage;             // Explanation of applied discount
    
    // Shipping information
    private ShippingFeeDto appliedShippingFee;
    private String shippingMessage;             // Explanation of shipping calculation
    
    // Cart summary
    private Integer totalItems;                 // Total number of items
    private Integer totalQuantity;              // Total quantity of all items
    
    // Validation messages
    private List<String> warnings;              // Any validation warnings (e.g., low stock)
    private List<String> errors;                // Critical errors preventing order creation
    
    /**
     * Check if order preview is valid for checkout
     * @return true if no critical errors exist
     */
    public boolean isValidForCheckout() {
        return errors == null || errors.isEmpty();
    }
    
    /**
     * Check if discount was applied
     * @return true if discount amount > 0
     */
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if shipping fee is applied
     * @return true if shipping fee > 0
     */
    public boolean hasShippingFee() {
        return shippingFeeAmount != null && shippingFeeAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Get savings amount from discount
     * @return discount amount or zero
     */
    public BigDecimal getSavingsAmount() {
        return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }
}