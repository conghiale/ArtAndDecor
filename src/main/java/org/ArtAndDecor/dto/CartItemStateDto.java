package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * CartItemState DTO for API requests and responses
 * Auxiliary class - contains only CART_ITEM_STATE table data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemStateDto {
    
    private Long cartItemStateId;
    
    @NotBlank(message = "Cart item state name is required")
    @Size(max = 50, message = "Cart item state name must not exceed 50 characters")
    private String cartItemStateName;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String cartItemStateRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String cartItemStateRemark;
    
    @NotNull(message = "Cart item state enabled flag is required")
    private Boolean cartItemStateEnabled;
    
    // Additional information for reporting
    private Long cartItemCount;
    
    /**
     * Check if this is an active cart item state
     */
    public boolean isActiveCartItemState() {
        return "ACTIVE".equalsIgnoreCase(cartItemStateName);
    }
    
    /**
     * Check if this is an ordered cart item state
     */
    public boolean isOrderedCartItemState() {
        return "ORDERED".equalsIgnoreCase(cartItemStateName);
    }
    
    /**
     * Check if this is a removed cart item state
     */
    public boolean isRemovedCartItemState() {
        return "REMOVED".equalsIgnoreCase(cartItemStateName);
    }
}