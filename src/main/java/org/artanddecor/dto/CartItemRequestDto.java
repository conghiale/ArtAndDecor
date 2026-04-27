package org.artanddecor.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cart Item Request DTO for both add and update operations
 * Contains essential data needed to create/update cart items with attributes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {

    // Cart identification (for add operations)
    private Long cartId;
    
    // Alternative cart identification (priority: cartId > userId > sessionId)
    private Long userId;
    private String sessionId;

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    private Integer quantity;
    
    /**
     * Unit price calculated on frontend and passed to API
     * Optional - if not provided, will be calculated based on product/attributes
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Cart item unit price must not be negative")
    private BigDecimal cartItemUnitPrice;

    /**
     * List of selected product attribute IDs for this cart item
     * Optional - if null or empty, no attributes will be associated
     */
    private List<Long> selectedAttributeIds;

    /**
     * Optional cart item state ID override (for admin operations)
     * If not provided, will use default ACTIVE state for add operations
     */
    private Long cartItemStateId;

    /**
     * Check if cart identification is provided
     * Note: Cart identification is optional for add operations - new guest cart will be created if not provided
     * @return true if at least one cart identifier is present
     */
    public boolean hasCartIdentification() {
        return cartId != null || userId != null || 
               (sessionId != null && !sessionId.trim().isEmpty());
    }

    /**
     * Check if product attributes are selected
     * @return true if has selected attributes, false otherwise
     */
    public boolean hasSelectedAttributes() {
        return selectedAttributeIds != null && !selectedAttributeIds.isEmpty();
    }

    /**
     * Get count of selected attributes
     * @return Number of selected attributes
     */
    public int getSelectedAttributesCount() {
        return selectedAttributeIds != null ? selectedAttributeIds.size() : 0;
    }

    /**
     * Validate that the request has valid data for add operations
     * Note: Cart identification is optional - if not provided, a new guest cart will be created
     * @return true if valid for add, false otherwise
     */
    public boolean isValidForAdd() {
        return productId != null && productId > 0 &&
               quantity != null && quantity > 0 && quantity <= 999;
    }

    /**
     * Validate that the request has valid data for update operations
     * @return true if valid for update, false otherwise
     */
    public boolean isValidForUpdate() {
        return quantity != null && quantity >= 0 && quantity <= 999;
    }
}