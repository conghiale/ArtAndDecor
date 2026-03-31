package org.artanddecor.dto;

import lombok.*;
import jakarta.validation.constraints.*;

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
     * @return true if valid for add, false otherwise
     */
    public boolean isValidForAdd() {
        return hasCartIdentification() &&
               productId != null && productId > 0 &&
               quantity != null && quantity > 0 && quantity <= 999;
    }

    /**
     * Validate that the request has valid data for update operations
     * @return true if valid for update, false otherwise
     */
    public boolean isValidForUpdate() {
        return quantity != null && quantity >= 0 && quantity <= 999;
    }

    /**
     * Create request for simple add without attributes
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemRequestDto without attributes
     */
    public static CartItemRequestDto createSimpleAdd(Long cartId, Long productId, Integer quantity) {
        return CartItemRequestDto.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    /**
     * Create request for add with attributes
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Quantity
     * @param attributeIds Selected attribute IDs
     * @return CartItemRequestDto with attributes
     */
    public static CartItemRequestDto createWithAttributes(
            Long cartId, Long productId, Integer quantity, List<Long> attributeIds) {
        return CartItemRequestDto.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .selectedAttributeIds(attributeIds)
                .build();
    }

    /**
     * Create request for user-based add
     * @param userId User ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemRequestDto for user
     */
    public static CartItemRequestDto createForUser(Long userId, Long productId, Integer quantity) {
        return CartItemRequestDto.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    /**
     * Create request for guest session add
     * @param sessionId Session ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemRequestDto for guest
     */
    public static CartItemRequestDto createForGuest(String sessionId, Long productId, Integer quantity) {
        return CartItemRequestDto.builder()
                .sessionId(sessionId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}