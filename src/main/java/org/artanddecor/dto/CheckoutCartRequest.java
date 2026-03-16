package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for checkout cart to create order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartRequest {
    
    @NotNull(message = "Cart ID is required")
    private Long cartId;
    
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String discountCode; // Optional
}