package org.artanddecor.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for preview order checkout
 * Used to validate selected cart items and calculate shipping, discount, total amount
 * WITHOUT creating actual order record in database
 * 
 * Requires cartId for security validation - ensures all selected items belong to specified cart
 * Prevents preview of items from different carts or unauthorized access
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewOrderRequest {

    // Cart ID for validation - ensures selected items belong to this cart
    @NotNull(message = "Cart ID is required for preview validation")
    private Long cartId;
    
    // Selected Cart Items for Preview
    @NotNull(message = "Selected cart item IDs are required for preview")
    private List<Long> selectedCartItemIds;
}