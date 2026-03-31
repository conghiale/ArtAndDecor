package org.artanddecor.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating order (Admin)
 * Contains fields that can be updated by admin
 * If orderStateId is changed, order state history will be created automatically
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    
    private Long orderStateId; // Optional - if provided, order state will be updated and history created
    
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;
    
    @Size(max = 20, message = "Customer phone number must not exceed 20 characters")
    private String customerPhoneNumber;
    
    @Size(max = 255, message = "Customer email must not exceed 255 characters")
    private String customerEmail;
    
    @Size(max = 500, message = "Customer address must not exceed 500 characters")
    private String customerAddress;
    
    @Size(max = 255, message = "Receiver name must not exceed 255 characters")
    private String receiverName;
    
    @Size(max = 20, message = "Receiver phone must not exceed 20 characters")
    private String receiverPhone;
    
    @Size(max = 255, message = "Receiver email must not exceed 255 characters")
    private String receiverEmail;
    
    @Size(max = 500, message = "Receiver address must not exceed 500 characters")
    private String receiverAddress;
    
    private BigDecimal shippingFeeAmount; // Optional - admin can adjust shipping fee
    
    @Size(max = 1000, message = "Order note must not exceed 1000 characters")
    private String orderNote; // Optional - admin can add/update order notes
}