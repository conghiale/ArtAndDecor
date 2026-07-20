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
    
    @Size(max = 255, message = "Tên khách hàng không được vượt quá 255 ký tự")
    private String customerName;
    
    @Size(max = 20, message = "Số điện thoại khách hàng không được vượt quá 20 ký tự")
    private String customerPhoneNumber;
    
    @Size(max = 255, message = "Email khách hàng không được vượt quá 255 ký tự")
    private String customerEmail;
    
    @Size(max = 500, message = "Địa chỉ khách hàng không được vượt quá 500 ký tự")
    private String customerAddress;
    
    @Size(max = 255, message = "Tên người nhận không được vượt quá 255 ký tự")
    private String receiverName;
    
    @Size(max = 20, message = "Số điện thoại người nhận không được vượt quá 20 ký tự")
    private String receiverPhone;
    
    @Size(max = 255, message = "Email người nhận không được vượt quá 255 ký tự")
    private String receiverEmail;
    
    @Size(max = 500, message = "Địa chỉ người nhận không được vượt quá 500 ký tự")
    private String receiverAddress;
    
    private BigDecimal shippingFeeAmount; // Optional - admin can adjust shipping fee
    
    @Size(max = 1000, message = "Ghi chú đơn hàng không được vượt quá 1000 ký tự")
    private String orderNote; // Optional - admin can add/update order notes
}