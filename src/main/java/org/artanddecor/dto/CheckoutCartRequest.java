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
    @NotEmpty(message = "Danh sách ID mục giỏ hàng được chọn là bắt buộc")
    private List<Long> selectedCartItemIds;
    
    @NotNull(message = "ID giỏ hàng là bắt buộc")
    private Long cartId;
    
    // Customer Information (người đặt hàng)
    @NotBlank(message = "Tên khách hàng là bắt buộc")
    private String customerName;
    
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerAddress;
    
    // Receiver Information (người nhận)
    @NotBlank(message = "Tên người nhận là bắt buộc")
    private String receiverName;
    
    @NotBlank(message = "Số điện thoại người nhận là bắt buộc")
    private String receiverPhone;
    
    private String receiverEmail;
    
    // Delivery Address (địa chỉ giao hàng chi tiết)
    @NotBlank(message = "Địa chỉ chi tiết là bắt buộc")
    private String addressLine;
    
    @NotBlank(message = "Thành phố là bắt buộc")
    private String city;
    
    @NotBlank(message = "Phường/Xã là bắt buộc")
    private String ward;
    
    private String country = "Vietnam"; // Default value
    
    // Payment Information
    @NotNull(message = "ID phương thức thanh toán là bắt buộc")
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