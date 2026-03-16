package org.artanddecor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating order items in admin create order API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private Long variantId; // Optional for products with variants
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}