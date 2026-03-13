package org.ArtAndDecor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for admin create order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;
    
    private String discountCode; // Optional
    
    @NotEmpty(message = "Order items are required")
    @Valid
    private List<CreateOrderItemRequest> orderItems;
}