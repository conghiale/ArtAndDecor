package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for checkout cart to create actual order
 * Contains complete order information for checkout process
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartRequest {
    
    // Cart Items Selection (NEW)
    @NotEmpty(message = "Selected cart item IDs are required")
    private List<Long> selectedCartItemIds;
    
    @NotNull(message = "Cart ID is required")
    private Long cartId;
    
    // Customer Information (người đặt hàng)
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerAddress;
    
    // Receiver Information (người nhận)
    @NotBlank(message = "Receiver name is required")
    private String receiverName;
    
    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;
    
    private String receiverEmail;
    
    // Delivery Address (địa chỉ giao hàng chi tiết)
    @NotBlank(message = "Address line is required")
    private String addressLine;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Ward is required")
    private String ward;
    
    private String country = "Vietnam"; // Default value
    
    // Payment Information
    @NotNull(message = "Payment method ID is required")
    private Long paymentMethodId;
    
    // Optional Information
    private String orderNote;
    private String discountCode;    // Manual discount code (optional) - if provided, use this code; otherwise auto-select best discount
    
    /**
     * Check if manual discount code is provided
     * @return true if manual discount code is specified
     */
    public boolean hasManualDiscountCode() {
        return discountCode != null && !discountCode.trim().isEmpty();
    }
    
    /**
     * Validate required order information
     * @return true if all required fields are present
     */
    public boolean hasCompleteOrderInfo() {
        return customerName != null && !customerName.trim().isEmpty() &&
               receiverName != null && !receiverName.trim().isEmpty() &&
               receiverPhone != null && !receiverPhone.trim().isEmpty() &&
               addressLine != null && !addressLine.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               ward != null && !ward.trim().isEmpty() &&
               paymentMethodId != null;
    }
}