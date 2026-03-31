package org.artanddecor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for create order (Admin/Manager)
 * Refactored to remove DISCOUNT functionality as per requirements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;
    
    @NotEmpty(message = "Order items are required")
    @Valid
    private List<CreateOrderItemRequest> orderItems;
    
    private String orderNote; // Optional order note
}